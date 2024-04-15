package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Promise
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.FileFormat

object ShootingControlCommandService {
  suspend fun getCaptureMode(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.shootingControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getCaptureMode()
      promise.resolve(value.name)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun setCaptureMode(id: Int, value: String, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.shootingControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val enumValue = CaptureMode.entries.find {
        it.name == value
      }
      if (enumValue == null) {
        promise.reject(Exception("Capture mode not found. $value"))
      } else {
        service.setCaptureMode(enumValue)
        promise.resolve(null)
      }
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getFileFormat(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.shootingControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getFileFormat()
      promise.resolve(value.name)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun setFileFormat(id: Int, value: String, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.shootingControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val enumValue = FileFormat.entries.find {
        it.name == value
      }
      if (enumValue == null) {
        promise.reject(Exception("File format not found. $value"))
      } else {
        service.setFileFormat(enumValue)
        promise.resolve(null)
      }
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  fun takePicture(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.shootingControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      service.takePicture { error ->
        if (error == null) {
          promise.resolve(null)
        } else {
          promise.reject(error)
        }
      }
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}
