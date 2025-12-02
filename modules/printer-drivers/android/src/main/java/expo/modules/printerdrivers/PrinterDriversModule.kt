package expo.modules.printerdrivers

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.printerdrivers.bluetoothService.BluetoothEventHandler
import expo.modules.printerdrivers.bluetoothService.BluetoothService
import expo.modules.printerdrivers.drivers.WoosimWSPi350Driver
import com.facebook.react.bridge.ReadableMap
import expo.modules.kotlin.exception.Exceptions
import expo.modules.printerdrivers.bluetoothService.BluetoothConnectionState
import expo.modules.printerdrivers.utils.constants.PrinterType

class PrinterDriversModule : Module() {
    companion object {
        var TAG = "PrinterDriversModule"
    }

    // Event handler forwards native events to JS via sendEvent
    private val eventHandler = object : BluetoothEventHandler {
        override fun onDeviceConnected(deviceName: String) {
            sendEvent("onChange", mapOf("type" to "connected", "name" to deviceName))
        }

        override fun onDeviceDisconnected() {
            sendEvent("onChange", mapOf("type" to "disconnected"))
        }

        override fun onConnectionFailed(error: String) {
            sendEvent("onChange", mapOf("type" to "connectionFailed", "message" to error))
        }

        override fun onConnectionLost() {
            sendEvent("onChange", mapOf("type" to "connectionLost"))
        }

        override fun onDataReceived(data: ByteArray) {
            val b64 = Base64.encodeToString(data, Base64.NO_WRAP)
            sendEvent("onChange", mapOf("type" to "data", "dataBase64" to b64))
        }
    }

    // Bluetooth adapter to check if bluetooth is available and enabled
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    // Singleton BluetoothService (first access provides the event handler)
    private val bluetoothService: BluetoothService by lazy {
        BluetoothService.getInstance(eventHandler)
    }

    // Drivers initialized with the singleton BluetoothService
    private val woosimWSPi350Driver: WoosimWSPi350Driver by lazy {
        WoosimWSPi350Driver(bluetoothService)
    }
    private val honeywell0188Driver: WoosimWSPi350Driver by lazy {
        WoosimWSPi350Driver(bluetoothService)
    }

    private fun checkBluetoothPermissions() {
        val context = appContext.reactContext ?: throw Exceptions.ReactContextLost()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hasConnect = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            val hasScan = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasConnect || !hasScan) {
                throw Exceptions.MissingPermissions(
                    "BLUETOOTH_CONNECT and BLUETOOTH_SCAN permissions are required"
                )
            }
        } else {
            val hasBluetooth = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED

            val hasBluetoothAdmin = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasBluetooth || !hasBluetoothAdmin) {
                throw Exceptions.MissingPermissions(
                    "BLUETOOTH and BLUETOOTH_ADMIN permissions are required"
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun definition() = ModuleDefinition {
        Name("PrinterDrivers")

        OnCreate {
            bluetoothService.start()
        }

        OnDestroy {
            bluetoothService.stop()
        }

        // === Data sending to TS layer ===
        Events(
            "onDeviceConnected",
            "onDeviceDisconnected",
            "onConnectionFailed",
            "onConnectionLost",
            "onDataReceived",
        )

        // Expose constants
        Constant("PrinterType") {
            mapOf(
                "WOOSIM_WSP_i350" to PrinterType.WOOSIM_WSP_i350,
                "HONEYWELL_0188" to PrinterType.HONEYWELL_0188
            )
        }

        Constant("BluetoothConnectionState") {
            mapOf(
                "NONE" to BluetoothConnectionState.NONE,
                "LISTEN" to BluetoothConnectionState.LISTEN,
                "CONNECTING" to BluetoothConnectionState.CONNECTING,
                "CONNECTED" to BluetoothConnectionState.CONNECTED
            )
        }

        Function("isBluetoothAvailable") {
            return@Function bluetoothAdapter != null
        }

        Function("isBluetoothEnabled") {
            return@Function bluetoothAdapter?.isEnabled == true
        }

        AsyncFunction("getPairedDevices") {
            checkBluetoothPermissions()
            val pairedDevices = bluetoothAdapter?.bondedDevices ?: emptySet()
            return@AsyncFunction pairedDevices.map { device ->
                mapOf(
                    "name" to (device.name ?: "Unknown"),
                    "address" to device.address
                )
            }
        }

        AsyncFunction("connect") { address: String, secure: Boolean ->
            checkBluetoothPermissions()

            val device = bluetoothAdapter?.getRemoteDevice(address)
                ?: throw Throwable("Device not found")

            bluetoothService.connect(device, secure)
        }

        AsyncFunction("disconnect") {
            bluetoothService.stop()
        }

        Function("giayBaoTienNuocNongThon") { printerType: String, jsonData: ReadableMap ->
            when (printerType) {
                PrinterType.WOOSIM_WSP_i350 -> woosimWSPi350Driver.giayBaoTienNuocNongThon(jsonData)
                PrinterType.HONEYWELL_0188 -> honeywell0188Driver.giayBaoTienNuocNongThon(jsonData)
                else -> Log.e(TAG, "--> Printer type not supported")
            }
        }
    }
}
