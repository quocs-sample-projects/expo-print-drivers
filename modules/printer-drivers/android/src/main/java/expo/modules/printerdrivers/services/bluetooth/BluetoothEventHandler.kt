package expo.modules.printerdrivers.services.bluetooth

interface BluetoothEventHandler {
    fun onDeviceConnected(deviceName: String, deviceAddress: String)
    fun onDeviceDisconnected()
    fun onConnectionFailed(error: String)
    fun onConnectionLost()
    fun onDataReceived(data: ByteArray)
}