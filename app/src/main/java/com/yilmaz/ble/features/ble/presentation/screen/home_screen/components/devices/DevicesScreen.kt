package com.yilmaz.ble.features.ble.presentation.screen.home_screen.components.devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yilmaz.ble.R
import com.yilmaz.ble.features.ble.domain.model.BluetoothDeviceModel

@Composable
fun DevicesScreen(
    pairedDevices: List<BluetoothDeviceModel>,
    scannedDevices: List<BluetoothDeviceModel>,
    onPairedDevicesItemClick: (BluetoothDeviceModel) -> Unit,
    onScannedDevicesItemClick: (BluetoothDeviceModel) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_bluetooth_connected_24),
                        modifier = Modifier.padding(end = 8.dp),
                        contentDescription = "Paired devices",
                    )
                    Text(
                        text = "Paired Devices",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            items(pairedDevices) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = device.name ?: "(No name)",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 12.dp)
                            .clickable { onPairedDevicesItemClick(device) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_bluetooth_searching_24),
                        modifier = Modifier.padding(end = 8.dp),
                        contentDescription = "Searched devices"
                    )
                    Text(
                        text = "Scanned Devices",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            items(scannedDevices) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = device.name ?: "(No name)",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 12.dp)
                            .clickable { onScannedDevicesItemClick(device) }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(all = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { onStartScan() }) {
                Text(text = "Start scan")
            }
            Button(
                onClick = { onStopScan() }) {
                Text(text = "Stop scan")
            }
        }
    }
}