package com.ricoh360.thetableclient.service.data

import com.ricoh360.thetableclient.service.data.values.CameraPower
import com.ricoh360.thetableclient.service.data.values.CaptureMode
import com.ricoh360.thetableclient.service.data.values.NetworkType
import com.ricoh360.thetableclient.service.data.values.OptionName
import com.ricoh360.thetableclient.service.data.values.WlanAntennaConfig
import com.ricoh360.thetableclient.service.data.values.WlanFrequency
import com.ricoh360.thetableclient.transferred.CameraOptions

/**
 * Camera setting options.
 * Refer to the [options category](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/options.md)
 */
data class ThetaOptions(
    /**
     * Connected network information.
     */
    var accessInfo: AccessInfo? = null,

    /**
     * Camera power
     */
    var cameraPower: CameraPower? = null,

    /**
     * Shooting mode.
     */
    var captureMode: CaptureMode? = null,

    /**
     * Default WiFi password in AP mode (factory settings)
     */
    var defaultWifiPassword: String? = null,

    /**
     * Network type of the camera.
     */
    var networkType: NetworkType? = null,

    /**
     * Password used for digest authentication when _networkType is set to client mode.
     * Can be set by camera.setOptions during direct mode.
     */
    var password: String? = null,

    /**
     * SSID to connect when in AP mode.
     */
    var ssid: String? = null,

    /**
     * User name used for digest authentication when _networkType is set to client mode.
     * Can be set by camera.setOptions during direct mode.
     */
    var username: String? = null,

    /**
     * Configure SISO or MIMO for Wireless LAN.
     */
    var wlanAntennaConfig: WlanAntennaConfig? = null,

    /**
     * Network type of the camera.
     */
    var wlanFrequency: WlanFrequency? = null,
) {
    constructor() : this(
        accessInfo = null,
        cameraPower = null,
        captureMode = null,
        defaultWifiPassword = null,
        networkType = null,
        password = null,
        ssid = null,
        username = null,
        wlanAntennaConfig = null,
        wlanFrequency = null,
    )

    internal constructor(options: CameraOptions) : this(
        accessInfo = options.accessInfo?.let { AccessInfo(it) },
        cameraPower = options.cameraPower?.let { CameraPower.getFromValue(it) },
        captureMode = options.captureMode?.let { CaptureMode.getFromValue(it) },
        defaultWifiPassword = options.defaultWifiPassword,
        networkType = options.networkType?.let { NetworkType.getFromValue(it) },
        password = options.password,
        ssid = options.ssid,
        username = options.username,
        wlanAntennaConfig = options.wlanAntennaConfig?.let { WlanAntennaConfig.getFromValue(it) },
        wlanFrequency = options.wlanFrequency?.let { WlanFrequency.getFromValue(it) },
    )

    internal fun toCameraOptions(): CameraOptions {
        return CameraOptions(
            accessInfo = accessInfo?.toTransferred(),
            cameraPower = cameraPower?.value,
            captureMode = captureMode?.value,
            defaultWifiPassword = defaultWifiPassword,
            networkType = networkType?.value,
            password = password,
            ssid = ssid,
            username = username,
            wlanAntennaConfig = wlanAntennaConfig?.value,
            wlanFrequency = wlanFrequency?.value,
        )
    }

    @Throws(Throwable::class)
    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    fun <T> getValue(name: OptionName): T? {
        return when (name) {
            OptionName.AccessInfo -> accessInfo
            OptionName.CameraPower -> cameraPower
            OptionName.CaptureMode -> captureMode
            OptionName.DefaultWifiPassword -> defaultWifiPassword
            OptionName.NetworkType -> networkType
            OptionName.Password -> password
            OptionName.Ssid -> ssid
            OptionName.Username -> username
            OptionName.WlanAntennaConfig -> wlanAntennaConfig
            OptionName.WlanFrequency -> wlanFrequency
        } as T
    }

    @Throws(Throwable::class)
    fun setValue(name: OptionName, value: Any) {
        when (name) {
            OptionName.AccessInfo -> accessInfo = value as AccessInfo
            OptionName.CameraPower -> cameraPower = value as CameraPower
            OptionName.CaptureMode -> captureMode = value as CaptureMode
            OptionName.DefaultWifiPassword -> defaultWifiPassword = value as String
            OptionName.NetworkType -> networkType = value as NetworkType
            OptionName.Password -> password = value as String
            OptionName.Ssid -> ssid = value as String
            OptionName.Username -> username = value as String
            OptionName.WlanAntennaConfig -> wlanAntennaConfig = value as WlanAntennaConfig
            OptionName.WlanFrequency -> wlanFrequency = value as WlanFrequency
        }
    }
}
