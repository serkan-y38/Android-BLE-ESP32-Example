package com.yilmaz.ble.features.ble.domain

import com.yilmaz.ble.features.ble.domain.model.BluetoothDeviceModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BLEController {
    val scannedDevices: StateFlow<List<BluetoothDeviceModel>>
    val pairedDevices: StateFlow<List<BluetoothDeviceModel>>
    val receivedValues: StateFlow<List<String>>

    val message: SharedFlow<String>
    val isConnected: StateFlow<Boolean>
    val isPairing: StateFlow<Boolean>
    val connectedDeviceName: StateFlow<String>

    fun startScan()

    fun stopScan()

    fun release()

    fun pair(address: String)

    fun connect(address: String)

    fun disConnect()

    fun sendValue(text: String)
}