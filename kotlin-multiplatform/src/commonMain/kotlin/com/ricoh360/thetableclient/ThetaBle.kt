package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.BleAdvertisement
import com.ricoh360.thetableclient.ble.BlePeripheral
import com.ricoh360.thetableclient.ble.getScanner
import com.ricoh360.thetableclient.service.CameraControlCommandV2
import com.ricoh360.thetableclient.service.CameraControlCommands
import com.ricoh360.thetableclient.service.CameraInformation
import com.ricoh360.thetableclient.service.CameraStatusCommand
import com.ricoh360.thetableclient.service.ShootingControlCommand
import com.ricoh360.thetableclient.service.ThetaService
import com.ricoh360.thetableclient.service.data.values.ThetaModel
import kotlinx.coroutines.*

internal const val TIMEOUT_SCAN = 30_000
internal const val TIMEOUT_PERIPHERAL = 1_000
internal const val TIMEOUT_CONNECT = 5_000
internal const val TIMEOUT_TAKE_PICTURE = 10_000
internal const val WAIT_SCAN = 3_000
internal const val MTU_SIZE = 512

internal const val ERROR_MESSAGE_NOT_CONNECTED = "Not connected."
internal const val ERROR_MESSAGE_EMPTY_DATA = "Empty data."
internal const val ERROR_MESSAGE_UNKNOWN_VALUE = "Unknown value."
internal const val ERROR_MESSAGE_UNSUPPORTED_VALUE = "Unsupported value."

/**
 * Wrapper of THETA Bluetooth API.
 *
 * https://github.com/ricohapi/theta-api-specs/tree/main/theta-bluetooth-api
 */
class ThetaBle internal constructor() {
    /**
     * Configuration of timeout.
     */
    data class Timeout(
        /**
         * Specifies a time period (in milliseconds) required to scan THETA.
         */
        var timeoutScan: Int = TIMEOUT_SCAN,

        /**
         * Specifies a time period (in milliseconds) required to process an ble peripheral.
         */
        var timeoutPeripheral: Int = TIMEOUT_PERIPHERAL,

        /**
         * Specifies a time period (in milliseconds) required to connection with THETA.
         */
        var timeoutConnect: Int = TIMEOUT_CONNECT,

        /**
         * Specifies a time period (in milliseconds) required to take a picture.
         */
        var timeoutTakePicture: Int = TIMEOUT_TAKE_PICTURE,
    ) {
        constructor() : this(
            timeoutScan = TIMEOUT_SCAN,
            timeoutPeripheral = TIMEOUT_PERIPHERAL,
            timeoutConnect = TIMEOUT_CONNECT,
            timeoutTakePicture = TIMEOUT_TAKE_PICTURE,
        )
    }

