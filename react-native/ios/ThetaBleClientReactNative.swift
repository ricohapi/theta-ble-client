import THETABleClient

let ERROR_TITLE = "Error"
let ERROR_MESSAGE_DEVICE_NOT_FOUND = "Device not found."
let ERROR_MESSAGE_UNSUPPORTED_SERVICE = "Unsupported service."

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
                let device = try await ThetaBle.Companion.shared.scan(name: scanParams.name, timeout: scanParams.timeout)
                ThetaBleClientReactNative.counter += 1
                let id = ThetaBleClientReactNative.counter
                
                if let device = device {
                    ThetaBleClientReactNative.deviceList[id] = device
                    resolve(id)
                } else {
                    resolve(0)
                }
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
