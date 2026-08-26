package com.kronocore.app

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kronocore.app.auth.AuthManager
import com.kronocore.app.data.Reminder
import com.kronocore.app.ui.AddEditReminderSheet
import com.kronocore.app.ui.AuthScreen
import com.kronocore.app.ui.HomeScreen
import com.kronocore.app.ui.MainViewModel
import com.kronocore.app.ui.RemindersScreen
import com.kronocore.app.ui.UpdateDialog
import com.kronocore.app.ui.theme.AppTheme
import com.kronocore.app.ui.theme.BackgroundDark
import com.kronocore.app.updater.AppUpdater
import com.kronocore.app.updater.UpdateInfo

enum class Screen {
    HOME,
    REMINDERS
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    val context = LocalContext.current
                    var isAuthenticated by remember {
                        mutableStateOf(AuthManager.isDeviceAuthenticated(context))
                    }

                    if (!isAuthenticated) {
                        AuthScreen(
                            onAuthenticated = {
                                isAuthenticated = true
                            }
                        )
                    } else {
                        AppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var showAddEditSheet by remember { mutableStateOf(false) }
    var reminderToEdit by remember { mutableStateOf<Reminder?>(null) }
    var availableUpdate by remember { mutableStateOf<UpdateInfo?>(null) }

    // Auto-check for updates on launch
    LaunchedEffect(Unit) {
        try {
            val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            val currentVersion = packageInfo.versionName ?: "1.0.0"
            val update = AppUpdater.checkForUpdate(currentVersion)
            if (update != null) {
                availableUpdate = update
            }
        } catch (e: Exception) {
            // Silently ignore network failures
        }
    }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToReminders = { currentScreen = Screen.REMINDERS },
                onAddReminderClick = {
                    reminderToEdit = null
                    showAddEditSheet = true
                }
            )
        }
        Screen.REMINDERS -> {
            RemindersScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.HOME },
                onAddReminderClick = {
                    reminderToEdit = null
                    showAddEditSheet = true
                },
                onEditReminderClick = { reminder ->
                    reminderToEdit = reminder
                    showAddEditSheet = true
                }
            )
        }
    }

    if (availableUpdate != null) {
        UpdateDialog(
            updateInfo = availableUpdate!!,
            onDismiss = { availableUpdate = null }
        )
    }

    if (showAddEditSheet) {
        AddEditReminderSheet(
            reminderToEdit = reminderToEdit,
            onDismiss = {
                showAddEditSheet = false
                reminderToEdit = null
            },
            onSave = { platform, hour, minute, daysOfWeek, notifyMinutesBefore ->
                if (reminderToEdit != null) {
                    val updated = reminderToEdit!!.copy(
                        platform = platform,
                        hour = hour,
                        minute = minute,
                        daysOfWeek = daysOfWeek,
                        notifyMinutesBefore = notifyMinutesBefore
                    )
                    viewModel.updateReminder(updated)
                } else {
                    viewModel.addReminder(
                        platform = platform,
                        hour = hour,
                        minute = minute,
                        daysOfWeek = daysOfWeek,
                        notifyMinutesBefore = notifyMinutesBefore
                    )
                }
            },
            onDelete = { reminder ->
                viewModel.deleteReminder(reminder)
            }
        )
    }
}
