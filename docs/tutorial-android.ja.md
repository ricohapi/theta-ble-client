# RICOH360 THETA BLE Clientチュートリアル Android編

## 使用可能な機種

* RICOH THETA Z1
* RICOH THETA X

## ライブラリの導入
モジュールの`build.gradle`の`dependencies`に次を追加します。

```
implementation "com.ricoh360.thetableclient:theta-ble-client-android:1.0.0"
```

## 権限の設定
Bluetoothを使用する為の権限の設定。
アプリケーション側でBluetooth権限の要求を行う必要がある。

``` Kotlin
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

`ThetaBle.scan()`を使用してTHETAを検索して`ThetaDevice`を取得する。
以降、`ThetaDevice`を使用して、各操作を行う。

``` Kotlin
  val device = ThetaBle.scan(devName)
  if (device != null) {
      // success scan THETA
  } else {
      // handle error
  }
```

### タイムアウトの設定
`ThetaBle.scan()`にタイムアウトを指定することができる。
各値は、省略することで既定値が使用される。

``` Kotlin
  val timeout = ThetaBle.Timeout(
      timeoutScan = 30_000,
      timeoutPeripheral = 1_000,
      timeoutConnect = 5_000,
      timeoutTakePicture = 10_000,
  )
  val device = ThetaBle.scan(devName, timeout)
```

| 属性                   | 使用される箇所             | 既定値(ms) |
|----------------------|---------------------|---------|
| `timeoutScan`        | 検索時                 | 30,000  |
| `timeoutPeripheral`  | THETAに接続する際の機器情報の取得 | 1,000   |
| `timeoutConnect`     | 実際にTHETAに接続する時      | 5,000   |
| `timeoutTakePicture` | 静止画撮影時              | 10,000  |

## THETAに接続する
`ThetaBle.scan()`で取得した`ThetaDevice`を使用して`ThetaDevice.connect()`で接続する。
認証が必要な場合は、認証で登録したUUIDを指定する。(RICOH THETA V/Z1)

``` Kotlin
  val device = ThetaBle.scan(devName)
  ...
  try {
      device!!.connect(uuid)
      // success
  } catch (e: ThetaBle.ThetaBleException) {
      // handle error
  }
```

## THETAから切断する
`ThetaBle.scan()`で取得した`ThetaDevice`を使用して`ThetaDevice.disconnect()`で切断する。

``` Kotlin
  val device = ThetaBle.scan(devName)
  ...
  try {
      device!!.disconnect()
      // success
  } catch (e: ThetaBle.ThetaBleException) {
      // handle error
  }
```

## APIの呼び出し
APIを呼び出すには、`ThetaDevice`に準備してあるサービスオブジェクトを取得して行う。
サービスオブジェクトは、`ThetaDevice.connect()`で接続した後に取得可能となる。
サービスが対応していない場合は、`null`となる。

| サービス名        | サービスオブジェクト               | クラス                      |
|--------------|--------------------------|--------------------------|
| カメラ情報        | `cameraInformation`      | `CameraInformation`      |
| カメラステータスコマンド | `cameraStatusCommand`    | `CameraStatusCommand`    |
| カメラ制御コマンド    | `cameraControlCommands`  | `CameraControlCommands`  |
| 撮影制御コマンド     | `shootingControlCommand` | `ShootingControlCommand` |
| カメラ制御コマンドV2  | `cameraControlCommandV2` | `CameraControlCommandV2` |

```kotlin
  val device = ThetaBle.scan(devName)
  device?.connect()
  val service = device?.cameraInformation
  val firmware = service?.getFirmwareRevision()
```

## カメラ情報を取得する
カメラ情報は、`CameraInformation`に準備してある以下の関数で取得する

| 情報                | 関数                       | 型        |
|-------------------|--------------------------|----------|
| ファームウェアリビジョン      | `getFirmwareRevision`    | `String` |
| メーカー名             | `getManufacturerName`    | `String` |
| モデル番号             | `getModelNumber`         | `String` |
| シリアル番号            | `getSerialNumber`        | `String` |
| WLAN MACアドレス      | `getWlanMacAddress`      | `String` |
| Bluetooth MACアドレス | `getBluetoothMacAddress` | `String` |


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

## 静止画を撮影する
キャプチャモードを確認してから、`ShootingControlCommand.takePicture()`を呼び出して静止画を撮影する。

キャプチャモード`CaptureMode`は、`ShootingControlCommand.getCaptureMode()`で取得して、`ShootingControlCommand.setCaptureMode()`で設定を行う。静止画の場合は、`CaptureMode.IMAGE`に設定する。
また、`ShootingControlCommand.setCaptureMode()`で変更した後は、少し待たないと、撮影に失敗する。

* キャプチャモード`CaptureMode`
  | 値       | 説明              |
  |---------|-----------------|
  | `IMAGE` | 静止画撮影モード        |
  | `VIDEO` | 動画撮影モード         |
  | `LIVE`  | ライブ ストリーミング モード |

撮影が完了すると、`ShootingControlCommand.takePicture()`に渡した関数が呼ばれ、エラーが発生した場合は、引数にエラー情報が格納される。

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
Camera Control Command v2の機能を使用するには、`CameraControlCommandV2`を使用します。
対応していない場合は、`ThetaDevice.cameraControlCommandV2`が`null`となります。

```Kotlin
  val thetaInfo = device.cameraControlCommandV2?.getInfo()
  val model = thetaInfo?.model
```
