//
//  ThetaBleApi.swift
//  demo-ios
//
//  Created on 2023/03/15.
//

import Foundation
import THETABleClient

let MESSAGE_ERROR_NO_DEVICE = "Error. No device."
let MESSAGE_ERROR_UNSUPPORTED = "Unsuppoted."

@MainActor
class ThetaBleApi: ObservableObject {
    @Published var infoText = "Init"
    @Published var batteryLevel: Int?
    @Published var batteryStatus: ChargingState?
    @Published var cameraPower: CameraPower?
    @Published var pluginControl: PluginControl?

    var thetaDevice: ThetaBle.ThetaDevice?
    var devName: String = ""

    func setInfoText(_ value: String) {
        DispatchQueue.main.async {
            self.infoText = value
        }
    }

    func scan(name: String) async throws {
        do {
            setInfoText("Scanning...")
            let timeout = ThetaBle.Timeout(
                timeoutScan: 30000,
                timeoutPeripheral: 1000,
                timeoutConnect: 5000,
                timeoutTakePicture: 10000
            )
            let device = try await ThetaBle.Companion.shared.scan(name: name, timeout: timeout)
            if let device {
                thetaDevice = device

                setInfoText("Scan. Found device: \(name)")
                devName = name
            } else {
                setInfoText("Scan. Not found device: \(name)")
            }
        } catch {
            setInfoText("Error. scan device.")
        }
    }

