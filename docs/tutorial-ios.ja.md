# RICOH360 THETA BLE Clientチュートリアル iOS編

## 使用可能な機種

* RICOH THETA Z1
* RICOH THETA X

## フレームワークの導入
`Podfile`に`THETABleClient`フレームワークを追加します。
  
``` Podfile
pod 'THETABleClient', '1.0.0'
```

## 権限の設定
Bluetoothを使用する為の権限の設定。

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

`ThetaBle.scan()`を使用してTHETAを検索して`ThetaDevice`を取得する。
以降、`ThetaDevice`を使用して、各操作を行う。

``` Swift
import THETABleClient

  let device = try await ThetaBle.Companion.shared.scan(name: name)
  if let device = device {
      // success scan THETA
  } else {
      // handle error
  }
```

### タイムアウトの設定
`ThetaBle.scan()`にタイムアウトを指定することができる。
各値は、省略することで既定値が使用される。

``` Swift
  let timeout = ThetaBle.Timeout(
      timeoutScan: 30000,
      timeoutPeripheral: 1000,
      timeoutConnect: 5000,
      timeoutTakePicture: 10000
  )
  let device = try await ThetaBle.Companion.shared.scan(name: name, timeout: timeout)
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

``` Swift
  let device = try await ThetaBle.Companion.shared.scan(name: name)
  ...
  do {
      try await device!.connect(uuid: uuid)
      // success
  } catch {
      // handle error
  }
```

## THETAから切断する
`ThetaBle.scan()`で取得した`ThetaDevice`を使用して`ThetaDevice.disconnect()`で切断する。

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

## APIの呼び出し
APIを呼び出すには、`ThetaDevice`に準備してあるサービスオブジェクトを取得して行う。
サービスオブジェクトは、`ThetaDevice.connect()`で接続した後に取得可能となる。
サービスが対応していない場合は、`nil`となる。

| サービス名        | サービスオブジェクト               | クラス                      |
|--------------|--------------------------|--------------------------|
| カメラ情報        | `cameraInformation`      | `CameraInformation`      |
| カメラステータスコマンド | `cameraStatusCommand`    | `CameraStatusCommand`    |
| カメラ制御コマンド    | `cameraControlCommands`  | `CameraControlCommands`  |
| 撮影制御コマンド     | `shootingControlCommand` | `ShootingControlCommand` |
| カメラ制御コマンドV2  | `cameraControlCommandV2` | `CameraControlCommandV2` |

```Swift
  let device = try await ThetaBle.Companion.shared.scan(name: name)
  try await device?.connect()
  let service = device?.cameraInformation
  let firmware = try await service?.getFirmwareRevision()
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

## 静止画を撮影する
キャプチャモードを確認してから、`ShootingControlCommand.takePicture()`を呼び出して静止画を撮影する。

キャプチャモード`CaptureMode`は、`ShootingControlCommand.getCaptureMode()`で取得して、`ShootingControlCommand.setCaptureMode()`で設定を行う。静止画の場合は、`CaptureMode.image`に設定する。
また、`ShootingControlCommand.setCaptureMode()`で変更した後は、少し待たないと、撮影に失敗する。

* キャプチャモード`CaptureMode`
  | 値       | 説明              |
  |---------|-----------------|
  | `image` | 静止画撮影モード        |
  | `video` | 動画撮影モード         |
  | `live`  | ライブ ストリーミング モード |

`ShootingControlCommand.takePicture()`は、以下のように`KotlinSuspendFunction1`を実装したコールバック用クラスを作成して呼び出します。
撮影が完了すると、コールバック用クラスの`invoke`関数が呼ばれ、エラーが発生した場合は、引数にエラー情報が格納される。

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
Camera Control Command v2の機能を使用するには、`CameraControlCommandV2`を使用します。
対応していない場合は、`ThetaDevice.cameraControlCommandV2`が`nil`となります。

``` Swift
  let thetaInfo = try? await device.cameraControlCommandV2?.getInfo()
  let model = thetaInfo?.model
  ...
```
