package com.ricoh360.thetableclient.ble

import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import kotlinx.coroutines.delay

internal class MockBlePeripheral(override val name: String) : BlePeripheral {
    companion object {
        var onConnect: (suspend () -> Unit)? = null
        var onDisconnect: (suspend () -> Unit)? = null
        var onWrite: ((characteristic: BleCharacteristic, data: ByteArray) -> Unit)? = null
        var onRead: ((characteristic: BleCharacteristic) -> ByteArray)? = null
        var onRequestMtu: ((mtu: Int) -> Unit)? = null
        var onObserve: ((characteristic: BleCharacteristic, collect: ((ByteArray) -> Unit)) -> Unit)? =
            null
        var onContain: ((characteristic: BleCharacteristic) -> Boolean)? = null
        var supportedServiceList: List<BleService>? = null
        var onTryBond: (() -> Unit)? = null
    }

    override suspend fun connect() {
        onConnect?.let { it() }
    }

    override suspend fun disconnect() {
        onDisconnect?.let { it() }
    }

    override suspend fun write(characteristic: BleCharacteristic, data: ByteArray) {
        onWrite?.let { it(characteristic, data) }
    }

    override suspend fun read(characteristic: BleCharacteristic): ByteArray {
        return onRead?.let { it(characteristic) } ?: ByteArray(0)
    }

    override suspend fun requestMtu(mtu: Int) {
        onRequestMtu?.let { it(mtu) }
    }

    override suspend fun observe(
        characteristic: BleCharacteristic,
        collect: ((ByteArray) -> Unit),
    ) {
        onObserve?.let { it(characteristic, collect) }

        // wait for cancel observe
        while (true) {
            delay(1)
        }
    }

    override fun contain(characteristic: BleCharacteristic): Boolean {
        return onContain?.let {
            it(characteristic)
        } ?: true
    }

    override fun contain(service: BleService): Boolean {
        return supportedServiceList?.contains(service) ?: true
    }

    override fun tryBond() {
        onTryBond?.let { it() }
    }
}
