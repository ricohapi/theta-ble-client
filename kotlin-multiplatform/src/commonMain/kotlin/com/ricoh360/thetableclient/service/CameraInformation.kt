package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException

/**
 * Camera Information Service
 *
 * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
 */
class CameraInformation internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.CAMERA_INFORMATION,
        thetaDevice
    ) {

    /**
     * Acquires the firmware version of the camera.
     *
     * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
     * Characteristic: B4EB8905-7411-40A6-A367-2834C2157EA7
     *
     * @return Firmware revision.
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getFirmwareRevision(): String {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.FIRMWARE_REVISION)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return data.decodeToString()
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the manufacturer name of the camera.
     *
     * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
     * Characteristic: F5666A48-6A74-40AE-A817-3C9B3EFB59A6
     *
     * @return Manufacturer Name.
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getManufacturerName(): String {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.MANUFACTURER_NAME)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return data.decodeToString()
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the model name of the camera.
     *
     * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
     * Characteristic: 35FE6272-6AA5-44D9-88E1-F09427F51A71
     *
     * @return Model Number of THETA.
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getModelNumber(): String {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.MODEL_NUMBER)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return data.decodeToString()
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the serial name of the camera.
     *
     * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
     * Characteristic: 0D2FC4D5-5CB3-4CDE-B519-445E599957D8
     *
     * @return Serial Number.
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getSerialNumber(): String {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.SERIAL_NUMBER)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return data.decodeToString()
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the MAC address of wireless LAN.
     *
     * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
     * Characteristic: 1C5C6C55-8E57-4B32-AD80-B124AE229DEC
     *
     * @return WLAN MAC Address.
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getWlanMacAddress(): String {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.WLAN_MAC_ADDRESS)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return data.decodeToString()
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Acquires the MAC address of Bluetooth.
     *
     * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
     * Characteristic: 97E34DA2-2E1A-405B-B80D-F8F0AA9CC51C
     *
     * @return Bluetooth MAC Address.
     * @exception ThetaBleApiException If an error occurs in library.
     * @exception BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getBluetoothMacAddress(): String {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.BLUETOOTH_MAC_ADDRESS)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            return data.decodeToString()
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }
}
