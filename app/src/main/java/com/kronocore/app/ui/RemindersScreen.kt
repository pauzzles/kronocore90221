package com.kronocore.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronocore.app.data.Platform
import com.kronocore.app.data.Reminder
import com.kronocore.app.ui.theme.AccentGreen
import com.kronocore.app.ui.theme.AccentRed
import com.kronocore.app.ui.theme.AccentWhite
import com.kronocore.app.ui.theme.BackgroundDark
import com.kronocore.app.ui.theme.BorderSubtle
import com.kronocore.app.ui.theme.SurfaceCard
import com.kronocore.app.ui.theme.SurfaceDark
import com.kronocore.app.ui.theme.SurfaceElevated
import com.kronocore.app.ui.theme.TextMuted
import com.kronocore.app.ui.theme.TextPrimary
import com.kronocore.app.ui.theme.TextSecondary

@Composable
fun RemindersScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onAddReminderClick: () -> Unit,
    onEditReminderClick: (Reminder) -> Unit
) {
    val reminders by viewModel.filteredReminders.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isMasterEnabled by viewModel.isMasterEnabled.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = SurfaceElevated,
            title = {
                Text(
                    text = "Reset Schedules?",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "This will replace all custom schedules with the default TikTok and Instagram IST schedules.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text(text = "Reset", color = AccentRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = "Cancel", color = TextSecondary)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = "Schedules",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Reset Defaults",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Master Reminders Power Switch
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isMasterEnabled) SurfaceCard else Color(0xFF231618),
                border = BorderStroke(1.dp, if (isMasterEnabled) BorderSubtle else AccentRed.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(
                                    if (isMasterEnabled) AccentGreen else AccentRed,
                                    CircleShape
                                )
                        )
                        Column {
                            Text(
                                text = if (isMasterEnabled) "Master Switch: ON" else "Master Switch: OFF (All Paused)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isMasterEnabled) TextPrimary else AccentRed
                            )
                            Text(
                                text = if (isMasterEnabled) "All enabled reminders are active" else "All alarms are temporarily stopped",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isMasterEnabled,
                        onCheckedChange = { viewModel.setMasterEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BackgroundDark,
                            checkedTrackColor = AccentWhite,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTab(
                    label = "All",
                    isSelected = selectedFilter == null,
                    onClick = { viewModel.setPlatformFilter(null) },
                    modifier = Modifier.weight(1f)
                )
                FilterTab(
                    label = "TikTok",
                    isSelected = selectedFilter == Platform.TIKTOK,
                    onClick = { viewModel.setPlatformFilter(Platform.TIKTOK) },
                    modifier = Modifier.weight(1f)
                )
                FilterTab(
                    label = "Instagram",
                    isSelected = selectedFilter == Platform.INSTAGRAM,
                    onClick = { viewModel.setPlatformFilter(Platform.INSTAGRAM) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reminders found",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reminders, key = { it.id }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onToggle = { viewModel.toggleReminder(reminder) },
                            onClick = { onEditReminderClick(reminder) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddReminderClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = AccentWhite,
            contentColor = BackgroundDark,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add Reminder",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FilterTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) SurfaceElevated else SurfaceDark,
        border = BorderStroke(1.dp, if (isSelected) AccentWhite else BorderSubtle)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) TextPrimary else TextSecondary
            )
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (reminder.isEnabled) SurfaceCard else SurfaceDark,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlatformBadge(platform = reminder.platform)

                    Text(
                        text = reminder.getDaysSummary(),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = reminder.formattedTime,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (reminder.isEnabled) TextPrimary else TextMuted
                )

                Text(
                    text = "Alert at ${reminder.formattedNotificationTime} (${reminder.notifyMinutesBefore}m before)",
                    fontSize = 11.sp,
                    color = if (reminder.isEnabled) TextSecondary else TextMuted
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BackgroundDark,
                        checkedTrackColor = AccentWhite,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceElevated,
                        uncheckedBorderColor = BorderSubtle
                    )
                )
            }
        }
    }
}
