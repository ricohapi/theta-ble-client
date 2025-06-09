package com.ricoh360.thetableclient.service.data.values

/**
 * Wifi Encryption Types
 */
enum class WifiSecurityMode(internal val value: String?) {
    /**
     * Undefined value
     */
    UNKNOWN(null),

    /**
     * none
     */
    NONE("none"),

    /**
     * WEP
     */
    WEP("WEP"),

    /**
     * WPA/WPA2 PSK
     */
    WPA_WPA2_PSK("WPA/WPA2 PSK"),

    /**
     * WPA3-SAE
     */
    WPA3_SAE("WPA3-SAE"),
    ;

    companion object {
        val keyName: String
            get() = "security"

        /**
         * Search by json value.
         *
         * @param value json value.
         * @return WifiSecurityMode
         */
        fun getFromValue(value: String): WifiSecurityMode {
            return entries.firstOrNull { it.value == value } ?: run {
                println("Unknown value ${UNKNOWN::class.simpleName}: $value")
                UNKNOWN
            }
        }
    }
}
