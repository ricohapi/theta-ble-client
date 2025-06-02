package com.ricoh360.thetableclient.service.data.values

import kotlin.reflect.KClass

/**
 * Camera setting options name.
 * [options name](https://github.com/ricohapi/theta-api-specs/blob/main/theta-web-api-v2.1/options.md)
 */
enum class OptionName(val value: String, val keyName: String, val valueType: KClass<*>) {
    /**
     * Option name
     * _accessInfo
     */
    AccessInfo(
        "_accessInfo",
        "accessInfo",
        com.ricoh360.thetableclient.service.data.AccessInfo::class
    ),

    /**
     * Option name
     * _cameraPower
     */
    CameraPower(
        "_cameraPower",
        "cameraPower",
        com.ricoh360.thetableclient.service.data.values.CameraPower::class
    ),

    /**
     * Option name
     * captureMode
     */
    CaptureMode(
        "captureMode",
        "captureMode",
        com.ricoh360.thetableclient.service.data.values.CaptureMode::class
    ),

    /**
     * Option name
     * _defaultWifiPassword
     */
    DefaultWifiPassword(
        "_defaultWifiPassword",
        "defaultWifiPassword",
        String::class
    ),

    /**
     * Option name
     * _networkType
     */
    NetworkType(
        "_networkType",
        "networkType",
        com.ricoh360.thetableclient.service.data.values.NetworkType::class
    ),

    /**
     * Option name
     * Password
     */
    Password(
        "_password",
        "password",
        String::class
    ),

    /**
     * Option name
     * Ssid
     */
    Ssid(
        "_ssid",
        "ssid",
        String::class
    ),

    /**
     * Option name
     * _username
     */
    Username(
        "_username",
        "username",
        String::class
    ),

    /**
     * Option name
     * _wlanAntennaConfig
     */
    WlanAntennaConfig(
        "_wlanAntennaConfig",
        "wlanAntennaConfig",
        com.ricoh360.thetableclient.service.data.values.WlanAntennaConfig::class
    ),

    /**
     * Option name
     * _wlanFrequency
     */
    WlanFrequency(
        "_wlanFrequency",
        "wlanFrequency",
        com.ricoh360.thetableclient.service.data.values.WlanFrequency::class
    ),
    ;

    companion object {
        /**
         * Acquires OptionName from Option property name
         *
         * @param keyName Option property name
         * @return Option name
         */
        fun getFromKeyName(keyName: String): OptionName? {
            return entries.firstOrNull { it.keyName == keyName }
        }
    }
}
