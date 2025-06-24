package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ERROR_MESSAGE_EMPTY_DATA
import com.ricoh360.thetableclient.ERROR_MESSAGE_NOT_CONNECTED
import com.ricoh360.thetableclient.TIMEOUT_SCAN
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ThetaBle.BluetoothException
import com.ricoh360.thetableclient.ThetaBle.ThetaBleApiException
import com.ricoh360.thetableclient.service.data.ConnectedWifiInfo
import com.ricoh360.thetableclient.service.data.Proxy
import com.ricoh360.thetableclient.service.data.values.NetworkType
import com.ricoh360.thetableclient.service.data.values.WifiSecurityMode
import com.ricoh360.thetableclient.transferred.CameraConnectedWifiInfo
import com.ricoh360.thetableclient.transferred.NotifySsid
import com.ricoh360.thetableclient.transferred.SetAccessPointParams
import com.ricoh360.thetableclient.transferred.WlanNetworkType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * WLAN Control Command V2 Service
 *
 * Service: 3C6FEEB6-F335-4F93-A4BB-495F926DB409
 */
class WlanControlCommandV2 internal constructor(thetaDevice: ThetaBle.ThetaDevice) :
    ThetaService(
        BleService.WLAN_CONTROL_COMMAND_V2,
        thetaDevice
    ) {

    interface ScanCallback {
        /**
         * Notification of discovery of SSID
         *
         * @param ssid scanned wireless lan SSID
         */
        fun onNotify(ssid: String) {}

        /**
         * Notification of scan completion
         *
         * Called when a timeout or other event ends.
         *
         * @param ssidList Discovery of SSID list
         */
        fun onCompleted(ssidList: List<String>) {}
    }

    val ssidList: List<String> = mutableListOf()
    internal var scanTimeoutJob: Job? = null

    internal suspend fun cancelScanTimeoutJob() {
        scanTimeoutJob?.let {
            it.cancel()
            it.join()
            scanTimeoutJob = null
        }
    }

    internal suspend fun unregisterNotifyScan() {
        thetaDevice.observeManager?.unregisterBleObserver(
            BleCharacteristic.NOTIFICATION_SCANNED_SSID
        )
    }

    internal fun addSsid(ssid: String) {
        val ssidList = this.ssidList as MutableList
        if (ssidList.contains(ssid)) {
            return
        }
        ssidList.add(ssid)
    }

    /**
     * Set the network type
     *
     * Characteristic: 4B181146-EF3B-4619-8C82-1BA4A743ACFE
     *
     * @param networkType Network type
     * @exception ThetaBle.ThetaBleApiException If an error occurs in library.
     * @exception ThetaBle.BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun setNetworkType(networkType: NetworkType) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val jsonString = "{\"type\":\"${networkType.value}\"}"
            println("setNetworkType: $jsonString")
            peripheral.write(
                BleCharacteristic.WRITE_SET_NETWORK_TYPE,
                jsonString.encodeToByteArray()
            )
        } catch (e: ThetaBleApiException) {
            e.printStackTrace()
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BluetoothException(e)
        }
    }

    /**
     * Set network type notification.
     *
     * Notify characteristic: 4B181146-EF3B-4619-8C82-1BA4A743ACFE
     *
     * @param callback Notification function
     * @exception ThetaBle.ThetaBleApiException If an error occurs in library.
     * @exception ThetaBle.BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    fun setNetworkTypeNotify(callback: ((value: NetworkType?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        try {
            if (callback == null) {
                runBlocking {
                    thetaDevice.observeManager?.unregisterBleObserver(BleCharacteristic.WRITE_SET_NETWORK_TYPE)
                }
                return
            }
            thetaDevice.observeManager?.registerBleObserver(BleCharacteristic.WRITE_SET_NETWORK_TYPE,
                onNotify = {
                    if (it.isEmpty()) {
                        callback(null, ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                    } else {
                        try {
                            val jsonString = it.decodeToString()
                            println("Notify NetworkType: $jsonString")
                            callback(WlanNetworkType.decode(jsonString), null)
                        } catch (e: SerializationException) {
                            callback(
                                null,
                                ThetaBle.ThetaBleSerializationException(
                                    e.message ?: ERROR_MESSAGE_JSON_DECODE
                                )
                            )
                        } catch (e: Throwable) {
                            callback(null, e)
                        }
                    }
                })
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }

    /**
     * Acquires the Wi-Fi/LAN/LTE connection status
     *
     * Characteristic: 01DFF9FF-00FA-44DD-AA6A-71D5E537ABCF
     *
     * @return Wi-Fi/LAN/LTE connection status
     * @exception ThetaBle.ThetaBleApiException If an error occurs in library.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException If an error occurs in bluetooth.
     */
    @Throws(Throwable::class)
    suspend fun getConnectedWifiInfo(): ConnectedWifiInfo {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )
        try {
            val data = peripheral.read(BleCharacteristic.READ_CONNECTED_WIFI_INFO)
            if (data.isEmpty()) {
                throw ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA)
            }
            val jsonString = data.decodeToString()
            println("getConnectedWifiInfo: $jsonString")
            return ConnectedWifiInfo(CameraConnectedWifiInfo.decode(jsonString))
        } catch (e: ThetaBleApiException) {
            throw e
        } catch (e: SerializationException) {
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            throw BluetoothException(e)
        }
    }

    /**
     * Set Wi-Fi/LAN/LTE connection status notification
     *
     * Notify characteristic: A90381FC-2DDA-4EED-B24B-60F3E6651134
     *
     * @param callback Notification function
     * @exception ThetaBle.ThetaBleApiException If an error occurs in library.
     */
    @Throws(Throwable::class)
    fun setConnectedWifiInfoNotify(callback: ((connectedWifiInfo: ConnectedWifiInfo?, error: Throwable?) -> Unit)?) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(ERROR_MESSAGE_NOT_CONNECTED)
        try {
            if (callback == null) {
                runBlocking {
                    thetaDevice.observeManager?.unregisterBleObserver(BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO)
                }
                return
            }
            thetaDevice.observeManager?.registerBleObserver(
                BleCharacteristic.NOTIFICATION_CONNECTED_WIFI_INFO,
                onNotify = { data ->
                    if (data.isEmpty()) {
                        callback(null, ThetaBleApiException(ERROR_MESSAGE_EMPTY_DATA))
                    } else {
                        try {
                            val jsonString = data.decodeToString()
                            println("Notify ConnectedWifiInfo: $jsonString")
                            val connectedWifiInfo =
                                ConnectedWifiInfo(CameraConnectedWifiInfo.decode(jsonString))
                            callback(connectedWifiInfo, null)
                        } catch (e: SerializationException) {
                            callback(
                                null,
                                ThetaBle.ThetaBleSerializationException(
                                    e.message ?: ERROR_MESSAGE_JSON_DECODE
                                )
                            )
                        } catch (e: Throwable) {
                            callback(null, ThetaBleApiException(e.message ?: e.toString()))
                        }
                    }
                })
        } catch (e: ThetaBleApiException) {
            throw e
        }
    }

    /**
     * Start scanning for SSID
     *
     * Change the network type to SCAN and start scanning for SSID
     *
     * Notify characteristic: 60EEDCCC-426A-49CF-9AE1-F602284703D7
     *
     * @param timeout Timeout of scanning
     * @param callback Notification function
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaApplicationErrorException
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun scanSsidStart(
        timeout: Int = TIMEOUT_SCAN,
        callback: ScanCallback
    ) {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        scanSsidStop()
        val ssidList = ssidList as MutableList
        ssidList.clear()
        try {
            var isStarted = false
            thetaDevice.observeManager?.registerBleObserver(
                BleCharacteristic.NOTIFICATION_SCANNED_SSID,
                {
                    try {
                        val jsonString = it.decodeToString()
                        val ssid = Json.decodeFromString<NotifySsid>(jsonString)
                        addSsid(ssid.ssid)
                        callback.onNotify(ssid.ssid)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }) {
                if (isStarted) {
                    callback.onCompleted(ssidList)
                    runBlocking {
                        cancelScanTimeoutJob()
                    }
                }
            }
            delay(1)    // wait registerBleObserver
            setNetworkType(NetworkType.SCAN)
            isStarted = true
        } catch (e: ThetaBleApiException) {
            unregisterNotifyScan()
            throw e
        } catch (e: ThetaBle.ThetaApplicationErrorException) {
            unregisterNotifyScan()
            throw e
        } catch (e: SerializationException) {
            unregisterNotifyScan()
            throw ThetaBle.ThetaBleSerializationException(e.message ?: ERROR_MESSAGE_JSON_DECODE)
        } catch (e: Throwable) {
            unregisterNotifyScan()
            throw BluetoothException(e)
        }

        scanTimeoutJob = CoroutineScope(Dispatchers.Default).launch {
            delay(timeout.toLong())
            scanTimeoutJob = null   // To not cancel this job
            scanSsidStop()
        }
    }

    /**
     * Scanning stop for SSID
     *
     * Notify characteristic: 60EEDCCC-426A-49CF-9AE1-F602284703D7
     *
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     */
    @Throws(Throwable::class)
    suspend fun scanSsidStop() {
        thetaDevice.peripheral ?: throw ThetaBleApiException(
            ERROR_MESSAGE_NOT_CONNECTED
        )
        cancelScanTimeoutJob()
        unregisterNotifyScan()
        setNetworkType(NetworkType.CLIENT)
    }

    @Throws(Throwable::class)
    internal suspend fun setAccessPoint(
        ssid: String,
        ssidStealth: Boolean,
        security: WifiSecurityMode = WifiSecurityMode.NONE,
        password: String?,
        connectionPriority: Int,
        ipAddressAllocation: String,
        ipAddress: String? = null,
        subnetMask: String? = null,
        defaultGateway: String? = null,
        proxy: Proxy? = null,
    ) {
        val peripheral =
            thetaDevice.peripheral ?: throw ThetaBleApiException(
                ERROR_MESSAGE_NOT_CONNECTED
            )

        val params = SetAccessPointParams(
            ssid = ssid,
            ssidStealth = ssidStealth,
            security = security.value,
            password = password,
            connectionPriority = connectionPriority,
            ipAddressAllocation = ipAddressAllocation,
            ipAddress = ipAddress,
            subnetMask = subnetMask,
            defaultGateway = defaultGateway,
            proxy = when (proxy) {
                null -> com.ricoh360.thetableclient.transferred.Proxy(use = false)
                else -> com.ricoh360.thetableclient.transferred.Proxy(proxy)
            },
        )
        try {
            val jsonString = Json.encodeToString(params)
//            println("setAccessPoint: $jsonString")
            peripheral.write(
                BleCharacteristic.WRITE_CONNECT_WIFI,
                jsonString.encodeToByteArray()
            )
        } catch (e: ThetaBleApiException) {
            e.printStackTrace()
            throw e
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BluetoothException(e)
        }
    }

    /**
     * Set access point. IP address is set dynamically.
     *
     * Characteristic: 4980ACBA-E2A5-460B-998B-9AD4C49FBE39
     *
     * @param ssid SSID of the access point.
     * @param ssidStealth True if SSID stealth is enabled.
     * @param security Authentication mode.
     * @param password Password. If [security] is "NONE", pass empty String.
     * @param connectionPriority Connection priority 1 to 5.
     * @param proxy Proxy information to be used for the access point.
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun setAccessPointDynamically(
        ssid: String,
        ssidStealth: Boolean = false,
        security: WifiSecurityMode = WifiSecurityMode.NONE,
        password: String = "",
        connectionPriority: Int = 1,
        proxy: Proxy? = null,
    ) {
        setAccessPoint(
            ssid = ssid,
            ssidStealth = ssidStealth,
            security = security,
            password = password,
            connectionPriority = connectionPriority,
            ipAddressAllocation = "dynamic",
            proxy = proxy,
        )
    }

    /**
     * Set access point. IP address is set statically.
     *
     * Characteristic: 4980ACBA-E2A5-460B-998B-9AD4C49FBE39
     *
     * @param ssid SSID of the access point.
     * @param ssidStealth True if SSID stealth is enabled.
     * @param security Authentication mode.
     * @param password Password. If [security] is "NONE", pass empty String.
     * @param connectionPriority Connection priority 1 to 5.
     * @param ipAddress IP address assigns to Theta.
     * @param subnetMask Subnet mask.
     * @param defaultGateway Default gateway.
     * @param proxy Proxy information to be used for the access point.
     * @exception ThetaBle.ThetaBleApiException If an error occurs in THETA.
     * @exception ThetaBle.ThetaBleSerializationException Json serialization error.
     * @exception ThetaBle.BluetoothException
     */
    @Throws(Throwable::class)
    suspend fun setAccessPointStatically(
        ssid: String,
        ssidStealth: Boolean = false,
        security: WifiSecurityMode = WifiSecurityMode.NONE,
        password: String = "",
        connectionPriority: Int = 1,
        ipAddress: String,
        subnetMask: String,
        defaultGateway: String,
        proxy: Proxy? = null,
    ) {
        setAccessPoint(
            ssid = ssid,
            ssidStealth = ssidStealth,
            security = security,
            password = password,
            connectionPriority = connectionPriority,
            ipAddressAllocation = "static",
            ipAddress = ipAddress,
            subnetMask = subnetMask,
            defaultGateway = defaultGateway,
            proxy = proxy,
        )
    }
}
