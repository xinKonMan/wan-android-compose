package com.xinkon.wancompose.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xinkon.wancompose.Home
import com.xinkon.wancompose.Mine
import com.xinkon.wancompose.Square
import com.xinkon.wancompose.WeChat

/**
 * Created by cwl on 2024/2/27.
 */
@Composable
fun WanNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = Home.route, modifier = modifier) {
        composable(Home.route) {
            Text(text = "Home")
        }
        composable(Square.route) {
            Text(text = "Square")
        }
        composable(WeChat.route) {
            Text(text = "WeChat")
        }
        composable(Mine.route) {
            Text(text = "Mine")
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}