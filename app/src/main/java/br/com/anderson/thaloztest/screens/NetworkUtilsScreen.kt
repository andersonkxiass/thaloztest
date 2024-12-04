package br.com.anderson.thaloztest.screens

import android.net.ConnectivityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.anderson.thaloztest.R
import br.com.anderson.thaloztest.utils.NetworkMonitor

@Composable
fun NetworkUtilsScreen(
    connectivityManager: ConnectivityManager,
    viewModel: NetworkUtilsViewModel = viewModel()
) {
    val networkMonitor = NetworkMonitor(connectivityManager)

    val networkState by networkMonitor.isConnected.collectAsState(false)
    val speedTestInfo by viewModel.speedTestInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    NetworkComponent(
        networkState = networkState,
        isLoading = isLoading,
        testConnectionSpeed = {
            viewModel.testConnectionSpeed()
        },
        speedTestInfo = speedTestInfo
    )
}

@Composable
private fun NetworkComponent(
    networkState: Boolean,
    isLoading: Boolean,
    testConnectionSpeed: () -> Unit,
    speedTestInfo: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier.padding(16.dp).size(80.dp),
            painter = painterResource(R.drawable.network),
            contentDescription = null,
            tint = if (networkState) Color.Green else Color.Gray
        )

        Text(
            text = "Your device has network: ${if (networkState) "Online" else "Offline"}",
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = !isLoading && networkState,
            onClick = { testConnectionSpeed() }
        ) {
            Text(text = if (isLoading) "Testing..." else "Test Connection Speed")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.Companion.align(Alignment.CenterHorizontally))
        } else {
            Text(
                text = speedTestInfo,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NetworkComponentPreview() {
    NetworkComponent(
        networkState = true,
        isLoading = false,
        testConnectionSpeed = {},
        speedTestInfo = "Speed Test Info"
    )
}

