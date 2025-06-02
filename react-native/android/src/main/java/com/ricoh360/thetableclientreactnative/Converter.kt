package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.service.data.GpsInfo
import com.ricoh360.thetableclient.service.data.Proxy
import com.ricoh360.thetableclient.service.data.ble.PluginControl
import com.ricoh360.thetableclient.service.data.ble.PluginList
import com.ricoh360.thetableclient.service.data.ble.PluginOrders
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.CommandErrorDescription
import com.ricoh360.thetableclient.service.data.values.PluginPowerStatus
import com.ricoh360.thetableclient.service.data.values.ThetaModel
import java.lang.IllegalArgumentException

const val KEY_TIMEOUT_SCAN = "timeoutScan"
const val KEY_TIMEOUT_PERIPHERAL = "timeoutPeripheral"
const val KEY_TIMEOUT_CONNECT = "timeoutConnect"
const val KEY_TIMEOUT_TAKE_PICTURE = "timeoutTakePicture"

const val KEY_DEVICE_ID = "deviceId"
const val KEY_PARAMS = "params"
const val KEY_BATTERY_LEVEL = "batteryLevel"
const val KEY_PLUGIN = "plugin"
const val KEY_ERROR = "error"
const val KEY_MESSAGE = "message"
const val KEY_PLUGINS = "plugins"
const val KEY_FIRST = "first"
const val KEY_SECOND = "second"
const val KEY_THIRD = "third"
const val KEY_NAME = "name"
const val KEY_TIMEOUT = "timeout"
const val KEY_MANUFACTURER = "manufacturer"
const val KEY_SERIAL_NUMBER = "serialNumber"
const val KEY_WLAN_MAC_ADDRESS = "wlanMacAddress"
const val KEY_BLUETOOTH_MAC_ADDRESS = "bluetoothMacAddress"
const val KEY_FIRMWARE_VERSION = "firmwareVersion"
const val KEY_UPTIME = "uptime"
const val KEY_RECORDED_TIME = "recordedTime"
const val KEY_RECORDABLE_TIME = "recordableTime"
const val KEY_CAPTURED_PICTURES = "capturedPictures"
const val KEY_LATEST_FILE_URL = "latestFileUrl"
const val KEY_BATTERY_INSERT = "batteryInsert"
const val KEY_BOARD_TEMP = "boardTemp"
const val KEY_BATTERY_TEMP = "batteryTemp"
const val KEY_GPS_LAT = "lat"
const val KEY_GPS_LNG = "lng"
const val KEY_GPS_ALTITUDE = "altitude"
const val KEY_GPS_DATE_TIME_ZONE = "dateTimeZone"
const val KEY_GPS_DATUM = "datum"
const val KEY_EXTERNAL_GPS_INFO = "externalGpsInfo"
const val KEY_INTERNAL_GPS_INFO = "internalGpsInfo"
const val KEY_SSID = "ssid"
const val KEY_PASSWORD = "password"
const val KEY_USE = "use"
const val KEY_URL = "url"
const val KEY_PORT = "port"
const val KEY_USER_ID = "userid"

data class ScanParams(
  val name: String?,
  val timeout: ThetaBle.Timeout?,
)

fun toScanParams(objects: ReadableMap): ScanParams {
  val name = objects.getString(KEY_NAME)
  val timeout = objects.getMap(KEY_TIMEOUT)?.let { toThetaTimeout(it) }
  return ScanParams(name, timeout)
}

fun fromTheta(firstId: Int, deviceList: List<ThetaBle.ThetaDevice>): WritableArray {
  val result = Arguments.createArray()
  deviceList.forEachIndexed { index, element ->
    val item = Arguments.createMap()
    item.putInt(KEY_DEVICE_ID, firstId + index)
    item.putString(KEY_NAME, element.name)
    result.pushMap(item)
  }
  return result
}

data class ScanSsidParams(
  val model: ThetaModel?,
  val timeout: Int?,
)

fun fromTheta(ssidList: List<Pair<String, String>>): WritableArray {
  val result = Arguments.createArray()
  ssidList.forEach {
    val item = Arguments.createMap()
    item.putString(KEY_SSID, it.first)
    item.putString(KEY_PASSWORD, it.second)
    result.pushMap(item)
  }
  return result
}

fun toScanSsidParams(objects: ReadableMap): ScanSsidParams {
  val model = objects.getString(ThetaModel.keyName)?.let{ name ->
    ThetaModel.values().find { it.name == name }
  }
  val timeout = if (objects.hasKey(KEY_TIMEOUT)) objects.getInt(KEY_TIMEOUT) else null
  return ScanSsidParams(model, timeout)
}

fun toThetaTimeout(objects: ReadableMap): ThetaBle.Timeout {
  val timeout = ThetaBle.Timeout()
  if (objects.hasKey(KEY_TIMEOUT_SCAN)) {
    timeout.timeoutScan = objects.getInt(KEY_TIMEOUT_SCAN)
  }
  if (objects.hasKey(KEY_TIMEOUT_PERIPHERAL)) {
    timeout.timeoutPeripheral = objects.getInt(KEY_TIMEOUT_PERIPHERAL)
  }
  if (objects.hasKey(KEY_TIMEOUT_CONNECT)) {
    timeout.timeoutConnect = objects.getInt(KEY_TIMEOUT_CONNECT)
  }
  if (objects.hasKey(KEY_TIMEOUT_TAKE_PICTURE)) {
    timeout.timeoutTakePicture = objects.getInt(KEY_TIMEOUT_TAKE_PICTURE)
  }
  return timeout
}

