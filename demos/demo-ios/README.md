# iOS demo for theta-ble-client

A simple sample iOS application using [theta-ble-client](https://github.com/ricohapi/theta-ble-client).

## Objective

* Show developers how to use THETA Client BLE.

## Functions

* Connect with THETA at bluetooth.
* Get THETA information.
* Take a photo with Theta.

## Policy

* Use [SwiftUI](https://developer.apple.com/jp/xcode/swiftui/) that can describe UI simply.

## Note

* Change bundle id and signature to your own.
* Before xcode building, execute pod install. Example of Podfile is following:

  ```
  platform :ios, '15.0'
  target 'demo-ios' do
    # Comment the next line if you don't want to use dynamic frameworks
    use_frameworks!

    # Pods for demo-ios
    pod 'THETABleClient'
  end
  ```
