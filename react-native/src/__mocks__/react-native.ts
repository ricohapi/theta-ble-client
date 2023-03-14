export const NativeModules  = {
  ThetaBleClientReactNative: {
    nativeScan: jest.fn(),
    nativeConnect: jest.fn(),
    nativeIsConnected: jest.fn(),
    nativeDisconnect: jest.fn(),
    nativeReleaseDevice: jest.fn(),
    nativeContainService: jest.fn(),

    nativeGetFirmwareRevision: jest.fn(),
    nativeGetManufacturerName: jest.fn(),
    nativeGetModelNumber: jest.fn(),
    nativeGetSerialNumber: jest.fn(),
    nativeGetWlanMacAddress: jest.fn(),
    nativeGetBluetoothMacAddress: jest.fn(),

    nativeGetBatteryLevel: jest.fn(),
    nativeSetBatteryLevelNotify: jest.fn(),
    nativeGetBatteryStatus: jest.fn(),
    nativeSetBatteryStatusNotify: jest.fn(),
    nativeGetCameraPower: jest.fn(),
    nativeSetCameraPower: jest.fn(),
    nativeSetCameraPowerNotify: jest.fn(),
    nativeSetCommandErrorDescriptionNotify: jest.fn(),
    nativeGetPluginControl: jest.fn(),
    nativeSetPluginControl: jest.fn(),
    nativeSetPluginControlNotify: jest.fn(),

    nativeGetPluginList: jest.fn(),
    nativeGetPluginOrders: jest.fn(),
    nativeSetPluginOrders: jest.fn(),

    nativeGetCaptureMode: jest.fn(),
    nativeSetCaptureMode: jest.fn(),
    nativeTakePicture: jest.fn(),

    nativeCameraControlCommandV2GetInfo: jest.fn(),
    nativeCameraControlCommandV2GetState: jest.fn(),
    nativeCameraControlCommandV2SetStateNotify: jest.fn(),
    nativeCameraControlCommandV2GetState2: jest.fn(),
  },
};

export const Platform = {
  select: jest.fn(),
};

export const NativeEventEmitter_addListener = jest.fn();

export class NativeEventEmitter {
  addListener = NativeEventEmitter_addListener;
}
