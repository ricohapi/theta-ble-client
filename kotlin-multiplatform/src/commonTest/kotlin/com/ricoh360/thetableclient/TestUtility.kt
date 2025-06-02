package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.MocBleAdvertisement
import com.ricoh360.thetableclient.ble.MockBlePeripheral
import com.ricoh360.thetableclient.ble.MockBleScanner

fun initMock() {
    ThetaBle.waitScan = WAIT_SCAN

    MockBleScanner.onInit = null
    MockBleScanner.bleList = null
    MockBleScanner.scanInterval = 10

    MocBleAdvertisement.onNewPeripheral = null

    MockBlePeripheral.onConnect = null
    MockBlePeripheral.onDisconnect = null
    MockBlePeripheral.onWrite = null
    MockBlePeripheral.onRead = null
    MockBlePeripheral.onRequestMtu = null
    MockBlePeripheral.onObserve = null
    MockBlePeripheral.onContain = null
    MockBlePeripheral.supportedServiceList = null
    MockBlePeripheral.onTryBond = null
}