fun toNotify(
  deviceId: Int,
  characteristic: BleCharacteristic,
  params: WritableMap?,
  error: Throwable?,
): WritableMap {
  val objects = Arguments.createMap()
  objects.putInt(KEY_DEVICE_ID, deviceId)
  objects.putString(BleCharacteristic.keyName, characteristic.name)
  error?.run {
    setNotifyError(objects, this)
    return objects
  }
  params?.run {
    objects.putMap(KEY_PARAMS, params)
  }
  return objects
}

fun toNotify(
  deviceId: Int,
  characteristic: BleCharacteristic,
  params: WritableArray?,
  error: Throwable?,
): WritableMap {
  val objects = Arguments.createMap()
  objects.putInt(KEY_DEVICE_ID, deviceId)
  objects.putString(BleCharacteristic.keyName, characteristic.name)
  error?.run {
    setNotifyError(objects, this)
    return objects
  }
  params?.run {
    objects.putArray(KEY_PARAMS, params)
  }
  return objects
}

fun setNotifyError(objects: WritableMap, error: Throwable) {
  val result = Arguments.createMap()
  result.putString(KEY_MESSAGE, error.message)
  result.putString(KEY_PARAMS, error.toString())
  objects.putMap(KEY_ERROR, result)
}

fun toBatteryLevelNotifyParam(value: Int): WritableMap {
  val result = Arguments.createMap()
  result.putInt(KEY_BATTERY_LEVEL, value)
  return result
}

fun toBatteryStatusNotifyParam(value: ChargingState): WritableMap {
  val result = Arguments.createMap()
  result.putString(ChargingState.keyName, value.name)
  return result
}

fun toCameraPowerNotifyParam(value: CameraPower): WritableMap {
  val result = Arguments.createMap()
  result.putString(CameraPower.keyName, value.name)
  return result
}

fun toCommandErrorDescriptionNotifyParam(value: CommandErrorDescription): WritableMap {
  val result = Arguments.createMap()
  result.putString(CommandErrorDescription.keyName, value.name)
  return result
}

fun fromTheta(value: PluginControl): WritableMap {
  val result = Arguments.createMap()
  result.putString(PluginControl.keyName, value.pluginControl.name)
  value.plugin?.run {
    result.putInt(KEY_PLUGIN, this)
  }
  return result
}

fun toThetaPluginControl(objects: ReadableMap): PluginControl? {
  if (!objects.hasKey(PluginControl.keyName)) {
    return null
  }
  val pluginControl = objects.getString(PluginPowerStatus.keyName)?.let { name ->
    PluginPowerStatus.values().find { it.name == name }
  } ?: run {
    return null
  }
  return PluginControl(
    pluginControl = pluginControl,
    plugin = if (objects.hasKey(KEY_PLUGIN)) objects.getInt(KEY_PLUGIN) else null,
  )
}

fun fromTheta(value: PluginList): WritableMap {
  val result = Arguments.createMap()
  val array = Arguments.createArray()
  value.plugins.forEach {
    array.pushInt(it)
  }
  result.putArray(KEY_PLUGINS, array)
  return result
}

fun fromTheta(value: PluginOrders): WritableMap {
  val result = Arguments.createMap()
  result.putInt(KEY_FIRST, value.first)
  result.putInt(KEY_SECOND, value.second)
  result.putInt(KEY_THIRD, value.third)
  return result
}

fun toThetaPluginOrders(objects: ReadableMap): PluginOrders {
  return PluginOrders(
    first = objects.getInt(KEY_FIRST),
    second = objects.getInt(KEY_SECOND),
    third = objects.getInt(KEY_THIRD),
  )
}

fun fromTheta(value: GpsInfo): WritableMap {
  val result = Arguments.createMap()
  value.lat?.let {
    result.putDouble(KEY_GPS_LAT, it.toDouble())
  }
  value.lng?.let {
    result.putDouble(KEY_GPS_LNG, it.toDouble())
  }
  value.altitude?.let {
    result.putDouble(KEY_GPS_ALTITUDE, it.toDouble())
  }
  value.dateTimeZone?.let {
    result.putString(KEY_GPS_DATE_TIME_ZONE, it)
  }
  value.datum?.let {
    result.putString(KEY_GPS_DATUM, it)
  }
  return result
}

fun toProxy(objects: ReadableMap): Proxy {
  val use = if (objects.hasKey(KEY_USE)) {
    objects.getBoolean(KEY_USE)
  } else {
    throw IllegalArgumentException(KEY_USE)
  }
  val url = objects.getString(KEY_URL)
  val port = if (objects.hasKey(KEY_PORT)) {
    objects.getInt(KEY_PORT)
  } else {
    null
  }
  val userid = objects.getString(KEY_USER_ID)
  val password = objects.getString(KEY_PASSWORD)

  return Proxy(
    use = use,
    url = url,
    port = port,
    userid = userid,
    password = password,
  )
}
