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

@Composable
fun CareerScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val applications by viewModel.careerApplications.collectAsState()
    var selectedSection by remember { mutableStateOf(0) } // 0: Applications, 1: Core CS Revision

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
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
                    text = { Text("Application Pipeline (${applications.size})") }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Core CS Flashcards") }
                )
            }
        }

        if (selectedSection == 0) {
            // Pipeline View
            items(applications, key = { it.id }) { app ->
                CareerApplicationCard(
                    application = app,
                    onStatusChange = { newStatus -> viewModel.updateApplicationStatus(app.id, newStatus) }
                )
            }
        } else {
            // Core CS Revision Cards
            item {
                CoreCsRevisionSection()
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CareerApplicationCard(
    application: CareerApplication,
    onStatusChange: (ApplicationStatus) -> Unit
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

            Text(
                text = "Deadline: ${application.deadline}",
                style = MaterialTheme.typography.labelSmall,
                color = CharcoalTextMuted
            )

            if (application.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = application.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stage Advance Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = application.stipendOrPackage,
                    style = MaterialTheme.typography.labelSmall,
                    color = IndigoPrimary,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (application.status == ApplicationStatus.WISHLIST) {
                        Button(
                            onClick = { onStatusChange(ApplicationStatus.APPLIED) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Mark Applied", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (application.status == ApplicationStatus.APPLIED) {
                        OutlinedButton(
                            onClick = { onStatusChange(ApplicationStatus.OA_ROUND) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("OA Received", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (application.status == ApplicationStatus.OA_ROUND) {
                        Button(
                            onClick = { onStatusChange(ApplicationStatus.INTERVIEW) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Interview", style = MaterialTheme.typography.labelSmall)
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
        Pair("Object-Oriented Design", "4 Pillars of OOP:\n• Encapsulation (Hiding internal state)\n• Abstraction (Exposing only essential interface)\n• Inheritance (Code reusability)\n• Polymorphism (Method overriding & overloading).")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        flashcards.forEach { (topic, content) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )
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
