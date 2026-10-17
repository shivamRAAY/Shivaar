package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubjectItem
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@Composable
fun AcademicsScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("academics_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Column {
                Text(
                    text = "AKTU Academics Hub",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Lloyd Institute of Engineering & Technology • Semester 1 CSE Core",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }
        }

        // --- Overall Attendance Summary Card ---
        item {
            OverallAttendanceSummary(subjects = subjects)
        }

        // --- Subject Cards ---
        items(subjects, key = { it.id }) { subject ->
            SubjectCard(
                subject = subject,
                onToggleTopic = { topic -> viewModel.toggleSubjectTopic(subject.id, topic) },
                onLogAttendance = { attended -> viewModel.logAttendance(subject.id, attended) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OverallAttendanceSummary(subjects: List<SubjectItem>) {
    val totalAttended = subjects.sumOf { it.attendedClasses }
    val totalClasses = subjects.sumOf { it.totalClasses }
    val overallPercentage = if (totalClasses > 0) (totalAttended.toFloat() / totalClasses.toFloat()) * 100f else 0f
    val isSafe = overallPercentage >= 75f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("overall_attendance_summary"),
        shape = RoundedCornerShape(18.dp),
        color = if (isSafe) SuccessEmeraldContainer else WarningAmberContainer,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSafe) SuccessEmerald.copy(alpha = 0.3f) else WarningAmber.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "75% Mandatory Attendance Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isSafe) {
                        "Compliant: You have attended $totalAttended of $totalClasses lectures."
                    } else {
                        "Warning: Attendance is below AKTU 75% threshold! Attend upcoming classes."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }

            Surface(
                shape = CircleShape,
                color = if (isSafe) SuccessEmerald else WarningAmber
            ) {
                Text(
                    text = "${String.format("%.1f", overallPercentage)}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectItem,
    onToggleTopic: (String) -> Unit,
    onLogAttendance: (Boolean) -> Unit
) {
    var expandedTopics by remember { mutableStateOf(false) }

    val percentage = if (subject.totalClasses > 0) {
        (subject.attendedClasses.toFloat() / subject.totalClasses.toFloat()) * 100f
    } else 0f

    val safeToMiss = if (subject.totalClasses > 0) {
        val maxCanMiss = (subject.attendedClasses / 0.75f).toInt() - subject.totalClasses
        maxOf(0, maxCanMiss)
    } else 0

    val needToAttend = if (percentage < 75f && subject.totalClasses > 0) {
        val needed = kotlin.math.ceil((0.75f * subject.totalClasses - subject.attendedClasses) / 0.25f).toInt()
        maxOf(1, needed)
    } else 0

    val syllabusProgress = if (subject.topics.isNotEmpty()) {
        (subject.completedTopics.size.toFloat() / subject.topics.size.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_card_${subject.code}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name & Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${subject.code} • ${subject.credits} Credits • ${subject.nextExamDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (percentage >= 75f) SuccessEmeraldContainer else DangerRoseContainer
                ) {
                    Text(
                        text = "${String.format("%.1f", percentage)}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (percentage >= 75f) SuccessEmerald else DangerRose,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Attendance Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCardSubtle, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Attended: ${subject.attendedClasses}/${subject.totalClasses} classes",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (percentage >= 75f) {
                            "Safe to miss: $safeToMiss more classes"
                        } else {
                            "Deficit: Attend next $needToAttend classes"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (percentage >= 75f) SuccessEmerald else DangerRose
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onLogAttendance(true) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("present_btn_${subject.code}")
                    ) {
                        Text("+ Present", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = { onLogAttendance(false) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("absent_btn_${subject.code}")
                    ) {
                        Text("+ Missed", style = MaterialTheme.typography.labelSmall, color = DangerRose)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Syllabus Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Syllabus Coverage (${subject.completedTopics.size}/${subject.topics.size} Units)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(syllabusProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = IndigoPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { syllabusProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = IndigoPrimary,
                trackColor = SurfaceCardSubtle
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Expand Topics Checklist
            TextButton(
                onClick = { expandedTopics = !expandedTopics },
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = if (expandedTopics) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = IndigoPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (expandedTopics) "Hide Topics Checklist" else "View Unit Topics Checklist",
                    style = MaterialTheme.typography.labelMedium,
                    color = IndigoPrimary
                )
            }

            AnimatedVisibility(visible = expandedTopics) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    subject.topics.forEach { topic ->
                        val isDone = subject.completedTopics.contains(topic)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onToggleTopic(topic) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { onToggleTopic(topic) },
                                colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = topic,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDone) CharcoalTextMuted else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (subject.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Professor / Exam Note: ${subject.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary
                        )
                    }
                }
            }
        }
    }
}
