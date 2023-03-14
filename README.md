[![test](https://github.com/ricohapi/theta-ble-client/actions/workflows/test.yaml/badge.svg)](https://github.com/ricohapi/theta-ble-client/actions/workflows/test.yaml)

# THETA BLE Client

This library provides a way to control RICOH THETA using [THETA Bluetooth API](https://github.com/ricohapi/theta-api-specs/tree/main/theta-bluetooth-api).
Your app can perform the following actions:
* Take a photo and video
* Acquire the status of THETA
* Acquire and set properties of THETA

## Supported Environments
* Android native (Kotlin)
* iOS native (Swift)
* React Native

## Supported Models
* THETA Z1
* THETA X

## Directory Structure
* theta-ble-client
  * demos: Demo applications
  * docs: Documentation
  * kotlin-multiplatform: Library body ([Kotlin Multiplatform Mobile](https://kotlinlang.org/docs/multiplatform-mobile-getting-started.html))
  * react-native: React Native package

## Build

### Android (aar)
```
theta-ble-client$ ./gradlew assemble
```

aar is output to `theta-ble-client-$/kotlin-multiplatform/build/outputs/aar`

### iOS (XCFramework)
```
theta-ble-client$ ./gradlew podPublishXCFramework
```

XCFramework is output to `theta-ble-client$/kotlin-multiplatform/build/cocoapods/publish`

### ReactNative
See README in each directory.(`react-native`)

### Test
```
theta-ble-client$ ./gradlew testReleaseUnitTest
```

## How to Use
See tutorial in `docs` directory.


## License

[MIT License](LICENSE)
