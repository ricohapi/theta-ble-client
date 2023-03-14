package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.service.data.ThetaInfo
import com.ricoh360.thetableclient.service.data.ThetaState
import com.ricoh360.thetableclient.service.data.ThetaState2
import com.ricoh360.thetableclient.transferred.CameraInfo
import com.ricoh360.thetableclient.transferred.CameraState
import com.ricoh360.thetableclient.transferred.CameraState2
import kotlinx.serialization.SerializationException

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
            thetaDevice.peripheral ?: throw ThetaBle.ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.GET_INFO)
            if (data.isEmpty()) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            val jsonString = data.decodeToString()
            println("get info: $jsonString")
            return ThetaInfo(CameraInfo.decode(jsonString))
        } catch (e: ThetaBle.ThetaBleApiException) {
            throw e
        } catch (e: SerializationException) {
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            throw ThetaBle.BluetoothException(e)
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
            thetaDevice.peripheral ?: throw ThetaBle.ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.GET_STATE)
            if (data.isEmpty()) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            val jsonString = data.decodeToString()
            println("get state: $jsonString")
            return ThetaState(CameraState.decode(jsonString))
        } catch (e: ThetaBle.ThetaBleApiException) {
            throw e
        } catch (e: SerializationException) {
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            throw ThetaBle.BluetoothException(e)
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
        thetaDevice.peripheral ?: throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_NOT_CONNECTED)
        try {
            callback ?: run {
                thetaDevice.observeManager?.setOnNotify(BleCharacteristic.NOTIFY_STATE, null)
                return
            }
            thetaDevice.observeManager?.setOnNotify(BleCharacteristic.NOTIFY_STATE) {
                if (it.isEmpty()) {
                    callback(null, ThetaBle.ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                } else {
                    try {
                        val jsonString = it.decodeToString()
                        println("Notify state: $jsonString")
                        val thetaState = ThetaState(CameraState.decode(jsonString))
                        callback(thetaState, null)
                    } catch (e: SerializationException) {
                        callback(null, ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE))
                    } catch (e: Throwable) {
                        callback(null, ThetaBle.ThetaBleApiException(e.message ?: e.toString()))
                    }
                }
            }
        } catch (e: ThetaBle.ThetaBleApiException) {
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
            thetaDevice.peripheral ?: throw ThetaBle.ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.GET_STATE2)
            if (data.isEmpty()) {
                throw ThetaBle.ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            val jsonString = data.decodeToString()
            println("get state2: $jsonString")
            return ThetaState2(CameraState2.decode(jsonString))
        } catch (e: ThetaBle.ThetaBleApiException) {
            throw e
        } catch (e: SerializationException) {
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            throw ThetaBle.BluetoothException(e)
        }
    }
}
