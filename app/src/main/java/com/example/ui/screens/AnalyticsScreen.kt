package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ProblemStatus
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val completedCount by viewModel.completedTasksCount.collectAsState()
    val completedMins by viewModel.completedMinutes.collectAsState()
    val totalPlannedMins by viewModel.totalPlannedMinutes.collectAsState()
    val dsaProblems by viewModel.dsaProblems.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val completionHistory by viewModel.completionHistory.collectAsState()

    val streakDays = remember(completionHistory, completedCount) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
        }
        val daysSet = completionHistory.map { sdf.format(java.util.Date(it.completedAt)) }.toMutableSet()
        if (completedCount > 0) {
            daysSet.add(sdf.format(java.util.Date()))
        }
        if (daysSet.isEmpty()) {
            0
        } else {
            val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Asia/Kolkata"))
            var streak = 0
            val checkCal = cal.clone() as java.util.Calendar
            val todayStr = sdf.format(checkCal.time)
            if (!daysSet.contains(todayStr)) {
                checkCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            }
            while (daysSet.contains(sdf.format(checkCal.time))) {
                streak++
                checkCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            }
            maxOf(streak, if (completedCount > 0) 1 else 0)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Column {
                Text(
                    text = "Performance & Mastery Analytics",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Distinguishing actual engineering competence and consistency from vanity metrics.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }
        }

        // --- Consistency Streak Card ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = IndigoContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Daily Study Consistency",
                            style = MaterialTheme.typography.labelMedium,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (streakDays > 0) "$streakDays Day${if (streakDays != 1) "s" else ""} Active Streak" else "No active streak yet",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnIndigoContainer
                        )
                        Text(
                            text = if (streakDays > 0) "Asia/Kolkata timezone tracked" else "Complete today's tasks to begin your streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Streak",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // --- Study Time Planned vs Actual ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Today's Study Load",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Completed Time", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                            Text("${completedMins} mins", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SuccessEmerald)
                        }
                        Column {
                            Text("Daily Budget", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                            Text("${userProfile.dailyBudgetMinutes} mins", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = IndigoPrimary)
                        }
                        Column {
                            Text("Tasks Finished", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                            Text("$completedCount tasks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- Subject-Wise Syllabus Coverage ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Subject Syllabus Coverage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    subjects.forEach { sub ->
                        val ratio = if (sub.topics.isNotEmpty()) sub.completedTopics.size.toFloat() / sub.topics.size.toFloat() else 0f
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = sub.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(text = "${(ratio * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = IndigoPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = IndigoPrimary,
                                trackColor = SurfaceCardSubtle
                            )
                        }
                    }
                }
            }
        }

        // --- DSA Problem Stats ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DSA Problem Bank Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val solved = dsaProblems.count { it.status == ProblemStatus.SOLVED }
                    val attempted = dsaProblems.count { it.status == ProblemStatus.ATTEMPTED }
                    val total = dsaProblems.size

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$solved", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SuccessEmerald)
                            Text(text = "Solved", style = MaterialTheme.typography.bodySmall, color = CharcoalTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$attempted", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = WarningAmber)
                            Text(text = "Attempted", style = MaterialTheme.typography.bodySmall, color = CharcoalTextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$total", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = IndigoPrimary)
                            Text(text = "Logged", style = MaterialTheme.typography.bodySmall, color = CharcoalTextSecondary)
                        }
                    }
                }
            }
        }

        // --- Task Completion History Log ---
        if (completionHistory.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Recent Task Completion History (${completionHistory.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        completionHistory.take(10).forEach { record ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceCardSubtle
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = record.taskTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (record.notes.isNotBlank()) {
                                            Text(
                                                text = record.notes,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = CharcoalTextSecondary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${record.actualMinutes}m",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessEmerald
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
