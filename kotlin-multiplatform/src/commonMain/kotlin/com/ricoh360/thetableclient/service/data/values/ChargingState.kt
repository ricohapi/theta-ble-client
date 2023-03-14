package com.ricoh360.thetableclient.service.data.values

/**
 * Battery charging state
 */
enum class ChargingState(internal val ble: Byte?) : SerialNameEnum {
    /**
     * Undefined value
     */
    UNKNOWN(null),

    /**
     * battery charging
     */
    CHARGING(0) {
        override val serialName: String = "charging"
    },

    /**
     * battery charged
     */
    CHARGED(1) {
        override val serialName: String = "charged"
    },

    /**
     * battery disconnect
     */
    DISCONNECT(2) {
        override val serialName: String = "disconnect"
    },
    ;

    companion object {
        val keyName: String
            get() = "batteryState"

        internal fun get(serialName: String?): ChargingState? {
            return SerialNameEnum.get(serialName, values(), UNKNOWN)
        }

        /**
         * Search by bluetooth value.
         *
         * @param ble Return value of bluetooth api.
         * @return ChargingState
         */
        internal fun getFromBle(ble: Byte): ChargingState? {
            return ChargingState.values().firstOrNull { it.ble == ble }
        }
    }
}
