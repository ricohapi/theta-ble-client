package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.ERROR_MESSAGE_UNKNOWN_VALUE
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.service.data.ble.PluginControl
import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.CommandErrorDescription
import com.ricoh360.thetableclient.toBytes

/**
 * Camera Status Command Service
 *
 * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
 */
class CameraStatusCommand internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.CAMERA_STATUS_COMMAND,
        thetaDevice
    ) {

    /**
     * Acquires the battery level of the camera.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: 875FC41D-4980-434C-A653-FD4A4D4410C4
     *
     * @return battery level. 0 to 100
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getBatteryLevel(): Int {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.BATTERY_LEVEL)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return data[0].toInt()
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the battery level notification.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: 875FC41D-4980-434C-A653-FD4A4D4410C4
     *
     * @param callback Notification function
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    fun setBatteryLevelNotify(callback: ((value: Int?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        try {
            callback ?: run {
                thetaDevice.observeManager?.setOnNotify(BleCharacteristic.BATTERY_LEVEL, null)
                return
            }
            thetaDevice.observeManager?.setOnNotify(BleCharacteristic.BATTERY_LEVEL) {
                if (it.isEmpty()) {
                    callback(null, ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                } else {
                    callback(it[0].toInt(), null)
                }
            }
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }

    /**
     * Acquires the charging state of the camera.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: 5429B6A0-66D6-491B-B906-902737D5442F
     *
     * @return charging state
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getBatteryStatus(): ChargingState {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.BATTERY_STATUS)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return ChargingState.getFromBle(data[0])
                ?: throw ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${data[0]}")
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the charging state notification.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: 5429B6A0-66D6-491B-B906-902737D5442F
     *
     * @param callback Notification function
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    fun setBatteryStatusNotify(callback: ((value: ChargingState?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        try {
            callback ?: run {
                thetaDevice.observeManager?.setOnNotify(BleCharacteristic.BATTERY_STATUS, null)
                return
            }
            thetaDevice.observeManager?.setOnNotify(BleCharacteristic.BATTERY_STATUS) {
                if (it.isEmpty()) {
                    callback(null, ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                } else {
                    ChargingState.getFromBle(it[0])?.run {
                        callback(this, null)
                    } ?: run {
                        callback(
                            null,
                            ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${it[0]}"),
                        )
                    }
                }
            }
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }

    /**
     * Acquires the camera's start-up status.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: B58CE84C-0666-4DE9-BEC8-2D27B27B3211
     *
     * @return start-up status
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getCameraPower(): CameraPower {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.CAMERA_POWER)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return CameraPower.getFromBle(data[0])
                ?: throw ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${data[0]}")
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the camera's start-up status.
     *
     * When the camera is turned off or put to sleep, it is necessary to reauthorize from connect.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: B58CE84C-0666-4DE9-BEC8-2D27B27B3211
     *
     * @param value start-up status
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun setCameraPower(value: CameraPower) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        value.ble ?: throw ThetaBleApiException(
            ERROR_MESSAGE_UNKNOWN_VALUE
        )
        try {
            val data = value.ble.toBytes()
            peripheral.write(BleCharacteristic.CAMERA_POWER, data)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the camera's start-up status notification.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: B58CE84C-0666-4DE9-BEC8-2D27B27B3211
     *
     * @param callback Notification function
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    fun setCameraPowerNotify(callback: ((value: CameraPower?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        try {
            callback ?: run {
                thetaDevice.observeManager?.setOnNotify(BleCharacteristic.CAMERA_POWER, null)
                return
            }
            thetaDevice.observeManager?.setOnNotify(BleCharacteristic.CAMERA_POWER) {
                if (it.isEmpty()) {
                    callback(null, ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                } else {
                    CameraPower.getFromBle(it[0])?.run {
                        callback(this, null)
                    } ?: run {
                        callback(
                            null,
                            ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${it[0]}"),
                        )
                    }
                }
            }
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }

    /**
     * Acquires the camera's error description in detail notification.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: 4B03D05E-02D2-412B-A20B-578AE82B9C01
     *
     * @param callback Notification function
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    fun setCommandErrorDescriptionNotify(callback: ((value: CommandErrorDescription?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        try {
            callback ?: run {
                thetaDevice.observeManager?.setOnNotify(
                    BleCharacteristic.COMMAND_ERROR_DESCRIPTION,
                    null
                )
                return
            }
            thetaDevice.observeManager?.setOnNotify(BleCharacteristic.COMMAND_ERROR_DESCRIPTION) {
                if (it.isEmpty()) {
                    callback(null, ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                } else {
                    CommandErrorDescription.getFromBle(it[0])?.run {
                        callback(this, null)
                    } ?: run {
                        callback(
                            null,
                            ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${it[0]}"),
                        )
                    }
                }
            }
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }

    /**
     * Acquires the plugin power status.
     *
     * RICOH THETA V firmware v2.21.1 or later.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: A88732D5-6786-4312-9364-B9A4514DC123
     *
     * @return plugin control
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getPluginControl(): PluginControl {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.PLUGIN_CONTROL)
            return PluginControl.newInstance(data)
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Starts or stops plugin.
     *
     * RICOH THETA V firmware v2.21.1 or later.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: A88732D5-6786-4312-9364-B9A4514DC123
     *
     * @param value plugin control
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun setPluginControl(value: PluginControl) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = value.toBleData()
            peripheral.write(BleCharacteristic.PLUGIN_CONTROL, data)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the plugin power status notification.
     *
     * RICOH THETA V firmware v2.21.1 or later.
     *
     * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
     * Characteristic: A88732D5-6786-4312-9364-B9A4514DC123
     *
     * @param callback Notification function
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    fun setPluginControlNotify(callback: ((value: PluginControl?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        try {
            callback ?: run {
                thetaDevice.observeManager?.setOnNotify(BleCharacteristic.PLUGIN_CONTROL, null)
                return
            }
            thetaDevice.observeManager?.setOnNotify(BleCharacteristic.PLUGIN_CONTROL) {
                try {
                    PluginControl.newInstance(it)
                    callback(PluginControl.newInstance(it), null)
                } catch (e: Throwable) {
                    callback(null, e)
                }
            }
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }
}
