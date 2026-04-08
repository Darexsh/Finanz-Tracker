package com.darexsh.finanztracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.domain.BookingDraft
import com.darexsh.finanztracker.model.TrackerState
import com.darexsh.finanztracker.ui.screens.BookingsScreen
import com.darexsh.finanztracker.ui.screens.DashboardScreen
import com.darexsh.finanztracker.ui.screens.ReportsScreen
import com.darexsh.finanztracker.ui.screens.SyncScreen
import kotlinx.coroutines.delay

private data class TabItem(val label: String, val icon: ImageVector)

@Composable
fun FinanceTrackerApp(
    state: TrackerState,
    onSetActiveUser: (userId: String) -> Unit,
    onAddUser: (name: String) -> Boolean,
    onRenameActiveUser: (name: String) -> Boolean,
    onDeleteActiveUser: () -> Boolean,
    onAddBooking: (draft: BookingDraft) -> Unit,
    onUpdateBooking: (bookingId: String, draft: BookingDraft) -> Unit,
    onDeleteBooking: (bookingId: String) -> Unit,
    onDeleteBookings: (bookingIds: Set<String>) -> Unit,
    onSetBookingTaxDeclaration: (bookingId: String, taxDeclaration: Boolean) -> Unit,
    onSyncFolderSelected: (String) -> Unit,
    onSyncFolderCleared: () -> Unit
) {
    val tabs = listOf(
        TabItem(stringResource(R.string.tab_overview), Icons.Outlined.Dashboard),
        TabItem(stringResource(R.string.tab_bookings), Icons.AutoMirrored.Outlined.List),
        TabItem(stringResource(R.string.tab_reports), Icons.Outlined.Assessment),
        TabItem(stringResource(R.string.tab_sync), Icons.Outlined.Sync)
    )
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            UserHeaderBar(
                state = state,
                onSetActiveUser = onSetActiveUser,
                onAddUser = onAddUser,
                onRenameActiveUser = onRenameActiveUser,
                onDeleteActiveUser = onDeleteActiveUser
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF6FBFF),
                            Color(0xFFEEF5FF)
                        )
                    )
                )
        ) {
            when (selectedIndex) {
                0 -> DashboardScreen(state = state)
                1 -> BookingsScreen(
                    state = state,
                    onAddBooking = onAddBooking,
                    onUpdateBooking = onUpdateBooking,
                    onDeleteBooking = onDeleteBooking,
                    onDeleteBookings = onDeleteBookings,
                    onSetBookingTaxDeclaration = onSetBookingTaxDeclaration
                )
                2 -> ReportsScreen(state = state)
                else -> SyncScreen(
                    syncFolderUri = state.syncFolderUri,
                    onSyncFolderSelected = onSyncFolderSelected,
                    onSyncFolderCleared = onSyncFolderCleared
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun UserHeaderBar(
    state: TrackerState,
    onSetActiveUser: (userId: String) -> Unit,
    onAddUser: (name: String) -> Boolean,
    onRenameActiveUser: (name: String) -> Boolean,
    onDeleteActiveUser: () -> Boolean
) {
    val activeUser = state.users.firstOrNull { it.id == state.activeUserId } ?: state.users.firstOrNull()
    var userMenuExpanded by remember { mutableStateOf(false) }
    var profileExpanded by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }

    LaunchedEffect(profileExpanded, userMenuExpanded, showAddDialog, showRenameDialog, showDeleteDialog) {
        if (profileExpanded && !userMenuExpanded && !showAddDialog && !showRenameDialog && !showDeleteDialog) {
            delay(3500)
            if (!userMenuExpanded && !showAddDialog && !showRenameDialog && !showDeleteDialog) {
                profileExpanded = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)) {
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        if (profileExpanded) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = userMenuExpanded,
                        onExpandedChange = { userMenuExpanded = !userMenuExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = activeUser?.name.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.profile_label)) },
                            singleLine = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userMenuExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = userMenuExpanded,
                            onDismissRequest = { userMenuExpanded = false }
                        ) {
                            state.users.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text(user.name) },
                                    onClick = {
                                        onSetActiveUser(user.id)
                                        userMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                nameInput = ""
                                showAddDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.profile_button_new),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                nameInput = activeUser?.name.orEmpty()
                                showRenameDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.profile_button_rename),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                        }
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.profile_button_delete),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    shape = RoundedCornerShape(999.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .clickable { profileExpanded = true }
                        .padding(top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(
                            modifier = Modifier
                                .width(24.dp)
                                .height(4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(999.dp)
                                )
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.dialog_add_user_title)) },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(stringResource(R.string.dialog_user_name_label)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (onAddUser(nameInput)) {
                        showAddDialog = false
                    }
                }) { Text(stringResource(R.string.button_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(stringResource(R.string.button_cancel))
                }
            }
        )
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text(stringResource(R.string.dialog_rename_user_title)) },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(stringResource(R.string.dialog_user_name_label)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (onRenameActiveUser(nameInput)) {
                        showRenameDialog = false
                    }
                }) { Text(stringResource(R.string.button_update)) }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text(stringResource(R.string.button_cancel))
                }
            }
        )
    }

    if (showDeleteDialog) {
        val activeId = activeUser?.id
        val bookingsCount = if (activeId.isNullOrBlank()) 0 else state.bookings.count { it.userId == activeId }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_user_title)) },
            text = {
                Text(
                    if (state.users.size <= 1) {
                        stringResource(R.string.dialog_delete_last_user_blocked)
                    } else {
                        stringResource(
                            R.string.dialog_delete_user_message,
                            activeUser?.name.orEmpty(),
                            bookingsCount
                        )
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (state.users.size > 1 && onDeleteActiveUser()) {
                            showDeleteDialog = false
                        }
                    }
                ) { Text(stringResource(R.string.button_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.button_cancel))
                }
            }
        )
    }
}
