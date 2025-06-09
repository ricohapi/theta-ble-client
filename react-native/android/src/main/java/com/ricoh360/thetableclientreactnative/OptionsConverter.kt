package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.ricoh360.thetableclient.service.data.AccessInfo
import com.ricoh360.thetableclient.service.data.DhcpLeaseAddress
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.NetworkType
import com.ricoh360.thetableclient.service.data.values.WlanAntennaConfig
import com.ricoh360.thetableclient.service.data.values.WlanFrequency
import com.ricoh360.thetableclient.service.data.values.OptionName
import java.lang.IllegalArgumentException

const val KEY_PROXY_URL = "proxyURL"
const val KEY_FREQUENCY = "frequency"
const val KEY_WLAN_SIGNAL_STRENGTH = "wlanSignalStrength"
const val KEY_WLAN_SIGNAL_LEVEL = "wlanSignalLevel"
const val KEY_LTE_SIGNAL_STRENGTH = "lteSignalStrength"
const val KEY_LTE_SIGNAL_LEVEL = "lteSignalLevel"
const val KEY_HOST_NAME = "hostName"

fun toGetOptionsParam(optionNames: ReadableArray): List<OptionName> {
  val optionNameList = mutableListOf<OptionName>()
  for (index in 0..<optionNames.size()) {
    val option = optionNames.getString(index)
    optionNameList.add(OptionName.valueOf(option))
  }
  return optionNameList
}

fun toGetOptionsByStringParam(optionNames: ReadableArray): List<String> {
  val optionNameList = mutableListOf<String>()
  for (index in 0..<optionNames.size()) {
    optionNameList.add(optionNames.getString(index))
  }
  return optionNameList
}

fun <T : Enum<T>> addOptionsEnumToMap(options: ThetaOptions, name: OptionName, objects: WritableMap) {
  options.getValue<T>(name)?.let { value ->
    objects.putString(name.keyName, value.name)
  }
}

fun addOptionsValueToMap(options: ThetaOptions, name: OptionName, objects: WritableMap) {
  options.getValue<Any>(name)?.let { value ->
    when (value) {
      is String -> {
        objects.putString(name.keyName, value)
      }

      is Int -> {
        objects.putInt(name.keyName, value)
      }

      is Number -> {
        objects.putDouble(name.keyName, value.toDouble())
      }

      else -> {
        objects.putString(name.keyName, value.toString())
      }
    }
  }
}

fun fromTheta(accessInfo: AccessInfo): WritableMap {
  val result = Arguments.createMap()
  result.putString(KEY_SSID, accessInfo.ssid)
  result.putString(KEY_IP_ADDRESS, accessInfo.ipAddress)
  result.putString(KEY_SUBNET_MASK, accessInfo.subnetMask)
  result.putString(KEY_DEFAULT_GATEWAY, accessInfo.defaultGateway)
  result.putString(KEY_PROXY_URL, accessInfo.proxyURL)
  result.putString(KEY_FREQUENCY, accessInfo.frequency.name)
  result.putInt(KEY_WLAN_SIGNAL_STRENGTH, accessInfo.wlanSignalStrength)
  result.putInt(KEY_WLAN_SIGNAL_LEVEL, accessInfo.wlanSignalLevel)
  result.putInt(KEY_LTE_SIGNAL_STRENGTH, accessInfo.lteSignalStrength)
  result.putInt(KEY_LTE_SIGNAL_LEVEL, accessInfo.lteSignalLevel)
  accessInfo.dhcpLeaseAddress?.let { list ->
    val array = Arguments.createArray()
    list.forEach {
      array.pushMap(fromTheta(it))
    }
    if (array.size() > 0) {
      result.putArray(DhcpLeaseAddress.keyName, array)
    }
  }
  return result
}

fun toAccessInfo(value: ReadableMap): AccessInfo {
  val ssid = value.getString(KEY_SSID)
  val ipAddress = value.getString(KEY_IP_ADDRESS)
  val subnetMask = value.getString(KEY_SUBNET_MASK)
  val defaultGateway = value.getString(KEY_DEFAULT_GATEWAY)
  val proxyURL = value.getString(KEY_PROXY_URL)
  val frequency = value.getString(KEY_FREQUENCY)?.let { value -> WlanFrequency.entries.find { it.name == value } }
  val wlanSignalStrength = value.getInt(KEY_WLAN_SIGNAL_STRENGTH)
  val wlanSignalLevel = value.getInt(KEY_WLAN_SIGNAL_LEVEL)
  val lteSignalStrength = value.getInt(KEY_LTE_SIGNAL_STRENGTH)
  val lteSignalLevel = value.getInt(KEY_LTE_SIGNAL_LEVEL)
  val dhcpLeaseAddress = value.getArray(DhcpLeaseAddress.keyName)?.let { array ->
    val list = mutableListOf<DhcpLeaseAddress>()
    for (i in 0 until array.size()) {
      val map = array.getMap(i)
      list.add(toDhcpLeaseAddress(map))
    }
    if (list.size > 0) list else null
  }
  if (ssid == null || ipAddress == null || subnetMask == null || defaultGateway == null || proxyURL == null || frequency == null) {
    throw IllegalArgumentException(AccessInfo.keyName)
  }
  return AccessInfo(
    ssid = ssid,
    ipAddress = ipAddress,
    subnetMask = subnetMask,
    defaultGateway = defaultGateway,
    proxyURL = proxyURL,
    frequency = frequency,
    wlanSignalStrength = wlanSignalStrength,
    wlanSignalLevel = wlanSignalLevel,
    lteSignalStrength = lteSignalStrength,
    lteSignalLevel = lteSignalLevel,
    dhcpLeaseAddress = dhcpLeaseAddress,
  )
}

