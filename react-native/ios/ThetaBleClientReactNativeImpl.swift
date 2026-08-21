import THETABleClient

let ERROR_TITLE = "Error"
let ERROR_MESSAGE_DEVICE_NOT_FOUND = "Device not found."
let ERROR_MESSAGE_UNSUPPORTED_SERVICE = "Unsupported service."
let MESSAGE_NO_ARGUMENT = "No Argument."
let MESSAGE_NO_RESULT = "No result."
let EVENT_NOTIFY = "ThetaBleNotify"

enum ThetaClientError: Error {
    case invalidArgument(String)
}

// This class IS the registered TurboModule -- ThetaBleClientReactNative.mm
// (Objective-C++) only attaches the RCTTurboModule/codegen-Spec protocol
// conformance to it at runtime (via class_addProtocol/a class extension),
// because this file can't see the codegen spec header itself: that header
// pulls in RCTTurboModule.h -> jsi.h, which needs the C++ stdlib, and Swift's
// "-import-underlying-module" build of this pod's own headers is Objective-C
// only. See ThetaBleClientReactNativeModuleImports.h for the longer version.
// The @objc name below is what makes NSClassFromString/RCT_EXTERN_MODULE find
// this class as "ThetaBleClientReactNative".
@objc(ThetaBleClientReactNative)
class ThetaBleClientReactNativeImpl: RCTEventEmitter {
    static var deviceList = [Int: ThetaBle.ThetaDevice]()
    static var counter = 0

