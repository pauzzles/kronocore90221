package com.kronocore.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronocore.app.data.Platform
import com.kronocore.app.data.Reminder
import com.kronocore.app.ui.theme.AccentRed
import com.kronocore.app.ui.theme.AccentWhite
import com.kronocore.app.ui.theme.BackgroundDark
import com.kronocore.app.ui.theme.BorderMedium
import com.kronocore.app.ui.theme.BorderSubtle
import com.kronocore.app.ui.theme.SurfaceCard
import com.kronocore.app.ui.theme.SurfaceDark
import com.kronocore.app.ui.theme.SurfaceElevated
import com.kronocore.app.ui.theme.TextMuted
import com.kronocore.app.ui.theme.TextPrimary
import com.kronocore.app.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditReminderSheet(
    reminderToEdit: Reminder? = null,
    onDismiss: () -> Unit,
    onSave: (platform: Platform, hour: Int, minute: Int, daysOfWeek: Set<Int>, notifyMinutesBefore: Int) -> Unit,
    onDelete: ((Reminder) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPlatform by remember { mutableStateOf(reminderToEdit?.platform ?: Platform.TIKTOK) }

    val initialHour = reminderToEdit?.hour ?: 19
    val initialMinute = reminderToEdit?.minute ?: 30
    var hour12 by remember {
        val h = if (initialHour % 12 == 0) 12 else initialHour % 12
        mutableIntStateOf(h)
    }
    var minute by remember { mutableIntStateOf(initialMinute) }
    var isAm by remember { mutableStateOf(initialHour < 12) }

    var selectedDays by remember {
        mutableStateOf(reminderToEdit?.daysOfWeek ?: setOf(1, 2, 3, 4, 5, 6, 7))
    }

    var notifyMinutesBefore by remember {
        mutableIntStateOf(reminderToEdit?.notifyMinutesBefore ?: 10)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        tonalElevation = 0.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(BorderMedium, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (reminderToEdit != null) "Edit Schedule" else "New Schedule",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                if (reminderToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(reminderToEdit)
                            onDismiss()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = AccentRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Platform Switcher
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "PLATFORM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    color = TextMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Platform.entries.forEach { platform ->
                        val isSelected = selectedPlatform == platform
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable { selectedPlatform = platform },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SurfaceElevated else SurfaceCard,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AccentWhite else BorderSubtle
                            )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = platform.displayName,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Time Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "POSTING TIME (IST)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    color = TextMuted
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeNumberPicker(
                            value = hour12,
                            range = 1..12,
                            onValueChange = { hour12 = it }
                        )

                        Text(
                            text = ":",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        TimeNumberPicker(
                            value = minute,
                            range = 0..59,
                            step = 5,
                            format = "%02d",
                            onValueChange = { minute = it }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(width = 44.dp, height = 28.dp)
                                    .clickable { isAm = true },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isAm) AccentWhite else SurfaceElevated,
                                border = BorderStroke(1.dp, if (isAm) AccentWhite else BorderSubtle)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "AM",
                                        color = if (isAm) Color(0xFF090A0C) else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .size(width = 44.dp, height = 28.dp)
                                    .clickable { isAm = false },
                                shape = RoundedCornerShape(6.dp),
                                color = if (!isAm) AccentWhite else SurfaceElevated,
                                border = BorderStroke(1.dp, if (!isAm) AccentWhite else BorderSubtle)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "PM",
                                        color = if (!isAm) Color(0xFF090A0C) else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Weekdays Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REPEAT DAYS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                        color = TextMuted
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Every day",
                            fontSize = 11.sp,
                            color = if (selectedDays.size == 7) AccentWhite else TextSecondary,
                            modifier = Modifier
                                .clickable { selectedDays = setOf(1, 2, 3, 4, 5, 6, 7) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Text(
                            text = "·",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Weekdays",
                            fontSize = 11.sp,
                            color = if (selectedDays == setOf(1, 2, 3, 4, 5)) AccentWhite else TextSecondary,
                            modifier = Modifier
                                .clickable { selectedDays = setOf(1, 2, 3, 4, 5) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                val dayLabels = listOf("Mon" to 1, "Tue" to 2, "Wed" to 3, "Thu" to 4, "Fri" to 5, "Sat" to 6, "Sun" to 7)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dayLabels.forEach { (label, dayValue) ->
                        val isSelected = selectedDays.contains(dayValue)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable {
                                    selectedDays = if (isSelected) {
                                        if (selectedDays.size > 1) selectedDays - dayValue else selectedDays
                                    } else {
                                        selectedDays + dayValue
                                    }
                                },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) SurfaceElevated else SurfaceDark,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AccentWhite else BorderSubtle
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    color = if (isSelected) TextPrimary else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Notification Offset
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "NOTIFY BEFORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    color = TextMuted
                )

                val presetOffsets = listOf(5, 10, 15, 30, 60)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetOffsets.forEach { offset ->
                        val isSelected = notifyMinutesBefore == offset
                        Surface(
                            modifier = Modifier
                                .clickable { notifyMinutesBefore = offset },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) SurfaceElevated else SurfaceCard,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AccentWhite else BorderSubtle
                            )
                        ) {
                            Text(
                                text = "${offset} min",
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderMedium),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Text(text = "Cancel", fontSize = 13.sp, color = TextSecondary)
                }

                Button(
                    onClick = {
                        val finalHour = when {
                            isAm && hour12 == 12 -> 0
                            !isAm && hour12 != 12 -> hour12 + 12
                            else -> hour12
                        }
                        onSave(
                            selectedPlatform,
                            finalHour,
                            minute,
                            selectedDays.ifEmpty { setOf(1, 2, 3, 4, 5, 6, 7) },
                            notifyMinutesBefore
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF090A0C)
                    )
                ) {
                    Text(
                        text = if (reminderToEdit != null) "Update Schedule" else "Save Schedule",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF090A0C)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeNumberPicker(
    value: Int,
    range: IntRange,
    step: Int = 1,
    format: String = "%d",
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "▲",
            fontSize = 9.sp,
            color = TextSecondary,
            modifier = Modifier
                .clickable {
                    val next = value + step
                    if (next > range.last) onValueChange(range.first) else onValueChange(next)
                }
                .padding(2.dp)
        )

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = SurfaceElevated,
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.size(width = 48.dp, height = 40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = String.format(Locale.US, format, value),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Text(
            text = "▼",
            fontSize = 9.sp,
            color = TextSecondary,
            modifier = Modifier
                .clickable {
                    val prev = value - step
                    if (prev < range.first) onValueChange(range.last) else onValueChange(prev)
                }
                .padding(2.dp)
        )
    }
}
