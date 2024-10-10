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
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    init {
        getErrors()
    }

    private fun getErrors() {
        bleController.errors.onEach { error ->
            _state.update {
                it.copy(
                    errorMessage = error
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

    override fun onCleared() {
        super.onCleared()
        bleController.release()
    }
}