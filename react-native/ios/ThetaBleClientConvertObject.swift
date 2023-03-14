//
//  ThetaBleClientConvertEnum.swift
//  ThetaBleClientReactNative
//
//  Created on 2023/03/20.
//

import Foundation
import THETABleClient

let KEY_TIMEOUT_SCAN = "timeoutScan"
let KEY_TIMEOUT_PERIPHERAL = "timeoutPeripheral"
let KEY_TIMEOUT_CONNECT = "timeoutConnect"
let KEY_TIMEOUT_TAKE_PICTURE = "timeoutTakePicture"

let KEY_DEVICE_ID = "deviceId"
let KEY_PARAMS = "params"
let KEY_BATTERY_LEVEL = "batteryLevel"
let KEY_PLUGIN = "plugin"
let KEY_ERROR = "error"
let KEY_MESSAGE = "message"
let KEY_PLUGINS = "plugins"
let KEY_FIRST = "first"
let KEY_SECOND = "second"
let KEY_THIRD = "third"
let KEY_NAME = "name"
let KEY_UUID = "uuid"
let KEY_TIMEOUT = "timeout"
let KEY_MANUFACTURER = "manufacturer"
let KEY_SERIAL_NUMBER = "serialNumber"
let KEY_WLAN_MAC_ADDRESS = "wlanMacAddress"
let KEY_BLUETOOTH_MAC_ADDRESS = "bluetoothMacAddress"
let KEY_FIRMWARE_VERSION = "firmwareVersion"
let KEY_UPTIME = "uptime"
let KEY_RECORDED_TIME = "recordedTime"
let KEY_RECORDABLE_TIME = "recordableTime"
let KEY_CAPTURED_PICTURES = "capturedPictures"
let KEY_LATEST_FILE_URL = "latestFileUrl"
let KEY_BATTERY_INSERT = "batteryInsert"
let KEY_BOARD_TEMP = "boardTemp"
let KEY_BATTERY_TEMP = "batteryTemp"
let KEY_GPS_LAT = "lat"
let KEY_GPS_LNG = "lng"
let KEY_GPS_ALTITUDE = "altitude"
let KEY_GPS_DATE_TIME_ZONE = "dateTimeZone"
let KEY_GPS_DATUM = "datum"
let KEY_EXTERNAL_GPS_INFO = "externalGpsInfo"
let KEY_INTERNAL_GPS_INFO = "internalGpsInfo"
let MESSAGE_UNKNOWN = "unknown"

class ScanParams {
    let name: String
    let timeout: ThetaBle.Timeout?
    init(name: String, timeout: ThetaBle.Timeout?) {
        self.name = name
        self.timeout = timeout
    }
}

func toScanParams(params: [String: Any?]) -> ScanParams {
    let name = params[KEY_NAME] as? String ?? ""
    let timeout = toTheta(timeout:params[KEY_TIMEOUT] as? [String : Any?] ?? [:])
    return ScanParams(name: name, timeout: timeout)
}

func toTheta(timeout: [String: Any?]) -> ThetaBle.Timeout {
    let timeoutObject = ThetaBle.Timeout()
    if let timeoutScan = timeout[KEY_TIMEOUT_SCAN] as? Int {
        timeoutObject.timeoutScan = Int32(timeoutScan)
    }
    if let timeoutPeripheral = timeout[KEY_TIMEOUT_PERIPHERAL] as? Int {
        timeoutObject.timeoutPeripheral = Int32(timeoutPeripheral)
    }
    if let timeoutConnect = timeout[KEY_TIMEOUT_CONNECT] as? Int {
        timeoutObject.timeoutConnect = Int32(timeoutConnect)
    }
    if let timeoutTakePicture = timeout[KEY_TIMEOUT_TAKE_PICTURE] as? Int {
        timeoutObject.timeoutTakePicture = Int32(timeoutTakePicture)
    }

    return timeoutObject
}

func toNotify(
    deviceId: Int,
    characteristic: BleCharacteristic,
    params: [String: Any?]?,
    error: [String: Any?]?
) -> [String: Any] {
    var result = [
        KEY_DEVICE_ID: deviceId,
        BleCharacteristic.companion.keyName: characteristic.name,
    ] as [String: Any]
    if let error = error {
        result[KEY_ERROR] = error
        return result
    }
    if let params = params {
        result[KEY_PARAMS] = params
        return result
    }
    return result
}

func toNotifyError(error: KotlinThrowable?) -> [String: Any]? {
    guard let error = error else {
        return nil
    }
    return [
        KEY_MESSAGE: error.message ?? MESSAGE_UNKNOWN,
        KEY_PARAMS: String(describing: type(of: error))
    ]
}

