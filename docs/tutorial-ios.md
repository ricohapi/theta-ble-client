# RICOH360 THETA BLE Client Tutorial for iOS

## Available models

* RICOH THETA Z1
* RICOH THETA X

## Implementation of a framework
Add the `THETABleClient` framework to the `Podfile`. 
  
``` podfile
pod 'THETABleClient', '1.0.0'
```

## Setting of authorization
To request to use Bluetooth, add `NSBluetoothAlwaysUsageDescription` to `info.plist`. 

# Bluetooth authentication(RICOH THETA V/Z1)
THETA authenticates via the Web API and Bluetooth API. THETA does not use pairing.

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


``` swift
import THETABleClient

  let device = try await ThetaBle.Companion.shared.scan(name: name)
  if let device = device {
      // success scan THETA
  } else {
      // handle error
  }
```

### Timeout settings
A timeout can be specified for `ThetaBle.scan()`.
If omitted, will use the default value.

``` swift
  let timeout = ThetaBle.Timeout(
      timeoutScan: 30000,
      timeoutPeripheral: 1000,
      timeoutConnect: 5000,
      timeoutTakePicture: 10000
  )
  let device = try await ThetaBle.Companion.shared.scan(name: name, timeout: timeout)
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


``` swift
  let device = try await ThetaBle.Companion.shared.scan(name: name)
  ...
  do {
      try await device!.connect(uuid: uuid)
      // success
  } catch {
      // handle error
  }
```

## Disconnect from THETA
Disconnect with `ThetaDevice.disconnect()` using `ThetaDevice` acquired with `ThetaBle.scan()`.

``` Swift
  let device = try await ThetaBle.Companion.shared.scan(name: name)
  ...
  do {
      try await device!.disconnect()
      // success
  } catch {
      // handle error
  }
```

## Call the API
Calling the API is done by retrieving the service object prepared in `ThetaDevice`.
The service object can be obtained after connecting with `ThetaDevice.connect()`.
If the service is not supported, it will be `nil`.

| Service Name              | Service Object           | Class                    |
|---------------------------|--------------------------|--------------------------|
| Camera Information        | `cameraInformation`      | `CameraInformation`      |
| Camera Status Command     | `cameraStatusCommand`    | `CameraStatusCommand`    |
| Camera Control Commands   | `cameraControlCommands`  | `CameraControlCommands`  |
| Shooting Control Command  | `shootingControlCommand` | `ShootingControlCommand` |
| Camera Control Command v2 | `cameraControlCommandV2` | `CameraControlCommandV2` |

```Swift
  let device = try await ThetaBle.Companion.shared.scan(name: name)
  try await device?.connect()
  let service = device?.cameraInformation
  let firmware = try await service?.getFirmwareRevision()
```

## Obtain THETA information
The THETA information is acquired by the following functions prepared in `CameraInformation`.

| Information           | Function                 | Type   |
|-----------------------|--------------------------|--------|
| Firmware vision       | `getFirmwareRevision`    | String |
| Manufacturer name     | `getManufacturerName`    | String |
| Model                 | `getModelNumber`         | String |
| Serial number         | `getSerialNumber`        | String |
| WLAN MAC address      | `getWlanMacAddress`      | String |
| Bluetooth MAC address | `getBluetoothMacAddress` | String |


```Swift
  let device = try await ThetaBle.Companion.shared.scan(name: name)
  do {
    try await device?.connect()
    let service = device?.cameraInformation

    let firmware = try await service?.getFirmwareRevision()
    let maker = try await service?.getManufacturerName()
    let model = try await service?.getModelNumber()
    let serial = try await service?.getSerialNumber()
    let wlan = try await service?.getWlanMacAddress()
    let bluetooth = try service?.getBluetoothMacAddress()
    // success
  } catch {
    // handle error
  }
```

## Shoot still images
After checking the capture mode, call `ShootingControlCommand.takePicture()` to shoot the still image. 

Capture mode `CaptureMode` is acquired with `ShootingControlCommand.getCaptureMode()` and set with `ShootingControlCommand.setCaptureMode()`. For still images, set to `CaptureMode.image`. 
Also, after changing with `ShootingControlCommand.setCaptureMode()`, shooting fails unless waiting a little. 

* Capture mode `CaptureMode`
  | Value   | Description               |
  |---------|---------------------------|
  | `image` | Still image shooting mode |
  | `video` | Movie shooting mode       |
  | `live`  | Live streaming mode       |

Define a class implements `KotlinSuspendFunction1` for callback, then call `ShootingControlCommand.takePicture()` as follows.
When shooting is completed, `invoke` function of the callback class is called. If an error occurs, error information is stored in the argument. 

``` Swift
  let device = try await ThetaBle.Companion.shared.scan(name: name)
  ...
  do {
    try await device?.connect()
    let service = device?.shootingControlCommand
    let captureMode = try await service?.getCaptureMode()
    if (captureMode != .image) {
      try await service?.setCaptureMode(value: .image)
      // Wait a little or you'll fail
      try await Task.sleep(nanoseconds: 1 * 1000 * 1000 * 1000)
    }

    class Callback: KotlinSuspendFunction1 {
        func invoke(p1: Any?) async throws -> Any? {
            if p1 == nil {
                // success. Take a picture.
            } else {
                // handle error
            }
            return nil
        }
    }

    try service?.takePicture(complete: Callback())
  } catch {
    // handle error
  }
```

## Camera status
The camera status can be got & set & notified by the following functions prepared in `CameraStatusCommand`.

| Type                         | Get              | Set              | Notify                           |
|------------------------------|------------------|------------------|----------------------------------|
| Battery level                | `getBatteryLevel`  | -                | `setBatteryLevelNotify`            |
| charging state               | `getBatteryStatus` | -                | `setBatteryStatusNotify`           |
| start-up status              | `getCameraPower`   | `setCameraPower`   | `setCameraPowerNotify`             |
| Error description in detail. | -                | -                | `setCommandErrorDescriptionNotify` |
| Plugin power status          | `getPluginControl` | `setPluginControl` | `setPluginControlNotify`           |

### Notification function
The Notification uses `setXxxxxxNotify()`.
If a callback function is passed as an argument, that function will be called when the state is changed. If the argument is omitted, the set callback function will be canceled.
If an error occurs, the `error` will be passed as an argument of the callback function.

``` Swift
  let service = device?.cameraStatusCommand
  try service?.setBatteryStatusNotify {value, error in
      if error != nil {
          // handle error
      } else {
          // Notify value
      }
  }
```

## Camera Control Command v2
To use Camera Control Command v2 functions, use `CameraControlCommandV2`.
If not supported, `ThetaDevice.cameraControlCommandV2` becomes `nil`.

``` Swift
  let thetaInfo = try? await device.cameraControlCommandV2?.getInfo()
  let model = thetaInfo?.model
  ...
```
