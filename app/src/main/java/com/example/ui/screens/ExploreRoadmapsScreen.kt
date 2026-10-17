package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PublicRoadmapsData
import com.example.data.model.Difficulty
import com.example.data.model.PublicRoadmap
import com.example.data.model.PublicRoadmapMilestone
import com.example.data.model.RoadmapResourceVerification
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreRoadmapsScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedLevel by remember { mutableStateOf<Difficulty?>(null) }
    var expandedRoadmapId by remember { mutableStateOf<String?>(null) }

    val savedIds by viewModel.savedRoadmapIds.collectAsState()
    val followedIds by viewModel.followedRoadmapIds.collectAsState()

    val categories = listOf(
        "All",
        "Programming Languages",
        "Computer Science",
        "Software Engineering",
        "Artificial Intelligence",
        "Infrastructure & Security",
        "Architecture"
    )

    val filteredRoadmaps = remember(searchQuery, selectedCategory, selectedLevel) {
        PublicRoadmapsData.searchRoadmaps(
            query = searchQuery,
            category = if (selectedCategory == "All") null else selectedCategory,
            level = selectedLevel
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_roadmaps_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Explore Public Roadmaps",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Structured curricula for CS, software engineering & AI. Browse freely without mandatory onboarding.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(IndigoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Public Library",
                            tint = IndigoPrimary
                        )
                    }
                }
            }
        }

        // --- Search Bar ---
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("roadmaps_search_input"),
                placeholder = { Text("Search roadmaps, skills, topics (e.g. C, Python, DSA, Agentic AI)...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CharcoalTextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = SurfaceCardBorder
                ),
                singleLine = true
            )
        }

        // --- Category Filters ---
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = CharcoalTextSecondary
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) IndigoPrimary else SurfaceCardBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // --- Level Filter Chips ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Difficulty:",
                    style = MaterialTheme.typography.labelMedium,
                    color = CharcoalTextMuted
                )

                FilterChip(
                    selected = selectedLevel == null,
                    onClick = { selectedLevel = null },
                    label = { Text("All Levels") },
                    shape = RoundedCornerShape(8.dp)
                )

                Difficulty.entries.forEach { diff ->
                    val isSelected = selectedLevel == diff
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLevel = if (isSelected) null else diff },
                        label = { Text(diff.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        // --- Results Counter ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filteredRoadmaps.size} Roadmap${if (filteredRoadmaps.size != 1) "s" else ""} Available",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalTextSecondary
                )

                if (savedIds.isNotEmpty()) {
                    Text(
                        text = "${savedIds.size} saved in My Shivaar",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // --- Roadmap Cards ---
        if (filteredRoadmaps.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No Results",
                            modifier = Modifier.size(48.dp),
                            tint = CharcoalTextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching roadmaps found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search terms or category filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextMuted
                        )
                    }
                }
            }
        } else {
            items(filteredRoadmaps, key = { it.id }) { roadmap ->
                PublicRoadmapCard(
                    roadmap = roadmap,
                    isSaved = savedIds.contains(roadmap.id),
                    isFollowed = followedIds.contains(roadmap.id),
                    isExpanded = expandedRoadmapId == roadmap.id,
                    onToggleExpand = {
                        expandedRoadmapId = if (expandedRoadmapId == roadmap.id) null else roadmap.id
                    },
                    onToggleSave = { viewModel.toggleSaveRoadmap(roadmap.id) },
                    onToggleFollow = { viewModel.toggleFollowRoadmap(roadmap.id) }
                )
            }
        }
    }
}

@Composable
private fun PublicRoadmapCard(
    roadmap: PublicRoadmap,
    isSaved: Boolean,
    isFollowed: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleFollow: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("public_roadmap_card_${roadmap.id}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isFollowed) IndigoPrimary else SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (roadmap.skillLevel) {
                                Difficulty.BEGINNER -> SuccessEmeraldContainer
                                Difficulty.INTERMEDIATE -> IndigoContainer
                                Difficulty.ADVANCED -> DangerRoseContainer
                            }
                        ) {
                            Text(
                                text = roadmap.skillLevel.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (roadmap.skillLevel) {
                                    Difficulty.BEGINNER -> SuccessEmerald
                                    Difficulty.INTERMEDIATE -> IndigoPrimary
                                    Difficulty.ADVANCED -> DangerRose
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "• ${roadmap.estimatedTotalHours} hours",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = roadmap.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleSave) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) "Saved" else "Save Roadmap",
                            tint = if (isSaved) IndigoPrimary else CharcoalTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = roadmap.tagline,
                style = MaterialTheme.typography.bodyMedium,
                color = CharcoalTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Verification Level Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = when (roadmap.verificationLevel) {
                        RoadmapResourceVerification.OFFICIAL_DOCUMENTATION -> Icons.Default.Verified
                        RoadmapResourceVerification.INDEPENDENTLY_REVIEWED -> Icons.Default.CheckCircle
                        RoadmapResourceVerification.AI_SUGGESTED -> Icons.Default.AutoAwesome
                    },
                    contentDescription = "Verification Badge",
                    modifier = Modifier.size(16.dp),
                    tint = if (roadmap.verificationLevel == RoadmapResourceVerification.OFFICIAL_DOCUMENTATION) SuccessEmerald else IndigoPrimary
                )
                Text(
                    text = roadmap.verificationLevel.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Audience & Prerequisites
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Audience: ${roadmap.audience}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prerequisites: ${roadmap.prerequisitesSummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable Milestones Accordion
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Divider(color = SurfaceCardBorder)

                    Text(
                        text = "Curriculum Milestones (${roadmap.milestones.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    roadmap.milestones.forEachIndexed { index, milestone ->
                        MilestoneDetailRow(index = index + 1, milestone = milestone)
                    }

                    if (roadmap.officialDocLinks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Official References & Documentation:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalTextMuted
                        )
                        roadmap.officialDocLinks.forEach { link ->
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        try { uriHandler.openUri(link) } catch (_: Exception) {}
                                    }
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open Link",
                                    modifier = Modifier.size(14.dp),
                                    tint = IndigoPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = link,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = IndigoPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Card Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.testTag("toggle_expand_${roadmap.id}")
                ) {
                    Text(
                        text = if (isExpanded) "Hide Details" else "View ${roadmap.milestones.size} Milestones",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        modifier = Modifier.size(16.dp),
                        tint = IndigoPrimary
                    )
                }

                Button(
                    onClick = onToggleFollow,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowed) SuccessEmerald else IndigoPrimary
                    ),
                    modifier = Modifier.testTag("follow_roadmap_${roadmap.id}")
                ) {
                    Icon(
                        imageVector = if (isFollowed) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFollowed) "Following" else "Follow Roadmap",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MilestoneDetailRow(
    index: Int,
    milestone: PublicRoadmapMilestone
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${milestone.estimatedHours}h",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalTextMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Key Topics: ${milestone.topics.joinToString(" • ")}",
                style = MaterialTheme.typography.labelSmall,
                color = IndigoPrimary
            )

            if (milestone.projectOutputs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Project Output: ${milestone.projectOutputs.joinToString("; ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessEmerald,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
