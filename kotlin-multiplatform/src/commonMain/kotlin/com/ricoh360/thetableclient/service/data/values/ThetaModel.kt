package com.ricoh360.thetableclient.service.data.values

internal const val FIRST_CHAR_OF_SERIAL_NUMBER_SC2_B = '4'

/**
 * Support THETA model
 *
 * @param value Theta model got by [CameraControlCommandV2.getInfo]
 * @param firstCharOfSerialNumber First character of serialNumber got by [CameraControlCommandV2.getInfo].
 * Needed just for Theta SC2 or SC2 for business.
 */
enum class ThetaModel(
    internal val value: String,
    internal val prefix: String,
    internal val supported: Boolean,
    internal val firstCharOfSerialNumber: Char? = null
) {
    /**
     * Undefined value
     */
    UNKNOWN("UNKNOWN", "", false),

    /**
     * THETA S
     */
    THETA_S("RICOH THETA S", "XS", false),

    /**
     * THETA SC
     */
    THETA_SC("RICOH THETA SC", "YJ", false),

    /**
     * THETA V
     */
    THETA_V("RICOH THETA V", "YL", true),

    /**
     * THETA Z1
     */
    THETA_Z1("RICOH THETA Z1", "YN", true),

    /**
     * THETA X
     */
    THETA_X("RICOH THETA X", "YR", true),

    /**
     * THETA SC2, the 1st character of which serial number is always other than
     * FIRST_CHAR_OF_SERIAL_NUMBER_SC2_B.
     */
    THETA_SC2("RICOH THETA SC2", "YP", true),

    /**
     * THETA SC2 for business, the first character of which serial number is always
     * FIRST_CHAR_OF_SERIAL_NUMBER_SC2_B.
     */
    THETA_SC2_B("RICOH THETA SC2", "YP", true, FIRST_CHAR_OF_SERIAL_NUMBER_SC2_B),

    /**
     * THETA A1
     */
    THETA_A1("RICOH360 THETA A1", "AA", true),
    ;

    companion object {
        val keyName: String
            get() = "model"

            /**
         * Get THETA model
         *
         * @param model Theta model got by [CameraControlCommandV2.getInfo]
         * @param serialNumber serial number got by [CameraControlCommandV2.getInfo], needed just for Theta SC2 and SC2 for business.
         * @return ThetaModel
         */
        internal fun get(model: String?, serialNumber: String? = null): ThetaModel {
                return serialNumber?.firstOrNull()?.let { firstChar ->
                    entries.filter { it.firstCharOfSerialNumber != null }.firstOrNull {
                        it.value == model && it.firstCharOfSerialNumber == firstChar
                    }
                }
                    ?: run { // In case of serialNumber is null or either model or serialNumber is not matched.
                        entries.sortedWith(compareBy<ThetaModel> { it.value }.thenBy { it.firstCharOfSerialNumber })
                            .firstOrNull { it.value == model } ?: UNKNOWN
                    }
            }
    }
}
