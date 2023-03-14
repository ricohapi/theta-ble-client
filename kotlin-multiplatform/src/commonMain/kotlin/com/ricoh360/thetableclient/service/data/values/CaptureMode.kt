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
         * @param ble Return value of bluetooth api.
         * @return CaptureMode
         */
        internal fun getFromBle(ble: Byte): CaptureMode? {
            return values().firstOrNull { it.ble == ble }
        }
    }
}
