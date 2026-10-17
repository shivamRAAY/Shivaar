package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.*
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: ShivaarViewModel,
    onFinishOrExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialData by viewModel.onboardingData.collectAsState()

    // Local mutable state initialized from saved repository state
    var currentStep by remember(initialData) { mutableIntStateOf(initialData.currentStep.coerceIn(1, 7)) }
    var chosenPathId by remember(initialData) { mutableStateOf(initialData.chosenPathId ?: "path_fullstack_cloud") }

    val aiAnalysisFromVm by viewModel.aiAnalysisState.collectAsState()
    val isAnalyzingProfile by viewModel.isAnalyzingProfile.collectAsState()
    val activeAiAnalysis = aiAnalysisFromVm ?: initialData.aiAnalysis

    // Step 1: Education state
    var qualification by remember(initialData) { mutableStateOf(initialData.step1Education.qualification) }
    var selectedStage by remember(initialData) { mutableStateOf(initialData.step1Education.stage) }
    var fieldOfStudy by remember(initialData) { mutableStateOf(initialData.step1Education.fieldOfStudy) }
    var currentYear by remember(initialData) { mutableIntStateOf(initialData.step1Education.currentYear ?: 1) }
    var currentSemester by remember(initialData) { mutableIntStateOf(initialData.step1Education.currentSemester ?: 1) }
    var isCollegeApplicable by remember(initialData) {
        mutableStateOf(initialData.step1Education.stage == EducationStage.UNDERGRADUATE || initialData.step1Education.stage == EducationStage.POSTGRADUATE || initialData.step1Education.stage == EducationStage.DIPLOMA_STUDENT)
    }
    var institution by remember(initialData) { mutableStateOf(initialData.step1Education.institution) }
    var curriculum by remember(initialData) { mutableStateOf(initialData.step1Education.curriculum) }
    var expectedGraduationDate by remember(initialData) { mutableStateOf(initialData.step1Education.expectedGraduationDate) }
    var studiedSubjects by remember(initialData) { mutableStateOf(initialData.step1Education.studiedSubjects) }
    var newSubjectInput by remember { mutableStateOf("") }

    // Step 2: Skills state
    var selectedLanguages by remember(initialData) { mutableStateOf(initialData.step2Skills.languagesKnown.toSet()) }
    var completedTopics by remember(initialData) { mutableStateOf(initialData.step2Skills.topicsCompleted) }
    var newTopicInput by remember { mutableStateOf("") }
    var projectsBuiltText by remember(initialData) { mutableStateOf(initialData.step2Skills.projectsBuilt.joinToString(", ")) }
    var dsaExperience by remember(initialData) { mutableStateOf(initialData.step2Skills.dsaExperience) }
    var devExperience by remember(initialData) { mutableStateOf(initialData.step2Skills.devExperience) }
    var aiExperience by remember(initialData) { mutableStateOf(initialData.step2Skills.aiExperience) }
    var portfolioOrGithubUrl by remember(initialData) { mutableStateOf(initialData.step2Skills.portfolioOrGithubUrl) }
    var confidenceLevel by remember(initialData) { mutableStateOf(initialData.step2Skills.confidenceLevel) }
    var diagnosticResult by remember(initialData) { mutableStateOf(initialData.step2Skills.diagnosticResult) }
    var showDiagnosticModal by remember { mutableStateOf(false) }

    // Step 3: Goals state
    var selectedGoals by remember(initialData) { mutableStateOf(initialData.step3Goals.selectedGoals.toSet()) }
    var customGoal by remember(initialData) { mutableStateOf(initialData.step3Goals.customGoal) }
    var targetTimelineMonths by remember(initialData) { mutableIntStateOf(initialData.step3Goals.targetTimelineMonths) }
    var preferredRolesText by remember(initialData) { mutableStateOf(initialData.step3Goals.preferredRoles.joinToString(", ")) }
    var primaryPriority by remember(initialData) { mutableStateOf(initialData.step3Goals.primaryPriority) }

    // Step 4: Interests state
    var selectedInterests by remember(initialData) { mutableStateOf(initialData.step4Interests.interests.toSet()) }

    // Step 5: Constraints state
    var availableHoursPerWeek by remember(initialData) { mutableIntStateOf(initialData.step5Constraints.availableHoursPerWeek) }
    var preferredSchedule by remember(initialData) { mutableStateOf(initialData.step5Constraints.preferredSchedule) }
    var budgetRangeInr by remember(initialData) { mutableStateOf(initialData.step5Constraints.budgetRangeInr) }
    var preferredLanguage by remember(initialData) { mutableStateOf(initialData.step5Constraints.preferredLanguage) }
    var deviceConstraints by remember(initialData) { mutableStateOf(initialData.step5Constraints.deviceConstraints) }
    var upcomingExams by remember(initialData) { mutableStateOf(initialData.step5Constraints.upcomingExamsOrDeadlines) }
    var selectedLearningStyles by remember(initialData) { mutableStateOf(initialData.step5Constraints.learningStylePreferences.toSet()) }
    var existingCommitments by remember(initialData) { mutableStateOf(initialData.step5Constraints.existingCommitments) }

    // Function to assemble current state and save incrementally
    val saveCurrentProgress: (Boolean) -> Unit = { markCompleted ->
        val updated = OnboardingData(
            step1Education = OnboardingEducation(
                qualification = qualification,
                stage = selectedStage,
                fieldOfStudy = fieldOfStudy,
                currentYear = if (isCollegeApplicable) currentYear else null,
                currentSemester = if (isCollegeApplicable) currentSemester else null,
                institution = institution,
                curriculum = curriculum,
                expectedGraduationDate = expectedGraduationDate,
                studiedSubjects = studiedSubjects
            ),
            step2Skills = OnboardingSkills(
                languagesKnown = selectedLanguages.toList(),
                topicsCompleted = completedTopics,
                projectsBuilt = projectsBuiltText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                dsaExperience = dsaExperience,
                devExperience = devExperience,
                aiExperience = aiExperience,
                portfolioOrGithubUrl = portfolioOrGithubUrl,
                confidenceLevel = confidenceLevel,
                diagnosticResult = diagnosticResult
            ),
            step3Goals = OnboardingGoals(
                selectedGoals = selectedGoals.toList(),
                customGoal = customGoal,
                targetTimelineMonths = targetTimelineMonths,
                preferredRoles = preferredRolesText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                primaryPriority = primaryPriority
            ),
            step4Interests = OnboardingInterests(interests = selectedInterests.toList()),
            step5Constraints = OnboardingConstraints(
                availableHoursPerWeek = availableHoursPerWeek,
                preferredSchedule = preferredSchedule,
                budgetRangeInr = budgetRangeInr,
                preferredLanguage = preferredLanguage,
                deviceConstraints = deviceConstraints,
                upcomingExamsOrDeadlines = upcomingExams,
                learningStylePreferences = selectedLearningStyles.toList(),
                existingCommitments = existingCommitments
            ),
            aiAnalysis = activeAiAnalysis,
            chosenPathId = chosenPathId,
            currentStep = currentStep,
            isCompleted = markCompleted,
            completedAt = if (markCompleted) System.currentTimeMillis() else initialData.completedAt
        )
        viewModel.saveOnboardingProgress(updated)
    }

    // Android back navigation: if > 1 step back, else exit
    BackHandler {
        if (currentStep > 1) {
            saveCurrentProgress(false)
            currentStep--
        } else {
            saveCurrentProgress(false)
            onFinishOrExit()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Personalized Onboarding",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step $currentStep of 7 • ${getStepTitle(currentStep)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) {
                                saveCurrentProgress(false)
                                currentStep--
                            } else {
                                saveCurrentProgress(false)
                                onFinishOrExit()
                            }
                        },
                        modifier = Modifier.testTag("onboarding_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            saveCurrentProgress(false)
                            onFinishOrExit()
                        },
                        modifier = Modifier.testTag("onboarding_save_exit_btn")
                    ) {
                        Text(
                            text = "Save & Exit",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = IndigoPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, SurfaceCardBorder),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = {
                                saveCurrentProgress(false)
                                currentStep--
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("onboarding_prev_step_btn")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep < 5) {
                                saveCurrentProgress(false)
                                currentStep++
                            } else if (currentStep == 5) {
                                saveCurrentProgress(false)
                                currentStep = 6
                                if (activeAiAnalysis == null) {
                                    viewModel.requestProfileAnalysis()
                                }
                            } else if (currentStep == 6) {
                                saveCurrentProgress(false)
                                currentStep = 7
                            } else {
                                // Step 7: Complete onboarding, generate personalized roadmap (Phase 4) and initialize execution tasks (Phase 5)
                                saveCurrentProgress(true)
                                viewModel.choosePath(chosenPathId)
                                onFinishOrExit()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier.testTag("onboarding_next_step_btn")
                    ) {
                        Text(
                            text = when (currentStep) {
                                in 1..4 -> "Save & Continue"
                                5 -> "Analyze Profile with AI"
                                6 -> "Proceed to Path Choice"
                                else -> "Generate Roadmap & Start"
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (currentStep < 7) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.RocketLaunch,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier.testTag("onboarding_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Stepper Progress Indicator
            LinearProgressIndicator(
                progress = { (currentStep / 7f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = IndigoPrimary,
                trackColor = SurfaceCardSubtle
            )

            // Step Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                when (currentStep) {
                    1 -> {
                        item {
                            Step1EducationSection(
                                qualification = qualification,
                                onQualificationChange = { qualification = it },
                                selectedStage = selectedStage,
                                onStageChange = {
                                    selectedStage = it
                                    isCollegeApplicable = it == EducationStage.UNDERGRADUATE || it == EducationStage.POSTGRADUATE || it == EducationStage.DIPLOMA_STUDENT
                                },
                                fieldOfStudy = fieldOfStudy,
                                onFieldOfStudyChange = { fieldOfStudy = it },
                                currentYear = currentYear,
                                onYearChange = { currentYear = it },
                                currentSemester = currentSemester,
                                onSemesterChange = { currentSemester = it },
                                isCollegeApplicable = isCollegeApplicable,
                                onToggleCollegeApplicable = { isCollegeApplicable = it },
                                institution = institution,
                                onInstitutionChange = { institution = it },
                                curriculum = curriculum,
                                onCurriculumChange = { curriculum = it },
                                expectedGraduation = expectedGraduationDate,
                                onGraduationChange = { expectedGraduationDate = it },
                                studiedSubjects = studiedSubjects,
                                onAddSubject = { sub ->
                                    if (sub.isNotBlank() && !studiedSubjects.contains(sub)) {
                                        studiedSubjects = studiedSubjects + sub
                                    }
                                },
                                onRemoveSubject = { sub ->
                                    studiedSubjects = studiedSubjects - sub
                                }
                            )
                        }
                    }

                    2 -> {
                        item {
                            Step2SkillsSection(
                                selectedLanguages = selectedLanguages,
                                onToggleLanguage = { lang ->
                                    selectedLanguages = if (selectedLanguages.contains(lang)) selectedLanguages - lang else selectedLanguages + lang
                                },
                                completedTopics = completedTopics,
                                onAddTopic = { topic ->
                                    if (topic.isNotBlank() && !completedTopics.contains(topic)) {
                                        completedTopics = completedTopics + topic
                                    }
                                },
                                onRemoveTopic = { topic -> completedTopics = completedTopics - topic },
                                projectsBuilt = projectsBuiltText,
                                onProjectsBuiltChange = { projectsBuiltText = it },
                                dsaExperience = dsaExperience,
                                onDsaExperienceChange = { dsaExperience = it },
                                devExperience = devExperience,
                                onDevExperienceChange = { devExperience = it },
                                aiExperience = aiExperience,
                                onAiExperienceChange = { aiExperience = it },
                                githubUrl = portfolioOrGithubUrl,
                                onGithubUrlChange = { portfolioOrGithubUrl = it },
                                confidenceLevel = confidenceLevel,
                                onConfidenceChange = { confidenceLevel = it },
                                diagnosticResult = diagnosticResult,
                                onOpenDiagnostic = { showDiagnosticModal = true }
                            )
                        }
                    }

                    3 -> {
                        item {
                            Step3GoalsSection(
                                selectedGoals = selectedGoals,
                                onToggleGoal = { goal ->
                                    selectedGoals = if (selectedGoals.contains(goal)) selectedGoals - goal else selectedGoals + goal
                                },
                                customGoal = customGoal,
                                onCustomGoalChange = { customGoal = it },
                                targetTimelineMonths = targetTimelineMonths,
                                onTimelineChange = { targetTimelineMonths = it },
                                preferredRoles = preferredRolesText,
                                onPreferredRolesChange = { preferredRolesText = it },
                                primaryPriority = primaryPriority,
                                onPriorityChange = { primaryPriority = it }
                            )
                        }
                    }

                    4 -> {
                        item {
                            Step4InterestsSection(
                                selectedInterests = selectedInterests,
                                onToggleInterest = { interest ->
                                    selectedInterests = if (selectedInterests.contains(interest)) selectedInterests - interest else selectedInterests + interest
                                }
                            )
                        }
                    }

                    5 -> {
                        item {
                            Step5ConstraintsSection(
                                availableHours = availableHoursPerWeek,
                                onHoursChange = { availableHoursPerWeek = it },
                                preferredSchedule = preferredSchedule,
                                onScheduleChange = { preferredSchedule = it },
                                budgetRange = budgetRangeInr,
                                onBudgetChange = { budgetRangeInr = it },
                                preferredLanguage = preferredLanguage,
                                onLanguageChange = { preferredLanguage = it },
                                deviceConstraints = deviceConstraints,
                                onDeviceChange = { deviceConstraints = it },
                                upcomingExams = upcomingExams,
                                onExamsChange = { upcomingExams = it },
                                selectedLearningStyles = selectedLearningStyles,
                                onToggleLearningStyle = { style ->
                                    selectedLearningStyles = if (selectedLearningStyles.contains(style)) selectedLearningStyles - style else selectedLearningStyles + style
                                },
                                commitments = existingCommitments,
                                onCommitmentsChange = { existingCommitments = it }
                            )
                        }
                    }

                    6 -> {
                        item {
                            Step6AiAnalysisSection(
                                analysis = activeAiAnalysis,
                                isAnalyzing = isAnalyzingProfile,
                                onRetryAnalysis = { viewModel.requestProfileAnalysis() }
                            )
                        }
                    }

                    7 -> {
                        item {
                            Step7PathSelectionSection(
                                selectedPathId = chosenPathId,
                                onSelectPath = { chosenPathId = it },
                                availableHours = availableHoursPerWeek
                            )
                        }
                    }
                }
            }
        }
    }

    // Diagnostic Assessment Dialog
    if (showDiagnosticModal) {
        DiagnosticAssessmentDialog(
            onDismiss = { showDiagnosticModal = false },
            onComplete = { result ->
                diagnosticResult = result
                showDiagnosticModal = false
            }
        )
    }
}

private fun getStepTitle(step: Int): String = when (step) {
    1 -> "Education & Academic Stage"
    2 -> "Current Technical Skills"
    3 -> "Career & Learning Goals"
    4 -> "Technical Interests"
    5 -> "Schedule & Constraints"
    6 -> "AI Profile & Diagnostic Analysis"
    7 -> "Career Path & Roadmap Selection"
    else -> "Onboarding"
}

// ==========================================
// STEP 1: EDUCATION COMPONENT
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step1EducationSection(
    qualification: String,
    onQualificationChange: (String) -> Unit,
    selectedStage: EducationStage,
    onStageChange: (EducationStage) -> Unit,
    fieldOfStudy: String,
    onFieldOfStudyChange: (String) -> Unit,
    currentYear: Int,
    onYearChange: (Int) -> Unit,
    currentSemester: Int,
    onSemesterChange: (Int) -> Unit,
    isCollegeApplicable: Boolean,
    onToggleCollegeApplicable: (Boolean) -> Unit,
    institution: String,
    onInstitutionChange: (String) -> Unit,
    curriculum: String,
    onCurriculumChange: (String) -> Unit,
    expectedGraduation: String,
    onGraduationChange: (String) -> Unit,
    studiedSubjects: List<String>,
    onAddSubject: (String) -> Unit,
    onRemoveSubject: (String) -> Unit
) {
    var customSubject by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Tell us about your educational background",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Shivaar adapts its roadmap milestones whether you are an undergraduate, self-learner, or career switcher.",
            style = MaterialTheme.typography.bodySmall,
            color = CharcoalTextSecondary
        )

        // Stage Selection
        Text(
            text = "Current Stage",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = CharcoalTextSecondary
        )

        EducationStage.entries.forEach { stage ->
            val isSelected = selectedStage == stage
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStageChange(stage) }
                    .testTag("stage_chip_${stage.name.lowercase()}"),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) IndigoContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onStageChange(stage) },
                        colors = RadioButtonDefaults.colors(selectedColor = IndigoPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stage.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) OnIndigoContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Degree / Qualification Input
        OutlinedTextField(
            value = qualification,
            onValueChange = onQualificationChange,
            label = { Text("Degree or Program (e.g. B.Tech, BCA, Self-Study)") },
            modifier = Modifier.fillMaxWidth().testTag("input_qualification"),
            shape = RoundedCornerShape(12.dp)
        )

        // Field / Department
        OutlinedTextField(
            value = fieldOfStudy,
            onValueChange = onFieldOfStudyChange,
            label = { Text("Field or Specialization (e.g. Computer Science, IT, Non-CS)") },
            modifier = Modifier.fillMaxWidth().testTag("input_field_of_study"),
            shape = RoundedCornerShape(12.dp)
        )

        // College Applicable Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Currently enrolled in university/college semesters?",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary
            )
            Switch(
                checked = isCollegeApplicable,
                onCheckedChange = onToggleCollegeApplicable
            )
        }

        if (isCollegeApplicable) {
            // Year & Semester Pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Year (1–4)", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..4).forEach { yr ->
                            FilterChip(
                                selected = currentYear == yr,
                                onClick = { onYearChange(yr) },
                                label = { Text("$yr") }
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1.5f)) {
                    Text("Semester (1–8)", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..4).forEach { sem ->
                            FilterChip(
                                selected = currentSemester == sem,
                                onClick = { onSemesterChange(sem) },
                                label = { Text("$sem") }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (5..8).forEach { sem ->
                            FilterChip(
                                selected = currentSemester == sem,
                                onClick = { onSemesterChange(sem) },
                                label = { Text("$sem") }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = institution,
                onValueChange = onInstitutionChange,
                label = { Text("Institution Name") },
                placeholder = { Text("e.g. Lloyd Institute of Engineering & Technology") },
                modifier = Modifier.fillMaxWidth().testTag("input_institution"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = curriculum,
                onValueChange = onCurriculumChange,
                label = { Text("Curriculum or Board") },
                placeholder = { Text("e.g. AKTU (Uttar Pradesh), Autonomous, CBSE") },
                modifier = Modifier.fillMaxWidth().testTag("input_curriculum"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        OutlinedTextField(
            value = expectedGraduation,
            onValueChange = onGraduationChange,
            label = { Text("Expected Completion Year (e.g. 2026, 2027, 2028)") },
            modifier = Modifier.fillMaxWidth().testTag("input_graduation_date"),
            shape = RoundedCornerShape(12.dp)
        )

        // Studied Subjects Tag Cloud
        Text(
            text = "Relevant Subjects Already Studied",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = CharcoalTextSecondary
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            studiedSubjects.forEach { sub ->
                InputChip(
                    selected = true,
                    onClick = { onRemoveSubject(sub) },
                    label = { Text(sub) },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customSubject,
                onValueChange = { customSubject = it },
                placeholder = { Text("Add subject (e.g. C, Python, Engineering Math)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    if (customSubject.isNotBlank()) {
                        onAddSubject(customSubject.trim())
                        customSubject = ""
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add")
            }
        }
    }
}

// ==========================================
// STEP 2: SKILLS & DIAGNOSTIC COMPONENT
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step2SkillsSection(
    selectedLanguages: Set<String>,
    onToggleLanguage: (String) -> Unit,
    completedTopics: List<String>,
    onAddTopic: (String) -> Unit,
    onRemoveTopic: (String) -> Unit,
    projectsBuilt: String,
    onProjectsBuiltChange: (String) -> Unit,
    dsaExperience: String,
    onDsaExperienceChange: (String) -> Unit,
    devExperience: String,
    onDevExperienceChange: (String) -> Unit,
    aiExperience: String,
    onAiExperienceChange: (String) -> Unit,
    githubUrl: String,
    onGithubUrlChange: (String) -> Unit,
    confidenceLevel: SkillConfidence,
    onConfidenceChange: (SkillConfidence) -> Unit,
    diagnosticResult: DiagnosticResult?,
    onOpenDiagnostic: () -> Unit
) {
    val commonLanguages = listOf("C", "C++", "Python", "Java", "JavaScript", "TypeScript", "Kotlin", "Go", "Rust", "SQL")
    var customTopic by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Current Technical Skills & Experience",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select what you already know. Shivaar respects your completed fundamentals and avoids repetitive beginner tasks.",
            style = MaterialTheme.typography.bodySmall,
            color = CharcoalTextSecondary
        )

        // Programming Languages
        Text("Programming Languages Known", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            commonLanguages.forEach { lang ->
                val isSelected = selectedLanguages.contains(lang)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleLanguage(lang) },
                    label = { Text(lang) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Completed Topics
        Text("Key Topics Completed", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            completedTopics.forEach { topic ->
                InputChip(
                    selected = true,
                    onClick = { onRemoveTopic(topic) },
                    label = { Text(topic) },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customTopic,
                onValueChange = { customTopic = it },
                placeholder = { Text("Add topic (e.g. Memory pointers, Recursion)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    if (customTopic.isNotBlank()) {
                        onAddTopic(customTopic.trim())
                        customTopic = ""
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add")
            }
        }

        // DSA Experience
        OutlinedTextField(
            value = dsaExperience,
            onValueChange = onDsaExperienceChange,
            label = { Text("Data Structures & Algorithms Background") },
            placeholder = { Text("e.g. Arrays, Strings, Basic Recursion; Solved 40 LeetCode problems") },
            modifier = Modifier.fillMaxWidth().testTag("input_dsa_exp"),
            shape = RoundedCornerShape(12.dp)
        )

        // Development Experience
        OutlinedTextField(
            value = devExperience,
            onValueChange = onDevExperienceChange,
            label = { Text("Development & Framework Experience") },
            placeholder = { Text("e.g. Built static HTML/CSS sites; basic React/FastAPI endpoints") },
            modifier = Modifier.fillMaxWidth().testTag("input_dev_exp"),
            shape = RoundedCornerShape(12.dp)
        )

        // AI / ML Experience
        OutlinedTextField(
            value = aiExperience,
            onValueChange = onAiExperienceChange,
            label = { Text("AI / ML Experience") },
            placeholder = { Text("e.g. Used LLM APIs, prompt engineering, or none yet") },
            modifier = Modifier.fillMaxWidth().testTag("input_ai_exp"),
            shape = RoundedCornerShape(12.dp)
        )

        // Projects Built
        OutlinedTextField(
            value = projectsBuilt,
            onValueChange = onProjectsBuiltChange,
            label = { Text("Notable Projects Built (comma separated)") },
            placeholder = { Text("e.g. CLI Student Record System, Portfolio Website") },
            modifier = Modifier.fillMaxWidth().testTag("input_projects_built"),
            shape = RoundedCornerShape(12.dp)
        )

        // Portfolio / GitHub Link
        OutlinedTextField(
            value = githubUrl,
            onValueChange = onGithubUrlChange,
            label = { Text("GitHub / Portfolio URL (Optional)") },
            placeholder = { Text("https://github.com/username") },
            modifier = Modifier.fillMaxWidth().testTag("input_github_url"),
            shape = RoundedCornerShape(12.dp)
        )

        // Self-Reported Confidence
        Text("Self-Reported Confidence", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        SkillConfidence.entries.forEach { conf ->
            val isSelected = confidenceLevel == conf
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConfidenceChange(conf) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) IndigoContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = { onConfidenceChange(conf) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = conf.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Optional Diagnostic Assessment Callout
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = "Diagnostic Assessment",
                        tint = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Optional 3-Minute Diagnostic Check",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Self-reported confidence is valuable context, but Shivaar does not treat claims as verified mastery. Take this quick 5-question check to verify your foundational baseline.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (diagnosticResult != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Diagnostic Score: ${diagnosticResult.totalScore} / ${diagnosticResult.maxScore} (${diagnosticResult.verifiedLevel})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessEmerald
                        )
                        OutlinedButton(
                            onClick = onOpenDiagnostic,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Retake Check")
                        }
                    }
                } else {
                    Button(
                        onClick = onOpenDiagnostic,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier.testTag("btn_take_diagnostic")
                    ) {
                        Text("Start Diagnostic Check (5 Questions)")
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 3: GOALS COMPONENT
// ==========================================

@Composable
private fun Step3GoalsSection(
    selectedGoals: Set<CareerGoal>,
    onToggleGoal: (CareerGoal) -> Unit,
    customGoal: String,
    onCustomGoalChange: (String) -> Unit,
    targetTimelineMonths: Int,
    onTimelineChange: (Int) -> Unit,
    preferredRoles: String,
    onPreferredRolesChange: (String) -> Unit,
    primaryPriority: String,
    onPriorityChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Define Your Career & Learning Goals",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select all applicable directions. Shivaar helps you focus on high-depth engineering fundamentals.",
            style = MaterialTheme.typography.bodySmall,
            color = CharcoalTextSecondary
        )

        // Multiple Goal Selection
        Text("Primary Direction(s)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

        CareerGoal.entries.forEach { goal ->
            val isSelected = selectedGoals.contains(goal)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleGoal(goal) }
                    .testTag("goal_item_${goal.name.lowercase()}"),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) IndigoContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleGoal(goal) },
                        colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = goal.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) OnIndigoContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = goal.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = CharcoalTextMuted
                        )
                    }
                }
            }
        }

        // Custom Goal Entry
        OutlinedTextField(
            value = customGoal,
            onValueChange = onCustomGoalChange,
            label = { Text("Custom Goal (Optional)") },
            placeholder = { Text("e.g. Build an autonomous compiler agent by semester 6") },
            modifier = Modifier.fillMaxWidth().testTag("input_custom_goal"),
            shape = RoundedCornerShape(12.dp)
        )

        // Target Timeline
        Text("Target Timeline: $targetTimelineMonths Months", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Slider(
            value = targetTimelineMonths.toFloat(),
            onValueChange = { onTimelineChange(it.toInt()) },
            valueRange = 6f..48f,
            steps = 6,
            modifier = Modifier.testTag("slider_timeline")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("6 Months", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
            Text("24 Months", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
            Text("48 Months (B.Tech)", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
        }

        // Preferred Target Roles
        OutlinedTextField(
            value = preferredRoles,
            onValueChange = onPreferredRolesChange,
            label = { Text("Preferred Roles or Job Titles") },
            placeholder = { Text("e.g. Backend Engineer, Systems Programmer, AI Engineer") },
            modifier = Modifier.fillMaxWidth().testTag("input_preferred_roles"),
            shape = RoundedCornerShape(12.dp)
        )

        // Top Priority
        OutlinedTextField(
            value = primaryPriority,
            onValueChange = onPriorityChange,
            label = { Text("Top Single Priority This Year") },
            placeholder = { Text("e.g. Master C & DSA fundamentals with 75%+ GPA") },
            modifier = Modifier.fillMaxWidth().testTag("input_primary_priority"),
            shape = RoundedCornerShape(12.dp)
        )

        // Ethical Transparency Notice
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = CharcoalTextMuted, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Shivaar develops genuine engineering competence. We never promise guaranteed salary figures or hiring placements.",
                    style = MaterialTheme.typography.labelSmall,
                    color = CharcoalTextSecondary
                )
            }
        }
    }
}

// ==========================================
// STEP 4: INTERESTS COMPONENT
// ==========================================

@Composable
private fun Step4InterestsSection(
    selectedInterests: Set<InterestArea>,
    onToggleInterest: (InterestArea) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "What engineering work do you truly enjoy?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Learning stickiness depends on working on problems you find intrinsically engaging. Multiple selections are encouraged.",
            style = MaterialTheme.typography.bodySmall,
            color = CharcoalTextSecondary
        )

        InterestArea.entries.forEach { interest ->
            val isSelected = selectedInterests.contains(interest)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleInterest(interest) }
                    .testTag("interest_card_${interest.name.lowercase()}"),
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) IndigoContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleInterest(interest) },
                        colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = interest.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) OnIndigoContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = interest.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) IndigoPrimary else CharcoalTextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 5: CONSTRAINTS COMPONENT
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step5ConstraintsSection(
    availableHours: Int,
    onHoursChange: (Int) -> Unit,
    preferredSchedule: String,
    onScheduleChange: (String) -> Unit,
    budgetRange: String,
    onBudgetChange: (String) -> Unit,
    preferredLanguage: String,
    onLanguageChange: (String) -> Unit,
    deviceConstraints: String,
    onDeviceChange: (String) -> Unit,
    upcomingExams: String,
    onExamsChange: (String) -> Unit,
    selectedLearningStyles: Set<String>,
    onToggleLearningStyle: (String) -> Unit,
    commitments: String,
    onCommitmentsChange: (String) -> Unit
) {
    val commonStyles = listOf("Project-based learning", "Interactive coding exercises", "Official documentation & reading", "Video tutorials + written notes")
    val schedules = listOf("Evenings & Weekends", "Early Mornings", "Distributed Daily (2-3 hrs)", "Weekends Heavy")
    val budgets = listOf("Free resources only (₹0)", "Low-cost essentials (₹1,000–₹5,000)", "Flexible / Paid subscriptions okay")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Your Practical Constraints & Availability",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Honest time and resource constraints prevent unrealistic, demotivating backlogs.",
            style = MaterialTheme.typography.bodySmall,
            color = CharcoalTextSecondary
        )

        // Weekly Study Hours
        Text(
            text = "Available Study Hours Per Week: $availableHours hrs/week",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Slider(
            value = availableHours.toFloat(),
            onValueChange = { onHoursChange(it.toInt()) },
            valueRange = 5f..40f,
            steps = 6,
            modifier = Modifier.testTag("slider_weekly_hours")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("5 hrs (Light)", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
            Text("20 hrs (Recommended)", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
            Text("40 hrs (Intensive)", style = MaterialTheme.typography.labelSmall, color = CharcoalTextMuted)
        }

        // Preferred Schedule
        Text("Preferred Schedule", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        schedules.forEach { sched ->
            val isSelected = preferredSchedule == sched
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onScheduleChange(sched) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) IndigoContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SurfaceCardBorder)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onScheduleChange(sched) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = sched, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Budget Range
        Text("Budget Range (Course materials, certifications, tools)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        budgets.forEach { b ->
            val isSelected = budgetRange == b
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBudgetChange(b) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) IndigoContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SurfaceCardBorder)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onBudgetChange(b) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = b, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Preferred Language
        OutlinedTextField(
            value = preferredLanguage,
            onValueChange = onLanguageChange,
            label = { Text("Preferred Language of Instruction") },
            placeholder = { Text("e.g. English, Bilingual / Hinglish for technical concepts") },
            modifier = Modifier.fillMaxWidth().testTag("input_language"),
            shape = RoundedCornerShape(12.dp)
        )

        // Device & Internet Constraints
        OutlinedTextField(
            value = deviceConstraints,
            onValueChange = onDeviceChange,
            label = { Text("Device & Computing Constraints") },
            placeholder = { Text("e.g. Standard 8GB RAM Laptop, Linux/Windows; regular broadband") },
            modifier = Modifier.fillMaxWidth().testTag("input_device_constraints"),
            shape = RoundedCornerShape(12.dp)
        )

        // Upcoming Examinations or Deadlines
        OutlinedTextField(
            value = upcomingExams,
            onValueChange = onExamsChange,
            label = { Text("Upcoming Examinations or Key Deadlines") },
            placeholder = { Text("e.g. AKTU Semester End-Term exams in December") },
            modifier = Modifier.fillMaxWidth().testTag("input_upcoming_exams"),
            shape = RoundedCornerShape(12.dp)
        )

        // Learning Styles
        Text("Preferred Learning Styles", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            commonStyles.forEach { style ->
                val isSelected = selectedLearningStyles.contains(style)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleLearningStyle(style) },
                    label = { Text(style) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Commitments
        OutlinedTextField(
            value = commitments,
            onValueChange = onCommitmentsChange,
            label = { Text("Existing Daily Commitments") },
            placeholder = { Text("e.g. College attendance 9 AM to 4 PM, travel time 1 hr") },
            modifier = Modifier.fillMaxWidth().testTag("input_commitments"),
            shape = RoundedCornerShape(12.dp)
        )

        // Privacy & Transparency Note
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = IndigoContainer.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = IndigoPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Data Transparency & Control",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnIndigoContainer
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Shivaar uses your answers solely to generate your personal schedule and project prerequisites. You can view, edit, or delete any answer at any time in Settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                }
            }
        }
    }
}

// ==========================================
// OPTIONAL DIAGNOSTIC ASSESSMENT MODAL
// ==========================================

@Composable
private fun DiagnosticAssessmentDialog(
    onDismiss: () -> Unit,
    onComplete: (DiagnosticResult) -> Unit
) {
    val questions = remember {
        listOf(
            DiagnosticQuestion(
                id = "q1",
                question = "In C, what does the expression *(ptr + 2) do?",
                options = listOf(
                    "Increments the pointer address by 2 bytes",
                    "Dereferences the element located 2 element offsets ahead of ptr",
                    "Multiplies the address by 2",
                    "Throws a segmentation fault always"
                ),
                correctOptionIndex = 1,
                explanation = "Pointer arithmetic in C automatically multiplies the offset by sizeof(*ptr). *(ptr + 2) accesses the 3rd element in the buffer.",
                topic = "C Memory & Pointers"
            ),
            DiagnosticQuestion(
                id = "q2",
                question = "What is the average time complexity of searching a key in a balanced Binary Search Tree (BST)?",
                options = listOf("O(1)", "O(log N)", "O(N)", "O(N log N)"),
                correctOptionIndex = 1,
                explanation = "In a balanced BST (like AVL or Red-Black Tree), the tree height is O(log N), allowing logarithmic search operations.",
                topic = "DSA"
            ),
            DiagnosticQuestion(
                id = "q3",
                question = "What is the difference between Stack and Heap memory allocation?",
                options = listOf(
                    "Stack memory is manually allocated with malloc; Heap is automatic",
                    "Stack memory is managed automatically via call frames; Heap is dynamically allocated at runtime",
                    "Stack has unlimited size; Heap is strictly bounded to 1MB",
                    "They are identical concepts with different compiler names"
                ),
                correctOptionIndex = 1,
                explanation = "Stack memory allocation is fast and tied to function scopes; Heap memory is allocated dynamically (e.g. malloc/new) and persists until explicitly freed.",
                topic = "Systems & Memory"
            ),
            DiagnosticQuestion(
                id = "q4",
                question = "Which HTTP method should be used for an idempotent resource update where the client sends the complete representation?",
                options = listOf("POST", "PUT", "PATCH", "DELETE"),
                correctOptionIndex = 1,
                explanation = "PUT is designed to be idempotent: replacing the target resource with the uploaded payload produces the same state regardless of how many times it is called.",
                topic = "Web & APIs"
            ),
            DiagnosticQuestion(
                id = "q5",
                question = "In relational databases, what does the ACID property 'Atomicity' guarantee?",
                options = listOf(
                    "Transactions execute instantaneously with zero latency",
                    "All statements within a transaction succeed completely, or none take effect",
                    "Data is replicated across at least three physical disks",
                    "Every table has an auto-incrementing integer key"
                ),
                correctOptionIndex = 1,
                explanation = "Atomicity ensures that a multi-statement transaction is treated as a single indivisible unit: either all changes commit, or the entire transaction rolls back.",
                topic = "Database Systems"
            )
        )
    }

    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var submitted by remember { mutableStateOf(false) }

    val score = remember(submitted) {
        if (!submitted) 0
        else questions.mapIndexed { idx, q -> if (selectedAnswers[idx] == q.correctOptionIndex) 1 else 0 }.sum()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (!submitted) "Technical Diagnostic Check" else "Diagnostic Verified Score",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!submitted) {
                    item {
                        Text(
                            text = "Answer these 5 core questions to establish your baseline. We do not judge—we customize your personal roadmap based on verified competence.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalTextSecondary
                        )
                    }

                    items(questions.size) { idx ->
                        val q = questions[idx]
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Q${idx + 1}. [${q.topic}] ${q.question}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            q.options.forEachIndexed { optIdx, opt ->
                                val isSelected = selectedAnswers[idx] == optIdx
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedAnswers = HashMap(selectedAnswers).apply { put(idx, optIdx) }
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) IndigoContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, if (isSelected) IndigoPrimary else SurfaceCardBorder)
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = isSelected, onClick = {
                                            selectedAnswers = HashMap(selectedAnswers).apply { put(idx, optIdx) }
                                        })
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = opt, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(if (score >= 3) SuccessEmeraldContainer else WarningAmberContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$score/5",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (score >= 3) SuccessEmerald else WarningAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (score >= 4) "Solid Foundational Competence"
                                else if (score >= 2) "Intermediate Potential with Memory/System Gaps"
                                else "Early Stage — Core Fundamentals Prioritized",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "This verified score has been saved to your profile and will calibrate your personalized roadmap milestones.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CharcoalTextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!submitted) {
                Button(
                    onClick = {
                        submitted = true
                    },
                    enabled = selectedAnswers.size == questions.size,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Submit Check")
                }
            } else {
                Button(
                    onClick = {
                        val level = if (score >= 4) "Advanced Foundation" else if (score >= 2) "Intermediate" else "Foundational"
                        onComplete(
                            DiagnosticResult(
                                totalScore = score,
                                maxScore = questions.size,
                                verifiedLevel = level,
                                summaryFeedback = "Verified diagnostic baseline: $score/5."
                            )
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald)
                ) {
                    Text("Save to Profile")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (!submitted) "Cancel" else "Close")
            }
        }
    )
}

