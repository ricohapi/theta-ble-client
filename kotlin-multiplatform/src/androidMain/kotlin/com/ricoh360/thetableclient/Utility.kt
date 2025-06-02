package com.ricoh360.thetableclient

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import com.juul.kable.AndroidPeripheral
import com.juul.kable.GattStatusException
import com.juul.kable.Peripheral

internal actual suspend fun setPeripheralMtu(peripheral: Peripheral, mtu: Int) {
    try {
        (peripheral as AndroidPeripheral).requestMtu(mtu)
    } catch (e: GattStatusException) {
        e.printStackTrace()
    }
}

internal fun getBluetoothAdapter(): BluetoothAdapter {
    return (applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
}

@SuppressLint("MissingPermission")
internal actual fun tryBond(peripheral: Peripheral) {
    try {
        println("tryBond ${peripheral.name}")
        val address = (peripheral as AndroidPeripheral).address
        val adapter = getBluetoothAdapter()
        val device = adapter.getRemoteDevice(address)
        println("  state ${device.bondState}")
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            println("  crateBond ${peripheral.name}")
            device.createBond()
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}
