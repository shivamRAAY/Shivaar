package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    viewModel: ShivaarViewModel,
    onStartFocusSession: (TaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val tomorrowTasks by viewModel.tomorrowTasks.collectAsState()
    val completionHistory by viewModel.completionHistory.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val completedCount by viewModel.completedTasksCount.collectAsState()
    val activeCount by viewModel.activeTasksCount.collectAsState()
    val completedMins by viewModel.completedMinutes.collectAsState()
    val totalPlannedMins by viewModel.totalPlannedMinutes.collectAsState()
    val progressPercentage by viewModel.progressPercentage.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val onboardingData by viewModel.onboardingData.collectAsState()

    val roadmapMonths by viewModel.roadmapMonths.collectAsState()

    var selectedTaskForNotes by remember { mutableStateOf<TaskItem?>(null) }
    var noteInputText by remember { mutableStateOf("") }
    var taskToReschedule by remember { mutableStateOf<TaskItem?>(null) }
    var taskToSkip by remember { mutableStateOf<TaskItem?>(null) }
    var skipReasonText by remember { mutableStateOf("") }
    var showHistorySection by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("today_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Apple-calm Personalized Greeting Header ---
        item {
            TodayGreetingHeader(
                userProfile = userProfile,
                completedCount = completedCount,
                totalCount = tasks.size,
                completedMinutes = completedMins,
                budgetMinutes = userProfile.dailyBudgetMinutes,
                progressPercentage = progressPercentage,
                syncStatus = syncStatus
            )
        }

        // --- 1.5. Onboarding / Path Personalization Banner or Active Milestone Banner ---
        if (!onboardingData.isCompleted) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_prompt_banner"),
                    shape = RoundedCornerShape(16.dp),
                    color = IndigoContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Personalize My Shivaar",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OnIndigoContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Step ${onboardingData.currentStep} of 7 • Tailor roadmaps, constraints, AI diagnostic & path choice to your stage.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalTextSecondary
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppDestination.ONBOARDING) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            modifier = Modifier.testTag("btn_resume_onboarding")
                        ) {
                            Text(
                                text = if (onboardingData.currentStep > 1) "Resume" else "Start",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        } else {
            val activeMonth = roadmapMonths.firstOrNull { it.isCurrentMonth } ?: roadmapMonths.firstOrNull()
            if (activeMonth != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("active_milestone_execution_banner"),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.35f)),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SuccessEmerald)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Active Milestone • M${activeMonth.monthNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = activeMonth.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Target: ${activeMonth.projectMilestone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CharcoalTextSecondary,
                                    maxLines = 1
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = { viewModel.generateTasksForMonth(activeMonth) },
                                    modifier = Modifier.testTag("btn_quick_sync_milestone_tasks")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAlarm,
                                        contentDescription = "Sync Milestone Tasks",
                                        tint = IndigoPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.navigateTo(AppDestination.ROADMAP) },
                                    modifier = Modifier.testTag("btn_view_active_roadmap")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                        contentDescription = "View Roadmap",
                                        tint = CharcoalTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Executive Overall Information Dark Card (Reference 1) ---
        item {
            val totalAllTimeCompleted = maxOf(completedCount, completionHistory.size)
            OverallInformationCard(
                userProfile = userProfile,
                completedCount = completedCount,
                activeCount = activeCount,
                totalTasks = tasks.size,
                totalAllTimeCompleted = totalAllTimeCompleted,
                projectsCount = projects.size
            )
        }

        // --- 3. Productivity Ring Gauge & Weekly Rate (Reference 1 & 3) ---
        item {
            ProductivityOverviewSection(
                progressPercentage = progressPercentage,
                completedCount = completedCount,
                totalCount = tasks.size,
                inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS },
                completionHistory = completionHistory
            )
        }

        // --- 4. Today's Hourly Timetable (Reference 3) ---
        item {
            TodayTimetableCard(
                tasks = tasks,
                onStartFocus = { task ->
                    viewModel.markTaskInProgress(task.id)
                    onStartFocusSession(task)
                }
            )
        }

        // --- 2. Focus Session Banner ---
        val activeOrPendingTask = tasks.firstOrNull { it.status == TaskStatus.IN_PROGRESS }
            ?: tasks.firstOrNull { it.status == TaskStatus.PAUSED }
            ?: tasks.firstOrNull { it.status == TaskStatus.PENDING }

        if (activeOrPendingTask != null) {
            item {
                ActiveFocusPromptCard(
                    task = activeOrPendingTask,
                    onStartFocus = {
                        viewModel.markTaskInProgress(activeOrPendingTask.id)
                        onStartFocusSession(activeOrPendingTask)
                    }
                )
            }
        }

        // --- 3. Today's Tasks Section Header ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Study Plan",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "AKTU syllabus aligned • C, Python, DSA foundations",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (completedCount == tasks.size && tasks.isNotEmpty()) SuccessEmeraldContainer else IndigoContainer
                ) {
                    Text(
                        text = "$completedCount / ${tasks.size} Done",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (completedCount == tasks.size && tasks.isNotEmpty()) SuccessEmerald else IndigoPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- Task List Items ---
        items(tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onStart = {
                    viewModel.markTaskInProgress(task.id)
                    onStartFocusSession(task)
                },
                onPause = {
                    viewModel.markTaskPaused(task.id)
                },
                onComplete = {
                    viewModel.markTaskComplete(task.id)
                },
                onAddNotes = {
                    selectedTaskForNotes = task
                    noteInputText = task.userNotes
                },
                onSkip = {
                    taskToSkip = task
                    skipReasonText = ""
                },
                onRestore = {
                    viewModel.restoreTask(task.id)
                },
                onReschedule = {
                    taskToReschedule = task
                }
            )
        }

        // --- 4. Adaptive Recovery Plan Notice ---
        val incompleteCount = tasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.PAUSED }
        if (incompleteCount > 0) {
            item {
                AdaptiveScheduleNotice(
                    remainingTasks = incompleteCount,
                    onRescheduleAllTomorrow = {
                        tasks.filter { it.status == TaskStatus.PENDING || it.status == TaskStatus.PAUSED }.forEach {
                            viewModel.rescheduleTask(it.id, toTomorrow = true)
                        }
                    }
                )
            }
        }

        // --- 5. Tomorrow's Preview Section ---
        item {
            TomorrowPreviewSection(
                tomorrowTasks = tomorrowTasks,
                onMoveToToday = { taskId ->
                    viewModel.moveTomorrowTaskToToday(taskId)
                }
            )
        }

        // --- 6. Completion History Log ---
        if (completionHistory.isNotEmpty()) {
            item {
                CompletionHistorySection(
                    history = completionHistory,
                    isExpanded = showHistorySection,
                    onToggleExpand = { showHistorySection = !showHistorySection }
                )
            }
        }

        // Extra spacing at bottom for navigation bar
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Dialog for adding notes
    selectedTaskForNotes?.let { task ->
        AlertDialog(
            onDismissRequest = { selectedTaskForNotes = null },
            title = { Text(text = "Learning Notes: ${task.title}") },
            text = {
                Column {
                    Text(
                        text = "Document key formulas, code patterns, or edge cases:",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_notes_input"),
                        placeholder = { Text("E.g., In C arrays, *(arr + i) is equal to arr[i]...") },
                        minLines = 3,
                        maxLines = 6
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            viewModel.updateTaskNotes(task.id, noteInputText)
                            selectedTaskForNotes = null
                        },
                        modifier = Modifier.testTag("save_note_only_button")
                    ) {
                        Text("Save Note")
                    }

                    Button(
                        onClick = {
                            viewModel.markTaskComplete(task.id, notes = noteInputText)
                            selectedTaskForNotes = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                        modifier = Modifier.testTag("save_notes_button")
                    ) {
                        Text("Save & Complete")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTaskForNotes = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog for skipping task
    taskToSkip?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToSkip = null },
            title = { Text("Skip Task for Today") },
            text = {
                Column {
                    Text(
                        text = "Skipping '${task.title}'? Shivaar will record the reason and recalculate your daily focus balance without breaking your streak.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = skipReasonText,
                        onValueChange = { skipReasonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("skip_reason_input"),
                        label = { Text("Reason (Optional)") },
                        placeholder = { Text("E.g., Covered in college lecture, or focused on lab viva") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markTaskSkipped(task.id, skipReasonText)
                        taskToSkip = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CharcoalTextSecondary),
                    modifier = Modifier.testTag("confirm_skip_button")
                ) {
                    Text("Skip Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToSkip = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog for rescheduling
    taskToReschedule?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToReschedule = null },
            title = { Text("Reschedule Task") },
            text = {
                Text(
                    text = "Postpone '${task.title}' to tomorrow's schedule? Shivaar's adaptive engine will balance tomorrow's study load.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rescheduleTask(task.id, toTomorrow = true)
                        taskToReschedule = null
                    },
                    modifier = Modifier.testTag("confirm_reschedule_button")
                ) {
                    Text("Move to Tomorrow")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToReschedule = null }) {
                    Text("Keep Today")
                }
            }
        )
    }
}

@Composable
private fun TodayGreetingHeader(
    userProfile: UserProfile,
    completedCount: Int,
    totalCount: Int,
    completedMinutes: Int,
    budgetMinutes: Int,
    progressPercentage: Int,
    syncStatus: SyncStatus
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_greeting_card"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Good day, ${userProfile.name.substringBefore(" ")}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Semester 1 • ${userProfile.institute.substringBefore(" ")} (AKTU)",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                // Status Indicators Column
                Column(horizontalAlignment = Alignment.End) {
                    // Cloud Sync status pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (syncStatus) {
                            SyncStatus.Synced -> SuccessEmeraldContainer
                            SyncStatus.Syncing -> IndigoContainer
                            is SyncStatus.Error -> DangerRoseContainer
                            SyncStatus.Idle -> SurfaceCardSubtle
                        },
                        modifier = Modifier.testTag("cloud_sync_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (syncStatus) {
                                            SyncStatus.Synced -> SuccessEmerald
                                            SyncStatus.Syncing -> IndigoPrimary
                                            is SyncStatus.Error -> DangerRose
                                            SyncStatus.Idle -> CharcoalTextMuted
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (syncStatus) {
                                    SyncStatus.Synced -> "Cloud Synced"
                                    SyncStatus.Syncing -> "Syncing..."
                                    is SyncStatus.Error -> "Sync Offline"
                                    SyncStatus.Idle -> "Local Cache"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = when (syncStatus) {
                                    SyncStatus.Synced -> SuccessEmerald
                                    SyncStatus.Syncing -> IndigoPrimary
                                    is SyncStatus.Error -> DangerRose
                                    SyncStatus.Idle -> CharcoalTextSecondary
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Indian Timezone indicator pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceCardSubtle
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(SuccessEmerald)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "IST",
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Focus Metric Progress Bar
            val progressFraction = (progressPercentage / 100f).coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Study Time Budget",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalTextMuted
                    )
                    Text(
                        text = "${completedMinutes}m / ${budgetMinutes}m",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$progressPercentage% goal met",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (progressPercentage >= 100) SuccessEmerald else IndigoPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progressPercentage >= 100) SuccessEmerald else IndigoPrimary,
                trackColor = SurfaceCardSubtle,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Daily Focus: ${userProfile.currentLanguagePriority}",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActiveFocusPromptCard(
    task: TaskItem,
    onStartFocus: () -> Unit
) {
    val isInProgress = task.status == TaskStatus.IN_PROGRESS
    val isPaused = task.status == TaskStatus.PAUSED

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_focus_prompt_card"),
        shape = RoundedCornerShape(16.dp),
        color = if (isPaused) WarningAmberContainer.copy(alpha = 0.5f) else IndigoContainer,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPaused) WarningAmber.copy(alpha = 0.4f) else IndigoPrimary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isPaused) WarningAmber else IndigoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isInProgress) Icons.Default.PlayArrow else if (isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Start Focus Session",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isInProgress) "Current Focus Session" else if (isPaused) "Paused Session" else "Next Focus Session",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPaused) WarningAmber else IndigoPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = OnIndigoContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${task.durationMinutes} mins • ${task.category.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnIndigoContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Button(
                onClick = onStartFocus,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPaused) WarningAmber else IndigoPrimary
                ),
                modifier = Modifier.testTag("launch_focus_session_btn")
            ) {
                Text(
                    text = if (isInProgress) "Resume" else if (isPaused) "Resume" else "Start",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onComplete: () -> Unit,
    onAddNotes: () -> Unit,
    onSkip: () -> Unit,
    onRestore: () -> Unit,
    onReschedule: () -> Unit
) {
    val isDone = task.status == TaskStatus.COMPLETED
    val isInProgress = task.status == TaskStatus.IN_PROGRESS
    val isPaused = task.status == TaskStatus.PAUSED
    val isSkipped = task.status == TaskStatus.SKIPPED
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isDone || isSkipped) SurfaceCardSubtle else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
                isInProgress -> IndigoPrimary
                isPaused -> WarningAmber
                isDone -> SuccessEmerald.copy(alpha = 0.4f)
                else -> SurfaceCardBorder
            }
        ),
        shadowElevation = if (isDone || isSkipped) 0.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header: Category & Status Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (task.category) {
                            TaskCategory.C_PROGRAMMING -> IndigoContainer
                            TaskCategory.PYTHON -> SuccessEmeraldContainer
                            TaskCategory.DSA -> WarningAmberContainer
                            TaskCategory.ACADEMICS -> InfoBlueContainer
                            else -> SurfaceCardSubtle
                        }
                    ) {
                        Text(
                            text = task.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (task.category) {
                                TaskCategory.C_PROGRAMMING -> IndigoPrimary
                                TaskCategory.PYTHON -> SuccessEmerald
                                TaskCategory.DSA -> WarningAmber
                                TaskCategory.ACADEMICS -> InfoBlue
                                else -> CharcoalTextSecondary
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${task.durationMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalTextMuted
                    )
                }

                // Status Badge
                when (task.status) {
                    TaskStatus.COMPLETED -> {
                        Surface(
                            shape = CircleShape,
                            color = SuccessEmeraldContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SuccessEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SuccessEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    TaskStatus.IN_PROGRESS -> {
                        Surface(
                            shape = CircleShape,
                            color = IndigoContainer
                        ) {
                            Text(
                                text = "In Progress",
                                style = MaterialTheme.typography.labelSmall,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    TaskStatus.PAUSED -> {
                        Surface(
                            shape = CircleShape,
                            color = WarningAmberContainer
                        ) {
                            Text(
                                text = "Paused",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarningAmber,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    TaskStatus.SKIPPED -> {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceCardSubtle
                        ) {
                            Text(
                                text = "Skipped",
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalTextMuted,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    TaskStatus.PENDING -> {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceCardSubtle
                        ) {
                            Text(
                                text = "Pending",
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDone || isSkipped) CharcoalTextMuted else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Why it matters
            Text(
                text = "Why it matters: ${task.whyItMatters}",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Instructions:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = task.instructions,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Definition of Done:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = task.definitionOfDone,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    if (task.userNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceCardSubtle,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Your Note: ${task.userNotes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalTextSecondary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    if (!task.blockerReason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = WarningAmberContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Skip Reason: ${task.blockerReason}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalTextPrimary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Show Less" else "Details & Criteria",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when {
                        isDone -> {
                            OutlinedButton(
                                onClick = onAddNotes,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("notes_btn_${task.id}")
                            ) {
                                Text("Edit Note", style = MaterialTheme.typography.labelSmall)
                            }
                            TextButton(
                                onClick = onRestore,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("restore_task_btn_${task.id}")
                            ) {
                                Text("Restore", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        isSkipped -> {
                            Button(
                                onClick = onRestore,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("restore_task_btn_${task.id}")
                            ) {
                                Text("Restore to Today", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        else -> {
                            // Active / Pending / Paused Controls

                            // Reschedule
                            IconButton(
                                onClick = onReschedule,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("reschedule_btn_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = "Reschedule Task",
                                    tint = CharcoalTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Skip
                            IconButton(
                                onClick = onSkip,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("skip_task_btn_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Block,
                                    contentDescription = "Skip Task",
                                    tint = CharcoalTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Notes
                            IconButton(
                                onClick = onAddNotes,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("notes_btn_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.EditNote,
                                    contentDescription = "Add Notes",
                                    tint = CharcoalTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Pause Button (available when In Progress)
                            if (isInProgress) {
                                OutlinedButton(
                                    onClick = onPause,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("pause_task_btn_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pause", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            // Start / Resume Button
                            if (!isInProgress) {
                                OutlinedButton(
                                    onClick = onStart,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("start_task_btn_${task.id}")
                                ) {
                                    Text(
                                        text = if (isPaused) "Resume" else "Start",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            // Complete / Done Button
                            Button(
                                onClick = onComplete,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("complete_task_btn_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Done", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdaptiveScheduleNotice(
    remainingTasks: Int,
    onRescheduleAllTomorrow: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("adaptive_schedule_notice"),
        shape = RoundedCornerShape(16.dp),
        color = WarningAmberContainer.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Adaptive Scheduling Engine",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "You have $remainingTasks pending or paused tasks today. If college lectures or viva ran late, Shivaar can roll over tasks without overloading tomorrow's 2.5-hour budget.",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onRescheduleAllTomorrow,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CharcoalTextPrimary),
                modifier = Modifier.testTag("roll_over_tasks_btn")
            ) {
                Text(
                    text = "Balance & Reschedule to Tomorrow",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun TomorrowPreviewSection(
    tomorrowTasks: List<TaskItem>,
    onMoveToToday: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tomorrow's Preview (${tomorrowTasks.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Planned tasks ready to roll over into Today's schedule",
            style = MaterialTheme.typography.bodySmall,
            color = CharcoalTextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (tomorrowTasks.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCardSubtle
            ) {
                Text(
                    text = "No queued tasks for tomorrow. Focus on today's milestones!",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextMuted,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            tomorrowTasks.forEach { task ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${task.category.label} • ${task.durationMinutes} mins",
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalTextMuted
                            )
                        }

                        Button(
                            onClick = { onMoveToToday(task.id) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("move_to_today_btn_${task.id}")
                        ) {
                            Text("Move to Today", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletionHistorySection(
    history: List<TaskCompletionRecord>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("completion_history_section"),
        shape = RoundedCornerShape(16.dp),
        color = SuccessEmeraldContainer.copy(alpha = 0.25f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Completed History (${history.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (isExpanded) "Hide" else "Show Details",
                    style = MaterialTheme.typography.labelMedium,
                    color = SuccessEmerald,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    history.forEach { record ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = record.taskTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${record.actualMinutes}m spent • ${dateFormat.format(Date(record.completedAt))}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CharcoalTextMuted
                                    )
                                }

                                if (record.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Note: ${record.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CharcoalTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverallInformationCard(
    userProfile: UserProfile,
    completedCount: Int,
    activeCount: Int,
    totalTasks: Int,
    totalAllTimeCompleted: Int,
    projectsCount: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("overall_information_card"),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "AKTU B.Tech CSE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$totalAllTimeCompleted",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Tasks done for all time",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Month ${userProfile.currentMonth}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "Roadmap active (48 mo.)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$projectsCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Projects", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$activeCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                        Text("In Progress", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$completedCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                        Text("Completed", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductivityOverviewSection(
    progressPercentage: Int,
    completedCount: Int,
    totalCount: Int,
    inProgressCount: Int,
    completionHistory: List<TaskCompletionRecord>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("productivity_overview_section"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Tasks progress today (Circular Gauge)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tasks progress today",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier.size(86.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val sweep = (progressPercentage / 100f).coerceIn(0f, 1f) * 360f
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 8.dp.toPx()
                        drawArc(
                            color = Color(0xFFF1F5F9),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = if (progressPercentage >= 100) Color(0xFF10B981) else Color(0xFF6366F1),
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$progressPercentage%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$completedCount of $totalCount tasks completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "$inProgressCount tasks in progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = IndigoPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 2. Weekly Productivity Rate Bar Chart
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Productivity rate",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "This Week",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalTextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val calendar = remember { java.util.Calendar.getInstance() }
                // Calendar.DAY_OF_WEEK: Sunday=1, Monday=2..Saturday=7 -> Monday=0..Sunday=6
                val todayIndex = (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
                val days = listOf("M", "T", "W", "T", "F", "S", "S")

                val heights = remember(completionHistory, progressPercentage) {
                    val rates = FloatArray(7) { 0.12f }
                    val cal = java.util.Calendar.getInstance()
                    completionHistory.forEach { rec ->
                        cal.timeInMillis = rec.completedAt
                        val d = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
                        rates[d] = (rates[d] + (rec.actualMinutes.toFloat() / 150f)).coerceAtMost(1f)
                    }
                    rates[todayIndex] = (progressPercentage / 100f).coerceIn(0.15f, 1f)
                    rates.toList()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.forEachIndexed { idx, day ->
                        val h = heights[idx]
                        val isToday = idx == todayIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (isToday) Color(0xFF6366F1) else Color(0xFFC7D2FE))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = if (isToday) Color(0xFF6366F1) else CharcoalTextMuted,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Active 2.5h pace",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessEmerald,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TodayTimetableCard(
    tasks: List<TaskItem>,
    onStartFocus: (TaskItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_timetable_card"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Today's Schedule & Timetable",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (tasks.size > 2) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(if (isExpanded) "Collapse" else "View All (${tasks.size})", style = MaterialTheme.typography.labelSmall, color = IndigoPrimary)
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = IndigoPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (tasks.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCardSubtle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "All scheduled tasks completed for today. Tap 'Add Task' to plan your next study block.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                val defaultTimes = listOf(
                    "08:00 - 08:45",
                    "10:00 - 10:45",
                    "14:00 - 14:45",
                    "16:30 - 17:15",
                    "19:00 - 19:45",
                    "21:00 - 21:45"
                )

                val displayTasks = if (isExpanded) tasks else tasks.take(2)

                displayTasks.forEachIndexed { idx, task ->
                    val timeSlot = defaultTimes.getOrElse(idx) {
                        val startHour = 8 + (idx * 2)
                        "${String.format("%02d:00", startHour)} - ${String.format("%02d:%02d", startHour, task.durationMinutes.coerceAtMost(59))}"
                    }
                    val isFirstActive = idx == 0

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isFirstActive) IndigoContainer.copy(alpha = 0.6f) else SurfaceCardSubtle,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isFirstActive) IndigoPrimary.copy(alpha = 0.3f) else SurfaceCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isFirstActive) IndigoPrimary else CharcoalTextMuted)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "$timeSlot • ${task.durationMinutes}m",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFirstActive) IndigoPrimary else CharcoalTextMuted
                                    )
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            if (task.status != TaskStatus.COMPLETED) {
                                FilledTonalButton(
                                    onClick = { onStartFocus(task) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("start_timetable_slot_$idx")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = IndigoPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Focus", style = MaterialTheme.typography.labelSmall, color = IndigoPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
