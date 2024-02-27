package com.xinkon.wancompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xinkon.wancompose.ui.WanNavHost
import com.xinkon.wancompose.ui.components.WanTabRow
import com.xinkon.wancompose.ui.navigateSingleTopTo
import com.xinkon.wancompose.ui.theme.WanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WanApp()
                }
            }
        }
    }
}

@Composable
fun WanApp() {
    WanTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            wanTabRowScreens.find { it.route == currentDestination?.route } ?: Home
        Scaffold(bottomBar = {
            WanTabRow(
                allScreens = wanTabRowScreens,
                onTabSelected = { newScreen -> navController.navigateSingleTopTo(newScreen.route) },
                currentScreen = currentScreen
            )
        }) { innerPadding ->
            Box(
                Modifier.padding(innerPadding)
            ) {
                WanNavHost(navController = navController, modifier = Modifier)
            }
        }
    }
}