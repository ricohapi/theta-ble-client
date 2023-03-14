package com.ricoh360.thetableclient.service.data.values

/**
 * Camera error
 */
enum class CameraError : SerialNameEnum {
    /**
     * Undefined value
     */
    UNKNOWN,

    /**
     * RICOH THETA X or later
     * 0x00000001: Insufficient memory
     */
    NO_MEMORY,

    /**
     * 0x00000004: Maximum file number exceeded
     */
    FILE_NUMBER_OVER,

    /**
     * 0x00000008: Camera clock not set
     */
    NO_DATE_SETTING,

    /**
     * 0x00000010: Includes when the card is removed
     */
    READ_ERROR,

    /**
     * 0x00000020: Unsupported media (SDHC, etc.)
     */
    NOT_SUPPORTED_MEDIA_TYPE,

    /**
     * 0x00000040: FAT32, etc.
     */
    NOT_SUPPORTED_FILE_SYSTEM,

    /**
     * 0x00000100: Error warning while mounting
     */
    MEDIA_NOT_READY,

    /**
     * 0x00000200: Battery level warning (firmware update)
     */
    NOT_ENOUGH_BATTERY,

    /**
     * 0x00000400: Firmware file mismatch warning
     */
    INVALID_FILE,

    /**
     * 0x00000800: Plug-in start warning (IoT technical standards compliance)
     */
    PLUGIN_BOOT_ERROR,

    /**
     * 0x00001000: When performing continuous shooting by operating
     * the camera while executing <Delete object>, <Transfer firmware
     * file>, <Install plug-in> or <Uninstall plug-in> with the WebAPI
     * or MTP.
     */
    IN_PROGRESS_ERROR,

    /**
     * 0x00001000: Battery inserted + WLAN ON + Video mode + 4K 60fps
     * / 5.7K 10fps / 5.7K 15fps / 5.7K 30fps / 8K 10fps
     */
    CANNOT_RECORDING,

    /**
     * 0x00002000: Battery inserted AND Specified battery level or
     * lower + WLAN ON + Video mode + 4K 30fps
     */
    CANNOT_RECORD_LOWBAT,

    /**
     * 0x00400000: Shooting hardware failure
     */
    CAPTURE_HW_FAILED,

    /**
     * 0x00800000: Software error
     */
    CAPTURE_SW_FAILED,

    /**
     * 0x08000000: Internal memory access error
     */
    INTERNAL_MEM_ACCESS_FAIL,

    /**
     * 0x20000000: Undefined error
     */
    UNEXPECTED_ERROR,

    /**
     * 0x40000000: Charging error
     */
    BATTERY_CHARGE_FAIL,

    /**
     * 0x00100000: (Board) temperature warning
     */
    HIGH_TEMPERATURE_WARNING,

    /**
     * 0x80000000: (Board) temperature error
     */
    HIGH_TEMPERATURE,

    /**
     * 0x00200000: Battery temperature error
     */
    BATTERY_HIGH_TEMPERATURE,


    /**
     * 0x00000010: Electronic compass error
     */
    COMPASS_CALIBRATION,
    ;

    companion object {
        val keyName: String
            get() = "cameraError"

        internal fun get(serialName: String?): CameraError? {
            return SerialNameEnum.get(serialName, values(), UNKNOWN)
        }
    }
}
