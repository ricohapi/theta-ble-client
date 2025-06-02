package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.TIMEOUT_SCAN
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.service.data.PeripheralDevice
import com.ricoh360.thetableclient.transferred.Peripheral
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerializationException

/**
 * Bluetooth Control Command Service
 *
 * Service: 0F291746-0C80-4726-87A7-3C501FD3B4B6
 */
class BluetoothControlCommand internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.BLUETOOTH_CONTROL_COMMAND,
        thetaDevice
    ) {

    /**
     * Callback of scanning
     */
    interface ScanCallback {
        /**
         * Notification of discovery of peripheral device
         *
         * @param peripheralDevice Discovery of peripheral device
         */
        fun onNotify(peripheralDevice: PeripheralDevice) {}

        /**
         * Notification of scan completion
         *
         * Called when a timeout or other event ends.
         *
         * @param peripheralDeviceList Discovery of peripheral device list
         */
        fun onCompleted(peripheralDeviceList: List<PeripheralDevice>) {}
    }

    /**
     * Scanned peripheral device list
     */
    val peripheralDeviceList: List<PeripheralDevice> = mutableListOf()

    internal var scanTimeoutJob: Job? = null

    internal fun addPeripheralDevice(peripheralDevice: PeripheralDevice): PeripheralDevice {
        val peripheralDeviceList = peripheralDeviceList as MutableList
        peripheralDeviceList.find { it.macAddress == peripheralDevice.macAddress }?.let {
            it.update(peripheralDevice)
            return it
        }
        peripheralDeviceList.add(peripheralDevice)
        return peripheralDevice

    }

    internal suspend fun cancelScanTimeoutJob() {
        scanTimeoutJob?.let {
            it.cancel()
            it.join()
            scanTimeoutJob = null
        }
    }

    internal suspend fun unregisterNotifyScan() {
        thetaDevice.observeManager?.unregisterBleObserver(
            BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE
        )
    }

    /**
     * Scanning start for peripheral device
     *
     * ScanCallback.onCompleted is called in a timeout.
     *
     * Scan characteristic: 03F423B3-A71F-4D70-A4BC-437C3137AFCD
     * Notify characteristic: 7B058429-DF5C-4454-88A2-C81086131C30
     *
     * @param timeout Timeout of scanning
     * @param callback Notification function
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaApplicationErrorException
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun scanPeripheralDeviceStart(
        timeout: Int = TIMEOUT_SCAN,
        callback: ScanCallback
    ) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        scanPeripheralDeviceStop()
        val peripheralDeviceList = peripheralDeviceList as MutableList
        peripheralDeviceList.clear()
        try {
            thetaDevice.observeManager?.registerBleObserver(
                BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE,
                {
                    try {
                        val jsonString = it.decodeToString()
                        val device =
                            addPeripheralDevice(PeripheralDevice(Peripheral.decode(jsonString)))
                        callback.onNotify(device)
                    } catch (e: SerializationException) {
                        e.printStackTrace()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }) {
                callback.onCompleted(peripheralDeviceList)
            }
            delay(1)    // wait registerBleObserver
            val jsonString = "{\"type\":\"SCAN\"}"
            val data = jsonString.encodeToByteArray()
            peripheral.write(BleCharacteristic.SCAN_BLUETOOTH_PERIPHERAL_DEVICE, data)
        } catch (e: ThetaBleApiException) {
            e.printStackTrace()
            unregisterNotifyScan()
            throw e
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            e.printStackTrace()
            unregisterNotifyScan()
            throw e
        } catch (e: SerializationException) {
            e.printStackTrace()
            unregisterNotifyScan()
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            e.printStackTrace()
            unregisterNotifyScan()
            throw BluetoothException(e)
        }
        scanTimeoutJob = CoroutineScope(Dispatchers.Default).launch {
            delay(timeout.toLong())
            scanTimeoutJob = null
            thetaDevice.observeManager?.unregisterBleObserver(
                BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE
            )
        }
    }

    /**
     * Scanning stop for peripheral device
     *
     * Notify characteristic: 7B058429-DF5C-4454-88A2-C81086131C30
     *
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     */
    @Throws(Throwable::class)
    suspend fun scanPeripheralDeviceStop() {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        cancelScanTimeoutJob()
        thetaDevice.observeManager?.unregisterBleObserver(
            BleCharacteristic.NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE
        )
    }

    /**
     * Scanning for peripheral device
     *
     * Blocking until timeout.
     *
     * Scan characteristic: 03F423B3-A71F-4D70-A4BC-437C3137AFCD
     * Notify characteristic: 7B058429-DF5C-4454-88A2-C81086131C30
     *
     * @param timeout Timeout of scanning
     * @return Scanned peripheral device list.
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaApplicationErrorException
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun scanPeripheralDevice(timeout: Int = TIMEOUT_SCAN): List<PeripheralDevice> {
        val deferred = CompletableDeferred<Unit>()
        scanPeripheralDeviceStart(timeout, object : ScanCallback {
            override fun onCompleted(peripheralDeviceList: List<PeripheralDevice>) {
                super.onCompleted(peripheralDeviceList)
                deferred.complete(Unit)
            }
        })
        withTimeoutOrNull(timeout.toLong() + 100) {
            deferred.await()
        }
        return peripheralDeviceList
    }

    /**
     * Connect to peripheral device
     *
     * Characteristic: 1FA3E524-BAD5-4F75-808B-94487A4B9024
     *
     * @param peripheralDevice Peripheral device
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaApplicationErrorException
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun connectPeripheralDevice(peripheralDevice: PeripheralDevice) {
        connectPeripheralDevice(peripheralDevice.macAddress)
    }

    /**
     * Connect to peripheral device
     *
     * Characteristic: 1FA3E524-BAD5-4F75-808B-94487A4B9024
     *
     * @param macAddress MAC address of peripheral device
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaApplicationErrorException
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun connectPeripheralDevice(macAddress: String) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val jsonString = "{\"macAddress\":\"${macAddress}\"}"
            peripheral.write(
                BleCharacteristic.CONNECT_BLUETOOTH_PERIPHERAL_DEVICE,
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

    /**
     * Unsubscribe from peripheral device
     *
     * Characteristic: 61A37C82-D635-43B9-A973-5857EFE64094
     *
     * @param peripheralDevice Peripheral device
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaApplicationErrorException
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun deletePeripheralDevice(peripheralDevice: PeripheralDevice) {
        deletePeripheralDevice(peripheralDevice.macAddress)
    }

    /**
     * Unsubscribe from peripheral device
     *
     * Characteristic: 61A37C82-D635-43B9-A973-5857EFE64094
     *
     * @param macAddress MAC address of peripheral device
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaApplicationErrorException
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun deletePeripheralDevice(macAddress: String) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val jsonString = "{\"macAddress\":\"${macAddress}\"}"
            peripheral.write(
                BleCharacteristic.DELETE_BLUETOOTH_PERIPHERAL_DEVICE,
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
}
