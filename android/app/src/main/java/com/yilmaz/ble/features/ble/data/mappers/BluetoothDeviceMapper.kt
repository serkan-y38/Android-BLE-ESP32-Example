package com.yilmaz.ble.features.ble.data.mappers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.yilmaz.ble.features.ble.domain.model.BluetoothDeviceModel

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceModel() = BluetoothDeviceModel(
    name = name,
    address = address
)