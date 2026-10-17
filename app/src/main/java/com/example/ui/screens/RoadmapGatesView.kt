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
import com.example.data.model.GateStatus
import com.example.data.model.PrerequisiteGate
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@Composable
fun RoadmapGatesView(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val gates by viewModel.prerequisiteGates.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("prerequisite_gates_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Column {
                Text(
                    text = "8 Prerequisite Assessment Gates",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Rigorous milestone gates ensuring true competence before advancing to subsequent development and interview tiers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }
        }

        // --- Gates List ---
        items(gates, key = { it.id }) { gate ->
            PrerequisiteGateCard(
                gate = gate,
                onStatusChange = { newStatus -> viewModel.updateGateStatus(gate.id, newStatus) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrerequisiteGateCard(
    gate: PrerequisiteGate,
    onStatusChange: (GateStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }

    val statusColor = when (gate.status) {
        GateStatus.MASTERED -> SuccessEmerald
        GateStatus.READY_FOR_ASSESSMENT -> InfoBlue
        GateStatus.IN_PROGRESS -> IndigoPrimary
        GateStatus.NEEDS_REVISION -> WarningAmber
        GateStatus.NOT_STARTED -> CharcoalTextMuted
    }

    val statusBg = when (gate.status) {
        GateStatus.MASTERED -> SuccessEmeraldContainer
        GateStatus.READY_FOR_ASSESSMENT -> InfoBlueContainer
        GateStatus.IN_PROGRESS -> IndigoContainer
        GateStatus.NEEDS_REVISION -> WarningAmberContainer
        GateStatus.NOT_STARTED -> SurfaceCardSubtle
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gate_card_${gate.number}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Gate Number, Title & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(statusColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${gate.number}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = gate.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Target Checkpoint: Month ${gate.targetMonth}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextSecondary
                        )
                    }
                }

                // Interactive Status Pill with Dropdown
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBg,
                        modifier = Modifier
                            .clickable { showStatusMenu = true }
                            .testTag("gate_status_pill_${gate.number}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = gate.status.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        GateStatus.entries.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption.label) },
                                onClick = {
                                    onStatusChange(statusOption)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Practical Evidence & Assessment
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceCardSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Practical Evidence Required:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )
                    Text(
                        text = gate.practicalEvidence,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expandable details (Topics, Completion Conditions, Remediation)
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Required Topics to Master:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    gate.requiredTopics.forEach { topic ->
                        Text(
                            text = "• $topic",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Project & Code Submission Requirements:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = gate.projectRequirements,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Completion Conditions:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    gate.completionConditions.forEach { cond ->
                        Text(
                            text = "✓ $cond",
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessEmerald,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remediation Plan if gate is not yet met
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = WarningAmberContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Remediation Steps If Blocked:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = gate.remediationSteps,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = { expanded = !expanded },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (expanded) "Hide Criteria" else "View Assessment Criteria & Remediation",
                    style = MaterialTheme.typography.labelSmall,
                    color = IndigoPrimary
                )
            }
        }
    }
}
