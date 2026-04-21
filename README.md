# Init project

```bash
npx create-expo-app --template blank-typescript
bun install
npx expo install dev-client
npx expo install expo-system-ui
npx expo install sharp-cli

npx create-expo-module <module_name> --local
npx expo-doctor
```

# Run project

```bash
fnm use

bun install

npx expo prebuild --clean

npx expo run:android # or `npx expo run:ios`

npx expo start
```

# Using local module

```typescript
// If your file is in /src/components/MyScreen.tsx
// and your module is in /modules/my-native-module/index.ts
import MyNativeModule from "../../modules/my-native-module";

// Use it like this:
const result = MyNativeModule.hello();
```
