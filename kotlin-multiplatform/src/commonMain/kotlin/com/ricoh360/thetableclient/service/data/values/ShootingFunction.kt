package com.ricoh360.thetableclient.service.data.values

/**
 * Shooting function.
 */
enum class ShootingFunction : SerialNameEnum {
    /**
     * Undefined value
     */
    UNKNOWN,

    /**
     * normal
     */
    NORMAL {
        override val serialName: String = "normal"
    },

    /**
     * Self timer
     *
     * RICOH THETA X is not supported.
     */
    SELF_TIMER {
        override val serialName: String = "selfTimer"
    },

    /**
     * My setting
     */
    MY_SETTING {
        override val serialName: String = "mySetting"
    },
    ;

    companion object {
        val keyName: String
            get() = "shootingFunction"

        internal fun get(serialName: String?): ShootingFunction? {
            return SerialNameEnum.get(serialName, values(), UNKNOWN)
        }
    }
}