    companion object {
        var waitScan = WAIT_SCAN

        /**
         * Scan for nearby THETA.
         *
         * @return Found THETA list.
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun scan(): List<ThetaDevice> {
            return scan(null)
        }

        /**
         * Scan for nearby THETA.
         *
         * @param timeout Configuration of timeout.
         * @return Found THETA list.
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun scan(timeout: Timeout?): List<ThetaDevice> {
            return scanImpl(null, timeout ?: Timeout())
        }

        /**
         * Scan for nearby THETA.
         *
         * @param name Name of THETA to connect.
         * @param timeout Configuration of timeout.
         * @return Found THETA.
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun scan(name: String, timeout: Timeout? = null): ThetaDevice? {
            return scanImpl(name, timeout ?: Timeout()).firstOrNull()
        }

        /**
         * Scan for nearby THETA.
         *
         * @param name Name of THETA to connect.
         * @return Found THETA.
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun scan(name: String): ThetaDevice? {
            return scan(name, null)
        }

        @Throws(Throwable::class)
        internal suspend fun scanImpl(
            name: String?,
            timeout: Timeout,
        ): List<ThetaDevice> {
            try {
                val scanner = getScanner(timeout.timeoutScan, name)

                scanner.init()

                delay(waitScan.toLong()) // Need to wait a little only the first time.

                val advertisementList = scanner.scan()
                waitScan = 0 // No need to wait for a second time.

                return advertisementList.let { list ->
                    list.map {
                        ThetaDevice(it, timeout)
                    }
                }
            } catch (e: ThetaBleException) {
                throw e
            } catch (e: Throwable) {
                throw BluetoothException(e)
            }
        }

        /**
         * Scan for nearby THETA SSID.
         *
         * @param model THETA model.
         * @param timeout Specifies a time period (in milliseconds) required to scan THETA.
         * @return Found THETA SSID list. (SSID, Default password)[]
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun scanThetaSsid(
            model: ThetaModel? = null,
            timeout: Int? = null,
        ): List<Pair<String, String>> {
            model?.let {
                val prefix = ThetaDevice.serialPrefix[it]
                prefix ?: throw ThetaBleApiException(ERROR_MESSAGE_UNSUPPORTED_VALUE)
            }
            val scanTimeout = Timeout(
                timeoutScan = timeout ?: TIMEOUT_SCAN
            )
            val thetaList = scan(scanTimeout)
            val ssidList = mutableListOf<Pair<String, String>>()
            thetaList.forEach {
                when (model) {
                    null -> {
                        ssidList.add(it.getSsid(ThetaModel.THETA_X))
                        ssidList.add(it.getSsid(ThetaModel.THETA_Z1))
                        ssidList.add(it.getSsid(ThetaModel.THETA_SC2))
                        ssidList.add(it.getSsid(ThetaModel.THETA_V))
                    }
                    else -> ssidList.add(it.getSsid(model))
                }
            }
            return ssidList
        }
    }

    /**
     * Base exception of ThetaBle
     */
    abstract class ThetaBleException : RuntimeException {
        constructor(message: String?) : super(message)
        constructor(cause: Throwable?) : super(cause)
    }

    /**
     * Thrown if an error occurs on ThetaBLE.
     */
    class ThetaBleApiException(message: String) : ThetaBleException(message)

    /**
     * Thrown if an error occurs on ThetaBLE.
     */
    class ThetaBleSerializationException(message: String) : ThetaBleException(message)

    /**
     * Thrown if an error occurs on bluetooth.
     */
    class BluetoothException : ThetaBleException {
        constructor(message: String?) : super(message)
        constructor(cause: Throwable?) : super(cause)
    }

    /**
     * THETA camera device
     *
     * Call ThetaBle.scan() to obtain.
     */
    class ThetaDevice internal constructor(
        internal val advertisement: BleAdvertisement,
        /**
         * Configuration of timeout.
         */
        val timeout: Timeout = Timeout(),
    ) {
        companion object {
            internal val serialPrefix = mapOf(
                ThetaModel.THETA_V to "YL",
                ThetaModel.THETA_SC2 to "YP",
                ThetaModel.THETA_SC2_B to "YP",
                ThetaModel.THETA_Z1 to "YN",
                ThetaModel.THETA_X to "YR",
            )
        }

        internal var scope = CoroutineScope(Dispatchers.Default)

        @OptIn(ExperimentalCoroutinesApi::class)
        internal val notifyScope = CoroutineScope(newSingleThreadContext("NotifyScope"))

        internal var peripheral: BlePeripheral? = null
        internal var deferredTakePicture: CompletableDeferred<Unit>? = null
        internal var observeManager: BleObserveManager? = null
        internal val notifyCharacteristicList = listOf(
            BleCharacteristic.BATTERY_LEVEL,
            BleCharacteristic.BATTERY_STATUS,
            BleCharacteristic.CAMERA_POWER,
            BleCharacteristic.COMMAND_ERROR_DESCRIPTION,
            BleCharacteristic.PLUGIN_CONTROL,
            BleCharacteristic.NOTIFY_STATE,
        )

        internal var _uuid: String? = null

        /**
         * Name of THETA
         */
        val name: String
            get() = advertisement.name

        /**
         * UUID used for authentication
         */
        val uuid: String?
            get() = _uuid

        /**
         * Supported service list
         */
        val serviceList: List<ThetaService> = mutableListOf()

        /**
         * Acquire THETA SSID.
         *
         * @param model THETA model.
         * @return THETA SSID list. (SSID, Default password)[]
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        fun getSsid(model: ThetaModel): Pair<String, String> {
            val prefix = serialPrefix[model]
            prefix ?: throw ThetaBleApiException(ERROR_MESSAGE_UNSUPPORTED_VALUE)
            return Pair("THETA$prefix$name.OSC", name)
        }

        /**
         * Acquire THETA service
         *
         * @param service Service of THETA.
         * @return Supported service.
         */
        fun getService(service: BleService): ThetaService? {
            return serviceList.firstOrNull { it.service == service }
        }

        /**
         * Camera Information Service
         */
        var cameraInformation: CameraInformation? = null

        /**
         * Camera Status Command Service
         */
        var cameraStatusCommand: CameraStatusCommand? = null

        /**
         * Camera Control Commands Service
         */
        var cameraControlCommands: CameraControlCommands? = null

        /**
         * Shooting Control Command
         */
        var shootingControlCommand: ShootingControlCommand? = null

        /**
         * Camera Control Command v2 Service
         */
        var cameraControlCommandV2: CameraControlCommandV2? = null

        internal suspend fun createPeripheral(): BlePeripheral {
            val deferred = CompletableDeferred<BlePeripheral>()
            scope.launch {
                try {
                    val peripheral = advertisement.newPeripheral(this)
                    deferred.complete(peripheral)
                } catch (e: Throwable) {
                    deferred.completeExceptionally(e)
                }
            }
            val peripheral = withTimeoutOrNull(timeout.timeoutPeripheral.toLong()) {
                deferred.await()
            } ?: throw BluetoothException("Timeout for get peripheral")

            return peripheral
        }

        internal fun registerTakePictureObserver() {
            val peripheral = peripheral ?: return
            if (!peripheral.contain(BleCharacteristic.TAKE_PICTURE)) {
                return
            }
            notifyScope.launch {
                peripheral.observe(BleCharacteristic.TAKE_PICTURE) {
                    if (it[0].toInt() == 0) {
                        println("observe end")
                        deferredTakePicture?.apply {
                            this.complete(Unit)
                        }
                    }
                }
            }
        }

        internal fun registerServices() {
            val peripheral = peripheral ?: return
            if (peripheral.contain(BleService.CAMERA_INFORMATION)) {
                val service = CameraInformation(this)
                (serviceList as MutableList).add(service)
                cameraInformation = service
            }

            if (peripheral.contain(BleService.CAMERA_STATUS_COMMAND)) {
                val service = CameraStatusCommand(this)
                (serviceList as MutableList).add(service)
                cameraStatusCommand = service
            }

            if (peripheral.contain(BleService.CAMERA_CONTROL_COMMANDS)) {
                val service = CameraControlCommands(this)
                (serviceList as MutableList).add(service)
                cameraControlCommands = service
            }

            if (peripheral.contain(BleService.SHOOTING_CONTROL_COMMAND)) {
                val service = ShootingControlCommand(this)
                (serviceList as MutableList).add(service)
                shootingControlCommand = service
            }

            if (peripheral.contain(BleService.CAMERA_CONTROL_COMMAND_V2)) {
                val service = CameraControlCommandV2(this)
                (serviceList as MutableList).add(service)
                cameraControlCommandV2 = service
            }
        }

        /**
         * Connect to THETA.
         *
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun connect() {
            connect(null)
        }

        /**
         * Connect to THETA.
         *
         * @param uuid UUID used for authentication.
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun connect(uuid: String? = null) {
            try {
                if (scope.isActive) {
                    cleanup()
                }
                scope = CoroutineScope(Dispatchers.Default)
                if (peripheral == null) {
                    peripheral = createPeripheral()
                    println("connect ready: ${peripheral?.name}")
                }
                val peripheral = peripheral ?: throw BluetoothException("Error. get peripheral ")

                withTimeout(timeout.timeoutConnect.toLong()) {
                    peripheral.connect()
                }
                println("connected: ${peripheral.name}")
                peripheral.requestMtu(MTU_SIZE)
                uuid?.let {
                    _uuid = it
                }
                _uuid?.let {
                    authBluetoothDevice(it)
                }
                registerTakePictureObserver()
                observeManager = BleObserveManager(peripheral, notifyCharacteristicList)
                registerServices()
            } catch (e: ThetaBleException) {
                throw e
            } catch (e: Throwable) {
                throw BluetoothException(e)
            }
        }

        /**
         * Whether connected to THETA.
         *
         * @return Whether connected or not.
         */
        fun isConnected(): Boolean {
            return peripheral != null
        }

        /**
         * Disconnect from THETA.
         *
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        suspend fun disconnect() {
            val peripheral = peripheral ?: throw ThetaBleApiException(ERROR_MESSAGE_NOT_CONNECTED)
            try {
                withTimeout(timeout.timeoutConnect.toLong()) {
                    peripheral.disconnect()
                }
            } catch (e: Throwable) {
                throw BluetoothException(e)
            } finally {
                cleanup()
            }
        }

        /**
         * Clearing process at disconnection.
         */
        internal fun cleanup() {
            deferredTakePicture = null
            peripheral = null

            cameraInformation = null
            cameraStatusCommand = null
            cameraControlCommands = null
            cameraControlCommandV2 = null
            shootingControlCommand = null
            (serviceList as MutableList).clear()

            observeManager?.release()
            scope.cancel()
        }

        /**
         * Authentication when connecting to THETA.
         *
         * Service: 0F291746-0C80-4726-87A7-3C501FD3B4B6
         * Characteristic: EBAFB2F0-0E0F-40A2-A84F-E2F098DC13C3
         *
         * @param uuid UUID used for authentication.
         * @exception ThetaBleApiException If an error occurs in library.
         * @exception BluetoothException If an error occurs in bluetooth.
         */
        @Throws(Throwable::class)
        internal suspend fun authBluetoothDevice(uuid: String) {
            val peripheral =
                this.peripheral ?: throw ThetaBleApiException(ERROR_MESSAGE_NOT_CONNECTED)
            try {
                peripheral.write(
                    BleCharacteristic.AUTH_BLUETOOTH_DEVICE,
                    uuid.encodeToByteArray(),
                )
            } catch (e: Throwable) {
                throw BluetoothException(e)
            }
        }
    }
}
