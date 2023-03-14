package com.ricoh360.thetableclient.service.data.values

/**
 *  Capture Status
 */
enum class CaptureStatus : SerialNameEnum {
    /**
     * Undefined value
     */
    UNKNOWN,

    /**
     * shooting: Performing continuously shoots,
     */
    SHOOTING {
        override val serialName: String = "shooting"
    },

    /**
     * idle: In standby,
     */
    IDLE {
        override val serialName: String = "idle"
    },

    /**
     * self-timer countdown: Self-timer is operating,
     */
    SELF_TIMER_COUNTDOWN {
        override val serialName: String = "self-timer countdown"
    },

    /**
     * bracket shooting: Performing multi bracket shooting,
     */
    BRACKET_SHOOTING {
        override val serialName: String = "bracket shooting"
    },

    /**
     * converting: Converting post file…,
     */
    CONVERTING {
        override val serialName: String = "converting"
    },

    /**
     * timeShift shooting: Performing timeShift shooting,
     */
    TIME_SHIFT_SHOOTING {
        override val serialName: String = "timeShift shooting"
    },

    /**
     * continuous shooting: Performing continuous shooting,
     */
    CONTINUOUS_SHOOTING {
        override val serialName: String = "continuous shooting"
    },

    /**
     * retrospective image recording: Waiting for retrospective video…
     */
    RETROSPECTIVE_IMAGE_RECORDING {
        override val serialName: String = "retrospective image recording"
    },
    ;

    companion object {
        val keyName: String
            get() = "captureStatus"

        internal fun get(serialName: String?): CaptureStatus? {
            return SerialNameEnum.get(serialName, values(), UNKNOWN)
        }
    }
}
