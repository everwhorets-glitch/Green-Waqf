package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BestariGreenPrimary
import com.example.ui.theme.BestariMintContainer
import com.example.ui.theme.BestariOnSurfaceVariant

enum class BestariNavTab(
    val title: String,
    val testTag: String
) {
    HOME("Beranda", "nav_tab_home"),
    HISTORY("Tabungan", "nav_tab_history"),
    LEADERBOARD("Peringkat", "nav_tab_leaderboard"),
    MORE("Game & Edu", "nav_tab_more")
}

@Composable
fun BestariBottomBar(
    currentTab: BestariNavTab,
    onTabSelected: (BestariNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bestari_bottom_navigation_bar"),
        containerColor = Color.White,
        contentColor = BestariOnSurfaceVariant,
        tonalElevation = 6.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        BestariNavTab.entries.forEach { tab ->
            val isSelected = tab == currentTab
            val (selectedIcon, unselectedIcon) = when (tab) {
                BestariNavTab.HOME -> Pair(Icons.Filled.Home, Icons.Outlined.Home)
                BestariNavTab.HISTORY -> Pair(Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
                BestariNavTab.LEADERBOARD -> Pair(Icons.Filled.BarChart, Icons.Outlined.BarChart)
                BestariNavTab.MORE -> Pair(Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports)
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BestariGreenPrimary,
                    selectedTextColor = BestariGreenPrimary,
                    indicatorColor = BestariMintContainer,
                    unselectedIconColor = BestariOnSurfaceVariant,
                    unselectedTextColor = BestariOnSurfaceVariant
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
    }
}
