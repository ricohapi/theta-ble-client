package com.ricoh360.thetableclient.data

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.PeripheralDevice
import com.ricoh360.thetableclient.service.data.values.PeripheralDeviceStatus
import com.ricoh360.thetableclient.transferred.Peripheral
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PeripheralDeviceTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    @Test
    fun constructorPeripheralTest() = runTest {
        val peripheral = Peripheral(
            device = "name",
            macAddress = "mac address",
            pairing = true,
            status = "idle",
        )
        val device = PeripheralDevice(peripheral)
        assertEquals(device.device, "name")
        assertEquals(device.macAddress, "mac address")
        assertEquals(device.pairing, true)
        assertEquals(device.status, PeripheralDeviceStatus.IDLE)
    }

    @Test
    fun updateTest() = runTest {
        val device = PeripheralDevice(
            "name1", "address", true, PeripheralDeviceStatus.IDLE
        )
        device.update(
            PeripheralDevice(
                "name2", "address2", false, PeripheralDeviceStatus.CONNECTED
            )
        )
        assertEquals(device.device, "name2")
        assertEquals(device.macAddress, "address2")
        assertEquals(device.pairing, false)
        assertEquals(device.status, PeripheralDeviceStatus.CONNECTED)
    }
}
