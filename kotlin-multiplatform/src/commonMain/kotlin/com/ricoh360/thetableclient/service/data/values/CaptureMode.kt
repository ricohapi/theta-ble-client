package com.ricoh360.thetableclient.service.data.values

/**
 * Capture mode.
 */
enum class CaptureMode(internal val ble: Byte?) {
    /**
     * Still image shooting mode.
     */
    IMAGE(0),

    /**
     * Movie shooting mode.
     */
    VIDEO(2),

    /**
     * Live streaming mode.
     */
    LIVE(3),
    ;

    companion object {
        val keyName: String
            get() = "captureMode"

        /**
         * Search by bluetooth value.
         *
         * @param bleData Return value of bluetooth api.
         * @return CaptureMode
         */
        internal fun getFromBle(bleData: Byte): CaptureMode? {
            return entries.firstOrNull { it.ble == bleData }
        }
    }
}
