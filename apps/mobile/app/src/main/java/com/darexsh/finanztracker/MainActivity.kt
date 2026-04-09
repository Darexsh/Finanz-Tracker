package com.darexsh.finanztracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.darexsh.finanztracker.domain.DefaultTrackerService
import com.darexsh.finanztracker.data.StateRepository
import com.darexsh.finanztracker.ui.AppViewModel
import com.darexsh.finanztracker.ui.FinanceTrackerApp
import com.darexsh.finanztracker.ui.theme.FinanzTrackerTheme

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<AppViewModel> {
        AppViewModel.Factory(
            DefaultTrackerService(StateRepository(applicationContext))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.state.collectAsState()
            FinanzTrackerTheme(fontSizeMode = state.appSettings.fontSizeMode) {
                FinanceTrackerApp(
                    state = state,
                    onSetActiveUser = viewModel::setActiveUser,
                    onAddUser = viewModel::addUser,
                    onRenameActiveUser = viewModel::renameActiveUser,
                    onDeleteActiveUser = viewModel::deleteActiveUser,
                    onAddBooking = viewModel::addBooking,
                    onUpdateBooking = viewModel::updateBooking,
                    onDeleteBooking = viewModel::deleteBooking,
                    onDeleteBookings = viewModel::deleteBookings,
                    onSetBookingTaxDeclaration = viewModel::setBookingTaxDeclaration,
                    onAddCustomCategory = viewModel::addCustomCategory,
                    onRenameCustomCategory = viewModel::renameCustomCategory,
                    onDeleteCustomCategory = viewModel::deleteCustomCategory,
                    onSyncFolderSelected = viewModel::setSyncFolderUri,
                    onSyncFolderCleared = viewModel::clearSyncFolderUri,
                    onUpdateAppSettings = viewModel::updateAppSettings,
                    onExportStateJson = viewModel::exportStateJson,
                    onImportStateJson = viewModel::importStateJson
                )
            }
        }
    }
}
