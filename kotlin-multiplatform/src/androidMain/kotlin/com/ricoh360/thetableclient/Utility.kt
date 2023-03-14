package com.ricoh360.thetableclient

import com.juul.kable.AndroidPeripheral
import com.juul.kable.GattStatusException
import com.juul.kable.Peripheral

actual suspend fun setPeripheralMtu(peripheral: Peripheral, mtu: Int) {
    try {
        (peripheral as AndroidPeripheral).requestMtu(mtu)
    } catch (e: GattStatusException) {
        e.printStackTrace()
    }
}
