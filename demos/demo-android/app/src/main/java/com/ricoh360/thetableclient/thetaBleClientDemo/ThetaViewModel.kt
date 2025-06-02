package com.ricoh360.thetableclient.thetaBleClientDemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.service.CameraStatusCommand
import com.ricoh360.thetableclient.service.data.GpsInfo
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.ble.PluginControl
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.PluginPowerStatus
import com.ricoh360.thetaclient.ThetaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThetaViewModel : ViewModel() {
    private val _infoText = MutableLiveData("Initialized")
    val infoText: LiveData<String> = _infoText
    private val _batteryLevel = MutableLiveData(0)
    val batteryLevel: LiveData<Int> = _batteryLevel
    private val _batteryStatus = MutableLiveData<ChargingState?>()
    val batteryStatus: LiveData<ChargingState?> = _batteryStatus
    private val _cameraPower = MutableLiveData<CameraPower?>()
    val cameraPower: LiveData<CameraPower?> = _cameraPower
    private val _pluginControl = MutableLiveData<PluginControl?>()
    val pluginControl: LiveData<PluginControl?> = _pluginControl

    private var _deviceName = MutableLiveData<String?>()
    val deviceName: LiveData<String?> = _deviceName
    private val _useUuid = MutableLiveData(true)
    val useUuid: LiveData<Boolean> = _useUuid
    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    private val scope = CoroutineScope(Dispatchers.Default)
    private val uuid = APP_UUID
    private var thetaDevice: ThetaBle.ThetaDevice? = null

    fun getBleThetaName(info: ThetaInfo): String {
        return when(info.model) {
            "RICOH360 THETA A1" -> "AA" + info.serialNumber.takeLast(8)
            else -> info.serialNumber.takeLast(8)
        }
    }

    fun connectWifi() {
        scope.launch {
            val thetaRepository: ThetaRepository?
            try {
                thetaRepository = ThetaRepository.newInstance("http://192.168.1.1")
                thetaRepository.let {
                    val info = getThetaInfoApi()
                    val devName = getBleThetaName(info)
                    setDevice(devName, false)
                    setInfoText("wifi connected. $devName")
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                return@launch
            }
            try {
                thetaRepository.let {
                    val name = it.setBluetoothDevice(uuid)
                    setDevice(name, true)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            try {
                thetaRepository.let {
                    val options =
                        ThetaRepository.Options(bluetoothPower = ThetaRepository.BluetoothPowerEnum.ON)
                    it.setOptions(options)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun setDevice(devName: String, useUuid: Boolean) {
        viewModelScope.launch {
            _useUuid.value = useUuid
            _deviceName.value = devName
        }
    }

    fun scan() {
        scope.launch {
            setInfoText("Scanning ${deviceName.value}...")
            val timeout = ThetaBle.Timeout(
                timeoutScan = 30_000,
                timeoutPeripheral = 1_000,
                timeoutConnect = 30_000,
                timeoutTakePicture = 10_000,
            )
            _deviceName.value?.let {
                val device = ThetaBle.scan(it, timeout)
                if (device == null) {
                    setInfoText("Error. $it not found.")
                    return@launch
                }
                thetaDevice = device
                setInfoText("Scan. Found device: $it")
            }
        }
    }

    fun connect() {
        scope.launch {
            val thetaDevice = thetaDevice
            if (thetaDevice == null) {
                setInfoText("Error. No device.")
                setLiveData(_isConnected, false)
                return@launch
            }
            try {
                setInfoText("Connecting... ${deviceName.value}")
                val uuid = when (useUuid.value) {
                    false -> null
                    else -> APP_UUID
                }
                thetaDevice.connect(uuid)
                setLiveData(_isConnected, true)
                setInfoText("Connected. ${deviceName.value}")
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
                setLiveData(_isConnected, false)
            }

            try {
                withContext(Dispatchers.IO) {
                    delay(1000)
                    thetaDevice.cameraStatusCommand?.let {
                        setNotifications(it)
                    }
                }
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    private fun setNotifications(service: CameraStatusCommand) {
        try {
            service.setBatteryStatusNotify { value, error ->
                error?.run {
                    setInfoText("Error battery status notification")
                } ?: run {
                    setInfoText("battery status. ${value?.name}")
                    setLiveData(_batteryStatus, value)
                }
            }
        } catch (e: ThetaBle.ThetaBleApiException) {
            setInfoText("Not support battery status notification")
        }
        try {
            service.setBatteryLevelNotify { value, error ->
                error?.run {
                    setInfoText("Error battery level notification")
                } ?: run {
                    setInfoText("Battery level. $value")
                    setLiveData(_batteryLevel, value)
                }
            }
        } catch (e: ThetaBle.ThetaBleApiException) {
            setInfoText("Not support battery level notification")
        }
        try {
            service.setCameraPowerNotify { value, error ->
                error?.run {
                    setInfoText("Error camera power notification")
                } ?: run {
                    setInfoText("Camera power. ${value?.name}")
                    setLiveData(_cameraPower, value)
                }
            }
        } catch (e: ThetaBle.ThetaBleApiException) {
            setInfoText("Not support camera power notification")
        }
        try {
            service.setCommandErrorDescriptionNotify { value, error ->
                error?.run {
                    setInfoText("Error command error notification")
                } ?: run {
                    setInfoText("Command error. ${value?.name}")
                }
            }
        } catch (e: ThetaBle.ThetaBleApiException) {
            setInfoText("Not support command error notification")
        }
        try {
            service.setPluginControlNotify { value, error ->
                error?.run {
                    setInfoText("Error plugin control notification")
                } ?: run {
                    setInfoText("Plugin. ${value?.pluginControl?.name}")
                    setLiveData(_pluginControl, value)
                }
            }
        } catch (e: ThetaBle.ThetaBleApiException) {
            setInfoText("Not support plugin control notification")
        }
    }

    fun disconnect() {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            if (device.isConnected()) {
                device.disconnect()
            }
            setInfoText("Disconnected. ${deviceName.value}")
            setLiveData(_isConnected, false)
        }
    }

    fun getInfo() {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            val service = device.cameraInformation
            service ?: run {
                setInfoText("Unsupported Camera Information")
                return@launch
            }
            try {
                val firmware = service.getFirmwareRevision()
                val manuName = service.getManufacturerName()
                val model = service.getModelNumber()
                val serial = service.getSerialNumber()
                val wlanMac = service.getWlanMacAddress()
                val bleMac = service.getBluetoothMacAddress()


                setInfoText("Information\n firmware: $firmware\n maker: $manuName\n model: $model\nserial: $serial\n wlan: $wlanMac\n ble: $bleMac")
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun updateBatteryLevel() {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            val service = device.cameraStatusCommand
            service ?: run {
                setInfoText("Unsupported Camera Status Command")
                return@launch
            }
            try {
                val value = service.getBatteryLevel()
                setLiveData(_batteryLevel, value)
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun updateBatteryStatus() {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            val service = device.cameraStatusCommand
            service ?: run {
                setInfoText("Unsupported Camera Status Command")
                return@launch
            }
            try {
                val value = service.getBatteryStatus()
                setLiveData(_batteryStatus, value)
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun updateCameraPower() {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            val service = device.cameraStatusCommand
            service ?: run {
                setInfoText("Unsupported Camera Status Command")
                return@launch
            }
            try {
                val value = service.getCameraPower()
                setLiveData(_cameraPower, value)
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun setCameraPower(value: CameraPower) {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            val service = device.cameraStatusCommand
            service ?: run {
                setInfoText("Unsupported Camera Status Command")
                return@launch
            }
            try {
                service.setCameraPower(value)
                setLiveData(_cameraPower, value)
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun updatePluginControl() {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            val service = device.cameraStatusCommand
            service ?: run {
                setInfoText("Unsupported Camera Status Command")
                return@launch
            }
            try {
                val value = service.getPluginControl()
                setLiveData(_pluginControl, value)
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    private suspend fun getFirstPlugin(): Int? {
        val device = thetaDevice
        device ?: run {
            setInfoText("Error. No device.")
            return null
        }
        val service = device.cameraControlCommands
        service ?: run {
            setInfoText("Unsupported Camera Status Command")
            return null
        }
        return try {
            val orders = service.getPluginOrders()
            orders.first
        } catch (_: Throwable) {
            null
        }
    }
    fun setPluginControl(value: PluginPowerStatus) {
        scope.launch {
            val device = thetaDevice
            device ?: run {
                setInfoText("Error. No device.")
                return@launch
            }
            val service = device.cameraStatusCommand
            service ?: run {
                setInfoText("Unsupported Camera Status Command")
                return@launch
            }
            try {
                if (_pluginControl.value == null) {
                    setInfoText("Error. Not yet acquired")
                    return@launch
                }
                if (value == PluginPowerStatus.STOP) {
                    service.setPluginControl(PluginControl(PluginPowerStatus.STOP, null))
                } else {
                    val firstPlugin = getFirstPlugin()
                    if (_pluginControl.value?.plugin != null && firstPlugin != null) {
                        service.setPluginControl(
                            PluginControl(
                                PluginPowerStatus.RUNNING,
                                firstPlugin,
                            ),
                        )
                    } else {
                        service.setPluginControl(
                            PluginControl(
                                PluginPowerStatus.RUNNING,
                                null,
                            ),
                        )
                    }
                }
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun takePicture() {
        val device = thetaDevice
        device ?: run {
            setInfoText("Error. No device.")
            return
        }
        val service = device.shootingControlCommand
        service ?: run {
            setInfoText("Unsupported Camera Status Command")
            return
        }
        scope.launch {
            try {
                if (service.getCaptureMode() != CaptureMode.IMAGE) {
                    setInfoText("Change capture mode...")
                    service.setCaptureMode(CaptureMode.IMAGE)
                    delay(1000) // Wait a little or you'll fail
                }
                setInfoText("Start take a picture.")
                service.takePicture {
                    if (it == null) {
                        setInfoText("End take a picture.")
                    } else {
                        setInfoText("End take a picture. error:\n${it}\n message: ${it.message}")
                    }
                }
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun setInfoText(text: String) {
        setLiveData(_infoText, text)
    }

    fun checkCameraControlCommandV2() {
        val thetaDevice = thetaDevice
        if (thetaDevice == null) {
            setInfoText("Error. No device.")
            return
        }
        if (thetaDevice.cameraControlCommandV2 == null) {
            setInfoText("Unsupported CameraControlCommandV2.")
        } else {
            setInfoText("OK.")
        }
    }

    fun checkCameraStatusCommand() {
        val thetaDevice = thetaDevice
        if (thetaDevice == null) {
            setInfoText("Error. No device.")
            return
        }
        if (thetaDevice.cameraStatusCommand == null) {
            setInfoText("Unsupported CameraStatusCommand.")
        } else {
            setInfoText("OK.")
        }
    }

    fun cameraControlCommandV2GetInfo() {
        val thetaDevice = thetaDevice
        if (thetaDevice == null) {
            setInfoText("Error. No device.")
            return
        }
        val service = thetaDevice.cameraControlCommandV2
        if (service == null) {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        scope.launch {
            try {
                val thetaInfo = service.getInfo()
                val text = """
                    info
                    manufacturer: ${thetaInfo.manufacturer}
                    model: ${thetaInfo.model.name}
                    serialNumber: ${thetaInfo.serialNumber}
                    wlanMacAddress: ${thetaInfo.wlanMacAddress}
                    bluetoothMacAddress: ${thetaInfo.bluetoothMacAddress}
                    firmwareVersion: ${thetaInfo.firmwareVersion}
                    uptime: ${thetaInfo.uptime}
                """.trimIndent()
                setInfoText(text)
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    private fun getStateString(thetaState: ThetaState): String {
        var text = ""
        thetaState.batteryLevel?.let {
            text += "batteryLevel: ${it}\n"
        }
        thetaState.captureStatus?.let {
            text += "captureStatus: ${it}\n"
        }
        thetaState.recordedTime?.let {
            text += "recordedTime: ${it}\n"
        }
        thetaState.recordableTime?.let {
            text += "recordableTime: ${it}\n"
        }
        thetaState.capturedPictures?.let {
            text += "capturedPictures: ${it}\n"
        }
        thetaState.latestFileUrl?.let {
            text += "latestFileUrl: ${it}\n"
        }
        thetaState.batteryState?.let {
            text += "batteryState: ${it}\n"
        }
        thetaState.function?.let {
            text += "function: ${it}\n"
        }
        thetaState.cameraError?.let {
            val errorText = it.joinToString("\n")
            text += "cameraError: ${errorText}\n"
        }
        thetaState.batteryInsert?.let {
            text += "batteryInsert: ${it}\n"
        }
        thetaState.boardTemp?.let {
            text += "boardTemp: ${it}\n"
        }
        thetaState.batteryTemp?.let {
            text += "batteryTemp: ${it}\n"
        }
        return text
    }

    private fun getGpsInfoString(gpsInfo: GpsInfo, leftMargin: Int): String {
        var text = ""
        val margin = " ".repeat(leftMargin)
        gpsInfo.lat?.let {
            text += margin + "lat: ${it}\n"
        }
        gpsInfo.lng?.let {
            text += margin + "lng: ${it}\n"
        }
        gpsInfo.altitude?.let {
            text += margin + "altitude: ${it}\n"
        }
        gpsInfo.dateTimeZone?.let {
            text += margin + "dateTimeZone: ${it}\n"
        }
        gpsInfo.datum?.let {
            text += margin + "datum: ${it}\n"
        }
        return text
    }

    fun cameraControlCommandV2GetState() {
        val thetaDevice = thetaDevice
        if (thetaDevice == null) {
            setInfoText("Error. No device.")
            return
        }
        val service = thetaDevice.cameraControlCommandV2
        if (service == null) {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        scope.launch {
            try {
                val thetaState = service.getState()
                setInfoText("state:\n${getStateString(thetaState)}")
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun cameraControlCommandV2SetStateNotify() {
        val thetaDevice = thetaDevice
        if (thetaDevice == null) {
            setInfoText("Error. No device.")
            return
        }
        val service = thetaDevice.cameraControlCommandV2
        if (service == null) {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        service.setStateNotify { state, error ->
            if (error != null) {
                setInfoText("Error. ${error.message}")
            } else if (state != null) {
                setInfoText("state notify:\n${getStateString(state)}")
            }
        }
        setInfoText("OK. set state notify")
    }

    fun cameraControlCommandV2ClearStateNotify() {
        val thetaDevice = thetaDevice ?: return
        val service = thetaDevice.cameraControlCommandV2 ?: return
        service.setStateNotify(null)
    }

    fun cameraControlCommandV2GetState2() {
        val thetaDevice = thetaDevice
        if (thetaDevice == null) {
            setInfoText("Error. No device.")
            return
        }
        val service = thetaDevice.cameraControlCommandV2
        if (service == null) {
            setInfoText("Unsupported CameraControlCommandV2.")
            return
        }
        scope.launch {
            try {
                val thetaState2 = service.getState2()
                var text = "state2:\n"
                thetaState2.externalGpsInfo?.let { stateGpsInfo ->
                    text += "  externalGpsInfo:\n"
                    stateGpsInfo.gpsInfo?.let {
                        text += getGpsInfoString(it, 4)
                    }
                }
                thetaState2.internalGpsInfo?.let { stateGpsInfo ->
                    text += "  internalGpsInfo:\n"
                    stateGpsInfo.gpsInfo?.let {
                        text += getGpsInfoString(it, 4)
                    }
                }
                setInfoText(text)
            } catch (e: Throwable) {
                setInfoText("Error. ${e.message}")
            }
        }
    }

    fun scanThetaSsid() {
        scope.launch {
            setInfoText("Scanning SSID...")
            val ssidList = ThetaBle.scanThetaSsid(null, 10_000)
            when (ssidList.size) {
                0 -> setInfoText("Device not found.")
                else -> {
                    val message = StringBuilder()
                    ssidList.forEach {
                        message.append("ssid: ${it.first}\npassword: ${it.second}\n\n")
                    }
                    setInfoText(message.toString())
                }
            }
        }
    }

    private fun <T> setLiveData(data: MutableLiveData<T>, value: T) {
        viewModelScope.launch {
            data.value = value
        }
    }
}
