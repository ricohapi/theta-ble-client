package com.ricoh360.thetableclient.ble

import kotlinx.coroutines.CoroutineScope

internal class MocBleAdvertisement(override val name: String) : BleAdvertisement {
    companion object {
        var onNewPeripheral: ((scope: CoroutineScope) -> BlePeripheral?)? = null
    }

    override fun newPeripheral(scope: CoroutineScope): BlePeripheral {
        onNewPeripheral?.let { callback ->
            callback.invoke(scope)?.let {
                return it
            }
        }
        return MockBlePeripheral(name)
    }
}

internal fun newAdvertisement(name: String): BleAdvertisement {
    return MocBleAdvertisement(name)
}
