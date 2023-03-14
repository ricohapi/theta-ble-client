package com.ricoh360.thetableclient.service.data.values

/**
 * Command error description
 */
enum class CommandErrorDescription(internal val ble: Byte?) {
    /**
     * Disabled Command
     * Command cannot be executed due to the camera status
     */
    DISABLED_COMMAND(0),

    /**
     * Missing Parameter
     * Insufficient required parameters to issue the command
     */
    MISSING_PARAMETER(1),

    /**
     * Invalid Parameter Value
     * Parameter value when command was issued is invalid
     */
    INVALID_PARAMETER_VALUE(2),

    /**
     * Power Off Sequence Running
     * Process request when power supply is off
     */
    POWER_OFF_SEQUENCE_RUNNING(3),

    /**
     * Invalid File Format
     * Invalid file format specified
     */
    INVALID_FILE_FORMAT(4),

    /**
     * Service Unavailable
     * Processing requests cannot be received temporarily
     */
    SERVICE_UNAVAILABLE(5),

    /**
     * Device Busy
     */
    DEVICE_BUSY(6),

    /**
     * Unexpected
     * Other errors
     */
    UNEXPECTED(7),
    ;

    companion object {
        val keyName: String
            get() = "commandErrorDescription"

        /**
         * Search by bluetooth value.
         *
         * @param ble Return value of bluetooth api.
         * @return CommandErrorDescription
         */
        internal fun getFromBle(ble: Byte): CommandErrorDescription? {
            return CommandErrorDescription.values().firstOrNull { it.ble == ble }
        }
    }
}
