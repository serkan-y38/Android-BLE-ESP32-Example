package com.yilmaz.ble.features.ble.domain

import com.yilmaz.ble.features.ble.domain.model.BluetoothDeviceModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BLEController {
    val scannedDevices: StateFlow<List<BluetoothDeviceModel>>
    val pairedDevices: StateFlow<List<BluetoothDeviceModel>>
    val errors: SharedFlow<String>

    fun startScan()

    fun stopScan()

    fun release()

    fun pair(address: String)
}