package com.darexsh.finanztracker.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.darexsh.finanztracker.R
import com.darexsh.finanztracker.model.AppSettings
import com.darexsh.finanztracker.model.AppStartTab
import com.darexsh.finanztracker.model.CurrencyPreference
import com.darexsh.finanztracker.model.DateFormatPreference
import com.darexsh.finanztracker.model.ExportFormatPreference
import com.darexsh.finanztracker.model.FontSizeMode
import com.darexsh.finanztracker.model.LanguagePreference
import com.darexsh.finanztracker.model.NavigationAnimationStyle
import com.darexsh.finanztracker.model.TrackerState

@Composable
fun SettingsScreen(
    state: TrackerState,
    settings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onExportStateJson: () -> String,
    onImportStateJson: (String) -> Boolean
) {
    val context = LocalContext.current
    val backupExportSuccess = stringResource(R.string.settings_backup_export_success)
    val backupExportFailed = stringResource(R.string.settings_backup_export_failed)
    val backupImportSuccess = stringResource(R.string.settings_backup_import_success)
    val backupImportFailed = stringResource(R.string.settings_backup_import_failed)
    val appLockUnavailableText = stringResource(R.string.settings_app_lock_unavailable)
    val appLockAuthTitle = stringResource(R.string.settings_app_lock_auth_title)
    val appLockAuthSubtitle = stringResource(R.string.settings_app_lock_auth_subtitle)
    var showAboutDialog by remember { mutableStateOf(false) }
    val appIconBitmap = remember(context) {
        runCatching {
            context.packageManager.getApplicationIcon(context.packageName)
        }.getOrNull()?.let { drawableToBitmap(it).asImageBitmap() }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val payload = onExportStateJson()
        val ok = runCatching {
            context.contentResolver.openOutputStream(uri)?.use { it.write(payload.toByteArray()) }
        }.isSuccess
        Toast.makeText(
            context,
            if (ok) backupExportSuccess else backupExportFailed,
            Toast.LENGTH_SHORT
        ).show()
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val raw = runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
        }.getOrNull()
        if (raw.isNullOrBlank()) {
            Toast.makeText(context, backupImportFailed, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val ok = onImportStateJson(raw)
        Toast.makeText(
            context,
            if (ok) backupImportSuccess else backupImportFailed,
            Toast.LENGTH_SHORT
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.screen_settings),
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(
                onClick = { showAboutDialog = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.settings_info_desc)
                )
            }
        }

        SettingsCard(title = stringResource(R.string.settings_general_title)) {
            CompactSelectField(
                label = stringResource(R.string.settings_language_title),
                selectedLabel = languageLabel(settings.language),
                options = listOf(
                    languageLabel(LanguagePreference.SYSTEM) to LanguagePreference.SYSTEM,
                    languageLabel(LanguagePreference.GERMAN) to LanguagePreference.GERMAN,
                    languageLabel(LanguagePreference.ENGLISH) to LanguagePreference.ENGLISH
                ),
                onSelected = { onSettingsChanged(settings.copy(language = it)) }
            )

            CompactSelectField(
                label = stringResource(R.string.settings_date_format_title),
                selectedLabel = dateFormatLabel(settings.dateFormat),
                options = listOf(
                    dateFormatLabel(DateFormatPreference.DMY_DOT) to DateFormatPreference.DMY_DOT,
                    dateFormatLabel(DateFormatPreference.YMD_DASH) to DateFormatPreference.YMD_DASH,
                    dateFormatLabel(DateFormatPreference.MDY_SLASH) to DateFormatPreference.MDY_SLASH
                ),
                onSelected = { onSettingsChanged(settings.copy(dateFormat = it)) }
            )

            CompactSelectField(
                label = stringResource(R.string.settings_currency_title),
                selectedLabel = currencyLabel(settings.currency),
                options = listOf(
                    currencyLabel(CurrencyPreference.EUR) to CurrencyPreference.EUR,
                    currencyLabel(CurrencyPreference.USD) to CurrencyPreference.USD
                ),
                onSelected = { onSettingsChanged(settings.copy(currency = it)) }
            )

            CompactSelectField(
                label = stringResource(R.string.settings_start_tab_title),
                selectedLabel = startTabLabel(settings.startTab),
                options = listOf(
                    startTabLabel(AppStartTab.OVERVIEW) to AppStartTab.OVERVIEW,
                    startTabLabel(AppStartTab.BOOKINGS) to AppStartTab.BOOKINGS,
                    startTabLabel(AppStartTab.REPORTS) to AppStartTab.REPORTS,
                    startTabLabel(AppStartTab.SYNC) to AppStartTab.SYNC,
                    startTabLabel(AppStartTab.SETTINGS) to AppStartTab.SETTINGS
                ),
                onSelected = { onSettingsChanged(settings.copy(startTab = it)) }
            )

            CompactSelectField(
                label = stringResource(R.string.settings_default_export_format_title),
                selectedLabel = exportFormatLabel(settings.defaultExportFormat),
                options = listOf(
                    exportFormatLabel(ExportFormatPreference.PDF) to ExportFormatPreference.PDF,
                    exportFormatLabel(ExportFormatPreference.XLSX) to ExportFormatPreference.XLSX,
                    exportFormatLabel(ExportFormatPreference.CSV) to ExportFormatPreference.CSV
                ),
                onSelected = { onSettingsChanged(settings.copy(defaultExportFormat = it)) }
            )
        }

        SettingsCard(title = stringResource(R.string.settings_booking_behavior_title)) {
            SwitchRow(
                title = stringResource(R.string.settings_keep_date_title),
                subtitle = stringResource(R.string.settings_keep_date_desc),
                checked = settings.keepDateAfterSave,
                onCheckedChange = { onSettingsChanged(settings.copy(keepDateAfterSave = it)) }
            )

            SwitchRow(
                title = stringResource(R.string.settings_sort_desc_title),
                subtitle = stringResource(R.string.settings_sort_desc_desc),
                checked = settings.bookingsSortNewestFirst,
                onCheckedChange = { onSettingsChanged(settings.copy(bookingsSortNewestFirst = it)) }
            )

            SwitchRow(
                title = stringResource(R.string.settings_category_suggestions_title),
                subtitle = stringResource(R.string.settings_category_suggestions_desc),
                checked = settings.categorySuggestionsEnabled,
                onCheckedChange = { onSettingsChanged(settings.copy(categorySuggestionsEnabled = it)) }
            )
        }

        SettingsCard(title = stringResource(R.string.settings_appearance_title)) {
            CompactSelectField(
                label = stringResource(R.string.settings_font_size_title),
                selectedLabel = fontSizeLabel(settings.fontSizeMode),
                options = listOf(
                    fontSizeLabel(FontSizeMode.COMPACT) to FontSizeMode.COMPACT,
                    fontSizeLabel(FontSizeMode.NORMAL) to FontSizeMode.NORMAL,
                    fontSizeLabel(FontSizeMode.LARGE) to FontSizeMode.LARGE
                ),
                onSelected = { onSettingsChanged(settings.copy(fontSizeMode = it)) }
            )
            CompactSelectField(
                label = stringResource(R.string.settings_navigation_animation_title),
                selectedLabel = navigationAnimationLabel(settings.navigationAnimationStyle),
                options = listOf(
                    navigationAnimationLabel(NavigationAnimationStyle.SLIDE) to NavigationAnimationStyle.SLIDE,
                    navigationAnimationLabel(NavigationAnimationStyle.FADE) to NavigationAnimationStyle.FADE,
                    navigationAnimationLabel(NavigationAnimationStyle.ZOOM) to NavigationAnimationStyle.ZOOM,
                    navigationAnimationLabel(NavigationAnimationStyle.POP) to NavigationAnimationStyle.POP,
                    navigationAnimationLabel(NavigationAnimationStyle.ROTATE) to NavigationAnimationStyle.ROTATE,
                    navigationAnimationLabel(NavigationAnimationStyle.NONE) to NavigationAnimationStyle.NONE
                ),
                onSelected = { onSettingsChanged(settings.copy(navigationAnimationStyle = it)) }
            )

            SwitchRow(
                title = stringResource(R.string.settings_reduce_animations_title),
                subtitle = stringResource(R.string.settings_reduce_animations_desc),
                checked = settings.reduceAnimations,
                onCheckedChange = { onSettingsChanged(settings.copy(reduceAnimations = it)) }
            )
        }

        SettingsCard(title = stringResource(R.string.settings_security_title)) {
            SwitchRow(
                title = stringResource(R.string.settings_app_lock_title),
                subtitle = stringResource(R.string.settings_app_lock_desc),
                checked = settings.appLockEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        if (!canUseSystemAuthentication(context)) {
                            Toast.makeText(context, appLockUnavailableText, Toast.LENGTH_SHORT).show()
                        } else {
                            onSettingsChanged(settings.copy(appLockEnabled = true))
                        }
                    } else {
                        requestSystemAuthentication(
                            context = context,
                            title = appLockAuthTitle,
                            subtitle = appLockAuthSubtitle,
                            onAuthenticated = {
                                onSettingsChanged(settings.copy(appLockEnabled = false))
                            },
                            onUnavailable = {
                                Toast.makeText(context, appLockUnavailableText, Toast.LENGTH_SHORT).show()
                            },
                            onError = { message ->
                                if (message.isNotBlank()) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            )
        }

        SettingsCard(title = stringResource(R.string.settings_backup_title)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { exportLauncher.launch("finanz-tracker-backup.json") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.settings_backup_export),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                OutlinedButton(
                    onClick = { importLauncher.launch(arrayOf("application/json", "text/plain")) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.settings_backup_import),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = stringResource(R.string.settings_backup_hint, state.bookings.size, state.users.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.app_info_title)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (appIconBitmap != null) {
                            Image(
                                bitmap = appIconBitmap,
                                contentDescription = stringResource(R.string.app_name),
                                modifier = Modifier.size(42.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = stringResource(R.string.app_info_version_label, appVersionName(context)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.app_info_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.app_info_developer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.app_info_actions_title),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Button(
                        onClick = { openEmail(context, "sichler.daniel@gmail.com") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.gmail_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(stringResource(R.string.app_info_open_email))
                        }
                    }
                    Button(
                        onClick = { openUri(context, "https://linktr.ee/darexsh") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.linktree_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(stringResource(R.string.app_info_open_github))
                        }
                    }
                    Button(
                        onClick = { openUri(context, "https://t.me/darexsh_bot") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.telegram_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(stringResource(R.string.app_info_open_telegram_bot))
                        }
                    }
                    Button(
                        onClick = { openUri(context, "https://github.com/Darexsh") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.github_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(stringResource(R.string.app_info_open_github_profile))
                        }
                    }
                    Button(
                        onClick = { openUri(context, "https://buymeacoffee.com/darexsh") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.buy_me_coffee_icon),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(stringResource(R.string.app_info_open_coffee))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        )
    }
}

private fun openUri(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    runCatching { context.startActivity(intent) }
}

private fun openEmail(context: android.content.Context, address: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$address")
    }
    runCatching { context.startActivity(intent) }
}

private fun appVersionName(context: android.content.Context): String {
    return runCatching {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        info.versionName ?: "1.0.0"
    }.getOrElse { "1.0.0" }
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

private fun canUseSystemAuthentication(context: android.content.Context): Boolean {
    val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    return BiometricManager.from(context).canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
}

private fun requestSystemAuthentication(
    context: android.content.Context,
    title: String,
    subtitle: String,
    onAuthenticated: () -> Unit,
    onUnavailable: () -> Unit,
    onError: (String) -> Unit
) {
    if (!canUseSystemAuthentication(context)) {
        onUnavailable()
        return
    }
    val activity = context as? AppCompatActivity ?: run {
        onUnavailable()
        return
    }
    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onAuthenticated()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onError(errString.toString())
            }
        }
    )
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()
    prompt.authenticate(promptInfo)
}

@Composable
private fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                content()
            }
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun <T> CompactSelectField(
    label: String,
    selectedLabel: String,
    options: List<Pair<String, T>>,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = label,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                options.forEach { (text, value) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelected(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun startTabLabel(tab: AppStartTab): String {
    return when (tab) {
        AppStartTab.OVERVIEW -> stringResource(R.string.tab_overview)
        AppStartTab.BOOKINGS -> stringResource(R.string.tab_bookings)
        AppStartTab.REPORTS -> stringResource(R.string.tab_reports)
        AppStartTab.SYNC -> stringResource(R.string.tab_sync)
        AppStartTab.SETTINGS -> stringResource(R.string.tab_settings)
    }
}

@Composable
private fun exportFormatLabel(format: ExportFormatPreference): String {
    return when (format) {
        ExportFormatPreference.PDF -> "PDF"
        ExportFormatPreference.XLSX -> "XLSX"
        ExportFormatPreference.CSV -> "CSV"
    }
}

@Composable
private fun languageLabel(language: LanguagePreference): String {
    return when (language) {
        LanguagePreference.SYSTEM -> stringResource(R.string.settings_language_system)
        LanguagePreference.GERMAN -> stringResource(R.string.settings_language_german)
        LanguagePreference.ENGLISH -> stringResource(R.string.settings_language_english)
    }
}

@Composable
private fun dateFormatLabel(format: DateFormatPreference): String {
    return when (format) {
        DateFormatPreference.DMY_DOT -> stringResource(R.string.settings_date_format_dmy)
        DateFormatPreference.YMD_DASH -> stringResource(R.string.settings_date_format_ymd)
        DateFormatPreference.MDY_SLASH -> stringResource(R.string.settings_date_format_mdy)
    }
}

@Composable
private fun currencyLabel(currency: CurrencyPreference): String {
    return when (currency) {
        CurrencyPreference.EUR -> "EUR (€)"
        CurrencyPreference.USD -> "USD ($)"
    }
}

@Composable
private fun fontSizeLabel(mode: FontSizeMode): String {
    return when (mode) {
        FontSizeMode.COMPACT -> stringResource(R.string.settings_font_size_compact)
        FontSizeMode.NORMAL -> stringResource(R.string.settings_font_size_normal)
        FontSizeMode.LARGE -> stringResource(R.string.settings_font_size_large)
    }
}

@Composable
private fun navigationAnimationLabel(style: NavigationAnimationStyle): String {
    return when (style) {
        NavigationAnimationStyle.SLIDE -> stringResource(R.string.settings_navigation_animation_slide)
        NavigationAnimationStyle.FADE -> stringResource(R.string.settings_navigation_animation_fade)
        NavigationAnimationStyle.ZOOM -> stringResource(R.string.settings_navigation_animation_zoom)
        NavigationAnimationStyle.POP -> stringResource(R.string.settings_navigation_animation_pop)
        NavigationAnimationStyle.ROTATE -> stringResource(R.string.settings_navigation_animation_rotate)
        NavigationAnimationStyle.NONE -> stringResource(R.string.settings_navigation_animation_none)
    }
}
