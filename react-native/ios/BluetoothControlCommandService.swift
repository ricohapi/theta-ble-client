//
//  BluetoothControlCommandService.swift
//  ThetaBleClientReactNative
//
//  Created on 2024/12/20.
//

import THETABleClient

let KEY_DEVICE = "device"
let KEY_MAC_ADDRESS = "macAddress"
let KEY_PAIRING = "pairing"

enum BluetoothControlCommandService {
    static func scanPeripheralDevice(id: Int,
                                     timeout: Int32,
                                     resolve: @escaping ([[String: Any?]]) -> Void,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.bluetoothControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.scanPeripheralDevice(timeout: timeout)
                resolve(fromTheta(peripheralDeviceList: value))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func scanPeripheralDeviceStart(id: Int,
                                          timeout: Int32,
                                          sendEvent: @escaping ([String: Any]) -> Void,
                                          resolve: @escaping RCTPromiseResolveBlock,
                                          reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.bluetoothControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                class Callback: BluetoothControlCommandScanCallback {
                    let deviceId: Int
                    let sendEvent: ([String: Any]) -> Void
                    init(deviceId: Int, sendEvent: @escaping ([String: Any]) -> Void) {
                        self.deviceId = deviceId
                        self.sendEvent = sendEvent
                    }

                    func onCompleted(peripheralDeviceList: [PeripheralDevice]) {
                        sendEvent(
                            toNotify(
                                deviceId: deviceId,
                                characteristic: BleCharacteristic.scanBluetoothPeripheralDevice,
                                params: fromTheta(peripheralDeviceList: peripheralDeviceList),
                                error: nil
                            )
                        )
                    }

                    func onNotify(peripheralDevice: PeripheralDevice) {
                        sendEvent(
                            toNotify(
                                deviceId: deviceId,
                                characteristic: BleCharacteristic.notificationScannedBluetoothPeripheralDevice,
                                params: fromTheta(peripheralDevice: peripheralDevice),
                                error: nil
                            )
                        )
                    }
                }
                try await service.scanPeripheralDeviceStart(timeout: timeout, callback: Callback(deviceId: id, sendEvent: sendEvent))
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func scanPeripheralDeviceStop(id: Int,
                                         resolve: @escaping RCTPromiseResolveBlock,
                                         reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.bluetoothControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                try await service.scanPeripheralDeviceStop()
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func connectPeripheralDevice(id: Int,
                                        macAddress: String,
                                        resolve: @escaping RCTPromiseResolveBlock,
                                        reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.bluetoothControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                try await service.connectPeripheralDevice(macAddress: macAddress)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func deletePeripheralDevice(id: Int,
                                       macAddress: String,
                                       resolve: @escaping RCTPromiseResolveBlock,
                                       reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.bluetoothControlCommand else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                try await service.deletePeripheralDevice(macAddress: macAddress)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
}

func fromTheta(peripheralDeviceList: [PeripheralDevice]) -> [[String: Any?]] {
    peripheralDeviceList.map { peripheralDevice in
        fromTheta(peripheralDevice: peripheralDevice)
    }
}

func fromTheta(peripheralDevice: PeripheralDevice) -> [String: Any?] {
    [
        KEY_DEVICE: peripheralDevice.device,
        KEY_MAC_ADDRESS: peripheralDevice.macAddress,
        KEY_PAIRING: peripheralDevice.pairing,
        PeripheralDeviceStatus.companion.keyName: peripheralDevice.status.name,
    ]
}
