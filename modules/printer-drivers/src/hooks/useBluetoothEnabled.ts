import { useState, useEffect } from "react";
import { NativeEventEmitter, NativeModules } from "react-native";
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
    // Initial check
    checkBluetoothState();

    // Set up listener for Bluetooth state changes (if available)
    const eventEmitter = new NativeEventEmitter(NativeModules.ClassicBluetooth);
    const subscription = eventEmitter.addListener(
      "onBluetoothStateChanged",
      () => {
        checkBluetoothState();
      }
    );

    // Poll for state changes as fallback (since Android doesn't always emit events)
    const interval = setInterval(() => {
      checkBluetoothState();
    }, 2000); // Check every 2 seconds

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
    } catch (error) {
      console.error("Error checking Bluetooth state:", error);
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
