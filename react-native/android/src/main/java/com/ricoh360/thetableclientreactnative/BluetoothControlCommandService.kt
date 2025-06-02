package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.service.BluetoothControlCommand
import com.ricoh360.thetableclient.service.data.PeripheralDevice
import com.ricoh360.thetableclient.service.data.values.PeripheralDeviceStatus

const val KEY_DEVICE = "device"
const val KEY_MAC_ADDRESS = "macAddress"
const val KEY_PAIRING = "pairing"

object BluetoothControlCommandService {
  suspend fun scanPeripheralDevice(id: Int, timeout: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.bluetoothControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      val result = service.scanPeripheralDevice(timeout)
      promise.resolve(fromTheta(result))
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun scanPeripheralDeviceStart(
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
      val service = device.bluetoothControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      service.scanPeripheralDeviceStart(timeout, object: BluetoothControlCommand.ScanCallback {
        override fun onCompleted(peripheralDeviceList: List<PeripheralDevice>) {
          super.onCompleted(peripheralDeviceList)
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.SCAN_BLUETOOTH_PERIPHERAL_DEVICE,
              fromTheta(peripheralDeviceList),
              null,
            ),
          )
        }

        override fun onNotify(peripheralDevice: PeripheralDevice) {
          super.onNotify(peripheralDevice)
          sendNotifyEvent(
            toNotify(
              id,
              BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE,
              fromTheta(peripheralDevice),
              null,
            ),
          )
        }
      })
      println("before resolve")
      promise.resolve(null)
      println("after resolve")
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun scanPeripheralDeviceStop(id: Int, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.bluetoothControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      service.scanPeripheralDeviceStop()
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun connectPeripheralDevice(id: Int, macAddress: String, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.bluetoothControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      service.connectPeripheralDevice(macAddress)
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }

  suspend fun deletePeripheralDevice(id: Int, macAddress: String, promise: Promise) {
    try {
      val device = ThetaBleClientReactNativeModule.deviceList[id]
      device ?: let {
        promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
        return
      }
      val service = device.bluetoothControlCommand
      service ?: let {
        promise.reject(Exception(ERROR_MESSAGE_UNSUPPORTED_SERVICE))
        return
      }
      service.deletePeripheralDevice(macAddress)
      promise.resolve(null)
    } catch (e: Throwable) {
      promise.reject(e)
    }
  }
}

fun fromTheta(peripheralDevice: PeripheralDevice): WritableMap {
  val result = Arguments.createMap()
  result.putString(KEY_DEVICE, peripheralDevice.device)
  result.putString(KEY_MAC_ADDRESS, peripheralDevice.macAddress)
  result.putBoolean(KEY_PAIRING, peripheralDevice.pairing)
  result.putString(PeripheralDeviceStatus.keyName, peripheralDevice.status.name)
  return result
}

fun fromTheta(peripheralDeviceList: List<PeripheralDevice>): WritableArray {
  val result = Arguments.createArray()
  peripheralDeviceList.forEach {
    result.pushMap(fromTheta(it))
  }
  return result
}
