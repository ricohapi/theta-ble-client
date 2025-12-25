# RICOH360 THETA BLE Clientチュートリアル React Native編

## 使用可能な機種

* RICOH360 THETA A1
* RICOH THETA X
* RICOH THETA Z1

## パッケージの導入
`theta-ble-client`を`package.json`に追加する。

```shell
$ npx react-native@latest init YourProject
$ cd YourProject
$ yarn add theta-ble-client
```

## 権限の設定
Bluetoothを使用する為の権限の設定が必要です。

### Android
アプリケーション側でBluetooth権限の要求を行います。

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
`plist`に`NSBluetoothAlwaysUsageDescription`を追加します。

## Bluetoothの有効化 (Theta X/Z1のみ)

Theta X/Z1のBluetoothがオフの場合、本体操作でオンにすることもできますが、Web APIでオンにすることも可能です。Theta A1はBluetoothが常にオンになっています。

1. Web APIでオプション[\_bluetoothPower](https://docs-theta-api.ricoh360.com/web-api/options/bluetoothPower.html)を`ON`に設定します。

## THETAの検出

Theta A1のシリアル番号がわかっていれば、シリアル番号を引数にして[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-ble/theta-ble.ts)を呼びます。

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

Theta X/Z1のシリアル番号がわかっていれば、シリアル番号の数字部分を引数にして[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-ble/theta-ble.ts)を呼びます。

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

検出したいThetaのシリアル番号が不明な場合は、次のようにしてThetaの候補のリストを検出します。

``` Typescript
import {
  scan,
  ThetaDevice,
} from 'theta-ble-client-react-native';

const deviceList = await scan();
deviceList.forEach(device => {
  // Theta A1なら device.name がシリアル番号
  // Theta X/Z1なら device.name がシリアル番号の数字部分
})
```

[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-ble/theta-ble.ts)にタイムアウトを指定することもできます。デフォルト値でよければ指定を省略できます。

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

| 属性                  | 使用される場面       | デフォルト値(ms) |
|----------------------|---------------------|-----------------|
| `timeoutScan`        | 検出時                          | 30,000  |
| `timeoutPeripheral`  | THETAに接続する際の機器情報の取得 | 1,000   |
| `timeoutConnect`     | 実際にTHETAに接続する時          | 5,000   |
| `timeoutTakePicture` | 静止画撮影時                    | 10,000  |

## THETAに接続

[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-ble/theta-ble.ts)で取得した[`ThetaDevice`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/theta-device/theta-device.ts)を使用して`ThetaDevice.connect()`で接続します。

BLE APIの使用が終わったら、`ThetaDevice.disconnect()`で切断します。

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

## BLE APIの呼び出し

`ThetaDevice.getService()`の引数に[`BleServiceEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/ble-service.ts)を渡して、サービスオブジェクトを取得しAPIを呼び出します。
サービスオブジェクトは、`ThetaDevice.connect()`で接続した後に取得可能になります。
デバイスが指定したサービスに対応していない場合、サービスオブジェクトは`undefined`になります。

| サービス名 | [`BleServiceEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/ble-service.ts) | クラス | 備考 |
|-----------|--------------------|--------|-----|
| [Camera control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#camera-control-command-v2-service)  | `CAMERA_CONTROL_COMMAND_V2` | [`CameraControlCommandV2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/camera-control-command-v2.ts) | |
| [WLAN control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-service) | `WLAN_CONTROL_COMMAND`| [`WlanControlCommand`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/wlan-control-command.ts) ||
| [WLAN control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-v2-service) | `WLAN_CONTROL_COMMAND_V2`| [`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/wlan-control-command-v2.ts) | |
| [Bluetooth control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#bluetooth-control-command) | `BLUETOOTH_CONTROL_COMMAND` | [`BluetoothControlCommand`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/bluetooth-control-command.ts) | Theta A1のみ |

例えば、Thetaの機種名とシリアル番号を取得するには次のようにサービスオブジェクトを使用します。

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

## カメラ情報の取得

`CameraControlCommandV2.getInfo()`でカメラ情報([`ThetaInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-info.ts)オブジェクト)を取得できます。`ThetaInfo`のプロパティは次の通りです。

| 情報 | プロパティ | 型 |
|------|-----------|----|
| メーカー名 | `manufacturer` | `string` |
| THETAのモデル | `model` | `ThetaModel` |
| シリアル番号 | `serialNumber` | `string` |
| WLAN MACアドレス | `wlanMacAddress?` | `string` |
| Bluetooth MACアドレス | `bluetoothMacAddress?` | `string` |
| ファームウェアバージョン | `firmwareVersion` | `string` |
| 稼働時間(秒) | `uptime` | `number` |

## オプションの値の取得

[`OptionName`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/option-name.ts) で定義しているオプションの値を`CameraControlCommandV2.getOptions()`で取得できます。ただし`Password`は取得できません。

| オプション | `OptionName`のプロパティ | 型 | 備考 |
| --------- | ----------------------- | -- | ---- |
| アクセスポイント情報 | `AccessInfo` | [`AccessInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/access-info.ts) | Theta A1, Xのみ |
| Thetaの電源状態 | `CameraPower` | [`CameraPowerEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/camera-power.ts) | |
| 撮影モード | `CaptureMode` | [`CaptureModeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/capture-mode.ts) | |
| APモードのWLANパスワードの初期値 | `DefaultWifiPassword` | `string` ||
| 設定されているネットワークタイプ | `NetworkType` | [`NetworkTypeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/network-type.ts) | |
| APモードのSSID | `Ssid` | `string` ||
| CLモードのダイジェスト認証用のユーザ名 | `Username` | `string` ||
| 無線アンテナの設定 | `WlanAntennaConfig` | [`WlanAntennaConfigEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-antenna-config.ts) | Theta A1, Xのみ |
| APモードの無線周波数 | `WlanFrequency` | [`WlanFrequencyEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-frequency.ts) ||

<br/>

`OptionName.DefaultWifiPassword`を取得するサンプルコードです。

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


## オプションの値の設定

[`ThetaOptions`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-options.ts) で定義しているオプションの値を`CameraControlCommandV2.setOptions()`で設定できます。
ただし`defaultWifiPassword`は設定できません。

| オプション | [`ThetaOptions`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-options.ts)のプロパティ | 型 | 備考 |
| --------- | ----------------------- | -- | ---- |
| アクセスポイント情報 | `accessInfo?` | [`AccessInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/access-info.ts) | Theta A1, Xのみ |
| Thetaの電源状態 | `cameraPower?` | [`CameraPowerEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/camera-power.ts) | |
| 撮影モード | `captureMode?` | [`CaptureModeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/capture-mode.ts) | |
| ネットワークタイプ | `networkType?` | [`NetworkTypeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/network-type.ts) | |
| APモードのSSID | `ssid?` | `String` ||
| CLモードのダイジェスト認証用のユーザ名 | `username?` | `string` ||
| CLモードのダイジェスト認証用のパスワード | `password?` | `string` ||
| 無線アンテナの設定 | `wlanAntennaConfig?` | [`WlanAntennaConfigEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-antenna-config.ts) | Theta A1, Xのみ |
| APモードの無線周波数 | `wlanFrequency?` | [`WlanFrequencyEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/wlan-frequency.ts) ||

<br/>

`ThetaOptions.captureMode`をビデオモードに設定するサンプルコードです。

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

## 撮影

`CameraControlCommandV2.releaseShutter()`を呼ぶと、`captureMode`オプションの値とThetaの状態に従って撮影処理を行ないます。

| `captureMode`オプションの値 | 動画撮影中か否か | 撮影処理 |
| ------------------------- | ----------------| ------- |
| `CaptureModeEnum.IMAGE` | n/a | 静止画撮影 |
| `CaptureModeEnum.VIDEO` | 撮影していない | ビデオ撮影開始 |
| `CaptureModeEnum.VIDEO` | 撮影中 | ビデオ撮影終了 |

<br/>

撮影を行うサンプルコードをです。

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

## Thetaの状態取得

`CameraControlCommandV2.getState()`および`CameraControlCommandV2.getState2()`でThetaの状態([`ThetaState`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state.ts)、[`ThetaState2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state2.ts))を取得できます。

[`ThateState`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state.ts)のプロパティは下記の通りです。

| 情報 | プロパティ | 型 | 備考 |
|------|-----------|----|-----|
| 最新画像URL | `latestFileUrl?` | `string` | 最後に撮影された画像(DNGフォーマット以外)のURL。WLAN接続すればダウンロードできる。 |
| ビデオ撮影時間(秒) | `recordedTime?` | `number` ||
| ビデオ撮影可能時間(秒) | `recordableTime?` | `number` ||
| 連続撮影状態 | `captureStatus?` | [`CaptureStatusEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/capture-status.ts) ||
| 連続撮影枚数 | `capturedPictures?` | `number` ||
| 撮影設定 | `shootingFunction?` | [`ShootingFunctionEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/shooting-function.ts) ||
| バッテリーの有無 | `batteryInsert?` | `boolean` ||
| バッテリー残量 | `batteryLevel?` | `number` | 0から1まで |
| 充電状態 | `batteryState?` | [`ChargingStateEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/charging-state.ts) ||
| メイン基盤の温度 | `boardTemp?` | `number` ||
| バッテリーの温度 | `batteryTemp?` | `number` ||
| エラー状態 | `cameraError?` | [`CameraErrorEnum[]`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/camera-error.ts)||

<br/>

[`ThateState2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/theta-state2.ts)のプロパティは下記の通りです。

| 情報 | プロパティ | 型 | 備考 |
|------|-----------|----|-----|
| 内蔵GPSモジュールの位置情報 | `internalGpsInfo?` | [`{gpsInfo: GpsInfo}`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/gps-info.ts) ||
| 外部GPSデバイスの位置情報 | `externalGpsInfo?` | [`{gpsInfo: GpsInfo}`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/gps-info.ts) ||

<br/>

最新画像URLを取得するサンプルコードです。

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

## Thetaの状態通知

コールバック関数を引数にして`CameraControlCommandV2.setStateNotify()`を呼ぶと、状態が変化した時にその関数が呼び出されます。
引数を省略すると、設定済みのコールバック関数を解除します。
エラーが発生した場合には、コールバック関数の引数`error`に値が返ります。

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

## 無線LANの制御

[`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/wlan-control-command-v2.ts)サービスを使って無線LANの制御を行えます。

| 機能 | メソッド | 引数 |戻り値 |
| ---- | ------- | ---- |------ |
| アクセスポイント接続状態の取得 | `getConnectedWifiInfo()` | - |[`ConnectedWifiInfo`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/data/connected-wifi-info.ts) |
| アクセスポイントの設定(DHCP) | `setAccessPointDynamically()` | ssidなど | - |
| アクセスポイントの設定(静的) | `setAccessPointStatically()` | ssid、IPアドレスなど | - |
| ネットワークタイプの設定 | `setNetworkType()` | [`NetworkTypeEnum`](https://github.com/ricohapi/theta-ble-client-private/blob/main/react-native/src/service/values/network-type.ts) | - |
