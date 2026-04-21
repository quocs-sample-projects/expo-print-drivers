export interface BluetoothDevice {
  name: string;
  address: string;
}

export interface DeviceConnectedEvent {
  deviceName: string;
  deviceAddress: string;
}

export interface DeviceDisconnectedEvent {}

export interface ConnectionFailedEvent {
  error: string;
}

export interface ConnectionLostEvent {}

export interface DataReceivedEvent {
  data: number[];
}

export interface BluetoothStateChangedEvent {
  isAvailable: boolean;
  isEnabled: boolean;
}

export type PrinterDriversModuleEvents = {
  onDeviceConnected(event: DeviceConnectedEvent): void;
  onDeviceDisconnected(event: DeviceDisconnectedEvent): void;
  onConnectionFailed(event: ConnectionFailedEvent): void;
  onConnectionLost(event: ConnectionLostEvent): void;
  onDataReceived(event: DataReceivedEvent): void;
  onBluetoothStateChanged(event: BluetoothStateChangedEvent): void;
};

export type ChangeEventPayload = {
  value: string;
};
