# RICOH360 THETA BLE Clientチュートリアル iOS編

## 使用可能な機種

* RICOH360 THETA A1
* RICOH THETA X
* RICOH THETA Z1

## フレームワークの導入

`Podfile`に`THETABleClient`フレームワークを追加します。
  
``` Podfile
pod 'THETABleClient', '1.3.2'
```

## 権限の設定

Bluetoothを使用する権限を設定するために、
`plist`に`NSBluetoothAlwaysUsageDescription`を追加する。

## Bluetoothの有効化 (Theta X/Z1のみ)

Theta X/Z1のBluetoothがオフの場合、本体操作でオンにすることもできますが、Web APIでオンにすることも可能です。Theta A1はBluetoothが常にオンになっています。

1. Web APIでオプション[\_bluetoothPower](https://docs-theta-api.ricoh360.com/web-api/options/bluetoothPower.html)を`ON`に設定します。

## THETAの検出

`ThetaBle.scan()`を使用してTHETAを検索して`ThetaDevice`を取得します。
Theta A1はシリアル番号を、Theta X/Z1はシリアル番号の数字部分を引数として渡します。
以降、`ThetaDevice`を使用して、各操作を行ないます。

``` Swift
import THETABleClient

let device = try await ThetaBle.Companion.shared.scan(name: name)
if let device = device {
    // success scan THETA
} else {
    // handle error
}
```

検出したいThetaのシリアル番号が不明な場合は、次のようにしてThetaの候補のリストを検出します。

```swift
let deviceList: [ThetaDevice] = try await ThetaBle.Companion.shared.scan()
for device in deviceList {
    // Theta A1の場合は device.name がシリアル番号
    // Theta X/Z1の場合は device.name シリアル番号の数字部分
}
```

`ThetaBle.scan()`にタイムアウトを指定することもできます。デフォルト値でよければ指定を省略できます。

```swift
let timeout = ThetaBle.Timeout(
    timeoutScan: 20000,
    timeoutPeripheral: 2000,
    timeoutConnect: 3000,
    timeoutTakePicture: 15000
)
let device = try await ThetaBle.Companion.shared.scan(name: name, timeout: timeout)
```

| 属性                  | 使用される場面       | デフォルト値(ms) |
|----------------------|---------------------|-----------------|
| `timeoutScan`        | 検出時                          | 30,000  |
| `timeoutPeripheral`  | THETAに接続する際の機器情報の取得 | 1,000   |
| `timeoutConnect`     | 実際にTHETAに接続する時          | 5,000   |
| `timeoutTakePicture` | 静止画撮影時                    | 10,000  |

## THETAに接続
`ThetaBle.scan()`で取得した`ThetaDevice`を使用して`ThetaDevice.connect()`で接続します。

BLE APIの使用が終わったら、`ThetaDevice.disconnect()`で切断します。


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

## BLE APIの呼び出し

BLE APIを呼び出すには、`ThetaDevice`に定義したサービスオブジェクトのメソッドを呼びます。
サービスオブジェクトは、`ThetaDevice.connect()`で接続した後に取得可能となります。
接続した機種がサービスに対応していない場合、サービスオブジェクトは`nil`になります。

| サービス名 | サービスオブジェクト | クラス | 備考 |
|-----------|--------------------|--------|-----|
| [Camera control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#camera-control-command-v2-service)  | `cameraControlCommandV2` | `CameraControlCommandV2` | |
| [WLAN control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-service) | `wlanControlCommand`| `WlanControlCommand` ||
| [WLAN control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-v2-service) | `wlanControlCommandV2`| `WlanControlCommandV2` | |
| [Bluetooth control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#bluetooth-control-command) | `bluetoothControlCommand` | `BluetoothControlCommand` | Theta A1のみ |

例えば、Thetaの機種名とシリアル番号を取得するには次のようにサービスオブジェクトを使用します。

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
if let info = try await service?.getInfo() {
  print("\(info.model) \(info.serialNumber)")
}
```

## カメラ情報の取得

`ThetaDevice.cameraControlCommandV2.getInfo()`でカメラ情報(`ThetaInfo`オブジェクト)を取得できます。`ThetaInfo`のプロパティは次の通りです。

| 情報 | プロパティ | 型 |
|------|-----------|----|
| メーカー名 | `manufacturer` | `String` |
| THETAのモデル | `model` | `ThetaModel` |
| シリアル番号 | `serialNumber` | `String` |
| WLAN MACアドレス | `wlanMacAddress` | `String?` |
| Bluetooth MACアドレス | `bluetoothMacAddress` | `String?` |
| ファームウェアバージョン | `firmwareVersion` | `String` |
| 稼働時間(秒) | `uptime` | `Int` |

## オプションの値の取得

`OptionName`で定義しているオプションの値を`CameraControlCommandV2.getOptions()`で取得できます。ただし`Password`は取得できません。

| オプション | `OptionName`のプロパティ | 型 | 備考 |
| --------- | ----------------------- | -- | ---- |
| アクセスポイント情報 | `AccessInfo` | `AccessInfo?` | Theta A1, Xのみ |
| Thetaの電源状態 | `CameraPower` | `CameraPower?` | |
| 撮影モード | `CaptureMode` | `CaptureMode?` | |
| APモードのWLANパスワードの初期値 | `DefaultWifiPassword` | `String?` ||
| 設定されているネットワークタイプ | `NetworkType` | `NetworkType?` | |
| APモードのSSID | `Ssid` | `String?` ||
| CLモードのダイジェスト認証用のユーザ名 | `Username` | `String?` ||
| 無線アンテナの設定 | `WlanAntennaConfig` | `WlanAntennaConfig?` | Theta A1, Xのみ |
| APモードの無線周波数 | `WlanFrequency` | `WlanFrequency?` ||

<br/>

`OptionName.DefaultWifiPassword`を取得するサンプルコードです。

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
let optionNames: [OptionName] = [.DefaultWifiPassword]
options = try await service?.getOptions(optionNames)
print(options?.defaultWifiPassword)
```

## オプションの値の設定

`ThetaOptions`で定義しているオプションの値を`CameraControlCommandV2.setOptions()`で設定できます。
ただし`defaultWifiPassword`は設定できません。

| オプション | `ThetaOptions`のプロパティ | 型 | 備考 |
| --------- | ----------------------- | -- | ---- |
| アクセスポイント情報 | `accessInfo` | `AccessInfo?` | Theta A1, Xのみ |
| Thetaの電源状態 | `cameraPower` | `CameraPower?` | |
| 撮影モード | `captureMode` | `CaptureMode?` | |
| ネットワークタイプ | `networkType` | `NetworkType?` | |
| APモードのSSID | `ssid` | `String?` ||
| CLモードのダイジェスト認証用のユーザ名 | `username` | `String?` ||
| CLモードのダイジェスト認証用のパスワード | `password` | `String?` ||
| 無線アンテナの設定 | `wlanAntennaConfig` | `WlanAntennaConfig?` | Theta A1, Xのみ |
| APモードの無線周波数 | `wlanFrequency` | `WlanFrequency?` ||

<br/>

`ThetaOptions.captureMode`をビデオモードに設定するサンプルコードです。

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
let options = ThetaOptions()
options.captureMode = .video
try await service?.setOptions(options)
```

## 撮影

`CameraControlCommandV2.releaseShutter()`を呼ぶと、`CaptureMode`オプションの値とThetaの状態に従って撮影処理を行ないます。

| `CaptureMode`オプションの値 | 動画撮影中か否か | 撮影処理 |
| ------------------------- | ----------------| ------- |
| `image` | n/a | 静止画撮影 |
| `video` | 撮影していない | ビデオ撮影開始 |
| `video` | 撮影中 | ビデオ撮影終了 |

<br/>

撮影を行うサンプルコードです。

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
try await service?.releaseShutter()
```

## Thetaの状態取得

`CameraControlCommandV2.getState()`および`CameraControlCommandV2.getState2()`でThetaの状態(`ThetaState`、`ThetaState2`)を取得できます。

`ThateState`のプロパティは下記の通りです。

| 情報 | プロパティ | 型 | 備考 |
|------|-----------|----|-----|
| 最新画像URL | `latestFileUrl` | `String?` | 最後に撮影された画像(DNGフォーマット以外)のURL。WLAN接続すればダウンロードできる。 |
| ビデオ撮影時間(秒) | `recordedTime` | `Int?` ||
| ビデオ撮影可能時間(秒) | `recordableTime` | `Int?` ||
| 連続撮影状態 | `captureStatus` | `CaptureStatus?` ||
| 連続撮影枚数 | `capturedPictures` | `Int?` ||
| 撮影設定 | `function` | `ShootingFunction?` ||
| バッテリーの有無 | `batteryInsert` | `Boolean?` ||
| バッテリー残量 | `batteryLevel` | `Float?` | 0から1まで |
| 充電状態 | `batteryState` | `ChargingState?` ||
| メイン基盤の温度 | `boardTemp` | `Int?` ||
| バッテリーの温度 | `batteryTemp` | `Int?` ||
| エラー状態 | `cameraError` | `List<CameraError>?`||

<br/>

`ThateState2`のプロパティは下記の通りです。

| 情報 | プロパティ | 型 | 備考 |
|------|-----------|----|-----|
| 内蔵GPSモジュールの位置情報 | `internalGpsInfo` | `StateGpsInfo?` ||
| 外部GPSデバイスの位置情報 | `externalGpsInfo` | `StateGpsInfo?` ||

<br/>

最新画像URLを取得するサンプルコードです。

```Swift
let device = try await ThetaBle.Companion.shared.scan(name: name)
try await device?.connect()
let service = device?.cameraControlCommandV2
let state = try await service?.getState()
let url = state?.latestFileUrl
```

## Thetaの状態通知

コールバック関数を引数にして`CameraControlCommandV2.setStateNotify()`を呼ぶと、状態が変化した時にその関数が呼び出されます。
引数を省略すると、設定済みのコールバック関数を解除します。
エラーが発生した場合には、コールバック関数の引数`error`に値が返ります。

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

## 無線LANの制御

`WlanControlCommandV2`サービスを使って無線LANの制御を行えます。

| 機能 | メソッド | 引数 |戻り値 |
| ---- | ------- | ---- |------ |
| アクセスポイント接続状態の取得 | `getConnectedWifiInfo()` | - |`ConnectedWifiInfo` |
| アクセスポイントの設定(DHCP) | `setAccessPointDynamically()` | ssidなど | - |
| アクセスポイントの設定(静的) | `setAccessPointStatically()` | ssid、IPアドレスなど | - |
| ネットワークタイプの設定 | `setNetworkType()` | `NetworkType` | - |
