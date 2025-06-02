package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.service.data.values.PeripheralDeviceStatus
import com.ricoh360.thetableclient.transferred.Peripheral

/**
 * Peripheral Device
 */
data class PeripheralDevice internal constructor (
    internal var _device: String,
    internal var _macAddress: String,
    internal var _pairing: Boolean,
    internal var _status: PeripheralDeviceStatus,
) {
    /**
     * Device name
     */
    val device: String
        get() = _device

    /**
     * MAC address
     */
    val macAddress: String
        get() = _macAddress

    /**
     * Paired or not
     */
    val pairing: Boolean
        get() = _pairing

    /**
     * State of connection
     */
    val status: PeripheralDeviceStatus
        get() = _status

    internal constructor(value: Peripheral) : this(
        _device = value.device,
        _macAddress = value.macAddress,
        _pairing = value.pairing,
        _status = PeripheralDeviceStatus.getFromValue(value.status),
    )

    internal fun update(value: PeripheralDevice) {
        _device = value.device
        _macAddress = value.macAddress
        _pairing = value.pairing
        _status = value.status
    }

    companion object {
        val keyName: String
            get() = "peripheralDevice"
    }
}
