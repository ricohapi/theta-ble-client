//
//  CameraInformationService.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/11/06.
//

import THETABleClient

class CameraInformationService {
    static func getFirmwareRevision(id: Int,
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraInformation else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        
        Task {
            do {
                let value = try await service.getFirmwareRevision()
                resolve(value)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
    
    static func getManufacturerName(id: Int,
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraInformation else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        
        Task {
            do {
                let value = try await service.getManufacturerName()
                resolve(value)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
    
    static func getModelNumber(id: Int,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraInformation else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        
        Task {
            do {
                let value = try await service.getModelNumber()
                resolve(value)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
    
    static func getSerialNumber(id: Int,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraInformation else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        
        Task {
            do {
                let value = try await service.getSerialNumber()
                resolve(value)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
    
    static func getWlanMacAddress(id: Int,
                                  resolve: @escaping RCTPromiseResolveBlock,
                                  reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraInformation else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        
        Task {
            do {
                let value = try await service.getWlanMacAddress()
                resolve(value)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
    
    static func getBluetoothMacAddress(id: Int,
                                       resolve: @escaping RCTPromiseResolveBlock,
                                       reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.cameraInformation else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        
        Task {
            do {
                let value = try await service.getBluetoothMacAddress()
                resolve(value)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
}
