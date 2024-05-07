package com.ricoh360.thetableclient.service.data.values

/**
 * Maximum recordable time (in seconds) of the camera
 */
enum class MaxRecordableTime(internal val ble: Short?) {
    /**
     * Maximum recordable time. 300sec for other than SC2.
     */
    RECORDABLE_TIME_300(300),

    /**
     * Maximum recordable time. 1500sec for other than SC2.
     */
    RECORDABLE_TIME_1500(1500),

    /**
     * Maximum recordable time. 3000sec for THETA Z1 Version 3.01.1 or later
     * only for 3.6K 1/2fps and 2.7K 1/2fps.
     * If you set 3000 seconds in 3.6K 2fps mode and then set back to 4K 30fps mode,
     * the max recordable time will be overwritten to 300 seconds automatically.
     */
    RECORDABLE_TIME_3000(3000),
    ;

    companion object {
        val keyName: String
            get() = "maxRecordableTime"

        /**
         * Search by bluetooth value.
         *
         * @param bleData Return value of bluetooth api.
         * @return MaxRecordableTime
         */
        internal fun getFromBle(bleData: Short): MaxRecordableTime? {
            return entries.firstOrNull { it.ble == bleData }
        }
    }
}
