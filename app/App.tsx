import { StatusBar } from "expo-status-bar";
import { ReactNode, use, useEffect, useState } from "react";
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
  TicketPrinter,
} from "../modules/printer-drivers";
import { styles, colors } from "./App.styles";
import { isEmpty, isEqual } from "lodash";
import { FileHelper } from "./utils/helpers";
import PrinterDriversModule from "../modules/printer-drivers/src/PrinterDriversModule";
import { TIME_UNITS } from "./utils/constants";

const MA_QR =
  "https://drive.google.com/uc?export=download&id=1zJtXeNPdzZHqr6rzdoBXekNdk9pDfThj";

const testPrinterData = {
  tenCongTy: "CTY CỔ PHẨN CẤP NƯỚC CẤP CẤP",
  tenPhieu: "PHIẾU BÁO CHỈ SỐ ĐOM ĐÓM",
  ky: "12/2025",
  tuNgay: "04/11/2025",
  denNgay: "04/12/2025",
  mdb: "0123456789",
  mlt: "0123456789",
  khachHang: "NGUYỄN VĂN A",
  soDienThoai: "0123456789",
  diaChi:
    "123 Đường Đi Hoài Sẽ Thấy, Phường Biết Tới, Quận Rõ Ràng, TP Hiểu Rồi",
  giaBieu: "21",
  dinhMuc: "69",
  chiSo: "1600",
  tienNuoc: "3000",
  tienKyMoi: "2169",
  nhanVien: "Trần Văn A",
  dienThoaiNhanVien: "0987654321",
  maQR: "ma_qr.png",
};

export default function App() {
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
    const qrExists = FileHelper.checkFileExists("ma_qr.png");
    if (!qrExists) {
      FileHelper.downloadFile(MA_QR, "ma_qr.png");
    } else {
      console.log("--> QR file URI:", FileHelper.getFileUri("ma_qr.png"));
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
      usingDriver = PrinterDriversModule.PrinterType.WOOSIM_WSP_i350;
    } else if (deviceName.includes("pr3")) {
      usingDriver = PrinterDriversModule.PrinterType.HONEYWELL_PR3;
    } else if (deviceName.includes("mpd31d")) {
      usingDriver = PrinterDriversModule.PrinterType.HONEYWELL_0188;
    }

    setPrintDisabled(true);
    if (usingDriver) {
      TicketPrinter.testGiayBaoTienNuoc(usingDriver, testPrinterData);
    } else {
      Alert.alert(
        "Printing Error",
        "Unsupported printer model for test print."
      );
    }
    setTimeout(() => {
      setPrintDisabled(false);
    }, TIME_UNITS.SECOND * 2);
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

  const renderDeviceList = () => {
    if (!isAvailable || !isEnabled || isEmpty(devices)) {
      return <Text style={styles.emptyText}>No devices found</Text>;
    }

    return (
      <View style={styles.deviceList}>
        {devices.map((item) => renderDeviceItem(item))}
      </View>
    );
  };

  const renderRefreshButton = () => {
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
    <View style={styles.screen}>
      <ScrollView style={styles.container}>
        {renderHeader()}
        {renderBluetoothStatus()}
        {renderPermissionStatus()}
        {renderRefreshButton()}
        <Text style={styles.subtitle}>Paired Devices:</Text>
        {renderDeviceList()}
      </ScrollView>
      <StatusBar style="auto" />
    </View>
  );
}
