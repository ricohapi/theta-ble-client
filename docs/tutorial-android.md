# RICOH360 THETA BLE Client Tutorial for Android

## Available models

* RICOH THETA Z1
* RICOH THETA X

## Add build dependencies
Add following descriptions to the `dependencies` of your module's `build.gradle`.

```
implementation "com.ricoh360.thetableclient:theta-ble-client-android:1.0.0"
```

## Setting of permissions
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

## Bluetooth authentication(RICOH THETA V/Z1)
THETA authenticates the application via the Web API and Bluetooth API. THETA does not use pairing.

Register the UUID to THETA using the Web API command [camera.\_setBluetoothDevice](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/commands/camera._set_bluetooth_device.md) and turn on the Bluetooth module using the option [\_bluetoothPower](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/options/_bluetooth_power.md).
Since the name of THETA can be retrieved when the UUID is registered, this name is used in the library. 
The registered UUID is used for the connection.

For RICOH THETA X, operation can be performed without authentication.

Reference:
https://github.com/ricohapi/theta-api-specs/blob/main/theta-bluetooth-api/getting_started.md#1-bluetooth-authentication


## Search for THETA
Search THETA by the name obtained when registering with the Web API command [camera.\_setBluetoothDevice](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/commands/camera._set_bluetooth_device.md). 

Use `ThetaBle.scan()` to search for THETA and acquire `ThetaDevice`.
Thereafter, each operation is performed using `ThetaDevice`. 

``` kotlin
  val device = ThetaBle.scan(devName)
  if (device != null) {
      // success scan THETA
  } else {
      // handle error
  }
```
### Timeout settings
A timeout can be specified for `ThetaBle.scan()`.
If omitted, will use the default value.

``` kotlin
  val timeout = ThetaBle.Timeout(
      timeoutScan = 30_000,
      timeoutPeripheral = 1_000,
      timeoutConnect = 5_000,
      timeoutTakePicture = 10_000,
  )
  val device = ThetaBle.scan(devName, timeout)
```

| Attributes           | Location of use                                       | default(ms) |
|----------------------|-------------------------------------------------------|-------------|
| `timeoutScan`        | When searching for THETA                              | 30,000      |
| `timeoutPeripheral`  | Obtaining device information when connecting to THETA | 1,000       |
| `timeoutConnect`     | When connecting to THETA actually                     | 5,000       |
| `timeoutTakePicture` | When taking picture                                   | 10,000      |

## Connect to THETA
Connect with `ThetaDevice.connect()` using `ThetaDevice` acquired with `ThetaBle.scan()`. 
If authentication is required, specify the UUID registered for authentication.(RICOH THETA V/Z1)

``` kotlin
  val device = ThetaBle.scan(devName)
  ...
  try {
      device!!.connect(uuid)
      // success
  } catch (e: ThetaBle.ThetaBleException) {
      // handle error
  }
```

## Disconnect from THETA
Disconnect with `ThetaDevice.disconnect()` using `ThetaDevice` acquired with `ThetaBle.scan()`. 

``` kotlin
  val device = ThetaBle.scan(devName)
  ...
  try {
      device!!.disconnect()
      // success
  } catch (e: ThetaBle.ThetaBleException) {
      // handle error
  }
```

## Call the API
Calling the API is done by retrieving the service object prepared in `ThetaDevice`.
The service object can be obtained after connecting with `ThetaDevice.connect()`.
If the service is not supported, it will be `null`.

| Service Name              | Service Object           | Class                    |
|---------------------------|--------------------------|--------------------------|
| Camera Information        | `cameraInformation`      | `CameraInformation`      |
| Camera Status Command     | `cameraStatusCommand`    | `CameraStatusCommand`    |
| Camera Control Commands   | `cameraControlCommands`  | `CameraControlCommands`  |
| Shooting Control Command  | `shootingControlCommand` | `ShootingControlCommand` |
| Camera Control Command v2 | `cameraControlCommandV2` | `CameraControlCommandV2` |

