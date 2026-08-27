# theta-ble-client-react-native

This library provides a way to control RICOH THETA using BLE.

## Prerequisite

* Build react-native wrapper

  ```shell
  theta-ble-client$ cd react-native
  react-native$ yarn
  react-native$ yarn prepare
  ```

* Install react-native

## Creating a project

* Create a project using `react-native-cli`, then add theta-ble-client.

  ```shell
  $ npx react-native@latest init YourProject
  $ cd YourProject
  $ yarn add [theta-ble-client directory]/react-native
  ```

* Settings on android

  * YourProject/android/build.gradle
	* set `minSdkVersion` to 26 or later.
  * YourProject/android/gradle.properties
	* This library supports the New Architecture only. Set `newArchEnabled=true` (required; without it, native modules fail to register at runtime).

* Settings on iOS
  * YourProject/ios/Podfile
	* Set `platform :ios` to '15.1' or later.
  * This library supports the New Architecture only. Do not set `RCT_NEW_ARCH_ENABLED=0`; the New Architecture is enabled by default since React Native 0.76.

* Building and execution

  ```
  $ cd YourProject
  $ yarn install
  $ yarn run android

  OR

  $ yarn run ios
  ```

  You can execute metro bundler on other terminal in advance.

  ```
  $ yarn start
  ```
