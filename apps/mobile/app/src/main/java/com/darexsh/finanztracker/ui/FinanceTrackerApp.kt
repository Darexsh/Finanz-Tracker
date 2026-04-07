package com.darexsh.finanztracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.model.TxType
import com.darexsh.finanztracker.ui.screens.BookingsScreen
import com.darexsh.finanztracker.ui.screens.DashboardScreen
import com.darexsh.finanztracker.ui.screens.ReportsScreen
import com.darexsh.finanztracker.ui.screens.SyncScreen

private data class TabItem(val label: String, val icon: ImageVector)

@Composable
fun FinanceTrackerApp(
    state: TrackerState,
    onAddBooking: (
        description: String,
        amount: Double,
        category: String,
        txType: TxType,
        account: String,
        note: String,
        taxDeclaration: Boolean
    ) -> Unit
) {
    val tabs = listOf(
        TabItem(stringResource(R.string.tab_overview), Icons.Outlined.Dashboard),
        TabItem(stringResource(R.string.tab_bookings), Icons.AutoMirrored.Outlined.List),
        TabItem(stringResource(R.string.tab_reports), Icons.Outlined.Assessment),
        TabItem(stringResource(R.string.tab_sync), Icons.Outlined.Sync)
    )
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedIndex) {
                0 -> DashboardScreen(state = state)
                1 -> BookingsScreen(state = state, onAddBooking = onAddBooking)
                2 -> ReportsScreen(state = state)
                else -> SyncScreen()
            }
        }
    }
}
