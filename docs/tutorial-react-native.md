# RICOH360 THETA BLE Client Tutorial for React Native

## Available models

* RICOH360 THETA A1
* RICOH THETA X
* RICOH THETA Z1

## Deploy the package
Add 'theta-ble-client' to 'package.json'.

```shell
$ npx react-native@latest init YourProject
$ cd YourProject
$ yarn add theta-ble-client
```

## Setting permissions
You need to set permissions to use Bluetooth.

### Android
An application makes a request for Bluetooth permissions.

```Typescript
import {PermissionsAndroid, Platform} from 'react-native';

const requestPermission = async () => {
  if (Platform.OS !== 'android') {
    return;
  }
  try {
    if (Platform.Version > 30) {
      await PermissionsAndroid.requestMultiple([
        PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT,
        PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN,
      ]);
    } else {
      await PermissionsAndroid.requestMultiple([
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      ]);
    }
  } catch (err) {
    console.warn(err);
  }
};
```

### iOS
Add `NSBluetoothAlwaysUsageDescription` to `plist`.

## Enabling Bluetooth (Theta X/Z1 only)

When Bluetooth is turned off on the Theta X/Z1, you can turn it on with the Theta operation, but you can also turn it on via the Web API. 
Theta A1 has Bluetooth on all the time.

