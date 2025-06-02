package com.ricoh360.thetableclient.service.data.values

/**
 * Network type
 */
enum class NetworkType(internal val value: String?) {
    /**
     * Undefined value
     */
    UNKNOWN(null),

    /**
     * Network is off. This value can be gotten only by plugin
     */
    OFF("OFF"),

    /**
     * Direct mode
     */
    DIRECT("AP"),

    /**
     * Client mode via WLAN
     */
    CLIENT("CL"),

    /**
     * Client mode via Ethernet cable
     */
    ETHERNET("ETHERNET"),

    /**
     * LTE plan-D
     */
    LTE_PLAN_D("LTE plan-D"),

    /**
     * LTE plan-DU
     */
    LTE_PLAN_DU("LTE plan-DU"),

    /**
     * LTE plan01s
     */
    LTE_PLAN_01S("LTE plan01s"),

    /**
     * LTE planX3
     */
    LTE_PLAN_X3("LTE planX3"),

    /**
     * LTE planP1
     */
    LTE_PLAN_P1("LTE planP1"),

    /**
     * LTE plan-K2
     */
    LTE_PLAN_K2("LTE plan-K2"),

    /**
     * LTE plan-K
     */
    LTE_PLAN_K("LTE plan-K"),

    /**
     * SSID scan mode
     *
     * Switch to CL mode and search for SSID
     */
    SCAN("SCAN"),
    ;

    companion object {
        /**
         * Options property key name
         */
        val keyName: String
            get() = "networkType"

        /**
         * Search by json value.
         *
         * @param value json value.
         * @return NetworkType
         */
        fun getFromValue(value: String): NetworkType {
            return entries.firstOrNull { it.value == value } ?: run {
                println("Unknown value ${UNKNOWN::class.simpleName}: $value")
                UNKNOWN
            }
        }
    }
}
