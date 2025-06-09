//
//  CameraControlCommandsService.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/11/06.
//

import THETABleClient

class CameraControlCommandsService {
    static func getPluginList(id: Int,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommands else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getPluginList()
                resolve(fromTheta(pluginList: value))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func getPluginOrders(id: Int,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommands else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getPluginOrders()
                resolve(fromTheta(pluginOrders: value))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setPluginOrders(id: Int,
                                value: Any,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraControlCommands else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let objects = value as? [String: Any?] ?? [:]
                let thetaValue = toTheta(pluginOrders: objects)
                guard let thetaValue else {
                    reject(ERROR_TITLE, "Plugin orders not found. \(value)", nil)
                    return
                }
                try await service.setPluginOrders(value: thetaValue)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
}
