package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Promise

object CameraInformationService {
  suspend fun getFirmwareRevision(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraInformation
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getFirmwareRevision()
      promise.resolve(value)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getManufacturerName(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraInformation
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getManufacturerName()
      promise.resolve(value)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getModelNumber(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraInformation
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getModelNumber()
      promise.resolve(value)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getSerialNumber(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraInformation
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getSerialNumber()
      promise.resolve(value)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getWlanMacAddress(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraInformation
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getWlanMacAddress()
      promise.resolve(value)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getBluetoothMacAddress(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraInformation
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getBluetoothMacAddress()
      promise.resolve(value)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}
