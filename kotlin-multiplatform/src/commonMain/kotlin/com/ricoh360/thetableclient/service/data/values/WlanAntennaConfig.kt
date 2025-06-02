package com.ricoh360.thetableclient.service.data.values

/**
 * Configure SISO or MIMO for Wireless LAN.
 */
enum class WlanAntennaConfig(internal val value: String?) {
    /**
     * Undefined value
     */
    UNKNOWN(null),

    /**
     * SISO
     */
    SISO("SISO"),

    /**
     * MIMO
     */
    MIMO("MIMO"),
    ;

    companion object {
        /**
         * Options property key name
         */
        val keyName: String
            get() = "wlanAntennaConfig"

        /**
         * Search by json value.
         *
         * @param value String value.
         * @return WlanAntennaConfig
         */
        fun getFromValue(value: String?): WlanAntennaConfig {
            return entries.firstOrNull { it.value == value } ?: run {
                println("Unknown value ${UNKNOWN::class.simpleName}: $value")
                UNKNOWN
            }
        }
    }
}
