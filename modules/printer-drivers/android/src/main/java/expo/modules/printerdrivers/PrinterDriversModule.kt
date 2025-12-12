package expo.modules.printerdrivers

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.ReadableMap
import expo.modules.kotlin.exception.Exceptions
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.printerdrivers.drivers.BaseDriver
import expo.modules.printerdrivers.drivers.Honeywell0188Driver
import expo.modules.printerdrivers.drivers.HoneywellPR3Driver
import expo.modules.printerdrivers.drivers.WoosimWSPi350Driver
import expo.modules.printerdrivers.services.bluetooth.BluetoothConnectionState
import expo.modules.printerdrivers.services.bluetooth.BluetoothEventHandler
import expo.modules.printerdrivers.services.bluetooth.BluetoothService
import expo.modules.printerdrivers.utils.constants.PrinterType

class PrinterDriversModule : Module() {
    companion object {
        private const val TAG = "PrinterDriversModule"
    }

    // Event handler forwards native events to JS via sendEvent
    private val eventHandler = object : BluetoothEventHandler {
        override fun onDeviceConnected(deviceName: String, deviceAddress: String) {
            Log.d(TAG, "--> onDeviceConnected: $deviceName ($deviceAddress)")
            sendEvent(
                "onDeviceConnected", mapOf(
                    "deviceName" to deviceName,
                    "deviceAddress" to deviceAddress
                )
            )
        }

        override fun onDeviceDisconnected() {
            Log.d(TAG, "--> onDeviceDisconnected")
            sendEvent("onDeviceDisconnected", emptyMap<String, Any>())
        }

        override fun onConnectionFailed(error: String) {
            Log.d(TAG, "--> onConnectionFailed: $error")
            sendEvent("onConnectionFailed", mapOf("error" to error))
        }

        override fun onConnectionLost() {
            Log.d(TAG, "--> onConnectionLost")
            sendEvent("onConnectionLost", emptyMap<String, Any>())
        }

        override fun onDataReceived(data: ByteArray) {
            Log.d(TAG, "--> onDataReceived: ${data.size} bytes")
            sendEvent("onDataReceived", mapOf("data" to data.map { it.toInt() }))
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
        val context = appContext.reactContext ?: throw Exceptions.ReactContextLost()
        WoosimWSPi350Driver(bluetoothService, context)
    }
    private val honeywell0188Driver: Honeywell0188Driver by lazy {
        val context = appContext.reactContext ?: throw Exceptions.ReactContextLost()
        Honeywell0188Driver(bluetoothService, context)
    }
    private val honeywellPR3Driver: HoneywellPR3Driver by lazy {
        val context = appContext.reactContext ?: throw Exceptions.ReactContextLost()
        HoneywellPR3Driver(bluetoothService, context)
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
            Log.d(TAG, "--> OnCreate: Starting BluetoothService")
            bluetoothService.start()
        }

        OnDestroy {
            Log.d(TAG, "--> OnDestroy: Stopping BluetoothService")
            bluetoothService.stop()
            BluetoothService.clearInstance()
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
        Constants(
            "PrinterType" to mapOf(
                "WOOSIM_WSP_i350" to PrinterType.WOOSIM_WSP_i350,
                "HONEYWELL_0188" to PrinterType.HONEYWELL_0188,
                "HONEYWELL_PR3" to PrinterType.HONEYWELL_PR3
            ),
            "BluetoothConnectionState" to mapOf(
                "NONE" to BluetoothConnectionState.NONE,
                "LISTEN" to BluetoothConnectionState.LISTEN,
                "CONNECTING" to BluetoothConnectionState.CONNECTING,
                "CONNECTED" to BluetoothConnectionState.CONNECTED
            )
        )

        Function("getBluetoothState") {
            bluetoothService.getState()
        }

        Function("isBluetoothAvailable") {
            bluetoothAdapter != null
        }

        Function("isBluetoothEnabled") {
            bluetoothAdapter?.isEnabled == true
        }

        AsyncFunction("getPairedDevices") {
            checkBluetoothPermissions()
            val pairedDevices = bluetoothAdapter?.bondedDevices ?: emptySet()
            pairedDevices.map { device ->
                mapOf(
                    "name" to (device.name ?: "Unknown"),
                    "address" to device.address
                )
            }
        }

        AsyncFunction("connect") { address: String, secure: Boolean ->
            Log.d(TAG, "--> connect() called for address: $address")
            checkBluetoothPermissions()

            val device = bluetoothAdapter?.getRemoteDevice(address)
                ?: throw IllegalArgumentException("Device not found: $address")

            bluetoothService.connect(device, secure)
        }

        AsyncFunction("disconnect") {
            Log.d(TAG, "--> disconnect() called")
            bluetoothService.stop()
        }

        fun getDriver(printerType: String): BaseDriver {
            return when (printerType) {
                PrinterType.WOOSIM_WSP_i350 -> woosimWSPi350Driver
                PrinterType.HONEYWELL_PR3 -> honeywellPR3Driver
                PrinterType.HONEYWELL_0188 -> honeywell0188Driver
                else -> throw IllegalArgumentException("Unknown printer type: $printerType")
            }
        }

        fun printHandler(driver: BaseDriver, printTicketFunction: () -> Unit) {
            try {
                driver.clearBuffer()
                driver.initPrinter()
                printTicketFunction()
                driver.sendPrintData()
            } catch (err: Error) {
                Log.e(TAG, "--> Error in printHandler: $err")
            }
        }

        Function("testGiayBaoTienNuoc") { printerType: String, jsonData: ReadableMap ->
            val usingDriver = getDriver(printerType)
            printHandler(
                driver = usingDriver,
                printTicketFunction = { usingDriver.testGiayBaoTienNuoc(jsonData) }
            )
        }
    }
}
