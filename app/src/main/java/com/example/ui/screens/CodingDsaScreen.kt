package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DsaProblem
import com.example.data.model.ProblemStatus
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodingDsaScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val problems by viewModel.dsaProblems.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedLangFilter by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    var selectedProblemForSnippet by remember { mutableStateOf<DsaProblem?>(null) }
    var selectedProblemForNotes by remember { mutableStateOf<DsaProblem?>(null) }
    var noteText by remember { mutableStateOf("") }

    var showAddProblemDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newPlatform by remember { mutableStateOf("LeetCode") }
    var newTopic by remember { mutableStateOf("Arrays") }
    var newLanguage by remember { mutableStateOf("C++") }
    var newDifficulty by remember { mutableStateOf("Medium") }
    var newUrl by remember { mutableStateOf("") }

    val filteredProblems = remember(problems, selectedLangFilter, selectedStatusFilter, searchQuery) {
        problems.filter { prob ->
            val matchLang = selectedLangFilter == "All" || prob.language.contains(selectedLangFilter, ignoreCase = true)
            val matchStatus = when (selectedStatusFilter) {
                "Solved" -> prob.status == ProblemStatus.SOLVED
                "Attempted" -> prob.status == ProblemStatus.ATTEMPTED
                "Revise" -> prob.status == ProblemStatus.REVISE
                "Not Started" -> prob.status == ProblemStatus.NOT_STARTED
                else -> true
            }
            val matchSearch = searchQuery.isBlank() ||
                    prob.title.contains(searchQuery, ignoreCase = true) ||
                    prob.topic.contains(searchQuery, ignoreCase = true) ||
                    prob.platform.contains(searchQuery, ignoreCase = true)
            matchLang && matchStatus && matchSearch
        }
    }

    val solvedCount = problems.count { it.status == ProblemStatus.SOLVED }
    val attemptedCount = problems.count { it.status == ProblemStatus.ATTEMPTED }
    val reviseCount = problems.count { it.status == ProblemStatus.REVISE }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddProblemDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Problem") },
                text = { Text("Add Problem") },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("btn_add_dsa_problem")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("coding_dsa_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Header ---
            item {
                Column {
                    Text(
                        text = "Coding & DSA Progression",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Master C pointers & memory, complete AKTU Python, and scale to C++ DSA for placements.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }
            }

            // --- Stats Summary Bar ---
            item {
                DsaStatsSummaryCard(
                    total = problems.size,
                    solved = solvedCount,
                    attempted = attemptedCount,
                    revise = reviseCount
                )
            }

            // --- Progression Stage Banner ---
            item {
                ProgressionStageBanner()
            }

            // --- Search Bar ---
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search problems by title, topic, platform...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_dsa_search")
                )
            }

            // --- Filter Chips Row (Language & Status) ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Language filters
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val langFilters = listOf("All", "C", "C++", "Python", "Java")
                        items(langFilters) { filter ->
                            val isSelected = selectedLangFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedLangFilter = filter },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndigoPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_lang_$filter")
                            )
                        }
                    }

                    // Status filters
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val statusFilters = listOf("All", "Solved", "Attempted", "Revise", "Not Started")
                        items(statusFilters) { status ->
                            val isSelected = selectedStatusFilter == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStatusFilter = status },
                                label = { Text(status) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (status) {
                                        "Solved" -> SuccessEmerald
                                        "Attempted" -> WarningAmber
                                        "Revise" -> DangerRose
                                        else -> IndigoPrimary
                                    },
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("filter_status_$status")
                            )
                        }
                    }
                }
            }

            // --- List Header ---
            item {
                Text(
                    text = "Problems (${filteredProblems.size} of ${problems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // --- Problem Cards ---
            items(filteredProblems, key = { it.id }) { problem ->
                DsaProblemCard(
                    problem = problem,
                    onViewCode = { selectedProblemForSnippet = problem },
                    onEditNotes = {
                        selectedProblemForNotes = problem
                        noteText = problem.mistakeNote
                    },
                    onToggleStatus = {
                        val nextStatus = when (problem.status) {
                            ProblemStatus.NOT_STARTED -> ProblemStatus.ATTEMPTED
                            ProblemStatus.ATTEMPTED -> ProblemStatus.SOLVED
                            ProblemStatus.SOLVED -> ProblemStatus.REVISE
                            ProblemStatus.REVISE -> ProblemStatus.NOT_STARTED
                        }
                        viewModel.updateDsaProblem(problem.id, nextStatus, problem.mistakeNote, problem.solutionSnippet)
                    },
                    onScheduleDailyTask = {
                        viewModel.createDsaDailyPracticeTask(problem)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Added 35m practice task for ${problem.title} to Today's schedule!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    onOpenLink = {
                        if (problem.url.isNotBlank()) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(problem.url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Ignored if browser not installed
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // Add Problem Dialog
    if (showAddProblemDialog) {
        AlertDialog(
            onDismissRequest = { showAddProblemDialog = false },
            title = { Text("Add DSA Problem") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Problem Title") },
                        placeholder = { Text("e.g. Trapping Rain Water") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_dsa_title"),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newPlatform,
                            onValueChange = { newPlatform = it },
                            label = { Text("Platform") },
                            placeholder = { Text("LeetCode") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_dsa_platform"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newDifficulty,
                            onValueChange = { newDifficulty = it },
                            label = { Text("Difficulty") },
                            placeholder = { Text("Medium") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_dsa_difficulty"),
                            singleLine = true
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTopic,
                            onValueChange = { newTopic = it },
                            label = { Text("Topic") },
                            placeholder = { Text("Arrays / Two Pointers") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_dsa_topic"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newLanguage,
                            onValueChange = { newLanguage = it },
                            label = { Text("Language") },
                            placeholder = { Text("C++") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_dsa_language"),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text("Problem URL") },
                        placeholder = { Text("https://leetcode.com/problems/...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_dsa_url"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = newTitle.ifBlank { "Untitled Problem" }
                        viewModel.addNewDsaProblem(
                            title = title,
                            platform = newPlatform.ifBlank { "LeetCode" },
                            topic = newTopic.ifBlank { "DSA" },
                            language = newLanguage.ifBlank { "C++" },
                            difficulty = newDifficulty.ifBlank { "Medium" },
                            url = newUrl
                        )
                        showAddProblemDialog = false
                        newTitle = ""
                        newUrl = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Added $title to DSA collection!")
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_add_dsa")
                ) {
                    Text("Add Problem")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProblemDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Snippet Dialog
    selectedProblemForSnippet?.let { prob ->
        AlertDialog(
            onDismissRequest = { selectedProblemForSnippet = null },
            title = { Text(text = "${prob.title} (${prob.language})") },
            text = {
                Column {
                    Text(
                        text = "Clean, optimal solution code:",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalTextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        Text(
                            text = prob.solutionSnippet.ifBlank { "// Solution implementation verified in ${prob.language}\n// Time Complexity: Optimal\n// Space Complexity: O(1)" },
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedProblemForSnippet = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Edit Notes Dialog
    selectedProblemForNotes?.let { prob ->
        AlertDialog(
            onDismissRequest = { selectedProblemForNotes = null },
            title = { Text("Mistake Log: ${prob.title}") },
            text = {
                Column {
                    Text(
                        text = "What was tricky? Off-by-one errors, edge cases (empty array, negative nums)?",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Log mistake & lesson learned...") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateDsaProblem(prob.id, prob.status, noteText, prob.solutionSnippet)
                    selectedProblemForNotes = null
                }) {
                    Text("Save Lesson")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProblemForNotes = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DsaStatsSummaryCard(
    total: Int,
    solved: Int,
    attempted: Int,
    revise: Int
) {
    val solvePercentage = if (total > 0) (solved.toFloat() / total.toFloat()) * 100f else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dsa_stats_summary_card"),
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
                        text = "Placement DSA Readiness",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Striver A2Z & LeetCode Curated Progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SuccessEmeraldContainer
                ) {
                    Text(
                        text = "${String.format("%.0f", solvePercentage)}% Solved",
                        style = MaterialTheme.typography.labelMedium,
                        color = SuccessEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatPill(count = solved, label = "Solved", color = SuccessEmerald)
                StatPill(count = attempted, label = "Attempted", color = WarningAmber)
                StatPill(count = revise, label = "Revise", color = DangerRose)
                StatPill(count = total - (solved + attempted + revise), label = "To Do", color = CharcoalTextSecondary)
            }
        }
    }
}

@Composable
private fun StatPill(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CharcoalTextMuted
        )
    }
}

@Composable
private fun ProgressionStageBanner() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progression_stage_banner"),
        shape = RoundedCornerShape(16.dp),
        color = IndigoContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Focus Track",
                    style = MaterialTheme.typography.labelSmall,
                    color = IndigoPrimary,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = CircleShape,
                    color = SuccessEmeraldContainer
                ) {
                    Text(
                        text = "Step 1 of 4: C Memory",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessEmerald,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "1. C Pointers & Memory ➔ 2. AKTU Python ➔ 3. C++ STL ➔ 4. LeetCode Mediums",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnIndigoContainer
            )
        }
    }
}

@Composable
private fun DsaProblemCard(
    problem: DsaProblem,
    onViewCode: () -> Unit,
    onEditNotes: () -> Unit,
    onToggleStatus: () -> Unit,
    onScheduleDailyTask: () -> Unit,
    onOpenLink: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dsa_problem_${problem.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceCardSubtle
                    ) {
                        Text(
                            text = problem.platform,
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (problem.difficulty) {
                            "Easy" -> SuccessEmeraldContainer
                            "Medium" -> WarningAmberContainer
                            else -> DangerRoseContainer
                        }
                    ) {
                        Text(
                            text = problem.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (problem.difficulty) {
                                "Easy" -> SuccessEmerald
                                "Medium" -> WarningAmber
                                else -> DangerRose
                            },
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Interactive Status Pill
                Surface(
                    shape = CircleShape,
                    color = when (problem.status) {
                        ProblemStatus.SOLVED -> SuccessEmeraldContainer
                        ProblemStatus.ATTEMPTED -> WarningAmberContainer
                        ProblemStatus.REVISE -> DangerRoseContainer
                        ProblemStatus.NOT_STARTED -> SurfaceCardSubtle
                    },
                    modifier = Modifier.clickable { onToggleStatus() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (problem.status) {
                                ProblemStatus.SOLVED -> Icons.Default.CheckCircle
                                ProblemStatus.ATTEMPTED -> Icons.Default.PlayCircleOutline
                                ProblemStatus.REVISE -> Icons.Default.Repeat
                                ProblemStatus.NOT_STARTED -> Icons.Outlined.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = when (problem.status) {
                                ProblemStatus.SOLVED -> SuccessEmerald
                                ProblemStatus.ATTEMPTED -> WarningAmber
                                ProblemStatus.REVISE -> DangerRose
                                ProblemStatus.NOT_STARTED -> CharcoalTextMuted
                            },
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = problem.status.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (problem.status) {
                                ProblemStatus.SOLVED -> SuccessEmerald
                                ProblemStatus.ATTEMPTED -> WarningAmber
                                ProblemStatus.REVISE -> DangerRose
                                ProblemStatus.NOT_STARTED -> CharcoalTextSecondary
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = problem.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Topic: ${problem.topic} • Language: ${problem.language}",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary
            )

            if (problem.mistakeNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceCardSubtle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lesson: ${problem.mistakeNote}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onScheduleDailyTask,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_schedule_dsa_task_${problem.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Schedule Practice Task",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Practice Today (35m)", style = MaterialTheme.typography.labelSmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (problem.url.isNotBlank()) {
                        IconButton(
                            onClick = onOpenLink,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Link",
                                tint = IndigoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onEditNotes,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EditNote,
                            contentDescription = "Edit Notes",
                            tint = CharcoalTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onViewCode,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("View Code", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
