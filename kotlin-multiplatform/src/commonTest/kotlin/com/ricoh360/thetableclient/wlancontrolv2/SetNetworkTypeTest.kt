package com.ricoh360.thetableclient.wlancontrolv2

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.NetworkType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetNetworkTypeTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        println("setup")
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * call setNetworkType.
     */
    @Test
    fun normalTest() = runBlocking {
        val networkTypeList = listOf(
            NetworkType.SCAN,
            NetworkType.CLIENT,
            NetworkType.DIRECT,
            NetworkType.OFF,
        )
        networkTypeList.forEach {
            setNetworkTypeTest(it)
        }
    }

    private fun setNetworkTypeTest(networkType: NetworkType) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()
        println("setNetworkTypeTest: $networkType")

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "WRITE_SET_NETWORK_TYPE") {
                assertEquals(
                    data.decodeToString(),
                    "{\"type\":\"${networkType.value}\"}",
                    "setNetworkType"
                )
                deferred.complete(Unit)
            }
        }
        device.wlanControlCommandV2?.setNetworkType(networkType)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setNetworkType")
    }

    /**
     * Not connected exception for setNetworkType call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val wlanControlCommandV2 = device.wlanControlCommandV2
            device.disconnect()
            wlanControlCommandV2?.setNetworkType(NetworkType.SCAN)
            assertTrue(false, "exception Not connected")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertTrue(
                e.message!!.indexOf("Not connected", 0, true) >= 0,
                "exception Not connected"
            )
        } catch (e: Throwable) {
            assertTrue(false, "exception Not connected. ${e.message}")
        }
    }

    /**
     * Write exception for setNetworkType call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val networkType = NetworkType.DIRECT
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "WRITE_SET_NETWORK_TYPE") {
                assertEquals(
                    data.decodeToString(),
                    "{\"type\":\"${networkType.value}\"}",
                    "setNetworkType"
                )
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.wlanControlCommandV2?.setNetworkType(networkType)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
