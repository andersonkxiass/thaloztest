package br.com.anderson.thaloztest

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.outlined.VpnLock
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.anderson.thaloztest.screens.NetworkUtilsScreen
import br.com.anderson.thaloztest.screens.SimpleListScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        setContent {
            val navController = rememberNavController()
            var selectedTabIndex by remember { mutableIntStateOf(0) }

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTabIndex == 0,
                            onClick = {
                                selectedTabIndex = 0
                                navController.navigate("network_utils")
                            },
                            icon = { Icon(Icons.Filled.NetworkCheck, contentDescription = null) }
                        )

                        NavigationBarItem(
                            selected = selectedTabIndex == 1,
                            onClick = {
                                selectedTabIndex = 1
                                navController.navigate("vpn_list")
                            },
                            icon = { Icon(Icons.Outlined.VpnLock, contentDescription = null) }
                        )
                    }
                }
            ) { innerPadding ->

                NavHost(
                    navController = navController,
                    startDestination = "network_utils",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("network_utils") {
                        NetworkUtilsScreen(
                            connectivityManager = connectivityManager
                        )
                    }
                    composable("vpn_list") {
                        SimpleListScreen()
                    }
                }
            }
        }
    }
}
