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
import com.example.data.model.ApplicationStatus
import com.example.data.model.CareerApplication
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val applications by viewModel.careerApplications.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedSection by remember { mutableStateOf(0) } // 0: Applications, 1: Core CS Flashcards
    var showAddAppDialog by remember { mutableStateOf(false) }

    var newCompany by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("Software Engineer Intern") }
    var newDeadline by remember { mutableStateOf("Oct 30, 2026") }
    var newPackage by remember { mutableStateOf("₹45,000/mo Stipend") }
    var newStatus by remember { mutableStateOf(ApplicationStatus.APPLIED) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedSection == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAddAppDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Application") },
                    text = { Text("Add Application") },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("btn_add_application")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("career_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Header ---
            item {
                Column {
                    Text(
                        text = "Career & Placements Hub",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Track software engineering internship applications and revise high-yield core CS interview topics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }
            }

            // --- Pipeline Funnel Summary Card ---
            item {
                PipelineFunnelCard(applications = applications)
            }

            // --- Tab Switcher ---
            item {
                TabRow(
                    selectedTabIndex = selectedSection,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = IndigoPrimary
                ) {
                    Tab(
                        selected = selectedSection == 0,
                        onClick = { selectedSection = 0 },
                        text = { Text("Application Pipeline (${applications.size})") },
                        modifier = Modifier.testTag("tab_career_pipeline")
                    )
                    Tab(
                        selected = selectedSection == 1,
                        onClick = { selectedSection = 1 },
                        text = { Text("Core CS Flashcards") },
                        modifier = Modifier.testTag("tab_core_cs")
                    )
                }
            }

            if (selectedSection == 0) {
                // Pipeline View
                items(applications, key = { it.id }) { app ->
                    CareerApplicationCard(
                        application = app,
                        onStatusChange = { newStatus -> viewModel.updateApplicationStatus(app.id, newStatus) },
                        onGeneratePrepTask = {
                            viewModel.createCareerPrepTask(app)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Created 45m interview prep task for ${app.company} in Today's list!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            } else {
                // Core CS Revision Cards
                item {
                    CoreCsRevisionSection()
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // Add Application Dialog
    if (showAddAppDialog) {
        AlertDialog(
            onDismissRequest = { showAddAppDialog = false },
            title = { Text("Track Job / Internship Application") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newCompany,
                        onValueChange = { newCompany = it },
                        label = { Text("Company Name") },
                        placeholder = { Text("e.g. Google, Atlassian, Microsoft") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_app_company"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newRole,
                        onValueChange = { newRole = it },
                        label = { Text("Role") },
                        placeholder = { Text("Software Engineer Intern (SDE-1)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_app_role"),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newDeadline,
                            onValueChange = { newDeadline = it },
                            label = { Text("Deadline") },
                            placeholder = { Text("Nov 15, 2026") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_app_deadline"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newPackage,
                            onValueChange = { newPackage = it },
                            label = { Text("Package / Stipend") },
                            placeholder = { Text("₹50k/mo or 18 LPA") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_app_package"),
                            singleLine = true
                        )
                    }

                    Text("Initial Status:", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(ApplicationStatus.APPLIED, ApplicationStatus.OA_ROUND, ApplicationStatus.WISHLIST).forEach { status ->
                            FilterChip(
                                selected = newStatus == status,
                                onClick = { newStatus = status },
                                label = { Text(status.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndigoPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val company = newCompany.ifBlank { "Tech Company" }
                        viewModel.addNewCareerApplication(
                            company = company,
                            role = newRole.ifBlank { "Software Engineer Intern" },
                            status = newStatus,
                            deadline = newDeadline,
                            stipendOrPackage = newPackage
                        )
                        showAddAppDialog = false
                        newCompany = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Added $company application to pipeline!")
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_add_app")
                ) {
                    Text("Add Application")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAppDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PipelineFunnelCard(applications: List<CareerApplication>) {
    val applied = applications.count { it.status == ApplicationStatus.APPLIED }
    val oa = applications.count { it.status == ApplicationStatus.OA_ROUND }
    val interview = applications.count { it.status == ApplicationStatus.INTERVIEW }
    val offer = applications.count { it.status == ApplicationStatus.OFFER }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pipeline_funnel_card"),
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
                        text = "Recruitment Funnel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Active Placement Track Season 2026-27",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (offer > 0) SuccessEmeraldContainer else IndigoContainer
                ) {
                    Text(
                        text = if (offer > 0) "$offer Offer Received" else "${applications.size} In Pipeline",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (offer > 0) SuccessEmerald else IndigoPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FunnelStep(count = applied, label = "Applied", color = InfoBlue)
                FunnelStep(count = oa, label = "OA Test", color = WarningAmber)
                FunnelStep(count = interview, label = "Interview", color = Color(0xFF8B5CF6))
                FunnelStep(count = offer, label = "Offer", color = SuccessEmerald)
            }
        }
    }
}

@Composable
private fun FunnelStep(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CharcoalTextSecondary
        )
    }
}

@Composable
private fun CareerApplicationCard(
    application: CareerApplication,
    onStatusChange: (ApplicationStatus) -> Unit,
    onGeneratePrepTask: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("application_card_${application.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = application.company,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = application.role,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (application.status) {
                        ApplicationStatus.OFFER -> SuccessEmeraldContainer
                        ApplicationStatus.INTERVIEW, ApplicationStatus.OA_ROUND -> WarningAmberContainer
                        ApplicationStatus.APPLIED -> InfoBlueContainer
                        else -> SurfaceCardSubtle
                    }
                ) {
                    Text(
                        text = application.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = when (application.status) {
                            ApplicationStatus.OFFER -> SuccessEmerald
                            ApplicationStatus.INTERVIEW, ApplicationStatus.OA_ROUND -> WarningAmber
                            ApplicationStatus.APPLIED -> InfoBlue
                            else -> CharcoalTextSecondary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Deadline: ${application.deadline}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalTextMuted
                )
                if (application.stipendOrPackage.isNotBlank()) {
                    Text(
                        text = application.stipendOrPackage,
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (application.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = application.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Prep Task Generator & Stage Transitions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onGeneratePrepTask,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_career_prep_${application.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Prep Task",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prep Task (45m)", style = MaterialTheme.typography.labelSmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (application.status == ApplicationStatus.WISHLIST) {
                        OutlinedButton(
                            onClick = { onStatusChange(ApplicationStatus.APPLIED) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Mark Applied", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (application.status == ApplicationStatus.APPLIED) {
                        OutlinedButton(
                            onClick = { onStatusChange(ApplicationStatus.OA_ROUND) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("OA Received", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (application.status == ApplicationStatus.OA_ROUND) {
                        OutlinedButton(
                            onClick = { onStatusChange(ApplicationStatus.INTERVIEW) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Interview", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (application.status == ApplicationStatus.INTERVIEW) {
                        Button(
                            onClick = { onStatusChange(ApplicationStatus.OFFER) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Offer!", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoreCsRevisionSection() {
    val flashcards = listOf(
        Pair("Operating Systems", "Deadlock 4 Coffman Conditions:\n1. Mutual Exclusion\n2. Hold and Wait\n3. No Preemption\n4. Circular Wait.\nTo prevent deadlocks, eliminate at least one condition (e.g., resource hierarchy)."),
        Pair("DBMS", "ACID Properties in Transactions:\n• Atomicity (All or nothing)\n• Consistency (Valid DB state maintained)\n• Isolation (Concurrent transactions do not interfere)\n• Durability (Committed changes survive system crash)."),
        Pair("Computer Networks", "TCP 3-Way Handshake:\n1. SYN: Client sends SYN packet to server with initial sequence number.\n2. SYN-ACK: Server acknowledges and sends SYN.\n3. ACK: Client sends final ACK. Connection established!"),
        Pair("Object-Oriented Design", "4 Pillars of OOP:\n• Encapsulation (Hiding internal state)\n• Abstraction (Exposing only essential interface)\n• Inheritance (Code reusability)\n• Polymorphism (Method overriding & overloading)."),
        Pair("System Design Basics", "Horizontal vs Vertical Scaling & CAP Theorem:\n• Consistency, Availability, Partition Tolerance (pick 2 in distributed partitions).\n• Latency vs Throughput and caching with Redis/Memcached.")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        flashcards.forEach { (topic, content) ->
            var isMastered by remember { mutableStateOf(false) }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (isMastered) SuccessEmeraldContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isMastered) SuccessEmerald.copy(alpha = 0.4f) else SurfaceCardBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topic,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                        IconButton(
                            onClick = { isMastered = !isMastered },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isMastered) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = "Mastered",
                                tint = if (isMastered) SuccessEmerald else CharcoalTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }
            }
        }
    }
}
