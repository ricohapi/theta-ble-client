//
//  OptionsConverter.swift
//  ThetaBleClientReactNative
//
//  Created on 2024/11/13.
//

import Foundation
import THETABleClient

let KEY_PROXY_URL = "proxyURL"
let KEY_FREQUENCY = "frequency"
let KEY_WLAN_SIGNAL_STRENGTH = "wlanSignalStrength"
let KEY_WLAN_SIGNAL_LEVEL = "wlanSignalLevel"
let KEY_LTE_SIGNAL_STRENGTH = "lteSignalStrength"
let KEY_LTE_SIGNAL_LEVEL = "lteSignalLevel"
let KEY_HOST_NAME = "hostName"

func fromTheta(dhcpLeaseAddress: DhcpLeaseAddress) -> [String: Any] {
    [
        KEY_IP_ADDRESS: dhcpLeaseAddress.ipAddress,
        KEY_MAC_ADDRESS: dhcpLeaseAddress.macAddress,
        KEY_HOST_NAME: dhcpLeaseAddress.hostName,
    ]
}

func toDhcpLeaseAddress(value: [String: Any?]) throws -> DhcpLeaseAddress {
    guard let ipAddress = value[KEY_IP_ADDRESS] as? String,
          let macAddress = value[KEY_MAC_ADDRESS] as? String,
          let hostName = value[KEY_HOST_NAME] as? String
    else {
        throw ThetaClientError.invalidArgument(DhcpLeaseAddress.companion.keyName)
    }
    return DhcpLeaseAddress(
        ipAddress: ipAddress,
        macAddress: macAddress,
        hostName: hostName
    )
}

func fromTheta(accessInfo: AccessInfo) -> [String: Any] {
    var result = [
        KEY_SSID: accessInfo.ssid,
        KEY_IP_ADDRESS: accessInfo.ipAddress,
        KEY_SUBNET_MASK: accessInfo.subnetMask,
        KEY_DEFAULT_GATEWAY: accessInfo.defaultGateway,
        KEY_PROXY_URL: accessInfo.proxyURL,
        KEY_FREQUENCY: accessInfo.frequency.name,
        KEY_WLAN_SIGNAL_STRENGTH: accessInfo.wlanSignalStrength,
        KEY_WLAN_SIGNAL_LEVEL: accessInfo.wlanSignalStrength,
        KEY_LTE_SIGNAL_STRENGTH: accessInfo.lteSignalStrength,
        KEY_LTE_SIGNAL_LEVEL: accessInfo.lteSignalLevel,
    ] as [String: Any]
    if let list = accessInfo.dhcpLeaseAddress {
        let array = list.map { item in fromTheta(dhcpLeaseAddress: item) }
        if !array.isEmpty {
            result[DhcpLeaseAddress.companion.keyName] = array
        }
    }
    return result
}

func toAccessInfo(value: [String: Any?]) throws -> AccessInfo {
    guard let ssid = value[KEY_SSID] as? String,
          let ipAddress = value[KEY_IP_ADDRESS] as? String,
          let subnetMask = value[KEY_SUBNET_MASK] as? String,
          let defaultGateway = value[KEY_DEFAULT_GATEWAY] as? String,
          let proxyURL = value[KEY_PROXY_URL] as? String,
          let frequencyName = value[KEY_FREQUENCY] as? String,
          let frequency = getEnumValue(values: WlanFrequency.values(), name: frequencyName),
          let wlanSignalStrength = value[KEY_WLAN_SIGNAL_STRENGTH] as? Int,
          let wlanSignalLevel = value[KEY_WLAN_SIGNAL_LEVEL] as? Int,
          let lteSignalStrength = value[KEY_LTE_SIGNAL_STRENGTH] as? Int,
          let lteSignalLevel = value[KEY_LTE_SIGNAL_LEVEL] as? Int
    else {
        throw ThetaClientError.invalidArgument(AccessInfo.companion.keyName)
    }
    let dhcpLeaseAddress = try { () throws -> [DhcpLeaseAddress]? in
        if let list = value[KEY_LTE_SIGNAL_LEVEL] as? [[String: Any?]] {
            let array = try list.map { item in
                try toDhcpLeaseAddress(value: item)
            }
            if array.isEmpty {
                return nil
            }
            return array
        } else {
            return nil
        }
    }()
    return AccessInfo(
        ssid: ssid,
        ipAddress: ipAddress,
        subnetMask: subnetMask,
        defaultGateway: defaultGateway,
        proxyURL: proxyURL,
        frequency: frequency,
        wlanSignalStrength: Int32(wlanSignalStrength),
        wlanSignalLevel: Int32(),
        lteSignalStrength: Int32(lteSignalStrength),
        lteSignalLevel: Int32(lteSignalLevel),
        dhcpLeaseAddress: dhcpLeaseAddress
    )
}

func toGetOptionsParam(optionNames: [String]) -> [OptionName] {
    var result: [OptionName] = []
    let optionNameValues = OptionName.values()
    for name in optionNames {
        if let value = getEnumValue(values: optionNameValues, name: name) {
            result.append(value)
        }
    }
    return result
}

func fromTheta(thetaOptions: ThetaOptions) -> [String: Any?] {
    var result: [String: Any?] = [:]
    OptionName.entries.forEach { optionName in
        if let value = try? thetaOptions.getValue(name: optionName) {
            if optionName == .accessinfo, let accessInfo = value as? AccessInfo {
                result[optionName.keyName] = fromTheta(accessInfo: accessInfo)
            } else if let enumValue = value as? KotlinEnum<AnyObject> {
                result[optionName.keyName] = enumValue.name
            } else if let stringValue = value as? String {
                result[optionName.keyName] = stringValue
            }
        }
    }
    return result
}

func toSetOptionsParam(options: [String: Any]) throws -> ThetaOptions {
    var result = ThetaOptions()
    try options.forEach { key, value in
        if let optionName = OptionName.companion.getFromKeyName(keyName: key) {
            switch optionName {
            case OptionName.accessinfo:
                result.accessInfo = try toAccessInfo(value: value as? [String: Any?] ?? [:])
            case OptionName.camerapower:
                result.cameraPower = getEnumValue(values: CameraPower.values(), name: value)
            case OptionName.capturemode:
                result.captureMode = getEnumValue(values: CaptureMode.values(), name: value)
            case OptionName.networktype:
                result.networkType = getEnumValue(values: NetworkType.values(), name: value)
            case OptionName.password:
                result.password = value as? String
            case OptionName.username:
                result.username = value as? String
            case OptionName.wlanantennaconfig:
                result.wlanAntennaConfig = getEnumValue(values: WlanAntennaConfig.values(), name: value)
            case OptionName.wlanfrequency:
                result.wlanFrequency = getEnumValue(values: WlanFrequency.values(), name: value)
            case OptionName.wifipassword:
                result.wifiPassword = value as? String
            default:
                break
            }
        }
    }
    return result
}
