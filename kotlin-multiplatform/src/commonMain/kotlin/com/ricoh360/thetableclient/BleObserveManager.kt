package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.BlePeripheral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Observe registered characteristic
 */
internal class BleObserveManager(
    val peripheral: BlePeripheral,
    characteristicList: List<BleCharacteristic>,
) {
    val notifyList = mutableMapOf<BleCharacteristic, ((data: ByteArray) -> Unit)?>()
    val scopeList = mutableListOf<CoroutineScope>()

    init {
        registerBleObservers(characteristicList)
    }

    fun registerBleObservers(characteristicList: List<BleCharacteristic>) {
        characteristicList.forEach { characteristic ->
            if (peripheral.contain(characteristic)) {
                notifyList[characteristic] = null
                val scope = CoroutineScope(Dispatchers.Default)
                scopeList.add(scope)
                scope.launch {
                    peripheral.observe(characteristic) { data ->
                        notifyList[characteristic]?.let {
                            it(data)
                        }
                    }
                }
            }
        }
    }

    fun setOnNotify(characteristic: BleCharacteristic, onNotify: ((data: ByteArray) -> Unit)?) {
        if (!notifyList.containsKey(characteristic)) {
            throw ThetaBle.ThetaBleApiException("Not supported characteristic: ${characteristic.name}")
        }
        notifyList[characteristic] = onNotify
    }

    fun release() {
        scopeList.forEach {
            it.cancel()
        }
        scopeList.clear()
    }
}