fun fromTheta(dhcpLeaseAddress: DhcpLeaseAddress): WritableMap {
  val result = Arguments.createMap()
  result.putString(KEY_IP_ADDRESS, dhcpLeaseAddress.ipAddress)
  result.putString(KEY_MAC_ADDRESS, dhcpLeaseAddress.macAddress)
  result.putString(KEY_HOST_NAME, dhcpLeaseAddress.hostName)
  return result
}

fun toDhcpLeaseAddress(value: ReadableMap): DhcpLeaseAddress {
  val ipAddress = value.getString(KEY_IP_ADDRESS)
  val macAddress = value.getString(KEY_MAC_ADDRESS)
  val hostName = value.getString(KEY_HOST_NAME)
  if (ipAddress == null || macAddress == null || hostName == null) {
    throw IllegalArgumentException(DhcpLeaseAddress.keyName)
  }
  return DhcpLeaseAddress(ipAddress, macAddress, hostName)
}

fun fromTheta(options: ThetaOptions): WritableMap {
  val valueOptions = listOf(
    OptionName.DefaultWifiPassword,
    OptionName.Ssid
  )
  val result = Arguments.createMap()
  OptionName.entries.forEach {
    when {
      it == OptionName.AccessInfo -> {
        options.accessInfo?.let { value ->
          result.putMap(it.keyName, fromTheta(value))
        }
      }

      valueOptions.contains(it) -> addOptionsValueToMap(options, it, result)
      // enum value
      else -> addOptionsEnumToMap(options, it, result)
    }
  }
  return result
}

fun toSetOptionsParam(optionsMap: ReadableMap): ThetaOptions {
  val result = ThetaOptions()
  optionsMap.toHashMap().forEach { (key, value) ->
    OptionName.getFromKeyName(key)?.let { name ->
      when (name) {
        OptionName.AccessInfo -> result.accessInfo = toAccessInfo(value as ReadableMap)
        OptionName.CameraPower -> result.cameraPower = CameraPower.entries.find { it.name == value }
        OptionName.CaptureMode -> result.captureMode = CaptureMode.entries.find { it.name == value }
        OptionName.DefaultWifiPassword -> result.defaultWifiPassword = value as? String
        OptionName.NetworkType -> result.networkType = NetworkType.entries.find { it.name == value }
        OptionName.Password -> result.password = value as? String
        OptionName.Ssid -> result.ssid = value as? String
        OptionName.Username -> result.username = value as? String
        OptionName.WlanAntennaConfig -> result.wlanAntennaConfig = WlanAntennaConfig.entries.find { it.name == value }
        OptionName.WlanFrequency -> result.wlanFrequency = WlanFrequency.entries.find { it.name == value }
        else -> {}
      }
    }
  }
  return result
}

fun convertMapToWritableMap(map: Map<String, Any>): WritableMap {
  val writableMap = Arguments.createMap()
  for ((key, value) in map) {
    when (value) {
      is String -> writableMap.putString(key, value)
      is Int -> writableMap.putInt(key, value)
      is Boolean -> writableMap.putBoolean(key, value)
      is Long -> writableMap.putDouble(key, value.toDouble())
      is Double -> writableMap.putDouble(key, value)
      is Map<*, *> -> writableMap.putMap(key, convertMapToWritableMap(value as Map<String, Any>))
      is List<*> -> writableMap.putArray(key, convertListToWritableArray(value))
      else -> writableMap.putString(key, value.toString())
    }
  }
  return writableMap
}

fun convertListToWritableArray(list: List<*>): WritableArray {
  val writableArray = Arguments.createArray()
  for (value in list) {
    when (value) {
      is String -> writableArray.pushString(value)
      is Int -> writableArray.pushInt(value)
      is Boolean -> writableArray.pushBoolean(value)
      is Long -> writableArray.pushDouble(value.toDouble())
      is Double -> writableArray.pushDouble(value)
      is Map<*, *> -> writableArray.pushMap(convertMapToWritableMap(value as Map<String, Any>))
      is List<*> -> writableArray.pushArray(convertListToWritableArray(value))
      else -> writableArray.pushString(value.toString())
    }
  }
  return writableArray
}
