package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectItem
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddProjectDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newTagline by remember { mutableStateOf("") }
    var newTechStack by remember { mutableStateOf("Kotlin, Jetpack Compose, Coroutines") }
    var newGithubUrl by remember { mutableStateOf("https://github.com/shivam/my-project") }
    var newWhyItMatters by remember { mutableStateOf("Demonstrates clean architecture, concurrency, and real-time state management.") }

    val completedProjectsCount = projects.count { proj -> proj.features.all { it.isDone } }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddProjectDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Project") },
                text = { Text("Add Project") },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("btn_add_project")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            // --- Portfolio Readiness Summary Card ---
            item {
                PortfolioReadinessCard(
                    totalProjects = projects.size,
                    completedProjects = completedProjectsCount
                )
            }

            // --- Section Header ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Engineering Capstones & Systems (${projects.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // --- Projects List ---
            items(projects, key = { it.id }) { project ->
                ProjectCard(
                    project = project,
                    onToggleFeature = { index -> viewModel.toggleProjectFeature(project.id, index) },
                    onGenerateSprintTask = {
                        viewModel.createProjectSprintTask(project)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Created 45m sprint task for ${project.title} in Today's list!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
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
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // Add Project Dialog
    if (showAddProjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddProjectDialog = false },
            title = { Text("Launch New Capstone Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Project Title") },
                        placeholder = { Text("e.g. Distributed Cache Engine") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_project_title"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newTagline,
                        onValueChange = { newTagline = it },
                        label = { Text("Tagline / Elevator Pitch") },
                        placeholder = { Text("e.g. LRU/LFU eviction with sub-millisecond p99 latency") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_project_tagline"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newTechStack,
                        onValueChange = { newTechStack = it },
                        label = { Text("Tech Stack (comma separated)") },
                        placeholder = { Text("e.g. Go, Redis, Docker, gRPC") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_project_tech"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newGithubUrl,
                        onValueChange = { newGithubUrl = it },
                        label = { Text("GitHub Repository URL") },
                        placeholder = { Text("https://github.com/...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_project_github"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newWhyItMatters,
                        onValueChange = { newWhyItMatters = it },
                        label = { Text("Recruiter Value Proposition") },
                        placeholder = { Text("Why does this impress tier-1 engineering hiring managers?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_project_recruiter_val"),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = newTitle.ifBlank { "Untitled Project" }
                        val techs = newTechStack.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        viewModel.addNewProject(
                            title = title,
                            tagline = newTagline.ifBlank { "High-performance software project." },
                            techStack = techs.ifEmpty { listOf("Kotlin", "Clean Architecture") },
                            githubUrl = newGithubUrl,
                            whyItMatters = newWhyItMatters
                        )
                        showAddProjectDialog = false
                        newTitle = ""
                        newTagline = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Added $title to Project Studio!")
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_add_project")
                ) {
                    Text("Add Project")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProjectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PortfolioReadinessCard(totalProjects: Int, completedProjects: Int) {
    val completionRatio = if (totalProjects > 0) completedProjects.toFloat() / totalProjects.toFloat() else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("portfolio_readiness_card"),
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
                        text = "Placement Portfolio Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$completedProjects of $totalProjects Capstones Production-Ready",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (completedProjects > 0) SuccessEmeraldContainer else WarningAmberContainer
                ) {
                    Text(
                        text = if (completedProjects > 0) "Resume Ready" else "In Development",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (completedProjects > 0) SuccessEmerald else WarningAmber,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { completionRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (completionRatio >= 1f) SuccessEmerald else IndigoPrimary,
                trackColor = SurfaceCardSubtle
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Key Criteria: Architecture • Tests • Docs • CI/CD",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalTextMuted
                )
                Text(
                    text = "${(completionRatio * 100).toInt()}% Done",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectItem,
    onToggleFeature: (Int) -> Unit,
    onGenerateSprintTask: () -> Unit,
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

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            // Action Row: Sprint Task Generator & GitHub Repo Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onGenerateSprintTask,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_project_sprint_${project.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Sprint Task",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sprint Task (45m)", style = MaterialTheme.typography.labelSmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
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
                        Text("GitHub", style = MaterialTheme.typography.labelSmall)
                    }

                    if (progress >= 1f) {
                        Surface(
                            shape = CircleShape,
                            color = SuccessEmeraldContainer
                        ) {
                            Text(
                                text = "Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessEmerald,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
