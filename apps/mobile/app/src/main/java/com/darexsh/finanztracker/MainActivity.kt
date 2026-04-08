package com.darexsh.finanztracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.darexsh.finanztracker.domain.DefaultTrackerService
import com.darexsh.finanztracker.data.StateRepository
import com.darexsh.finanztracker.ui.AppViewModel
import com.darexsh.finanztracker.ui.FinanceTrackerApp
import com.darexsh.finanztracker.ui.theme.FinanzTrackerTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<AppViewModel> {
        AppViewModel.Factory(
            DefaultTrackerService(StateRepository(applicationContext))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanzTrackerTheme {
                val state = viewModel.state.collectAsState().value
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
                    onSyncFolderSelected = viewModel::setSyncFolderUri,
                    onSyncFolderCleared = viewModel::clearSyncFolderUri
                )
            }
        }
    }
}
