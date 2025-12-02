package expo.modules.printerdrivers.bluetoothService

/**
 * Interface for handling Bluetooth events.
 */

interface BluetoothEventHandler {
    fun onDeviceConnected(deviceName: String)
    fun onDeviceDisconnected()
    fun onConnectionFailed(error: String)
    fun onConnectionLost()
    fun onDataReceived(data: ByteArray)
}