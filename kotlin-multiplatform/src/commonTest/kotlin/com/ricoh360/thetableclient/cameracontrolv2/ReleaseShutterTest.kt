package com.ricoh360.thetableclient.cameracontrolv2

import com.goncalossilva.resources.Resource
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.ThetaBle
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.newAdvertisement
import com.ricoh360.thetableclient.initMock
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ReleaseShutterTest {
    private val devName = "AA12345678"

    @BeforeTest
    fun setup() {
        initMock()
    }

    @AfterTest
    fun teardown() {
    }

    @Test
    fun releaseShutterTakePicture() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/options_capture_image.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_OPTIONS)
            jsonText.encodeToByteArray()
        }
        var onWriteInvoked = false
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> {
                    assertTrue(
                        (jsonString.indexOf("camera.takePicture", 0, true)) >= 0,
                        "exception requestShutterCommand"
                    )
                    onWriteInvoked = true
                }

                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        service.releaseShutter()
        assertTrue(onWriteInvoked, "Write shutter command")
    }

    @Test
    fun releaseShutterStartCapture() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonTextOptions =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/options_capture_video.json").readText()
        val jsonTextState =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/state_capture_idle.json").readText()
        MockBlePeripheral.onRead = {
            when (it) {
                BleCharacteristic.GET_OPTIONS -> jsonTextOptions.encodeToByteArray()
                BleCharacteristic.GET_STATE -> jsonTextState.encodeToByteArray()
                else -> {
                    assertTrue(false, "unforeseen: ${it.name}")
                    ByteArray(0)
                }
            }
        }
        var onWriteInvoked = false
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.GET_STATE -> {
                    assertTrue(
                        (jsonString.indexOf("_captureStatus", 0, true)) >= 0,
                        "exception getState"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> {
                    assertTrue(
                        (jsonString.indexOf("camera.startCapture", 0, true)) >= 0,
                        "exception requestShutterCommand"
                    )
                    onWriteInvoked = true
                }

                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        service.releaseShutter()
        assertTrue(onWriteInvoked, "Write shutter command")
    }

    @Test
    fun releaseShutterStopCapture() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonTextOptions =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/options_capture_video.json").readText()
        val jsonTextState =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/state_capture_shooting.json").readText()
        MockBlePeripheral.onRead = {
            when (it) {
                BleCharacteristic.GET_OPTIONS -> jsonTextOptions.encodeToByteArray()
                BleCharacteristic.GET_STATE -> jsonTextState.encodeToByteArray()
                else -> {
                    assertTrue(false, "unforeseen: ${it.name}")
                    ByteArray(0)
                }
            }
        }
        var onWriteInvoked = false
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.GET_STATE -> {
                    assertTrue(
                        (jsonString.indexOf("_captureStatus", 0, true)) >= 0,
                        "exception getState"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> {
                    assertTrue(
                        (jsonString.indexOf("camera.stopCapture", 0, true)) >= 0,
                        "exception requestShutterCommand"
                    )
                    onWriteInvoked = true
                }

                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        service.releaseShutter()
        assertTrue(onWriteInvoked, "Write shutter command")
    }

    @Test
    fun invalidCaptureMode() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/options_capture_live.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_OPTIONS)
            jsonText.encodeToByteArray()
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> {
                    assertTrue(false, "call requestShutterCommand")
                }

                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        try {
            service.releaseShutter()
            assertTrue(false, "Not exception")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertEquals(e.message, "Disabled Command", "response error")
        } catch (e: Throwable) {
            assertTrue(false, "Other exception: $e")
        }
    }

    @Test
    fun invalidCaptureState() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonTextOptions =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/options_capture_video.json").readText()
        val jsonTextState =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/state_capture_self_timer.json").readText()
        MockBlePeripheral.onRead = {
            when (it) {
                BleCharacteristic.GET_OPTIONS -> jsonTextOptions.encodeToByteArray()
                BleCharacteristic.GET_STATE -> jsonTextState.encodeToByteArray()
                else -> {
                    assertTrue(false, "unforeseen: ${it.name}")
                    ByteArray(0)
                }
            }
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> {
                    assertTrue(false, "call requestShutterCommand")
                }

                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        try {
            service.releaseShutter()
            assertTrue(false, "Not exception")
        } catch (e: ThetaBle.ThetaBleApiException) {
            assertEquals(e.message, "Disabled Command", "response error")
        } catch (e: Throwable) {
            assertTrue(false, "Other exception: $e")
        }
    }

    @Test
    fun errorGetOptions() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        MockBlePeripheral.onRead = {
            throw Exception("read")
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> {
                    assertTrue(false, "call requestShutterCommand")
                }

                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        try {
            service.releaseShutter()
            assertTrue(false, "Not exception")
        } catch (e: ThetaBle.ThetaBleException) {
            assertTrue(
                ((e.message?.indexOf("read", 0, true)) ?: -1) >= 0,
                "exception getOptions"
            )
        } catch (e: Throwable) {
            assertTrue(false, "Other exception: $e")
        }
    }

    @Test
    fun errorGetState() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonTextOptions =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/options_capture_video.json").readText()
        MockBlePeripheral.onRead = {
            when (it) {
                BleCharacteristic.GET_OPTIONS -> jsonTextOptions.encodeToByteArray()
                BleCharacteristic.GET_STATE -> throw Exception("read")
                else -> {
                    assertTrue(false, "unforeseen: ${it.name}")
                    ByteArray(0)
                }
            }
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> {
                    assertTrue(false, "call requestShutterCommand")
                }

                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        try {
            service.releaseShutter()
            assertTrue(false, "Not exception")
        } catch (e: ThetaBle.ThetaBleException) {
            assertTrue(
                ((e.message?.indexOf("read", 0, true)) ?: -1) >= 0,
                "exception getState"
            )
        } catch (e: Throwable) {
            assertTrue(false, "Other exception: $e")
        }
    }

    @Test
    fun errorRequestShutterCommand() = runTest {
        val device = ThetaBle.ThetaDevice(newAdvertisement(devName))
        runBlocking {
            device.connect()
        }
        val service = device.cameraControlCommandV2
        assertNotNull(service)

        val jsonText =
            Resource("src/commonTest/resources/cameracontrolv2/releaseShutter/options_capture_image.json").readText()
        MockBlePeripheral.onRead = {
            assertEquals(it, BleCharacteristic.GET_OPTIONS)
            jsonText.encodeToByteArray()
        }
        MockBlePeripheral.onWrite = { characteristic, data ->
            val jsonString = data.decodeToString()
            when (characteristic) {
                BleCharacteristic.GET_OPTIONS -> {
                    assertTrue(
                        (jsonString.indexOf("captureMode", 0, true)) >= 0,
                        "exception getOptions"
                    )
                }

                BleCharacteristic.REQUEST_SHUTTER_COMMAND -> throw Exception("write")
                else -> assertTrue(false, "unforeseen: ${characteristic.name}")
            }
        }

        try {
            service.releaseShutter()
            assertTrue(false, "Not exception")
        } catch (e: ThetaBle.ThetaBleException) {
            assertTrue(
                ((e.message?.indexOf("write", 0, true)) ?: -1) >= 0,
                "exception requestShutterCommand"
            )
        } catch (e: Throwable) {
            assertTrue(false, "Other exception: $e")
        }
    }
}
