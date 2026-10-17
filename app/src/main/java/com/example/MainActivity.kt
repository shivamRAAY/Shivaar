package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
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
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import com.example.ui.components.FocusSessionDialog
import com.example.ui.components.QuickAddTaskDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val viewModel: ShivaarViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var isAppOpeningActive by remember { mutableStateOf(true) }
                val auth: FirebaseAuth = Firebase.auth
                var currentUser by remember { mutableStateOf<FirebaseUser?>(auth.currentUser) }

                DisposableEffect(auth) {
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        currentUser = firebaseAuth.currentUser
                    }
                    auth.addAuthStateListener(listener)
                    onDispose {
                        auth.removeAuthStateListener(listener)
                    }
                }

                androidx.compose.animation.AnimatedContent(
                    targetState = isAppOpeningActive,
                    transitionSpec = {
                        androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(500)) togetherWith
                                androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(400))
                    },
                    label = "app_opening_transition"
                ) { isOpening ->
                    if (isOpening) {
                        AppOpeningAnimationScreen(
                            onAnimationComplete = { isAppOpeningActive = false }
                        )
                    } else if (currentUser == null) {
                        SignInScreen(
                            onAuthSuccess = {
                                currentUser = Firebase.auth.currentUser
                            }
                        )
                    } else {
                        val currentTab by viewModel.currentTab.collectAsState()
                        val focusSession by viewModel.focusSession.collectAsState()
                        var showAddTaskDialog by remember { mutableStateOf(false) }

                        // Android back navigation: if not on TODAY, back navigates to TODAY
                        BackHandler(enabled = currentTab != AppDestination.TODAY) {
                            viewModel.navigateTo(AppDestination.TODAY)
                        }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            ShivaarTopAppBar(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.navigateTo(it) },
                                onQuickAdd = { showAddTaskDialog = true }
                            )
                        },
                        bottomBar = {
                            ShivaarBottomNavigationBar(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.navigateTo(it) }
                            )
                        },
                        floatingActionButton = {
                            if (currentTab == AppDestination.TODAY) {
                                FloatingActionButton(
                                    onClick = { showAddTaskDialog = true },
                                    containerColor = IndigoPrimary,
                                    contentColor = Color.White,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .testTag("fab_quick_add_task")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Task"
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Add Task",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentTab) {
                                AppDestination.TODAY -> TodayScreen(
                                    viewModel = viewModel,
                                    onStartFocusSession = { task -> viewModel.startFocusSession(task) }
                                )
                                AppDestination.ROADMAP -> RoadmapScreen(viewModel = viewModel)
                                AppDestination.EXPLORE_ROADMAPS -> ExploreRoadmapsScreen(viewModel = viewModel)
                                AppDestination.ONBOARDING -> OnboardingScreen(
                                    viewModel = viewModel,
                                    onFinishOrExit = { viewModel.navigateTo(AppDestination.TODAY) }
                                )
                                AppDestination.ACADEMICS -> AcademicsScreen(viewModel = viewModel)
                                AppDestination.CODING -> CodingDsaScreen(viewModel = viewModel)
                                AppDestination.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                                AppDestination.CAREER -> CareerScreen(viewModel = viewModel)
                                AppDestination.MENTOR -> AiMentorScreen(viewModel = viewModel)
                                AppDestination.RESEARCH -> ResearchLibraryScreen(viewModel = viewModel)
                                AppDestination.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                                AppDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // Focus Session Modal
                    if (focusSession.isActive) {
                        FocusSessionDialog(
                            focusSession = focusSession,
                            onTogglePause = { viewModel.togglePauseFocusSession() },
                            onComplete = { notes -> viewModel.completeFocusSession(notes) },
                            onCancel = { viewModel.cancelFocusSession() }
                        )
                    }

                    // Quick Add Task Modal
                    if (showAddTaskDialog) {
                        QuickAddTaskDialog(
                            onDismiss = { showAddTaskDialog = false },
                            onAddTask = { title, cat, dur, pri, why, dod ->
                                viewModel.addNewTask(title, cat, dur, pri, why, dod)
                            }
                        )
                    }
                }
            }
        }
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShivaarTopAppBar(
    currentTab: AppDestination,
    onTabSelected: (AppDestination) -> Unit,
    onQuickAdd: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Shivaar Logo Crest
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndigoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Shivaar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your degree. Your skills. Your future.",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalTextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Explore Public Roadmaps Shortcut
            IconButton(
                onClick = { onTabSelected(AppDestination.EXPLORE_ROADMAPS) },
                modifier = Modifier.testTag("top_bar_explore_roadmaps_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Explore Roadmaps",
                    tint = if (currentTab == AppDestination.EXPLORE_ROADMAPS) IndigoPrimary else CharcoalTextSecondary
                )
            }

            // AI Mentor Shortcut
            IconButton(
                onClick = { onTabSelected(AppDestination.MENTOR) },
                modifier = Modifier.testTag("top_bar_mentor_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Mentor",
                    tint = if (currentTab == AppDestination.MENTOR) IndigoPrimary else CharcoalTextSecondary
                )
            }

            // Research Library Shortcut
            IconButton(
                onClick = { onTabSelected(AppDestination.RESEARCH) },
                modifier = Modifier.testTag("top_bar_research_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.LibraryBooks,
                    contentDescription = "Research Library",
                    tint = if (currentTab == AppDestination.RESEARCH) IndigoPrimary else CharcoalTextSecondary
                )
            }

            // Analytics Shortcut
            IconButton(
                onClick = { onTabSelected(AppDestination.ANALYTICS) },
                modifier = Modifier.testTag("top_bar_analytics_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.BarChart,
                    contentDescription = "Analytics",
                    tint = if (currentTab == AppDestination.ANALYTICS) IndigoPrimary else CharcoalTextSecondary
                )
            }

            // Settings Shortcut
            IconButton(
                onClick = { onTabSelected(AppDestination.SETTINGS) },
                modifier = Modifier.testTag("top_bar_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = if (currentTab == AppDestination.SETTINGS) IndigoPrimary else CharcoalTextSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.testTag("shivaar_top_app_bar")
    )
}

@Composable
private fun ShivaarBottomNavigationBar(
    currentTab: AppDestination,
    onTabSelected: (AppDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("shivaar_bottom_nav")
    ) {
        val items = listOf(
            Triple(AppDestination.TODAY, Icons.Default.Today, Icons.Outlined.Today),
            Triple(AppDestination.ROADMAP, Icons.Default.Timeline, Icons.Outlined.Timeline),
            Triple(AppDestination.ACADEMICS, Icons.Default.School, Icons.Outlined.School),
            Triple(AppDestination.CODING, Icons.Default.Code, Icons.Outlined.Code),
            Triple(AppDestination.PROJECTS, Icons.Default.Build, Icons.Outlined.Build),
            Triple(AppDestination.CAREER, Icons.Default.Work, Icons.Outlined.WorkOutline)
        )

        items.forEach { (dest, selectedIcon, unselectedIcon) ->
            val isSelected = currentTab == dest
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(dest) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = dest.label
                    )
                },
                label = {
                    Text(
                        text = dest.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IndigoPrimary,
                    selectedTextColor = IndigoPrimary,
                    indicatorColor = IndigoContainer,
                    unselectedIconColor = CharcoalTextMuted,
                    unselectedTextColor = CharcoalTextSecondary
                ),
                modifier = Modifier.testTag("nav_item_${dest.name.lowercase()}")
            )
        }
    }
}
