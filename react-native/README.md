# theta-ble-client-react-native

This library provides a way to control RICOH THETA using.

## Prerequisite

* Build react-native wrapper

  ```shell
  theta-ble-client$ cd react-native
  react-native$ yarn
  react-native$ sh ./mkpackage.sh
  ```

* Install react-native

## Creating a project

* Create a project using `react-native-cli`, then add theta-ble-client.

  ```shell
  $ npx react-native@latest init YourProject
  $ cd YourProject
  $ yarn add [theta-ble-client directory]/react-native/package
  ```

* Settings on android

  * YourProject/android/build.gradle
	* set `minSdkVersion` to 26 or later.

* Settings on iOS
  * YourProject/ios/Podfile
	* Set `platform :ios` to '14.0' or later.

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
