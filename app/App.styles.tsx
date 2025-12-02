import { StyleSheet } from "react-native";

const colors = {
  primary: "#2196F3",
  success: "#4CAF50",
  warning: "#FF9800",
  error: "#f44336",
  white: "#FFFFFF",
  gray: "#666",
  lightGray: "#999",
  borderGray: "#BDBDBD",
  disabledText: "#757575",
  successBg: "#E8F5E9",
  warningBg: "#FFF3E0",
  errorBg: "#FFEBEE",
};

const shadows = {
  small: {
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
};

const baseCard = {
  backgroundColor: colors.white,
  padding: 15,
  borderRadius: 8,
  marginBottom: 15,
};

const baseButton = {
  paddingVertical: 10,
  paddingHorizontal: 20,
  borderRadius: 6,
  alignItems: "center" as const,
  justifyContent: "center" as const,
};

const baseButtonText = {
  color: colors.white,
  fontSize: 14,
  fontWeight: "600" as const,
};

const baseStatusCard = {
  ...baseCard,
  borderWidth: 2,
  alignItems: "center" as const,
  justifyContent: "center" as const,
};

const baseStatusText = {
  fontSize: 14,
  fontWeight: "600" as const,
  textAlign: "center" as const,
};

export const styles = StyleSheet.create({
  screen: {
    flex: 1,
    marginTop: 40,
    margin: 20,
  },
  container: {
    flex: 1,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
    textAlign: "center",
  },
  subtitle: {
    fontSize: 18,
    fontWeight: "600",
    marginTop: 20,
    marginBottom: 10,
  },
  statusCard: {
    ...baseCard,
    borderWidth: 2,
    flexDirection: "row",
    alignItems: "center",
  },
  statusCardSuccess: {
    ...baseStatusCard,
    borderColor: colors.success,
    backgroundColor: colors.successBg,
  },
  statusCardWarning: {
    ...baseStatusCard,
    borderColor: colors.warning,
    backgroundColor: colors.warningBg,
  },
  statusCardError: {
    ...baseStatusCard,
    borderColor: colors.error,
    backgroundColor: colors.errorBg,
  },
  statusLabel: {
    fontSize: 16,
    fontWeight: "600",
    marginRight: 10,
  },
  statusText: {
    fontSize: 16,
    fontWeight: "bold",
  },
  statusTextSuccess: {
    ...baseStatusText,
    color: colors.success,
  },
  statusTextWarning: {
    ...baseStatusText,
    color: colors.warning,
  },
  statusTextError: {
    ...baseStatusText,
    color: colors.error,
  },
  buttonRow: {
    flexDirection: "row",
    gap: 10,
    marginTop: 15,
    flexWrap: "wrap",
  },
  deviceList: {
    marginBottom: 20,
  },
  deviceCard: {
    ...baseCard,
    marginBottom: 10,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    ...shadows.small,
  },
  deviceInfo: {
    flex: 1,
    marginRight: 10,
  },
  deviceName: {
    fontSize: 16,
    fontWeight: "600",
    marginBottom: 5,
  },
  deviceAddress: {
    fontSize: 14,
    color: colors.gray,
  },
  emptyText: {
    textAlign: "center",
    color: colors.lightGray,
    marginTop: 20,
    fontSize: 16,
  },
  permissionCard: {
    ...baseCard,
    backgroundColor: colors.warningBg,
    borderWidth: 1,
    borderColor: colors.warning,
    alignItems: "center",
    justifyContent: "center",
  },
  permissionText: {
    fontSize: 14,
    fontWeight: "600",
    textAlign: "center",
    color: colors.gray,
    marginBottom: 10,
  },
  refreshButton: {
    ...baseButton,
    backgroundColor: colors.primary,
  },
  refreshButtonDisabled: {
    backgroundColor: colors.borderGray,
    opacity: 0.6,
  },
  refreshButtonText: {
    ...baseButtonText,
  },
  refreshButtonTextDisabled: {
    color: colors.disabledText,
  },
  actionButton: {
    ...baseButton,
    backgroundColor: colors.success,
    minWidth: 110,
  },
  connectButton: {
    ...baseButton,
    paddingVertical: 8,
    paddingHorizontal: 16,
    backgroundColor: colors.success,
    minWidth: 120,
  },
  connectButtonDisabled: {
    backgroundColor: colors.borderGray,
    opacity: 0.6,
  },
  connectButtonText: {
    ...baseButtonText,
  },
  connectButtonTextDisabled: {
    color: colors.disabledText,
  },
});
