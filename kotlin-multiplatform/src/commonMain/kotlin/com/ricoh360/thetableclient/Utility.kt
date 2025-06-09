package com.ricoh360.thetableclient

import com.juul.kable.Peripheral

internal expect suspend fun setPeripheralMtu(peripheral: Peripheral, mtu: Int)

internal expect fun tryBond(peripheral: Peripheral)
