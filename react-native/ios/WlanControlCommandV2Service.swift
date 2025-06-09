//
//  WlanControlCommandV2Service.swift
//  ThetaBleClientReactNative
//
//  Created on 2025/01/22.
//

import THETABleClient

let KEY_WIFI_INFO = "wifiInfo"
let KEY_ETHERNET = "ethernet"
let KEY_LTE = "lte"
let KEY_IS_CONNECTED = "isConnected"
let KEY_IS_INTERNET_ACCESSIBLE = "isInternetAccessible"
let KEY_SSID_LIST = "ssidList"
let KEY_SSID_STEALTH = "ssidStealth"
let KEY_CONNECTION_PRIORITY = "connectionPriority"
let KEY_IP_ADDRESS = "ipAddress"
let KEY_SUBNET_MASK = "subnetMask"
let KEY_DEFAULT_GATEWAY = "defaultGateway"

enum WlanControlCommandV2Service {
    static func setNetworkType(id: Int,
                               value: String,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let enumValue = getEnumValue(values: NetworkType.values(), name: value)
                guard let enumValue else {
                    reject(ERROR_TITLE, "Network type not found. \(value)", nil)
                    return
                }
                try await service.setNetworkType(networkType: enumValue)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setNetworkTypeNotify(id: Int,
                                     enable: Bool,
                                     sendEvent: @escaping ([String: Any]) -> Void,
                                     resolve: @escaping () -> Void,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        do {
            if enable {
                try service.setNetworkTypeNotify { value, error in
                    sendEvent(
                        toNetworkTypeNotify(
                            deviceId: id,
                            value: value,
                            error: error
                        )
                    )
                }
            } else {
                try service.setNetworkTypeNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }

    static func getConnectedWifiInfo(id: Int,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let value = try await service.getConnectedWifiInfo()
                resolve(fromTheta(connectedWifiInfo: value))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setConnectedWifiInfoNotify(id: Int,
                                           enable: Bool,
                                           sendEvent: @escaping ([String: Any]) -> Void,
                                           resolve: @escaping () -> Void,
                                           reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        do {
            if enable {
                try service.setConnectedWifiInfoNotify { value, error in
                    sendEvent(
                        toConnectedWifiInfoNotify(
                            deviceId: id,
                            value: value,
                            error: error
                        )
                    )
                }
            } else {
                try service.setConnectedWifiInfoNotify()
            }
            resolve()
        } catch {
            reject(ERROR_TITLE, error.localizedDescription, error)
        }
    }

    static func scanSsidStart(id: Int,
                              timeout: Int32,
                              sendEvent: @escaping ([String: Any]) -> Void,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                class Callback: WlanControlCommandV2ScanCallback {
                    let deviceId: Int
                    let sendEvent: ([String: Any]) -> Void
                    init(deviceId: Int, sendEvent: @escaping ([String: Any]) -> Void) {
                        self.deviceId = deviceId
                        self.sendEvent = sendEvent
                    }

                    func onCompleted(ssidList: [String]) {
                        sendEvent(
                            toNotify(
                                deviceId: deviceId,
                                characteristic: BleCharacteristic.notificationScannedSsid,
                                params: fromTheta(ssid: nil, ssidList: ssidList),
                                error: nil
                            )
                        )
                    }

                    func onNotify(ssid: String) {
                        sendEvent(
                            toNotify(
                                deviceId: deviceId,
                                characteristic: BleCharacteristic.notificationScannedSsid,
                                params: fromTheta(ssid: ssid, ssidList: nil),
                                error: nil
                            )
                        )
                    }
                }
                try await service.scanSsidStart(timeout: timeout, callback: Callback(deviceId: id, sendEvent: sendEvent))
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func scanSsidStop(id: Int,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                try await service.scanSsidStop()
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setAccessPointDynamically(id: Int,
                                          params: [String: Any],
                                          resolve: @escaping RCTPromiseResolveBlock,
                                          reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let base = try toSetAccessPointBasicParams(params: params)
                try await service.setAccessPointDynamically(
                    ssid: base.ssid,
                    ssidStealth: base.ssidStealth,
                    security: base.security,
                    password: base.password,
                    connectionPriority: base.connectionPriority,
                    proxy: base.proxy
                )
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func setAccessPointStatically(id: Int,
                                         params: [String: Any],
                                         resolve: @escaping RCTPromiseResolveBlock,
                                         reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }

        Task {
            do {
                let base = try toSetAccessPointBasicParams(params: params)
                let statically = try toSetAccessPointStaticallyParams(params: params)
                try await service.setAccessPointStatically(
                    ssid: base.ssid,
                    ssidStealth: base.ssidStealth,
                    security: base.security,
                    password: base.password,
                    connectionPriority: base.connectionPriority,
                    ipAddress: statically.ipAddress,
                    subnetMask: statically.subnetMask,
                    defaultGateway: statically.defaultGateway,
                    proxy: base.proxy
                )
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    static func getWlanPasswordState(id: Int,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let service = device.wlanControlCommandV2 else {
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

func toNetworkTypeNotify(
    deviceId: Int,
    value: NetworkType?,
    error: KotlinThrowable?
) -> [String: Any] {
    let paramsInfo = (value != nil) ? [NetworkType.companion.keyName: value?.name] : nil
    let errorInfo = toNotifyError(error: error)
    return toNotify(deviceId: deviceId,
                    characteristic: BleCharacteristic.writeSetNetworkType,
                    params: paramsInfo,
                    error: errorInfo)
}

func fromTheta(connectedInfo: ConnectedInfo) -> [String: Any?] {
    let result: [String: Any?] = [
        KEY_SSID: connectedInfo.ssid,
        KEY_IS_CONNECTED: connectedInfo.isConnected,
        KEY_IS_INTERNET_ACCESSIBLE: connectedInfo.isInternetAccessible,
    ]
    return result
}

func fromTheta(connectedWifiInfo: ConnectedWifiInfo) -> [String: Any?] {
    var result: [String: Any?] = [:]
    if let value = connectedWifiInfo.wifiInfo {
        result[KEY_WIFI_INFO] = fromTheta(connectedInfo: value)
    }
    if let value = connectedWifiInfo.ethernet {
        result[KEY_ETHERNET] = fromTheta(connectedInfo: value)
    }
    if let value = connectedWifiInfo.lte {
        result[KEY_LTE] = fromTheta(connectedInfo: value)
    }
    return result
}

func toConnectedWifiInfoNotify(
    deviceId: Int,
    value: ConnectedWifiInfo?,
    error: KotlinThrowable?
) -> [String: Any] {
    var paramsInfo: [String: Any?]?
    if let value {
        paramsInfo = fromTheta(connectedWifiInfo: value)
    }
    let errorInfo = toNotifyError(error: error)
    return toNotify(deviceId: deviceId,
                    characteristic: BleCharacteristic.notificationConnectedWifiInfo,
                    params: paramsInfo,
                    error: errorInfo)
}

func fromTheta(ssid: String?, ssidList: [String]?) -> [String: Any?] {
    var result: [String: Any?] = [:]
    if let ssid {
        result[KEY_SSID] = ssid
    }
    if let ssidList {
        result[KEY_SSID_LIST] = ssidList
    }
    return result
}

struct SetAccessPointBaseParams {
    let ssid: String
    let ssidStealth: Bool
    let security: WifiSecurityMode
    let password: String
    let connectionPriority: Int32
    let proxy: Proxy?
}

struct SetAccessPointStaticallyParams {
    let ipAddress: String
    let subnetMask: String
    let defaultGateway: String
}

func toSetAccessPointBasicParams(params: [String: Any?]) throws -> SetAccessPointBaseParams {
    guard let ssid = params[KEY_SSID] as? String else {
        throw ThetaClientError.invalidArgument(KEY_SSID)
    }
    let ssidStealth = params[KEY_SSID_STEALTH] as? Bool ?? false
    let security = getEnumValue(
        values: WifiSecurityMode.values(),
        name: params[WifiSecurityMode.companion.keyName] as? String ?? WifiSecurityMode.none.name
    ) ?? WifiSecurityMode.none
    let password = params[KEY_PASSWORD] as? String ?? ""
    let connectionPriority = Int32(params[KEY_CONNECTION_PRIORITY] as? Int ?? 1)
    let ipAddress = params[KEY_IP_ADDRESS] as? String
    let subnetMask = params[KEY_SUBNET_MASK] as? String
    let defaultGateway = params[KEY_DEFAULT_GATEWAY] as? String
    let proxy = params[Proxy.companion.keyName]
    let proxyParam: Proxy? = {
        if let proxy = proxy as? [String: Any] {
            return toProxy(params: proxy)
        }
        return nil
    }()

    return SetAccessPointBaseParams(
        ssid: ssid,
        ssidStealth: ssidStealth,
        security: security,
        password: password,
        connectionPriority: connectionPriority,
        proxy: proxyParam
    )
}

func toSetAccessPointStaticallyParams(params: [String: Any?]) throws -> SetAccessPointStaticallyParams {
    guard let ipAddress = params[KEY_IP_ADDRESS] as? String else {
        throw ThetaClientError.invalidArgument(KEY_IP_ADDRESS)
    }
    guard let subnetMask = params[KEY_SUBNET_MASK] as? String else {
        throw ThetaClientError.invalidArgument(KEY_SUBNET_MASK)
    }
    guard let defaultGateway = params[KEY_DEFAULT_GATEWAY] as? String else {
        throw ThetaClientError.invalidArgument(KEY_DEFAULT_GATEWAY)
    }

    return SetAccessPointStaticallyParams(
        ipAddress: ipAddress,
        subnetMask: subnetMask,
        defaultGateway: defaultGateway
    )
}
