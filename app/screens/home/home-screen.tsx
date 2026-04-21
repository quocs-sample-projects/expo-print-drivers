import { ReactNode, useEffect, useState } from "react";
import { Button, View, Text, Alert, TouchableOpacity } from "react-native";
import {
  useBluetoothPermissions,
  useBluetoothEnabled,
  useBluetoothConnection,
  BluetoothDevice,
  BluetoothService,
  TicketPrinter,
  PrinterType,
} from "@/modules/printer-drivers";
import { isEmpty, isEqual } from "lodash";
import { FileHelper } from "@/app/utils/helpers";
import { TIME_UNITS, TEST_DATA } from "@/app/utils/constants";
import { colors } from "@/app/utils/theme";
import { AppTitle, Screen } from "@/app/components";
import { styles } from "./home-screen.styles";

export const HomeScreen = () => {
  const [devices, setDevices] = useState<BluetoothDevice[]>([]);
  const [printDisabled, setPrintDisabled] = useState<boolean>(false);

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

  useEffect(() => {
    const qrExists = FileHelper.checkFileExists(TEST_DATA.PRINT_DATA.maQR);
    if (!qrExists) {
      FileHelper.downloadFile(TEST_DATA.URL_MA_QR, TEST_DATA.PRINT_DATA.maQR);
    } else {
      console.log(
        "--> QR file URI:",
        FileHelper.getFileUri(TEST_DATA.PRINT_DATA.maQR),
      );
    }
  }, []);

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

  const handlePrint = () => {
    const deviceName = status.connectedDevice?.name.toLowerCase() ?? "";
    let usingDriver: string | null = null;
    if (deviceName.includes("woosim")) {
      usingDriver = PrinterType.WOOSIM_WSP_i350;
    } else if (deviceName.includes("pr3")) {
      usingDriver = PrinterType.HONEYWELL_PR3;
    } else if (deviceName.includes("mpd31d")) {
      usingDriver = PrinterType.HONEYWELL_0188;
    }

    setPrintDisabled(true);
    if (usingDriver) {
      TicketPrinter.giayBaoTienNuocBenThanh(usingDriver, TEST_DATA.PRINT_DATA);
    } else {
      Alert.alert(
        "Printing Error",
        "Unsupported printer model for test print.",
      );
    }
    setTimeout(() => {
      setPrintDisabled(false);
    }, TIME_UNITS.SECOND * 2);
  };

  const BluetoothStatus = () => {
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

  const PermissionStatus = () => {
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
        <View style={styles.deviceCardButton}>
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
          {isThisDeviceConnected && (
            <TouchableOpacity
              style={[
                { ...styles.connectButton, backgroundColor: colors.primary },
                printDisabled && styles.connectButtonDisabled,
              ]}
              onPress={handlePrint}
              disabled={printDisabled}
            >
              <Text style={styles.connectButtonText}>Print</Text>
            </TouchableOpacity>
          )}
        </View>
      </View>
    );
  };

  const DeviceList = () => {
    if (!isAvailable || !isEnabled || isEmpty(devices)) {
      return <Text style={styles.emptyText}>No devices found</Text>;
    }

    return (
      <View style={styles.deviceList}>
        {devices.map((item) => renderDeviceItem(item))}
      </View>
    );
  };

  const RefreshButton = () => {
    return (
      <TouchableOpacity
        style={[
          styles.refreshButton,
          (!granted || !isEnabled) && styles.refreshButtonDisabled,
        ]}
        onPress={loadPairedDevices}
        disabled={!granted || !isEnabled}
      >
        <Text
          style={[
            styles.refreshButtonText,
            (!granted || !isEnabled) && styles.refreshButtonTextDisabled,
          ]}
        >
          Refresh devices
        </Text>
      </TouchableOpacity>
    );
  };

  return (
    <Screen>
      <AppTitle />
      <BluetoothStatus />
      <PermissionStatus />
      <RefreshButton />
      <Text style={styles.subtitle}>Paired Devices:</Text>
      <DeviceList />
    </Screen>
  );
};

export const HomeScreenName = "home";
