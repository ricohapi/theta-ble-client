import { TurboModuleRegistry, type TurboModule } from 'react-native';
import type { UnsafeObject } from 'react-native/Libraries/Types/CodegenTypes';

// TODO: rich THETA data (plugin settings, wifi info, etc.) is typed as
// UnsafeObject here instead of full codegen shapes; native-functions.ts
// re-applies the real types at the JS boundary instead, same as the old
// bridge did via `any`. Model these for real once the native side is
// codegen'd against exact fields.
//
// Also: every method below takes a single UnsafeObject instead of
// individual scalar params, so the native side extracts each field with
// a validated getter (ReadableMap.getInt/getString on Android,
// RCTConvert on iOS) instead of trusting codegen's raw double/string
// params.
export interface Spec extends TurboModule {
  nativeScan(params: UnsafeObject): Promise<Array<UnsafeObject>>;
  nativeScanThetaSsid(params: UnsafeObject): Promise<Array<UnsafeObject>>;
  nativeConnect(params: UnsafeObject): Promise<void>;
  nativeIsConnected(params: UnsafeObject): Promise<boolean>;
  nativeDisconnect(params: UnsafeObject): Promise<void>;
  nativeContainService(params: UnsafeObject): Promise<boolean>;

  nativeGetFirmwareRevision(params: UnsafeObject): Promise<string>;
  nativeGetManufacturerName(params: UnsafeObject): Promise<string>;
  nativeGetModelNumber(params: UnsafeObject): Promise<string>;
  nativeGetSerialNumber(params: UnsafeObject): Promise<string>;
  nativeGetWlanMacAddress(params: UnsafeObject): Promise<string>;
  nativeGetBluetoothMacAddress(params: UnsafeObject): Promise<string>;

  nativeGetBatteryLevel(params: UnsafeObject): Promise<number>;
  nativeSetBatteryLevelNotify(params: UnsafeObject): Promise<void>;
  nativeGetBatteryStatus(params: UnsafeObject): Promise<string>;
  nativeSetBatteryStatusNotify(params: UnsafeObject): Promise<void>;
  nativeGetCameraPower(params: UnsafeObject): Promise<string>;
  nativeSetCameraPower(params: UnsafeObject): Promise<void>;
  nativeSetCameraPowerNotify(params: UnsafeObject): Promise<void>;
  nativeSetCommandErrorDescriptionNotify(params: UnsafeObject): Promise<void>;
  nativeGetPluginControl(params: UnsafeObject): Promise<UnsafeObject>;
  nativeSetPluginControl(params: UnsafeObject): Promise<void>;
  nativeSetPluginControlNotify(params: UnsafeObject): Promise<void>;

  nativeGetPluginList(params: UnsafeObject): Promise<UnsafeObject>;
  nativeGetPluginOrders(params: UnsafeObject): Promise<UnsafeObject>;
  nativeSetPluginOrders(params: UnsafeObject): Promise<void>;

  nativeGetCaptureMode(params: UnsafeObject): Promise<string>;
  nativeSetCaptureMode(params: UnsafeObject): Promise<void>;
  nativeGetFileFormat(params: UnsafeObject): Promise<string>;
  nativeSetFileFormat(params: UnsafeObject): Promise<void>;
  nativeGetMaxRecordableTime(params: UnsafeObject): Promise<string>;
  nativeSetMaxRecordableTime(params: UnsafeObject): Promise<void>;
  nativeTakePicture(params: UnsafeObject): Promise<void>;

  nativeCameraControlCommandV2GetInfo(
    params: UnsafeObject
  ): Promise<UnsafeObject>;
  nativeCameraControlCommandV2GetState(
    params: UnsafeObject
  ): Promise<UnsafeObject>;
  nativeCameraControlCommandV2SetStateNotify(
    params: UnsafeObject
  ): Promise<void>;
  nativeCameraControlCommandV2GetState2(
    params: UnsafeObject
  ): Promise<UnsafeObject>;
  nativeCameraControlCommandV2GetOptions(
    params: UnsafeObject
  ): Promise<UnsafeObject>;
  nativeCameraControlCommandV2GetOptionsByString(
    params: UnsafeObject
  ): Promise<UnsafeObject>;
  nativeCameraControlCommandV2SetOptions(params: UnsafeObject): Promise<void>;
  nativeCameraControlCommandV2ReleaseShutter(
    params: UnsafeObject
  ): Promise<void>;

  nativeBluetoothControlCommandScanPeripheralDevice(
    params: UnsafeObject
  ): Promise<Array<UnsafeObject>>;
  nativeBluetoothControlCommandScanPeripheralDeviceStart(
    params: UnsafeObject
  ): Promise<void>;
  nativeBluetoothControlCommandScanPeripheralDeviceStop(
    params: UnsafeObject
  ): Promise<void>;
  nativeBluetoothControlCommandConnectPeripheralDevice(
    params: UnsafeObject
  ): Promise<void>;
  nativeBluetoothControlCommandDeletePeripheralDevice(
    params: UnsafeObject
  ): Promise<void>;

  nativeWlanControlCommandGetWlanPasswordState(
    params: UnsafeObject
  ): Promise<string>;

  nativeWlanControlCommandV2SetNetworkType(params: UnsafeObject): Promise<void>;
  nativeWlanControlCommandV2SetNetworkTypeNotify(
    params: UnsafeObject
  ): Promise<void>;
  nativeWlanControlCommandV2GetConnectedWifiInfo(
    params: UnsafeObject
  ): Promise<UnsafeObject>;
  nativeWlanControlCommandV2SetConnectedWifiInfoNotify(
    params: UnsafeObject
  ): Promise<void>;
  nativeWlanControlCommandV2ScanSsidStart(params: UnsafeObject): Promise<void>;
  nativeWlanControlCommandV2ScanSsidStop(params: UnsafeObject): Promise<void>;
  nativeWlanControlCommandV2SetAccessPointDynamically(
    params: UnsafeObject
  ): Promise<void>;
  nativeWlanControlCommandV2SetAccessPointStatically(
    params: UnsafeObject
  ): Promise<void>;

  nativeReleaseDevice(params: UnsafeObject): Promise<void>;

  // Required stubs for NativeEventEmitter to work against a TurboModule.
  addListener(eventName: string): void;
  removeListeners(count: number): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>(
  'ThetaBleClientReactNative'
);