```kotlin
  val device = ThetaBle.scan(devName)
  device?.connect()
  val service = device?.cameraInformation
  val firmware = service?.getFirmwareRevision()
```

## Obtain THETA information
The THETA information is acquired by the following functions prepared in `CameraInformation`.

| Information           | Function                 | Type     |
|-----------------------|--------------------------|----------|
| Firmware revision     | `getFirmwareRevision`    | `String` |
| Manufacturer name     | `getManufacturerName`    | `String` |
| Model                 | `getModelNumber`         | `String` |
| Serial number         | `getSerialNumber`        | `String` |
| WLAN MAC address      | `getWlanMacAddress`      | `String` |
| Bluetooth MAC address | `getBluetoothMacAddress` | `String` |


```Kotlin
  val device = ThetaBle.scan(devName)
  try {
    device?.connect()
    val service = device?.cameraInformation

    val firmware = service?.getFirmwareRevision()
    val maker = service?.getManufacturerName()
    val model = service?.getModelNumber()
    val serial = service?.getSerialNumber()
    val wlan = service?.getWlanMacAddress()
    val bluetooth = service?.getBluetoothMacAddress()
    // success
  } catch (e: ThetaBle.ThetaBleException) {
    // handle error
  }
```

## Shoot still images
After checking the capture mode, call `ShootingControlCommand.takePicture()` to shoot the still image. 

Capture mode `CaptureMode` is acquired with `ShootingControlCommand.getCaptureMode()` and set with `ShootingControlCommand.setCaptureMode()`. For still images, set to `CaptureMode.IMAGE`. 
Also, after changing with `ShootingControlCommand.setCaptureMode()`, shooting fails unless waiting a little. 

* Capture mode `CaptureMode`
  | Value   | Description               |
  |---------|---------------------------|
  | `IMAGE` | Still image shooting mode |
  | `VIDEO` | Movie shooting mode       |
  | `LIVE`  | Live streaming mode       |

When shooting is completed, the function passed to `ShootingControlCommand.takePicture()` is called. If an error occurs, error information is stored in the argument. 

``` Kotlin
  val device = ThetaBle.scan(devName)
  try {
    device?.connect()
    val service = device?.shootingControlCommand

    val captureMode = service?.getCaptureMode()
    if (captureMode != CaptureMode.IMAGE) {
      service?.setCaptureMode(CaptureMode.IMAGE)
      delay(1000)  // Wait a little or you'll fail
    }

    service?.takePicture {
      if (it == null) {
        // success. Take a picture.
      } else {
        // handle error
      }
    }
  } catch (e: ThetaBle.ThetaBleException) {
    // handle error
  }
```

## Camera status
The camera status can be got & set & notified by the following functions prepared in `CameraStatusCommand`.

| Type                         | Get                | Set                | Notify                             |
|------------------------------|--------------------|--------------------|------------------------------------|
| Battery level                | `getBatteryLevel`  | -                  | `setBatteryLevelNotify`            |
| charging state               | `getBatteryStatus` | -                  | `setBatteryStatusNotify`           |
| start-up status              | `getCameraPower`   | `setCameraPower`   | `setCameraPowerNotify`             |
| Error description in detail. | -                  | -                  | `setCommandErrorDescriptionNotify` |
| Plugin power status          | `getPluginControl` | `setPluginControl` | `setPluginControlNotify`           |

### Notification function
The Notification uses `setXxxxxxNotify()`.
If a callback function is passed as an argument, that function will be called when the state is changed. If the argument is omitted, the set callback function will be canceled.
If an error occurs, the `error` will be passed as an argument of the callback function.

```Kotlin
  val service = device?.cameraStatusCommand
  service?.setBatteryStatusNotify { value, error ->
    error?.run {
      // handle error
    } ?: run {
      // Notify value
    }
  }
```

## Camera Control Command v2
To use Camera Control Command v2 functions, use `CameraControlCommandV2`.
If not supported, `ThetaDevice.cameraControlCommandV2` becomes `null`.

``` Kotlin
  val thetaInfo = device.cameraControlCommandV2?.getInfo()
  val model = thetaInfo?.model
```
