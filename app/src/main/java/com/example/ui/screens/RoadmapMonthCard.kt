package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoadmapMonth
import com.example.data.model.SkillTrack
import com.example.ui.theme.*

@Composable
fun RoadmapMonthCard(
    month: RoadmapMonth,
    onToggleCompletion: () -> Unit,
    onSetActive: () -> Unit,
    onGenerateDailyTasks: () -> Unit,
    onSaveCustomNotes: (String) -> Unit = {},
    onToggleExercise: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(month.isCurrentMonth) }
    var taskGeneratedToast by remember { mutableStateOf(false) }
    var localNotes by remember(month.customNotes) { mutableStateOf(month.customNotes) }
    var isEditingNotes by remember { mutableStateOf(false) }

    val trackColor = Color(month.primaryTrack.colorHex)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .testTag("month_card_${month.monthNumber}"),
        shape = RoundedCornerShape(18.dp),
        color = if (month.isCompleted) SurfaceCardSubtle else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (month.isCurrentMonth) 2.dp else 1.dp,
            if (month.isCurrentMonth) IndigoPrimary else if (month.isCompleted) SuccessEmerald.copy(alpha = 0.5f) else SurfaceCardBorder
        ),
        shadowElevation = if (month.isCurrentMonth) 3.dp else 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // --- Card Top Header: Month badge, Semester, Track & Current Month Pill ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(trackColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M${month.monthNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = trackColor
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (month.phaseOrYearName.isNotBlank()) month.phaseOrYearName else "Year ${month.year} • Semester ${month.semester}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (month.semesterOrMilestoneName.isNotBlank()) "${month.semesterOrMilestoneName} • ${month.primaryTrack.label}" else month.primaryTrack.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = trackColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (month.isCurrentMonth) {
                        Surface(
                            shape = CircleShape,
                            color = IndigoContainer
                        ) {
                            Text(
                                text = "Active Focus",
                                style = MaterialTheme.typography.labelSmall,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Completion Checkbox
                    IconButton(
                        onClick = onToggleCompletion,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("month_checkbox_${month.monthNumber}")
                    ) {
                        Icon(
                            imageVector = if (month.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = if (month.isCompleted) "Mark Incomplete" else "Mark Complete",
                            tint = if (month.isCompleted) SuccessEmerald else CharcoalTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Month Title & Main Focus
            Text(
                text = month.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Main Focus: ${month.mainFocus}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = trackColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Key Metric Badges Row ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCardSubtle, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Study Hours", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                    Text("${month.weeklyAllocation.totalWeeklyHours}h/wk (${month.estimatedMonthlyHours}h/mo)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                }

                Column {
                    Text("DSA Target", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                    Text("Goal: ${month.cumulativeDsaGoal} Problems", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = IndigoPrimary)
                }

                Column {
                    Text("Est. Budget", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                    Text(month.budget.totalInrEstimate, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = SuccessEmerald)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick summary of Project & Academic milestone
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = SuccessEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Project: ${month.projectMilestone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = InfoBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Academics: ${month.collegeAcademicFocus}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // --- Comprehensive 15 Fields Expanded Section ---
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = SurfaceCardBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (month.learningObjectives.isNotEmpty()) {
                        Text(
                            text = "🎯 Key Learning Objectives:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                        month.learningObjectives.forEach { obj ->
                            Text(
                                text = "• $obj",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (month.practiceExercises.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Practice Exercises & Code Challenges:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val completedCount = month.completedExercises.size
                            Text(
                                text = "$completedCount/${month.practiceExercises.size} Done",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (completedCount == month.practiceExercises.size && completedCount > 0) SuccessEmerald else IndigoPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        month.practiceExercises.forEachIndexed { idx, ex ->
                            val isChecked = month.completedExercises.contains(idx)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleExercise(idx) }
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { onToggleExercise(idx) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = SuccessEmerald,
                                        uncheckedColor = CharcoalTextMuted
                                    ),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .testTag("exercise_check_${month.monthNumber}_$idx")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ex,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isChecked) CharcoalTextMuted else MaterialTheme.colorScheme.onSurface,
                                    textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (month.definitionOfDone.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IndigoContainer.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "🏁 Definition of Done (DoD):",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = month.definitionOfDone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (month.revisionSchedule.isNotBlank()) {
                        Text(
                            text = "📅 Spaced Recall: ${month.revisionSchedule}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // 1. Topics to Learn
                    Text(
                        text = "1. Topics & Core Syllabus:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    month.topics.forEach { topic ->
                        Text(
                            text = "• $topic",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Core CS Target
                    Text(
                        text = "2. Core Computer Science Target:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = month.coreCsTarget,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Weekly Hours Breakdown
                    Text(
                        text = "3. Weekly Time Allocation (${month.weeklyAllocation.totalWeeklyHours} Hours/Week):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "DSA: ${month.weeklyAllocation.dsaHours}h • Dev/Projects: ${month.weeklyAllocation.developmentHours}h • Core CS: ${month.weeklyAllocation.coreCsHours}h • Academics: ${month.weeklyAllocation.academicsHours}h",
                        style = MaterialTheme.typography.bodySmall,
                        color = IndigoPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Measurable Expected Output
                    Text(
                        text = "4. Measurable Expected Output:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = month.measurableOutput,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5. Readiness Checkpoint
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessEmeraldContainer.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "✅ Ready to Move On? (Checkpoint Criteria):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SuccessEmerald
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = month.readinessCheckpoint,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. INR Spending Breakdown
                    Text(
                        text = "6. Monthly Budget Details (INR):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Free Resources: ${month.budget.freeResources}\nLow-Cost Essentials: ${month.budget.lowCostEssentials}\nOptional Paid: ${month.budget.optionalPaid}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 7. Prerequisites & Revision
                    Text(
                        text = "7. Prerequisites & Revision Strategy:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Prerequisites: ${month.prerequisites.joinToString(", ")}\nRevision Rule: ${month.revisionRequirements}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 8. Failure Recovery Instructions
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = WarningAmberContainer.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⚠️ If You Fall Behind (Recovery Strategy):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = month.failureRecoveryInstructions,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (month.optionalActivities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Optional Activities: ${month.optionalActivities.joinToString("; ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Personal Notes & Custom Targets
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("milestone_notes_section_${month.monthNumber}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📝 Personal Notes & Target Links:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )
                                if (!isEditingNotes && localNotes.isNotBlank()) {
                                    IconButton(
                                        onClick = { isEditingNotes = true },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Notes",
                                            tint = IndigoPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            if (isEditingNotes || localNotes.isBlank()) {
                                OutlinedTextField(
                                    value = localNotes,
                                    onValueChange = { localNotes = it },
                                    placeholder = {
                                        Text(
                                            "Add personal insights, custom GitHub repo links, or study notes for this milestone...",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .testTag("milestone_notes_input_${month.monthNumber}"),
                                    maxLines = 4
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        onSaveCustomNotes(localNotes)
                                        isEditingNotes = false
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .testTag("btn_save_notes_${month.monthNumber}")
                                ) {
                                    Text("Save Notes", style = MaterialTheme.typography.labelSmall)
                                }
                            } else {
                                Text(
                                    text = localNotes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clickable { isEditingNotes = true }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- Footer Controls: Details toggle, Set Active, & Generate Today's Tasks ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "Hide Full Details" else "View All 15 Details",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!month.isCurrentMonth) {
                        OutlinedButton(
                            onClick = onSetActive,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("set_active_month_${month.monthNumber}")
                        ) {
                            Text("Make Active", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Button(
                        onClick = {
                            onGenerateDailyTasks()
                            taskGeneratedToast = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("generate_tasks_btn_${month.monthNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAlarm,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (taskGeneratedToast) "Tasks Added!" else "Send to Today",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
