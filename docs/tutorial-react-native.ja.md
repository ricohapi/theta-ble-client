# RICOH360 THETA BLE Clientチュートリアル React Native編

## 使用可能な機種

* RICOH THETA Z1
* RICOH THETA X

## パッケージの導入
`theta-ble-client`を`package.json`に追加する。

```shell
$ npx react-native@latest init YourProject
$ cd YourProject
$ yarn add theta-ble-client
```

## 権限の設定
Bluetoothを使用する為の権限の設定。

### Android
Androidは、アプリケーション側でBluetooth権限の要求を行う必要がある。

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
`plist`に`NSBluetoothAlwaysUsageDescription`を追加する。

## Bluetooth認証(RICOH THETA V/Z1)
THETA は、Web API と Bluetooth API を介して認証します。カメラはペアリングを使用しません。
Web API コマンド[camera.\_setBluetoothDevice](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/commands/camera._set_bluetooth_device.md)からUUID をカメラに登録し、オプション[\_bluetoothPower](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/options/_bluetooth_power.md)で Bluetooth モジュールをオンにする。
UUIDを登録時に、THETAの名前が取得できるので、ライブラリでは、この名前を使用して操作を行う。
登録したUUIDは、接続時に使用する。

RICOH THETA Xの場合は、認証せずに操作を行うことができます。

参考：
https://github.com/ricohapi/theta-api-specs/blob/main/theta-bluetooth-api/getting_started.md#1-bluetooth-authentication


## THETAを検索する
Web API コマンド[camera.\_setBluetoothDevice](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/commands/camera._set_bluetooth_device.md)で、UUIDを登録した際に取得した名前で、THETAを検索する。

`scan()`を使用してTHETAを検索して`ThetaDevice`を取得する。
以降、`ThetaDevice`を使用して、各操作を行う。
所得した`ThetaDevice`の使用が終了した場合は、`ThetaDevice.release()`を呼び出して、リソースを解放する必要がある。

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

### タイムアウトの設定
`scan()`にタイムアウトを指定することができる。
各値は、省略することで既定値が使用される。

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

| 属性                   | 使用される箇所             | 既定値(ms) |
|----------------------|---------------------|---------|
| `timeoutScan`        | 検索時                 | 30,000  |
| `timeoutPeripheral`  | THETAに接続する際の機器情報の取得 | 1,000   |
| `timeoutConnect`     | 実際にTHETAに接続する時      | 5,000   |
| `timeoutTakePicture` | 静止画撮影時              | 10,000  |

## THETAに接続する
`scan()`で取得した`ThetaDevice`を使用して`ThetaDevice.connect()`で接続する。
認証が必要な場合は、認証で登録したUUIDを指定する。(RICOH THETA V/Z1)

``` Typescript
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

## THETAから切断する
`scan()`で取得した`ThetaDevice`を使用して`ThetaDevice.disconnect()`で切断する。

``` Typescript
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

## APIの呼び出し
APIを呼び出すには、`ThetaDevice.getService()`に`BleServiceEnum`を渡して、サービスオブジェクトを取得して行う。
サービスオブジェクトは、`ThetaDevice.connect()`で接続した後に取得可能となる。
サービスが対応していない場合は、`undefined`となる。

| サービス名        | `BleServiceEnum`            | サービスオブジェクト               |
|--------------|-----------------------------|--------------------------|
| カメラ情報        | `CAMERA_INFORMATION`        | `CameraInformation`      |
| カメラステータスコマンド | `CAMERA_STATUS_COMMAND`     | `CameraStatusCommand`    |
| カメラ制御コマンド    | `CAMERA_CONTROL_COMMANDS`   | `CameraControlCommands`  |
| 撮影制御コマンド     | `SHOOTING_CONTROL_COMMAND`  | `ShootingControlCommand` |
| カメラ制御コマンドV2  | `CAMERA_CONTROL_COMMAND_V2` | `CameraControlCommandV2` |

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

## カメラ情報を取得する
カメラ情報は、`CameraInformation`に準備してある以下の関数で取得する

| 情報                | 関数                       | 型        |
|-------------------|--------------------------|----------|
| ファームウェアリビジョン      | `getFirmwareRevision`    | `string` |
| メーカー名             | `getManufacturerName`    | `string` |
| モデル番号             | `getModelNumber`         | `string` |
| シリアル番号            | `getSerialNumber`        | `string` |
| WLAN MACアドレス      | `getWlanMacAddress`      | `string` |
| Bluetooth MACアドレス | `getBluetoothMacAddress` | `string` |


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

## 静止画を撮影する
キャプチャモードを確認してから、`ShootingControlCommand.takePicture()`を呼び出して静止画を撮影する。

キャプチャモード`CaptureModeEnum`は、`ShootingControlCommand.getCaptureMode()`で取得して、`ShootingControlCommand.setCaptureMode()`で設定を行う。静止画の場合は、`CaptureModeEnum.IMAGE`に設定する。
また、`ShootingControlCommand.setCaptureMode()`で変更した後は、少し待たないと、撮影に失敗する。

* キャプチャモード`CaptureModeEnum`
  | 値       | 説明              |
  |---------|-----------------|
  | `IMAGE` | 静止画撮影モード        |
  | `VIDEO` | 動画撮影モード         |
  | `LIVE`  | ライブ ストリーミング モード |

撮影が完了すると、`ShootingControlCommand.takePicture()`に渡した関数が呼ばれ、エラーが発生した場合は、引数にエラー情報が格納される。

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

## カメラの状態
カメラの状態は、`CameraStatusCommand`に準備してある以下の関数で取得、設定、通知を行う。

| 種類         | 取得                 | 設定                 | 通知                                 |
|------------|--------------------|--------------------|------------------------------------|
| バッテリー残量    | `getBatteryLevel`  | -                  | `setBatteryLevelNotify`            |
| 充電状態       | `getBatteryStatus` | -                  | `setBatteryStatusNotify`           |
| カメラの起動状態   | `getCameraPower`   | `setCameraPower`   | `setCameraPowerNotify`             |
| カメラのエラー    | -                  | -                  | `setCommandErrorDescriptionNotify` |
| プラグインの起動状態 | `getPluginControl` | `setPluginControl` | `setPluginControlNotify`           |

### 通知機能
通知は、`setXxxxxxNotify()`を使用する。
引数にコールバック関数を渡すと、状態が変更された時に、その関数が呼び出される。引数を省略すると、設定したコールバック関数を解除する。
エラーが発生した場合には、コールバック関数の引数`error`に値が返る。

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
Camera Control Command v2の機能を使用するには、`ThetaDevice.getService()`に`BleServiceEnum.CAMERA_CONTROL_COMMAND_V2`を指定して得られる、`CameraControlCommandV2`を使用します。

``` Typescript
  const cameraControlCommandV2 = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2) as CameraControlCommandV2 | undefined;
  const thetaInfo = await cameraControlCommandV2?.getInfo();
  const model = thetaInfo?.model;
  ...
```
