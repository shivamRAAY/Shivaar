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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicsScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newSubjectCode by remember { mutableStateOf("") }
    var newSubjectCredits by remember { mutableStateOf("4") }
    var newSubjectExamDate by remember { mutableStateOf("Dec 15, 2026") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSubjectDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Subject") },
                text = { Text("Add Subject") },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("btn_add_subject")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("academics_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Header ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
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
            }

            // --- Overall Attendance Summary Card with What-If Simulator ---
            item {
                OverallAttendanceSummary(subjects = subjects)
            }

            // --- SGPA Target Calculator & Credit Summary ---
            item {
                SgpaTargetCalculatorCard(subjects = subjects)
            }

            // --- Section Title ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enrolled University Courses (${subjects.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // --- Subject Cards ---
            items(subjects, key = { it.id }) { subject ->
                SubjectCard(
                    subject = subject,
                    onToggleTopic = { topic -> viewModel.toggleSubjectTopic(subject.id, topic) },
                    onLogAttendance = { attended -> viewModel.logAttendance(subject.id, attended) },
                    onGenerateExamTask = {
                        viewModel.createExamPrepTask(subject)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Created 45m Exam Prep task for ${subject.code} in Today's list!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // Add Subject Dialog
    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Enroll New University Subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newSubjectName,
                        onValueChange = { newSubjectName = it },
                        label = { Text("Subject Name") },
                        placeholder = { Text("e.g. Operating Systems") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_subject_name"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSubjectCode,
                        onValueChange = { newSubjectCode = it },
                        label = { Text("Subject Code") },
                        placeholder = { Text("e.g. KCS-401") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_subject_code"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSubjectCredits,
                        onValueChange = { newSubjectCredits = it },
                        label = { Text("Credits") },
                        placeholder = { Text("4") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_subject_credits"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newSubjectExamDate,
                        onValueChange = { newSubjectExamDate = it },
                        label = { Text("Next Exam Date") },
                        placeholder = { Text("Dec 15, 2026") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_subject_exam_date"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val credits = newSubjectCredits.toIntOrNull() ?: 4
                        val name = newSubjectName.ifBlank { "University Elective" }
                        val code = newSubjectCode.ifBlank { "KCS-E01" }
                        viewModel.addNewSubject(name, code, credits, newSubjectExamDate)
                        showAddSubjectDialog = false
                        newSubjectName = ""
                        newSubjectCode = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Subject $name added successfully!")
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_add_subject")
                ) {
                    Text("Add Subject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun OverallAttendanceSummary(subjects: List<SubjectItem>) {
    val totalAttended = subjects.sumOf { it.attendedClasses }
    val totalClasses = subjects.sumOf { it.totalClasses }
    val overallPercentage = if (totalClasses > 0) (totalAttended.toFloat() / totalClasses.toFloat()) * 100f else 0f
    val isSafe = overallPercentage >= 75f

    var showSimulator by remember { mutableStateOf(false) }
    var simulatedMissed by remember { mutableStateOf(0) }
    var simulatedAttended by remember { mutableStateOf(0) }

    val projectedAttended = (totalAttended + simulatedAttended).coerceAtLeast(0)
    val projectedTotal = (totalClasses + simulatedAttended + simulatedMissed).coerceAtLeast(1)
    val projectedPercentage = (projectedAttended.toFloat() / projectedTotal.toFloat()) * 100f
    val isProjectedSafe = projectedPercentage >= 75f

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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(10.dp))

            // What-If Simulator Toggle
            TextButton(
                onClick = { showSimulator = !showSimulator },
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = if (showSimulator) Icons.Default.ExpandLess else Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = IndigoPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showSimulator) "Hide Attendance Simulator" else "Open Attendance 'What-If' Simulator",
                    style = MaterialTheme.typography.labelMedium,
                    color = IndigoPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(visible = showSimulator) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Simulate Attendance Projections",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulate Next Attended Classes: $simulatedAttended",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilledTonalIconButton(
                                onClick = { simulatedAttended = (simulatedAttended - 1).coerceAtLeast(0) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold)
                            }
                            FilledTonalIconButton(
                                onClick = { simulatedAttended++ },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Simulate Next Missed Classes: $simulatedMissed",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilledTonalIconButton(
                                onClick = { simulatedMissed = (simulatedMissed - 1).coerceAtLeast(0) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold)
                            }
                            FilledTonalIconButton(
                                onClick = { simulatedMissed++ },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Projected: ${String.format("%.1f", projectedPercentage)}% ($projectedAttended/$projectedTotal)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isProjectedSafe) SuccessEmerald else DangerRose
                        )
                        if (simulatedAttended > 0 || simulatedMissed > 0) {
                            TextButton(
                                onClick = {
                                    simulatedAttended = 0
                                    simulatedMissed = 0
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Reset", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SgpaTargetCalculatorCard(subjects: List<SubjectItem>) {
    val totalCredits = subjects.sumOf { it.credits }
    var targetSgpa by remember { mutableStateOf("8.5") }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sgpa_target_calculator"),
        shape = RoundedCornerShape(16.dp),
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
                Column {
                    Text(
                        text = "AKTU Credit & SGPA Calculator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Total Semester Credits: $totalCredits • 5 Units / Course",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IndigoContainer
                ) {
                    Text(
                        text = "Target: $targetSgpa SGPA",
                        style = MaterialTheme.typography.labelMedium,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("7.5 (Good)", "8.0 (Very Good)", "8.5 (Distinction)", "9.0+ (Top)").forEach { target ->
                    val cleanValue = target.substringBefore(" ")
                    val isSelected = targetSgpa == cleanValue
                    FilterChip(
                        selected = isSelected,
                        onClick = { targetSgpa = cleanValue },
                        label = { Text(target, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectItem,
    onToggleTopic: (String) -> Unit,
    onLogAttendance: (Boolean) -> Unit,
    onGenerateExamTask: () -> Unit
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
                        text = "${subject.code} • ${subject.credits} Credits • Exam: ${subject.nextExamDate}",
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

            Spacer(modifier = Modifier.height(12.dp))

            // Exam Prep Generator & Expand Checklist Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onGenerateExamTask,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("exam_prep_btn_${subject.code}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Generate Exam Task",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exam Prep Task (45m)", style = MaterialTheme.typography.labelSmall)
                }

                TextButton(
                    onClick = { expandedTopics = !expandedTopics },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (expandedTopics) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (expandedTopics) "Hide Units" else "View Units",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary
                    )
                }
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
