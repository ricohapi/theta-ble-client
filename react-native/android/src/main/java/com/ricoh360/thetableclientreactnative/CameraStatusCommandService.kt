package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.service.data.values.CameraPower

object CameraStatusCommandService {
  suspend fun getBatteryLevel(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getBatteryLevel()
      promise.resolve(value)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setBatteryLevelNotify(
    id: Int,
    enable: Boolean,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      if (enable) {
        service.setBatteryLevelNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.BATTERY_LEVEL,
              value?.let { toBatteryLevelNotifyParam(it) },
              error,
            ),
          )
        }
      } else {
        service.setBatteryLevelNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getBatteryStatus(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getBatteryStatus()
      promise.resolve(value.name)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setBatteryStatusNotify(
    id: Int,
    enable: Boolean,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      if (enable) {
        service.setBatteryStatusNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.BATTERY_STATUS,
              value?.let { toBatteryStatusNotifyParam(it) },
              error,
            ),
          )
        }
      } else {
        service.setBatteryStatusNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getCameraPower(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getCameraPower()
      promise.resolve(value.name)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun setCameraPower(id: Int, value: String, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val enumValue = CameraPower.values().find {
        it.name == value
      }
      if (enumValue == null) {
        promise.reject(Exception("Camera power not found. $value"))
      } else {
        service.setCameraPower(enumValue)
        promise.resolve(null)
      }
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setCameraPowerNotify(
    id: Int,
    enable: Boolean,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      if (enable) {
        service.setCameraPowerNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.CAMERA_POWER,
              value?.let { toCameraPowerNotifyParam(it) },
              error,
            ),
          )
        }
      } else {
        service.setCameraPowerNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setCommandErrorDescriptionNotify(
    id: Int,
    enable: Boolean,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      if (enable) {
        service.setCommandErrorDescriptionNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.COMMAND_ERROR_DESCRIPTION,
              value?.let { toCommandErrorDescriptionNotifyParam(it) },
              error,
            ),
          )
        }
      } else {
        service.setCommandErrorDescriptionNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getPluginControl(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getPluginControl()
      promise.resolve(fromTheta(value))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun setPluginControl(id: Int, value: ReadableMap, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      toThetaPluginControl(value)?.run {
        service.setPluginControl(this)
        promise.resolve(null)
      } ?: run {
        promise.reject(Exception("Plugin control not found. $value"))
      }
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun setPluginControlNotify(
    id: Int,
    enable: Boolean,
    promise: Promise,
    sendNotifyEvent: (WritableMap) -> Unit,
  ) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraStatusCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      if (enable) {
        service.setPluginControlNotify { value, error ->
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.PLUGIN_CONTROL,
              value?.let { fromTheta(it) },
              error,
            ),
          )
        }
      } else {
        service.setPluginControlNotify(null)
      }
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}
