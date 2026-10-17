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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResearchSource
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@Composable
fun ResearchLibraryScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val sources by viewModel.researchSources.collectAsState()
    val isSearching by viewModel.isResearchSearching.collectAsState()
    val statusMessage by viewModel.researchStatusMessage.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Documentation", "Academics", "Placement & DSA", "Bookmarked")

    val filteredSources = sources.filter { source ->
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Bookmarked" -> source.isBookmarked
            else -> source.category.contains(selectedCategory, ignoreCase = true)
        }
        val matchesQuery = if (searchQuery.isBlank()) true else {
            source.title.contains(searchQuery, ignoreCase = true) ||
            source.domain.contains(searchQuery, ignoreCase = true) ||
            source.summary.contains(searchQuery, ignoreCase = true)
        }
        matchesCategory && matchesQuery
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("research_library_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Header Section ---
        item {
            Column {
                Text(
                    text = "Research Library & References",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Authoritative syllabus specifications, live documentation & verified roadmaps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }
        }

        // --- Search Bar with Google Search Grounding ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search docs, syllabus, syntax (e.g. Kotlin 2.0, AKTU Syllabus)...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = IndigoPrimary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("research_search_field"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IndigoPrimary,
                                unfocusedBorderColor = SurfaceCardBorder
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    viewModel.searchResearchLibrary(searchQuery)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            modifier = Modifier.testTag("research_search_btn"),
                            enabled = searchQuery.isNotBlank() && !isSearching
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.TravelExplore,
                                    contentDescription = "Live Search",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ground", fontSize = 12.sp)
                            }
                        }
                    }

                    // Preset search chips
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val presetPrompts = listOf(
                            "Kotlin 2.0 Docs",
                            "AKTU Syllabus 2024-25",
                            "C Pointers cppreference",
                            "Python 3.12 Tutorial",
                            "Striver DSA Sheet"
                        )
                        items(presetPrompts) { prompt ->
                            SuggestionChip(
                                onClick = {
                                    searchQuery = prompt
                                    viewModel.searchResearchLibrary(prompt)
                                },
                                label = { Text(prompt, fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = SurfaceCardSubtle,
                                    labelColor = IndigoPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- Status Banner ---
        if (statusMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary
                        )
                    }
                }
            }
        }

        // --- Category Filters ---
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // --- Results Count & Grounding Badge ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredSources.size} References",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalTextSecondary
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFECFDF5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Verified Grounding Active",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // --- Source Cards ---
        items(filteredSources, key = { it.id }) { source ->
            ResearchSourceCard(
                source = source,
                onOpen = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                        context.startActivity(intent)
                    } catch (e: Exception) {}
                },
                onToggleBookmark = {
                    viewModel.toggleResearchBookmark(source.id)
                },
                onAskAi = {
                    viewModel.navigateTo(AppDestination.MENTOR)
                    viewModel.sendMentorMessage("Explain and provide an exam & interview study breakdown for: ${source.title} (${source.url})")
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ResearchSourceCard(
    source: ResearchSource,
    onOpen: () -> Unit,
    onToggleBookmark: () -> Unit,
    onAskAi: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("research_card_${source.id}"),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = IndigoContainer
                    ) {
                        Text(
                            text = source.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (source.isOfficial) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFECFDF5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Text(
                                text = "Official Document",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF047857),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (source.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (source.isBookmarked) IndigoPrimary else CharcoalTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = source.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Domain: ${source.domain}",
                style = MaterialTheme.typography.labelSmall,
                color = CharcoalTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = source.summary,
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpen,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_source_btn_${source.id}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open Reference", style = MaterialTheme.typography.labelSmall)
                }

                FilledTonalButton(
                    onClick = onAskAi,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("ask_ai_btn_${source.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Study Guide", style = MaterialTheme.typography.labelSmall, color = IndigoPrimary)
                }
            }
        }
    }
}
