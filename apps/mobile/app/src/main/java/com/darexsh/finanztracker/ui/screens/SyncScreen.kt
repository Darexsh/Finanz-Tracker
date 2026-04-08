package com.darexsh.finanztracker.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.darexsh.finanztracker.R

@Composable
fun SyncScreen(
    syncFolderUri: String?,
    onSyncFolderSelected: (String) -> Unit,
    onSyncFolderCleared: () -> Unit
) {
    val context = LocalContext.current

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        val granted = runCatching {
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            true
        }.getOrElse { false }

        if (!granted) {
            Toast.makeText(
                context,
                context.getString(R.string.sync_folder_permission_failed),
                Toast.LENGTH_LONG
            ).show()
            return@rememberLauncherForActivityResult
        }

        onSyncFolderSelected(uri.toString())
        Toast.makeText(
            context,
            context.getString(R.string.sync_folder_saved_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    val internalStorageLabel = stringResource(R.string.sync_volume_internal)
    val folderDisplayName = syncFolderUri?.let { toReadableFolderPath(it, internalStorageLabel) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.screen_sync), style = MaterialTheme.typography.headlineSmall)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.sync_info_1))
                Text(
                    text = stringResource(R.string.sync_info_2),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (syncFolderUri.isNullOrBlank() || folderDisplayName.isNullOrBlank()) {
                    Text(stringResource(R.string.sync_folder_not_set))
                } else {
                    Text(stringResource(R.string.sync_folder_set_label))
                    Text(folderDisplayName, style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { folderPickerLauncher.launch(null) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (syncFolderUri.isNullOrBlank()) {
                                stringResource(R.string.sync_folder_select_button)
                            } else {
                                stringResource(R.string.sync_folder_change_button)
                            }
                        )
                    }

                    Button(
                        onClick = {
                            if (!syncFolderUri.isNullOrBlank()) {
                                runCatching {
                                    context.contentResolver.releasePersistableUriPermission(
                                        Uri.parse(syncFolderUri),
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    )
                                }
                            }
                            onSyncFolderCleared()
                            Toast.makeText(
                                context,
                                context.getString(R.string.sync_folder_cleared_toast),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !syncFolderUri.isNullOrBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(stringResource(R.string.sync_folder_clear_button))
                    }
                }
            }
        }
    }
}

private fun toReadableFolderPath(uriText: String, internalStorageLabel: String): String {
    val uri = runCatching { Uri.parse(uriText) }.getOrNull() ?: return uriText

    val treeId = runCatching { DocumentsContract.getTreeDocumentId(uri) }.getOrNull()
    if (treeId.isNullOrBlank()) {
        return Uri.decode(uriText)
    }

    val volume = treeId.substringBefore(':', treeId)
    val relativePath = treeId.substringAfter(':', "")

    val volumeLabel = when (volume.lowercase()) {
        "primary" -> internalStorageLabel
        else -> volume
    }

    return if (relativePath.isBlank()) volumeLabel else "$volumeLabel/$relativePath"
}
