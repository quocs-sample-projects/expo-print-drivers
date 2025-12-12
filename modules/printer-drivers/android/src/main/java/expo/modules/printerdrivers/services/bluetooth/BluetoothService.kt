package expo.modules.printerdrivers.services.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.util.Log
import expo.modules.printerdrivers.BuildConfig
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

/**
 * This class does all the work for setting up and managing Bluetooth connections with printers.
 * It has a thread for connecting with a printer, and a thread for performing data transmissions when connected.
 */
class BluetoothService private constructor(private val eventHandler: BluetoothEventHandler) {
    companion object {
        private const val TAG = "BluetoothPrintService"
        private val DEBUG = BuildConfig.DEBUG

        // Unique UUID for SPP (Serial Port Profile)
        @OptIn(ExperimentalUuidApi::class)
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        @Volatile
        private var INSTANCE: BluetoothService? = null

        /**
         * Thread-safe accessor for the singleton instance.
         * The first caller must provide a non-null eventHandler; subsequent calls return the same instance.
         */
        fun getInstance(eventHandler: BluetoothEventHandler): BluetoothService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: BluetoothService(eventHandler).also {
                    INSTANCE = it
                    Log.d(TAG, "--> BluetoothService created")
                }
            }

        /**
         * Clear the singleton instance (useful for tests or cleanup).
         */
        fun clearInstance() {
            synchronized(this) {
                INSTANCE?.stop()
                INSTANCE = null
                Log.d(TAG, "--> BluetoothService instance cleared")
            }
        }
    }

    // Member fields
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val handler = Handler(Looper.getMainLooper())

    @Volatile
    private var currentState: Int = BluetoothConnectionState.NONE
    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null

    /**
     * Set the current state of the connection
     */
    @Synchronized
    private fun setState(newState: Int) {
        if (DEBUG) Log.d(TAG, "--> setState() $currentState -> $newState")
        currentState = newState
    }

    /**
     * Return the current connection state.
     */
    @Synchronized
    fun getState(): Int = currentState

    /**
     * Start the print service.
     */
    @Synchronized
    fun start() {
        if (DEBUG) Log.d(TAG, "--> start")

        // Cancel any thread attempting to make a connection
        connectThread?.cancel()
        connectThread = null

        // Cancel any thread currently running a connection
        connectedThread?.cancel()
        connectedThread = null

        setState(BluetoothConnectionState.LISTEN)
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true), Insecure (false)
     */
    @SuppressLint("MissingPermission")
    @Synchronized
    fun connect(device: BluetoothDevice, secure: Boolean = true) {
        if (DEBUG) Log.d(TAG, "--> connect to: ${device.name} (${device.address})")

        // Cancel any thread attempting to make a connection
        if (currentState == BluetoothConnectionState.CONNECTING) {
            connectThread?.cancel()
            connectThread = null
        }

        // Cancel any thread currently running a connection
        connectedThread?.cancel()
        connectedThread = null

        // Start the thread to connect with the given device
        connectThread = ConnectThread(device, secure)
        connectThread?.start()
        setState(BluetoothConnectionState.CONNECTING)
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     */
    @SuppressLint("MissingPermission")
    @Synchronized
    private fun connected(socket: BluetoothSocket, device: BluetoothDevice, socketType: String) {
        if (DEBUG) Log.d(TAG, "--> connected, Socket Type: $socketType")

        // Cancel the thread that completed the connection
        connectThread?.cancel()
        connectThread = null

        // Cancel any thread currently running a connection
        connectedThread?.cancel()
        connectedThread = null

        // Start the thread to manage the connection and perform transmissions
        connectedThread = ConnectedThread(socket, socketType)
        connectedThread?.start()

        setState(BluetoothConnectionState.CONNECTED)

        // Notify connection success
        handler.post {
            eventHandler.onDeviceConnected(device.name ?: "Unknown", device.address)
        }
    }

    /**
     * Stop all threads
     */
    @Synchronized
    fun stop() {
        if (DEBUG) Log.d(TAG, "--> stop")

        val wasConnected = currentState == BluetoothConnectionState.CONNECTED

        connectThread?.cancel()
        connectThread = null

        connectedThread?.cancel()
        connectedThread = null

        setState(BluetoothConnectionState.NONE)

        // Notify if we were connected
        if (wasConnected) {
            handler.post {
                eventHandler.onDeviceDisconnected()
            }
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param data The bytes to write
     */
    fun write(data: ByteArray) {
        // Create temporary object
        val r: ConnectedThread?

        // Synchronize a copy of the ConnectedThread
        synchronized(this) {
            if (currentState != BluetoothConnectionState.CONNECTED) {
                Log.w(TAG, "--> write() called but not connected")
                return
            }
            r = connectedThread
        }

        // Perform the write unsynchronized
        r?.write(data)
    }

    /**
     * Indicate that the connection attempt failed and notify the UI.
     */
    private fun connectionFailed() {
        // When the application is destroyed, just return
        if (currentState == BluetoothConnectionState.NONE) return

        setState(BluetoothConnectionState.NONE)

        handler.post {
            eventHandler.onConnectionFailed("Unable to connect to device")
        }

        // Start the service over to restart listening mode
        start()
    }

    /**
     * Indicate that the connection was lost and notify the UI.
     */
    private fun connectionLost() {
        // When the application is destroyed, just return
        if (currentState == BluetoothConnectionState.NONE) return

        setState(BluetoothConnectionState.NONE)

        handler.post {
            eventHandler.onConnectionLost()
        }

        // Start the service over to restart listening mode
        start()
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private inner class ConnectThread(
        private val device: BluetoothDevice,
        secure: Boolean
    ) : Thread() {

        private val socket: BluetoothSocket?
        private val socketType: String = if (secure) "Secure" else "Insecure"

        init {
            var tmp: BluetoothSocket? = null

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = if (secure) {
                    device.createRfcommSocketToServiceRecord(SPP_UUID)
                } else {
                    device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                }
            } catch (e: IOException) {
                Log.e(TAG, "--> Socket Type: $socketType create() failed", e)
            } catch (e: SecurityException) {
                Log.e(TAG, "--> Missing Bluetooth permissions", e)
            }

            socket = tmp
        }

        @SuppressLint("MissingPermission")
        override fun run() {
            Log.i(TAG, "--> BEGIN mConnectThread SocketType: $socketType")
            name = "ConnectThread$socketType"

            // Always cancel discovery because it will slow down a connection
            try {
                adapter?.cancelDiscovery()
            } catch (e: SecurityException) {
                Log.e(TAG, "--> Missing permission to cancel discovery", e)
            }

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                socket?.connect()
            } catch (e: IOException) {
                Log.e(TAG, "--> Connection Failed", e)
                // Close the socket
                try {
                    socket?.close()
                } catch (e2: IOException) {
                    Log.e(
                        TAG,
                        "--> unable to close() $socketType socket during connection failure",
                        e2
                    )
                }
                connectionFailed()
                return
            }

            // Reset the ConnectThread because we're done
            synchronized(this@BluetoothService) {
                connectThread = null
            }

            // Start the connected thread
            socket?.let { connected(it, device, socketType) }
        }

        fun cancel() {
            try {
                socket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "--> close() of connect $socketType socket failed", e)
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private inner class ConnectedThread(
        private val socket: BluetoothSocket,
        socketType: String
    ) : Thread() {

        private val inStream: InputStream?
        private val outStream: OutputStream?

        init {
            Log.d(TAG, "--> create ConnectedThread: $socketType")

            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                Log.e(TAG, "--> temp sockets not created", e)
            }

            inStream = tmpIn
            outStream = tmpOut
        }

        override fun run() {
            Log.i(TAG, "--> BEGIN mConnectedThread")
            val buffer = ByteArray(1024)

            // Keep listening to the InputStream while connected
            while (currentState == BluetoothConnectionState.CONNECTED) {
                try {
                    // Read from the InputStream
                    val bytes = inStream?.read(buffer) ?: -1

                    if (bytes > 0) {
                        // Create a copy of the received data
                        val rcvData = buffer.copyOf(bytes)

                        // Send the obtained bytes to the event handler
                        handler.post {
                            eventHandler.onDataReceived(rcvData)
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "--> Connection Lost", e)
                    connectionLost()
                    break
                }
            }

            Log.i(TAG, "--> END mConnectedThread")
        }

        /**
         * Write to the connected OutStream.
         * @param buffer The bytes to write
         */
        fun write(buffer: ByteArray) {
            try {
                outStream?.write(buffer)
                outStream?.flush()
                if (DEBUG) Log.d(TAG, "--> Wrote ${buffer.size} bytes")
            } catch (e: IOException) {
                Log.e(TAG, "--> Exception during write", e)
                handler.post {
                    eventHandler.onConnectionLost()
                }
            }
        }

        fun cancel() {
            try {
                inStream?.close()
                outStream?.close()
                socket.close()
                Log.d(TAG, "--> ConnectedThread cancelled")
            } catch (e: IOException) {
                Log.e(TAG, "--> close() of connect socket failed", e)
            }
        }
    }
}