// Reexport the native module. On web, it will be resolved to PrinterDriversModule.web.ts
// and on native platforms to PrinterDriversModule.ts
export { default } from "./src/PrinterDriversModule";
export * from "./src/PrinterDrivers.types";
export * from "./src/hooks";
