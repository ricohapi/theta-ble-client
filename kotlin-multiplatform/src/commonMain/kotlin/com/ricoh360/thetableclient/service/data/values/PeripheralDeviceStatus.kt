package com.ricoh360.thetableclient.service.data.values

/**
 * Peripheral device connection state
 */
enum class PeripheralDeviceStatus(internal val value: String?) {
    /**
     * Undefined value
     */
    UNKNOWN(null),

    /**
     * Unconnected
     */
    IDLE("idle"),

    /**
     * Connected
     */
    CONNECTED("connected"),
    ;

    companion object {
        /**
         * Options property key name
         */
        val keyName: String
            get() = "status"

        /**
         * Search by json value.
         *
         * @param value json value.
         * @return PeripheralDeviceStatus
         */
        fun getFromValue(value: String): PeripheralDeviceStatus {
            return entries.firstOrNull { it.value == value } ?: run {
                println("Unknown value ${UNKNOWN::class.simpleName}: $value")
                UNKNOWN
            }
        }
    }
}