1. In the Web API, set the option [\_bluetoothPower](https://docs-theta-api.ricoh360.com/web-api/options/bluetoothPower.html) to `ON`.

## THETA detection

If you know the serial number of Theta A1, you can use the serial number as an argument and use [`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-ble/theta-ble.ts). 

``` Typescript
import {
  scan,
  ThetaDevice,
} from 'theta-ble-client-react-native';

const device = await scan({
  name: 'AA12345678',
});
if (device) {
  // success scan THETA
} else {
  // handle error
}
```

If you know the serial number of Theta X/Z1, you can use the numeric part of the serial number as an argument to [`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-ble/theta-ble.ts). 

``` Typescript
import {
  scan,
  ThetaDevice,
} from 'theta-ble-client-react-native';

const device = await scan({
  name: '12345678',
});
if (device) {
  // success scan THETA
} else {
  // handle error
}
```

If you are unsure of the Theta serial number you want to detect, detect the list of Theta candidates as follows:

``` Typescript
import {
  scan,
  ThetaDevice,
} from 'theta-ble-client-react-native';

const deviceList = await scan();
deviceList.forEach(device => {
  // If Theta A1, it.name is the serial number
  // If Theta X/Z1, it.name is the numeric part of the serial number
})
```

You can also specify a timeout for [`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-ble/theta-ble.ts). If the default value is okay, you can omit the designation. 

``` Typescript
const device = await scan({
  name: '12345678',
  timeout: {
    timeoutScan: 20_000,
    timeoutPeripheral: 2_000,
    timeoutConnect: 3_000,
    timeoutTakePicture: 15_000,
  },
});
```

| Properties | Where it is used | Default Value (ms) |
|----------------------|---------------------|-----------------|
| `timeoutScan`        | At Detection | 30,000  |
| `timeoutPeripheral`  | Obtaining Device Information When Connecting to THETA | 1,000   |
| `timeoutConnect`     | When you actually connect to THETA | 5,000   |
| `timeoutTakePicture` | When shooting still images | 10,000  |

## Connect to THETA

You can connect to Theta with [`ThetaDevice.connect()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-device/theta-device.ts). 

When you have done using the BLE API, disconnect with `ThetaDevice.disconnect()`.

``` Typescript
const device = await scan({
  name: 'AA12345678', // for Theta A1
  // name : '12345678', // for Theta X/Z1
});
...
try {
  await device.connect();
  // call BLE APIs
  await device.disconnect();
} catch (_) {
  // handle error
}
```

## Calling the BLE API

Pass [`BleServiceEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/ble-service.ts) as an argument to `ThetaDevice.getService()`, to get the service object and call the API. 
The service object becomes available after connecting with `ThetaDevice.connect()`.
If the device does not support the specified service, the service object is `undefined`.

| Service Name | [`BleServiceEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/ble-service.ts) | Classes | Remarks |
|-----------|--------------------|--------|-----|
| [Camera control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#camera-control-command-v2-service)  | `CAMERA_CONTROL_COMMAND_V2` | [`CameraControlCommandV2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/camera-control-command-v2.ts) | |
| [WLAN control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-service) | `WLAN_CONTROL_COMMAND`| [`WlanControlCommand`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/wlan-control-command.ts) ||
| [WLAN control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-v2-service) | `WLAN_CONTROL_COMMAND_V2`| [`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/wlan-control-command-v2.ts) | |
| [Bluetooth control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#bluetooth-control-command) | `BLUETOOTH_CONTROL_COMMAND` | [`BluetoothControlCommand`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/bluetooth-control-command.ts) | Theta A1 Only |

For example, to get the Theta model name and serial number, use the service object as follows:

```Typescript
const device = await scan({
  name: '12345678', // or 'AA12345678' for Theta A1
});
await device?.connect();

const service = (await device?.getService(
  BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
)) as CameraControlCommandV2 | undefined;

const info = await service?.getInfo();
console.log(`${info?.model} ${info?.serialNumber}`)
```

## Obtaining Camera Information

You can get camera information  ([`ThetaInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-info.ts) object) with `CameraControlCommandV2.getInfo()`.
The properties of `ThetaInfo` are as follows: 

| Information | Properties | Type |
|------|-----------|----|
| Manufacturer | `manufacturer` | `string` |
| THETA Models | `model` | `ThetaModel` |
| Serial Number | `serialNumber` | `string` |
| WLAN MAC Address | `wlanMacAddress?` | `string` |
| Bluetooth MAC Addresses | `bluetoothMacAddress?` | `string` |
| Firmware Version | `firmwareVersion` | `string` |
| Uptime (seconds) | `uptime` | `number` |

## Obtaining optional values

You can get values of options defined in [`OptionName`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/option-name.ts) with `CameraControlCommandV2.getOptions()`.
However, you cannot get `Password`. 

| Options | `OptionName` Properties | Type | Remarks |
| --------- | ----------------------- | -- | ---- |
| Access Point Information | `AccessInfo` | [`AccessInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/access-info.ts) | Theta A1, X only |
| Theta Power Status | `CameraPower` | [`CameraPowerEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/camera-power.ts) | |
| Shooting Mode | `CaptureMode` | [`CaptureModeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/capture-mode.ts) | |
| Default WLAN Password in AP Mode | `DefaultWifiPassword` | `string` ||
| Network type set | `NetworkType` | [`NetworkTypeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/network-type.ts) | |
| SSID in AP Mode | `Ssid` | `string` ||
| Username for CL Mode Digest Authentication | `Username` | `string` ||
| Radio Antenna Configuration | `WlanAntennaConfig` | [`WlanAntennaConfigEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-antenna-config.ts) | Theta A1, X only |
| AP Mode Radio Frequency | `WlanFrequency` | [`WlanFrequencyEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-frequency.ts) ||

<br/>

Here is the sample code to get `OptionName.DefaultWifiPassword`.

```Typescript
const device = await scan({
  name: '12345678', // or 'AA12345678' for Theta A1
});
await device?.connect();

const service = (await device?.getService(
  BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
)) as CameraControlCommandV2 | undefined;

const optionNames = [ OptionName.DefaultWifiPassword, ]
const options = await service?.getOptions(optionNames);
console.log(options.defaultWifiPassword)
```

## Setting optional values

You can set velues of options defined in [`ThetaOptions`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-options.ts) with `CameraControlCommandV2.setOptions()`. 
However, you cannot set `defaultWifiPassword`. 

| Options | [`ThetaOptions`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-options.ts) Properties | Type | Remarks |
| --------- | ----------------------- | -- | ---- |
| Access Point Information | `accessInfo?` | [`AccessInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/access-info.ts) | Theta A1, X only |
| Theta Power Status | `cameraPower?` | [`CameraPowerEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/camera-power.ts) | |
| Shooting Mode | `captureMode?` | [`CaptureModeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/capture-mode.ts) | |
| Default WLAN Password in AP Mode | `defaultWifiPassword?` | `string` ||
| Network type | `networkType?` | [`NetworkTypeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/network-type.ts) | |
| SSID in AP Mode | `ssid?` | `String` ||
| Username for CL Mode Digest Authentication | `username?` | `string` ||
| Password for CL Mode Digest Authentication | `password?` | `string` ||
| Radio Antenna Configuration | `wlanAntennaConfig?` | [`WlanAntennaConfigEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-antenna-config.ts) | Theta A1, X only |
| AP Mode Radio Frequency | `wlanFrequency?` | [`WlanFrequencyEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-frequency.ts) ||

<br/>

Sample code to set `ThetaOptions.captureMode` to video mode.

```Typescript
const device = await scan({
  name: '12345678', // or 'AA12345678' for Theta A1
});
await device?.connect();

const service = (await device?.getService(
  BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
)) as CameraControlCommandV2 | undefined;

const options = {
  captureMode: CaptureModeEnum.IMAGE,
} as ThetaOptions;
await service?.setOptions(options);
```

## Shooting

Calling `CameraControlCommandV2.releaseShutter()` will perform the shooting process according to the value of the `captureMode` option and the state of Theta.

| Value of the `captureMode` option | Whether or not a video is being recorded | Shooting Processing |
| ------------------------- | ----------------| ------- |
| `CaptureModeEnum.IMAGE` | n/a | Still image shooting |
| `CaptureModeEnum.VIDEO` | Not recording | Start video shooting |
| `CaptureModeEnum.VIDEO` | Recording in progress | Finish video shooting |

<br/>

This is a sample code to shoot.

```Typescript
const device = await scan({
  name: '12345678', // or 'AA12345678' for Theta A1
});
await device?.connect();

const service = (await device?.getService(
  BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
)) as CameraControlCommandV2 | undefined;

await service?.releaseShutter();
```

## Getting Theta state

You can get the status of Theta ([`ThetaState`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state.ts)、[`ThetaState2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state2.ts)) with `CameraControlCommandV2.getState()` and `CameraControlCommandV2.getState2()`.

The properties of [`ThateState`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state.ts) are as follows: 

| Information | Properties | Type | Remarks |
|------|-----------|----|-----|
| Latest image URL | `latestFileUrl?` | `string` | The URL of the last image taken (non-DNG format). You can download it if you connect to WLAN.
| Video recording time (seconds) | `recordedTime?` | `number` ||
| Video recording time (seconds) | `recordableTime?` | `number` ||
| Continuous shooting status | `captureStatus?` | [`CaptureStatusEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/capture-status.ts) ||
| Number of continuous shots | `capturedPictures?` | `number` ||
| Shooting settings | `shootingFunction?` | [`ShootingFunctionEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/shooting-function.ts) ||
| Battery availability | `batteryInsert?` | `boolean` ||
| Battery level | `batteryLevel?` | `number` | 0 to 1 |
| Charging status | `batteryState?` | [`ChargingStateEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/charging-state.ts) ||
| Main board temperature | `boardTemp?` | `number` ||
| Battery temperature | `batteryTemp?` | `number` ||
| Error state | `cameraError?` | [`CameraErrorEnum[]`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/camera-error.ts)||

<br/>

The properties of [`ThateState2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state2.ts) are as follows: 

| Information | Properties | Type | Remarks |
|------|-----------|----|-----|
| Location of built-in GPS module | `internalGpsInfo?` | [`{gpsInfo: GpsInfo}`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/gps-info.ts) ||
| Location of an external GPS device | `externalGpsInfo?` | [`{gpsInfo: GpsInfo}`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/gps-info.ts) ||

<br/>

This is a sample code to get the latest image URL.

```Typescript
const device = await scan({
  name: '12345678', // or 'AA12345678' for Theta A1
});
await device?.connect();

const service = (await device?.getService(
  BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
)) as CameraControlCommandV2 | undefined;

const state = await service?.getState();
console.log(state?.latestFileUrl)
```

## Theta status notifications

If you call `CameraControlCommandV2.setStateNotify()` with a callback function as an argument, it will be called when the state changes.
If you omit the argument, you will cancel the configured callback function.
If an error occurs, the callback function argument `error` returns a value.

```Typescript
const device = await scan({
  name: '12345678', // or 'AA12345678' for Theta A1
});
await device?.connect();

const service = (await device?.getService(
  BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
)) as CameraControlCommandV2 | undefined;

service?.setStateNotify((value?: ThetaState, error?: NotifyError) => {
    if (value !== undefined) {
      // value's type is ThetaState
    } else if (error !== undefined) {
      // error's type is NotifyError
    } else {
      // something is wrong
    }
});
```

## Controlling the WLAN

You can use the [`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/wlan-control-command-v2.ts) service to control the wireless LAN. 

| Features | Methods | Arguments | Returns |
| ---- | ------- | ---- |------ |
Get access point connection status | `getConnectedWifiInfo()` | - | [`ConnectedWifiInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/connected-wifi-info.ts) |
| Configuring access points (DHCP) | `setAccessPointDynamically()` | SSID, etc. | - |
| Configuring access points (static) | `setAccessPointStatically()` | SSID, IP Address, and more | - |
| Set network type | `setNetworkType()` | [`NetworkTypeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/network-type.ts) | - |
