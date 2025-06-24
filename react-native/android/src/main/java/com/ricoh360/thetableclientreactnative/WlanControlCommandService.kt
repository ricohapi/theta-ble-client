package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Promise

object WlanControlCommandService {
  suspend fun getWlanPasswordState(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.wlanControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val value = service.getWlanPasswordState()
      promise.resolve(value.name)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}
