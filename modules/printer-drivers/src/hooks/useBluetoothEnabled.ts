import { useState, useEffect } from "react";
import { Alert, NativeEventEmitter, NativeModules } from "react-native";
import PrinterDriversModule from "../PrinterDriversModule";

export interface BluetoothEnabledStatus {
  isEnabled: boolean;
  isAvailable: boolean;
  loading: boolean;
}

/**
 * Custom hook to check if Bluetooth is currently enabled on the device
 *
 * Monitors Bluetooth state and updates when it changes.
 * Returns the current enabled state, availability, and loading status.
 */
export function useBluetoothEnabled() {
  const [status, setStatus] = useState<BluetoothEnabledStatus>({
    isEnabled: false,
    isAvailable: false,
    loading: true,
  });

  useEffect(() => {
    checkBluetoothState();

    const eventEmitter = new NativeEventEmitter(NativeModules.ClassicBluetooth);
    const subscription = eventEmitter.addListener(
      "onBluetoothStateChanged",
      () => {
        checkBluetoothState();
      }
    );

    const interval = setInterval(() => {
      checkBluetoothState();
    }, 2000);

    return () => {
      subscription?.remove();
      clearInterval(interval);
    };
  }, []);

  const checkBluetoothState = () => {
    try {
      const available = PrinterDriversModule.isBluetoothAvailable();
      const enabled = available
        ? PrinterDriversModule.isBluetoothEnabled()
        : false;

      setStatus({
        isEnabled: enabled,
        isAvailable: available,
        loading: false,
      });
    } catch (error: unknown) {
      if (error instanceof Error) {
        Alert.alert("Error checking Bluetooth state:", error.message);
      } else {
        Alert.alert(
          "Error checking Bluetooth state:",
          "An unknown error occurred"
        );
      }
      setStatus({
        isEnabled: false,
        isAvailable: false,
        loading: false,
      });
    }
  };

  const refresh = () => {
    checkBluetoothState();
  };

  return {
    ...status,
    refresh,
  };
}
