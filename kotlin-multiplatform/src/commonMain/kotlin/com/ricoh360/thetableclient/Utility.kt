package com.ricoh360.thetableclient

import com.juul.kable.Peripheral

expect suspend fun setPeripheralMtu(peripheral: Peripheral, mtu: Int)
