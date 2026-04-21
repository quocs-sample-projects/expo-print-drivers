import ExpoModulesCore
import ExternalAccessory

public class PrinterDriversModule: Module {

    // Forwards native Bluetooth events to JS via sendEvent.
    private lazy var eventHandler: BluetoothEventHandler = ModuleEventHandler(module: self)

    // Lazy singleton BluetoothService (first access wires up the event handler).
    private lazy var bluetoothService: BluetoothService = {
        BluetoothService.getInstance(eventHandler: eventHandler)
    }()

    public func definition() -> ModuleDefinition {
        Name("PrinterDrivers")

        OnCreate {
            NSLog("[PrinterDriversModule] --> OnCreate: starting BluetoothService")
            self.bluetoothService.start()
        }

        OnDestroy {
            NSLog("[PrinterDriversModule] --> OnDestroy: stopping BluetoothService")
            self.bluetoothService.stop()
            BluetoothService.clearInstance()
        }

        // === Events sent to the TS layer ===
        Events(
            "onDeviceConnected",
            "onDeviceDisconnected",
            "onConnectionFailed",
            "onConnectionLost",
            "onDataReceived",
            "onBluetoothStateChanged"
        )

        // === Exposed constants ===
        Constant("PrinterType") {
            [
                "WOOSIM_WSP_i350": "WOOSIM_WSP_i350",
                "HONEYWELL_0188": "HONEYWELL_0188",
                "HONEYWELL_PR3": "HONEYWELL_PR3"
            ]
        }

        Constant("BluetoothConnectionState") {
            [
                "NONE": BluetoothConnectionState.NONE,
                "LISTEN": BluetoothConnectionState.LISTEN,
                "CONNECTING": BluetoothConnectionState.CONNECTING,
                "CONNECTED": BluetoothConnectionState.CONNECTED
            ]
        }

        // === Functions ===
        Function("getBluetoothState") { () -> Int in
            return self.bluetoothService.currentState
        }

        Function("isBluetoothAvailable") { () -> Bool in
            return self.bluetoothService.isBluetoothAvailable()
        }

        Function("isBluetoothEnabled") { () -> Bool in
            return self.bluetoothService.isBluetoothEnabled()
        }

        AsyncFunction("getPairedDevices") { () -> [[String: String]] in
            return self.bluetoothService.getPairedDevices().map { device in
                ["name": device.name, "address": device.address]
            }
        }

        AsyncFunction("connect") { (address: String, secure: Bool) in
            NSLog("[PrinterDriversModule] --> connect() called for address: \(address)")
            self.bluetoothService.connect(address: address, secure: secure)
        }

        AsyncFunction("disconnect") {
            NSLog("[PrinterDriversModule] --> disconnect() called")
            self.bluetoothService.stop()
        }

        // Driver-level printing is not yet implemented on iOS.
        // Requires the Woosim (woosim302) SDK and per-vendor iOS drivers to be added.
        Function("giayBaoTienNuocBenThanh") { (_ printerType: String, _ jsonData: [String: Any]) in
            NSLog("[PrinterDriversModule] --> giayBaoTienNuocBenThanh not implemented on iOS yet")
        }
    }
}

/// Bridges BluetoothEventHandler callbacks to the Expo Module's sendEvent.
private final class ModuleEventHandler: BluetoothEventHandler {
    private weak var module: PrinterDriversModule?

    init(module: PrinterDriversModule) {
        self.module = module
    }

    func onDeviceConnected(deviceName: String, deviceAddress: String) {
        NSLog("[PrinterDriversModule] --> onDeviceConnected: \(deviceName) (\(deviceAddress))")
        module?.sendEvent("onDeviceConnected", [
            "deviceName": deviceName,
            "deviceAddress": deviceAddress
        ])
    }

    func onDeviceDisconnected() {
        NSLog("[PrinterDriversModule] --> onDeviceDisconnected")
        module?.sendEvent("onDeviceDisconnected", [:])
    }

    func onConnectionFailed(error: String) {
        NSLog("[PrinterDriversModule] --> onConnectionFailed: \(error)")
        module?.sendEvent("onConnectionFailed", ["error": error])
    }

    func onConnectionLost() {
        NSLog("[PrinterDriversModule] --> onConnectionLost")
        module?.sendEvent("onConnectionLost", [:])
    }

    func onDataReceived(data: Data) {
        NSLog("[PrinterDriversModule] --> onDataReceived: \(data.count) bytes")
        module?.sendEvent("onDataReceived", [
            "data": Array(data).map { Int($0) }
        ])
    }

    func onBluetoothStateChanged(isAvailable: Bool, isEnabled: Bool) {
        NSLog("[PrinterDriversModule] --> onBluetoothStateChanged: available=\(isAvailable) enabled=\(isEnabled)")
        module?.sendEvent("onBluetoothStateChanged", [
            "isAvailable": isAvailable,
            "isEnabled": isEnabled
        ])
    }
}
