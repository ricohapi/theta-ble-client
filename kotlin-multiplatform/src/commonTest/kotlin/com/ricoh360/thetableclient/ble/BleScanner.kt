package com.ricoh360.thetableclient.ble

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class MockBleScanner(timeout: Int, name: String?) : BleScanner(timeout, name) {

    companion object {
        const val DEFAULT_NAME = "12345678"
        var onInit: (() -> Unit)? = null
        var bleList: List<BleAdvertisement?>? = null
        var scanInterval: Long = 10
    }

    override fun init() {
        onInit?.let { it() }
    }

    override val advertisements: Flow<BleAdvertisement>
        get() = flow {
            val targetList = bleList ?: listOf(newAdvertisement(name ?: DEFAULT_NAME))
            targetList.forEach {
                delay(scanInterval)
                it?.run { emit(this) } ?: throw Exception("scan")
            }
        }
}

internal fun getScanner(timeout: Int, name: String?): BleScanner {
    return MockBleScanner(timeout, name)
}
