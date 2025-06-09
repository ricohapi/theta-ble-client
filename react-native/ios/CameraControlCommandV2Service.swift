//
//  CameraControlCommandV2Service.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/09/28.
//

import THETABleClient

class CameraControlCommandV2Service {
    static func getInfo(id: Int,
                        resolve: @escaping ([String: Any?]) -> Void,
                        reject: @escaping (String, String, Error?) -> Void)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let thetaInfo = try await service.getInfo()
                resolve(fromTheta(thetaInfo: thetaInfo))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func getState(id: Int,
                         resolve: @escaping ([String: Any?]) -> Void,
                         reject: @escaping (String, String, Error?) -> Void)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let thetaState = try await service.getState()
                resolve(fromTheta(thetaState: thetaState))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setStateNotify(id: Int,
                               enable: Bool,
                               sendEvent: @escaping ([String: Any]) -> Void,
                               resolve: @escaping () -> Void,
                               reject: @escaping (String, String, Error?) -> Void)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        do {
            if enable {
                try service.setStateNotify { state, error in
                    let params = { () -> [String: Any?]? in
                        guard let state else {
                            return nil
                        }
                        return fromTheta(thetaState: state)
                    }()
                    sendEvent(
                        toNotify(
                            deviceId: id,
                            characteristic: BleCharacteristic.notifyState,
                            params: params,
                            error: toNotifyError(error: error)
                        )
                    )
                }
            } else {
                try service.setStateNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }

    static func getState2(id: Int,
                          resolve: @escaping ([String: Any?]) -> Void,
                          reject: @escaping (String, String, Error?) -> Void)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let thetaState2 = try await service.getState2()
                resolve(fromTheta(thetaState2: thetaState2))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func getOptions(id: Int,
                           optionNames: [Any],
                           resolve: @escaping ([String: Any?]) -> Void,
                           reject: @escaping (String, String, Error?) -> Void)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        guard let optionNames = optionNames as? [String] else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        let params = toGetOptionsParam(optionNames: optionNames)
        service.getOptions(optionNames: params) { options, error in
            if let error {
                reject(ERROR_TITLE, error.localizedDescription, error)
            } else if let options {
                resolve(fromTheta(thetaOptions: options))
            } else {
                reject(ERROR_TITLE, MESSAGE_NO_RESULT, nil)
            }
        }
    }

    static func getOptionsByString(id: Int,
                                   optionNames: [String],
                                   resolve: @escaping ([String: Any?]) -> Void,
                                   reject: @escaping (String, String, Error?) -> Void)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        service.getOptionsByString(optionNames: optionNames) { values, error in
            if let error {
                reject(ERROR_TITLE, error.localizedDescription, error)
            } else if let values {
                resolve(values)
            } else {
                reject(ERROR_TITLE, MESSAGE_NO_RESULT, nil)
            }
        }
    }

    static func setOptions(id: Int,
                           options: [AnyHashable: Any],
                           resolve: @escaping RCTPromiseResolveBlock,
                           reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        guard let options = options as? [String: Any] else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        Task {
            do {
                try await service.setOptions(options: toSetOptionsParam(options: options))
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func releaseShutter(id: Int,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        Task {
            do {
                try await service.releaseShutter()
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
}

func fromTheta(thetaInfo: ThetaInfo) -> [String: Any?] {
    var result: [String: Any?] = [
        KEY_MANUFACTURER: thetaInfo.manufacturer,
        ThetaModel.companion.keyName: thetaInfo.model.name,
        KEY_SERIAL_NUMBER: thetaInfo.serialNumber,
        KEY_FIRMWARE_VERSION: thetaInfo.firmwareVersion,
        KEY_UPTIME: thetaInfo.uptime,
    ]
    if let wlanMacAddress = thetaInfo.wlanMacAddress {
        result[KEY_WLAN_MAC_ADDRESS] = wlanMacAddress
    }
    if let bluetoothMacAddress = thetaInfo.bluetoothMacAddress {
        result[KEY_BLUETOOTH_MAC_ADDRESS] = bluetoothMacAddress
    }
    return result
}

func fromTheta(thetaState: ThetaState) -> [String: Any?] {
    var result: [String: Any?] = [:]
    if let batteryLevel = thetaState.batteryLevel {
        result[KEY_BATTERY_LEVEL] = batteryLevel.floatValue
    }
    if let captureStatus = thetaState.captureStatus {
        result[CaptureStatus.companion.keyName] = captureStatus.name
    }
    if let recordedTime = thetaState.recordedTime {
        result[KEY_RECORDED_TIME] = recordedTime.int32Value
    }
    if let recordableTime = thetaState.recordableTime {
        result[KEY_RECORDABLE_TIME] = recordableTime.int32Value
    }
    if let capturedPictures = thetaState.capturedPictures {
        result[KEY_CAPTURED_PICTURES] = capturedPictures.int32Value
    }
    if let latestFileUrl = thetaState.latestFileUrl {
        result[KEY_LATEST_FILE_URL] = latestFileUrl
    }
    if let batteryState = thetaState.batteryState {
        result[ChargingState.companion.keyName] = batteryState.name
    }
    if let function = thetaState.function {
        result[ShootingFunction.companion.keyName] = function.name
    }
    if let cameraError = thetaState.cameraError {
        var array: [String] = []
        cameraError.forEach { error in
            array.append(error.name)
        }
        result[CameraError.companion.keyName] = array
    }
    if let batteryInsert = thetaState.batteryInsert {
        result[KEY_BATTERY_INSERT] = batteryInsert.boolValue
    }
    if let boardTemp = thetaState.boardTemp {
        result[KEY_BOARD_TEMP] = boardTemp.int32Value
    }
    if let batteryTemp = thetaState.batteryTemp {
        result[KEY_BATTERY_TEMP] = batteryTemp.int32Value
    }
    return result
}

func fromTheta(thetaState2: ThetaState2) -> [String: Any?] {
    var result: [String: Any?] = [:]
    if let externalGpsInfo = thetaState2.externalGpsInfo,
       let gpsInfo = externalGpsInfo.gpsInfo
    {
        result[KEY_EXTERNAL_GPS_INFO] = [
            GpsInfo.companion.keyName: fromTheta(gpsInfo: gpsInfo),
        ]
    }
    if let internalGpsInfo = thetaState2.internalGpsInfo,
       let gpsInfo = internalGpsInfo.gpsInfo
    {
        result[KEY_INTERNAL_GPS_INFO] = [
            GpsInfo.companion.keyName: fromTheta(gpsInfo: gpsInfo),
        ]
    }
    return result
}
