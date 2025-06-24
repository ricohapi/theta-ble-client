package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.ERROR_MESSAGE_UNKNOWN_VALUE
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.service.data.values.WlanPasswordState

/**
 * WLAN Control Command Service
 *
 * Service: F37F568F-9071-445D-A938-5441F2E82399
 */
class WlanControlCommand internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.WLAN_CONTROL_COMMAND,
        thetaDevice
    ) {

    /**
     * Read WLAN password state.
     *
     * Characteristic: E522112A-5689-4901-0803-0520637DC895
     *
     * @return start-up status
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getWlanPasswordState(): WlanPasswordState {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.WLAN_PASSWORD_STATE)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return WlanPasswordState.getFromBle(data[0])
                ?: throw ThetaBleApiException("$ERROR_MESSAGE_UNKNOWN_VALUE ${data[0]}")
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }
}
