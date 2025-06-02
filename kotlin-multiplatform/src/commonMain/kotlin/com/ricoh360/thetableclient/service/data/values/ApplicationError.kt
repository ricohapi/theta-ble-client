package com.ricoh360.thetableclient.service.data.values

enum class ApplicationError(internal val ble: Byte?, val message: String) {
    UNKNOWN(null, "Unknown error"),

    /**
     * Disabled Command 0x80
     */
    DISABLE_COMMAND(0x80.toByte(), "Disabled Command"),

    /**
     * Missing Parameter 0x81
     */
    MISSING_PARAMETER(0x81.toByte(), "Missing Parameter"),

    /**
     * Invalid Parameter Value 0x82
     */
    INVALID_PARAMETER_VALUE(0x82.toByte(), "Invalid Parameter Value"),

    /**
     * Power Off Sequence Running 0x83
     */
    POWER_OFF_SEQUENCE_RUNNING(0x83.toByte(), "Power Off Sequence Running"),

    /**
     * Invalid File Format 0x84
     */
    INVALID_FILE_FORMAT(0x84.toByte(), "Invalid File Format"),

    /**
     * Service Unavailable 0x85
     */
    SERVICE_UNAVAILABLE(0x85.toByte(), "Service Unavailable"),

    /**
     * Device Busy 0x86
     */
    DEVICE_BUSY(0x86.toByte(), "Device Busy"),

    /**
     * Unexpected 0x87
     */
    UNEXPECTED(0x87.toByte(), "Unexpected"),

    /**
     * unknownCommand 0x88
     */
    UNKNOWN_COMMAND(0x88.toByte(), "unknownCommand"),

    /**
     * tooManyParameters 0x89
     */
    TOO_MANY_PARAMETERS(0x89.toByte(), "tooManyParameters"),

    /**
     * noFreeSpace 0x8A
     */
    NO_FREE_SPACE(0x8A.toByte(), "noFreeSpace"),

    /**
     * canceledShooting 0x8B
     */
    CANCELED_SHOOTING(0x8B.toByte(), "canceledShooting"),

    /**
     * Size Over 0x8C
     */
    SIZE_OVER((0x8C).toByte(), "Size Over"),
    ;

    companion object {
        /**
         * Options property key name
         */
        val keyName: String
            get() = "applicationError"

        /**
         * Search by bluetooth value.
         *
         * @param bleData Return value of bluetooth api.
         * @return ApplicationError
         */
        internal fun getFromBle(bleData: Byte): ApplicationError {
            return entries.firstOrNull { it.ble == bleData } ?: UNKNOWN
        }

        fun checkErrorResponse(data: ByteArray): ApplicationError? {
            if (data.isEmpty()) {
                return null
            }
            // Application error is 0x80-0x9F
            val start = 0x80.toByte()
            val end = 0x9F.toByte()
            if (data[0] in start..end) {
                return getFromBle(data[0])
            }
            return null
        }
    }
}
