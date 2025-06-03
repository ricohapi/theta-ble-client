package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.service.CameraControlCommandV2
import com.ricoh360.thetableclient.service.CameraControlCommands
import com.ricoh360.thetableclient.service.CameraInformation
import com.ricoh360.thetableclient.service.CameraStatusCommand
import com.ricoh360.thetableclient.service.ShootingControlCommand
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ThetaDeviceServiceTest {
    private val devName = "99999999"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    /**
     * Contain CameraInformation.
     */
    @Test
    fun containCameraInformationTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf(BleService.CAMERA_INFORMATION)
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNotNull(device.cameraInformation)
    }

    /**
     * Unsupported CameraInformation.
     */
    @Test
    fun unsupportedCameraInformationTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNull(device.cameraInformation)
    }

    /**
     * Get CameraInformation.
     */
    @Test
    fun getCameraInformationTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.getService(BleService.CAMERA_INFORMATION)
        assertNotNull(service)
        assertEquals(service.service, BleService.CAMERA_INFORMATION)
        val cameraInformation = service as? CameraInformation
        assertNotNull(cameraInformation)
    }

    /**
     * Contain CameraStatusCommand.
     */
    @Test
    fun containCameraStatusCommandTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf(BleService.CAMERA_STATUS_COMMAND)
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNotNull(device.cameraStatusCommand)
    }

    /**
     * Unsupported CameraStatusCommand.
     */
    @Test
    fun unsupportedCameraStatusCommandTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNull(device.cameraStatusCommand)
    }

    /**
     * Get CameraStatusCommand.
     */
    @Test
    fun getCameraStatusCommandTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.getService(BleService.CAMERA_STATUS_COMMAND)
        assertNotNull(service)
        assertEquals(service.service, BleService.CAMERA_STATUS_COMMAND)
        val cameraStatusCommand = service as? CameraStatusCommand
        assertNotNull(cameraStatusCommand)
    }

    /**
     * Contain CameraControlCommands.
     */
    @Test
    fun containCameraControlCommandsTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf(BleService.CAMERA_CONTROL_COMMANDS)
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNotNull(device.cameraControlCommands)
    }

    /**
     * Unsupported CameraControlCommands.
     */
    @Test
    fun unsupportedCameraControlCommandsTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNull(device.cameraControlCommands)
    }

    /**
     * Get CameraControlCommands.
     */
    @Test
    fun getCameraControlCommandsTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.getService(BleService.CAMERA_CONTROL_COMMANDS)
        assertNotNull(service)
        assertEquals(service.service, BleService.CAMERA_CONTROL_COMMANDS)
        val cameraControlCommands = service as? CameraControlCommands
        assertNotNull(cameraControlCommands)
    }

    /**
     * Contain ShootingControlCommand.
     */
    @Test
    fun containShootingControlCommandTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf(BleService.SHOOTING_CONTROL_COMMAND)
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNotNull(device.shootingControlCommand)
    }

    /**
     * Unsupported ShootingControlCommand.
     */
    @Test
    fun unsupportedShootingControlCommandTest() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNull(device.shootingControlCommand)
    }

    /**
     * Get ShootingControlCommand.
     */
    @Test
    fun getShootingControlCommandTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.getService(BleService.SHOOTING_CONTROL_COMMAND)
        assertNotNull(service)
        assertEquals(service.service, BleService.SHOOTING_CONTROL_COMMAND)
        val shootingControlCommand = service as? ShootingControlCommand
        assertNotNull(shootingControlCommand)
    }

    /**
     * Contain CameraControlCommandV2.
     */
    @Test
    fun containCameraControlCommandV2Test() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf(BleService.CAMERA_CONTROL_COMMAND_V2)
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNotNull(device.cameraControlCommandV2)
    }

    /**
     * Unsupported CameraInformation.
     */
    @Test
    fun unsupportedCameraControlCommandV2Test() = runTest {
        MockBlePeripheral.supportedServiceList = mutableListOf()
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        assertNull(device.cameraControlCommandV2)
    }

    /**
     * Get CameraControlCommandV2.
     */
    @Test
    fun getCameraControlCommandV2Test() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.getService(BleService.CAMERA_CONTROL_COMMAND_V2)
        assertNotNull(service)
        assertEquals(service.service, BleService.CAMERA_CONTROL_COMMAND_V2)
        val cameraControlCommandV2 = service as? CameraControlCommandV2
        assertNotNull(cameraControlCommandV2)
    }

    /**
     * Get unsupported service.
     */
    @Test
    fun getUnsupportedServiceTest() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.getService(BleService.SERVICE_UUID)
        assertNull(service)
    }
}
