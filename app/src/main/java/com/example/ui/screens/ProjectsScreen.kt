package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.ProjectItem
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@Composable
fun ProjectsScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("projects_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Column {
                Text(
                    text = "Project Studio & Portfolio",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Build production-grade software that proves engineering competence beyond tutorial clones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }
        }

        // --- Projects List ---
        items(projects, key = { it.id }) { project ->
            ProjectCard(
                project = project,
                onToggleFeature = { index -> viewModel.toggleProjectFeature(project.id, index) },
                onOpenGithub = {
                    if (project.githubUrl.isNotBlank()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project.githubUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignored if browser not installed
                        }
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectItem,
    onToggleFeature: (Int) -> Unit,
    onOpenGithub: () -> Unit
) {
    val completedFeatures = project.features.count { it.isDone }
    val progress = if (project.features.isNotEmpty()) {
        completedFeatures.toFloat() / project.features.size.toFloat()
    } else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_card_${project.id}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Title & Semester Target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = project.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IndigoContainer
                ) {
                    Text(
                        text = "Year ${project.year} • Sem ${project.semester}",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tech Stack Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                project.techStack.forEach { tech ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceCardSubtle
                    ) {
                        Text(
                            text = tech,
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextSecondary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recruiter Value Statement
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = IndigoContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "🎯 ", fontSize = 14.sp)
                    Text(
                        text = "Recruiter Value: ${project.whyItMattersForPlacement}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnIndigoContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Feature Checklist Header & Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Feature Milestones ($completedFeatures/${project.features.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (progress >= 1f) SuccessEmerald else IndigoPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress >= 1f) SuccessEmerald else IndigoPrimary,
                trackColor = SurfaceCardSubtle
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Checkboxes
            project.features.forEachIndexed { index, feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onToggleFeature(index) }
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = feature.isDone,
                        onCheckedChange = { onToggleFeature(index) },
                        colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (feature.isDone) CharcoalTextMuted else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // GitHub & Demo Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpenGithub,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("github_btn_${project.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "GitHub",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("GitHub Repository", style = MaterialTheme.typography.labelSmall)
                }

                if (progress >= 1f) {
                    Surface(
                        shape = CircleShape,
                        color = SuccessEmeraldContainer
                    ) {
                        Text(
                            text = "Portfolio Ready",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
