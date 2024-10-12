package com.yilmaz.ble.features.ble.presentation.screen.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.yilmaz.ble.features.ble.presentation.screen.home_screen.components.devices.DevicesScreen
import com.yilmaz.ble.features.ble.presentation.screen.home_screen.components.receive_send_service_value.ReceiveSendServiceValueScreen

@Composable
fun HomeScreen(
    @Suppress("UNUSED_PARAMETER") navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val hostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = state.message) {
        state.message?.let { message ->
            hostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when {
                state.isConnected -> {
                    ReceiveSendServiceValueScreen(
                        state.receivedValues,
                        state.connectedDeviceName,
                        onDisconnect = { viewModel.disconnect() },
                        onSendValue = { text -> viewModel.sendValue(text) }
                    )
                }

                state.isPairing -> {
                    ConnectingDialog()
                }

                else -> {
                    DevicesScreen(
                        pairedDevices = state.pairedDevices,
                        scannedDevices = state.scannedDevices,
                        onPairedDevicesItemClick = { device -> viewModel.connect(device.address) },
                        onScannedDevicesItemClick = { device -> viewModel.pair(device.address) },
                        onStartScan = { viewModel.startScan() },
                        onStopScan = { viewModel.stopScan() },
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectingDialog() {
    AlertDialog(
        properties = DialogProperties(dismissOnClickOutside = false),
        title = {
            Text(text = "Connecting...")
        },
        text = {
            Text(text = "Please wait while connecting to device")
        },
        onDismissRequest = { },
        confirmButton = {
            TextButton(
                onClick = {  }
            ) {
                Text("OK")
            }
        }
    )
}