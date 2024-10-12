package com.yilmaz.ble.features.ble.presentation.screen.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.ble.features.ble.domain.BLEController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bleController: BLEController
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())

    val state = combine(
        bleController.scannedDevices,
        bleController.pairedDevices,
        bleController.receivedValues,
        _state
    ) { scannedDevices, pairedDevices, receivedValues, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            receivedValues = receivedValues
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    init {
        getMessage()
        isConnected()
        isPairing()
        getConnectedDeviceName()
    }

    private fun getMessage() {
        bleController.message.onEach { message ->
            _state.update {
                it.copy(
                    message = message
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun isConnected() {
        bleController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)
    }

    private fun isPairing() {
        bleController.isPairing.onEach { isPairing ->
            _state.update { it.copy(isPairing = isPairing) }
        }.launchIn(viewModelScope)
    }

    private fun getConnectedDeviceName() {
        bleController.connectedDeviceName.onEach { name ->
            _state.update {
                it.copy(
                    connectedDeviceName = name
                )
            }
        }.launchIn(viewModelScope)
    }

    fun startScan() {
        bleController.startScan()
    }

    fun stopScan() {
        bleController.stopScan()
    }

    fun pair(address: String) {
        bleController.pair(address)
    }

    fun connect(address: String) {
        bleController.connect(address)
    }

    fun disconnect() {
        bleController.disConnect()
    }

    fun sendValue(text: String) {
        bleController.sendValue(text)
    }

    override fun onCleared() {
        super.onCleared()
        bleController.release()
    }
}