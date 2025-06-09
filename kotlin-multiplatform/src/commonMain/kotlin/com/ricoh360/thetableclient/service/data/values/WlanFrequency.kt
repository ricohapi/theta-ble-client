package com.ricoh360.thetableclient.service.data.values

/**
 * Wireless LAN frequency of the camera supported by Theta V, Z1, X and A1.
 */
enum class WlanFrequency(internal val value: Double?, internal val stringValue: String? = null) {
    /**
     * Undefined value
     */
    UNKNOWN(null),

    /**
     * 2.4GHz
     */
    GHZ_2_4(2.4, "2.4"),

    /**
     * 5GHz
     */
    GHZ_5(5.0, null),

    /**
     * 5.2GHz
     *
     * For RICOH THETA A1
     */
    GHZ_5_2(5.2, "5.2"),

    /**
     * 5.8GHz
     *
     * For RICOH THETA A1
     */
    GHZ_5_8(5.8, "5.8"),

    /**
     * Initial value
     */
    INITIAL_VALUE(null, ""),
    ;

    companion object {
        /**
         * Options property key name
         */
        val keyName: String
            get() = "wlanFrequency"

        /**
         * Search by json value.
         *
         * @param value Double value.
         * @return WlanFrequency
         */
        internal fun getFromValue(value: Double): WlanFrequency {
            return entries.firstOrNull { it.value != null && it.value == value } ?: run {
                println("Unknown value ${UNKNOWN::class.simpleName}: $value")
                UNKNOWN
            }
        }

        /**
         * Search by json value.
         *
         * @param value String value.
         * @return WlanFrequency
         */
        internal fun getFromValue(value: String): WlanFrequency {
            return entries.firstOrNull { it.stringValue != null && it.stringValue == value } ?: run {
                println("Unknown value ${UNKNOWN::class.simpleName}: $value")
                UNKNOWN
            }
        }
    }
}
