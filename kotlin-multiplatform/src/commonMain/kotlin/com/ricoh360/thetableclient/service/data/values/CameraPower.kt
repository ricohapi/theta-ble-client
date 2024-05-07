package com.ricoh360.thetableclient.service.data.values

/**
 * Camera power
 */
enum class CameraPower(internal val ble: Byte?) {
    /**
     * Camera power off.
     */
    OFF(0),

    /**
     * Camera power on.
     */
    ON(1),

    /**
     * Camera sleep.
     */
    SLEEP(2),
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
    }
}
