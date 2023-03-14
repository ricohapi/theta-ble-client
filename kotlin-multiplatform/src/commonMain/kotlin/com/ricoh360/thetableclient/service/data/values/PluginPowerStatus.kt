package com.ricoh360.thetableclient.service.data.values


/**
 * Plugin power status (Kind of action)
 * @see PluginControl
 */
enum class PluginPowerStatus(internal val ble: Byte?) {
    /**
     * Running (Start plugin)
     */
    RUNNING(0),

    /**
     * Stop
     */
    STOP(1),
    ;

    companion object {
        val keyName: String
            get() = "pluginControl"

        /**
         * Search by bluetooth value.
         *
         * @param ble Return value of bluetooth api.
         * @return PluginPowerStatus
         */
        internal fun getFromBle(ble: Byte): PluginPowerStatus? {
            return values().firstOrNull { it.ble == ble }
        }
    }
}
