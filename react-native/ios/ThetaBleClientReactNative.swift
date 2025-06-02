import THETABleClient

let ERROR_TITLE = "Error"
let ERROR_MESSAGE_DEVICE_NOT_FOUND = "Device not found."
let ERROR_MESSAGE_UNSUPPORTED_SERVICE = "Unsupported service."
let MESSAGE_NO_ARGUMENT = "No Argument."
let MESSAGE_NO_RESULT = "No result."

enum ThetaClientError: Error {
    case invalidArgument(String)
}

@objc(ThetaBleClientReactNative)
class ThetaBleClientReactNative: RCTEventEmitter {
    static let EVENT_NOTIFY = "ThetaBleNotify"
    static var deviceList = [Int: ThetaBle.ThetaDevice]()
    static var counter = 0
    
    @objc
    override func supportedEvents() -> [String]! {
        return [ThetaBleClientReactNative.EVENT_NOTIFY]
    }
    
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
    
    @objc(nativeScan:withResolver:withRejecter:)
    func nativeScan(params: [AnyHashable: Any],
                    resolve: @escaping RCTPromiseResolveBlock,
                    reject: @escaping RCTPromiseRejectBlock
    ) {
        Task {
            do {
                let scanParams = toScanParams(params: params as? [String: Any] ?? [:])
                let scanList = try await {
                    if let name = scanParams.name {
                        if let device = try await ThetaBle.Companion.shared.scan(name: name, timeout: scanParams.timeout) {
                            return [device]
                        } else {
                            return []
                        }
                    } else {
                        return try await ThetaBle.Companion.shared.scan(timeout: scanParams.timeout)
                    }
                }()
                let resultList = fromTheta(
                    firstId: ThetaBleClientReactNative.counter + 1,
                    deviceList: scanList
                )
                scanList.forEach { device in
                    ThetaBleClientReactNative.counter += 1
                    ThetaBleClientReactNative.deviceList[ThetaBleClientReactNative.counter] = device
                }
                resolve(resultList)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    @objc(nativeScanThetaSsid:withResolver:withRejecter:)
    func nativeScanThetaSsid(params: [AnyHashable: Any],
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        Task {
            do {
                let scanParams = toScanSsidParams(params: params as? [String: Any] ?? [:])
                let scanList = try await ThetaBle.Companion.shared.scanThetaSsid(model: scanParams.model, timeout: toKotlinInt(value: scanParams.timeout))
                resolve(fromTheta(ssidList: scanList))
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    @objc(nativeConnect:withUuid:withResolver:withRejecter:)
    func nativeConnect(id: Int,
                       uuid: String?,
                       resolve: @escaping RCTPromiseResolveBlock,
                       reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        Task {
            do {
                try await device.connect(uuid: uuid)
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    @objc(nativeIsConnected:withResolver:withRejecter:)
    func nativeIsConnected(id: Int,
                           resolve: @escaping RCTPromiseResolveBlock,
                           reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            resolve(false)
            return
        }
        resolve(device.isConnected())
    }

    @objc(nativeDisconnect:withResolver:withRejecter:)
    func nativeDisconnect(id: Int,
                          resolve: @escaping RCTPromiseResolveBlock,
                          reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        Task {
            do {
                try await device.disconnect()
                resolve(nil)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }
    
    @objc(nativeContainService:withService:withResolver:withRejecter:)
    func nativeContainService(id: Int,
                              service: String,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock
    ) {
        guard let device = ThetaBleClientReactNative.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let bleService = getEnumValue(values: BleService.values(), name: service) else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        resolve(device.getService(service: bleService) != nil)
    }
    
    @objc(nativeGetFirmwareRevision:withResolver:withRejecter:)
    func nativeGetFirmwareRevision(id: Int,
                                   resolve: @escaping RCTPromiseResolveBlock,
                                   reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraInformationService.getFirmwareRevision(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetManufacturerName:withResolver:withRejecter:)
    func nativeGetManufacturerName(id: Int,
                                   resolve: @escaping RCTPromiseResolveBlock,
                                   reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraInformationService.getManufacturerName(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetModelNumber:withResolver:withRejecter:)
    func nativeGetModelNumber(id: Int,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraInformationService.getModelNumber(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetSerialNumber:withResolver:withRejecter:)
    func nativeGetSerialNumber(id: Int,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraInformationService.getSerialNumber(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetWlanMacAddress:withResolver:withRejecter:)
    func nativeGetWlanMacAddress(id: Int,
                                 resolve: @escaping RCTPromiseResolveBlock,
                                 reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraInformationService.getWlanMacAddress(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetBluetoothMacAddress:withResolver:withRejecter:)
    func nativeGetBluetoothMacAddress(id: Int,
                                      resolve: @escaping RCTPromiseResolveBlock,
                                      reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraInformationService.getBluetoothMacAddress(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetBatteryLevel:withResolver:withRejecter:)
    func nativeGetBatteryLevel(id: Int,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.getBatteryLevel(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetBatteryLevelNotify:withEnable:withResolver:withRejecter:)
    func nativeSetBatteryLevelNotify(id: Int, enable: Bool,
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.setBatteryLevelNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetBatteryStatus:withResolver:withRejecter:)
    func nativeGetBatteryStatus(id: Int,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.getBatteryStatus(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetBatteryStatusNotify:withEnable:withResolver:withRejecter:)
    func nativeSetBatteryStatusNotify(id: Int,
                                      enable: Bool,
                                      resolve: @escaping RCTPromiseResolveBlock,
                                      reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.setBatteryStatusNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetCameraPower:withResolver:withRejecter:)
    func nativeGetCameraPower(id: Int,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.getCameraPower(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetCameraPower:withValue:withResolver:withRejecter:)
    func nativeSetCameraPower(id: Int,
                              value: String,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.setCameraPower(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetCameraPowerNotify:withEnable:withResolver:withRejecter:)
    func nativeSetCameraPowerNotify(id: Int,
                                    enable: Bool,
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.setCameraPowerNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetCommandErrorDescriptionNotify:withEnable:withResolver:withRejecter:)
    func nativeSetCommandErrorDescriptionNotify(id: Int,
                                                enable: Bool,
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.setCommandErrorDescriptionNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetPluginControl:withResolver:withRejecter:)
    func nativeGetPluginControl(id: Int,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.getPluginControl(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetPluginControl:withValue:withResolver:withRejecter:)
    func nativeSetPluginControl(id: Int,
                                value: Any,
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.setPluginControl(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetPluginControlNotify:withEnable:withResolver:withRejecter:)
    func nativeSetPluginControlNotify(id: Int,
                                      enable: Bool,
                                      resolve: @escaping RCTPromiseResolveBlock,
                                      reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraStatusCommandService.setPluginControlNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetPluginList:withResolver:withRejecter:)
    func nativeGetPluginList(id: Int,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraControlCommandsService.getPluginList(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetPluginOrders:withResolver:withRejecter:)
    func nativeGetPluginOrders(id: Int,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraControlCommandsService.getPluginOrders(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetPluginOrders:withValue:withResolver:withRejecter:)
    func nativeSetPluginOrders(id: Int,
                               value: Any,
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraControlCommandsService.setPluginOrders(id: id, value: value) {_ in
            resolve(nil)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetCaptureMode:withResolver:withRejecter:)
    func nativeGetCaptureMode(id: Int,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        ShootingControlCommandService.getCaptureMode(id: id) {value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetCaptureMode:withValue:withResolver:withRejecter:)
    func nativeSetCaptureMode(id: Int, value: String,
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        ShootingControlCommandService.setCaptureMode(id: id, value: value) {_ in
            resolve(nil)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetFileFormat:withResolver:withRejecter:)
    func nativeGetFileFormat(id: Int,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        ShootingControlCommandService.getFileFormat(id: id) {value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetFileFormat:withValue:withResolver:withRejecter:)
    func nativeSetFileFormat(id: Int, value: String,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        ShootingControlCommandService.setFileFormat(id: id, value: value) {_ in
            resolve(nil)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeGetMaxRecordableTime:withResolver:withRejecter:)
    func nativeGetMaxRecordableTime(id: Int,
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        ShootingControlCommandService.getMaxRecordableTime(id: id) {value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeSetMaxRecordableTime:withValue:withResolver:withRejecter:)
    func nativeSetMaxRecordableTime(id: Int, value: String,
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        ShootingControlCommandService.setMaxRecordableTime(id: id, value: value) {_ in
            resolve(nil)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeTakePicture:withResolver:withRejecter:)
    func nativeTakePicture(id: Int,
                           resolve: @escaping RCTPromiseResolveBlock,
                           reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        ShootingControlCommandService.takePicture(id: id) {value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeCameraControlCommandV2GetInfo:withResolver:withRejecter:)
    func nativeCameraControlCommandV2GetInfo(id: Int,
                                             resolve: @escaping RCTPromiseResolveBlock,
                                             reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraControlCommandV2Service.getInfo(id: id) { thetaInfo in
            resolve(thetaInfo)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeCameraControlCommandV2GetState:withResolver:withRejecter:)
    func nativeCameraControlCommandV2GetState(id: Int,
                                              resolve: @escaping RCTPromiseResolveBlock,
                                              reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraControlCommandV2Service.getState(id: id) { thetaInfo in
            resolve(thetaInfo)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }
    
    @objc(nativeCameraControlCommandV2SetStateNotify:withEnable:withResolver:withRejecter:)
    func nativeCameraControlCommandV2SetStateNotify(id: Int,
                                                    enable: Bool,
                                                    resolve: @escaping RCTPromiseResolveBlock,
                                                    reject: @escaping RCTPromiseRejectBlock
    ) {
        CameraControlCommandV2Service.setStateNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeCameraControlCommandV2GetState2:withResolver:withRejecter:)
    func nativeCameraControlCommandV2GetState2(id: Int,
                                               resolve: @escaping RCTPromiseResolveBlock,
                                               reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraControlCommandV2Service.getState2(id: id) { thetaInfo in
            resolve(thetaInfo)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2GetOptions:withOptionNames:withResolver:withRejecter:)
    func nativeCameraControlCommandV2GetOptions(id: Int,
                                                optionNames: [Any],
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraControlCommandV2Service.getOptions(id: id, optionNames: optionNames) { options in
            resolve(options)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }
  
  @objc(nativeCameraControlCommandV2GetOptionsByString:withOptionNames:withResolver:withRejecter:)
  func nativeCameraControlCommandV2GetOptionsByString(id: Int,
                                                      optionNames: [String],
                                                      resolve: @escaping RCTPromiseResolveBlock,
                                                      reject: @escaping RCTPromiseRejectBlock) -> Void
  {
      CameraControlCommandV2Service.getOptionsByString(id: id, optionNames: optionNames) { options in
          resolve(options)
      } reject: {code, message, error in
          reject(code, message, error)
      }
  }
    
    @objc(nativeCameraControlCommandV2SetOptions:withOptions:withResolver:withRejecter:)
    func nativeCameraControlCommandV2SetOptions(id: Int,
                                                options: [AnyHashable: Any],
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraControlCommandV2Service.setOptions(id: id, options: options) { options in
            resolve(options)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2ReleaseShutter:withResolver:withRejecter:)
    func nativeCameraControlCommandV2ReleaseShutter(id: Int,
                                                    resolve: @escaping RCTPromiseResolveBlock,
                                                    reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        CameraControlCommandV2Service.releaseShutter(id: id) {_ in
            resolve(nil)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandScanPeripheralDevice:withTimeout:withResolver:withRejecter:)
    func nativeBluetoothControlCommandScanPeripheralDevice(id: Int,
                                                           timeout: Int,
                                                           resolve: @escaping RCTPromiseResolveBlock,
                                                           reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        BluetoothControlCommandService.scanPeripheralDevice(id: id, timeout: Int32(timeout)) { deviceList in
            resolve(deviceList)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeBluetoothControlCommandScanPeripheralDeviceStart:withTimeout:withResolver:withRejecter:)
    func nativeBluetoothControlCommandScanPeripheralDeviceStart(id: Int,
                                                                timeout: Int,
                                                                resolve: @escaping RCTPromiseResolveBlock,
                                                                reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        
        BluetoothControlCommandService.scanPeripheralDeviceStart(id: id, timeout: Int32(timeout)) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandScanPeripheralDeviceStop:withResolver:withRejecter:)
    func nativeBluetoothControlCommandScanPeripheralDeviceStop(id: Int,
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        BluetoothControlCommandService.scanPeripheralDeviceStop(id: id) { _ in
            resolve(nil)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandConnectPeripheralDevice:withMacAddress:withResolver:withRejecter:)
    func nativeBluetoothControlCommandConnectPeripheralDevice(id: Int,
                                                              macAddress: String,
                                                              resolve: @escaping RCTPromiseResolveBlock,
                                                              reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        BluetoothControlCommandService.connectPeripheralDevice(id: id, macAddress: macAddress) { _ in
            resolve(nil)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeBluetoothControlCommandDeletePeripheralDevice:withMacAddress:withResolver:withRejecter:)
    func nativeBluetoothControlCommandDeletePeripheralDevice(id: Int,
                                                             macAddress: String,
                                                             resolve: @escaping RCTPromiseResolveBlock,
                                                             reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        BluetoothControlCommandService.deletePeripheralDevice(id: id, macAddress: macAddress) { _ in
            resolve(nil)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetNetworkType:withNetworkType:withResolver:withRejecter:)
    func nativeWlanControlCommandV2SetNetworkType(id: Int,
                                                  networkType: String,
                                                  resolve: @escaping RCTPromiseResolveBlock,
                                                  reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        WlanControlCommandV2Service.setNetworkType(id: id, value: networkType) {_ in
            resolve(nil)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetNetworkTypeNotify:withEnable:withResolver:withRejecter:)
    func nativeWlanControlCommandV2SetNetworkTypeNotify(id: Int,
                                                        enable: Bool,
                                                        resolve: @escaping RCTPromiseResolveBlock,
                                                        reject: @escaping RCTPromiseRejectBlock
    ) {
        WlanControlCommandV2Service.setNetworkTypeNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2GetConnectedWifiInfo:withResolver:withRejecter:)
    func nativeWlanControlCommandV2GetConnectedWifiInfo(id: Int,
                                                        resolve: @escaping RCTPromiseResolveBlock,
                                                        reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        WlanControlCommandV2Service.getConnectedWifiInfo(id: id) { value in
            resolve(value)
        } reject: { code, message, error  in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetConnectedWifiInfoNotify:withEnable:withResolver:withRejecter:)
    func nativeWlanControlCommandV2SetConnectedWifiInfoNotify(id: Int,
                                                              enable: Bool,
                                                              resolve: @escaping RCTPromiseResolveBlock,
                                                              reject: @escaping RCTPromiseRejectBlock
    ) {
        WlanControlCommandV2Service.setConnectedWifiInfoNotify(id: id, enable: enable) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2ScanSsidStart:withTimeout:withResolver:withRejecter:)
    func nativeWlanControlCommandV2ScanSsidStart(id: Int,
                                                 timeout: Int,
                                                 resolve: @escaping RCTPromiseResolveBlock,
                                                 reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        WlanControlCommandV2Service.scanSsidStart(id: id, timeout: Int32(timeout)) { body in
            self.sendEvent(
                withName: ThetaBleClientReactNative.EVENT_NOTIFY,
                body: body
            )
        } resolve: { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2ScanSsidStop:withResolver:withRejecter:)
    func nativeWlanControlCommandV2ScanSsidStop(id: Int,
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        WlanControlCommandV2Service.scanSsidStop(id: id) { _ in
            resolve(nil)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetAccessPointDynamically:withParams:withResolver:withRejecter:)
    func nativeWlanControlCommandV2SetAccessPointDynamically(id: Int,
                                                             params: [AnyHashable: Any],
                                                             resolve: @escaping RCTPromiseResolveBlock,
                                                             reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        let accessPointParams = params as? [String: Any] ?? [:]
        WlanControlCommandV2Service.setAccessPointDynamically(id: id, params: accessPointParams) { _ in
            resolve(nil)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }
    
    @objc(nativeWlanControlCommandV2SetAccessPointStatically:withParams:withResolver:withRejecter:)
    func nativeWlanControlCommandV2SetAccessPointStatically(id: Int,
                                                            params: [AnyHashable: Any],
                                                            resolve: @escaping RCTPromiseResolveBlock,
                                                            reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        let accessPointParams = params as? [String: Any] ?? [:]
        WlanControlCommandV2Service.setAccessPointStatically(id: id, params: accessPointParams) { _ in
            resolve(nil)
        } reject: {code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeReleaseDevice:withResolver:withRejecter:)
    func nativeReleaseDevice(id: Int,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) -> Void
    {
        if ThetaBleClientReactNative.deviceList[id] != nil {
            ThetaBleClientReactNative.deviceList.removeValue(forKey: id)
        }
        resolve(nil)
    }
}
