# iOS demo for theta-ble-client

A simple sample iOS application using [theta-ble-client](https://github.com/ricohapi/theta-ble-client).

## 目的

* THETA Client BLEを使う開発者に、簡単な実例を示す

## Functions

* Connect with THETA at bluetooth.
* Get THETA information.
* Take a photo with Theta.

## 設計方針

* UIの簡潔な記述ができる[SwiftUI](https://developer.apple.com/jp/xcode/swiftui/)を使う

## 留意点

* bundle id、signature情報はご自身のものに変更ください。
* xcodeでのビルド前にpod installを実行しておくこと。
  Podfileは以下のように、`THETABleClient`を設定している
  ```
  platform :ios, '15.0'
  target 'demo-ios' do
    # Comment the next line if you don't want to use dynamic frameworks
    use_frameworks!

    # Pods for demo-ios
    pod 'THETABleClient'
  end
  ```
