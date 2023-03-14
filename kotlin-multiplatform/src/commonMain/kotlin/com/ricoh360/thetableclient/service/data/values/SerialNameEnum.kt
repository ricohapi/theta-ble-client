package com.ricoh360.thetableclient.service.data.values

internal interface SerialNameEnum {
    val serialName: String
        get() = (this as Enum<*>).name

    companion object {
        fun <T> get(serialName: String?, values: Array<T>, unknown: T?): T?
                where T : Enum<T>, T : SerialNameEnum {
            return serialName?.let {
                values.firstOrNull { it.serialName == serialName }
                    ?: unknown
            }
        }
    }
}
