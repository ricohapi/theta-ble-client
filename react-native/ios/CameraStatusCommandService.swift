//
//  CameraStatusCommandService.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/11/06.
//

import THETABleClient

class CameraStatusCommandService {
    static func getBatteryLevel(id: Int,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getBatteryLevel()
                resolve(value)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setBatteryLevelNotify(id: Int,
                                      enable: Bool,
                                      sendEvent: @escaping ([String: Any]) -> Void,
                                      resolve: @escaping () -> Void,
                                      reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        do {
            if enable {
                try service.setBatteryLevelNotify { value, error in
                    sendEvent(
                        toBatteryLevelNotify(
                            deviceId: id,
                            value: value,
                            error: error
                        )
                    )
                }
            } else {
                try service.setBatteryLevelNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }

    static func getBatteryStatus(id: Int,
                                 resolve: @escaping RCTPromiseResolveBlock,
                                 reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getBatteryStatus()
                resolve(value.name)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setBatteryStatusNotify(id: Int,
                                       enable: Bool,
                                       sendEvent: @escaping ([String: Any]) -> Void,
                                       resolve: @escaping () -> Void,
                                       reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        do {
            if enable {
                try service.setBatteryStatusNotify { value, error in
                    sendEvent(
                        toBatteryStatusNotify(
                            deviceId: id,
                            value: value,
                            error: error
                        )
                    )
                }
            } else {
                try service.setBatteryStatusNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }

    static func getCameraPower(id: Int,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getCameraPower()
                resolve(value.name)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setCameraPower(id: Int,
                               value: String,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let enumValue = getEnumValue(values: CameraPower.values(), name: value)
                guard let enumValue else {
                    reject(ERROR_TITLE, "Camera power not found. \(value)", nil)
                    return
                }
                try await service.setCameraPower(value: enumValue)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setCameraPowerNotify(id: Int,
                                     enable: Bool,
                                     sendEvent: @escaping ([String: Any]) -> Void,
                                     resolve: @escaping () -> Void,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        do {
            if enable {
                try service.setCameraPowerNotify { value, error in
                    sendEvent(
                        toCameraPowerNotify(
                            deviceId: id,
                            value: value,
                            error: error
                        )
                    )
                }
            } else {
                try service.setCameraPowerNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }

    static func setCommandErrorDescriptionNotify(id: Int,
                                                 enable: Bool,
                                                 sendEvent: @escaping ([String: Any]) -> Void,
                                                 resolve: @escaping () -> Void,
                                                 reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        do {
            if enable {
                try service.setCommandErrorDescriptionNotify { value, error in
                    sendEvent(
                        toCommandErrorDescriptionNotify(
                            deviceId: id,
                            value: value,
                            error: error
                        )
                    )
                }
            } else {
                try service.setCommandErrorDescriptionNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }

    static func getPluginControl(id: Int,
                                 resolve: @escaping RCTPromiseResolveBlock,
                                 reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getPluginControl()
                resolve(fromTheta(pluginControl: value))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setPluginControl(id: Int,
                                 value: Any,
                                 resolve: @escaping RCTPromiseResolveBlock,
                                 reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let objects = value as? [String: Any?] ?? [:]
                let thetaValue = toTheta(pluginControl: objects)
                guard let thetaValue else {
                    reject(ERROR_TITLE, "Plugin control not found. \(value)", nil)
                    return
                }
                try await service.setPluginControl(value: thetaValue)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setPluginControlNotify(id: Int,
                                       enable: Bool,
                                       sendEvent: @escaping ([String: Any]) -> Void,
                                       resolve: @escaping () -> Void,
                                       reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraStatusCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        do {
            if enable {
                try service.setPluginControlNotify { value, error in
                    sendEvent(
                        toPluginControlNotify(
                            deviceId: id,
                            value: value,
                            error: error
                        )
                    )
                }
            } else {
                try service.setPluginControlNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }
}