// ==========================================
// STEP 6: AI PROFILE ANALYSIS COMPONENT
// ==========================================

@Composable
private fun Step6AiAnalysisSection(
    analysis: AiProfileAnalysis?,
    isAnalyzing: Boolean,
    onRetryAnalysis: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.testTag("step6_ai_analysis_section")
    ) {
        // AI Mentor Header Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, SurfaceCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IndigoContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gemini 2.5 Pro Career Architect",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Grounded Profile & Diagnostics Synthesis",
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalTextSecondary
                            )
                        }
                    }

                    if (!isAnalyzing) {
                        IconButton(
                            onClick = onRetryAnalysis,
                            modifier = Modifier.testTag("btn_refresh_ai_analysis")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate Analysis",
                                tint = IndigoPrimary
                            )
                        }
                    }
                }
            }
        }

        if (isAnalyzing) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCardSubtle,
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = IndigoPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Synthesizing your engineering baseline...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Analyzing academic curriculum, diagnostic verification, target timeline, and available hours...",
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else if (analysis != null) {
            // Starting Level Badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IndigoContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Assessed Baseline Level",
                            style = MaterialTheme.typography.labelSmall,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = analysis.currentStartingLevel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Strengths Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SuccessEmeraldContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verified Strengths & Competencies",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SuccessEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    analysis.existingStrengths.forEach { str ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text("• ", color = SuccessEmerald, fontWeight = FontWeight.Bold)
                            Text(text = str, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Knowledge Gaps Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = WarningAmberContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Identified Knowledge Gaps & Crucial Prerequisites",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    (analysis.knowledgeGaps + analysis.relevantPrerequisites).distinct().forEach { gap ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text("⚠️ ", fontSize = 12.sp)
                            Text(text = gap, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Feasibility Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Goal Feasibility Assessment",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = analysis.goalFeasibilityAnalysis,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceCardSubtle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Calibrated Weekly Study:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text("${analysis.expectedWeeklyEffortHours} Hours/Week", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = IndigoPrimary)
                        }
                    }

                    if (analysis.tradeoffsAndDisadvantages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Anticipated Trade-offs & Challenges:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        analysis.tradeoffsAndDisadvantages.forEach { t ->
                            Text(text = "• $t", style = MaterialTheme.typography.bodySmall, color = CharcoalTextSecondary)
                        }
                    }
                }
            }

            // Next Immediate Step Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = IndigoPrimary.copy(alpha = 0.08f),
                border = BorderStroke(1.5.dp, IndigoPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recommended Next Immediate Step",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = analysis.recommendedNextImmediateStep,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Transparency Disclosure
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCardSubtle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "AI Transparency Disclosure",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Verified Curriculum: ${analysis.verifiedInfoSummary}\nSelf-Reported: ${analysis.userProvidedInfoSummary}\nAssumptions: ${analysis.aiAssumptionsSummary}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// STEP 7: PATH SELECTION & COMPARISON COMPONENT
// ==========================================

@Composable
private fun Step7PathSelectionSection(
    selectedPathId: String,
    onSelectPath: (String) -> Unit,
    availableHours: Int
) {
    val paths = listOf(
        PathOptionDisplay(
            id = "path_fullstack_cloud",
            title = "Full-Stack Software Development & Cloud",
            subtitle = "Modern Web, Scalable Backend APIs, PostgreSQL & Cloud-Native Deployment",
            hours = 650,
            recommendedBadge = "Highest Job Volume",
            suits = "Ideal for shipping complete production applications, web platforms, and startup MVPs with end-to-end autonomy.",
            techStack = listOf("TypeScript", "React 19", "Node/Express", "PostgreSQL", "Docker", "AWS / VPS"),
            project = "Multi-Tenant SaaS Platform with Stripe Billing & Live Analytics",
            advantages = listOf("Fastest path to interview-ready portfolio", "High industry demand across product companies"),
            challenges = listOf("Fast-moving ecosystem requiring disciplined stack selection")
        ),
        PathOptionDisplay(
            id = "path_systems_dsa",
            title = "Systems Programming & High-Performance Core DSA",
            subtitle = "C/C++20, Memory Models, Linux POSIX Internals & Algorithmic Depth",
            hours = 750,
            recommendedBadge = "Elite Tech & Quant",
            suits = "Ideal for low-level systems, kernel development, databases, high-frequency trading, and top-tier product SWE.",
            techStack = listOf("C/C++20", "POSIX Sockets", "epoll", "GDB", "Valgrind", "Advanced DSA"),
            project = "In-Memory Key-Value Storage Engine with WAL and Snapshot Isolation",
            advantages = listOf("Deep architectural understanding of hardware & OS", "Decisive edge in rigorous coding rounds"),
            challenges = listOf("Steeper learning curve with manual memory management")
        ),
        PathOptionDisplay(
            id = "path_agentic_ai",
            title = "Agentic AI & Autonomous Systems Engineering",
            subtitle = "Python AsyncIO, Gemini 2.5 API, Multi-Agent LangGraph & Enterprise RAG",
            hours = 600,
            recommendedBadge = "Frontier Specialization",
            suits = "Ideal for engineers building autonomous agent workflows, LLM tool pipelines, and modern intelligent automation.",
            techStack = listOf("Python 3.12", "AsyncIO", "Gemini Pro API", "LangGraph", "Vector DBs", "RAG"),
            project = "Autonomous Multi-Agent Code Reviewer with Self-Correction Loops",
            advantages = listOf("Cutting-edge technology with explosive market interest", "Direct leverage of modern AI APIs"),
            challenges = listOf("Requires solid programming fundamentals before agent abstractions")
        ),
        PathOptionDisplay(
            id = "path_custom_exploration",
            title = "Custom & Exploratory Track",
            subtitle = "Curated Hybrid Curriculum — CS Fundamentals, Algorithms & Web Basics",
            hours = 550,
            recommendedBadge = "Flexible Pace",
            suits = "Ideal for students sampling multiple domains before committing to a single career specialty.",
            techStack = listOf("C/C++", "Python", "Data Structures", "Web Basics", "Operating Systems"),
            project = "Modular Foundation Projects across Systems, Web, and Algorithms",
            advantages = listOf("Maximum flexibility to pivot later", "Strong academic alignment"),
            challenges = listOf("Requires deliberate specialization in later semesters")
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.testTag("step7_path_selection_section")
    ) {
        Column {
            Text(
                text = "Choose Your Engineering Specialization",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your personalized roadmap milestones, weekly project targets, and daily tasks will be synthesized based on this choice.",
                style = MaterialTheme.typography.bodySmall,
                color = CharcoalTextSecondary
            )
        }

        paths.forEach { path ->
            val isSelected = selectedPathId == path.id
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) IndigoContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) IndigoPrimary else SurfaceCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPath(path.id) }
                    .testTag("path_card_${path.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = path.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = path.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectPath(path.id) },
                            colors = RadioButtonDefaults.colors(selectedColor = IndigoPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IndigoContainer
                        ) {
                            Text(
                                text = path.recommendedBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = SurfaceCardSubtle
                        ) {
                            Text(
                                text = "Est. ${path.hours} Total Hours (~$availableHours hrs/wk)",
                                style = MaterialTheme.typography.labelSmall,
                                color = CharcoalTextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = path.suits,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tech Stack: ${path.techStack.joinToString(" • ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalTextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Capstone: ${path.project}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private data class PathOptionDisplay(
    val id: String,
    val title: String,
    val subtitle: String,
    val hours: Int,
    val recommendedBadge: String,
    val suits: String,
    val techStack: List<String>,
    val project: String,
    val advantages: List<String>,
    val challenges: List<String>
)
