package com.ricoh360.thetableclient.service.data.values

/**
 * Capture mode.
 */
enum class CaptureMode(internal val value: String?, internal val ble: Byte?) {
    UNKNOWN(null, null),
    /**
     * Still image shooting mode.
     */
    IMAGE("image", 0),

    /**
     * Movie shooting mode.
     */
    VIDEO("video", 2),

    /**
     * Live streaming mode.
     */
    LIVE("_liveStreaming", 3),

    /**
     * Interval mode of Theta SC2.
     */
    INTERVAL("interval", null),

    /**
     * Preset mode of Theta SC2.
     */
    PRESET("_preset", null),

    /**
     * WebRTC.
     */
    WEB_RTC("_streaming", null),
    ;

    companion object {
        /**
         * Options property key name
         */
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

        /**
         * Search by json value.
         *
         * @param value json value.
         * @return CaptureMode
         */
        fun getFromValue(value: String): CaptureMode {
            return entries.firstOrNull { it.value == value } ?: run {
                println("Unknown value ${UNKNOWN::class.simpleName}: $value")
                UNKNOWN
            }
        }
    }
}
