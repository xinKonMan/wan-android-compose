/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xinkon.wancompose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Contract for information needed on every Rally navigation destination
 */
interface WanDestination {
    val icon: Int
    val iconSelected:Int
    val route: String
    val tabName: String
}

/**
 * Rally app navigation destinations
 */
object Home : WanDestination {
    override val icon = R.drawable.home
    override val iconSelected = R.drawable.home_fill
    override val route = "Home"
    override val tabName: String = "首页"
}

object Square : WanDestination {
    override val icon = R.drawable.discover
    override val iconSelected = R.drawable.discover_fill
    override val route = "Square"
    override val tabName: String = "广场"
}

object WeChat : WanDestination {
    override val icon = R.drawable.community
    override val iconSelected = R.drawable.community_fill
    override val route = "WeChat"
    override val tabName: String = "公众号"
}

object Mine : WanDestination {
    override val icon = R.drawable.mine
    override val iconSelected = R.drawable.mine_fill
    override val route = "Mine"
    override val tabName: String = "首页"
}

// Screens to be displayed in the top RallyTabRow
val wanTabRowScreens = listOf(Home, Square, WeChat, Mine)
