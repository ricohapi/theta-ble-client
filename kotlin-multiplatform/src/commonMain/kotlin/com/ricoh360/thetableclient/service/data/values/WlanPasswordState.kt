package com.ricoh360.thetableclient.service.data.values

/**
 * WLAN password state
 */
enum class WlanPasswordState(internal val value: String?, internal val ble: Byte?) {
    UNKNOWN(null, null),

    /**
     * The WLAN password has not been changed and the initial password is a part of the Theta's serial number.
     */
    SERIAL("serial", 0),

    /**
     * The WLAN password has not been changed and the initial password is a random string.
     */
    RANDOM("random", 1),

    /**
     * The WLAN password has been changed.
     */
    CHANGED("changed", 2),
    ;

    companion object {
        val keyName: String
            get() = "wlanPasswordState"

        /**
         * Search by bluetooth value.
         *
         * @param bleData Return value of bluetooth api.
         * @return WlanPasswordState
         */
        internal fun getFromBle(bleData: Byte): WlanPasswordState? {
            return entries.firstOrNull { it.ble == bleData }
        }

        /**
         * Search by json value.
         *
         * @param value json value.
         * @return WlanPasswordState
         */
        fun getFromValue(value: String): WlanPasswordState {
            return WlanPasswordState.entries.firstOrNull { it.value == value } ?: run {
                println("Unknown value ${com.ricoh360.thetableclient.service.data.values.WlanPasswordState.UNKNOWN::class.simpleName}: $value")
                com.ricoh360.thetableclient.service.data.values.WlanPasswordState.UNKNOWN
            }
        }
    }
}
