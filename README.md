# Init project

```bash
npx create-expo-app --template blank-typescript
pnpm install
pnpm add dev-client
pnpm add expo-system-ui
npx create-expo-module <module_name> --local
pnpm add ./modules/<module_name>
npx expo-doctor
```

# Run project

```bash
# With npx
npx expo run:android # or `npx expo run:ios`
npx expo start

# With custom commands in package.json
pnpm android
pnpm start
```
