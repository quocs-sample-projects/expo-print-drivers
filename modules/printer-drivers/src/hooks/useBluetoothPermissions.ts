import { useState, useEffect } from "react";
import { Platform, PermissionsAndroid, Alert } from "react-native";

export interface BluetoothPermissionStatus {
  granted: boolean;
  canAskAgain: boolean;
  loading: boolean;
}

/**
 * Custom hook to check and request Bluetooth permissions on Android
 *
 * For Android 12+ (API 31+): Requests runtime permissions for BLUETOOTH_CONNECT and BLUETOOTH_SCAN
 * For Android 11 and below: Permissions are automatically granted via AndroidManifest.xml
 *
 * Note: BLUETOOTH and BLUETOOTH_ADMIN are legacy permissions that don't require runtime checks
 */
export function useBluetoothPermissions() {
  const [permissionStatus, setPermissionStatus] =
    useState<BluetoothPermissionStatus>({
      granted: false,
      canAskAgain: true,
      loading: true,
    });

  useEffect(() => {
    if (Platform.OS === "android") {
      checkPermissions();
    } else {
      // iOS doesn't support Classic Bluetooth, so permissions are not applicable
      setPermissionStatus({
        granted: false,
        canAskAgain: false,
        loading: false,
      });
    }
  }, []);

  const checkPermissions = async () => {
    setPermissionStatus((prev) => ({ ...prev, loading: true }));

    try {
      const apiLevel = Platform.Version as number;

      if (apiLevel >= 31) {
        // Android 12 and above - need runtime permissions
        const connectGranted = await PermissionsAndroid.check(
          PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT
        );
        const scanGranted = await PermissionsAndroid.check(
          PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN
        );

        setPermissionStatus({
          granted: connectGranted && scanGranted,
          canAskAgain: true,
          loading: false,
        });
      } else {
        // Android 11 and below - permissions are automatically granted via manifest
        // No runtime permission check needed for BLUETOOTH and BLUETOOTH_ADMIN
        setPermissionStatus({
          granted: true,
          canAskAgain: true,
          loading: false,
        });
      }
    } catch (error) {
      console.error("Error checking Bluetooth permissions:", error);
      setPermissionStatus({
        granted: false,
        canAskAgain: true,
        loading: false,
      });
    }
  };

  const requestPermissions = async (): Promise<boolean> => {
    if (Platform.OS !== "android") {
      Alert.alert("Not Supported", "Classic Bluetooth is not supported on iOS");
      return false;
    }

    setPermissionStatus((prev) => ({ ...prev, loading: true }));

    try {
      const apiLevel = Platform.Version as number;
      let allGranted = false;

      if (apiLevel >= 31) {
        // Android 12 and above - Request BLUETOOTH_CONNECT and BLUETOOTH_SCAN
        const results = await PermissionsAndroid.requestMultiple([
          PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT,
          PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN,
        ]);

        allGranted =
          results[PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT] ===
            PermissionsAndroid.RESULTS.GRANTED &&
          results[PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN] ===
            PermissionsAndroid.RESULTS.GRANTED;

        const canAskAgain =
          results[PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT] !==
            PermissionsAndroid.RESULTS.NEVER_ASK_AGAIN &&
          results[PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN] !==
            PermissionsAndroid.RESULTS.NEVER_ASK_AGAIN;

        setPermissionStatus({
          granted: allGranted,
          canAskAgain,
          loading: false,
        });
      } else {
        // Android 11 and below - Permissions are declared in manifest
        // They are automatically granted, no runtime request needed
        allGranted = true;
        setPermissionStatus({
          granted: true,
          canAskAgain: true,
          loading: false,
        });
      }

      if (!allGranted) {
        Alert.alert(
          "Permissions Required",
          "Bluetooth permissions are required to connect to devices. Please grant them in your device settings.",
          [{ text: "OK" }]
        );
      }

      return allGranted;
    } catch (error) {
      console.error("Error requesting Bluetooth permissions:", error);
      setPermissionStatus({
        granted: false,
        canAskAgain: true,
        loading: false,
      });
      return false;
    }
  };

  return {
    ...permissionStatus,
    requestPermissions,
    checkPermissions,
  };
}
