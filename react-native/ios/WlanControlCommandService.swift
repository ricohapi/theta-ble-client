//
//  WlanControlCommandService.swift
//  ThetaBleClientReactNative
//
//  Created on 2025/06/20.
//

import THETABleClient

enum WlanControlCommandService {
    static func getWlanPasswordState(id: Int,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getWlanPasswordState()
                resolve(value.name)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
}
