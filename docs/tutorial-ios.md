# RICOH360 THETA BLE Client Tutorial for iOS

## Available models

* RICOH360 THETA A1
* RICOH THETA X
* RICOH THETA Z1

## Implementing the framework

Add the `THETABleClient` framework to the `Podfile`.
  
``` Podfile
pod 'THETABleClient', '1.3.2'
```

## Setting permissions

To set permissions to use Bluetooth,
Add `NSBluetoothAlwaysUsageDescription` to `plist`.

## Enabling Bluetooth (Theta X/Z1 only)

When Bluetooth is turned off on the Theta X/Z1, you can turn it on with the Theta operation, but you can also turn it on via the Web API.
Theta A1 has Bluetooth on all the time.

1. Set the option [\_bluetoothPower](https://docs-theta-api.ricoh360.com/web-api/options/bluetoothPower.html) to `ON` in the Web API. 

## THETA detection

Use `ThetaBle.scan()` to search for THETA to get `ThetaDevice`.
As an argument, pass the serial number for Theta A1, and the numeric part of the serial number for Theta X/Z1.
From then on, use `ThetaDevice` to perform each operation.

``` Swift
import THETABleClient

let device = try await ThetaBle.Companion.shared.scan(name: name)
if let device = device {
    // success scan THETA
} else {
    // handle error
}
```

If you are unsure of the Theta serial number you want to detect, detect the list of Theta candidates as follows:

```swift
let deviceList: [ThetaDevice] = try await ThetaBle.Companion.shared.scan()
for device in deviceList {
  // For Theta A1, device.name is the serial number
  // For Theta X/Z1, device.name is the numeric part of the serial number
}
```

You can also specify a timeout for `ThetaBle.scan()`. If the default value is okay, you can omit the designation.

```swift
let timeout = ThetaBle.Timeout(
    timeoutScan: 20000,
    timeoutPeripheral: 2000,
    timeoutConnect: 3000,
    timeoutTakePicture: 15000
)
let device = try await ThetaBle.Companion.shared.scan(name: name, timeout: timeout)
```

| Properties | Where it is used | Default Value (ms) |
|----------------------|---------------------|-----------------|
| `timeoutScan`        | At detection | 30,000  |
| `timeoutPeripheral`  | Obtaining device information when connecting to THETA | 1,000   |
| `timeoutConnect`     | When you actually connect to THETA | 5,000   |
| `timeoutTakePicture` | When shooting still images | 10,000  |

## Connect to THETA
Connect with `ThetaDevice.connect()` using the `ThetaDevice` obtained in `ThetaBle.scan()`.

When you're done using the BLE API, disconnect with `ThetaDevice.disconnect()`.

``` Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
...
do {
    try await device!.connect()
     // call BLE APIs
    try await device!.disconnect()
} catch {
    // handle error
}
```

## Calling the BLE API

To call the BLE API, call the method of the service object defined in `ThetaDevice`.
The service object can be retrieved after connecting with `ThetaDevice.connect()`.
If the connected model does not support the service, the service object will be `nil`.

| Service Name | Service Objects | Classes | Remarks |
|-----------|--------------------|--------|-----|
| [Camera control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#camera-control-command-v2-service)  | `cameraControlCommandV2` | `CameraControlCommandV2` | |
| [WLAN control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-service) | `wlanControlCommand`| `WlanControlCommand` ||
| [WLAN control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-v2-service) | `wlanControlCommandV2`| `WlanControlCommandV2` | |
| [Bluetooth control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#bluetooth-control-command) | `bluetoothControlCommand` | `BluetoothControlCommand` | Theta A1 Only |

For example, to get the Theta model name and serial number, use the service object as follows: 

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
if let info = try await service?.getInfo() {
  // use info?.model and info?.serialNumber
}
```

## Obtaining camera information

You can get the camera information (`ThetaInfo` object) using `ThetaDevice.cameraControlCommandV2.getInfo()`. The properties of `ThetaInfo` are as follows:

| Information | Properties | Type |
|------|-----------|----|
| Manufacturer | `manufacturer` | `String` |
| THETA models | `model` | `ThetaModel` |
| Serial number | `serialNumber` | `String` |
| WLAN MAC address | `wlanMacAddress` | `String?` |
| Bluetooth MAC addresses | `bluetoothMacAddress` | `String?` |
| Firmware version | `firmwareVersion` | `String` |
| Uptime (seconds) | `uptime` | `Int` |

## Obtaining optional values

You can get the value of the option defined in `OptionName` with `CameraControlCommandV2.getOptions()`. However, you cannot get the `Password`.

| Options | `OptionName` Properties | Type | Remarks |
| --------- | ----------------------- | -- | ---- |
| Access point information | `AccessInfo` | `AccessInfo?` | Theta A1, X only |
| Theta power state | `CameraPower` | `CameraPower?` | |
| Shooting modes | `CaptureMode` | `CaptureMode?` | |
| Default WLAN password in AP mode | `DefaultWifiPassword` | `String?` ||
| Configured network type | `NetworkType` | `NetworkType?` | |
| SSID in AP mode | `Ssid` | `String?` ||
| Username for CL mode digest authentication | `Username` | `String?` ||
| Radio antenna settings | `WlanAntennaConfig` | `WlanAntennaConfig?` | Theta A1, X only |
| AP mode radio frequency | `WlanFrequency` | `WlanFrequency?` ||

<br/>

Here is the sample code to get `OptionName.DefaultWifiPassword`.

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
let optionNames: [OptionName] = [.DefaultWifiPassword]
options = try await service?.getOptions(optionNames)
// use options?.defaultWifiPassword
```

## Setting optional values

You can set the values of the option defined in `ThetaOptions` with `CameraControlCommandV2.setOptions()`.
However, you cannot set `defaultWifiPassword`.

| Options | `ThetaOptions` Properties | Type | Remarks |
| --------- | ----------------------- | -- | ---- |
| Access point information | `accessInfo` | `AccessInfo?` | Theta A1, X only |
| Theta power state | `cameraPower` | `CameraPower?` | |
| Shooting modes | `captureMode` | `CaptureMode?` | |
| Network type | `networkType` | `NetworkType?` | |
| SSID in AP mode | `ssid` | `String?` ||
| Username for CL mode digest authentication | `username` | `String?` ||
| Password for CL mode digest authentication | `password` | `String?` ||
| Radio antenna settings | `wlanAntennaConfig` | `WlanAntennaConfig?` | Theta A1, X only |
| AP mode radio frequency | `wlanFrequency` | `WlanFrequency?` ||

<br/>

Sample code to set `ThetaOptions.captureMode` to video mode.

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
let options = ThetaOptions()
options.captureMode = .video
try await service?.setOptions(options)
```

## Shooting

Calling `CameraControlCommandV2.releaseShutter()` will perform the shooting process according to the value of the `CaptureMode` option and the state of Theta.

| Value of the `CaptureMode` option | Whether or not a video is being recorded | Shooting Processing |
| ------------------------- | ----------------| ------- |
| `image` | n/a | Still image shooting |
| `video` | Not filmed | Start video shooting |
| `video` | Filming | Finish video shooting |

<br/>

This is a sample code to shoot.

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
try await service?.releaseShutter()
```

## Getting Theta state

You can get the state of Theta (`ThetaState`, `ThetaState2`) with `CameraControlCommandV2.getState()` and `CameraControlCommandV2.getState2()`.

The properties of `ThateState` are as follows:

| Information | Properties | Type | Remarks |
|------|-----------|----|-----|
| Latest image URL | `latestFileUrl` | `String?` | The URL of the last image taken (non-DNG format). You can download it by connecting to WLAN.
| Video recording time (seconds) | `recordedTime` | `Int?` ||
| Video recording time (seconds) | `recordableTime` | `Int?` ||
| Continuous shooting status | `captureStatus` | `CaptureStatus?` ||
| Number of continuous shots | `capturedPictures` | `Int?` ||
| Shooting settings | `function` | `ShootingFunction?` ||
| Battery availability | `batteryInsert` | `Boolean?` ||
| Battery level | `batteryLevel` | `Float?` | 0 to 1 |
| State of charge | `batteryState` | `ChargingState?` ||
| Main board temperature | `boardTemp` | `Int?` ||
| Battery temperature | `batteryTemp` | `Int?` ||
| Error condition | `cameraError` | `List<CameraError>?`||

<br/>

The properties of `ThateState2` are as follows:

| Information | Properties | Type | Remarks |
|------|-----------|----|-----|
| Location of built-in GPS module | `internalGpsInfo` | `StateGpsInfo?` ||
| Location of external GPS devices | `externalGpsInfo` | `StateGpsInfo?` ||

<br/>

This is a sample code to get the latest image URL.

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
let state = try await service?.getState()
// use state?.latestFileUrl
```

## Theta state notifications

If you call `CameraControlCommandV2.setStateNotify()` with a callback function as an argument, it will be called when the state changes.
If you omit the argument, you will cancel the configured callback function.
If an error occurs, the callback function argument `error` returns a value.

```swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
...
do {
  try await device?.connect()
  let service = device?.cameraControlCommandV2
  try service?.setStateNotify { state, error in
      if error != nil {
          // error: Error
      } else {
          // state: ThetaState
      }
  }
} catch {
  // handle error
}
```

## Controlling the WLAN

You can use the `WlanControlCommandV2` service to control the wireless LAN.

| Features | Methods | Arguments | Returns |
| ---- | ------- | ---- |------ |
| Getting access point connection state | `getConnectedWifiInfo()` | - |`ConnectedWifiInfo` |
| Configuring an access point (DHCP) | `setAccessPointDynamically()` | SSID, etc. | - |
| Configuring an access point (Static) | `setAccessPointStatically()` | SSID, IP Address, & More | - |
| Network type settings | `setNetworkType()` | `NetworkType` | - |
