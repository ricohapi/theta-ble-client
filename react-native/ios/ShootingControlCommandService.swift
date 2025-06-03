//
//  ShootingControlCommandService.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/11/06.
//

import THETABleClient

class ShootingControlCommandService {
    static func getCaptureMode(id: Int,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.shootingControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getCaptureMode()
                resolve(value.name)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setCaptureMode(id: Int,
                               value: String,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.shootingControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let enumValue = getEnumValue(values: CaptureMode.values(), name: value)
                guard let enumValue else {
                    reject(ERROR_TITLE, "Capture mode not found. \(value)", nil)
                    return
                }
                try await service.setCaptureMode(value: enumValue)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func getFileFormat(id: Int,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.shootingControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getFileFormat()
                resolve(value.name)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setFileFormat(id: Int,
                              value: String,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.shootingControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let enumValue = getEnumValue(values: FileFormat.values(), name: value)
                guard let enumValue else {
                    reject(ERROR_TITLE, "File format not found. \(value)", nil)
                    return
                }
                try await service.setFileFormat(value: enumValue)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func getMaxRecordableTime(id: Int,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.shootingControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getMaxRecordableTime()
                resolve(value.name)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setMaxRecordableTime(id: Int,
                                     value: String,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.shootingControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let enumValue = getEnumValue(values: MaxRecordableTime.values(), name: value)
                guard let enumValue else {
                    reject(ERROR_TITLE, "File format not found. \(value)", nil)
                    return
                }
                try await service.setMaxRecordableTime(value: enumValue)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func takePicture(id: Int,
                            resolve: @escaping RCTPromiseResolveBlock,
                            reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.shootingControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        class Callback: KotlinSuspendFunction1 {
            let resolve: RCTPromiseResolveBlock
            let reject: RCTPromiseRejectBlock
            init(
                resolve: @escaping RCTPromiseResolveBlock,
                reject: @escaping RCTPromiseRejectBlock
            ) {
                self.resolve = resolve
                self.reject = reject
            }

            func invoke(p1: Any?) async throws -> Any? {
                if p1 == nil {
                    resolve(nil)
                } else {
                    let errorClass = String(describing: type(of: p1))
                    reject(ERROR_TITLE,
                           (p1 as? NSError)?.localizedDescription ?? "unknown " + errorClass,
                           p1 as? NSError)
                }
                return nil
            }
        }

        do {
            try service.takePicture(complete: Callback(resolve: resolve, reject: reject))
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }
}
