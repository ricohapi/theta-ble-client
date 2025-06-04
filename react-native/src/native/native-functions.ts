import {
  EmitterSubscription,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';
import type { BaseNotify } from '../theta-device/notify';
import type {
  Timeout,
} from '../values';
import type {
  ConnectedWifiInfo,
  PeripheralDevice,
  Proxy,
  ThetaInfo,
  ThetaOptions,
  ThetaState,
  ThetaState2,
} from '../service/data';
import type {
  CameraPowerEnum,
  CaptureModeEnum,
  ChargingStateEnum,
  FileFormatEnum,
  MaxRecordableTimeEnum,
  NetworkTypeEnum,
  OptionName,
  PluginControl,
  PluginList,
  PluginOrders,
  ThetaModel,
  WifiSecurityModeEnum,
  WlanPasswordStateEnum,
} from '../service';

const ThetaBleClientReactNative = NativeModules.ThetaBleClientReactNative;

export function addNotifyListener(callback: (event: BaseNotify) => void): EmitterSubscription {
  const eventEmitter = new NativeEventEmitter(NativeModules.ThetaBleClientReactNative);
  return eventEmitter.addListener('ThetaBleNotify', callback);
}

interface DeviceListItem {
  deviceId: number;
  name: string;
}

interface ScanParams {
  name?: string;
  timeout?: Timeout;
}

export async function nativeScan(params: ScanParams): Promise<DeviceListItem[]> {
  return ThetaBleClientReactNative.nativeScan(params);
}

export async function nativeScanThetaSsid(params: ScanSsidParams): Promise<SsidListItem[]> {
  return ThetaBleClientReactNative.nativeScanThetaSsid(params);
}

export async function nativeConnect(id: number, uuid?: string) {
  return ThetaBleClientReactNative.nativeConnect(id, uuid);
}

interface ScanSsidParams {
  model?: ThetaModel;
  timeout?: number;
}

interface SsidListItem {
  ssid: string;
  password: string;
}

export async function nativeIsConnected(id: number): Promise<boolean> {
  return ThetaBleClientReactNative.nativeIsConnected(id);
}

export async function nativeDisconnect(id: number) {
  return ThetaBleClientReactNative.nativeDisconnect(id);
}

export async function nativeContainService(id: number, service: string): Promise<boolean> {
  return ThetaBleClientReactNative.nativeContainService(id, service);
}

export async function nativeGetFirmwareRevision(id: number): Promise<string> {
  return ThetaBleClientReactNative.nativeGetFirmwareRevision(id);
}

export async function nativeGetManufacturerName(id: number): Promise<string> {
  return ThetaBleClientReactNative.nativeGetManufacturerName(id);
}

export async function nativeGetModelNumber(id: number): Promise<string> {
  return ThetaBleClientReactNative.nativeGetModelNumber(id);
}

export async function nativeGetSerialNumber(id: number): Promise<string> {
  return ThetaBleClientReactNative.nativeGetSerialNumber(id);
}

export async function nativeGetWlanMacAddress(id: number): Promise<string> {
  return ThetaBleClientReactNative.nativeGetWlanMacAddress(id);
}

export async function nativeGetBluetoothMacAddress(id: number): Promise<string> {
  return ThetaBleClientReactNative.nativeGetBluetoothMacAddress(id);
}

export async function nativeGetBatteryLevel(id: number): Promise<number> {
  return ThetaBleClientReactNative.nativeGetBatteryLevel(id);
}

export async function nativeSetBatteryLevelNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeSetBatteryLevelNotify(id, enable);
}

export async function nativeGetBatteryStatus(id: number): Promise<ChargingStateEnum> {
  return ThetaBleClientReactNative.nativeGetBatteryStatus(id);
}

export async function nativeSetBatteryStatusNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeSetBatteryStatusNotify(id, enable);
}

export async function nativeGetCameraPower(id: number): Promise<CameraPowerEnum> {
  return ThetaBleClientReactNative.nativeGetCameraPower(id);
}
export async function nativeSetCameraPower(id: number, value: CameraPowerEnum) {
  return ThetaBleClientReactNative.nativeSetCameraPower(id, value);
}

export async function nativeSetCameraPowerNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeSetCameraPowerNotify(id, enable);
}

export async function nativeSetCommandErrorDescriptionNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeSetCommandErrorDescriptionNotify(id, enable);
}

export async function nativeGetPluginControl(id: number): Promise<PluginControl> {
  return ThetaBleClientReactNative.nativeGetPluginControl(id);
}

export async function nativeSetPluginControl(id: number, value: PluginControl) {
  return ThetaBleClientReactNative.nativeSetPluginControl(id, value);
}

export async function nativeSetPluginControlNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeSetPluginControlNotify(id, enable);
}

export async function nativeGetPluginList(id: number): Promise<PluginList> {
  return ThetaBleClientReactNative.nativeGetPluginList(id);
}

export async function nativeGetPluginOrders(id: number): Promise<PluginOrders> {
  return ThetaBleClientReactNative.nativeGetPluginOrders(id);
}

export async function nativeSetPluginOrders(id: number, value: PluginOrders) {
  return ThetaBleClientReactNative.nativeSetPluginOrders(id, value);
}

