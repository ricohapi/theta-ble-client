# RICOH360 THETA BLE Client Tutorial for React Native

## Available models

* RICOH THETA Z1
* RICOH THETA X

## Installation of packages
Add `theta-ble-client` to `package.json`.

```shell
$ npx react-native@latest init YourProject
$ cd YourProject
$ yarn add theta-ble-client
```

## Setting of authorization
You need request to use Bluetooth. 

### Android
Android requires the application to request Bluetooth permissions. 

``` Typescript
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
Add `NSBluetoothAlwaysUsageDescription` to `info.plist`. 


## Bluetooth authentication(RICOH THETA V/Z1)
THETA authenticates via the Web API and Bluetooth API. THETA does not use pairing.

Register the UUID to THETA using the Web API command [camera.\_setBluetoothDevice](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/commands/camera._set_bluetooth_device.md) and turn on the Bluetooth module using the option [\_bluetoothPower](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/options/_bluetooth_power.md).
Since the name of THETA can be retrieved when the UUID is registered, this name is used in the library. 

For RICOH THETA X, operation can be performed without authentication.

Reference:
https://github.com/ricohapi/theta-api-specs/blob/main/theta-bluetooth-api/getting_started.md#1-bluetooth-authentication


## Search for THETA
Search THETA by the name obtained when registering with the Web API command [camera.\_setBluetoothDevice](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/commands/camera._set_bluetooth_device.md). 

Use `scan()` to search for THETA then acquire `ThetaDevice`.
Thereafter, each operation is performed using `ThetaDevice`.
If the use of `Theta Device` ends, you need to call `ThetaDevice.release()` to free the resource. 

``` typescript
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

### Timeout settings
A timeout can be specified for `scan()`.
If omitted, will use the default value.

``` Typescript
  const device = await scan({
    name: '12345678',
    timeout: {
      timeoutScan: 30_000,
      timeoutPeripheral: 1_000,
      timeoutConnect: 5_000,
      timeoutTakePicture: 10_000,
    },
  });
```

| Attributes           | Location of use                                       | default(ms) |
|----------------------|-------------------------------------------------------|-------------|
| `timeoutScan`        | When searching for THETA                              | 30,000      |
| `timeoutPeripheral`  | Obtaining device information when connecting to THETA | 1,000       |
| `timeoutConnect`     | When connecting to THETA actually                     | 5,000       |
| `timeoutTakePicture` | When taking picture                                   | 10,000      |

## Connect to THETA
Connect with `ThetaDevice.connect()` using `ThetaDevice` acquired with `scan()`. 
If authentication is required, specify the UUID registered for authentication.(RICOH THETA V/Z1)

``` typescript
  const device = await scan({
    name: '12345678',
  });
  ...
  try {
    await device.connect(uuid);
    // success
  } catch (_) {
    // handle error
  }
```

## Disconnect from THETA
Disconnect with `ThetaDevice.disconnect()` using `ThetaDevice` acquired with `scan()`.

``` typescript
  const device = await scan({
    name: '12345678',
  });
  ...
  try {
    await device.disconnect();
    // success
  } catch (_) {
    // handle error
  }
```

## Call the API
The API is called by passing `BleServiceEnum` to `ThetaDevice.getService()` to obtain a service object.
The service object can be obtained after connecting with `ThetaDevice.connect()`.
If the service is not supported, it is `undefined`.

| Service Name              | `BleServiceEnum`            | Service Object           |
|---------------------------|-----------------------------|--------------------------|
| Camera Information        | `CAMERA_INFORMATION`        | `CameraInformation`      |
| Camera Status Command     | `CAMERA_STATUS_COMMAND`     | `CameraStatusCommand`    |
| Camera Control Commands   | `CAMERA_CONTROL_COMMANDS`   | `CameraControlCommands`  |
| Shooting Control Command  | `SHOOTING_CONTROL_COMMAND`  | `ShootingControlCommand` |
| Camera Control Command v2 | `CAMERA_CONTROL_COMMAND_V2` | `CameraControlCommandV2` |