    override func supportedEvents() -> [String]! {
        return [EVENT_NOTIFY]
    }

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc(nativeScan:resolve:reject:)
    func nativeScan(params: [AnyHashable: Any],
                    resolve: @escaping RCTPromiseResolveBlock,
                    reject: @escaping RCTPromiseRejectBlock)
    {
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
                    firstId: ThetaBleClientReactNativeImpl.counter + 1,
                    deviceList: scanList
                )
                scanList.forEach { device in
                    ThetaBleClientReactNativeImpl.counter += 1
                    ThetaBleClientReactNativeImpl.deviceList[ThetaBleClientReactNativeImpl.counter] = device
                }
                resolve(resultList)
            } catch {
                reject(ERROR_TITLE, error.localizedDescription, error)
            }
        }
    }

    @objc(nativeScanThetaSsid:resolve:reject:)
    func nativeScanThetaSsid(params: [AnyHashable: Any],
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock)
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

    @objc(nativeConnect:resolve:reject:)
    func nativeConnect(params: [AnyHashable: Any],
                       resolve: @escaping RCTPromiseResolveBlock,
                       reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        let uuid = params["uuid"] as? String
        guard let device = ThetaBleClientReactNativeImpl.deviceList[id] else {
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

    @objc(nativeIsConnected:resolve:reject:)
    func nativeIsConnected(params: [AnyHashable: Any],
                           resolve: @escaping RCTPromiseResolveBlock,
                           reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        guard let device = ThetaBleClientReactNativeImpl.deviceList[id] else {
            resolve(false)
            return
        }
        resolve(device.isConnected())
    }

    @objc(nativeDisconnect:resolve:reject:)
    func nativeDisconnect(params: [AnyHashable: Any],
                          resolve: @escaping RCTPromiseResolveBlock,
                          reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        guard let device = ThetaBleClientReactNativeImpl.deviceList[id] else {
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

    @objc(nativeContainService:resolve:reject:)
    func nativeContainService(params: [AnyHashable: Any],
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let service = params["service"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        guard let device = ThetaBleClientReactNativeImpl.deviceList[id] else {
            reject(ERROR_TITLE, ERROR_MESSAGE_DEVICE_NOT_FOUND, nil)
            return
        }
        guard let bleService = getEnumValue(values: BleService.values(), name: service) else {
            reject(ERROR_TITLE, ERROR_MESSAGE_UNSUPPORTED_SERVICE, nil)
            return
        }
        resolve(device.getService(service: bleService) != nil)
    }

    @objc(nativeGetFirmwareRevision:resolve:reject:)
    func nativeGetFirmwareRevision(params: [AnyHashable: Any],
                                   resolve: @escaping RCTPromiseResolveBlock,
                                   reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraInformationService.getFirmwareRevision(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetManufacturerName:resolve:reject:)
    func nativeGetManufacturerName(params: [AnyHashable: Any],
                                   resolve: @escaping RCTPromiseResolveBlock,
                                   reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraInformationService.getManufacturerName(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetModelNumber:resolve:reject:)
    func nativeGetModelNumber(params: [AnyHashable: Any],
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraInformationService.getModelNumber(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetSerialNumber:resolve:reject:)
    func nativeGetSerialNumber(params: [AnyHashable: Any],
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraInformationService.getSerialNumber(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetWlanMacAddress:resolve:reject:)
    func nativeGetWlanMacAddress(params: [AnyHashable: Any],
                                 resolve: @escaping RCTPromiseResolveBlock,
                                 reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraInformationService.getWlanMacAddress(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetBluetoothMacAddress:resolve:reject:)
    func nativeGetBluetoothMacAddress(params: [AnyHashable: Any],
                                      resolve: @escaping RCTPromiseResolveBlock,
                                      reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraInformationService.getBluetoothMacAddress(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetBatteryLevel:resolve:reject:)
    func nativeGetBatteryLevel(params: [AnyHashable: Any],
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.getBatteryLevel(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetBatteryLevelNotify:resolve:reject:)
    func nativeSetBatteryLevelNotify(params: [AnyHashable: Any],
                                     resolve: @escaping RCTPromiseResolveBlock,
                                     reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.setBatteryLevelNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetBatteryStatus:resolve:reject:)
    func nativeGetBatteryStatus(params: [AnyHashable: Any],
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.getBatteryStatus(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetBatteryStatusNotify:resolve:reject:)
    func nativeSetBatteryStatusNotify(params: [AnyHashable: Any],
                                      resolve: @escaping RCTPromiseResolveBlock,
                                      reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.setBatteryStatusNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetCameraPower:resolve:reject:)
    func nativeGetCameraPower(params: [AnyHashable: Any],
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.getCameraPower(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetCameraPower:resolve:reject:)
    func nativeSetCameraPower(params: [AnyHashable: Any],
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let value = params["value"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.setCameraPower(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetCameraPowerNotify:resolve:reject:)
    func nativeSetCameraPowerNotify(params: [AnyHashable: Any],
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.setCameraPowerNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetCommandErrorDescriptionNotify:resolve:reject:)
    func nativeSetCommandErrorDescriptionNotify(params: [AnyHashable: Any],
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.setCommandErrorDescriptionNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetPluginControl:resolve:reject:)
    func nativeGetPluginControl(params: [AnyHashable: Any],
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.getPluginControl(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetPluginControl:resolve:reject:)
    func nativeSetPluginControl(params: [AnyHashable: Any],
                                resolve: @escaping RCTPromiseResolveBlock,
                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let value = params["value"] as? [String: Any]
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.setPluginControl(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetPluginControlNotify:resolve:reject:)
    func nativeSetPluginControlNotify(params: [AnyHashable: Any],
                                      resolve: @escaping RCTPromiseResolveBlock,
                                      reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraStatusCommandService.setPluginControlNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetPluginList:resolve:reject:)
    func nativeGetPluginList(params: [AnyHashable: Any],
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandsService.getPluginList(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetPluginOrders:resolve:reject:)
    func nativeGetPluginOrders(params: [AnyHashable: Any],
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandsService.getPluginOrders(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetPluginOrders:resolve:reject:)
    func nativeSetPluginOrders(params: [AnyHashable: Any],
                               resolve: @escaping RCTPromiseResolveBlock,
                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let value = params["value"]
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandsService.setPluginOrders(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetCaptureMode:resolve:reject:)
    func nativeGetCaptureMode(params: [AnyHashable: Any],
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        ShootingControlCommandService.getCaptureMode(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetCaptureMode:resolve:reject:)
    func nativeSetCaptureMode(params: [AnyHashable: Any],
                              resolve: @escaping RCTPromiseResolveBlock,
                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let value = params["value"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        ShootingControlCommandService.setCaptureMode(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetFileFormat:resolve:reject:)
    func nativeGetFileFormat(params: [AnyHashable: Any],
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        ShootingControlCommandService.getFileFormat(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetFileFormat:resolve:reject:)
    func nativeSetFileFormat(params: [AnyHashable: Any],
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let value = params["value"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        ShootingControlCommandService.setFileFormat(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeGetMaxRecordableTime:resolve:reject:)
    func nativeGetMaxRecordableTime(params: [AnyHashable: Any],
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        ShootingControlCommandService.getMaxRecordableTime(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeSetMaxRecordableTime:resolve:reject:)
    func nativeSetMaxRecordableTime(params: [AnyHashable: Any],
                                    resolve: @escaping RCTPromiseResolveBlock,
                                    reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let value = params["value"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        ShootingControlCommandService.setMaxRecordableTime(id: id, value: value) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeTakePicture:resolve:reject:)
    func nativeTakePicture(params: [AnyHashable: Any],
                           resolve: @escaping RCTPromiseResolveBlock,
                           reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        ShootingControlCommandService.takePicture(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2GetInfo:resolve:reject:)
    func nativeCameraControlCommandV2GetInfo(params: [AnyHashable: Any],
                                             resolve: @escaping RCTPromiseResolveBlock,
                                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.getInfo(id: id) { thetaInfo in
            resolve(thetaInfo)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2GetState:resolve:reject:)
    func nativeCameraControlCommandV2GetState(params: [AnyHashable: Any],
                                              resolve: @escaping RCTPromiseResolveBlock,
                                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.getState(id: id) { thetaInfo in
            resolve(thetaInfo)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2SetStateNotify:resolve:reject:)
    func nativeCameraControlCommandV2SetStateNotify(params: [AnyHashable: Any],
                                                    resolve: @escaping RCTPromiseResolveBlock,
                                                    reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.setStateNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2GetState2:resolve:reject:)
    func nativeCameraControlCommandV2GetState2(params: [AnyHashable: Any],
                                               resolve: @escaping RCTPromiseResolveBlock,
                                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.getState2(id: id) { thetaInfo in
            resolve(thetaInfo)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2GetOptions:resolve:reject:)
    func nativeCameraControlCommandV2GetOptions(params: [AnyHashable: Any],
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let optionNames = params["optionNames"] as? [Any]
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.getOptions(id: id, optionNames: optionNames) { options in
            resolve(options)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2GetOptionsByString:resolve:reject:)
    func nativeCameraControlCommandV2GetOptionsByString(params: [AnyHashable: Any],
                                                        resolve: @escaping RCTPromiseResolveBlock,
                                                        reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let optionNames = params["optionNames"] as? [String]
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.getOptionsByString(id: id, optionNames: optionNames) { options in
            resolve(options)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2SetOptions:resolve:reject:)
    func nativeCameraControlCommandV2SetOptions(params: [AnyHashable: Any],
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let options = params["options"] as? [AnyHashable: Any]
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.setOptions(id: id, options: options) { options in
            resolve(options)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeCameraControlCommandV2ReleaseShutter:resolve:reject:)
    func nativeCameraControlCommandV2ReleaseShutter(params: [AnyHashable: Any],
                                                    resolve: @escaping RCTPromiseResolveBlock,
                                                    reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        CameraControlCommandV2Service.releaseShutter(id: id) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandScanPeripheralDevice:resolve:reject:)
    func nativeBluetoothControlCommandScanPeripheralDevice(params: [AnyHashable: Any],
                                                           resolve: @escaping RCTPromiseResolveBlock,
                                                           reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let timeout = params["timeout"] as? Int
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        BluetoothControlCommandService.scanPeripheralDevice(id: id, timeout: Int32(timeout)) { deviceList in
            resolve(deviceList)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandScanPeripheralDeviceStart:resolve:reject:)
    func nativeBluetoothControlCommandScanPeripheralDeviceStart(params: [AnyHashable: Any],
                                                                resolve: @escaping RCTPromiseResolveBlock,
                                                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let timeout = params["timeout"] as? Int
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        BluetoothControlCommandService.scanPeripheralDeviceStart(id: id, timeout: Int32(timeout)) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandScanPeripheralDeviceStop:resolve:reject:)
    func nativeBluetoothControlCommandScanPeripheralDeviceStop(params: [AnyHashable: Any],
                                                               resolve: @escaping RCTPromiseResolveBlock,
                                                               reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        BluetoothControlCommandService.scanPeripheralDeviceStop(id: id) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandConnectPeripheralDevice:resolve:reject:)
    func nativeBluetoothControlCommandConnectPeripheralDevice(params: [AnyHashable: Any],
                                                              resolve: @escaping RCTPromiseResolveBlock,
                                                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let macAddress = params["macAddress"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        BluetoothControlCommandService.connectPeripheralDevice(id: id, macAddress: macAddress) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeBluetoothControlCommandDeletePeripheralDevice:resolve:reject:)
    func nativeBluetoothControlCommandDeletePeripheralDevice(params: [AnyHashable: Any],
                                                             resolve: @escaping RCTPromiseResolveBlock,
                                                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let macAddress = params["macAddress"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        BluetoothControlCommandService.deletePeripheralDevice(id: id, macAddress: macAddress) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetNetworkType:resolve:reject:)
    func nativeWlanControlCommandV2SetNetworkType(params: [AnyHashable: Any],
                                                  resolve: @escaping RCTPromiseResolveBlock,
                                                  reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let networkType = params["networkType"] as? String
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.setNetworkType(id: id, value: networkType) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetNetworkTypeNotify:resolve:reject:)
    func nativeWlanControlCommandV2SetNetworkTypeNotify(params: [AnyHashable: Any],
                                                        resolve: @escaping RCTPromiseResolveBlock,
                                                        reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.setNetworkTypeNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2GetConnectedWifiInfo:resolve:reject:)
    func nativeWlanControlCommandV2GetConnectedWifiInfo(params: [AnyHashable: Any],
                                                        resolve: @escaping RCTPromiseResolveBlock,
                                                        reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.getConnectedWifiInfo(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetConnectedWifiInfoNotify:resolve:reject:)
    func nativeWlanControlCommandV2SetConnectedWifiInfoNotify(params: [AnyHashable: Any],
                                                              resolve: @escaping RCTPromiseResolveBlock,
                                                              reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let enable = params["enable"] as? Bool
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.setConnectedWifiInfoNotify(id: id, enable: enable) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: {
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2ScanSsidStart:resolve:reject:)
    func nativeWlanControlCommandV2ScanSsidStart(params: [AnyHashable: Any],
                                                 resolve: @escaping RCTPromiseResolveBlock,
                                                 reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let timeout = params["timeout"] as? Int
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.scanSsidStart(id: id, timeout: Int32(timeout)) { body in
            self.sendEvent(withName: EVENT_NOTIFY, body: body)
        } resolve: { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2ScanSsidStop:resolve:reject:)
    func nativeWlanControlCommandV2ScanSsidStop(params: [AnyHashable: Any],
                                                resolve: @escaping RCTPromiseResolveBlock,
                                                reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.scanSsidStop(id: id) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetAccessPointDynamically:resolve:reject:)
    func nativeWlanControlCommandV2SetAccessPointDynamically(params: [AnyHashable: Any],
                                                             resolve: @escaping RCTPromiseResolveBlock,
                                                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let accessPointParams = params["params"] as? [String: Any]
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.setAccessPointDynamically(id: id, params: accessPointParams) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandV2SetAccessPointStatically:resolve:reject:)
    func nativeWlanControlCommandV2SetAccessPointStatically(params: [AnyHashable: Any],
                                                            resolve: @escaping RCTPromiseResolveBlock,
                                                            reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int,
              let accessPointParams = params["params"] as? [String: Any]
        else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandV2Service.setAccessPointStatically(id: id, params: accessPointParams) { _ in
            resolve(nil)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeWlanControlCommandGetWlanPasswordState:resolve:reject:)
    func nativeWlanControlCommandGetWlanPasswordState(params: [AnyHashable: Any],
                                                      resolve: @escaping RCTPromiseResolveBlock,
                                                      reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        WlanControlCommandService.getWlanPasswordState(id: id) { value in
            resolve(value)
        } reject: { code, message, error in
            reject(code, message, error)
        }
    }

    @objc(nativeReleaseDevice:resolve:reject:)
    func nativeReleaseDevice(params: [AnyHashable: Any],
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock)
    {
        guard let id = params["id"] as? Int else {
            reject(ERROR_TITLE, MESSAGE_NO_ARGUMENT, nil)
            return
        }
        if ThetaBleClientReactNativeImpl.deviceList[id] != nil {
            ThetaBleClientReactNativeImpl.deviceList.removeValue(forKey: id)
        }
        resolve(nil)
    }
}
