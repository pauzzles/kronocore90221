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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronocore.app.data.Platform
import com.kronocore.app.ui.theme.AccentWhite
import com.kronocore.app.ui.theme.BackgroundDark
import com.kronocore.app.ui.theme.BorderMedium
import com.kronocore.app.ui.theme.BorderSubtle
import com.kronocore.app.ui.theme.InstagramBadgeBg
import com.kronocore.app.ui.theme.InstagramBadgeBorder
import com.kronocore.app.ui.theme.InstagramBadgeText
import com.kronocore.app.ui.theme.SurfaceCard
import com.kronocore.app.ui.theme.SurfaceDark
import com.kronocore.app.ui.theme.SurfaceElevated
import com.kronocore.app.ui.theme.TextMuted
import com.kronocore.app.ui.theme.TextPrimary
import com.kronocore.app.ui.theme.TextSecondary
import com.kronocore.app.ui.theme.TikTokBadgeBg
import com.kronocore.app.ui.theme.TikTokBadgeBorder
import com.kronocore.app.ui.theme.TikTokBadgeText
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToReminders: () -> Unit,
    onAddReminderClick: () -> Unit
) {
    val nextPost by viewModel.nextPost.collectAsState()
    val upcomingPosts by viewModel.todayUpcomingPosts.collectAsState()
    val completedPosts by viewModel.todayCompletedPosts.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    var showCompletedSection by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KRONO CORE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = TextMuted
                        )
                        Text(
                            text = currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMM d · h:mm a", Locale.US)) + " IST",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToReminders() },
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "Manage Schedules",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Schedules",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            item {
                PermissionCard()
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "NEXT POST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, BorderMedium)
                    ) {
                        if (nextPost != null) {
                            val next = nextPost!!
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PlatformBadge(platform = next.platform)

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SurfaceElevated,
                                        border = BorderStroke(1.dp, BorderSubtle)
                                    ) {
                                        Text(
                                            text = next.countdownText,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AccentWhite,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = next.formattedTime,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Notifying ${next.notifyMinutesBefore} min before (${next.postDateTime.minusMinutes(next.notifyMinutesBefore.toLong()).format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))})",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "No upcoming posts scheduled",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Enable reminders or create custom schedules",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "TODAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )

                    if (upcomingPosts.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "All posts for today have passed",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceCard,
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column {
                                upcomingPosts.forEachIndexed { index, post ->
                                    TodayPostRow(post = post)
                                    if (index < upcomingPosts.size - 1) {
                                        HorizontalDivider(
                                            color = BorderSubtle,
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (completedPosts.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCompletedSection = !showCompletedSection }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMPLETED TODAY (${completedPosts.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextMuted
                            )
                            Text(
                                text = if (showCompletedSection) "Hide" else "Show",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        if (showCompletedSection) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Column {
                                    completedPosts.forEachIndexed { index, post ->
                                        TodayPostRow(post = post, isDimmed = true)
                                        if (index < completedPosts.size - 1) {
                                            HorizontalDivider(
                                                color = BorderSubtle,
                                                thickness = 1.dp,
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
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
fun TodayPostRow(
    post: TodayPostItem,
    isDimmed: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PlatformDot(platform = post.platform, isDimmed = isDimmed)
            Text(
                text = "${post.platform.displayName} · ${post.formattedTime}",
                fontSize = 14.sp,
                fontWeight = if (isDimmed) FontWeight.Normal else FontWeight.Medium,
                color = if (isDimmed) TextMuted else TextPrimary
            )
        }

        if (isDimmed) {
            Text(
                text = "Passed",
                fontSize = 12.sp,
                color = TextMuted
            )
        } else {
            Text(
                text = "-${post.notifyMinutesBefore}m alert",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun PlatformBadge(platform: Platform) {
    val (bgColor, borderColor, textColor) = when (platform) {
        Platform.TIKTOK -> Triple(TikTokBadgeBg, TikTokBadgeBorder, TikTokBadgeText)
        Platform.INSTAGRAM -> Triple(InstagramBadgeBg, InstagramBadgeBorder, InstagramBadgeText)
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = platform.displayName,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PlatformDot(platform: Platform, isDimmed: Boolean = false) {
    val color = if (isDimmed) {
        TextMuted
    } else {
        when (platform) {
            Platform.TIKTOK -> TikTokBadgeText
            Platform.INSTAGRAM -> InstagramBadgeText
        }
    }
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(color, CircleShape)
    )
}
