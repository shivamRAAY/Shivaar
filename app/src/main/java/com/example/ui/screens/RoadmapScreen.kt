package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.RoadmapMonth
import com.example.data.model.SkillTrack
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@Composable
fun RoadmapScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val months by viewModel.roadmapMonths.collectAsState()
    val gates by viewModel.prerequisiteGates.collectAsState()

    var selectedViewTab by remember { mutableStateOf(0) } // 0: 48-Month Timeline, 1: Master Poster, 2: Prerequisite Gates
    var searchQuery by remember { mutableStateOf("") }
    var selectedYearFilter by remember { mutableStateOf(0) } // 0: All, 1..4
    var selectedSemesterFilter by remember { mutableStateOf(0) } // 0: All, 1..8
    var selectedTrackFilter by remember { mutableStateOf<SkillTrack?>(null) } // null: All

    val filteredMonths = remember(months, searchQuery, selectedYearFilter, selectedSemesterFilter, selectedTrackFilter) {
        months.filter { m ->
            val matchesYear = selectedYearFilter == 0 || m.year == selectedYearFilter
            val matchesSem = selectedSemesterFilter == 0 || m.semester == selectedSemesterFilter
            val matchesTrack = selectedTrackFilter == null || m.primaryTrack == selectedTrackFilter
            val matchesSearch = searchQuery.isBlank() ||
                    m.title.contains(searchQuery, ignoreCase = true) ||
                    m.mainFocus.contains(searchQuery, ignoreCase = true) ||
                    m.topics.any { it.contains(searchQuery, ignoreCase = true) } ||
                    m.projectMilestone.contains(searchQuery, ignoreCase = true)
            matchesYear && matchesSem && matchesTrack && matchesSearch
        }
    }

    val completedMonthsCount = months.count { it.isCompleted }
    val currentMonthObj = months.find { it.isCurrentMonth } ?: months.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("roadmap_screen")
    ) {
        // --- Top Primary Tab Switcher ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            TabRow(
                selectedTabIndex = selectedViewTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = IndigoPrimary
            ) {
                Tab(
                    selected = selectedViewTab == 0,
                    onClick = { selectedViewTab = 0 },
                    text = {
                        Text(
                            text = "48-Month Timeline",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selectedViewTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_48_months")
                )
                Tab(
                    selected = selectedViewTab == 1,
                    onClick = { selectedViewTab = 1 },
                    text = {
                        Text(
                            text = "Master Poster",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selectedViewTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_master_poster")
                )
                Tab(
                    selected = selectedViewTab == 2,
                    onClick = { selectedViewTab = 2 },
                    text = {
                        Text(
                            text = "8 Prerequisite Gates",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selectedViewTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_gates")
                )
                Tab(
                    selected = selectedViewTab == 3,
                    onClick = { selectedViewTab = 3 },
                    text = {
                        Text(
                            text = "Explore Library",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selectedViewTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_explore_roadmaps")
                )
            }
        }

        when (selectedViewTab) {
            0 -> {
                // --- 48-Month Continuous Interactive Timeline ---
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("roadmap_timeline_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Overview Summary
                    item {
                        RoadmapProgressHeader(
                            completedCount = completedMonthsCount,
                            totalCount = months.size,
                            currentMonth = currentMonthObj
                        )
                    }

                    // Search & Filter Controls
                    item {
                        RoadmapFilterControls(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            selectedYear = selectedYearFilter,
                            onYearChange = { selectedYearFilter = it },
                            selectedTrack = selectedTrackFilter,
                            onTrackChange = { selectedTrackFilter = it }
                        )
                    }

                    // Monthly Cards (Months 1 to 48)
                    items(filteredMonths, key = { it.id }) { month ->
                        RoadmapMonthCard(
                            month = month,
                            onToggleCompletion = { viewModel.toggleMonthCompletion(month.id) },
                            onSetActive = { viewModel.setActiveMonth(month.monthNumber) },
                            onGenerateDailyTasks = { viewModel.generateTasksForMonth(month) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            1 -> {
                // --- Dedicated Master Poster View ---
                RoadmapMasterPoster(viewModel = viewModel)
            }

            2 -> {
                // --- 8 Prerequisite Assessment Gates ---
                RoadmapGatesView(viewModel = viewModel)
            }

            3 -> {
                // --- Public Roadmap Library ---
                ExploreRoadmapsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun RoadmapProgressHeader(
    completedCount: Int,
    totalCount: Int,
    currentMonth: RoadmapMonth?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("roadmap_progress_header"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "4-Year Continuous CSE Roadmap",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "48 connected months from C foundations to applied AI & placement mastery",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = IndigoContainer
                ) {
                    Text(
                        text = "$completedCount / $totalCount Months",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            val progress = (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = IndigoPrimary,
                trackColor = SurfaceCardSubtle
            )

            if (currentMonth != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCardSubtle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Active Now: Month ${currentMonth.monthNumber} • ${currentMonth.title}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoadmapFilterControls(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    selectedTrack: SkillTrack?,
    onTrackChange: (SkillTrack?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search 48 months (e.g. pointers, dynamic programming, react, RAG)...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CharcoalTextMuted)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("roadmap_search_input")
        )

        // Year Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedYear == 0,
                    onClick = { onYearChange(0) },
                    label = { Text("All 48 Months") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndigoPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_year_all")
                )
            }
            (1..4).forEach { yr ->
                item {
                    FilterChip(
                        selected = selectedYear == yr,
                        onClick = { onYearChange(yr) },
                        label = { Text("Year $yr ${if (yr == 1) "• Active" else ""}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_year_$yr")
                    )
                }
            }
        }

        // Skill Track Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedTrack == null,
                    onClick = { onTrackChange(null) },
                    label = { Text("All Tracks") }
                )
            }
            SkillTrack.entries.forEach { track ->
                item {
                    val isSelected = selectedTrack == track
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTrackChange(if (isSelected) null else track) },
                        label = { Text(track.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(track.colorHex),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