    func connect(uuid: String?) async -> Bool {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return false
        }
        do {
            setInfoText("Connecting...")
            try await device.connect(uuid: uuid)
            setInfoText("Connected: \(devName)")
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                self.setNotifications(device: device)
            }
        } catch {
            setInfoText("Error. connect")
            return false
        }
        return true
    }

    func setNotifications(device: ThetaBle.ThetaDevice) {
        guard let service = device.cameraStatusCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        setBatteryLevelNotify(service: service)
        setBatteryStatusNotify(service: service)
        setCameraPowerNotify(service: service)
        setCameraPowerNotify(service: service)
        setPluginControlNotify(service: service)
    }

    func setBatteryLevelNotify(service: CameraStatusCommand) {
        do {
            try service.setBatteryLevelNotify { level, error in
                if error != nil {
                    self.setInfoText("Error battery level notification")
                } else {
                    self.setInfoText("Battery level: \(level ?? 0)")
                    DispatchQueue.main.async {
                        self.batteryLevel = level?.intValue
                    }
                }
            }
        } catch {
            setInfoText("Not support battery level notification")
        }
    }

    func setBatteryStatusNotify(service: CameraStatusCommand) {
        do {
            try service.setBatteryStatusNotify { status, error in
                if error != nil {
                    self.setInfoText("Error battery status notification")
                } else {
                    self.setInfoText("Battery status: \(status?.name ?? "nil")")
                    DispatchQueue.main.async {
                        self.batteryStatus = status
                    }
                }
            }
        } catch {
            setInfoText("Not support battery status notification")
        }
    }

    func setCameraPowerNotify(service: CameraStatusCommand) {
        do {
            try service.setCameraPowerNotify { power, error in
                if error != nil {
                    self.setInfoText("Error camera power notification")
                } else {
                    self.setInfoText("Camera power: \(power?.name ?? "nil")")
                    DispatchQueue.main.async {
                        self.cameraPower = power
                    }
                }
            }
        } catch {
            setInfoText("Not support camera power notification")
        }
    }

    func setCommandErrorDescriptionNotify(service: CameraStatusCommand) {
        do {
            try service.setCommandErrorDescriptionNotify { value, error in
                if error != nil {
                    self.setInfoText("Error command error notification")
                } else {
                    self.setInfoText("Command error: \(value?.name ?? "nil")")
                }
            }
        } catch {
            setInfoText("Not support command error notification")
        }
    }

    func setPluginControlNotify(service: CameraStatusCommand) {
        do {
            try service.setPluginControlNotify { plugin, error in
                if error != nil {
                    self.setInfoText("Error plugin control notification")
                } else {
                    self.setInfoText("Plugin: \(plugin?.pluginControl.name ?? "nil")")
                    DispatchQueue.main.async {
                        self.pluginControl = plugin
                    }
                }
            }
        } catch {
            setInfoText("Not support plugin control notification")
        }
    }

    func updateBatteryLevel() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraStatusCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        do {
            let value = try await service.getBatteryLevel()
            DispatchQueue.main.async {
                self.batteryLevel = value.intValue
            }
        } catch {
            setInfoText("Error.")
        }
    }

    func updateBatteryStatus() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraStatusCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        do {
            let value = try await service.getBatteryStatus()
            DispatchQueue.main.async {
                self.batteryStatus = value
            }
        } catch {
            setInfoText("Error.")
        }
    }

    func updateCameraPower() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraStatusCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        do {
            let value = try await service.getCameraPower()
            DispatchQueue.main.async {
                self.cameraPower = value
            }
        } catch {
            setInfoText("Error.")
        }
    }

    func setCameraPower(value: CameraPower) async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraStatusCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        do {
            try await service.setCameraPower(value: value)
        } catch {
            setInfoText("Error.")
        }
    }

    func updatePluginControl() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraStatusCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        do {
            let value = try await service.getPluginControl()
            DispatchQueue.main.async {
                self.pluginControl = value
            }
        } catch {
            setInfoText("Error.")
        }
    }

    func getFirstPlugin() async -> Int32? {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return nil
        }
        guard let service = device.cameraControlCommands else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return nil
        }
        if let order = try? await service.getPluginOrders() {
            return order.first
        } else {
            return nil
        }
    }

    func setPluginControl(value: PluginPowerStatus) async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraStatusCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        if let pluginControl {
            do {
                if value == PluginPowerStatus.stop {
                    try await service.setPluginControl(value: PluginControl(pluginControl: value, plugin: nil))
                } else {
                    if let firstPlugin = await getFirstPlugin(), pluginControl.plugin != nil {
                        try await service.setPluginControl(value:
                            PluginControl(
                                pluginControl: value,
                                plugin: KotlinInt(int: firstPlugin)
                            ))
                    } else {
                        try await service.setPluginControl(value:
                            PluginControl(
                                pluginControl: value,
                                plugin: nil
                            ))
                    }
                }
            } catch {
                setInfoText("Error.")
            }
        } else {
            setInfoText("Error. Not yet acquired.")
        }
    }

    func disconnect() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        do {
            if device.isConnected() {
                try await device.disconnect()
            }
            setInfoText("Disconnected.")
        } catch {
            setInfoText("Error. disconnect.")
        }
    }

    func getInfo() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraInformation else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        do {
            let firmware = try await service.getFirmwareRevision()
            let manuName = try await service.getManufacturerName()
            let model = try await service.getModelNumber()
            let serial = try await service.getSerialNumber()
            let wlanMac = try await service.getWlanMacAddress()
            let bleMac = try await service.getBluetoothMacAddress()

            setInfoText(
                " firmware: \(firmware)\n maker: \(manuName)\n model: \(model)\n serial: \(serial)\n wlan: \(wlanMac)\n ble: \(bleMac)."
            )
        } catch {
            setInfoText("Error. Get Information.")
        }
    }

    func takePicture() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.shootingControlCommand else {
            setInfoText(MESSAGE_ERROR_UNSUPPORTED)
            return
        }
        do {
            if try await (service.getCaptureMode() != .image) {
                setInfoText("Change capture mode...")
                try await service.setCaptureMode(value: .image)

                // Wait a little or you'll fail
                try await Task.sleep(nanoseconds: 1 * 1000 * 1000 * 1000)
            }

            class Callback: KotlinSuspendFunction1 {
                let thetaBleApi: ThetaBleApi
                init(thetaBleApi: ThetaBleApi) {
                    self.thetaBleApi = thetaBleApi
                }

                func invoke(p1: Any?) async throws -> Any? {
                    if p1 == nil {
                        await thetaBleApi.setInfoText("End take a picture.")
                    } else {
                        await thetaBleApi.setInfoText("End take a picture. error:\(p1 ?? "unknown")")
                    }
                    return nil
                }
            }

            setInfoText("Start take a picture")
            try service.takePicture(complete: Callback(thetaBleApi: self))
        } catch {
            setInfoText("Error. Take a picture.")
        }
    }

    func checkCameraControlCommandV2() -> Bool {
        guard let device = thetaDevice, device.cameraControlCommandV2 != nil else {
            return false
        }
        return true
    }

    func checkCameraStatusCommand() -> Bool {
        guard let device = thetaDevice, device.cameraStatusCommand != nil else {
            return false
        }
        return true
    }

    func cameraControlCommandV2GetInfo() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        do {
            let thetaInfo = try await service.getInfo()
            let text = """
            info
            manufacturer: \(thetaInfo.manufacturer)
            model: \(thetaInfo.model.name)
            serialNumber: \(thetaInfo.serialNumber)
            wlanMacAddress: \(thetaInfo.wlanMacAddress ?? "")
            bluetoothMacAddress: \(thetaInfo.bluetoothMacAddress ?? "")
            firmwareVersion: \(thetaInfo.firmwareVersion)
            uptime: \(thetaInfo.uptime)
            """

            setInfoText(text)
        } catch {
            setInfoText("Error. Get info.")
        }
    }

    func getStateString(thetaState: ThetaState) -> String {
        var text = ""
        if let batteryLevel = thetaState.batteryLevel {
            text += "batteryLevel: \(batteryLevel)\n"
        }
        if let captureStatus = thetaState.captureStatus {
            text += "captureStatus: \(captureStatus.name)\n"
        }
        if let recordedTime = thetaState.recordedTime {
            text += "recordedTime: \(recordedTime)\n"
        }
        if let recordableTime = thetaState.recordableTime {
            text += "recordableTime: \(recordableTime)\n"
        }
        if let capturedPictures = thetaState.capturedPictures {
            text += "capturedPictures: \(capturedPictures)\n"
        }
        if let latestFileUrl = thetaState.latestFileUrl {
            text += "latestFileUrl: \(latestFileUrl)\n"
        }
        if let batteryState = thetaState.batteryState {
            text += "batteryState: \(batteryState.name)\n"
        }
        if let function = thetaState.function {
            text += "function: \(function.name)\n"
        }
        if let cameraError = thetaState.cameraError {
            let errorText = cameraError.map { it in
                it.name
            }.joined(separator: "\n")
            text += "cameraError: \(errorText)\n"
        }
        if let batteryInsert = thetaState.batteryInsert {
            text += "batteryInsert: \(batteryInsert.boolValue ? "true" : "false")\n"
        }
        if let boardTemp = thetaState.boardTemp {
            text += "boardTemp: \(boardTemp)\n"
        }
        if let batteryTemp = thetaState.batteryTemp {
            text += "batteryTemp: \(batteryTemp)\n"
        }
        return text
    }

    func cameraControlCommandV2GetState() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        do {
            let thetaState = try await service.getState()
            setInfoText("state\n\(getStateString(thetaState: thetaState))")
        } catch {
            setInfoText("Error. Get state.")
        }
    }

    func getGpsInfoString(gpsInfo: GpsInfo, leftMargin: Int) -> String {
        var text = ""
        let margin = String(repeating: " ", count: leftMargin)

        if let lat = gpsInfo.lat {
            text += margin + "lat: \(lat)\n"
        }
        if let lng = gpsInfo.lng {
            text += margin + "lng: \(lng)\n"
        }
        if let altitude = gpsInfo.altitude {
            text += margin + "altitude: \(altitude)\n"
        }
        if let dateTimeZone = gpsInfo.dateTimeZone {
            text += margin + "dateTimeZone: \(dateTimeZone)\n"
        }
        if let datum = gpsInfo.datum {
            text += margin + "datum: \(datum)\n"
        }

        return text
    }

    func cameraControlCommandV2GetState2() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        do {
            let thetaState2 = try await service.getState2()
            var text = "state2\n"
            if let externalGpsInfo = thetaState2.externalGpsInfo {
                text += "  externalGpsInfo:\n"
                if let gpsInfo = externalGpsInfo.gpsInfo {
                    text += getGpsInfoString(gpsInfo: gpsInfo, leftMargin: 4)
                }
            }
            if let internalGpsInfo = thetaState2.internalGpsInfo {
                text += "  internalGpsInfo:\n"
                if let gpsInfo = internalGpsInfo.gpsInfo {
                    text += getGpsInfoString(gpsInfo: gpsInfo, leftMargin: 4)
                }
            }
            setInfoText(text)
        } catch {
            setInfoText("Error. Get state2.")
        }
    }

    func cameraControlCommandV2ClearStateNotify() {
        guard let device = thetaDevice else {
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            return
        }
        try? service.setStateNotify()
    }

    func cameraControlCommandV2SetStateNotify() {
        guard let device = thetaDevice else {
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            return
        }
        do {
            try service.setStateNotify { state, error in
                if error != nil {
                    self.setInfoText("Error. Set state notify")
                } else if let state {
                    self.setInfoText("state notify\n\(self.getStateString(thetaState: state))")
                }
            }
            setInfoText("OK. set state notify.")
        } catch {
            setInfoText("Error. Set state notify.")
        }
    }

    func cameraControlCommandV2GetOptions() async throws {
        guard let device = thetaDevice else {
            setInfoText(MESSAGE_ERROR_NO_DEVICE)
            return
        }
        guard let service = device.cameraControlCommandV2 else {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        do {
            let thetaOptions = try await service.getOptions(optionNames: [OptionName.capturemode])
            var text = "options captureMode:\n"
            text += thetaOptions.captureMode?.name ?? "nil"
            setInfoText(text)
        } catch {
            setInfoText("Error. Get state2.")
        }
    }

    func scanSsid() async throws {
        do {
            setInfoText("Scanning...")
            let ssidList = try await ThetaBle.Companion.shared.scanThetaSsid(model: nil, timeout: 10000)
            if ssidList.isEmpty {
                setInfoText("Device not found")
            } else {
                var message = ""
                ssidList.forEach { element in
                    message += "ssid: \(element.first as? String ?? "")\npassword: \(element.second as? String ?? "")\n\n"
                }
                setInfoText(message)
            }
        } catch {
            setInfoText("Error. scan ssid.")
        }
    }
}
