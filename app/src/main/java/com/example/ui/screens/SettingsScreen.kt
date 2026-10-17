package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.credentials.CredentialManager
import com.example.data.model.SyncStatus
import com.example.data.model.UserProfile
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun SettingsScreen(
    viewModel: ShivaarViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val userProfile by viewModel.userProfile.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    var nameInput by remember(userProfile) { mutableStateOf(userProfile.name) }
    var instituteInput by remember(userProfile) { mutableStateOf(userProfile.institute) }
    var budgetInput by remember(userProfile) { mutableStateOf(userProfile.dailyBudgetMinutes.toString()) }
    var apiKeyInput by remember(customApiKey) { mutableStateOf(customApiKey) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    val currentUser = Firebase.auth.currentUser

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header ---
        item {
            Column {
                Text(
                    text = "Settings & Student Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Personalize your college timetable, study budget, and AI credentials.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
            }
        }

        // --- Cloud Account & Persistence Card ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = IndigoPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cloud Firestore Persistence",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            color = when (syncStatus) {
                                SyncStatus.Synced -> SuccessEmeraldContainer
                                SyncStatus.Syncing -> IndigoContainer
                                is SyncStatus.Error -> DangerRoseContainer
                                SyncStatus.Idle -> SurfaceCardSubtle
                            }
                        ) {
                            Text(
                                text = when (syncStatus) {
                                    SyncStatus.Synced -> "Synced"
                                    SyncStatus.Syncing -> "Syncing"
                                    is SyncStatus.Error -> "Offline"
                                    SyncStatus.Idle -> "Idle"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = when (syncStatus) {
                                    SyncStatus.Synced -> SuccessEmerald
                                    SyncStatus.Syncing -> IndigoPrimary
                                    is SyncStatus.Error -> DangerRose
                                    SyncStatus.Idle -> CharcoalTextSecondary
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentUser != null) {
                        Text(
                            text = "Signed in as: ${currentUser.email ?: currentUser.displayName ?: "Student"}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "User UID: ${currentUser.uid}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextMuted,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.retrySync() },
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("sync_cloud_now_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Re-sync Cloud")
                            }

                            Button(
                                onClick = {
                                    signOutUser(
                                        context = context,
                                        credentialManager = credentialManager,
                                        onSignOutComplete = {},
                                        scope = coroutineScope
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DangerRoseContainer,
                                    contentColor = DangerRose
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("sign_out_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sign Out")
                            }
                        }
                    } else {
                        Text(
                            text = "Not connected to cloud account. Operating in local-cache mode.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextMuted
                        )
                    }
                }
            }
        }

        // --- Profile Card ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Student Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = instituteInput,
                        onValueChange = { instituteInput = it },
                        label = { Text("College / Institute") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text("Daily Study Budget (Minutes)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val budget = budgetInput.toIntOrNull() ?: 150
                            viewModel.updateUserProfile(
                                userProfile.copy(
                                    name = nameInput,
                                    institute = instituteInput,
                                    dailyBudgetMinutes = budget
                                )
                            )
                            showSavedSnackbar = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_profile_btn")
                    ) {
                        Text("Save Profile")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppDestination.ONBOARDING) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_retake_onboarding"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit 5-Step Learning & Career Onboarding",
                            color = IndigoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // --- AI Studio Secrets / Gemini API Key ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini AI Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Shivaar connects to Gemini 3.5 Flash for contextual syllabus queries, viva preparation, and code analysis. In AI Studio, keys are automatically provided via the Secrets panel. You can also paste an explicit API key below:",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Gemini API Key (Optional Override)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.setCustomApiKey(apiKeyInput.trim())
                            showSavedSnackbar = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier.testTag("save_api_key_btn")
                    ) {
                        Text("Update Key")
                    }
                }
            }
        }

        // --- App Information ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceCardSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CharcoalTextMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About Shivaar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Shivaar v1.0.0 (Production Build)\nTailored for Shivam Yadav • B.Tech CSE Core\nCurriculum: AKTU 2024-2028 Scheme\nSpecialization: Full-Stack & Applied AI",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
