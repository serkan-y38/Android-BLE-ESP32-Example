package com.yilmaz.ble.features.ble.presentation.screen.home_screen

import com.yilmaz.ble.features.ble.domain.model.BluetoothDeviceModel

data class HomeState(
    val scannedDevices: List<BluetoothDeviceModel> = emptyList(),
    val pairedDevices: List<BluetoothDeviceModel> = emptyList(),
    val errorMessage: String? = null,
)