```Typescript
  const device = await scan({
    name: '12345678',
  });
  await device?.connect();

  const service = (await device?.getService(
    BleServiceEnum.CAMERA_INFORMATION,
  )) as CameraInformation | undefined;

  const firmware = await service?.getFirmwareRevision();
```

## Obtain THETA information
The THETA information is acquired by the following functions prepared in `CameraInformation`.

| Information           | Function                 | Type     |
|-----------------------|--------------------------|----------|
| Firmware vision       | `getFirmwareRevision`    | `string` |
| Manufacturer name     | `getManufacturerName`    | `string` |
| Model                 | `getModelNumber`         | `string` |
| Serial number         | `getSerialNumber`        | `string` |
| WLAN MAC address      | `getWlanMacAddress`      | `string` |
| Bluetooth MAC address | `getBluetoothMacAddress` | `string` |


```Typescript
  const device = await scan({
    name: '12345678',
  });
  try {
    await device?.connect();

    const service = (await device?.getService(
      BleServiceEnum.CAMERA_INFORMATION,
    )) as CameraInformation | undefined;

    const firmware = await service?.getFirmwareRevision();
    const maker = await service?.getManufacturerName();
    const model = await service?.getModelNumber();
    const serial = await service?.getSerialNumber();
    const wlan = await service?.getWlanMacAddress();
    const bluetooth = await service?.getBluetoothMacAddress();
    // success
  } catch (_) {
    // handle error
  }
```

## Shoot still images
After checking the capture mode, call `ShootingControlCommand.takePicture()` to shoot the still image. 

Capture mode `CaptureModeEnum` is acquired with `ShootingControlCommand.getCaptureMode()` and set with `ShootingControlCommand.setCaptureMode()`. For still images, set to `CaptureModeEnum.IMAGE`. 
Also, after changing with `ShootingControlCommand.setCaptureMode()`, shooting fails unless waiting a little. 

* Capture mode `CaptureModeEnum`
  | Value| Description |
  |---| ---|
  |`IMAGE`| Still image shooting mode|
  |`VIDEO`| Movie shooting mode|
  |`LIVE`| Live streaming mode|

When shooting is completed, the function passed to `ShootingControlCommand.takePicture()` is called. If an error occurs, error information is stored in the argument. 

```Typescript
  const device = await scan({
    name: '12345678',
  });
  try {
    await device?.connect();

    const service = (await device?.getService(
      BleServiceEnum.SHOOTING_CONTROL_COMMAND,
    )) as ShootingControlCommand | undefined;

    const captureMode = await service?.getCaptureMode();
    if (captureMode !== CaptureModeEnum.IMAGE) {
      await service?.setCaptureMode(CaptureModeEnum.IMAGE);
      await sleep(1000);  // Wait a little or you'll fail
    }

    service?.takePicture(error => {
      if (error) {
        // handle error
      } else {
        // success. Take a picture.
      }
    });
  } catch (error) {
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

``` Typescript
  const service = (await device?.getService(
    BleServiceEnum.CAMERA_STATUS_COMMAND,
  )) as CameraStatusCommand | undefined;

  await service?.setBatteryStatusNotify((value, error) => {
    if (error) {
        // handle error
    } else {
        // Notify value
    }
  });
```

## Camera Control Command v2
To use the Camera Control Command v2 feature, use the `CameraControlCommandV2` obtained by specifying `BleServiceEnum.CAMERA_CONTROL_COMMAND_V2` for `ThetaDevice.getService()`.

``` Typescript
  const cameraControlCommandV2 = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2) as CameraControlCommandV2 | undefined;
  const thetaInfo = await cameraControlCommandV2?.getInfo();
  const model = thetaInfo?.model;
  ...
```
