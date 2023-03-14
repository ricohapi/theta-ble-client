package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

object CameraControlCommandsService {
  suspend fun getPluginList(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraControlCommands
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getPluginList()
      promise.resolve(fromTheta(value))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun getPluginOrders(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraControlCommands
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getPluginOrders()
      promise.resolve(fromTheta(value))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun setPluginOrders(id: Int, value: ReadableMap, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.cameraControlCommands
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val result = toThetaPluginOrders(value)
      service.setPluginOrders(result)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}
