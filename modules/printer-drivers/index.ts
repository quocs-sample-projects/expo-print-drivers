import PrinterDriversModeule from "./src/PrinterDriversModule";

export * from "./src/PrinterDrivers.types";
export * from "./src/hooks";

export const BluetoothService = {
  getState: () => PrinterDriversModeule.getBluetoothState(),
  isAvailable: () => PrinterDriversModeule.isBluetoothAvailable(),
  isEnabled: () => PrinterDriversModeule.isBluetoothEnabled(),
  async getPairedDevices() {
    return await PrinterDriversModeule.getPairedDevices();
  },
  async connect(address: string, secure: boolean) {
    return await PrinterDriversModeule.connect(address, secure);
  },
  async disconnect() {
    return await PrinterDriversModeule.disconnect();
  },
};

export const TicketPrinter = {
  giayBaoTienNuocNongThon(printerType: string, jsonData: object) {
    PrinterDriversModeule.giayBaoTienNuocNongThon(printerType, jsonData);
  },
};
