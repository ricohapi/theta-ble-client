package com.ricoh360.thetableclient.camerastatus

import com.ricoh360.thetableclient.initMock
import com.ricoh360.thetableclient.service.data.values.ChargingState
import com.ricoh360.thetableclient.service.data.values.CommandErrorDescription
import com.ricoh360.thetableclient.service.data.values.PluginPowerStatus
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ValuesTest {
    @BeforeTest
    fun setup() {
        println("setup")
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * ChargingState.
     */
    @Test
    fun checkChargingState() = runBlocking {
        val values = listOf(
            Pair(ChargingState.UNKNOWN, null),
            Pair(ChargingState.CHARGING, 0),
            Pair(ChargingState.CHARGED, 1),
            Pair(ChargingState.DISCONNECT, 2),
        )
        assertEquals(values.size, ChargingState.entries.size)
        values.forEach {
            assertEquals(it.first.ble, it.second?.toByte(), "ChargingState ${it.first}")
        }
    }

    /**
     * CommandErrorDescription.
     */
    @Test
    fun checkCommandErrorDescription() = runBlocking {
        val values = listOf(
            Pair(CommandErrorDescription.DISABLED_COMMAND, 0),
            Pair(CommandErrorDescription.MISSING_PARAMETER, 1),
            Pair(CommandErrorDescription.INVALID_PARAMETER_VALUE, 2),
            Pair(CommandErrorDescription.POWER_OFF_SEQUENCE_RUNNING, 3),
            Pair(CommandErrorDescription.INVALID_FILE_FORMAT, 4),
            Pair(CommandErrorDescription.SERVICE_UNAVAILABLE, 5),
            Pair(CommandErrorDescription.DEVICE_BUSY, 6),
            Pair(CommandErrorDescription.UNEXPECTED, 7),
        )
        assertEquals(values.size, CommandErrorDescription.entries.size)
        values.forEach {
            assertEquals(
                it.first.ble,
                it.second.toByte(),
                "CommandErrorDescription ${it.first}",
            )
        }
    }

    /**
     * PluginPowerStatus.
     */
    @Test
    fun checkPluginPowerStatus() = runBlocking {
        val values = listOf(
            Pair(PluginPowerStatus.RUNNING, 0),
            Pair(PluginPowerStatus.STOP, 1),
        )
        assertEquals(values.size, PluginPowerStatus.entries.size)
        values.forEach {
            assertEquals(it.first.ble, it.second.toByte(), "PluginPowerStatus ${it.first}")
        }
    }
}
