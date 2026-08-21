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
const val MESSAGE_NO_ARGUMENT = "No Argument."

class ThetaBleClientReactNativeModule(reactContext: ReactApplicationContext) :
  NativeThetaBleClientReactNativeSpec(reactContext), CoroutineScope {

  override val coroutineContext: CoroutineContext = Job()

  /**
   * add event listener for [eventName]
   */
  override fun addListener(eventName: String) {
    // Set up any upstream listeners or background tasks as necessary
    if (eventName == EVENT_NOTIFY) {
      listenerCount += 1
    }
  }

  /**
   * remove event listener [count]
   */
  override fun removeListeners(count: Double) {
    // Remove upstream listeners, stop unnecessary background tasks
    listenerCount -= count.toInt()
  }

  fun sendNotifyEvent(param: WritableMap) {
    reactApplicationContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(EVENT_NOTIFY, param)
  }

  override fun nativeScan(params: ReadableMap, promise: Promise) {
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

  override fun nativeScanThetaSsid(params: ReadableMap, promise: Promise) {
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

  override fun nativeConnect(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val uuid = params.getString("uuid")
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

  override fun nativeIsConnected(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val device = deviceList[id]
    if (device == null) {
      promise.resolve(false)
      return
    }
    promise.resolve(device.isConnected())
  }

  override fun nativeDisconnect(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
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

  override fun nativeContainService(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val service = params.getString("service")
      ?: return promise.rejectNoArgument()
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

  override fun nativeGetFirmwareRevision(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraInformationService.getFirmwareRevision(id, promise)
    }
  }

  override fun nativeGetManufacturerName(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraInformationService.getManufacturerName(id, promise)
    }
  }

  override fun nativeGetModelNumber(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraInformationService.getModelNumber(id, promise)
    }
  }

  override fun nativeGetSerialNumber(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraInformationService.getSerialNumber(id, promise)
    }
  }

  override fun nativeGetWlanMacAddress(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraInformationService.getWlanMacAddress(id, promise)
    }
  }

  override fun nativeGetBluetoothMacAddress(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraInformationService.getBluetoothMacAddress(id, promise)
    }
  }

  override fun nativeGetBatteryLevel(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.getBatteryLevel(id, promise)
    }
  }

  override fun nativeSetBatteryLevelNotify(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.setBatteryLevelNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeGetBatteryStatus(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.getBatteryStatus(id, promise)
    }
  }

  override fun nativeSetBatteryStatusNotify(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.setBatteryStatusNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeGetCameraPower(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.getCameraPower(id, promise)
    }
  }

  override fun nativeSetCameraPower(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val value = params.getString("value")
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.setCameraPower(id, value, promise)
    }
  }

  override fun nativeSetCameraPowerNotify(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.setCameraPowerNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeSetCommandErrorDescriptionNotify(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.setCommandErrorDescriptionNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeGetPluginControl(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.getPluginControl(id, promise)
    }
  }

  override fun nativeSetPluginControl(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val value = params.getMap("value")
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.setPluginControl(id, value, promise)
    }
  }

  override fun nativeSetPluginControlNotify(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      CameraStatusCommandService.setPluginControlNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeGetPluginList(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandsService.getPluginList(id, promise)
    }
  }

  override fun nativeGetPluginOrders(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandsService.getPluginOrders(id, promise)
    }
  }

  override fun nativeSetPluginOrders(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val value = params.getMap("value")
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandsService.setPluginOrders(id, value, promise)
    }
  }

  override fun nativeGetCaptureMode(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      ShootingControlCommandService.getCaptureMode(id, promise)
    }
  }

  override fun nativeSetCaptureMode(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val value = params.getString("value")
      ?: return promise.rejectNoArgument()
    launch {
      ShootingControlCommandService.setCaptureMode(id, value, promise)
    }
  }

  override fun nativeGetFileFormat(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      ShootingControlCommandService.getFileFormat(id, promise)
    }
  }

  override fun nativeSetFileFormat(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val value = params.getString("value")
      ?: return promise.rejectNoArgument()
    launch {
      ShootingControlCommandService.setFileFormat(id, value, promise)
    }
  }

  override fun nativeGetMaxRecordableTime(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      ShootingControlCommandService.getMaxRecordableTime(id, promise)
    }
  }

  override fun nativeSetMaxRecordableTime(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val value = params.getString("value")
      ?: return promise.rejectNoArgument()
    launch {
      ShootingControlCommandService.setMaxRecordableTime(id, value, promise)
    }
  }

  override fun nativeTakePicture(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    ShootingControlCommandService.takePicture(id, promise)
  }

  override fun nativeCameraControlCommandV2GetInfo(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.getInfo(id, promise)
    }
  }

  override fun nativeCameraControlCommandV2GetState(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.getState(id, promise)
    }
  }

  override fun nativeCameraControlCommandV2SetStateNotify(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.setStateNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeCameraControlCommandV2GetState2(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.getState2(id, promise)
    }
  }

  override fun nativeCameraControlCommandV2GetOptions(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val optionNames = params.getArray("optionNames")
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.getOptions(id, optionNames, promise)
    }
  }

  override fun nativeCameraControlCommandV2GetOptionsByString(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val optionNames = params.getArray("optionNames")
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.getOptionsByString(id, optionNames, promise)
    }
  }

  override fun nativeCameraControlCommandV2SetOptions(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val options = params.getMap("options")
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.setOptions(id, options, promise)
    }
  }

  override fun nativeCameraControlCommandV2ReleaseShutter(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      CameraControlCommandV2Service.releaseShutter(id, promise)
    }
  }

  override fun nativeBluetoothControlCommandScanPeripheralDevice(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val timeout = requireArgument(params, "timeout", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      BluetoothControlCommandService.scanPeripheralDevice(id, timeout, promise)
    }
  }

  override fun nativeBluetoothControlCommandScanPeripheralDeviceStart(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val timeout = requireArgument(params, "timeout", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      BluetoothControlCommandService.scanPeripheralDeviceStart(id, timeout, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeBluetoothControlCommandScanPeripheralDeviceStop(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      BluetoothControlCommandService.scanPeripheralDeviceStop(id, promise)
    }
  }

  override fun nativeBluetoothControlCommandConnectPeripheralDevice(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val macAddress = params.getString("macAddress")
      ?: return promise.rejectNoArgument()
    launch {
      BluetoothControlCommandService.connectPeripheralDevice(id, macAddress, promise)
    }
  }

  override fun nativeBluetoothControlCommandDeletePeripheralDevice(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val macAddress = params.getString("macAddress")
      ?: return promise.rejectNoArgument()
    launch {
      BluetoothControlCommandService.deletePeripheralDevice(id, macAddress, promise)
    }
  }

  override fun nativeWlanControlCommandGetWlanPasswordState(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandService.getWlanPasswordState(id, promise)
    }
  }

  override fun nativeWlanControlCommandV2SetNetworkType(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val networkType = params.getString("networkType")
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.setNetworkType(id, networkType, promise)
    }
  }

  override fun nativeWlanControlCommandV2SetNetworkTypeNotify(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.setNetworkTypeNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeWlanControlCommandV2GetConnectedWifiInfo(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.getConnectedWifiInfo(id, promise)
    }
  }

  override fun nativeWlanControlCommandV2SetConnectedWifiInfoNotify(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val enable = requireArgument(params, "enable", params::getBoolean)
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.setConnectedWifiInfoNotify(id, enable, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeWlanControlCommandV2ScanSsidStart(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val timeout = requireArgument(params, "timeout", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.scanSsidStart(id, timeout, promise) {
        sendNotifyEvent(it)
      }
    }
  }

  override fun nativeWlanControlCommandV2ScanSsidStop(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.scanSsidStop(id, promise)
    }
  }

  override fun nativeWlanControlCommandV2SetAccessPointDynamically(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val accessPointParams = params.getMap("params")
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.setAccessPointDynamically(id, accessPointParams, promise)
    }
  }

  override fun nativeWlanControlCommandV2SetAccessPointStatically(
    params: ReadableMap,
    promise: Promise
  ) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    val accessPointParams = params.getMap("params")
      ?: return promise.rejectNoArgument()
    launch {
      WlanControlCommandV2Service.setAccessPointStatically(id, accessPointParams, promise)
    }
  }

  override fun nativeReleaseDevice(params: ReadableMap, promise: Promise) {
    val id = requireArgument(params, "id", params::getInt)
      ?: return promise.rejectNoArgument()
    deviceList[id]?.let {
      deviceList.remove(id)
    }
    promise.resolve(null)
  }

  /**
   * [getter] (e.g. [ReadableMap.getInt]) throws if [name] is missing or JSON-null,
   * instead of returning null like getString/getMap/getArray do. Normalize that to a
   * null return so every field --  whichever accessor it uses -- can be checked and
   * rejected with MESSAGE_NO_ARGUMENT the same way.
   */
  private fun <T> requireArgument(params: ReadableMap, name: String, getter: (String) -> T): T? {
    if (!params.hasKey(name) || params.isNull(name)) {
      return null
    }
    return getter(name)
  }

  private fun Promise.rejectNoArgument() {
    reject(Exception(MESSAGE_NO_ARGUMENT))
  }

  companion object {
    const val NAME = "ThetaBleClientReactNative"
    const val EVENT_NOTIFY = "ThetaBleNotify"

    var deviceList = mutableMapOf<Int, ThetaBle.ThetaDevice>()
    var deviceCounter = 0
    var listenerCount = 0
  }
}
