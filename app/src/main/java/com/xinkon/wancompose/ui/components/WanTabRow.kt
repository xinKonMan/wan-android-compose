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

package com.xinkon.wancompose.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xinkon.wancompose.Home
import com.xinkon.wancompose.WanDestination
import com.xinkon.wancompose.ui.theme.Black
import com.xinkon.wancompose.ui.theme.BottomBar
import com.xinkon.wancompose.ui.theme.Green
import com.xinkon.wancompose.wanTabRowScreens

@Preview
@Composable
fun PreviewTabRow() {
    WanTabRow(
        allScreens = wanTabRowScreens,
        onTabSelected = {},
        currentScreen = Home
    )
}

@Composable
fun WanTabRow(
    allScreens: List<WanDestination>,
    onTabSelected: (WanDestination) -> Unit,
    currentScreen: WanDestination
) {
    Surface(modifier = Modifier.background(BottomBar)) {
        Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.SpaceEvenly) {
            allScreens.forEach { screen ->
                WanTab(
                    text = screen.tabName,
                    icon = screen.icon,
                    iconSelect = screen.iconSelected,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen,
                    modifier = Modifier.weight(1f) //必须这样设置才行，不能直接在Column中设置，原因不明
                )
            }
        }
    }
}

@Composable
private fun WanTab(
    text: String,
    icon: Int,
    iconSelect: Int,
    onSelected: () -> Unit,
    selected: Boolean,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .animateContentSize()
            .height(68.dp)
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = if (selected) iconSelect else icon),
            contentDescription = text,
        )
        Text(text, fontSize = 12.sp, color = if (selected) Green else Black)
    }
}
