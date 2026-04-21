import ExpoModulesCore

public class PrinterDriversModule: Module {
    public func definition() -> ModuleDefinition {
        Name("PrinterDrivers")
        
        // === Data sending to TS layer ===
        Events(
            "onDeviceConnected",
            "onDeviceDisconnected",
            "onConnectionFailed",
            "onConnectionLost",
            "onDataReceived",
            "onBluetoothStateChanged"
        )
        
        // Expose constants
        Constant("PrinterType"){
             [
                "WOOSIM_WSP_i350": 1,
                "HONEYWELL_0188": 2,
                "HONEYWELL_PR3": 3
            ]
        }
        
        Constant("BluetoothConnectionState"){
            [
                "NONE" : 0,
                "LISTEN": 1,
                "CONNECTING": 2,
                "CONNECTED": 3,
            ]
        }
        
        Function("getBluetoothState"){
             "Not supported yet"
        }
        
        Function("isBluetoothAvailable") {
            false
        }
        
        AsyncFunction("getPairedDevices"){
            
        }
        
        AsyncFunction("connect"){
            
        }
        
        AsyncFunction("disconnect"){
            
        }
    }
}
