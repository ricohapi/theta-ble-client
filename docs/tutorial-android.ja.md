# RICOH360 THETA BLE Clientチュートリアル Android編

## 使用可能な機種

* RICOH360 THETA A1
* RICOH THETA X
* RICOH THETA Z1

## ライブラリの導入
モジュールの`build.gradle`の`dependencies`にtheta-ble-clientを追加します。

```
implementation "com.ricoh360.thetableclient:theta-ble-client-android:1.3.2"
```

## 権限の設定
Bluetoothを使用するには、アプリケーション側でBluetooth権限の要求を行う必要があります。

``` Kotlin
  if(Build.VERSION.SDK_INT > 30) {
      requestPermissions(
          arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
          ),
          REQUEST_MULTI_PERMISSIONS
      )
  } else {
      requestPermissions(
          arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
          ),
          REQUEST_MULTI_PERMISSIONS
      )
  }
```

## Bluetoothの有効化 (Theta X/Z1のみ)

Theta X/Z1のBluetoothがオフの場合、本体操作でオンにすることもできますが、Web APIでオンにすることも可能です。Theta A1はBluetoothが常にオンになっています。

1. Web APIでオプション[\_bluetoothPower](https://docs-theta-api.ricoh360.com/web-api/options/bluetoothPower.html)を`ON`に設定します。
で Bluetooth モジュールの電源をオンにします。

## THETAの検出

Theta A1のシリアル番号がわかっていれば、シリアル番号を引数にして[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/ThetaBle.kt)を呼びます。

``` Kotlin
val device: ThetaDevice? = ThetaBle.scan("AA01234567")
if (device != null) {
    // Theta has been found
} else {
    // Theta has not been found
}
```

Theta X/Z1のシリアル番号がわかっていれば、シリアル番号の数字部分を引数にして[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/ThetaBle.kt)を呼びます。

``` Kotlin
val device: ThetaDevice? = ThetaBle.scan("01234567")
if (device != null) {
    // Theta has been found
} else {
    // Theta has not been found
}
```

検出したいThetaのシリアル番号が不明な場合は、次のようにしてThetaの候補のリストを検出します。

``` Kotlin
val deviceList: List<ThetaDevice> = ThetaBle.scan()
deviceList.forEach {
    // Theta A1なら it.name がシリアル番号
    // Theta X/Z1なら it.name がシリアル番号の数字部分
}
```

[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/ThetaBle.kt)にタイムアウトを指定することもできます。デフォルト値でよければ指定を省略できます。

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

| 属性                  | 使用される場面       | デフォルト値(ms) |
|----------------------|---------------------|-----------------|
| `timeoutScan`        | 検出時                          | 30,000  |
| `timeoutPeripheral`  | THETAに接続する際の機器情報の取得 | 1,000   |
| `timeoutConnect`     | 実際にTHETAに接続する時          | 5,000   |
| `timeoutTakePicture` | 静止画撮影時                    | 10,000  |

## THETAに接続

[`ThetaBle.scan()`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/ThetaBle.kt)で取得した`ThetaBle.ThetaDevice`を使用して`ThetaDevice.connect()`で接続します。

BLE APIの使用が終わったら、`ThetaDevice.disconnect()`で切断します。

``` Kotlin
val device: ThetaBle.ThetaDevice? = ThetaBle.scan("AA01234567") // For Thata A1
// or
// val device: ThetaBle.ThetaDevice? = ThetaBle.scan("01234567")   // For Theta X/Z1

if (device != null) {
    try {
        device.connect()
        // call BLE APIs
        device.disconnect()
    } catch (e: ThetaBle.ThetaBleException) {
        // handle an error
    }
} 
```

## BLE APIの呼び出し

BLE APIを呼び出すには、`ThetaBle.ThetaDevice`に定義したサービスオブジェクトのメソッドを呼びます。
サービスオブジェクトは、`ThetaDevice.connect()`で接続した後に取得可能となります。
接続した機種がサービスに対応していない場合、サービスオブジェクトは`null`になります。

| サービス名 | サービスオブジェクト | クラス | 備考 |
|-----------|--------------------|--------|-----|
| [Camera control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#camera-control-command-v2-service)  | `cameraControlCommandV2` | [`CameraControlCommandV2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/CameraControlCommandV2.kt) | |
| [WLAN control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-service) | `wlanControlCommand`| [`WlanControlCommand`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/WlanControlCommand.kt) ||
| [WLAN control command v2](https://docs-theta-api.ricoh360.com/bluetooth-api/#wlan-control-command-v2-service) | `wlanControlCommandV2`| [`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/WlanControlCommandV2.kt) | |
| [Bluetooth control command](https://docs-theta-api.ricoh360.com/bluetooth-api/#bluetooth-control-command) | `bluetoothControlCommand` | [`BluetoothControlCommand`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/BluetoothControlCommand.kt) | Theta A1のみ |

例えば、Thetaの機種名とシリアル番号を取得するには次のようにサービスオブジェクトを使用します。

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    val info = service?.getInfo()
    println("${info?.model} ${info?.serialNumber}")
}
```

## カメラ情報の取得

`ThetaDevice.cameraControlCommandV2.getInfo()`でカメラ情報([`ThetaInfo`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaInfo.kt)オブジェクト)を取得できます。`ThetaInfo`のプロパティは次の通りです。

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

[`OptionName`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/OptionName.kt) で定義しているオプションの値を`CameraControlCommandV2.getOptions()`で取得できます。ただし`Password`は取得できません。

| オプション | `OptionName`のプロパティ | 型 | 備考 |
| --------- | ----------------------- | -- | ---- |
| アクセスポイント情報 | `AccessInfo` | [`AccessInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/AccessInfo.kt) | Theta A1, Xのみ |
| Thetaの電源状態 | `CameraPower` | [`CameraPower?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CameraPower.kt) | |
| 撮影モード | `CaptureMode` | [`CaptureMode?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CaptureMode.kt) | |
| APモードのWLANパスワードの初期値 | `DefaultWifiPassword` | `String?` ||
| 設定されているネットワークタイプ | `NetworkType` | [`NetworkType?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/NetworkType.kt) | |
| APモードのSSID | `Ssid` | `String?` ||
| CLモードのダイジェスト認証用のユーザ名 | `Username` | `String?` ||
| 無線アンテナの設定 | `WlanAntennaConfig` | [`WlanAntennaConfig?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanAntennaConfig.kt) | Theta A1, Xのみ |
| APモードの無線周波数 | `WlanFrequency` | [`WlanFrequency?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanFrequency.kt) ||

<br/>

`OptionName.DefaultWifiPassword`を取得するサンプルコードです。

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    val optionNames = listOf(
        OptionName.DefaultWifiPassword,
    )
    println(service?.getOptions(optionNames).defaultWifiPassword)
}
```

## オプションの値の設定

[`ThetaOptions`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaOptions.kt) で定義しているオプションの値を`CameraControlCommandV2.setOptions()`で設定できます。
ただし`defaultWifiPassword`は設定できません。

| オプション | `ThetaOptions`のプロパティ | 型 | 備考 |
| --------- | ----------------------- | -- | ---- |
| アクセスポイント情報 | `accessInfo` | [`AccessInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/AccessInfo.kt) | Theta A1, Xのみ |
| Thetaの電源状態 | `cameraPower` | [`CameraPower?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CameraPower.kt) | |
| 撮影モード | `captureMode` | [`CaptureMode?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CaptureMode.kt) | |
| ネットワークタイプ | `networkType` | [`NetworkType?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/NetworkType.kt) | |
| APモードのSSID | `ssid` | `String?` ||
| CLモードのダイジェスト認証用のユーザ名 | `username` | `String?` ||
| CLモードのダイジェスト認証用のパスワード | `password` | `String?` ||
| 無線アンテナの設定 | `wlanAntennaConfig` | [`WlanAntennaConfig?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanAntennaConfig.kt) | Theta A1, Xのみ |
| APモードの無線周波数 | `wlanFrequency` | [`WlanFrequency?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/WlanFrequency.kt) ||

<br/>

`ThetaOptions.captureMode`をビデオモードに設定するサンプルコードです。

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val options = ThetaOptions(captureMode= CaptureMode.VIDEO)
    val service = device.cameraControlCommandV2
    service?.setOptions(options)
}
```

## 撮影

`CameraControlCommandV2.releaseShutter()`を呼ぶと、`CaptureMode`オプションの値とThetaの状態に従って撮影処理を行ないます。

| `CaptureMode`オプションの値 | 動画撮影中か否か | 撮影処理 |
| ------------------------- | ----------------| ------- |
| `CaptureMode.IMAGE` | n/a | 静止画撮影 |
| `CaptureMode.VIDEO` | 撮影していない | ビデオ撮影開始 |
| `CaptureMode.VIDEO` | 撮影中 | ビデオ撮影終了 |

<br/>

撮影を行うサンプルコードをです。

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    service?.releaseShutter()
}
```

## Thetaの状態取得

`CameraControlCommandV2.getState()`および`CameraControlCommandV2.getState2()`でThetaの状態([`ThetaState`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState.kt)、[`ThetaState2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState2.kt))を取得できます。

[`ThateState`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState.kt)のプロパティは下記の通りです。

| 情報 | プロパティ | 型 | 備考 |
|------|-----------|----|-----|
| 最新画像URL | `latestFileUrl` | `String?` | 最後に撮影された画像(DNGフォーマット以外)のURL。WLAN接続すればダウンロードできる。 |
| ビデオ撮影時間(秒) | `recordedTime` | `Int?` ||
| ビデオ撮影可能時間(秒) | `recordableTime` | `Int?` ||
| 連続撮影状態 | `captureStatus` | [`CaptureStatus?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CaptureStatus.kt) ||
| 連続撮影枚数 | `capturedPictures` | `Int?` ||
| 撮影設定 | `function` | [`ShootingFunction?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/ShootingFunction.kt) ||
| バッテリーの有無 | `batteryInsert` | `Boolean?` ||
| バッテリー残量 | `batteryLevel` | `Float?` | 0から1まで |
| 充電状態 | `batteryState` | [`ChargingState?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/ChargingState.kt) ||
| メイン基盤の温度 | `boardTemp` | `Int?` ||
| バッテリーの温度 | `batteryTemp` | `Int?` ||
| エラー状態 | `cameraError` | [`List<CameraError>?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/CameraError.kt)||

<br/>

[`ThateState2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ThetaState2.kt)のプロパティは下記の通りです。

| 情報 | プロパティ | 型 | 備考 |
|------|-----------|----|-----|
| 内蔵GPSモジュールの位置情報 | `internalGpsInfo` | [`StateGpsInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/StateGpsInfo.kt) ||
| 外部GPSデバイスの位置情報 | `externalGpsInfo` | [`StateGpsInfo?`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/StateGpsInfo.kt) ||

<br/>

最新画像URLを取得するサンプルコードです。

```kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    println(service?.getState().latestFileUrl)
}
```

## Thetaの状態通知

コールバック関数を引数にして`CameraControlCommandV2.setStateNotify()`を呼ぶと、状態が変化した時にその関数が呼び出されます。
引数を省略すると、設定済みのコールバック関数を解除します。
エラーが発生した場合には、コールバック関数の引数`error`に値が返ります。

```Kotlin
val device = ThetaBle.scan(devName)
if(device != null) {
    device.connect()
    val service = device.cameraControlCommandV2
    service?.setStateNotify { state, error ->
        error?.run {
          // this: Throwable
        } ?: run {
          // this: ThetaState
        }
    }
}
```

## 無線LANの制御

[`WlanControlCommandV2`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/WlanControlCommandV2.kt)サービスを使って無線LANの制御を行えます。

| 機能 | メソッド | 引数 |戻り値 |
| ---- | ------- | ---- |------ |
| アクセスポイント接続状態の取得 | `getConnectedWifiInfo()` | - |[`ConnectedWifiInfo`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/ConnectedWifiInfo.kt) |
| アクセスポイントの設定(DHCP) | `setAccessPointDynamically()` | ssidなど | - |
| アクセスポイントの設定(静的) | `setAccessPointStatically()` | ssid、IPアドレスなど | - |
| ネットワークタイプの設定 | `setNetworkType()` | [`NetworkType`](https://github.com/ricohapi/theta-ble-client/blob/main/kotlin-multiplatform/src/commonMain/kotlin/com/ricoh360/thetableclient/service/data/values/NetworkType.kt) | - |
