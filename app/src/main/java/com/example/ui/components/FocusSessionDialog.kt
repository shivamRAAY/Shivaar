package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.FocusSessionState
import com.example.ui.theme.*

@Composable
fun FocusSessionDialog(
    focusSession: FocusSessionState,
    onTogglePause: () -> Unit,
    onComplete: (String) -> Unit,
    onCancel: () -> Unit
) {
    val task = focusSession.task ?: return
    var completionNotes by remember { mutableStateOf("") }
    var showNotesInput by remember { mutableStateOf(false) }

    // Pulsing animation for active timer
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val minutes = focusSession.elapsedSeconds / 60
    val seconds = focusSession.elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("focus_session_modal"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close button & pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = IndigoContainer
                    ) {
                        Text(
                            text = "Deep Focus Mode",
                            style = MaterialTheme.typography.labelMedium,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CharcoalTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timer Display Circle
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(if (!focusSession.isPaused) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (focusSession.isPaused) SurfaceCardSubtle else IndigoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily.Monospace),
                            fontWeight = FontWeight.Bold,
                            color = if (focusSession.isPaused) CharcoalTextSecondary else IndigoPrimary,
                            fontSize = 36.sp
                        )
                        Text(
                            text = if (focusSession.isPaused) "Paused" else "Focusing...",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Task Details
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Goal: ${task.definitionOfDone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (showNotesInput) {
                    OutlinedTextField(
                        value = completionNotes,
                        onValueChange = { completionNotes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("focus_session_notes_input"),
                        placeholder = { Text("What did you learn? Any mistakes or insights?") },
                        minLines = 2,
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onTogglePause,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pause_resume_focus_btn")
                    ) {
                        Icon(
                            imageVector = if (focusSession.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (focusSession.isPaused) "Resume" else "Pause")
                    }

                    Button(
                        onClick = {
                            if (!showNotesInput) {
                                showNotesInput = true
                            } else {
                                onComplete(completionNotes)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("finish_focus_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showNotesInput) "Save & Finish" else "Complete Task")
                    }
                }
            }
        }
    }
}
