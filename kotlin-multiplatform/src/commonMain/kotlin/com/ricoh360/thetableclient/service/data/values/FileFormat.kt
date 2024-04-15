package com.ricoh360.thetableclient.service.data.values

/**
 * File format.
 */
enum class FileFormat(internal val ble: Byte?) {
    /**
     * Reserved value
     */
    RESERVED(null),

    /**
     * Still image. 5376x2688
     *
     * For RICOH THETA V
     */
    IMAGE_5K(0),

    /**
     * Movie. 3840x1920. H.264/MPEG-4 AVC
     *
     * For RICOH THETA V, RICOH THETA Z1 or later
     */
    VIDEO_4K(1),

    /**
     * Movie. 1920x960. H.264/MPEG-4 AVC
     *
     * For RICOH THETA V, RICOH THETA Z1 or later
     */
    VIDEO_2K(3),

    /**
     * Still image JPEG format. 6720x3360 (Equirectangular) or 7296x3648 (Dual-Fisheye)
     *
     * For RICOH THETA Z1 or later
     */
    IMAGE_6_7K(6),

    /**
     * Still image RAW+ format. 7296x3648
     *
     * For RICOH THETA Z1 or later
     */
    RAW_P_6_7K(7),

    /**
     * Movie. 2688x2688. H.264/MPEG-4 AVC
     *
     * RICOH THETA Z1 firmware v3.01.1 or later. This mode outputs two fisheye video for each lens.
     * The MP4 file name ending with _0 is the video file on the front lens,
     * and _1 is back lens. This mode does not record audio track to MP4 file.
     */
    VIDEO_2_7K(9),

    /**
     * Movie. 3648x3648. H.264/MPEG-4 AVC
     *
     * RICOH THETA Z1 firmware v3.01.1 or later. This mode outputs two fisheye video for each lens.
     * The MP4 file name ending with _0 is the video file on the front lens,
     * and _1 is back lens. This mode does not record audio track to MP4 file.
     */
    VIDEO_3_6K(11),
    ;

    companion object {
        internal val MAX_RESERVED = 10

        val keyName: String
            get() = "fileFormat"

        /**
         * Search by bluetooth value.
         *
         * @param bleData Return value of bluetooth api.
         * @return FileFormat
         */
        internal fun getFromBle(bleData: Byte): FileFormat? {
            entries.firstOrNull { it.ble == bleData }?.let {
                return it
            }
            return when {
                bleData in 0..MAX_RESERVED -> RESERVED
                else -> null
            }
        }
    }
}
