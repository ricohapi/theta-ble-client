package com.ricoh360.thetableclient

import com.juul.kable.Peripheral

internal actual suspend fun setPeripheralMtu(peripheral: Peripheral, mtu: Int) {}

internal actual fun tryBond(peripheral: Peripheral) {}
