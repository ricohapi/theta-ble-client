package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.service.data.values.ThetaModel
import com.ricoh360.thetableclient.transferred.CameraInfo

/**
 * Static attributes of THETA.
 */
data class ThetaInfo(
    /**
     * Manufacturer name
     */
    val manufacturer: String,

    /**
     * THETA model
     */
    val model: ThetaModel,

    /**
     * Serial number
     */
    val serialNumber: String,

    /**
     * MAC address of wireless LAN
     */
    val wlanMacAddress: String? = null,

    /**
     * MAC address of Bluetooth
     */
    val bluetoothMacAddress: String? = null,

    /**
     * Firmware version
     */
    val firmwareVersion: String,

    /**
     * time after startup (sec)
     */
    val uptime: Int,
) {
    internal constructor(value: CameraInfo) : this(
        manufacturer = value.manufacturer,
        model = ThetaModel.get(value.model, value.serialNumber),
        serialNumber = value.serialNumber,
        wlanMacAddress = value.wlanMacAddress,
        bluetoothMacAddress = value.bluetoothMacAddress,
        firmwareVersion = value.firmwareVersion,
        uptime = value.uptime
    )
}
