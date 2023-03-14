package com.ricoh360.thetableclient.service

import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.ThetaBle

abstract class ThetaService(
    val service: BleService,
    internal val thetaDevice: ThetaBle.ThetaDevice,
)
