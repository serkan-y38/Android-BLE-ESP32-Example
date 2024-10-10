package com.yilmaz.ble.features.ble.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.yilmaz.ble.features.ble.data.mappers.toBluetoothDeviceModel
import com.yilmaz.ble.features.ble.data.receivers.PairDeviceReceiver
import com.yilmaz.ble.features.ble.domain.BLEController
import com.yilmaz.ble.features.ble.domain.model.BluetoothDeviceModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class BLEControllerImpl(
    private val context: Context
) : BLEController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var isScanning = false
    private var isPairDeviceReceiverRegistered = false

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceModel>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceModel>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            _scannedDevices.update { devices ->
                val newDevice = result.device.toBluetoothDeviceModel()
                if (newDevice in devices) devices else devices + newDevice
            }
        }
    }

    private val pairDeviceReceiver = PairDeviceReceiver(
        onPairRequest = {
            Log.i("BLEControllerImpl -> ", "pairing request sent")
        },
        onPairedSuccessfully = {
            Log.i("BLEControllerImpl -> ", "bond state changed")

            stopScan()
            getPairedDevices()

            _scannedDevices.update { emptyList() }
        }
    )

    init {
        getPairedDevices()
    }

    override fun startScan() {
        if (!hasBluetoothScanPermission()) {
            error("No bluetooth permission")
            return
        }

        if (!isBluetoothEnabled()) {
            error("Bluetooth not enabled")
            return
        }

        if (isScanning) {
            error("Scanning")
            return
        }

        if (!isPairDeviceReceiverRegistered) {
            registerPairDeviceReceiver()
            isPairDeviceReceiverRegistered = true
        }

        getPairedDevices()

        bluetoothAdapter?.bluetoothLeScanner?.startScan(leScanCallback)
        isScanning = true
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        if (!hasBluetoothScanPermission()) {
            error("No bluetooth permission")
            return
        }

        if (!isBluetoothEnabled()) {
            error("Bluetooth not enabled")
            return
        }

        if (!isScanning) {
            error("!Scanning")
            return
        }

        bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        isScanning = false
    }

    override fun release() {
        if (isPairDeviceReceiverRegistered) context.unregisterReceiver(pairDeviceReceiver)
    }

    override fun pair(address: String) {
        if (!hasBluetoothConnectPermission()) return
        val device = bluetoothAdapter?.getRemoteDevice(address)

        device?.let {
            try {
                device.createBond()
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    _errors.emit("Failed to pair with device: ${e.message}")
                }
            }
        } ?: run {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Device not found with address: $address")
            }
        }
    }

    private fun getPairedDevices() {
        if (!hasBluetoothConnectPermission()) {
            error("No BLUETOOTH_CONNECT permission")
            return
        }

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceModel() }
            ?.also { devices -> _pairedDevices.update { devices } }
    }

    private fun registerPairDeviceReceiver() {
        context.registerReceiver(
            pairDeviceReceiver,
            IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
                addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            }
        )
    }

    private fun hasBluetoothScanPermission(): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
                hasPermission = false
            }
        } else {
            if (!hasPermission(Manifest.permission.BLUETOOTH)) {
                hasPermission = false
            }
        }
        return hasPermission
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                hasPermission = false
            }
        } else {
            if (!hasPermission(Manifest.permission.BLUETOOTH)) {
                hasPermission = false
            }
        }
        return hasPermission
    }

    private fun isBluetoothEnabled() = bluetoothAdapter?.isEnabled == true

    private fun error(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _errors.emit(text)
        }
    }

    private fun hasPermission(permission: String) =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}