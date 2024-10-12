package com.yilmaz.ble.features.ble.data.receivers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class PairDeviceReceiver(
    private val onPairRequest: () -> Unit,
    private val onPairedSuccessfully: (BluetoothDevice?) -> Unit,
    private val onPairing: (BluetoothDevice?) -> Unit,
    private val onPairingError: (BluetoothDevice?) -> Unit,
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(p0: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }

                val bondState = intent.getIntExtra(
                    BluetoothDevice.EXTRA_BOND_STATE,
                    BluetoothDevice.BOND_NONE
                )

                when (bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        onPairedSuccessfully(device)
                    }

                    BluetoothDevice.BOND_BONDING -> {
                        onPairing(device)
                    }

                    else -> {
                        onPairingError(device)
                    }
                }
            }

            BluetoothDevice.ACTION_PAIRING_REQUEST -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }

                val pin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_PAIRING_KEY,
                        BluetoothDevice::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_PAIRING_KEY)
                }

                device?.let { d ->
                    pin?.let { p ->
                        val pinBytes = p.toString().toByteArray(Charsets.UTF_8)

                        d.setPin(pinBytes)
                        d.setPairingConfirmation(true)

                        onPairRequest()
                    }
                }
            }
        }
    }
}