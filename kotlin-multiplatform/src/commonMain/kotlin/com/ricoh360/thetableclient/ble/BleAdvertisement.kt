package com.ricoh360.thetableclient.ble

import com.juul.kable.Advertisement
import com.juul.kable.peripheral
import kotlinx.coroutines.CoroutineScope

internal interface BleAdvertisement {
    val name: String
    fun newPeripheral(scope: CoroutineScope): BlePeripheral
}

internal class BleAdvertisementImpl internal constructor(val advertisement: Advertisement) :
    BleAdvertisement {
    override val name: String
        get() = advertisement.name ?: ""

    override fun newPeripheral(scope: CoroutineScope): BlePeripheral {
        return newPeripheral(scope.peripheral(advertisement))
    }
}

internal fun newAdvertisement(advertisement: Advertisement): BleAdvertisement {
    return BleAdvertisementImpl(advertisement)
}
