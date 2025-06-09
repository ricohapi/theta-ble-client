package com.ricoh360.thetableclient.service.data.values

/**
 * Camera power
 */
enum class CameraPower(internal val value: String?, internal val ble: Byte?) {
    UNKNOWN(null, null),

    /**
     * Camera power off.
     */
    OFF("off", 0),

    /**
     * Camera power on.
     */
    ON("on", 1),

    /**
     * Camera sleep.
     */
    SLEEP("sleep", 2),

    /**
     * Power Saving Mode
     * Power on, power saving mode. Camera is closed.
     * Unavailable parameter when plugin is running. In this case, invalidParameterValue error will be returned.
     */
    POWER_SAVING("powerSaving", null),

    /**
     * Power on, silent mode. LCD/LED is turned off.
     * Unavailable parameter when plugin is running. In this case, invalidParameterValue error will be returned.
     */
    SILENT_MODE("silentMode", null),
    ;

    companion object {
        val keyName: String
            get() = "cameraPower"

        /**
         * Search by bluetooth value.
         *
         * @param bleData Return value of bluetooth api.
         * @return CameraPower
         */
        internal fun getFromBle(bleData: Byte): CameraPower? {
            return entries.firstOrNull { it.ble == bleData }
        }

        /**
         * Search by json value.
         *
         * @param value json value.
         * @return CameraPower
         */
        fun getFromValue(value: String): CameraPower {
            return CameraPower.entries.firstOrNull { it.value == value } ?: run {
                println("Unknown value ${com.ricoh360.thetableclient.service.data.values.CameraPower.UNKNOWN::class.simpleName}: $value")
                com.ricoh360.thetableclient.service.data.values.CameraPower.UNKNOWN
            }
        }
    }
}
