export interface BluetoothDevice {
  name: string;
  address: string;
}

export interface BluetoothDevice {
  name: string;
  address: string;
}

// Event payloads
export interface DeviceConnectedEvent {
  deviceName: string;
}

export interface DeviceDisconnectedEvent {}

export interface ConnectionFailedEvent {
  error: string;
}

export interface ConnectionLostEvent {}

export interface DataReceivedEvent {
  bytes: number[];
}

export type PrinterDriversModuleEvents = {
  onDeviceConnected(event: DeviceConnectedEvent): void;
  onDeviceDisconnected(event: DeviceDisconnectedEvent): void;
  onConnectionFailed(event: ConnectionFailedEvent): void;
  onConnectionLost(event: ConnectionLostEvent): void;
  onDataReceived(event: DataReceivedEvent): void;
};

export type ChangeEventPayload = {
  value: string;
};
