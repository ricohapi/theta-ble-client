package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.ble.BlePeripheral
import com.ricoh360.thetableclient.service.data.ThetaInfo
import com.ricoh360.thetableclient.service.data.ThetaOptions
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.ThetaState2
import com.ricoh360.thetableclient.service.data.values.ApplicationError
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.CaptureStatus
import com.ricoh360.thetableclient.service.data.values.OptionName
import com.ricoh360.thetableclient.service.data.values.ThetaModel
import com.ricoh360.thetableclient.transferred.CameraInfo
import com.ricoh360.thetableclient.transferred.CameraOptions
import com.ricoh360.thetableclient.transferred.CameraOptionsParent
import com.ricoh360.thetableclient.transferred.CameraState
import com.ricoh360.thetableclient.transferred.CameraState2
import com.ricoh360.thetableclient.transferred.GetOptionsParams
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

internal const val ERROR_MESSAGE_JSON_DECODE = "Json decode error."

/**
 * Camera Control Command V2 Service
 *
 * Service: B6AC7A7E-8C01-4A52-B188-68D53DF53EA2
 */
class CameraControlCommandV2 internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.CAMERA_CONTROL_COMMAND_V2,
        thetaDevice
    ) {

    /**
     * Acquires basic information of the camera and supported functions.
     *
     * Characteristic: A0452E2D-C7D8-4314-8CD6-7B8BBAB4D523
     *
     * @return Static attributes of Theta.
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun getInfo(): ThetaInfo {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.GET_INFO)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            ApplicationError.checkErrorResponse(data)?.let {
                throw ThetaBle.ThetaApplicationErrorException(it)
            }
            val jsonString = data.decodeToString()
            println("get info: $jsonString")
            return ThetaInfo(CameraInfo.decode(jsonString))
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            e.printStackTrace()
            throw e
        } catch (e: SerializationException) {
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the camera states.
     *
     * Characteristic: 083D92B0-21E0-4FB2-9503-7D8B2C2BB1D1
     *
     * @return Mutable values representing Theta status.
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun getState(): ThetaState {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.GET_STATE)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            ApplicationError.checkErrorResponse(data)?.let {
                throw ThetaBle.ThetaApplicationErrorException(it)
            }
            val jsonString = data.decodeToString()
            println("get state: $jsonString")
            return ThetaState(CameraState.decode(jsonString))
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            e.printStackTrace()
            throw e
        } catch (e: SerializationException) {
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set camera state notification.
     *
     * Characteristic: D32CE140-B0C2-4C07-AF15-2301B5057B8C
     *
     * @param callback Notification function
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    fun setStateNotify(callback: ((state: ThetaState?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(ERROR_MESSAGE_NOT_CONNECTED)
        try {
            callback ?: run {
                thetaDevice.observeManager?.setOnNotify(BleCharacteristic.NOTIFY_STATE, null)
                return
            }
            thetaDevice.observeManager?.setOnNotify(BleCharacteristic.NOTIFY_STATE) {
                if (it.isEmpty()) {
                    callback(null, ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                } else {
                    try {
                        val jsonString = it.decodeToString()
                        println("Notify state: $jsonString")
                        val thetaState = ThetaState(CameraState.decode(jsonString))
                        callback(thetaState, null)
                    } catch (e: SerializationException) {
                        callback(null, ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE))
                    } catch (e: Throwable) {
                        callback(null, ThetaBleApiException(e.message ?: e.toString()))
                    }
                }
            }
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }

    /**
     * Acquires the camera states.
     *
     * Characteristic: 8881CE4E-96FC-4C6C-8103-5DDA0AD138FB
     *
     * @return Mutable values representing Theta status.
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun getState2(): ThetaState2 {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.GET_STATE2)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            ApplicationError.checkErrorResponse(data)?.let {
                throw ThetaBle.ThetaApplicationErrorException(it)
            }
            val jsonString = data.decodeToString()
            println("get state2: $jsonString")
            return ThetaState2(CameraState2.decode(jsonString))
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            e.printStackTrace()
            throw e
        } catch (e: SerializationException) {
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the properties and property support specifications for shooting, the camera, etc.
     *
     * Characteristic: 7CFFAAE3-8467-4D0C-A9DD-7F70B4F52863
     *
     * @param optionNames List of [OptionName]
     * @return Options acquired
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun getOptions(optionNames: List<OptionName>): ThetaOptions {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val names = optionNames.distinct().map {
                it.value
            }
            val getOptionsParams = GetOptionsParams(names)
            val params = Json.encodeToString(getOptionsParams)
            println("getOptions params: $params")
            peripheral.write(BleCharacteristic.GET_OPTIONS, params.encodeToByteArray())
            val data = peripheral.read(BleCharacteristic.GET_OPTIONS)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            ApplicationError.checkErrorResponse(data)?.let {
                throw ThetaBle.ThetaApplicationErrorException(it)
            }
            val jsonString = data.decodeToString()
            println("getOptions: $jsonString")
            return when (thetaDevice.model) {
                ThetaModel.THETA_A1 -> ThetaOptions(CameraOptions.decode(jsonString))
                else -> ThetaOptions(CameraOptionsParent.decode(jsonString).options)
            }
        } catch (e: ThetaBleApiException) {
            e.printStackTrace()
            throw e
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            e.printStackTrace()
            throw e
        } catch (e: SerializationException) {
            e.printStackTrace()
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the properties and property support specifications for shooting, the camera, etc.
     *
     * Characteristic: 7CFFAAE3-8467-4D0C-A9DD-7F70B4F52863
     *
     * @param optionNames optionNames A list of strings representing the names of the options.
     * @return A map where each key is an option name and each value is the corresponding value for that option.
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun getOptionsByString(optionNames: List<String>): Map<String, Any> {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val getOptionsParams = GetOptionsParams(optionNames)
            val params = Json.encodeToString(getOptionsParams)
            println("getOptionsByString params: $params")
            peripheral.write(BleCharacteristic.GET_OPTIONS, params.encodeToByteArray())
            val data = peripheral.read(BleCharacteristic.GET_OPTIONS)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            ApplicationError.checkErrorResponse(data)?.let {
                throw ThetaBle.ThetaApplicationErrorException(it)
            }
            val jsonString = data.decodeToString()
            println("getOptionsByString: $jsonString")
            val jsonElement = Json.parseToJsonElement(jsonString)
            val result = jsonElementToAny(jsonElement) as Map<String, Any>
            return result
        } catch (e: ThetaBleApiException) {
            e.printStackTrace()
            throw e
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            e.printStackTrace()
            throw e
        } catch (e: SerializationException) {
            e.printStackTrace()
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BluetoothException(e)
        }
    }

    /**
     * Property settings for shooting, the camera, etc.
     *
     * Characteristic: F0BCD2F9-5862-4653-B50D-80DC51E8CB82
     *
     * @param options Camera setting options
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun setOptions(options: ThetaOptions) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val cameraOptions = options.toCameraOptions()
            val params = Json.encodeToString(cameraOptions)
            println("setOptions params: $params")
            peripheral.write(BleCharacteristic.SET_OPTIONS, params.encodeToByteArray())
        } catch (e: ThetaBleApiException) {
            e.printStackTrace()
            throw e
        } catch (e: SerializationException) {
            e.printStackTrace()
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BluetoothException(e)
        }
    }

    @Throws(Throwable::class)
    internal suspend fun requestShutterCommand(peripheral: BlePeripheral, command: String) {
        try {
            val jsonString = "{\"name\":\"${command}\"}"
            peripheral.write(
                BleCharacteristic.REQUEST_SHUTTER_COMMAND,
                jsonString.encodeToByteArray()
            )
        } catch (e: ThetaBleApiException) {
            e.printStackTrace()
            throw e
        } catch (e: SerializationException) {
            e.printStackTrace()
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BluetoothException(e)
        }
    }

    @Throws(Throwable::class)
    internal suspend fun getShutterCommand(): String? {
        return when (getOptions(listOf(OptionName.CaptureMode)).captureMode) {
            CaptureMode.IMAGE -> "camera.takePicture"
            CaptureMode.VIDEO -> {
                when (getState().captureStatus) {
                    CaptureStatus.IDLE -> "camera.startCapture"
                    CaptureStatus.SHOOTING -> "camera.stopCapture"
                    else -> null
                }
            }

            else -> null
        }
    }

    /**
     * Release shutter
     *
     * If CaptureMode is IMAGE, perform `camera.takePicture`
     * If CaptureMode is VIDEO, and CaptureState is IDLE, then `camera.startCapture` is executed
     * If CaptureMode is VIDEO, and CaptureStatus is SHOOTING, then `camera.stopCapture` is executed
     *
     * Characteristic: 6E2DEEBE-88B0-42A5-829D-1B2C6ABCE750
     *
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun releaseShutter() {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        val command = getShutterCommand()
            ?: throw ThetaBleApiException(ApplicationError.DISABLE_COMMAND.message)
        requestShutterCommand(peripheral, command)
    }

    private fun jsonElementToAny(element: JsonElement): Any {
        return when (element) {
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.booleanOrNull != null -> element.boolean
                    element.intOrNull != null -> element.int
                    element.longOrNull != null -> element.long
                    element.doubleOrNull != null -> element.double
                    else -> element.content
                }
            }

            is JsonObject -> {
                element.mapValues { (_, value) -> jsonElementToAny(value) }
            }

            is JsonArray -> {
                element.map { item -> jsonElementToAny(item) }
            }

            else -> element.toString()
        }
    }
}
