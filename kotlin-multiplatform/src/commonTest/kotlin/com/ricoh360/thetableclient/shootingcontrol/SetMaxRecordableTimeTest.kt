package com.ricoh360.thetableclient.shootingcontrol

import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.MaxRecordableTime
import com.ricoh360.thetableclient.toShort
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetMaxRecordableTimeTest {
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
     * call setMaxRecordableTime.
     */
    @Test
    fun normalTest() = runBlocking {
        val enumList = listOf(
            MaxRecordableTime.RECORDABLE_TIME_300,
            MaxRecordableTime.RECORDABLE_TIME_1500,
            MaxRecordableTime.RECORDABLE_TIME_3000,
        )
        enumList.forEach {
            setMaxRecordableTimeTest(it)
        }
    }

    private fun setMaxRecordableTimeTest(value: MaxRecordableTime) = runBlocking {
        initMock()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        device.connect()
        println("setMaxRecordableTimeTest: $value")

        val deferred = CompletableDeferred<Unit>()

        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "MAX_RECORDABLE_TIME") {
                assertEquals(data.toShort(), value.ble, "setMaxRecordableTime")
                deferred.complete(Unit)
            }
        }
        device.shootingControlCommand?.setMaxRecordableTime(value)
        withTimeout(100) {
            deferred.await()
        }
        assertTrue(true, "setMaxRecordableTime")
    }

    /**
     * Not connected exception for setMaxRecordableTime call.
     */
    @Test
    fun exceptionApiTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        try {
            device.connect()
            val shootingControlCommand = device.shootingControlCommand
            device.disconnect()
            shootingControlCommand?.setMaxRecordableTime(MaxRecordableTime.RECORDABLE_TIME_300)
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
     * Read exception for setMaxRecordableTime call.
     */
    @Test
    fun exceptionBleTest() = runBlocking {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))

        val value = MaxRecordableTime.RECORDABLE_TIME_300
        MockBlePeripheral.onWrite = { characteristic, data ->
            if (characteristic.name == "MAX_RECORDABLE_TIME") {
                assertEquals(data.toShort(), value.ble, "setMaxRecordableTime")
                throw Exception("write")
            }
        }

        try {
            device.connect()
            device.shootingControlCommand?.setMaxRecordableTime(value)
            assertTrue(false, "exception write")
        } catch (e: ThetaBle.BluetoothException) {
            assertTrue(e.message!!.indexOf("write", 0, true) >= 0, "exception write")
        } catch (e: Throwable) {
            assertTrue(false, "exception write. ${e.message}")
        }
    }
}
