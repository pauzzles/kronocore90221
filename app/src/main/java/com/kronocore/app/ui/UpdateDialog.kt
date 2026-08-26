package com.kronocore.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kronocore.app.ui.theme.AccentRed
import com.kronocore.app.ui.theme.AccentWhite
import com.kronocore.app.ui.theme.BackgroundDark
import com.kronocore.app.ui.theme.BorderMedium
import com.kronocore.app.ui.theme.BorderSubtle
import com.kronocore.app.ui.theme.SurfaceElevated
import com.kronocore.app.ui.theme.TextMuted
import com.kronocore.app.ui.theme.TextPrimary
import com.kronocore.app.ui.theme.TextSecondary
import com.kronocore.app.updater.AppUpdater
import com.kronocore.app.updater.UpdateInfo
import kotlinx.coroutines.launch

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isDownloading) onDismiss() },
        containerColor = SurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        title = {
            Text(
                text = "Update Available (v${updateInfo.versionName})",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (updateInfo.releaseNotes.isNotBlank()) updateInfo.releaseNotes else "A new update is available with latest features and improvements.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                if (isDownloading) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = AccentWhite,
                        trackColor = BorderSubtle
                    )
                    Text(
                        text = "Downloading update: ${(downloadProgress * 100).toInt()}%",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = AccentRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isDownloading = true
                    errorMessage = null
                    scope.launch {
                        AppUpdater.downloadAndInstall(
                            context = context,
                            downloadUrl = updateInfo.downloadUrl,
                            onProgress = { progress ->
                                downloadProgress = progress
                            },
                            onError = { err ->
                                isDownloading = false
                                errorMessage = err
                            }
                        )
                    }
                },
                enabled = !isDownloading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentWhite,
                    contentColor = BackgroundDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isDownloading) "Downloading..." else "Update Now",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            if (!isDownloading) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Later", color = TextSecondary, fontSize = 13.sp)
                }
            }
        }
    )
}
