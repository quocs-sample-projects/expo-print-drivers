import { StatusBar } from "expo-status-bar";
import { ReactNode, useEffect, useState } from "react";
import {
  Button,
  ScrollView,
  View,
  Text,
  Alert,
  TouchableOpacity,
} from "react-native";
import {
  useBluetoothPermissions,
  useBluetoothEnabled,
  useBluetoothConnection,
  BluetoothDevice,
  BluetoothService,
} from "../modules/printer-drivers";
import { styles, colors } from "./App.styles";
import { isEqual } from "lodash";

export default function App() {
  const [devices, setDevices] = useState<BluetoothDevice[]>([]);

  const {
    granted,
    loading: permissionLoading,
    requestPermissions,
  } = useBluetoothPermissions();

  const {
    isEnabled,
    isAvailable,
    loading: enabledLoading,
  } = useBluetoothEnabled();

  const { status, connect, disconnect, clearError } = useBluetoothConnection();

  useEffect(() => {
    if (enabledLoading || permissionLoading) {
      return;
    }

    if (!isAvailable) {
      return;
    }

    if (!isEnabled) {
      return;
    }

    if (!granted) {
      return;
    }

    loadPairedDevices();
  }, [isAvailable, isEnabled, granted, permissionLoading, enabledLoading]);

  useEffect(() => {
    if (status.error) {
      Alert.alert("Bluetooth Connection Error", status.error);
    }
  }, [status]);

  const loadPairedDevices = async () => {
    if (!granted) {
      const hasPermission = await requestPermissions();
      if (!hasPermission) {
        Alert.alert("Error", "Bluetooth permissions are required");
        return;
      }
    }

    try {
      const pairedDevices = await BluetoothService.getPairedDevices();
      setDevices(pairedDevices);
    } catch (error) {
      Alert.alert("Error", "Failed to load paired devices");
    }
  };

  const handleConnect = async (device: BluetoothDevice) => {
    if (status.error) {
      clearError();
    }
    try {
      if (status.isConnected || status.isConnecting) {
        await disconnect();
      }
      await connect(device, true);
    } catch (error) {
      Alert.alert("Bluetooth Connection Error", "Failed to connect to device");
    }
  };

  const handleDisconnect = async () => {
    await disconnect();
  };

  const renderHeader = () => (
    <Text style={styles.title}>Bluetooth Printer</Text>
  );

  const renderBluetoothStatus = () => {
    if (enabledLoading) {
      return <Text style={styles.permissionText}>Checking Bluetooth...</Text>;
    }

    if (!isAvailable) {
      return (
        <View style={styles.statusCardError}>
          <Text style={styles.statusTextError}>⚠️ Bluetooth not available</Text>
        </View>
      );
    }

    if (!isEnabled) {
      return (
        <View style={styles.statusCardWarning}>
          <Text style={styles.statusTextWarning}>
            ⚠️ Bluetooth is off - Please enable it in settings
          </Text>
        </View>
      );
    }

    return (
      <View style={styles.statusCardSuccess}>
        <Text style={styles.statusTextSuccess}>✓ Bluetooth is on</Text>
      </View>
    );
  };

  const renderPermissionStatus = () => {
    let innerComponent: ReactNode;

    if (permissionLoading) {
      innerComponent = (
        <Text style={styles.permissionText}>Checking permissions...</Text>
      );
    } else if (!granted) {
      innerComponent = (
        <>
          <Text style={styles.permissionText}>
            Bluetooth permission required
          </Text>
          <Button title="Grant Permission" onPress={requestPermissions} />
        </>
      );
    } else {
      innerComponent = (
        <Text style={styles.statusTextSuccess}>✓ Permission granted</Text>
      );
    }

    return (
      <View
        style={granted ? styles.statusCardSuccess : styles.statusCardWarning}
      >
        {innerComponent}
      </View>
    );
  };

  const renderDeviceItem = (item: BluetoothDevice) => {
    const isThisDeviceConnected = isEqual(status.connectedDevice, item);
    const isThisDeviceConnecting = isEqual(status.connectingDevice, item);
    const isAnyDeviceBusy = status.isConnecting || status.isDisconnecting;

    const getButtonConfig = () => {
      if (isThisDeviceConnected) {
        return {
          onPress: handleDisconnect,
          text: status.isDisconnecting ? "Disconnecting..." : "Disconnect",
          backgroundColor: colors.error,
        };
      }

      const text = isThisDeviceConnecting
        ? status.isDisconnecting
          ? "Disconnecting..."
          : "Connecting..."
        : "Connect";

      return {
        onPress: () => handleConnect(item),
        text,
        backgroundColor: undefined,
      };
    };

    const buttonConfig = getButtonConfig();

    return (
      <View key={item.address} style={styles.deviceCard}>
        <View style={styles.deviceInfo}>
          <Text style={styles.deviceName}>{item.name}</Text>
          <Text style={styles.deviceAddress}>{item.address}</Text>
        </View>
        <TouchableOpacity
          style={[
            styles.connectButton,
            buttonConfig.backgroundColor && {
              backgroundColor: buttonConfig.backgroundColor,
            },
            isAnyDeviceBusy && styles.connectButtonDisabled,
          ]}
          onPress={buttonConfig.onPress}
          disabled={isAnyDeviceBusy}
        >
          <Text
            style={[
              styles.connectButtonText,
              isAnyDeviceBusy && styles.connectButtonTextDisabled,
            ]}
          >
            {buttonConfig.text}
          </Text>
        </TouchableOpacity>
      </View>
    );
  };

  const renderDeviceList = () => {
    if (!isAvailable || !isEnabled) {
      return <Text style={styles.emptyText}>No devices found</Text>;
    }

    return (
      <View style={styles.deviceList}>
        {devices.map((item) => renderDeviceItem(item))}
      </View>
    );
  };

  return (
    <View style={styles.screen}>
      <ScrollView style={styles.container}>
        {renderHeader()}
        {renderBluetoothStatus()}
        {renderPermissionStatus()}
        <Text style={styles.subtitle}>Paired Devices:</Text>
        {renderDeviceList()}
      </ScrollView>
      <StatusBar style="auto" />
    </View>
  );
}
