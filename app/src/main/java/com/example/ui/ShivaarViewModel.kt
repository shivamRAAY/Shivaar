package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ShivaarRepository
import com.example.data.model.*
import com.example.service.GeminiMentorService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppDestination(val label: String) {
    TODAY("Today"),
    ROADMAP("Roadmap"),
    EXPLORE_ROADMAPS("Explore Roadmaps"),
    ONBOARDING("Onboarding"),
    ACADEMICS("Academics"),
    CODING("Coding & DSA"),
    PROJECTS("Projects"),
    CAREER("Career"),
    MENTOR("AI Mentor"),
    RESEARCH("Research"),
    ANALYTICS("Analytics"),
    SETTINGS("Settings")
}

data class FocusSessionState(
    val isActive: Boolean = false,
    val task: TaskItem? = null,
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false
)

class ShivaarViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: ShivaarRepository = ShivaarRepository(application),
    private val mentorService: GeminiMentorService = GeminiMentorService()
) : AndroidViewModel(application) {

    private val _currentTab = MutableStateFlow(AppDestination.TODAY)
    val currentTab: StateFlow<AppDestination> = _currentTab.asStateFlow()

    val tasks: StateFlow<List<TaskItem>> = repository.tasks
    val tomorrowTasks: StateFlow<List<TaskItem>> = repository.tomorrowTasks
    val completionHistory: StateFlow<List<TaskCompletionRecord>> = repository.completionHistory
    val roadmapStages: StateFlow<List<RoadmapStage>> = repository.roadmapStages
    val roadmapMonths: StateFlow<List<RoadmapMonth>> = repository.roadmapMonths
    val prerequisiteGates: StateFlow<List<PrerequisiteGate>> = repository.prerequisiteGates
    val subjects: StateFlow<List<SubjectItem>> = repository.subjects
    val dsaProblems: StateFlow<List<DsaProblem>> = repository.dsaProblems
    val projects: StateFlow<List<ProjectItem>> = repository.projects
    val careerApplications: StateFlow<List<CareerApplication>> = repository.careerApplications
    val researchSources: StateFlow<List<ResearchSource>> = repository.researchSources
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val onboardingData: StateFlow<OnboardingData> = repository.onboardingData
    val publicRoadmaps: StateFlow<List<PublicRoadmap>> = repository.publicRoadmaps
    val savedRoadmapIds: StateFlow<Set<String>> = repository.savedRoadmapIds
    val followedRoadmapIds: StateFlow<Set<String>> = repository.followedRoadmapIds
    val adoptedRoadmapId: StateFlow<String?> = repository.adoptedRoadmapId
    val milestoneCustomNotes: StateFlow<Map<String, String>> = repository.milestoneCustomNotes
    val milestoneCompletedExercises: StateFlow<Map<String, List<Int>>> = repository.milestoneCompletedExercises
    val syncStatus: StateFlow<SyncStatus> = repository.syncStatus

    init {
        try {
            Firebase.auth.addAuthStateListener { auth ->
                if (auth.currentUser != null) {
                    repository.attachFirestoreSync(viewModelScope)
                } else {
                    repository.detachFirestoreSync()
                }
            }
            if (Firebase.auth.currentUser != null) {
                repository.attachFirestoreSync(viewModelScope)
            }
        } catch (e: Exception) {
            // FirebaseApp uninitialized in isolated testing or offline setup
        }
    }

    // Active Focus Session State
    private val _focusSession = MutableStateFlow(FocusSessionState())
    val focusSession: StateFlow<FocusSessionState> = _focusSession.asStateFlow()
    private var timerJob: Job? = null

    // AI Mentor Chat
    private val _chatMessages = MutableStateFlow(listOf(
        ChatMessage(
            id = "intro_msg",
            sender = MessageSender.MENTOR,
            text = "Namaste Shivam! I am Shivaar Intelligence, your personal academic and career companion for B.Tech CSE at Lloyd / AKTU.\n\nToday's key focus: Completing C pointer exercises and tackling AKTU Python Unit 2 control loops.\n\nHow can I help you excel right now?",
            suggestedPrompt = "Explain C pointers in simple Hinglish"
        )
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isMentorThinking = MutableStateFlow(false)
    val isMentorThinking: StateFlow<Boolean> = _isMentorThinking.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // Global Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Derived stats
    val completedTasksCount = tasks.map { list -> list.count { it.status == TaskStatus.COMPLETED } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeTasksCount = tasks.map { list -> list.count { it.status != TaskStatus.SKIPPED } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPlannedMinutes = tasks.map { list ->
        list.filter { it.status != TaskStatus.SKIPPED }.sumOf { it.durationMinutes }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 150)

    val completedMinutes = tasks.map { list ->
        list.filter { it.status == TaskStatus.COMPLETED }.sumOf {
            if (it.actualMinutes > 0) it.actualMinutes else it.durationMinutes
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val progressPercentage = combine(completedMinutes, userProfile) { completed, profile ->
        if (profile.dailyBudgetMinutes > 0) {
            ((completed.toFloat() / profile.dailyBudgetMinutes.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun navigateTo(tab: AppDestination) {
        _currentTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Task Actions
    fun markTaskComplete(taskId: String, notes: String = "", minutesSpent: Int = 0) {
        repository.updateTaskStatus(taskId, TaskStatus.COMPLETED, notes, minutesSpent)
        // If focus session active for this task, finish it
        if (_focusSession.value.task?.id == taskId) {
            timerJob?.cancel()
            _focusSession.value = FocusSessionState(isActive = false)
        }
    }

    fun markTaskInProgress(taskId: String) {
        repository.updateTaskStatus(taskId, TaskStatus.IN_PROGRESS)
    }

    fun markTaskPaused(taskId: String) {
        repository.markTaskPaused(taskId)
        if (_focusSession.value.task?.id == taskId) {
            timerJob?.cancel()
            _focusSession.update { it.copy(isPaused = true) }
        }
    }

    fun markTaskSkipped(taskId: String, reason: String = "") {
        repository.markTaskSkipped(taskId, reason)
        if (_focusSession.value.task?.id == taskId) {
            timerJob?.cancel()
            _focusSession.value = FocusSessionState(isActive = false)
        }
    }

    fun restoreTask(taskId: String) {
        repository.restoreTask(taskId)
    }

    fun updateTaskNotes(taskId: String, notes: String) {
        repository.updateTaskNotes(taskId, notes)
    }

    fun rescheduleTask(taskId: String, toTomorrow: Boolean) {
        repository.rescheduleTask(taskId, toTomorrow)
        if (_focusSession.value.task?.id == taskId) {
            timerJob?.cancel()
            _focusSession.value = FocusSessionState(isActive = false)
        }
    }

    fun moveTomorrowTaskToToday(taskId: String) {
        repository.moveTomorrowTaskToToday(taskId)
    }

    fun addNewTask(title: String, category: TaskCategory, duration: Int, priority: Priority, why: String, dod: String) {
        repository.addNewTask(title, category, duration, priority, why, dod)
    }

    // Focus Session Timer
    fun startFocusSession(task: TaskItem) {
        _focusSession.value = FocusSessionState(
            isActive = true,
            task = task,
            elapsedSeconds = 0,
            isPaused = false
        )
        markTaskInProgress(task.id)
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_focusSession.value.isActive && !_focusSession.value.isPaused) {
                delay(1000)
                _focusSession.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun togglePauseFocusSession() {
        val currentlyPaused = _focusSession.value.isPaused
        val activeTask = _focusSession.value.task
        _focusSession.update { it.copy(isPaused = !currentlyPaused) }

        if (currentlyPaused) {
            // Resuming
            if (activeTask != null) {
                repository.updateTaskStatus(activeTask.id, TaskStatus.IN_PROGRESS)
            }
            startTimer()
        } else {
            // Pausing
            timerJob?.cancel()
            if (activeTask != null) {
                repository.markTaskPaused(activeTask.id)
            }
        }
    }

    fun completeFocusSession(notes: String) {
        timerJob?.cancel()
        val current = _focusSession.value
        val task = current.task
        if (task != null) {
            val minutesSpent = maxOf(1, current.elapsedSeconds / 60)
            markTaskComplete(task.id, notes, minutesSpent)

            val session = StudySessionItem(
                id = "sess_${System.currentTimeMillis()}_${task.id}",
                userId = try { Firebase.auth.currentUser?.uid ?: "" } catch (e: Exception) { "" },
                taskId = task.id,
                taskTitle = task.title,
                startedAt = System.currentTimeMillis() - (current.elapsedSeconds * 1000L),
                endedAt = System.currentTimeMillis(),
                elapsedSeconds = current.elapsedSeconds,
                notes = notes,
                completed = true
            )
            repository.saveStudySession(session)
        }
        _focusSession.value = FocusSessionState(isActive = false)
    }

    fun retrySync() {
        try {
            if (Firebase.auth.currentUser != null) {
                repository.attachFirestoreSync(viewModelScope)
            }
        } catch (e: Exception) {}
    }

    fun cancelFocusSession() {
        timerJob?.cancel()
        val task = _focusSession.value.task
        if (task != null) {
            repository.markTaskPaused(task.id)
        }
        _focusSession.value = FocusSessionState(isActive = false)
    }

    // Academics Actions
    fun toggleSubjectTopic(subjectId: String, topic: String) {
        repository.toggleSubjectTopic(subjectId, topic)
    }

    fun logAttendance(subjectId: String, attended: Boolean) {
        repository.incrementAttendance(subjectId, attended)
    }

    // DSA Actions
    fun updateDsaProblem(problemId: String, status: ProblemStatus, notes: String, snippet: String) {
        repository.updateDsaProblemStatus(problemId, status, notes, snippet)
    }

    // Project Actions
    fun toggleProjectFeature(projectId: String, featureIndex: Int) {
        repository.toggleProjectFeature(projectId, featureIndex)
    }

    // Career Actions
    fun updateApplicationStatus(appId: String, newStatus: ApplicationStatus) {
        repository.updateApplicationStatus(appId, newStatus)
    }

    // Research Library
    private val _isResearchSearching = MutableStateFlow(false)
    val isResearchSearching: StateFlow<Boolean> = _isResearchSearching.asStateFlow()

    private val _researchStatusMessage = MutableStateFlow<String?>(null)
    val researchStatusMessage: StateFlow<String?> = _researchStatusMessage.asStateFlow()

    fun searchResearchLibrary(query: String) {
        if (query.isBlank()) return
        _isResearchSearching.value = true
        _researchStatusMessage.value = null
        viewModelScope.launch {
            val (sources, success) = mentorService.queryResearchLibrary(query, _customApiKey.value)
            _isResearchSearching.value = false
            if (success && sources.isNotEmpty()) {
                repository.addResearchSources(sources)
                _researchStatusMessage.value = "Retrieved ${sources.size} live verified source${if (sources.size > 1) "s" else ""} for \"$query\""
            } else {
                _researchStatusMessage.value = "Google Search grounding restricted or zero results. Showing verified syllabus & documentation reference catalog."
            }
        }
    }

    fun toggleResearchBookmark(sourceId: String) {
        repository.toggleResearchBookmark(sourceId)
    }

    // AI Mentor Actions
    fun sendMentorMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = userText
        )
        _chatMessages.update { it + userMsg }
        _isMentorThinking.value = true

        viewModelScope.launch {
            val response = mentorService.askMentor(userText, _customApiKey.value)
            _isMentorThinking.value = false
            val mentorReply = ChatMessage(
                id = "reply_${System.currentTimeMillis()}",
                sender = MessageSender.MENTOR,
                text = response.answerText,
                sources = response.sources,
                citations = response.citations,
                isGrounded = response.isGrounded,
                isServiceUnavailable = !response.isServiceAvailable,
                errorMessage = response.errorMessage
            )
            _chatMessages.update { it + mentorReply }
        }
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    // 48-Month Roadmap Actions
    fun toggleMonthCompletion(monthId: String) {
        repository.toggleMonthCompletion(monthId)
    }

    fun setActiveMonth(monthNumber: Int) {
        repository.setActiveMonth(monthNumber)
    }

    fun updateGateStatus(gateId: String, status: GateStatus) {
        repository.updateGateStatus(gateId, status)
    }

    fun generateTasksForMonth(month: RoadmapMonth) {
        repository.generateDailyTasksFromMonth(month)
    }

    fun updateUserProfile(updated: UserProfile) {
        repository.updateUserProfile(updated)
    }

    // Shivaar 2.0 Actions
    fun saveOnboardingProgress(data: OnboardingData) {
        repository.saveOnboardingProgress(data)
    }

    fun toggleSaveRoadmap(roadmapId: String) {
        repository.toggleSaveRoadmap(roadmapId)
    }

    fun toggleFollowRoadmap(roadmapId: String) {
        repository.toggleFollowRoadmap(roadmapId)
    }

    // AI Analysis & Path Choice Actions (Phase 3)
    private val _aiAnalysisState = MutableStateFlow<AiProfileAnalysis?>(null)
    val aiAnalysisState: StateFlow<AiProfileAnalysis?> = _aiAnalysisState.asStateFlow()

    private val _isAnalyzingProfile = MutableStateFlow(false)
    val isAnalyzingProfile: StateFlow<Boolean> = _isAnalyzingProfile.asStateFlow()

    fun requestProfileAnalysis(onComplete: (AiProfileAnalysis) -> Unit = {}) {
        viewModelScope.launch {
            _isAnalyzingProfile.value = true
            try {
                val analysis = mentorService.generateProfileAnalysis(onboardingData.value, _customApiKey.value)
                _aiAnalysisState.value = analysis
                val updatedOnboarding = onboardingData.value.copy(aiAnalysis = analysis)
                saveOnboardingProgress(updatedOnboarding)
                onComplete(analysis)
            } finally {
                _isAnalyzingProfile.value = false
            }
        }
    }

    fun choosePath(pathId: String) {
        val current = onboardingData.value
        val updated = current.copy(chosenPathId = pathId, isCompleted = true, completedAt = System.currentTimeMillis())
        saveOnboardingProgress(updated)
        repository.generateAndApplyPersonalizedRoadmap(updated)
    }

    fun generateAndApplyPersonalizedRoadmap() {
        repository.generateAndApplyPersonalizedRoadmap(onboardingData.value)
    }

    fun advanceToNextMilestone(currentMonthId: String) {
        repository.advanceToNextMilestone(currentMonthId)
    }

    fun triggerAdaptiveRecovery() {
        repository.triggerAdaptiveRecovery()
    }

    // Phase 6 Actions
    fun adoptPublicRoadmap(roadmapId: String, autoSpawnTasks: Boolean = true) {
        repository.adoptPublicRoadmap(roadmapId, autoSpawnTasks)
    }

    fun updateMilestoneCustomNotes(monthId: String, notes: String) {
        repository.updateMilestoneCustomNotes(monthId, notes)
    }

    fun toggleMilestoneExercise(monthId: String, exerciseIndex: Int) {
        repository.toggleMilestoneExercise(monthId, exerciseIndex)
    }

    fun getCurriculumExportMarkdown(): String {
        return repository.getCurriculumExportMarkdown()
    }
}
