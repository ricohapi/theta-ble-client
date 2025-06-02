package com.ricoh360.thetableclient

import com.ricoh360.thetableclient.ble.BlePeripheral
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Observe registered characteristic
 */
internal class BleObserveManager(
    val peripheral: BlePeripheral,
    characteristicList: List<BleCharacteristic>,
) {
    val notifyList = mutableMapOf<BleCharacteristic, ((data: ByteArray) -> Unit)?>()
    val scopeList = mutableMapOf<BleCharacteristic, Job>()
    val onCancelList = mutableMapOf<BleCharacteristic, ()-> Unit>()

    init {
        registerBleObservers(characteristicList)
    }

    fun registerBleObservers(characteristicList: List<BleCharacteristic>) {
        characteristicList.forEach { characteristic ->
            if (peripheral.contain(characteristic)) {
                registerBleObserver(characteristic)
            }
        }
    }

    fun setOnNotify(characteristic: BleCharacteristic, onNotify: ((data: ByteArray) -> Unit)?) {
        if (!notifyList.containsKey(characteristic)) {
            throw ThetaBle.ThetaBleApiException("Not supported characteristic: ${characteristic.name}")
        }
        notifyList[characteristic] = onNotify
    }

    fun registerBleObserver(
        characteristic: BleCharacteristic,
        onNotify: ((data: ByteArray) -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ) {
        if (!peripheral.contain(characteristic)) {
            throw ThetaBle.ThetaBleApiException("Not supported characteristic: ${characteristic.name}")
        }
        onCancel?.let {
            onCancelList[characteristic] = it
        }
        if (notifyList.containsKey(characteristic)) {
            notifyList[characteristic] = onNotify
            return
        }
        notifyList[characteristic] = onNotify

        println("register observe: ${characteristic.name}  ${characteristic.uuid}")
        scopeList[characteristic] = CoroutineScope(Dispatchers.Default).launch {
            try {
                peripheral.observe(characteristic) { data ->
                    println("observe on: ${characteristic.name}")
                    notifyList[characteristic]?.let {
                        it(data)
                    }
                }
            } catch (e: CancellationException) {
                println("cancel observe: ${characteristic.name}")
            } catch (e: Throwable) {
                println("error observe: ${characteristic.name}")
                e.printStackTrace()
            }
        }
    }

    suspend fun unregisterBleObserver(characteristic: BleCharacteristic) {
        notifyList.remove(characteristic)
        val scope = scopeList[characteristic]
        scope?.cancel()
        scope?.join()
        scopeList.remove(characteristic)
        onCancelList[characteristic]?.let {
            it.invoke()
            onCancelList.remove(characteristic)
        }
    }

    fun release() {
        notifyList.clear()
        scopeList.forEach {
            it.value.cancel()
        }
        scopeList.clear()
        onCancelList.clear()
    }
}
