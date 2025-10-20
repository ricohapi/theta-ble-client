# RICOH360 THETA BLE Client Tutorial for Android

## Available models

* RICOH360 THETA A1
* RICOH THETA X
* RICOH THETA Z1

## Add build dependencies
Add following descriptions to the `dependencies` of your module's `build.gradle`.

```
implementation "com.ricoh360.thetableclient:theta-ble-client-android:1.0.0"
```

## Setting permissions
The application must request Bluetooth permissions. 

``` kotlin
  if(Build.VERSION.SDK_INT > 30) {
      requestPermissions(
          arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
          REQUEST_MULTI_PERMISSIONS)
  } else {
      requestPermissions(
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN),
          REQUEST_MULTI_PERMISSIONS)
  }
```

## Enabling Bluetooth (Theta X/Z1 only)

When Bluetooth is turned off on the Theta X/Z1, you can turn it on with the Theta operation, but you can also turn it on via the Web API. The Theta A1 has Bluetooth on all the time.

1. In the Web API, set the option [\_bluetoothPower](https://docs-theta-api.ricoh360.com/web-api/options/bluetoothPower.html) to `ON`.

## THETA Detection

If you know the serial number of Theta A1, you can use the serial number as an argument and use [`ThetaBle.scan()`]( https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/ThetaBle.kt).

``` Kotlin
val device: ThetaDevice? = ThetaBle.scan("AA01234567")
if (device != null) {
    // Theta has been found
} else {
    // Theta has not been found
}
```

If you know the serial number of Theta X/Z1, you can use the numeric part of the serial number as an argument to [`ThetaBle.scan()`]( https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/ThetaBle.kt).

``` Kotlin
val device: ThetaDevice? = ThetaBle.scan("01234567")
if (device != null) {
    // Theta has been found
} else {
    // Theta has not been found
}
```

If you are unsure of the Theta serial number you want to detect, detect the list of Theta candidates as follows:

``` Kotlin
val deviceList: List<ThetaDevice> = ThetaBle.scan()
deviceList.forEach {
    // If Theta A1, it.name is the serial number
    // If Theta X/Z1, it.name is the numeric part of the serial number
}
```

 You can also specify a timeout for [`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/ThetaBle.kt). If the default value is okay, you can omit the designation.

``` Kotlin
val timeout = ThetaBle.Timeout(
    timeoutScan = 20_000,
    timeoutPeripheral = 2_000,
    timeoutConnect = 3_000,
    timeoutTakePicture = 15_000,
)

val device = ThetaBle.scan(deviceName, timeout)
or
val deviceList = ThetaBle.scan(timeout)
```

| Properties | Where it is used | Default Value (ms) |
|----------------------|---------------------|-----------------|
| `timeoutScan`        | At Detection | 30,000  |
| `timeoutPeripheral`  | Obtaining Device Information When Connecting to THETA | 1,000   |
| `timeoutConnect`     | When you actually connect to THETA | 5,000   |
| `timeoutTakePicture` | When shooting still images | 10,000  |

<br/>

## Calling the BLE API

To call the BLE API, call the method of the service object defined in `ThetaBle.ThetaDevice`.
The service object can be retrieved after connecting with `ThetaDevice.connect()`. 
If the connected model does not support the service, the service object will be `null`.

| Service Name | Service Objects | Classes | Remarks |
|-----------|--------------------|--------|-----|
| [Camera control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#camera-control-command-v2-service)  | `cameraControlCommandV2` | [`CameraControlCommandV2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/CameraControlCommandV2.kt) | |
| [WLAN control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-service) | `wlanControlCommand`| [`WlanControlCommand`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/WlanControlCommand.kt) ||
| [WLAN control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-v2-service) | `wlanControlCommandV2`| [`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/WlanControlCommandV2.kt) | |
| [Bluetooth control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#bluetooth-control-command) | `bluetoothControlCommand` | [`BluetoothControlCommand`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/BluetoothControlCommand.kt) | Theta A1 only |

For example, to get the Theta model name and serial number, use the service object as follows: 

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    val info = service?. getInfo()
    println("${info?. model} ${info?. serialNumber}")
}
```

## Obtaining Camera Information

You can get camera information ([`ThetaInfo`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaInfo.kt) object) using `ThetaDevice.cameraControlCommandV2.getInfo()`. The properties of `ThetaInfo` are as follows:

| Information | Properties | Type |
|------|-----------|----|
| Manufacturer | `manufacturer` | `String` |
| Theta model | `model` | `ThetaModel` |
| Serial number | `serialNumber` | `String` |
| WLAN MAC address | `wlanMacAddress` | `String?` |
| Bluetooth MAC address | `bluetoothMacAddress` | `String?` |
| Firmware version | `firmwareVersion` | `String` |
| Uptime (seconds) | `uptime` | `Int` |

## Obtaining Optional Values

You can get the value of the option defined in [`OptionName`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/OptionName.kt) with `CameraControlCommandV2.getOptions()`. However, you cannot get the `Password`.

| Options | `OptionName` Properties | Type | Remarks |
| --------- | ----------------------- | -- | ---- |
| Access point information | `AccessInfo` | [`AccessInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/AccessInfo.kt) | Theta A1, Xのみ |
| Theta power status | `CameraPower` | [`CameraPower?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CameraPower.kt) | |
| Shooting mode | `CaptureMode` | [`CaptureMode?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CaptureMode.kt) | |
| Default WLAN password for AP mode | `DefaultWifiPassword` | `String?` ||
| Configured network type | `NetworkType` | [`NetworkType?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/NetworkType.kt) | |
| AP mode SSID | `Ssid` | `String?` ||
| Username for CL mode digest authentication | `Username` | `String?` ||
| Radio antenna settings | `WlanAntennaConfig` | [`WlanAntennaConfig?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanAntennaConfig.kt ) | Theta A1, X only |
| AP mode radio frequency | `WlanFrequency` | [`WlanFrequency?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanFrequency.kt) ||

<br/>

Here is the sample code to get `OptionName.DefaultWifiPassword`.

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    val optionNames = listOf(
        OptionName.DefaultWifiPassword,
    )
    println(service?. getOptions(optionNames).defaultWifiPassword)
}
```

## Setting Optional Values

You can set the value of the options defined in [`ThetaOptions`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaOptions.kt) with `CameraControlCommandV2.setOptions()`.

| Options | `OptionName` Properties | Type | Remarks |
| --------- | ----------------------- | -- | ---- |
| Access point information | `AccessInfo` | [`AccessInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/AccessInfo.kt) | Theta A1, Xのみ |
| Theta power status | `CameraPower` | [`CameraPower?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CameraPower.kt) | |
| Shooting mode | `CaptureMode` | [`CaptureMode?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CaptureMode.kt) | |
| Default WLAN password for AP mode | `DefaultWifiPassword` | `String?` ||
| Configured network type | `NetworkType` | [`NetworkType?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/NetworkType.kt) | |
| AP mode SSID | `Ssid` | `String?` ||
| Username for CL mode digest authentication | `Username` | `String?` ||
| Password for CL mode digest authentication | `password` | `String?` ||
| Radio antenna settings | `WlanAntennaConfig` | [`WlanAntennaConfig?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanAntennaConfig.kt ) | Theta A1, X only |
| AP mode radio frequency | `WlanFrequency` | [`WlanFrequency?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanFrequency.kt) ||

<br/>

Sample code to set `ThetaOptions.captureMode` to video mode.

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val options = ThetaOptions(captureMode= CaptureMode.VIDEO)
    val service = device.cameraControlCommandV2
    service?. setOptions(options)
}
```

## Shooting

Calling `CameraControlCommandV2.releaseShutter()` will perform the shooting process according to the value of the `CaptureMode` option and the state of Theta.

| Value of the `CaptureMode` option | Whether or not a video is being recorded | Shooting Processing |
| ------------------------- | ----------------| ------- |
| `CaptureMode.IMAGE` | n/a | Still image shooting |
| `CaptureMode.VIDEO` | Not recording | Start video shooting |
| `CaptureMode.VIDEO` | Recording in progress | Finish video shooting |

<br/>

This is a sample code to shoot.

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    service?. releaseShutter()
}
```

## Getting Theta State

You can get the status of Theta ([`ThetaState`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState.kt)、[`ThetaState2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState2.kt)) with `CameraControlCommandV2.getState()` and `CameraControlCommandV2.getState2()`.

