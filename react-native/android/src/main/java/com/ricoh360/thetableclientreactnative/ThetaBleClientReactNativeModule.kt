package com.ricoh360.thetableclientreactnative

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ThetaBle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

const val ERROR_MESSAGE_DEVICE_NOT_FOUND = "Device not found."
const val ERROR_MESSAGE_UNSUPPORTED_SERVICE = "Unsupported service."

class ThetaBleClientReactNativeModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), CoroutineScope {

  override val coroutineContext: CoroutineContext = Job()

  override fun getName(): String {
    return NAME
  }

  /**
   * add event listener for [eventName]
   */
  @ReactMethod
  fun addListener(eventName: String) {
    // Set up any upstream listeners or background tasks as necessary
    if (eventName == EVENT_NOTIFY) {
      listenerCount += 1
    }
  }

  /**
   * remove event listener [count]
   */
  @ReactMethod
  fun removeListeners(count: Int) {
    // Remove upstream listeners, stop unnecessary background tasks
    listenerCount -= count
  }

  fun sendNotifyEvent(param: WritableMap) {
    reactApplicationContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(EVENT_NOTIFY, param)
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
  fun nativeScan(params: ReadableMap, promise: Promise) {
    launch {
      try {
        val scanParams = toScanParams(params)
        val scanList = when (scanParams.name) {
          null -> ThetaBle.scan(scanParams.timeout)
          else -> {
            when (val device = ThetaBle.scan(scanParams.name, scanParams.timeout)) {
              null -> listOf()
              else -> listOf(device)
            }
          }
        }
        val resultList = fromTheta(deviceCounter + 1, scanList)
        scanList.forEach {
          deviceCounter += 1
          deviceList.put(deviceCounter, it)
        }
        promise.resolve(resultList)
      } catch (e: Throwable) {
        promise.reject(e)
      }
    }
  }

  @ReactMethod
  fun nativeScanThetaSsid(params: ReadableMap, promise: Promise) {
    launch {
      try {
        val scanParams = toScanSsidParams(params)
        val scanList = ThetaBle.scanThetaSsid(scanParams.model, scanParams.timeout)
        promise.resolve(fromTheta(scanList))
      } catch (e: Throwable) {
        promise.reject(e)
      }
    }
  }

  @ReactMethod
  fun nativeConnect(id: Int, uuid: String?, promise: Promise) {
    launch {
      try {
        val device = deviceList[id]
        if (device == null) {
          promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
          return@launch
        }
        device.connect(uuid)
        promise.resolve(null)
      } catch (e: Throwable) {
        promise.reject(e)
      }
    }
  }

  @ReactMethod
  fun nativeIsConnected(id: Int, promise: Promise) {
    val device = deviceList[id]
    if (device == null) {
      promise.resolve(false)
      return
    }
    promise.resolve(device.isConnected())
  }

  @ReactMethod
  fun nativeDisconnect(id: Int, promise: Promise) {
    launch {
      try {
        val device = deviceList[id]
        if (device == null) {
          promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
          return@launch
        }
        device.disconnect()
        promise.resolve(null)
      } catch (e: Throwable) {
        promise.reject(e)
      }
    }
  }

  @ReactMethod
  fun nativeContainService(id: Int, service: String, promise: Promise) {
    launch {
      try {
        val device = deviceList[id]
        if (device == null) {
          promise.reject(Exception(ERROR_MESSAGE_DEVICE_NOT_FOUND))
          return@launch
        }
        val bleService = BleService.valueOf(service)
        device.getService(bleService)
        promise.resolve(device.getService(bleService) != null)
      } catch (e: Throwable) {
        promise.reject(e)
      }
    }
  }

  @ReactMethod
  fun nativeGetFirmwareRevision(id: Int, promise: Promise) {
    launch {
      CameraInformationService.getFirmwareRevision(id, promise)
    }
  }

  @ReactMethod
  fun nativeGetManufacturerName(id: Int, promise: Promise) {
    launch {
      CameraInformationService.getManufacturerName(id, promise)
    }
  }

  @ReactMethod
  fun nativeGetModelNumber(id: Int, promise: Promise) {
    launch {
      CameraInformationService.getModelNumber(id, promise)
    }
  }

  @ReactMethod
  fun nativeGetSerialNumber(id: Int, promise: Promise) {
    launch {
      CameraInformationService.getSerialNumber(id, promise)
    }
  }

  @ReactMethod
  fun nativeGetWlanMacAddress(id: Int, promise: Promise) {
    launch {
      CameraInformationService.getWlanMacAddress(id, promise)
    }
  }

  @ReactMethod
  fun nativeGetBluetoothMacAddress(id: Int, promise: Promise) {
    launch {
      CameraInformationService.getBluetoothMacAddress(id, promise)
    }
  }

  @ReactMethod
  fun nativeGetBatteryLevel(id: Int, promise: Promise) {
    launch {
      CameraStatusCommandService.getBatteryLevel(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetBatteryLevelNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      CameraStatusCommandService.setBatteryLevelNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeGetBatteryStatus(id: Int, promise: Promise) {
    launch {
      CameraStatusCommandService.getBatteryStatus(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetBatteryStatusNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      CameraStatusCommandService.setBatteryStatusNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeGetCameraPower(id: Int, promise: Promise) {
    launch {
      CameraStatusCommandService.getCameraPower(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetCameraPower(id: Int, value: String, promise: Promise) {
    launch {
      CameraStatusCommandService.setCameraPower(id, value, promise)
    }
  }

  @ReactMethod
  fun nativeSetCameraPowerNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      CameraStatusCommandService.setCameraPowerNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeSetCommandErrorDescriptionNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      CameraStatusCommandService.setCommandErrorDescriptionNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeGetPluginControl(id: Int, promise: Promise) {
    launch {
      CameraStatusCommandService.getPluginControl(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetPluginControl(id: Int, value: ReadableMap, promise: Promise) {
    launch {
      CameraStatusCommandService.setPluginControl(id, value, promise)
    }
  }

  @ReactMethod
  fun nativeSetPluginControlNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      CameraStatusCommandService.setPluginControlNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeGetPluginList(id: Int, promise: Promise) {
    launch {
      CameraControlCommandsService.getPluginList(id, promise)
    }
  }

  @ReactMethod
  fun nativeGetPluginOrders(id: Int, promise: Promise) {
    launch {
      CameraControlCommandsService.getPluginOrders(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetPluginOrders(id: Int, value: ReadableMap, promise: Promise) {
    launch {
      CameraControlCommandsService.setPluginOrders(id, value, promise)
    }
  }

  @ReactMethod
  fun nativeGetCaptureMode(id: Int, promise: Promise) {
    launch {
      ShootingControlCommandService.getCaptureMode(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetCaptureMode(id: Int, mode: String, promise: Promise) {
    launch {
      ShootingControlCommandService.setCaptureMode(id, mode, promise)
    }
  }

  @ReactMethod
  fun nativeGetFileFormat(id: Int, promise: Promise) {
    launch {
      ShootingControlCommandService.getFileFormat(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetFileFormat(id: Int, value: String, promise: Promise) {
    launch {
      ShootingControlCommandService.setFileFormat(id, value, promise)
    }
  }

  @ReactMethod
  fun nativeGetMaxRecordableTime(id: Int, promise: Promise) {
    launch {
      ShootingControlCommandService.getMaxRecordableTime(id, promise)
    }
  }

  @ReactMethod
  fun nativeSetMaxRecordableTime(id: Int, value: String, promise: Promise) {
    launch {
      ShootingControlCommandService.setMaxRecordableTime(id, value, promise)
    }
  }

  @ReactMethod
  fun nativeTakePicture(id: Int, promise: Promise) {
    ShootingControlCommandService.takePicture(id, promise)
  }

  @ReactMethod
  fun nativeCameraControlCommandV2GetInfo(id: Int, promise: Promise) {
    launch {
      CameraControlCommandV2Service.getInfo(id, promise)
    }
  }

  @ReactMethod
  fun nativeCameraControlCommandV2GetState(id: Int, promise: Promise) {
    launch {
      CameraControlCommandV2Service.getState(id, promise)
    }
  }

  @ReactMethod
  fun nativeCameraControlCommandV2SetStateNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      CameraControlCommandV2Service.setStateNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeCameraControlCommandV2GetState2(id: Int, promise: Promise) {
    launch {
      CameraControlCommandV2Service.getState2(id, promise)
    }
  }

  @ReactMethod
  fun nativeCameraControlCommandV2GetOptions(id: Int, optionNames: ReadableArray, promise: Promise) {
    launch {
      CameraControlCommandV2Service.getOptions(id, optionNames, promise)
    }
  }

  @ReactMethod
  fun nativeCameraControlCommandV2GetOptionsByString(id: Int, optionNames: ReadableArray, promise: Promise) {
    launch {
      CameraControlCommandV2Service.getOptionsByString(id, optionNames, promise)
    }
  }

  @ReactMethod
  fun nativeCameraControlCommandV2SetOptions(id: Int, options: ReadableMap, promise: Promise) {
    launch {
      CameraControlCommandV2Service.setOptions(id, options, promise)
    }
  }

  @ReactMethod
  fun nativeCameraControlCommandV2ReleaseShutter(id: Int, promise: Promise) {
    launch {
      CameraControlCommandV2Service.releaseShutter(id, promise)
    }
  }

  @ReactMethod
  fun nativeBluetoothControlCommandScanPeripheralDevice(id: Int, timeout: Int, promise: Promise) {
    launch {
      BluetoothControlCommandService.scanPeripheralDevice(id, timeout, promise)
    }
  }

  @ReactMethod
  fun nativeBluetoothControlCommandScanPeripheralDeviceStart(
    id: Int,
    timeout: Int,
    promise: Promise
  ) {
    launch {
      BluetoothControlCommandService.scanPeripheralDeviceStart(id, timeout, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeBluetoothControlCommandScanPeripheralDeviceStop(id: Int, promise: Promise) {
    launch {
      BluetoothControlCommandService.scanPeripheralDeviceStop(id, promise)
    }
  }

  @ReactMethod
  fun nativeBluetoothControlCommandConnectPeripheralDevice(
    id: Int,
    macAddress: String,
    promise: Promise
  ) {
    launch {
      BluetoothControlCommandService.connectPeripheralDevice(id, macAddress, promise)
    }
  }

  @ReactMethod
  fun nativeBluetoothControlCommandDeletePeripheralDevice(
    id: Int,
    macAddress: String,
    promise: Promise
  ) {
    launch {
      BluetoothControlCommandService.deletePeripheralDevice(id, macAddress, promise)
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2SetNetworkType(id: Int, value: String, promise: Promise) {
    launch {
      WlanControlCommandV2Service.setNetworkType(id, value, promise)
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2SetNetworkTypeNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      WlanControlCommandV2Service.setNetworkTypeNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2GetConnectedWifiInfo(id: Int, promise: Promise) {
    launch {
      WlanControlCommandV2Service.getConnectedWifiInfo(id, promise)
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2SetConnectedWifiInfoNotify(id: Int, enable: Boolean, promise: Promise) {
    launch {
      WlanControlCommandV2Service.setConnectedWifiInfoNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2ScanSsidStart(
    id: Int,
    timeout: Int,
    promise: Promise
  ) {
    launch {
      WlanControlCommandV2Service.scanSsidStart(id, timeout, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2ScanSsidStop(id: Int, promise: Promise) {
    launch {
      WlanControlCommandV2Service.scanSsidStop(id, promise)
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2SetAccessPointDynamically(id: Int, params: ReadableMap, promise: Promise) {
    launch {
      WlanControlCommandV2Service.setAccessPointDynamically(id, params, promise)
    }
  }

  @ReactMethod
  fun nativeWlanControlCommandV2SetAccessPointStatically(id: Int, params: ReadableMap, promise: Promise) {
    launch {
      WlanControlCommandV2Service.setAccessPointStatically(id, params, promise)
    }
  }

  @ReactMethod
  fun nativeReleaseDevice(id: Int, promise: Promise) {
    deviceList[id]?.let {
      deviceList.remove(id)
    }
    promise.resolve(null)
  }

  companion object {
    const val NAME = "ThetaBleClientReactNative"
    const val EVENT_NOTIFY = "ThetaBleNotify"

    var deviceList = mutableMapOf<Int, ThetaBle.ThetaDevice>()
    var deviceCounter = 0
    var listenerCount = 0
  }
}
