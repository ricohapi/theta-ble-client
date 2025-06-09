package com.ricoh360.thetableclient.wlancontrolv2

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.Proxy
import com.ricoh360.thetableclient.service.data.values.WifiSecurityMode
import com.ricoh360.thetableclient.transferred.SetAccessPointParams
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetAccessPointDynamicallyTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        println("setup")
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    private val testData = SetAccessPointParams(
        ssid = "ssid_test",
        ssidStealth = true,
        security = WifiSecurityMode.WPA_WPA2_PSK.value,
        password = "password_wifi",
        connectionPriority = 1,
        ipAddressAllocation = "dynamic",
        proxy = com.ricoh360.thetableclient.transferred.Proxy(
            use = true,
            url = "http://test_proxy",
            port = 10,
            userid = "proxy_user",
            password = "password_proxy",
        )
    )

    /**
     * call setAccessPointDynamically.
     */
    @Test
    fun normalTest() = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "WRITE_CONNECT_WIFI") {
                assertEquals(
                    data.decodeToString(),
                    Json.encodeToString(testData),
                    "setAccessPointDynamically"
                )
                deferred.complete(Unit)
            }
        }
        device.wlanControlCommandV2?.setAccessPointDynamically(
            ssid = testData.ssid,
            ssidStealth = testData.ssidStealth ?: true,
            security = testData.security?.let { WifiSecurityMode.getFromValue(it) }
                ?: WifiSecurityMode.NONE,
            password = testData.password ?: "",
            connectionPriority = testData.connectionPriority ?: 2,
            proxy = testData.proxy?.let { Proxy(it) },
        )
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setAccessPointDynamically")
    }

    /**
     * Not connected exception for setAccessPointDynamically call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val wlanControlCommandV2 = device.wlanControlCommandV2
            device.disconnect()
            wlanControlCommandV2?.setAccessPointDynamically(
                ssid = testData.ssid,
                ssidStealth = testData.ssidStealth ?: true,
                security = testData.security?.let { WifiSecurityMode.getFromValue(it) }
                    ?: WifiSecurityMode.NONE,
                password = testData.password ?: "",
                connectionPriority = testData.connectionPriority ?: 2,
                proxy = testData.proxy?.let { Proxy(it) },
            )
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
     * Write exception for setAccessPointDynamically call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "WRITE_CONNECT_WIFI") {
                assertEquals(
                    data.decodeToString(),
                    Json.encodeToString(testData),
                    "setAccessPointDynamically"
                )
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.wlanControlCommandV2?.setAccessPointDynamically(
                ssid = testData.ssid,
                ssidStealth = testData.ssidStealth ?: true,
                security = testData.security?.let { WifiSecurityMode.getFromValue(it) }
                    ?: WifiSecurityMode.NONE,
                password = testData.password ?: "",
                connectionPriority = testData.connectionPriority ?: 2,
                proxy = testData.proxy?.let { Proxy(it) },
            )
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
