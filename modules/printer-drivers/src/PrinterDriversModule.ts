import { NativeModule, requireNativeModule } from "expo";

import {
  BluetoothDevice,
  PrinterDriversModuleEvents,
} from "./PrinterDrivers.types";

declare class PrinterDriversModule extends NativeModule<PrinterDriversModuleEvents> {
  PrinterType: {
    WOOSIM_WSP_i350: string;
    HONEYWELL_0188: string;
  };
  BluetoothConnectionState: {
    NONE: number;
    LISTEN: number;
    CONNECTING: number;
    CONNECTED: number;
  };
  isBluetoothAvailable(): boolean;
  isBluetoothEnabled(): boolean;
  getPairedDevices(): Promise<BluetoothDevice[]>;
  connect(address: string, secure: boolean): Promise<void>;
  disconnect(): Promise<void>;
  giayBaoTienNuocNongThon(printerType: string, jsonData: object): void;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<PrinterDriversModule>("PrinterDrivers");
