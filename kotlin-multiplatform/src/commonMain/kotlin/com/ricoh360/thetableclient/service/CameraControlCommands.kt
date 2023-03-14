package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.service.data.ble.PluginList
import com.ricoh360.thetableclient.service.data.ble.PluginOrders

/**
 * Camera Control Commands Service
 *
 * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
 */
class CameraControlCommands internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.CAMERA_CONTROL_COMMANDS,
        thetaDevice
    ) {

    /**
     * Acquires a list of installed plugins.
     *
     * RICOH THETA V firmware v2.21.1 or later.
     *
     * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
     * Characteristic: E83264B2-C52D-454E-95BD-6485DE912430
     *
     * @return plugin number list
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getPluginList(): PluginList {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.PLUGIN_LIST)
            return PluginList.newInstance(data)
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the plugins for plugin mode.
     *
     * RICOH THETA Z1 or later.
     *
     * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
     * Characteristic: 8F710EDC-6F9B-45D4-A5F7-E6EDA304E790
     *
     * @return plugin order
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getPluginOrders(): PluginOrders {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.PLUGIN_ORDERS)
            return PluginOrders.newInstance(data)
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set the plugins for plugin mode.
     *
     * RICOH THETA Z1 or later.
     *
     * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
     * Characteristic: 8F710EDC-6F9B-45D4-A5F7-E6EDA304E790
     *
     * @param value plugin order
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun setPluginOrders(value: PluginOrders) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = value.toBleData()
            peripheral.write(BleCharacteristic.PLUGIN_ORDERS, data)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }
}
