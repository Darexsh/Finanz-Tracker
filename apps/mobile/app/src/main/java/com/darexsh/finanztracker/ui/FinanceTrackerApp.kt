package com.darexsh.finanztracker.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.Dialog
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.domain.BookingDraft
import com.darexsh.finanztracker.domain.CategoryMutationStatus
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
    onAddCustomCategory: (name: String) -> CategoryMutationStatus,
    onRenameCustomCategory: (currentName: String, newName: String) -> CategoryMutationStatus,
    onDeleteCustomCategory: (name: String) -> CategoryMutationStatus,
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedScreenBackground(
            modifier = Modifier.fillMaxSize()
        )
        Scaffold(
            containerColor = Color.Transparent,
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
            ) {
                when (selectedIndex) {
                    0 -> DashboardScreen(state = state)
                    1 -> BookingsScreen(
                        state = state,
                        onAddBooking = onAddBooking,
                        onUpdateBooking = onUpdateBooking,
                        onDeleteBooking = onDeleteBooking,
                        onDeleteBookings = onDeleteBookings,
                        onSetBookingTaxDeclaration = onSetBookingTaxDeclaration,
                        onAddCustomCategory = onAddCustomCategory,
                        onRenameCustomCategory = onRenameCustomCategory,
                        onDeleteCustomCategory = onDeleteCustomCategory
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
}

@Composable
private fun AnimatedScreenBackground(
    modifier: Modifier = Modifier
) {
    var mix by remember { mutableStateOf(0f) }
    var direction by remember { mutableStateOf(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            val next = mix + (0.03f * direction)
            if (next >= 1f) {
                mix = 1f
                direction = -1f
            } else if (next <= 0f) {
                mix = 0f
                direction = 1f
            } else {
                mix = next
            }
            delay(120)
        }
    }
    val gradientTop = lerp(Color(0xFF4A90E2), Color(0xFF0097A7), mix)
    val gradientBottom = lerp(Color(0xFFCDE7FF), Color(0xFFD9FFF4), mix)

    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    gradientTop,
                    gradientBottom
                )
            )
        )
    )
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    val fallback = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(fallback)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return fallback
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
    val context = LocalContext.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val activeUser = state.users.firstOrNull { it.id == state.activeUserId } ?: state.users.firstOrNull()
    val appIconBitmap = remember {
        runCatching {
            context.packageManager.getApplicationIcon(context.packageName)
        }.getOrNull()?.let { drawableToBitmap(it).asImageBitmap() }
    }
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFE8F4FF),
                                Color(0xFFFFFFFF)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(460f, 240f)
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0F766E),
                                        Color(0xFF0EA5E9)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .alpha(0.28f)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0x660EA5E9),
                                            Color.Transparent
                                        ),
                                        radius = 58f
                                    ),
                                    shape = CircleShape
                                )
                        )
                        if (appIconBitmap != null) {
                            Image(
                                bitmap = appIconBitmap,
                                contentDescription = stringResource(R.string.app_name),
                                modifier = Modifier.size(26.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text(
                                text = "FT",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            stringResource(R.string.app_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if (profileExpanded) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = activeUser?.name.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.profile_label)) },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    userMenuExpanded = !userMenuExpanded
                                    if (!userMenuExpanded) focusManager.clearFocus(force = true)
                                }) {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = userMenuExpanded)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { userMenuExpanded = true }
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) userMenuExpanded = true
                                }
                        )
                        DropdownMenu(
                            expanded = userMenuExpanded,
                            onDismissRequest = {
                                userMenuExpanded = false
                                focusManager.clearFocus(force = true)
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            properties = PopupProperties(focusable = false)
                        ) {
                            state.users.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text(user.name) },
                                    onClick = {
                                        onSetActiveUser(user.id)
                                        userMenuExpanded = false
                                        focusManager.clearFocus(force = true)
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
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.dialog_add_user_title), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(stringResource(R.string.dialog_user_name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text(stringResource(R.string.button_cancel))
                        }
                        TextButton(onClick = {
                            if (onAddUser(nameInput)) {
                                showAddDialog = false
                            }
                        }) { Text(stringResource(R.string.button_save)) }
                    }
                }
            }
        }
    }

    if (showRenameDialog) {
        Dialog(onDismissRequest = { showRenameDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.dialog_rename_user_title), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(stringResource(R.string.dialog_user_name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showRenameDialog = false }) {
                            Text(stringResource(R.string.button_cancel))
                        }
                        TextButton(onClick = {
                            if (onRenameActiveUser(nameInput)) {
                                showRenameDialog = false
                            }
                        }) { Text(stringResource(R.string.button_update)) }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        val activeId = activeUser?.id
        val bookingsCount = if (activeId.isNullOrBlank()) 0 else state.bookings.count { it.userId == activeId }
        Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.dialog_delete_user_title), style = MaterialTheme.typography.titleMedium)
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(stringResource(R.string.button_cancel))
                        }
                        TextButton(
                            onClick = {
                                if (state.users.size > 1 && onDeleteActiveUser()) {
                                    showDeleteDialog = false
                                }
                            }
                        ) { Text(stringResource(R.string.button_delete)) }
                    }
                }
            }
        }
    }
}
