package com.example.data

import android.content.Context
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShivaarRepository(
    private val context: Context? = null,
    val firestoreRepo: ShivaarFirestoreRepository? = try {
        context?.let { ctx ->
            val dbId = ctx.getString(com.example.R.string.firestore_database_id)
            val db = FirebaseFirestore.getInstance(dbId)
            ShivaarFirestoreRepository(db, Firebase.auth)
        }
    } catch (e: Exception) {
        null
    }
) {

    private val storage = ShivaarStorage(context)
    private var coroutineScope: CoroutineScope? = null
    private var syncJob: Job? = null

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _tasks = MutableStateFlow(storage.loadTasks() ?: getInitialTasks())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _tomorrowTasks = MutableStateFlow(storage.loadTomorrowTasks() ?: getTomorrowTasks())
    val tomorrowTasks: StateFlow<List<TaskItem>> = _tomorrowTasks.asStateFlow()

    private val _completionHistory = MutableStateFlow(storage.loadCompletionHistory() ?: emptyList())
    val completionHistory: StateFlow<List<TaskCompletionRecord>> = _completionHistory.asStateFlow()

    private val _roadmapStages = MutableStateFlow(getInitialRoadmap())
    val roadmapStages: StateFlow<List<RoadmapStage>> = _roadmapStages.asStateFlow()

    private val _roadmapMonths = MutableStateFlow(
        RoadmapYear1Data.getMonths() +
        RoadmapYear2Data.getMonths() +
        RoadmapYear3Data.getMonths() +
        RoadmapYear4Data.getMonths()
    )
    val roadmapMonths: StateFlow<List<RoadmapMonth>> = _roadmapMonths.asStateFlow()

    private val _prerequisiteGates = MutableStateFlow(RoadmapGatesData.getInitialGates())
    val prerequisiteGates: StateFlow<List<PrerequisiteGate>> = _prerequisiteGates.asStateFlow()

    private val _subjects = MutableStateFlow(getInitialSubjects())
    val subjects: StateFlow<List<SubjectItem>> = _subjects.asStateFlow()

    private val _dsaProblems = MutableStateFlow(getInitialDsaProblems())
    val dsaProblems: StateFlow<List<DsaProblem>> = _dsaProblems.asStateFlow()

    private val _projects = MutableStateFlow(getInitialProjects())
    val projects: StateFlow<List<ProjectItem>> = _projects.asStateFlow()

    private val _careerApplications = MutableStateFlow(getInitialCareerApplications())
    val careerApplications: StateFlow<List<CareerApplication>> = _careerApplications.asStateFlow()

    private val _researchSources = MutableStateFlow(getInitialResearchSources())
    val researchSources: StateFlow<List<ResearchSource>> = _researchSources.asStateFlow()

    private val _userProfile = MutableStateFlow(storage.loadUserProfile() ?: UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _onboardingData = MutableStateFlow(storage.loadOnboardingData() ?: OnboardingData())
    val onboardingData: StateFlow<OnboardingData> = _onboardingData.asStateFlow()

    private val _savedRoadmapIds = MutableStateFlow(storage.loadSavedRoadmapIds())
    val savedRoadmapIds: StateFlow<Set<String>> = _savedRoadmapIds.asStateFlow()

    private val _followedRoadmapIds = MutableStateFlow(storage.loadFollowedRoadmapIds())
    val followedRoadmapIds: StateFlow<Set<String>> = _followedRoadmapIds.asStateFlow()

    private val _adoptedRoadmapId = MutableStateFlow(storage.loadAdoptedRoadmapId())
    val adoptedRoadmapId: StateFlow<String?> = _adoptedRoadmapId.asStateFlow()

    private val _milestoneCustomNotes = MutableStateFlow(storage.loadMilestoneCustomNotes())
    val milestoneCustomNotes: StateFlow<Map<String, String>> = _milestoneCustomNotes.asStateFlow()

    private val _milestoneCompletedExercises = MutableStateFlow(storage.loadMilestoneCompletedExercises())
    val milestoneCompletedExercises: StateFlow<Map<String, List<Int>>> = _milestoneCompletedExercises.asStateFlow()

    private val _publicRoadmaps = MutableStateFlow(PublicRoadmapsData.allRoadmaps)
    val publicRoadmaps: StateFlow<List<PublicRoadmap>> = _publicRoadmaps.asStateFlow()

    private val _activeFocusTask = MutableStateFlow<TaskItem?>(null)
    val activeFocusTask: StateFlow<TaskItem?> = _activeFocusTask.asStateFlow()

    private fun applyCustomNotesAndExercisesToMonths(months: List<RoadmapMonth>): List<RoadmapMonth> {
        val notes = _milestoneCustomNotes.value
        val exercises = _milestoneCompletedExercises.value
        return months.map { m ->
            m.copy(
                customNotes = notes[m.id] ?: m.customNotes,
                completedExercises = exercises[m.id] ?: m.completedExercises
            )
        }
    }

    init {
        // Save initial tasks if first run to establish persistent baseline
        if (storage.loadTasks() == null) {
            storage.saveTasks(_tasks.value)
        }
        if (storage.loadTomorrowTasks() == null) {
            storage.saveTomorrowTasks(_tomorrowTasks.value)
        }
        val adoptedId = _adoptedRoadmapId.value
        if (adoptedId != null) {
            val pub = _publicRoadmaps.value.find { it.id == adoptedId }
            if (pub != null) {
                val adapted = PersonalizedRoadmapEngine.adaptPublicRoadmapToPersonalCurriculum(pub, _onboardingData.value)
                _roadmapMonths.value = applyCustomNotesAndExercisesToMonths(adapted)
            }
        } else if (_onboardingData.value.chosenPathId != null) {
            _roadmapMonths.value = applyCustomNotesAndExercisesToMonths(
                PersonalizedRoadmapEngine.generatePersonalizedRoadmap(_onboardingData.value)
            )
        } else {
            _roadmapMonths.value = applyCustomNotesAndExercisesToMonths(_roadmapMonths.value)
        }
    }

    fun attachFirestoreSync(scope: CoroutineScope) {
        this.coroutineScope = scope
        val repo = firestoreRepo ?: return
        if (Firebase.auth.currentUser == null) {
            _syncStatus.value = SyncStatus.Idle
            return
        }

        syncJob?.cancel()
        syncJob = scope.launch {
            _syncStatus.value = SyncStatus.Syncing
            try {
                // 1. User Profile Sync
                val remoteProfile = repo.getUserProfile()
                if (remoteProfile != null) {
                    _userProfile.value = remoteProfile
                    storage.saveUserProfile(remoteProfile)
                } else {
                    repo.saveUserProfile(_userProfile.value)
                }

                // 2. Observe Tasks in real-time
                launch {
                    repo.observeTasks().collect { remoteTasks ->
                        if (remoteTasks.isNotEmpty()) {
                            _tasks.value = remoteTasks
                            storage.saveTasks(remoteTasks)
                        } else {
                            // Seed initial baseline to cloud
                            _tasks.value.forEach { t ->
                                try { repo.saveTask(t) } catch (e: Exception) {}
                            }
                        }
                        _syncStatus.value = SyncStatus.Synced
                    }
                }

                // 3. Observe Subjects in real-time
                launch {
                    repo.observeSubjects().collect { remoteSubs ->
                        if (remoteSubs.isNotEmpty()) {
                            _subjects.value = remoteSubs
                        } else {
                            _subjects.value.forEach { s ->
                                try { repo.saveSubject(s) } catch (e: Exception) {}
                            }
                        }
                    }
                }

                // 4. Observe Completion History in real-time
                launch {
                    repo.observeCompletionHistory().collect { remoteHistory ->
                        if (remoteHistory.isNotEmpty()) {
                            _completionHistory.value = remoteHistory
                            storage.saveCompletionHistory(remoteHistory)
                        }
                    }
                }

                // 5. Observe Milestones in real-time
                launch {
                    repo.observeMilestones().collect { remoteMilestones ->
                        if (remoteMilestones.isNotEmpty()) {
                            val completedIds = remoteMilestones.filter { it.isCompleted }.map { it.monthId }.toSet()
                            _roadmapMonths.update { list ->
                                list.map { m ->
                                    if (completedIds.contains(m.id)) m.copy(isCompleted = true) else m
                                }
                            }
                        }
                    }
                }

                _syncStatus.value = SyncStatus.Synced
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error(e.localizedMessage ?: "Sync error")
            }
        }
    }

    fun detachFirestoreSync() {
        syncJob?.cancel()
        syncJob = null
        _syncStatus.value = SyncStatus.Idle
    }

    // --- Task Actions & Adaptive Scheduling ---
    fun updateTaskStatus(taskId: String, newStatus: TaskStatus, notes: String = "", minutesSpent: Int = 0) {
        var completedTaskRecord: TaskCompletionRecord? = null

        _tasks.update { currentList ->
            currentList.map { task ->
                if (task.id == taskId) {
                    val updatedNotes = if (notes.isNotBlank()) notes else task.userNotes
                    val updatedMinutes = if (minutesSpent > 0) minutesSpent else task.durationMinutes
                    val now = System.currentTimeMillis()

                    if (newStatus == TaskStatus.COMPLETED) {
                        completedTaskRecord = TaskCompletionRecord(
                            id = "comp_${now}_${task.id}",
                            taskId = task.id,
                            taskTitle = task.title,
                            category = task.category,
                            completedAt = now,
                            actualMinutes = updatedMinutes,
                            notes = updatedNotes
                        )
                    }

                    task.copy(
                        status = newStatus,
                        completedAt = if (newStatus == TaskStatus.COMPLETED) now else if (newStatus == TaskStatus.PENDING) null else task.completedAt,
                        userNotes = updatedNotes,
                        actualMinutes = if (newStatus == TaskStatus.COMPLETED) updatedMinutes else task.actualMinutes,
                        blockerReason = if (newStatus == TaskStatus.PENDING) null else task.blockerReason
                    )
                } else task
            }
        }

        completedTaskRecord?.let { record ->
            _completionHistory.update { listOf(record) + it }
            storage.saveCompletionHistory(_completionHistory.value)
            if (firestoreRepo != null && Firebase.auth.currentUser != null) {
                coroutineScope?.launch {
                    try { firestoreRepo.addCompletionRecord(record) } catch (e: Exception) {}
                }
            }
        }

        storage.saveTasks(_tasks.value)
        val updatedTask = _tasks.value.find { it.id == taskId }
        if (updatedTask != null && firestoreRepo != null && Firebase.auth.currentUser != null) {
            coroutineScope?.launch {
                try { firestoreRepo.saveTask(updatedTask) } catch (e: Exception) {}
            }
        }
    }

    fun updateTaskNotes(taskId: String, notes: String) {
        _tasks.update { currentList ->
            currentList.map { task ->
                if (task.id == taskId) task.copy(userNotes = notes) else task
            }
        }
        storage.saveTasks(_tasks.value)
        _tasks.value.find { it.id == taskId }?.let { task ->
            if (firestoreRepo != null && Firebase.auth.currentUser != null) {
                coroutineScope?.launch {
                    try { firestoreRepo.saveTask(task) } catch (e: Exception) {}
                }
            }
        }
    }

    fun markTaskPaused(taskId: String) {
        updateTaskStatus(taskId, TaskStatus.PAUSED)
    }

    fun markTaskSkipped(taskId: String, reason: String) {
        _tasks.update { currentList ->
            currentList.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        status = TaskStatus.SKIPPED,
                        blockerReason = reason.ifBlank { "Skipped for today" },
                        userNotes = if (notesEmpty(task.userNotes) && reason.isNotBlank()) "Skipped: $reason" else task.userNotes
                    )
                } else task
            }
        }
        storage.saveTasks(_tasks.value)
        _tasks.value.find { it.id == taskId }?.let { task ->
            if (firestoreRepo != null && Firebase.auth.currentUser != null) {
                coroutineScope?.launch {
                    try { firestoreRepo.saveTask(task) } catch (e: Exception) {}
                }
            }
        }
    }

    private fun notesEmpty(notes: String): Boolean = notes.isBlank()

    fun restoreTask(taskId: String) {
        _tasks.update { currentList ->
            currentList.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        status = TaskStatus.PENDING,
                        completedAt = null,
                        blockerReason = null
                    )
                } else task
            }
        }
        storage.saveTasks(_tasks.value)
        _tasks.value.find { it.id == taskId }?.let { task ->
            if (firestoreRepo != null && Firebase.auth.currentUser != null) {
                coroutineScope?.launch {
                    try { firestoreRepo.saveTask(task) } catch (e: Exception) {}
                }
            }
        }
    }

    fun rescheduleTask(taskId: String, toTomorrow: Boolean) {
        val taskToMove = _tasks.value.find { it.id == taskId }
        if (taskToMove != null) {
            _tasks.update { it.filterNot { t -> t.id == taskId } }
            val updatedTask = taskToMove.copy(
                scheduledDate = if (toTomorrow) "Tomorrow" else "Today",
                status = TaskStatus.PENDING
            )
            _tomorrowTasks.update { it + updatedTask }
            storage.saveTasks(_tasks.value)
            storage.saveTomorrowTasks(_tomorrowTasks.value)
            return
        }

        // Check if moving from tomorrow to today
        val taskFromTomorrow = _tomorrowTasks.value.find { it.id == taskId }
        if (taskFromTomorrow != null) {
            _tomorrowTasks.update { it.filterNot { t -> t.id == taskId } }
            val updatedTask = taskFromTomorrow.copy(
                scheduledDate = "Today",
                status = TaskStatus.PENDING
            )
            _tasks.update { it + updatedTask }
            storage.saveTasks(_tasks.value)
            storage.saveTomorrowTasks(_tomorrowTasks.value)
        }
    }

    fun moveTomorrowTaskToToday(taskId: String) {
        val task = _tomorrowTasks.value.find { it.id == taskId } ?: return
        _tomorrowTasks.update { it.filterNot { t -> t.id == taskId } }
        val updatedTask = task.copy(
            scheduledDate = "Today",
            status = TaskStatus.PENDING
        )
        _tasks.update { it + updatedTask }
        storage.saveTasks(_tasks.value)
        storage.saveTomorrowTasks(_tomorrowTasks.value)
    }

    fun addNewTask(title: String, category: TaskCategory, duration: Int, priority: Priority, why: String, dod: String) {
        val newTask = TaskItem(
            id = "custom_${System.currentTimeMillis()}",
            title = title,
            category = category,
            whyItMatters = why.ifBlank { "Personal daily engineering milestone" },
            instructions = "Follow systematic steps and document your key learning notes.",
            durationMinutes = duration,
            priority = priority,
            difficulty = Difficulty.BEGINNER,
            resourceName = "Shivaar Self-Study",
            definitionOfDone = dod.ifBlank { "Complete exercise and verify outputs." }
        )
        _tasks.update { listOf(newTask) + it }
        storage.saveTasks(_tasks.value)
        if (firestoreRepo != null && Firebase.auth.currentUser != null) {
            coroutineScope?.launch {
                try { firestoreRepo.saveTask(newTask) } catch (e: Exception) {}
            }
        }
    }

    fun startFocusSession(task: TaskItem) {
        _activeFocusTask.value = task
        updateTaskStatus(task.id, TaskStatus.IN_PROGRESS)
    }

    fun pauseFocusSession(task: TaskItem) {
        updateTaskStatus(task.id, TaskStatus.PAUSED)
    }

    fun completeFocusSession(notes: String, durationMins: Int) {
        val active = _activeFocusTask.value ?: return
        updateTaskStatus(active.id, TaskStatus.COMPLETED, notes, durationMins)
        _activeFocusTask.value = null
    }

    fun dismissFocusSession() {
        val active = _activeFocusTask.value
        if (active != null && active.status == TaskStatus.IN_PROGRESS) {
            updateTaskStatus(active.id, TaskStatus.PAUSED)
        }
        _activeFocusTask.value = null
    }

    fun saveStudySession(session: StudySessionItem) {
        if (firestoreRepo != null && Firebase.auth.currentUser != null) {
            coroutineScope?.launch {
                try { firestoreRepo.saveStudySession(session) } catch (e: Exception) {}
            }
        }
    }

    // --- Subject / Academics Actions ---
    fun toggleSubjectTopic(subjectId: String, topic: String) {
        _subjects.update { current ->
            current.map { subject ->
                if (subject.id == subjectId) {
                    val alreadyDone = subject.completedTopics.contains(topic)
                    val updatedDone = if (alreadyDone) {
                        subject.completedTopics.filterNot { it == topic }
                    } else {
                        subject.completedTopics + topic
                    }
                    subject.copy(completedTopics = updatedDone)
                } else subject
            }
        }
        val updatedSub = _subjects.value.find { it.id == subjectId }
        if (updatedSub != null && firestoreRepo != null && Firebase.auth.currentUser != null) {
            coroutineScope?.launch {
                try { firestoreRepo.saveSubject(updatedSub) } catch (e: Exception) {}
            }
        }
    }

    fun incrementAttendance(subjectId: String, attended: Boolean) {
        _subjects.update { current ->
            current.map { sub ->
                if (sub.id == subjectId) {
                    sub.copy(
                        totalClasses = sub.totalClasses + 1,
                        attendedClasses = if (attended) sub.attendedClasses + 1 else sub.attendedClasses
                    )
                } else sub
            }
        }
        val updatedSub = _subjects.value.find { it.id == subjectId }
        if (updatedSub != null && firestoreRepo != null && Firebase.auth.currentUser != null) {
            coroutineScope?.launch {
                try { firestoreRepo.saveSubject(updatedSub) } catch (e: Exception) {}
            }
        }
    }

    // --- DSA Actions ---
    fun updateDsaProblemStatus(problemId: String, status: ProblemStatus, notes: String = "", snippet: String = "") {
        _dsaProblems.update { list ->
            list.map { prob ->
                if (prob.id == problemId) {
                    prob.copy(
                        status = status,
                        mistakeNote = if (notes.isNotBlank()) notes else prob.mistakeNote,
                        solutionSnippet = if (snippet.isNotBlank()) snippet else prob.solutionSnippet
                    )
                } else prob
            }
        }
    }

    // --- Project Actions ---
    fun toggleProjectFeature(projectId: String, featureIndex: Int) {
        _projects.update { list ->
            list.map { proj ->
                if (proj.id == projectId) {
                    val updatedFeatures = proj.features.mapIndexed { idx, feat ->
                        if (idx == featureIndex) feat.copy(isDone = !feat.isDone) else feat
                    }
                    val allDone = updatedFeatures.all { it.isDone }
                    proj.copy(features = updatedFeatures, isCompleted = allDone)
                } else proj
            }
        }
    }

    // --- Career Actions ---
    fun updateApplicationStatus(appId: String, newStatus: ApplicationStatus) {
        _careerApplications.update { list ->
            list.map { if (it.id == appId) it.copy(status = newStatus) else it }
        }
    }

    // --- Profile Actions ---
    fun updateUserProfile(updated: UserProfile) {
        _userProfile.value = updated
        storage.saveUserProfile(updated)
        if (firestoreRepo != null && Firebase.auth.currentUser != null) {
            coroutineScope?.launch {
                try { firestoreRepo.saveUserProfile(updated) } catch (e: Exception) {}
            }
        }
    }

    // --- Shivaar 2.0 Onboarding & Public Roadmap Actions ---
    fun saveOnboardingProgress(data: OnboardingData) {
        _onboardingData.value = data
        storage.saveOnboardingData(data)
        // Automatically generate personalized roadmap if path is chosen
        if (data.chosenPathId != null) {
            _roadmapMonths.value = PersonalizedRoadmapEngine.generatePersonalizedRoadmap(data)
        }
        // Automatically reflect verified academic stage & constraints into UserProfile
        val updatedProfile = _userProfile.value.copy(
            degree = data.step1Education.qualification + if (data.step1Education.fieldOfStudy.isNotBlank()) " (${data.step1Education.fieldOfStudy})" else "",
            institute = data.step1Education.institution.ifBlank { _userProfile.value.institute },
            affiliatingUniversity = data.step1Education.curriculum.ifBlank { _userProfile.value.affiliatingUniversity },
            currentSemester = data.step1Education.currentSemester ?: _userProfile.value.currentSemester,
            dailyBudgetMinutes = maxOf(30, (data.step5Constraints.availableHoursPerWeek * 60) / 7),
            primaryTrack = data.chosenPathId ?: (data.step3Goals.selectedGoals.firstOrNull()?.label ?: _userProfile.value.primaryTrack)
        )
        updateUserProfile(updatedProfile)
    }

    fun generateAndApplyPersonalizedRoadmap(data: OnboardingData = _onboardingData.value) {
        val personalized = PersonalizedRoadmapEngine.generatePersonalizedRoadmap(data)
        _roadmapMonths.value = personalized
        // Automatically bridge & spawn tasks for active month into Today
        val activeMonth = personalized.firstOrNull { it.isCurrentMonth } ?: personalized.firstOrNull()
        if (activeMonth != null) {
            generateDailyTasksFromMonth(activeMonth)
        }
    }

    fun toggleSaveRoadmap(roadmapId: String) {
        val current = _savedRoadmapIds.value.toMutableSet()
        if (current.contains(roadmapId)) {
            current.remove(roadmapId)
        } else {
            current.add(roadmapId)
        }
        _savedRoadmapIds.value = current
        storage.saveSavedRoadmapIds(current)
    }

    fun toggleFollowRoadmap(roadmapId: String) {
        val current = _followedRoadmapIds.value.toMutableSet()
        if (current.contains(roadmapId)) {
            current.remove(roadmapId)
        } else {
            current.add(roadmapId)
        }
        _followedRoadmapIds.value = current
        storage.saveFollowedRoadmapIds(current)
    }

    /**
     * Phase 6: Adopt a Public Roadmap as the student's active curriculum.
     * Replaces active months with the adapted roadmap, sets milestone 1 active,
     * updates user profile, follows the roadmap, and spawns day 1 execution tasks.
     */
    fun adoptPublicRoadmap(roadmapId: String, autoSpawnTasks: Boolean = true) {
        val roadmap = _publicRoadmaps.value.find { it.id == roadmapId } ?: return
        val adapted = PersonalizedRoadmapEngine.adaptPublicRoadmapToPersonalCurriculum(roadmap, _onboardingData.value)
        val enriched = applyCustomNotesAndExercisesToMonths(adapted)
        _roadmapMonths.value = enriched
        _adoptedRoadmapId.value = roadmapId
        storage.saveAdoptedRoadmapId(roadmapId)

        // Automatically follow the roadmap
        val followed = _followedRoadmapIds.value.toMutableSet()
        followed.add(roadmapId)
        _followedRoadmapIds.value = followed
        storage.saveFollowedRoadmapIds(followed)

        // Update profile primary track
        val updatedProfile = _userProfile.value.copy(
            primaryTrack = roadmap.title,
            currentMonth = 1
        )
        updateUserProfile(updatedProfile)

        // Seed day 1 execution tasks
        if (autoSpawnTasks) {
            enriched.firstOrNull()?.let { generateDailyTasksFromMonth(it) }
        }
    }

    /**
     * Phase 6: Update personal notes on a roadmap milestone
     */
    fun updateMilestoneCustomNotes(monthId: String, notes: String) {
        val updatedNotes = _milestoneCustomNotes.value.toMutableMap()
        updatedNotes[monthId] = notes
        _milestoneCustomNotes.value = updatedNotes
        storage.saveMilestoneCustomNotes(updatedNotes)

        _roadmapMonths.update { list ->
            list.map { m ->
                if (m.id == monthId) m.copy(customNotes = notes) else m
            }
        }
    }

    /**
     * Phase 6: Toggle practice exercise completion on a milestone
     */
    fun toggleMilestoneExercise(monthId: String, exerciseIndex: Int) {
        val currentMap = _milestoneCompletedExercises.value.toMutableMap()
        val currentList = currentMap[monthId]?.toMutableList() ?: mutableListOf()
        if (currentList.contains(exerciseIndex)) {
            currentList.remove(exerciseIndex)
        } else {
            currentList.add(exerciseIndex)
        }
        currentMap[monthId] = currentList
        _milestoneCompletedExercises.value = currentMap
        storage.saveMilestoneCompletedExercises(currentMap)

        _roadmapMonths.update { list ->
            list.map { m ->
                if (m.id == monthId) m.copy(completedExercises = currentList) else m
            }
        }
    }

    /**
     * Phase 6: Export active curriculum syllabus as Markdown
     */
    fun getCurriculumExportMarkdown(): String {
        val title = _adoptedRoadmapId.value?.let { id ->
            _publicRoadmaps.value.find { it.id == id }?.title
        } ?: _userProfile.value.primaryTrack
        return PersonalizedRoadmapEngine.exportCurriculumAsMarkdown(title, _roadmapMonths.value)
    }

    // --- 48-Month Roadmap Actions ---
    fun toggleMonthCompletion(monthId: String) {
        _roadmapMonths.update { list ->
            list.map { m ->
                if (m.id == monthId) m.copy(isCompleted = !m.isCompleted) else m
            }
        }
        val month = _roadmapMonths.value.find { it.id == monthId }
        if (month != null && firestoreRepo != null && Firebase.auth.currentUser != null) {
            val uid = Firebase.auth.currentUser?.uid ?: ""
            val milestone = RoadmapMilestoneItem(
                id = month.id,
                userId = uid,
                monthId = month.id,
                monthNumber = month.monthNumber,
                year = month.year,
                semester = month.semester,
                title = month.title,
                focus = month.mainFocus,
                isCompleted = month.isCompleted,
                completedAt = if (month.isCompleted) System.currentTimeMillis() else null,
                checkpointPassed = month.isCompleted
            )
            coroutineScope?.launch {
                try { firestoreRepo.saveMilestone(milestone) } catch (e: Exception) {}
            }
        }
    }

    fun setActiveMonth(monthNumber: Int) {
        _roadmapMonths.update { list ->
            list.map { m ->
                m.copy(isCurrentMonth = m.monthNumber == monthNumber)
            }
        }
        _userProfile.update { it.copy(currentMonth = monthNumber) }
    }

    fun updateGateStatus(gateId: String, status: GateStatus) {
        _prerequisiteGates.update { list ->
            list.map { if (it.id == gateId) it.copy(status = status) else it }
        }
    }

    fun generateDailyTasksFromMonth(month: RoadmapMonth) {
        val newTasks = mutableListOf<TaskItem>()
        val mainTopic = month.learningObjectives.firstOrNull() ?: month.topics.firstOrNull() ?: month.mainFocus
        newTasks.add(
            TaskItem(
                id = "task_m${month.monthNumber}_topic_${System.currentTimeMillis()}",
                title = "Study: $mainTopic",
                category = when (month.primaryTrack) {
                    SkillTrack.PROGRAMMING_DSA -> TaskCategory.C_PROGRAMMING
                    SkillTrack.DEV_PROJECTS -> TaskCategory.DEV
                    SkillTrack.AI_ML -> TaskCategory.PYTHON
                    SkillTrack.CORE_CS -> TaskCategory.ACADEMICS
                    SkillTrack.PLACEMENT_PREP -> TaskCategory.DSA
                    SkillTrack.CAREER_INTERNSHIPS -> TaskCategory.CAREER
                    SkillTrack.ACADEMICS -> TaskCategory.ACADEMICS
                },
                whyItMatters = "Direct milestone for ${month.title} (${month.mainFocus}).",
                instructions = if (month.learningResources.isNotEmpty()) {
                    "Resources: ${month.learningResources.joinToString("; ")}\n1. Study core concepts.\n2. Write code samples and verify.\n3. Log notes."
                } else {
                    "1. Read recommended documentation.\n2. Write code examples and verify compilation.\n3. Log lessons in notes."
                },
                durationMinutes = 45,
                priority = Priority.HIGH,
                difficulty = Difficulty.INTERMEDIATE,
                resourceName = "Month ${month.monthNumber}: ${month.title}",
                definitionOfDone = month.definitionOfDone.ifBlank { month.measurableOutput }
            )
        )

        val dsaPractice = month.practiceExercises.firstOrNull { it.contains("DSA", ignoreCase = true) || it.contains("problem", ignoreCase = true) }
            ?: "DSA Sprint: ${month.dsaTarget}"
        newTasks.add(
            TaskItem(
                id = "task_m${month.monthNumber}_dsa_${System.currentTimeMillis() + 1}",
                title = dsaPractice,
                category = TaskCategory.DSA,
                whyItMatters = "Progresses toward Month ${month.monthNumber} cumulative target of ${month.cumulativeDsaGoal} quality problems.",
                instructions = "Solve 2 problems matching this pattern on LeetCode/GFG and record time/space complexities.",
                durationMinutes = 40,
                priority = Priority.MEDIUM,
                difficulty = Difficulty.INTERMEDIATE,
                resourceName = "Target: ${month.dsaTarget}",
                definitionOfDone = "Accepted submission on LeetCode with documented Big-O analysis."
            )
        )

        newTasks.add(
            TaskItem(
                id = "task_m${month.monthNumber}_proj_${System.currentTimeMillis() + 2}",
                title = "Milestone Build: ${month.projectMilestone}",
                category = TaskCategory.DEV,
                whyItMatters = month.readinessCheckpoint,
                instructions = "Implement core feature and verify with unit tests or automated linting.",
                durationMinutes = 45,
                priority = Priority.MEDIUM,
                difficulty = Difficulty.INTERMEDIATE,
                resourceName = "Portfolio Output",
                definitionOfDone = month.assessmentCriteria.ifBlank { month.readinessCheckpoint }
            )
        )

        if (month.revisionSchedule.isNotBlank()) {
            newTasks.add(
                TaskItem(
                    id = "task_m${month.monthNumber}_rev_${System.currentTimeMillis() + 3}",
                    title = "Spaced Recall: ${month.mainFocus}",
                    category = TaskCategory.ACADEMICS,
                    whyItMatters = month.revisionSchedule,
                    instructions = "Active recall session using flashcards and 1 previous milestone question.",
                    durationMinutes = 20,
                    priority = Priority.LOW,
                    difficulty = Difficulty.BEGINNER,
                    resourceName = "Active Recall",
                    definitionOfDone = "Reviewed past month summary notes and resolved 1 revision prompt."
                )
            )
        }

        _tasks.update { current ->
            // Prevent exact duplicates
            val titles = current.map { it.title }.toSet()
            current + newTasks.filter { it.title !in titles }
        }
    }

    /**
     * Phase 5: Advance to Next Milestone
     * Marks the current month complete, activates the subsequent month,
     * and automatically seeds the new month's daily tasks.
     */
    fun advanceToNextMilestone(currentMonthId: String) {
        val months = _roadmapMonths.value
        val currentIndex = months.indexOfFirst { it.id == currentMonthId }
        if (currentIndex != -1) {
            val updated = months.mapIndexed { index, m ->
                when (index) {
                    currentIndex -> m.copy(isCompleted = true, isCurrentMonth = false)
                    currentIndex + 1 -> m.copy(isCurrentMonth = true)
                    else -> m
                }
            }
            _roadmapMonths.value = updated
            val nextMonth = updated.getOrNull(currentIndex + 1)
            if (nextMonth != null) {
                _userProfile.update { it.copy(currentMonth = nextMonth.monthNumber) }
                generateDailyTasksFromMonth(nextMonth)
            }
        }
    }

    /**
     * Phase 5: Adaptive Schedule Recovery
     * When the user falls behind or needs catch-up, this adjusts tomorrow's queue,
     * tags critical milestones, and spawns a high-priority Catch-Up Task.
     */
    fun triggerAdaptiveRecovery() {
        val currentTasks = _tasks.value
        val uncompleted = currentTasks.filter { it.status != TaskStatus.COMPLETED }
        if (uncompleted.isNotEmpty()) {
            // Move lower priority items to tomorrow to reduce cognitive overwhelm
            val lowerPriority = uncompleted.filter { it.priority == Priority.LOW }
            if (lowerPriority.isNotEmpty()) {
                _tasks.update { it - lowerPriority.toSet() }
                _tomorrowTasks.update { it + lowerPriority }
            }
        }

        val activeMonth = _roadmapMonths.value.find { it.isCurrentMonth }
        val recoveryTask = TaskItem(
            id = "task_recovery_${System.currentTimeMillis()}",
            title = "Adaptive Catch-Up Sprint: ${activeMonth?.mainFocus ?: "Core Fundamentals"}",
            category = TaskCategory.ACADEMICS,
            whyItMatters = activeMonth?.failureRecoveryInstructions ?: "Streamlined 30-min catch-up sprint to maintain consistent study momentum.",
            instructions = "1. Focus exclusively on the single most critical project milestone or DSA problem.\n2. Skip secondary optional readings.\n3. Verify solution and mark milestone progress.",
            durationMinutes = 30,
            priority = Priority.HIGH,
            difficulty = Difficulty.INTERMEDIATE,
            resourceName = "Adaptive Recovery",
            definitionOfDone = "1 core priority item completed and checked off."
        )
        _tasks.update { listOf(recoveryTask) + it }
    }

    fun addResearchSources(newSources: List<ResearchSource>) {
        _researchSources.update { current ->
            (newSources + current).distinctBy { it.url }
        }
    }

    fun toggleResearchBookmark(sourceId: String) {
        _researchSources.update { list ->
            list.map { if (it.id == sourceId) it.copy(isBookmarked = !it.isBookmarked) else it }
        }
    }

    // --- Initial Seed Generators ---
    companion object {
        fun getInitialTasks(): List<TaskItem> = listOf(
            TaskItem(
                id = "task_c_pointers",
                title = "C Memory & Pointers: Practice 4 Pointer Arithmetic Questions",
                category = TaskCategory.C_PROGRAMMING,
                whyItMatters = "Pointers form the bedrock of memory management in C and are essential before transitioning to C++ pointers and references.",
                instructions = "1. Write a program to swap two variables using call-by-reference.\n2. Traverse a 1D integer array using pointer increments *(ptr + i).\n3. Test pointer to pointer (`int** ptr`) and dynamic malloc demo.",
                durationMinutes = 45,
                priority = Priority.HIGH,
                difficulty = Difficulty.INTERMEDIATE,
                resourceName = "C Programming: Pointers & Addresses (K&R Chapter 5)",
                resourceUrl = "https://en.cppreference.com/w/c/language/pointer",
                definitionOfDone = "Code 3 examples in GCC/Clang, verify no memory leaks, log common segmentation fault pitfalls."
            ),
            TaskItem(
                id = "task_aktu_python_basics",
                title = "AKTU Python: Unit 2 Control Structures & Loops",
                category = TaskCategory.PYTHON,
                whyItMatters = "Directly covers AKTU Semester 1 syllabus for Programming for Problem Solving (KCS-101/201).",
                instructions = "Study if-elif-else ladders, while and for loops in Python, and practice nested loops generating diamond patterns and prime number checks.",
                durationMinutes = 40,
                priority = Priority.HIGH,
                difficulty = Difficulty.BEGINNER,
                resourceName = "AKTU Syllabus Unit 2: Python Language Fundamentals",
                resourceUrl = "https://aktu.ac.in",
                definitionOfDone = "Solve 4 AKTU previous semester coding questions on loops and submit in your local exercise folder."
            ),
            TaskItem(
                id = "task_dsa_array_reverse",
                title = "DSA Practice: Two Sum & Array Manipulation",
                category = TaskCategory.DSA,
                whyItMatters = "Builds intuition for O(n^2) brute force vs O(n) hash table or two-pointer logic.",
                instructions = "Implement Two Sum on LeetCode. First write the double loop approach, then analyze its space/time complexity before optimizing.",
                durationMinutes = 35,
                priority = Priority.MEDIUM,
                difficulty = Difficulty.BEGINNER,
                resourceName = "LeetCode #1: Two Sum",
                resourceUrl = "https://leetcode.com/problems/two-sum/",
                definitionOfDone = "Submit accepted solution on LeetCode and record time complexity in notes."
            ),
            TaskItem(
                id = "task_math_matrices",
                title = "Engineering Mathematics: Cayley-Hamilton Theorem & Inverse",
                category = TaskCategory.ACADEMICS,
                whyItMatters = "Guaranteed 10-mark question in AKTU Semester 1 End-Sem Mathematics exam (BAS-103).",
                instructions = "Revise statement of Cayley-Hamilton theorem, solve 2 characteristic equation derivations for 3x3 matrices, and compute A^-1.",
                durationMinutes = 30,
                priority = Priority.MEDIUM,
                difficulty = Difficulty.INTERMEDIATE,
                resourceName = "Higher Engineering Mathematics by B.S. Grewal (Unit 1)",
                resourceUrl = "https://aktu.ac.in/syllabus.html",
                definitionOfDone = "Solve 2 textbook problems without looking at solutions and cross-check eigenvalues."
            )
        )

        fun getTomorrowTasks(): List<TaskItem> = listOf(
            TaskItem(
                id = "task_tmrw_c_structures",
                title = "C Structures & Typedef: Building a Student Record CLI",
                category = TaskCategory.C_PROGRAMMING,
                whyItMatters = "Prepares for Object-Oriented classes in C++ next semester.",
                instructions = "Define `struct Student { char name[50]; int roll; float cgpa; }` and implement input/display functions.",
                durationMinutes = 45,
                priority = Priority.HIGH,
                difficulty = Difficulty.BEGINNER,
                resourceName = "C Structures Guide",
                definitionOfDone = "Compile program with GCC and verify dynamic array of structs."
            ),
            TaskItem(
                id = "task_tmrw_python_lists",
                title = "AKTU Python: Lists, Tuples, and List Comprehensions",
                category = TaskCategory.PYTHON,
                whyItMatters = "AKTU Unit 3 syllabus requirement and vital for fast prototyping.",
                instructions = "Understand mutable vs immutable sequences and write 3 one-liner list comprehensions for matrix flattening.",
                durationMinutes = 40,
                priority = Priority.MEDIUM,
                difficulty = Difficulty.BEGINNER,
                resourceName = "Python Documentation (Sequence Types)",
                definitionOfDone = "Complete 5 short practice exercises and document indexing tricks."
            ),
            TaskItem(
                id = "task_tmrw_git_commit",
                title = "Git Foundations: Branching & GitHub Push",
                category = TaskCategory.DEV,
                whyItMatters = "Every software engineer needs clean Git commit hygiene from Day 1.",
                instructions = "Create a local repo, make meaningful commits, create a branch, merge it, and push to GitHub.",
                durationMinutes = 30,
                priority = Priority.LOW,
                difficulty = Difficulty.BEGINNER,
                resourceName = "Pro Git Book (Chapters 1-3)",
                definitionOfDone = "Push a verified public repo containing your C practice codes."
            )
        )

        fun getInitialRoadmap(): List<RoadmapStage> = listOf(
            RoadmapStage(
                year = 1,
                semester = 1,
                phaseName = "Year 1 • Sem 1: Foundations",
                focusTheme = "C Mastery, AKTU Python, & Math Rigor",
                milestones = listOf(
                    MilestoneItem(
                        id = "m1_c_fundamentals",
                        title = "Complete C Programming Fundamentals",
                        subtitle = "Functions, Pointers, Structs & Memory Allocation",
                        description = "Achieve deep mastery over pointers, memory addresses, stack vs heap, and basic file operations in C.",
                        prerequisites = "None (High School Math)",
                        outcomes = listOf("Pointer arithmetic fluency", "Understanding segmentation faults", "Writing clean modular C code"),
                        isCompleted = true,
                        category = "Programming"
                    ),
                    MilestoneItem(
                        id = "m1_aktu_python",
                        title = "AKTU Python Curriculum (KCS-101)",
                        subtitle = "Data Types, Loops, Functions & File I/O",
                        description = "Master Python syntax, built-in data structures (lists, dicts, sets, tuples), and problem-solving exercises prescribed by AKTU.",
                        prerequisites = "C basic control flow",
                        outcomes = listOf("Scoring 90%+ in AKTU Python Internal/External", "Scripting automation scripts", "Preparing for ML libraries"),
                        isCompleted = false,
                        category = "Academics"
                    ),
                    MilestoneItem(
                        id = "m1_git_cli",
                        title = "Linux CLI, Git & GitHub Essentials",
                        subtitle = "Version Control & Developer Workflow",
                        description = "Learn terminal commands, bash navigation, git init, add, commit, branch, merge, pull requests, and README formatting.",
                        prerequisites = "Basic computer literacy",
                        outcomes = listOf("Active GitHub profile", "Publishing first C code repository", "Clean commit history"),
                        isCompleted = true,
                        category = "Tools"
                    )
                )
            ),
            RoadmapStage(
                year = 1,
                semester = 2,
                phaseName = "Year 1 • Sem 2: C++ & DSA Kickoff",
                focusTheme = "OOP, Standard Template Library (STL) & Asymptotic Analysis",
                milestones = listOf(
                    MilestoneItem(
                        id = "m2_cpp_oop",
                        title = "Transition from C to C++",
                        subtitle = "References, Classes, Constructors & Encapsulation",
                        description = "Learn modern C++, memory safety, references vs pointers, copy constructors, operator overloading, and inheritance.",
                        prerequisites = "C Language completion",
                        outcomes = listOf("Writing clean OOP in C++", "Using vectors and strings comfortably"),
                        isCompleted = false,
                        category = "Programming"
                    ),
                    MilestoneItem(
                        id = "m2_stl_complexity",
                        title = "C++ STL & Time/Space Complexity",
                        subtitle = "Vectors, Maps, Sets & Big-O Notation",
                        description = "Master standard library containers (vector, unordered_map, set, stack, queue) and rigorously analyze Big-O time & space.",
                        prerequisites = "C++ basics",
                        outcomes = listOf("O(1) lookups using hash maps", "Intuition for worst-case time complexity"),
                        isCompleted = false,
                        category = "DSA"
                    ),
                    MilestoneItem(
                        id = "m2_arrays_strings",
                        title = "Elementary DSA: Arrays, Strings & Two Pointers",
                        subtitle = "Solve 40 LeetCode Easy/Medium Problems",
                        description = "Build speed and accuracy solving core array and string problems (sliding window, two pointers, prefix sums).",
                        prerequisites = "STL basics",
                        outcomes = listOf("40+ LeetCode problems logged", "Speed under 20 mins per problem"),
                        isCompleted = false,
                        category = "DSA"
                    )
                )
            ),
            RoadmapStage(
                year = 2,
                semester = 3,
                phaseName = "Year 2 • Sem 3: Core DSA & Web Foundations",
                focusTheme = "Linear Data Structures, React & Operating Systems",
                milestones = listOf(
                    MilestoneItem(
                        id = "m3_linear_dsa",
                        title = "Linked Lists, Stacks, Queues & Recursion",
                        subtitle = "Low-level implementation & LeetCode medium mastery",
                        description = "Implement singly/doubly linked lists, monotonic stacks, LRU cache intuition, and recursion trees.",
                        prerequisites = "Pointers & C++ OOP",
                        outcomes = listOf("Invert linked lists seamlessly", "Solve recursion without stack overflow"),
                        isCompleted = false,
                        category = "DSA"
                    ),
                    MilestoneItem(
                        id = "m3_web_frontend",
                        title = "Modern Frontend: HTML, CSS, JavaScript & React",
                        subtitle = "Component Architecture & Responsive Design",
                        description = "Build interactive responsive web UIs using modern JavaScript (ES6+), React hooks (useState, useEffect), and Tailwind CSS.",
                        prerequisites = "Basic programming",
                        outcomes = listOf("Deploy a live responsive React app", "Master asynchronous fetch/promises"),
                        isCompleted = false,
                        category = "Development"
                    ),
                    MilestoneItem(
                        id = "m3_os_core",
                        title = "Core CS: Operating Systems",
                        subtitle = "Processes, Threads, Scheduling & Deadlocks",
                        description = "Deep dive into OS concepts: process synchronization (mutex, semaphores), CPU scheduling algorithms, virtual memory and paging.",
                        prerequisites = "C Language",
                        outcomes = listOf("Ace college OS exam & interview rounds", "Understand multithreading"),
                        isCompleted = false,
                        category = "Core CS"
                    )
                )
            ),
            RoadmapStage(
                year = 2,
                semester = 4,
                phaseName = "Year 2 • Sem 4: Backend, Databases & Non-Linear DSA",
                focusTheme = "Trees, Graphs, Node.js/PostgreSQL & DBMS",
                milestones = listOf(
                    MilestoneItem(
                        id = "m4_trees_graphs",
                        title = "Trees, BSTs, Heaps & Graph Traversals",
                        subtitle = "BFS, DFS, Dijkstra & Topological Sort",
                        description = "Master binary tree traversals (inorder, preorder, postorder, level-order), heaps, priority queues, and graph algorithms.",
                        prerequisites = "Recursion and Queues",
                        outcomes = listOf("Solve 50+ tree/graph problems", "Implement BFS & DFS from scratch"),
                        isCompleted = false,
                        category = "DSA"
                    ),
                    MilestoneItem(
                        id = "m4_backend_sql",
                        title = "Backend Engineering with Node.js & PostgreSQL",
                        subtitle = "REST APIs, JWT Auth, Database Schema Design",
                        description = "Design relational databases, write complex SQL queries, index columns, and build authenticated RESTful backend APIs.",
                        prerequisites = "JavaScript / TypeScript",
                        outcomes = listOf("Full-Stack CRUD application with Auth", "Understand ACID properties & normalization"),
                        isCompleted = false,
                        category = "Development"
                    ),
                    MilestoneItem(
                        id = "m4_dbms_networks",
                        title = "Core CS: DBMS & Computer Networks",
                        subtitle = "Transactions, Concurrency, OSI Model, TCP/IP",
                        description = "Prepare for technical interviews with relational algebra, indexing, B-Trees, HTTP/HTTPS, DNS, TCP handshakes, and sockets.",
                        prerequisites = "Operating Systems basics",
                        outcomes = listOf("Explain TCP 3-way handshake", "Query optimization basics"),
                        isCompleted = false,
                        category = "Core CS"
                    )
                )
            ),
            RoadmapStage(
                year = 3,
                semester = 5,
                phaseName = "Year 3 • Sem 5: Applied AI & System Design",
                focusTheme = "Track 1: Full-Stack + Applied AI Engineering",
                milestones = listOf(
                    MilestoneItem(
                        id = "m5_applied_ai",
                        title = "Applied Generative AI & Vector Search",
                        subtitle = "Embeddings, RAG, LangChain/LlamaIndex & Gemini API",
                        description = "Build intelligent applications combining vector databases (Pinecone/Chroma), embedding models, RAG pipelines, and LLM tool calling.",
                        prerequisites = "Python & Backend API skills",
                        outcomes = listOf("Deploy an enterprise RAG assistant", "Evaluate hallucination metrics"),
                        isCompleted = false,
                        category = "Specialization"
                    ),
                    MilestoneItem(
                        id = "m5_system_design",
                        title = "High-Level & Low-Level System Design",
                        subtitle = "Load Balancers, Caching (Redis), Rate Limiting & Microservices",
                        description = "Learn how large-scale services operate: CDN, horizontal scaling, caching strategies, CAP theorem, message queues (Kafka).",
                        prerequisites = "Backend and DBMS",
                        outcomes = listOf("Design URL Shortener & Chat App architectures", "Perform capacity estimations"),
                        isCompleted = false,
                        category = "System Design"
                    )
                )
            ),
            RoadmapStage(
                year = 3,
                semester = 6,
                phaseName = "Year 3 • Sem 6: Cloud, DevOps & Internship Hunt",
                focusTheme = "Docker, CI/CD, Resume Polish & Mock Interviews",
                milestones = listOf(
                    MilestoneItem(
                        id = "m6_devops_cloud",
                        title = "Docker Containers & Cloud Deployment",
                        subtitle = "AWS/GCP, GitHub Actions CI/CD pipelines",
                        description = "Containerize full-stack services, write multi-stage Dockerfiles, configure automated testing CI pipelines, and deploy on cloud.",
                        prerequisites = "Backend applications",
                        outcomes = listOf("Production-deployed portfolio applications with custom domains"),
                        isCompleted = false,
                        category = "DevOps"
                    ),
                    MilestoneItem(
                        id = "m6_internship_prep",
                        title = "Technical Mock Interviews & Applications",
                        subtitle = "Live coding rounds, system design, and behavioral STAR format",
                        description = "Apply to 50+ internships, practice live problem solving under pressure, and prepare behavioral responses.",
                        prerequisites = "Core DSA and Full-Stack Project",
                        outcomes = listOf("Secure summer engineering internship", "Refined ATS-friendly resume"),
                        isCompleted = false,
                        category = "Career"
                    )
                )
            ),
            RoadmapStage(
                year = 4,
                semester = 7,
                phaseName = "Year 4 • Sem 7: Placement Drive & Capstone Project",
                focusTheme = "Final Year Engineering Capstone & On-Campus/Off-Campus Placements",
                milestones = listOf(
                    MilestoneItem(
                        id = "m7_capstone",
                        title = "Production-Grade Capstone Project",
                        subtitle = "End-to-End Architectural Masterpiece with AI & Cloud",
                        description = "Build a substantial, original capstone project solving a genuine problem with documented tests, telemetry, and live demo.",
                        prerequisites = "Full-Stack + AI engineering skills",
                        outcomes = listOf("Featured GitHub showcase", "Impressive talking point in interviews"),
                        isCompleted = false,
                        category = "Project"
                    ),
                    MilestoneItem(
                        id = "m7_placements",
                        title = "Company Placement Coding Rounds & Interviews",
                        subtitle = "Rapid revision of 150 top DSA patterns & Core CS flashcards",
                        description = "Clear technical assessments (OA), solve dynamic programming and graph problems under 30 mins, and ace HR/Managerial rounds.",
                        prerequisites = "All prior stages",
                        outcomes = listOf("Multiple SDE / SWE offers", "Confidence in salary negotiations"),
                        isCompleted = false,
                        category = "Placements"
                    )
                )
            ),
            RoadmapStage(
                year = 4,
                semester = 8,
                phaseName = "Year 4 • Sem 8: Graduation & Industry Onboarding",
                focusTheme = "Transition from Engineering Student to Professional Software Engineer",
                milestones = listOf(
                    MilestoneItem(
                        id = "m8_industry_readiness",
                        title = "Professional Engineering Practices",
                        subtitle = "Clean Code, Design Patterns, Open-Source & Career Longevity",
                        description = "Understand large legacy codebases, code reviews, observability, agile workflows, and lifelong learning habits.",
                        prerequisites = "Job Offer or Capstone",
                        outcomes = listOf("Successful joining at top company", "First-year promotion roadmap"),
                        isCompleted = false,
                        category = "Career"
                    )
                )
            )
        )

        fun getInitialSubjects(): List<SubjectItem> = listOf(
            SubjectItem(
                id = "sub_pps",
                name = "Programming for Problem Solving",
                code = "KCS-101 / 201",
                credits = 4,
                topics = listOf(
                    "Unit 1: Introduction to Components of a Computer & Number Systems",
                    "Unit 2: Arithmetic Expressions & Precedence, Conditional Branching",
                    "Unit 3: Loops (while, for, do-while), Break & Continue",
                    "Unit 4: Arrays (1-D, 2-D), Character Arrays, Strings & Functions",
                    "Unit 5: Pointers, Dynamic Memory Allocation (malloc, free) & Structures",
                    "Python Module: Variables, Data Types, Lists, Dicts & File Handling"
                ),
                completedTopics = listOf(
                    "Unit 1: Introduction to Components of a Computer & Number Systems",
                    "Unit 2: Arithmetic Expressions & Precedence, Conditional Branching",
                    "Unit 3: Loops (while, for, do-while), Break & Continue",
                    "Unit 4: Arrays (1-D, 2-D), Character Arrays, Strings & Functions"
                ),
                attendedClasses = 20,
                totalClasses = 24,
                nextExamDate = "Internal 2: Nov 28",
                notes = "Focus on Python syntax & file handling for upcoming practical viva."
            ),
            SubjectItem(
                id = "sub_math1",
                name = "Engineering Mathematics - I",
                code = "BAS-103",
                credits = 4,
                topics = listOf(
                    "Unit 1: Matrices, Rank, System of Linear Equations, Cayley-Hamilton",
                    "Unit 2: Differential Calculus I (Leibnitz theorem, Partial differentiation)",
                    "Unit 3: Differential Calculus II (Taylor's series, Maxima & Minima)",
                    "Unit 4: Multiple Integrals (Double, Triple integrals, Dirichlet's)",
                    "Unit 5: Vector Calculus (Gradient, Divergence, Curl, Gauss Divergence)"
                ),
                completedTopics = listOf(
                    "Unit 1: Matrices, Rank, System of Linear Equations, Cayley-Hamilton",
                    "Unit 2: Differential Calculus I (Leibnitz theorem, Partial differentiation)"
                ),
                attendedClasses = 19,
                totalClasses = 24,
                nextExamDate = "Mid-Term: Dec 04",
                notes = "Practice Cayley-Hamilton theorem and Jacobians regularly."
            ),
            SubjectItem(
                id = "sub_physics",
                name = "Engineering Physics",
                code = "BAS-101",
                credits = 4,
                topics = listOf(
                    "Unit 1: Relativistic Mechanics (Lorentz transformation, Length contraction)",
                    "Unit 2: Electromagnetic Field Theory (Maxwell's equations, Poynting vector)",
                    "Unit 3: Quantum Mechanics (de-Broglie hypothesis, Schrodinger equation)",
                    "Unit 4: Wave Optics (Interference, Diffraction, Polarization)",
                    "Unit 5: Fiber Optics and Laser (Principle, Numerical Aperture, Ruby Laser)"
                ),
                completedTopics = listOf(
                    "Unit 1: Relativistic Mechanics (Lorentz transformation, Length contraction)",
                    "Unit 2: Electromagnetic Field Theory (Maxwell's equations, Poynting vector)"
                ),
                attendedClasses = 17,
                totalClasses = 22,
                nextExamDate = "Internal: Dec 08",
                notes = "Derive Maxwell's equations in conducting vs dielectric media."
            ),
            SubjectItem(
                id = "sub_bee",
                name = "Basic Electrical Engineering",
                code = "BEE-101",
                credits = 3,
                topics = listOf(
                    "Unit 1: DC Circuits (Mesh, Nodal analysis, Thevenin & Norton theorems)",
                    "Unit 2: Steady State Analysis of Single Phase AC Circuits",
                    "Unit 3: Three Phase AC Circuits & Resonance",
                    "Unit 4: Magnetic Circuits and Single Phase Transformers",
                    "Unit 5: Electrical Machines (DC Motors, 3-Phase Induction Motors)"
                ),
                completedTopics = listOf(
                    "Unit 1: DC Circuits (Mesh, Nodal analysis, Thevenin & Norton theorems)"
                ),
                attendedClasses = 16,
                totalClasses = 20,
                nextExamDate = "Internal: Dec 11",
                notes = "Memorize Thevenin equivalent circuit calculation steps."
            )
        )

        fun getInitialDsaProblems(): List<DsaProblem> = listOf(
            DsaProblem(
                id = "prob_twosum",
                title = "1. Two Sum",
                platform = "LeetCode",
                topic = "Arrays & Hashing",
                language = "C++ / Python",
                difficulty = "Easy",
                status = ProblemStatus.SOLVED,
                mistakeNote = "Remember to store number as key and index as value in hash map for O(n) lookup.",
                solutionSnippet = """// Two Sum in C++ using unordered_map
#include <vector>
#include <unordered_map>
using namespace std;

vector<int> twoSum(vector<int>& nums, int target) {
    unordered_map<int, int> seen;
    for (int i = 0; i < nums.size(); i++) {
        int complement = target - nums[i];
        if (seen.find(complement) != seen.end()) {
            return {seen[complement], i};
        }
        seen[nums[i]] = i;
    }
    return {};
}""",
                url = "https://leetcode.com/problems/two-sum/"
            ),
            DsaProblem(
                id = "prob_reverse_array",
                title = "Reverse an Array using Pointers",
                platform = "GeeksforGeeks",
                topic = "Pointers & Arrays",
                language = "C",
                difficulty = "Easy",
                status = ProblemStatus.SOLVED,
                mistakeNote = "Watch out for off-by-one errors with left < right pointer condition.",
                solutionSnippet = """// Reverse array using two pointers in C
#include <stdio.h>

void reverseArray(int *arr, int n) {
    int *left = arr;
    int *right = arr + n - 1;
    while (left < right) {
        int temp = *left;
        *left = *right;
        *right = temp;
        left++;
        right--;
    }
}""",
                url = "https://www.geeksforgeeks.org/write-a-program-to-reverse-an-array-or-string/"
            ),
            DsaProblem(
                id = "prob_valid_parentheses",
                title = "20. Valid Parentheses",
                platform = "LeetCode",
                topic = "Stack",
                language = "C++",
                difficulty = "Easy",
                status = ProblemStatus.ATTEMPTED,
                mistakeNote = "Always check `stack.empty()` before calling `stack.top()` to prevent crash.",
                solutionSnippet = """// Valid parentheses check using std::stack
#include <stack>
#include <string>
using namespace std;

bool isValid(string s) {
    stack<char> st;
    for (char c : s) {
        if (c == '(' || c == '{' || c == '[') st.push(c);
        else {
            if (st.empty()) return false;
            char top = st.top();
            st.pop();
            if ((c == ')' && top != '(') ||
                (c == '}' && top != '{') ||
                (c == ']' && top != '[')) return false;
        }
    }
    return st.empty();
}""",
                url = "https://leetcode.com/problems/valid-parentheses/"
            ),
            DsaProblem(
                id = "prob_buy_sell_stock",
                title = "121. Best Time to Buy and Sell Stock",
                platform = "LeetCode",
                topic = "Arrays / Dynamic Programming",
                language = "Python",
                difficulty = "Easy",
                status = ProblemStatus.NOT_STARTED,
                mistakeNote = "Keep track of min_price so far and maximum profit achievable.",
                solutionSnippet = """# One-pass O(n) solution in Python
def maxProfit(prices: list[int]) -> int:
    min_price = float('inf')
    max_profit = 0
    for price in prices:
        if price < min_price:
            min_price = price
        elif price - min_price > max_profit:
            max_profit = price - min_price
    return max_profit""",
                url = "https://leetcode.com/problems/best-time-to-buy-and-sell-stock/"
            )
        )

        fun getInitialProjects(): List<ProjectItem> = listOf(
            ProjectItem(
                id = "proj_cli_allocator",
                title = "Student Grade & Memory Manager (C)",
                tagline = "Interactive CLI memory management system for college student databases",
                year = 1,
                semester = 1,
                techStack = listOf("C", "GCC", "Valgrind", "Make"),
                features = listOf(
                    ProjectFeature("Dynamic array allocation using malloc & realloc", true),
                    ProjectFeature("Binary search and quicksort on student records", true),
                    ProjectFeature("File persistence (read/write student binary data)", false),
                    ProjectFeature("Zero memory leaks verified with Valgrind", false)
                ),
                whyItMattersForPlacement = "Proves understanding of pointer arithmetic, heap allocation, and POSIX file system interfaces.",
                githubUrl = "https://github.com/shivam-yadav/student-memory-manager",
                isCompleted = false
            ),
            ProjectItem(
                id = "proj_aktu_helper",
                title = "AKTU Syllabus & SGPA Predictor (Python)",
                tagline = "Automation script parsing curriculum units and computing target internal marks",
                year = 1,
                semester = 1,
                techStack = listOf("Python 3", "JSON", "Rich CLI", "PyTest"),
                features = listOf(
                    ProjectFeature("JSON-backed database of AKTU 1st year subjects", true),
                    ProjectFeature("SGPA & CGPA target marks calculator", true),
                    ProjectFeature("75% attendance criteria buffer estimator", true),
                    ProjectFeature("Export markdown study checklists", false)
                ),
                whyItMattersForPlacement = "Demonstrates clean Pythonic syntax, unit testing, and problem solving for real student pain points.",
                githubUrl = "https://github.com/shivam-yadav/aktu-sgpa-predictor",
                isCompleted = false
            ),
            ProjectItem(
                id = "proj_devconnect",
                title = "DevStack — Student Portfolio & Project Showcase (Full-Stack)",
                tagline = "Modern platform for engineering students to host verified project milestones",
                year = 2,
                semester = 3,
                techStack = listOf("React", "TypeScript", "Node.js", "PostgreSQL", "Tailwind"),
                features = listOf(
                    ProjectFeature("Secure JWT authentication and role-based access", false),
                    ProjectFeature("GitHub API integration showing repository metrics", false),
                    ProjectFeature("Relational schema design for student milestones", false),
                    ProjectFeature("Live demo containerized on Docker", false)
                ),
                whyItMattersForPlacement = "The gold standard full-stack project demonstrating architecture, relational databases, and clean frontend UX.",
                githubUrl = "https://github.com/shivam-yadav/devstack-hub",
                isCompleted = false
            ),
            ProjectItem(
                id = "proj_ai_tutor",
                title = "Shivaar Intelligence — Multimodal CSE Engineering Tutor (Applied AI)",
                tagline = "RAG document retrieval assistant answering university syllabus queries with citations",
                year = 3,
                semester = 5,
                techStack = listOf("Gemini API", "Python", "Vector DB", "FastAPI"),
                features = listOf(
                    ProjectFeature("Contextual syllabus retrieval using embeddings", false),
                    ProjectFeature("Hinglish technical concept explanation generator", false),
                    ProjectFeature("Automated viva practice test generator", false),
                    ProjectFeature("Grounding check against official textbook excerpts", false)
                ),
                whyItMattersForPlacement = "Directly qualifies for applied AI and modern generative software engineering roles.",
                githubUrl = "https://github.com/shivam-yadav/shivaar-ai-tutor",
                isCompleted = false
            )
        )

        fun getInitialCareerApplications(): List<CareerApplication> = listOf(
            CareerApplication(
                id = "app_google_step",
                company = "Google",
                role = "STEP Intern (Summer 2026/2027)",
                status = ApplicationStatus.WISHLIST,
                deadline = "Application Window: Late Nov / Dec",
                notes = "Focus on C++ / Python coding speed and strong academic GPA (8.5+).",
                stipendOrPackage = "Competitive Monthly Stipend + Mentorship"
            ),
            CareerApplication(
                id = "app_microsoft_engage",
                company = "Microsoft",
                role = "Student Mentorship & SWE Intern",
                status = ApplicationStatus.WISHLIST,
                deadline = "Spring 2026 Cohort",
                notes = "Requires building a working prototype project with clear problem statement.",
                stipendOrPackage = "Fast-track to Internship Interview"
            ),
            CareerApplication(
                id = "app_unstop_hackathon",
                company = "Smart India Hackathon (SIH)",
                role = "Student Innovation Team Lead",
                status = ApplicationStatus.APPLIED,
                deadline = "Team Submission: Nov 2025",
                notes = "Idea submitted on student academic tracking and AI syllabus guidance.",
                stipendOrPackage = "National Recognition & Cash Prizes"
            )
        )

        fun getInitialResearchSources(): List<ResearchSource> = listOf(
            ResearchSource(
                id = "src_aktu_syllabus",
                title = "AKTU B.Tech 1st Year Official Syllabus & Evaluation Scheme",
                category = "Academics",
                url = "https://aktu.ac.in",
                domain = "aktu.ac.in",
                summary = "Official PDF curriculum for Programming for Problem Solving (KCS-101), Engineering Maths (BAS-103), and exam marking schemes.",
                isBookmarked = true
            ),
            ResearchSource(
                id = "src_cppreference",
                title = "cppreference.com — Standard C & C++ Documentation",
                category = "Documentation",
                url = "https://en.cppreference.com",
                domain = "en.cppreference.com",
                summary = "Authoritative reference for C memory allocation, standard library algorithms, pointer syntax, and C++ STL containers.",
                isBookmarked = true
            ),
            ResearchSource(
                id = "src_python_docs",
                title = "Python 3 Official Tutorial & Data Structures Guide",
                category = "Documentation",
                url = "https://docs.python.org/3/tutorial/datastructures.html",
                domain = "docs.python.org",
                summary = "Official guide covering lists, comprehensions, sets, dictionaries, and loop performance in Python.",
                isBookmarked = true
            ),
            ResearchSource(
                id = "src_striver_a2z",
                title = "Striver's A2Z DSA Preparation Sheet",
                category = "Placement & DSA",
                url = "https://takeuforward.org/strivers-a2z-dsa-course/strivers-a2z-dsa-course-sheet-2",
                domain = "takeuforward.org",
                summary = "Curated step-by-step roadmap from basic arrays to advanced dynamic programming and graph algorithms.",
                isBookmarked = true
            )
        )
    }
}