func toBatteryLevelNotify(deviceId: Int, value: KotlinInt?, error: KotlinThrowable?) -> [String: Any] {
    let paramsInfo = (value != nil) ? [KEY_BATTERY_LEVEL: value?.intValue] : nil
    let errorInfo = toNotifyError(error: error)
    return toNotify(deviceId: deviceId,
                    characteristic: BleCharacteristic.batteryLevel,
                    params: paramsInfo,
                    error: errorInfo
    )
}

func toBatteryStatusNotify(
    deviceId: Int,
    value: ChargingState?,
    error: KotlinThrowable?
) -> [String: Any] {
    let paramsInfo = (value != nil) ? [ChargingState.companion.keyName: value?.name] : nil
    let errorInfo = toNotifyError(error: error)
    return toNotify(deviceId: deviceId,
                    characteristic: BleCharacteristic.batteryStatus,
                    params: paramsInfo,
                    error: errorInfo
    )
}

func toCameraPowerNotify(
    deviceId: Int,
    value: CameraPower?,
    error: KotlinThrowable?
) -> [String: Any] {
    let paramsInfo = (value != nil) ? [CameraPower.companion.keyName: value?.name] : nil
    let errorInfo = toNotifyError(error: error)
    return toNotify(deviceId: deviceId,
                    characteristic: BleCharacteristic.cameraPower,
                    params: paramsInfo,
                    error: errorInfo
    )
}

func toCommandErrorDescriptionNotify(
    deviceId: Int,
    value: CommandErrorDescription?,
    error: KotlinThrowable?
) -> [String: Any] {
    let paramsInfo = (value != nil) ? [CommandErrorDescription.companion.keyName: value?.name] : nil
    let errorInfo = toNotifyError(error: error)
    return toNotify(deviceId: deviceId,
                    characteristic: BleCharacteristic.commandErrorDescription,
                    params: paramsInfo,
                    error: errorInfo
    )
}

func fromTheta(pluginControl: PluginControl) -> [String: Any?] {
    var result: [String: Any?] = [
        PluginControl.companion.keyName: pluginControl.pluginControl.name,
    ]
    if let plugin = pluginControl.plugin {
        result[KEY_PLUGIN] = plugin.intValue
    }
    return result
}

func toTheta(pluginControl: [String: Any?]) -> PluginControl? {
    let pluginControlEnum = getEnumValue(
        values: PluginPowerStatus.values(),
        name: pluginControl[PluginPowerStatus.companion.keyName] as? String ?? ""
    )
    guard let pluginControlEnum = pluginControlEnum else {
        return nil
    }
    if let plugin = pluginControl[KEY_PLUGIN] as? Int {
        return PluginControl(
            pluginControl: pluginControlEnum,
            plugin: KotlinInt(int: Int32(plugin))
        )
    } else {
        return PluginControl(
            pluginControl: pluginControlEnum,
            plugin: nil
        )
    }
}

func toPluginControlNotify(
    deviceId: Int,
    value: PluginControl?,
    error: KotlinThrowable?
) -> [String: Any] {
    let paramsInfo = (value != nil) ? fromTheta(pluginControl: value!) : nil
    let errorInfo = toNotifyError(error: error)
    return toNotify(deviceId: deviceId,
                    characteristic: BleCharacteristic.pluginControl,
                    params: paramsInfo,
                    error: errorInfo
    )
}

func fromTheta(pluginList: PluginList) -> [String: Any?] {
    let plugins = pluginList.plugins.map { value in
        value.intValue
    }
    return [
        KEY_PLUGINS: plugins,
    ]
}

func fromTheta(pluginOrders: PluginOrders) -> [String: Any?] {
    return [
        KEY_FIRST: Int(pluginOrders.first),
        KEY_SECOND: Int(pluginOrders.second),
        KEY_THIRD: Int(pluginOrders.third),
    ]
}

func toTheta(pluginOrders: [String: Any?]) -> PluginOrders? {
    guard let first = pluginOrders[KEY_FIRST] as? Int else {
        return nil
    }
    guard let second = pluginOrders[KEY_SECOND] as? Int else {
        return nil
    }
    guard let third = pluginOrders[KEY_THIRD] as? Int else {
        return nil
    }
    return PluginOrders(first: Int32(first), second: Int32(second), third: Int32(third))
}

func fromTheta(gpsInfo: GpsInfo) -> [String: Any?] {
    var result: [String: Any?] = [:]
    if let lat = gpsInfo.lat {
        result[KEY_GPS_LAT] = lat.floatValue
    }
    if let lng = gpsInfo.lng {
        result[KEY_GPS_LNG] = lng.floatValue
    }
    if let altitude = gpsInfo.altitude {
        result[KEY_GPS_ALTITUDE] = altitude.floatValue
    }
    if let dateTimeZone = gpsInfo.dateTimeZone {
        result[KEY_GPS_DATE_TIME_ZONE] = dateTimeZone
    }
    if let datum = gpsInfo.datum {
        result[KEY_GPS_DATUM] = datum
    }
    return result
}