The properties of [`ThateState`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState.kt) are as follows.

| Information | Properties | Type | Remarks |
|------|-----------|----|-----|
| Latest image URL | `latestFileUrl` | `String?` | The URL of the last image taken (non-DNG format). You can download it if you connect Theta to WiFi.
| Video recording time (seconds) | `recordedTime` | `Int?` ||
| Video recording time (seconds) | `recordableTime` | `Int?` ||
| Continuous shooting status | `captureStatus` | [`CaptureStatus?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CaptureStatus.kt) ||
| Number of continuous shots | `capturedPictures` | `Int?` ||
| Shooting settings | `function` | [`ShootingFunction?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/ShootingFunction.kt) ||
| Battery availability | `batteryInsert` | `Boolean?` ||
| Battery level | `batteryLevel` | `Float?` | 0 to 1 |
| Charging status | `batteryState` | [`ChargingState?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/ChargingState.kt) ||
| Main board temperature | `boardTemp` | `Int?` ||
| Battery temperature | `batteryTemp` | `Int?` ||
| Error condition | `cameraError` | [`List<CameraError>?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CameraError.kt)||

<br/>

The properties of [`ThateState2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState2.kt) are as follows.

| Information | Properties | Type | Remarks |
|------|-----------|----|-----|
| Location of built-in GPS module | `internalGpsInfo` | [`StateGpsInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/StateGpsInfo.kt) ||
| Location of an external GPS device | `externalGpsInfo` | [`StateGpsInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/StateGpsInfo.kt) ||

<br/>

This is a sample code to get the latest image URL.

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    println(service?. getState().latestFileUrl)
}
```

## Theta Status Notifications

If you call `CameraControlCommandV2.setStateNotify()` with a callback function as an argument, it will be called when the state changes.
If you omit the argument, you will cancel the configured callback function.
If an error occurs, the callback function argument `error` returns a value.

```Kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    service?. setStateNotify { state, error ->
        error?. run {
          // this: Throwable
        } ?: run {
          // this: ThetaState
        }
    }
}
```

## Controlling the Wifi

You can use the [`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/WlanControlCommandV2.kt) service to control the wireless LAN.

| Features | Methods | Arguments | Returns |
| ---- | ------- | ---- |------ |
| Getting access point connection status | `getConnectedWifiInfo()` | - | [`ConnectedWifiInfo`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ConnectedWifiInfo.kt) |
| Configuring an access point (DHCP) | `setAccessPointDynamically()` | SSID, etc. | - |
| Configuring an access point (static) | `setAccessPointStatically()` | SSID, IP address, and more | - |
| Network type settings | `setNetworkType()` | [`NetworkType`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/NetworkType.kt) | - |
