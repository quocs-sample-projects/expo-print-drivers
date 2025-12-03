import { useEffect, useState } from "react";
import PrinterDriversModule from "../PrinterDriversModule";
import { BluetoothDevice } from "../PrinterDrivers.types";

export interface BluetoothConnectionStatus {
  isConnected: boolean;
  isConnecting: boolean;
  isDisconnecting: boolean;
  connectingDevice: BluetoothDevice | null;
  connectedDevice: BluetoothDevice | null;
  error: string | null;
}

export function useBluetoothConnection() {
  const [status, setStatus] = useState<BluetoothConnectionStatus>({
    isConnected: false,
    isConnecting: false,
    isDisconnecting: false,
    connectingDevice: null,
    connectedDevice: null,
    error: null,
  });

  useEffect(() => {
    const deviceConnectedSubscription = PrinterDriversModule.addListener(
      "onDeviceConnected",
      (event) => {
        setStatus({
          isConnected: true,
          isConnecting: false,
          isDisconnecting: false,
          connectingDevice: null,
          connectedDevice: {
            name: event.deviceName,
            address: event.deviceAddress,
          },
          error: null,
        });
      }
    );

    const deviceDisconnectedSubscription = PrinterDriversModule.addListener(
      "onDeviceDisconnected",
      () => {
        setStatus({
          isConnected: false,
          isConnecting: false,
          isDisconnecting: false,
          connectingDevice: null,
          connectedDevice: null,
          error: null,
        });
      }
    );

    const connectionFailedSubscription = PrinterDriversModule.addListener(
      "onConnectionFailed",
      (event) => {
        setStatus({
          isConnected: false,
          isConnecting: false,
          isDisconnecting: false,
          connectingDevice: null,
          connectedDevice: null,
          error: event.error,
        });
      }
    );

    const connectionLostSubscription = PrinterDriversModule.addListener(
      "onConnectionLost",
      () => {
        setStatus({
          isConnected: false,
          isConnecting: false,
          isDisconnecting: false,
          connectingDevice: null,
          connectedDevice: null,
          error: "Connection lost",
        });
      }
    );

    return () => {
      deviceConnectedSubscription.remove();
      deviceDisconnectedSubscription.remove();
      connectionFailedSubscription.remove();
      connectionLostSubscription.remove();
    };
  }, []);

  const connect = async (device: BluetoothDevice, secure: boolean = true) => {
    if (status.isConnected) {
      await disconnect();
    }
    setStatus((prev) => ({
      ...prev,
      isConnecting: true,
      isDisconnecting: false,
      connectingDevice: device,
      error: null,
      state: PrinterDriversModule.BluetoothConnectionState.CONNECTING,
    }));

    try {
      await PrinterDriversModule.connect(device.address, secure);
    } catch (error) {
      setStatus((prev) => ({
        ...prev,
        isConnecting: false,
        error: error instanceof Error ? error.message : "Connection failed",
        state: PrinterDriversModule.BluetoothConnectionState.NONE,
      }));
    }
  };

  const disconnect = async () => {
    setStatus((prev) => ({
      ...prev,
      isDisconnecting: true,
      error: null,
    }));

    try {
      await PrinterDriversModule.disconnect();
    } catch (error) {
      setStatus((prev) => ({
        ...prev,
        isDisconnecting: false,
        error: error instanceof Error ? error.message : "Disconnection failed",
      }));
    }
  };

  const clearError = () => {
    setStatus((prev) => ({
      ...prev,
      error: null,
    }));
  };

  return {
    status,
    connect,
    disconnect,
    clearError,
  };
}
