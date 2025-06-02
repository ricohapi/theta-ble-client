export const NativeModules  = {
  ThetaBleClientReactNative: {
    nativeScan: jest.fn(),
    nativeScanThetaSsid: jest.fn(),
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
    nativeGetFileFormat: jest.fn(),
    nativeSetFileFormat: jest.fn(),
    nativeGetMaxRecordableTime: jest.fn(),
    nativeSetMaxRecordableTime: jest.fn(),
    nativeTakePicture: jest.fn(),

    nativeCameraControlCommandV2GetInfo: jest.fn(),
    nativeCameraControlCommandV2GetState: jest.fn(),
    nativeCameraControlCommandV2SetStateNotify: jest.fn(),
    nativeCameraControlCommandV2GetState2: jest.fn(),
    nativeCameraControlCommandV2GetOptions: jest.fn(),
    nativeCameraControlCommandV2SetOptions: jest.fn(),
    nativeCameraControlCommandV2ReleaseShutter: jest.fn(),
  
    nativeBluetoothControlCommandScanPeripheralDevice: jest.fn(),
    nativeBluetoothControlCommandScanPeripheralDeviceStart: jest.fn(),
    nativeBluetoothControlCommandScanPeripheralDeviceStop: jest.fn(),
    nativeBluetoothControlCommandConnectPeripheralDevice: jest.fn(),
    nativeBluetoothControlCommandDeletePeripheralDevice: jest.fn(),

    nativeWlanControlCommandV2SetNetworkType: jest.fn(),
    nativeWlanControlCommandV2SetNetworkTypeNotify: jest.fn(),
    nativeWlanControlCommandV2GetConnectedWifiInfo: jest.fn(),
    nativeWlanControlCommandV2SetConnectedWifiInfoNotify: jest.fn(),
    nativeWlanControlCommandV2ScanSsidStart: jest.fn(),
    nativeWlanControlCommandV2ScanSsidStop: jest.fn(),
    nativeWlanControlCommandV2SetAccessPointDynamically: jest.fn(),
    nativeWlanControlCommandV2SetAccessPointStatically: jest.fn(),
  },
};

export const Platform = {
  select: jest.fn(),
};

export const NativeEventEmitter_addListener = jest.fn();

export class NativeEventEmitter {
  addListener = NativeEventEmitter_addListener;
}
