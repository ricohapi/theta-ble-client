package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.ERROR_MESSAGE_RESERVED_VALUE
import com.ricoh360.thetableclient.ERROR_MESSAGE_UNKNOWN_VALUE
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.FileFormat
import com.ricoh360.thetableclient.toBytes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * Shooting Control Command Service
 *
 * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
 */
class ShootingControlCommand internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.SHOOTING_CONTROL_COMMAND,
        thetaDevice
    ) {

    /**
     * Acquires the capture mode of the camera.
     *
     * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
     * Characteristic: 78009238-AC3D-4370-9B6F-C9CE2F4E3CA8
     *
     * @return Capture Mode.[CaptureMode]
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getCaptureMode(): CaptureMode {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.CAPTURE_MODE)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return CaptureMode.getFromBle(data[0])
                ?: throw ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${data[0]}")
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the capture mode of the camera.
     *
     * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
     * Characteristic: 78009238-AC3D-4370-9B6F-C9CE2F4E3CA8
     *
     * @param value Capture Mode.[CaptureMode]
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun setCaptureMode(value: CaptureMode) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        value.ble ?: throw ThetaBleApiException(
            ERROR_MESSAGE_UNKNOWN_VALUE
        )
        try {
            val data = value.ble.toBytes()
            peripheral.write(BleCharacteristic.CAPTURE_MODE, data)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the recording size (pixels) of the camera.
     *
     * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
     * Characteristic: E8F0EDD1-6C0F-494A-95C3-3244AE0B9A01
     *
     * @return File format.[FileFormat]
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getFileFormat(): FileFormat {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.FILE_FORMAT)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return FileFormat.getFromBle(data[0])
                ?: throw ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${data[0]}")
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the recording size (pixels) of the camera.
     *
     * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
     * Characteristic: E8F0EDD1-6C0F-494A-95C3-3244AE0B9A01
     *
     * @param value File format.[FileFormat]
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun setFileFormat(value: FileFormat) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )

        if (value == FileFormat.RESERVED) {
            throw ThetaBleApiException(ERROR_MESSAGE_RESERVED_VALUE)
        }
        value.ble ?: throw ThetaBleApiException(
            ERROR_MESSAGE_UNKNOWN_VALUE
        )

        try {
            val data = value.ble.toBytes()
            peripheral.write(BleCharacteristic.FILE_FORMAT, data)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Instructs the camera to start shooting a still image. Also, acquires the shooting status.
     *
     * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
     * Characteristic: FEC1805C-8905-4477-B862-BA5E447528A5
     *
     * @param complete Notification of end of shooting. If an error occurs, notify the argument.
     */
    @Throws(Throwable::class)
    fun takePicture(complete: (suspend (error: Throwable?) -> Unit)?) {
        thetaDevice.notifyScope.launch {
            val peripheral = thetaDevice.peripheral ?: run {
                complete?.invoke(ThetaBleApiException(ERROR_MESSAGE_NOT_CONNECTED))
                return@launch
            }

            if (thetaDevice.deferredTakePicture != null) {
                complete?.invoke(ThetaBleApiException("In the middle of taking a picture."))
                return@launch
            }
            try {
                val value: Byte = 1
                val data = value.toBytes()
                peripheral.write(BleCharacteristic.TAKE_PICTURE, data)
                if (complete != null) {
                    val deferred = CompletableDeferred<Unit>()
                    thetaDevice.deferredTakePicture = deferred
                    withTimeout(thetaDevice.timeout.timeoutTakePicture.toLong()) {
                        println("wait take picture")
                        deferred.await()
                        println("wait take picture end")
                        thetaDevice.deferredTakePicture = null
                    }
                    complete(null)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                thetaDevice.deferredTakePicture = null
                if (complete != null) {
                    complete(BluetoothException(e))
                }
            }
        }
    }
}
