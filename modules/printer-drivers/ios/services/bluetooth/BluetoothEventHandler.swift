import Foundation

protocol BluetoothEventHandler: AnyObject {
    func onDeviceConnected(deviceName: String, deviceAddress: String)
    func onDeviceDisconnected()
    func onConnectionFailed(error: String)
    func onConnectionLost()
    func onDataReceived(data: Data)
    func onBluetoothStateChanged(isAvailable: Bool, isEnabled: Bool)
}
