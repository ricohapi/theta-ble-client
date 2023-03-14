package com.ricoh360.thetableclient.ble

import com.juul.kable.Peripheral
import com.juul.kable.WriteType
import com.ricoh360.thetableclient.BleCharacteristic
import com.ricoh360.thetableclient.BleService
import com.ricoh360.thetableclient.setPeripheralMtu

internal interface BlePeripheral {
    val name: String

    suspend fun connect()
    suspend fun disconnect()
    suspend fun write(characteristic: BleCharacteristic, data: ByteArray)
    suspend fun read(characteristic: BleCharacteristic): ByteArray
    suspend fun requestMtu(mtu: Int)
    suspend fun observe(characteristic: BleCharacteristic, collect: (ByteArray) -> Unit)
    fun contain(characteristic: BleCharacteristic): Boolean
    fun contain(service: BleService): Boolean
}

internal class BlePeripheralImpl internal constructor(val peripheral: Peripheral) : BlePeripheral {
    override val name: String
        get() = peripheral.name!!

    override suspend fun connect() {
        peripheral.connect()
    }

    override suspend fun disconnect() {
        peripheral.disconnect()
    }

    override suspend fun write(characteristic: BleCharacteristic, data: ByteArray) {
        peripheral.write(characteristic.getCharacteristic(), data, WriteType.WithResponse)
    }

    override suspend fun read(characteristic: BleCharacteristic): ByteArray {
        return peripheral.read(characteristic.getCharacteristic())
    }

    override suspend fun requestMtu(mtu: Int) {
        setPeripheralMtu(peripheral, mtu)
    }

    override suspend fun observe(characteristic: BleCharacteristic, collect: (ByteArray) -> Unit) {
        val observation = peripheral.observe(characteristic.getCharacteristic())
        observation.collect {
            collect(it)
        }
    }

    override fun contain(characteristic: BleCharacteristic): Boolean {
        peripheral.services?.run {
            find {
                it.serviceUuid.toString().uppercase() == characteristic.bleService.uuid
            }?.let { services ->
                services.characteristics.find {
                    it.characteristicUuid.toString().uppercase() == characteristic.uuid
                }
            }?.run {
                return true
            }
        }
        return false
    }

    override fun contain(service: BleService): Boolean {
        peripheral.services?.run {
            find {
                it.serviceUuid.toString().uppercase() == service.uuid
            }?.run {
                return true
            }
        }
        return false
    }
}

internal fun newPeripheral(peripheral: Peripheral): BlePeripheral {
    return BlePeripheralImpl(peripheral)
}
