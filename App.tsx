import { StatusBar } from "expo-status-bar";
import { StyleSheet, Text, View } from "react-native";
import { useEffect } from "react";
import printerDrivers from "./modules/printer-drivers";

export default function App() {
  useEffect(() => {
    console.log(
      "\b --> App.tsx:9 --> App --> printerDrivers.PI:",
      printerDrivers.PI
    );
    console.log(
      "\b --> App.tsx:11 --> App --> printerDrivers.hello():",
      printerDrivers.hello()
    );
  }, []);

  return (
    <View style={styles.container}>
      <Text>Open up App.tsx to start working on your app!</Text>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
});
