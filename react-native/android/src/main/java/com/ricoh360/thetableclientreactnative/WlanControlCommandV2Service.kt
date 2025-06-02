package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.service.WlanControlCommandV2
import com.ricoh360.thetableclient.service.data.ConnectedInfo
import com.ricoh360.thetableclient.service.data.ConnectedWifiInfo
import com.ricoh360.thetableclient.service.data.Proxy
import com.ricoh360.thetableclient.service.data.values.NetworkType
import com.ricoh360.thetableclient.service.data.values.WifiSecurityMode
import java.lang.IllegalArgumentException

const val KEY_WIFI_INFO = "wifiInfo"
const val KEY_ETHERNET = "ethernet"
const val KEY_LTE = "lte"
const val KEY_IS_CONNECTED = "isConnected"
const val KEY_IS_INTERNET_ACCESSIBLE = "isInternetAccessible"
const val KEY_SSID_LIST = "ssidList"
const val KEY_SSID_STEALTH = "ssidStealth"
const val KEY_CONNECTION_PRIORITY = "connectionPriority"
const val KEY_IP_ADDRESS = "ipAddress"
const val KEY_SUBNET_MASK = "subnetMask"
const val KEY_DEFAULT_GATEWAY = "defaultGateway"

object WlanControlCommandV2Service {
  suspend fun setNetworkType(id: Int, value: String, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val enumValue = NetworkType.entries.find {
        it.name == value
      }
      if (enumValue == null) {
        promise.reject(Exception("Network type not found. $value"))
      } else {
        service.setNetworkType(enumValue)
        promise.resolve(null)
      }
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setNetworkTypeNotify(
    id: Int,
    enable: Boolean,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      if (device == null) {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      if (service == null) {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }

      if (enable) {
        service.setNetworkTypeNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.WRITE_SET_NETWORK_TYPE,
              value?.let {
                val map = Arguments.createMap()
                map.putString(NetworkType.keyName, it.name)
                map
              },
              error,
            ),
          )
        }
      } else {
        service.setNetworkTypeNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getConnectedWifiInfo(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      if (device == null) {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      if (service == null) {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getConnectedWifiInfo()
      promise.resolve(fromTheta(value))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setConnectedWifiInfoNotify(
    id: Int,
    enable: Boolean,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      if (device == null) {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      if (service == null) {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }

      if (enable) {
        service.setConnectedWifiInfoNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO,
              value?.let {
                fromTheta(it)
              },
              error,
            ),
          )
        }
      } else {
        service.setConnectedWifiInfoNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun scanSsidStart(
    id: Int,
    timeout: Int,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      service.scanSsidStart(timeout, object : WlanControlCommandV2.ScanCallback {
        override fun onCompleted(ssidList: List<String>) {
          super.onCompleted(ssidList)
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.NOTIFICATION_SCANNED_SSID,
              fromTheta(null, ssidList),
              null,
            ),
          )
        }

        override fun onNotify(ssid: String) {
          super.onNotify(ssid)
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.NOTIFICATION_SCANNED_SSID,
              fromTheta(ssid, null),
              null,
            ),
          )
        }
      })
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun scanSsidStop(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      service.scanSsidStop()
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun setAccessPointDynamically(id: Int, params: ReadableMap, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val values = toSetAccessPointParams(params)
      service.setAccessPointDynamically(
        ssid = values.ssid,
        ssidStealth = values.ssidStealth,
        security = values.security,
        password = values.password,
        connectionPriority = values.connectionPriority,
        proxy = values.proxy,
      )
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun setAccessPointStatically(id: Int, params: ReadableMap, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommandV2
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val values = toSetAccessPointParams(params)
      service.setAccessPointStatically(
        ssid = values.ssid,
        ssidStealth = values.ssidStealth,
        security = values.security,
        password = values.password,
        connectionPriority = values.connectionPriority,
        ipAddress = values.ipAddress ?: throw IllegalArgumentException(KEY_IP_ADDRESS),
        subnetMask = values.subnetMask ?: throw IllegalArgumentException(KEY_SUBNET_MASK),
        defaultGateway = values.defaultGateway
          ?: throw IllegalArgumentException(KEY_DEFAULT_GATEWAY),
        proxy = values.proxy,
      )
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}

fun fromTheta(value: ConnectedWifiInfo): WritableMap {
  val result = Arguments.createMap()
  value.wifiInfo?.let {
    result.putMap(KEY_WIFI_INFO, fromTheta(it))
  }
  value.ethernet?.let {
    result.putMap(KEY_ETHERNET, fromTheta(it))
  }
  value.lte?.let {
    result.putMap(KEY_LTE, fromTheta(it))
  }
  return result
}

fun fromTheta(value: ConnectedInfo): WritableMap {
  val result = Arguments.createMap()
  result.putString(KEY_SSID, value.ssid)
  result.putBoolean(KEY_IS_CONNECTED, value.isConnected)
  result.putBoolean(KEY_IS_INTERNET_ACCESSIBLE, value.isInternetAccessible)
  return result
}

fun fromTheta(ssid: String?, ssidList: List<String>?): WritableMap {
  val result = Arguments.createMap()
  ssid?.let {
    result.putString(KEY_SSID, it)
  }
  ssidList?.let {
    val list = Arguments.fromList(it)
    result.putArray(KEY_SSID_LIST, list)
  }
  return result
}

data class SetAccessPointParams(
  val ssid: String,
  val ssidStealth: Boolean,
  val security: WifiSecurityMode,
  val password: String,
  val connectionPriority: Int,
  val ipAddress: String? = null,
  val subnetMask: String? = null,
  val defaultGateway: String? = null,
  val proxy: Proxy? = null,
)

fun toSetAccessPointParams(objects: ReadableMap): SetAccessPointParams {
  val ssid = objects.getString(KEY_SSID) ?: throw IllegalArgumentException(KEY_SSID)
  val ssidStealth = if (objects.hasKey(KEY_SSID_STEALTH)) {
    objects.getBoolean(KEY_SSID_STEALTH)
  } else {
    throw IllegalArgumentException(KEY_SSID_STEALTH)
  }
  val security =
    objects.getString(WifiSecurityMode.keyName)?.let { value ->
      WifiSecurityMode.entries.find {
        it.name == value
      }
    }
      ?: throw IllegalArgumentException(WifiSecurityMode.keyName)
  val password = objects.getString(KEY_PASSWORD) ?: throw IllegalArgumentException(KEY_PASSWORD)
  val connectionPriority = if (objects.hasKey(KEY_CONNECTION_PRIORITY)) {
    objects.getInt(KEY_CONNECTION_PRIORITY)
  } else {
    throw IllegalArgumentException(KEY_CONNECTION_PRIORITY)
  }
  val ipAddress = objects.getString(KEY_IP_ADDRESS)
  val subnetMask = objects.getString(KEY_SUBNET_MASK)
  val defaultGateway = objects.getString(KEY_DEFAULT_GATEWAY)
  val proxy = objects.getMap(Proxy.keyName)?.let { toProxy(it) }

  return SetAccessPointParams(
    ssid = ssid,
    ssidStealth = ssidStealth,
    security = security,
    password = password,
    connectionPriority = connectionPriority,
    ipAddress = ipAddress,
    subnetMask = subnetMask,
    defaultGateway = defaultGateway,
    proxy = proxy,
  )
}
