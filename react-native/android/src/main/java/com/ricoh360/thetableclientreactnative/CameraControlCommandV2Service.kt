package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.service.data.GpsInfo
import com.ricoh360.thetableclient.service.data.ThetaInfo
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.ThetaState2
import com.ricoh360.thetableclient.service.data.values.CameraError
import com.ricoh360.thetableclient.service.data.values.CaptureStatus
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.ShootingFunction
import com.ricoh360.thetableclient.service.data.values.ThetaModel

object CameraControlCommandV2Service {

  suspend fun getInfo(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      if (device == null) {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraControlCommandV2
      if (service == null) {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getInfo()
      promise.resolve(fromTheta(value))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getState(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      if (device == null) {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraControlCommandV2
      if (service == null) {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getState()
      promise.resolve(fromTheta(value))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setStateNotify(
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
      val service = device.cameraControlCommandV2
      if (service == null) {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }

      if (enable) {
        service.setStateNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.NOTIFY_STATE,
              value?.let { fromTheta(it) },
              error,
            ),
          )
        }
      } else {
        service.setStateNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getState2(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      if (device == null) {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraControlCommandV2
      if (service == null) {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getState2()
      promise.resolve(fromTheta(value))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}

fun fromTheta(value: ThetaInfo): WritableMap {
  val result = Arguments.createMap()
  result.putString(KEY_MANUFACTURER, value.manufacturer)
  result.putString(ThetaModel.keyName, value.model.name)
  result.putString(KEY_SERIAL_NUMBER, value.serialNumber)
  value.wlanMacAddress?.let {
    result.putString(KEY_WLAN_MAC_ADDRESS, it)
  }
  value.bluetoothMacAddress?.let {
    result.putString(KEY_BLUETOOTH_MAC_ADDRESS, it)
  }
  result.putString(KEY_FIRMWARE_VERSION, value.firmwareVersion)
  result.putInt(KEY_UPTIME, value.uptime)
  return result
}

fun fromTheta(value: ThetaState): WritableMap {
  val result = Arguments.createMap()
  value.batteryLevel?.let {
    result.putDouble(KEY_BATTERY_LEVEL, it.toDouble())
  }
  value.captureStatus?.let {
    result.putString(CaptureStatus.keyName, it.name)
  }
  value.recordedTime?.let {
    result.putInt(KEY_RECORDED_TIME, it)
  }
  value.recordableTime?.let {
    result.putInt(KEY_RECORDABLE_TIME, it)
  }
  value.capturedPictures?.let {
    result.putInt(KEY_CAPTURED_PICTURES, it)
  }
  value.latestFileUrl?.let {
    result.putString(KEY_LATEST_FILE_URL, it)
  }
  value.batteryState?.let {
    result.putString(ChargingState.keyName, it.name)
  }
  value.function?.let {
    result.putString(ShootingFunction.keyName, it.name)
  }
  value.cameraError?.let {
    val array = Arguments.createArray()
    it.forEach { cameraError ->
      array.pushString(cameraError.name)
    }
    result.putArray(CameraError.keyName, array)
  }
  value.batteryInsert?.let {
    result.putBoolean(KEY_BATTERY_INSERT, it)
  }
  value.boardTemp?.let {
    result.putInt(KEY_BOARD_TEMP, it)
  }
  value.batteryTemp?.let {
    result.putInt(KEY_BATTERY_TEMP, it)
  }

  return result
}

fun fromTheta(value: ThetaState2): WritableMap {
  val result = Arguments.createMap()
  value.externalGpsInfo?.let {
    it.gpsInfo?.let { gpsInfo ->
      val gpsInfoMap = fromTheta(gpsInfo)
      val stateGpsMap = Arguments.createMap()
      stateGpsMap.putMap(GpsInfo.keyName, gpsInfoMap)
      result.putMap(KEY_EXTERNAL_GPS_INFO, stateGpsMap)
    }
  }
  value.internalGpsInfo?.let {
    it.gpsInfo?.let { gpsInfo ->
      val gpsInfoMap = fromTheta(gpsInfo)
      val stateGpsMap = Arguments.createMap()
      stateGpsMap.putMap(GpsInfo.keyName, gpsInfoMap)
      result.putMap(KEY_INTERNAL_GPS_INFO, stateGpsMap)
    }
  }
  return result
}