export async function nativeGetCaptureMode(id: number): Promise<CaptureModeEnum> {
  return ThetaBleClientReactNative.nativeGetCaptureMode(id);
}

export async function nativeSetCaptureMode(id: number, value: CaptureModeEnum) {
  return ThetaBleClientReactNative.nativeSetCaptureMode(id, value);
}

export async function nativeGetFileFormat(id: number): Promise<FileFormatEnum> {
  return ThetaBleClientReactNative.nativeGetFileFormat(id);
}

export async function nativeSetFileFormat(id: number, value: FileFormatEnum) {
  return ThetaBleClientReactNative.nativeSetFileFormat(id, value);
}

export async function nativeGetMaxRecordableTime(id: number): Promise<MaxRecordableTimeEnum> {
  return ThetaBleClientReactNative.nativeGetMaxRecordableTime(id);
}

export async function nativeSetMaxRecordableTime(id: number, value: MaxRecordableTimeEnum) {
  return ThetaBleClientReactNative.nativeSetMaxRecordableTime(id, value);
}

export async function nativeTakePicture(id: number) {
  return ThetaBleClientReactNative.nativeTakePicture(id);
}

export async function nativeCameraControlCommandV2GetInfo(id: number): Promise<ThetaInfo> {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2GetInfo(id);
}

export async function nativeCameraControlCommandV2GetState(id: number): Promise<ThetaState> {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2GetState(id);
}

export async function nativeCameraControlCommandV2SetStateNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2SetStateNotify(id, enable);
}

export async function nativeCameraControlCommandV2GetState2(id: number): Promise<ThetaState2> {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2GetState2(id);
}

export async function nativeCameraControlCommandV2GetOptions(id: number, optionNames: OptionName[]): Promise<ThetaOptions> {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2GetOptions(id, optionNames);
}

export async function nativeCameraControlCommandV2GetOptionsByString(id: number, optionNames: string[]): Promise<Record<string, unknown>> {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2GetOptionsByString(id, optionNames);
}

export async function nativeCameraControlCommandV2SetOptions(id: number, options: ThetaOptions) {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2SetOptions(id, options);
}

export async function nativeCameraControlCommandV2ReleaseShutter(id: number) {
  return ThetaBleClientReactNative.nativeCameraControlCommandV2ReleaseShutter(id);
}

export async function nativeBluetoothControlCommandScanPeripheralDevice(id: number, timeout: number): Promise<PeripheralDevice[]> {
  return ThetaBleClientReactNative.nativeBluetoothControlCommandScanPeripheralDevice(id, timeout);
}

export async function nativeBluetoothControlCommandScanPeripheralDeviceStart(id: number, timeout: number) {
  return ThetaBleClientReactNative.nativeBluetoothControlCommandScanPeripheralDeviceStart(id, timeout);
}

export async function nativeBluetoothControlCommandScanPeripheralDeviceStop(id: number) {
  return ThetaBleClientReactNative.nativeBluetoothControlCommandScanPeripheralDeviceStop(id);
}

export async function nativeBluetoothControlCommandConnectPeripheralDevice(id: number, macAddress: string) {
  return ThetaBleClientReactNative.nativeBluetoothControlCommandConnectPeripheralDevice(id, macAddress);
}

export async function nativeBluetoothControlCommandDeletePeripheralDevice(id: number, macAddress: string) {
  return ThetaBleClientReactNative.nativeBluetoothControlCommandDeletePeripheralDevice(id, macAddress);
}

export async function nativeWlanControlCommandV2SetNetworkType(id: number, networkType: NetworkTypeEnum) {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2SetNetworkType(id, networkType);
}

export async function nativeWlanControlCommandV2SetNetworkTypeNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2SetNetworkTypeNotify(id, enable);
}

export async function nativeWlanControlCommandV2GetConnectedWifiInfo(id: number): Promise<ConnectedWifiInfo> {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2GetConnectedWifiInfo(id);
}

export async function nativeWlanControlCommandV2SetConnectedWifiInfoNotify(id: number, enable: boolean) {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2SetConnectedWifiInfoNotify(id, enable);
}

export async function nativeWlanControlCommandV2ScanSsidStart(id: number, timeout: number) {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2ScanSsidStart(id, timeout);
}

export async function nativeWlanControlCommandV2ScanSsidStop(id: number) {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2ScanSsidStop(id);
}

export interface SetAccessPointParams {
  ssid: string,
  ssidStealth?: boolean,
  security?: WifiSecurityModeEnum,
  password?: string,
  connectionPriority?: number,
  ipAddress?: string,
  subnetMask?: string,
  defaultGateway?: string,
  proxy?: Proxy,
}

export async function nativeWlanControlCommandV2SetAccessPointDynamically(
  id: number, params: SetAccessPointParams,
) {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2SetAccessPointDynamically(id, params);
}

export async function nativeWlanControlCommandV2SetAccessPointStatically(
  id: number, params: SetAccessPointParams,
) {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2SetAccessPointStatically(id, params);
}

export async function nativeWlanControlCommandV2GetWlanPasswordState(id: number): Promise<WlanPasswordStateEnum> {
  return ThetaBleClientReactNative.nativeWlanControlCommandV2GetWlanPasswordState(id);
}

export async function nativeReleaseDevice(id: number) {
  return ThetaBleClientReactNative.nativeReleaseDevice(id);
}
