package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PersonalizedRoadmapEngine
import com.example.data.ShivaarRepository
import com.example.data.ShivaarStorage
import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PersonalizedRoadmapAndExecutionTest {

    private lateinit var context: Context
    private lateinit var storage: ShivaarStorage
    private lateinit var repository: ShivaarRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        storage = ShivaarStorage(context)
        repository = ShivaarRepository(context = context, firestoreRepo = null)
    }

    @Test
    fun testPhase4UndergraduateBTechRoadmapGeneration() {
        val onboarding = OnboardingData(
            step1Education = OnboardingEducation(
                qualification = "B.Tech CSE",
                stage = EducationStage.UNDERGRADUATE,
                institution = "Lloyd Institute of Engineering & Technology",
                curriculum = "AKTU"
            ),
            step5Constraints = OnboardingConstraints(
                availableHoursPerWeek = 20,
                budgetRangeInr = "Free resources only (₹0)"
            ),
            chosenPathId = "path_fullstack_cloud"
        )

        val roadmap = PersonalizedRoadmapEngine.generatePersonalizedRoadmap(onboarding)

        // 48 full months for undergraduate engineering
        assertEquals(48, roadmap.size)

        val month1 = roadmap[0]
        assertEquals(1, month1.monthNumber)
        assertEquals(1, month1.year)
        assertEquals(1, month1.semester)
        assertTrue("Phase name must be populated", month1.phaseOrYearName.isNotBlank())
        assertTrue("Semester name must be populated", month1.semesterOrMilestoneName.isNotBlank())
        assertTrue("Learning objectives must be populated", month1.learningObjectives.isNotEmpty())
        assertTrue("Practice exercises must be populated", month1.practiceExercises.isNotEmpty())
        assertTrue("Definition of done must be populated", month1.definitionOfDone.isNotBlank())
        assertTrue("Revision schedule must be populated", month1.revisionSchedule.isNotBlank())
        assertEquals("₹0", month1.budget.totalInrEstimate)
        assertEquals(20, month1.weeklyAllocation.totalWeeklyHours)
    }

    @Test
    fun testPhase4AcceleratedProfessionalCareerSwitcherRoadmap() {
        val onboarding = OnboardingData(
            step1Education = OnboardingEducation(
                qualification = "B.Com",
                stage = EducationStage.CAREER_SWITCHER
            ),
            step5Constraints = OnboardingConstraints(
                availableHoursPerWeek = 25,
                budgetRangeInr = "₹5,000"
            ),
            chosenPathId = "path_systems_dsa"
        )

        val roadmap = PersonalizedRoadmapEngine.generatePersonalizedRoadmap(onboarding)

        // 12 months accelerated for career switchers
        assertEquals(12, roadmap.size)

        val m1 = roadmap[0]
        assertEquals("C Memory Model & Pointer Mechanics", m1.title)
        assertEquals(SkillTrack.PROGRAMMING_DSA, m1.primaryTrack)
        assertTrue(m1.learningObjectives.any { it.contains("pointer", ignoreCase = true) })
        assertTrue(m1.definitionOfDone.isNotBlank())
        assertEquals(25, m1.weeklyAllocation.totalWeeklyHours)
    }

    @Test
    fun testPhase4AgenticAiCurriculumGeneration() {
        val onboarding = OnboardingData(
            step1Education = OnboardingEducation(
                stage = EducationStage.WORKING_PROFESSIONAL
            ),
            chosenPathId = "path_agentic_ai"
        )

        val roadmap = PersonalizedRoadmapEngine.generatePersonalizedRoadmap(onboarding)
        assertEquals(12, roadmap.size)

        val m2 = roadmap[1]
        assertTrue(m2.title.contains("LLM", ignoreCase = true) || m2.mainFocus.contains("Gemini", ignoreCase = true))
        assertEquals(SkillTrack.AI_ML, m2.primaryTrack)
    }

    @Test
    fun testPhase5RoadmapToExecutionDailyTaskSpawner() {
        val month = RoadmapMonth(
            id = "test_month_1",
            monthNumber = 1,
            year = 1,
            semester = 1,
            title = "Modern TypeScript & Concurrency",
            mainFocus = "TypeScript strict typing, closures, Promises, Event Loop",
            secondarySkill = "Git",
            maintenanceSkill = "Testing",
            primaryTrack = SkillTrack.DEV_PROJECTS,
            topics = listOf("Type inference", "Generics", "Async/Await"),
            dsaTarget = "Two Pointers on Arrays",
            cumulativeDsaGoal = 25,
            projectMilestone = "CLI Task Management Tool",
            collegeAcademicFocus = "Standard RFCs",
            coreCsTarget = "Event loop microtasks",
            weeklyAllocation = WeeklyTimeAllocation(6, 8, 4, 0, 18),
            estimatedMonthlyHours = 72,
            measurableOutput = "Tested CLI tool with 90% test coverage",
            readinessCheckpoint = "Can explain Promise microtasks",
            budget = BudgetEstimate("Free", "None", "None", "₹0"),
            optionalActivities = emptyList(),
            prerequisites = listOf("Basic syntax"),
            revisionRequirements = "Sunday flashcards",
            failureRecoveryInstructions = "Focus on core project first",
            learningObjectives = listOf("Master TypeScript strict typing", "Write asynchronous coroutines"),
            practiceExercises = listOf("Implement Promise.all polyfill", "Solve 15 Two-Pointer problems"),
            definitionOfDone = "100% test pass on CI and clean GitHub commit.",
            revisionSchedule = "Sunday 45-min active recall."
        )

        val initialTaskCount = repository.tasks.value.size
        repository.generateDailyTasksFromMonth(month)

        val updatedTasks = repository.tasks.value
        assertTrue("New tasks should be added from milestone", updatedTasks.size > initialTaskCount)

        val studyTask = updatedTasks.find { it.title.contains("Study:", ignoreCase = true) }
        assertNotNull("Study task should be spawned", studyTask)
        assertEquals(Priority.HIGH, studyTask?.priority)
        assertEquals(month.definitionOfDone, studyTask?.definitionOfDone)

        val dsaTask = updatedTasks.find { it.category == TaskCategory.DSA }
        assertNotNull("DSA task should be spawned", dsaTask)

        val projTask = updatedTasks.find { it.title.contains("Milestone Build", ignoreCase = true) }
        assertNotNull("Project task should be spawned", projTask)

        val revTask = updatedTasks.find { it.title.contains("Spaced Recall", ignoreCase = true) }
        assertNotNull("Revision task should be spawned", revTask)
    }

    @Test
    fun testPhase5AdvanceToNextMilestone() {
        val onboarding = OnboardingData(
            step1Education = OnboardingEducation(stage = EducationStage.CAREER_SWITCHER),
            chosenPathId = "path_fullstack_cloud"
        )
        repository.saveOnboardingProgress(onboarding)
        repository.generateAndApplyPersonalizedRoadmap(onboarding)

        val initialMonths = repository.roadmapMonths.value
        assertTrue(initialMonths.isNotEmpty())
        val month1 = initialMonths[0]
        assertTrue(month1.isCurrentMonth)
        assertFalse(month1.isCompleted)

        // Advance to next milestone
        repository.advanceToNextMilestone(month1.id)

        val updatedMonths = repository.roadmapMonths.value
        val updatedMonth1 = updatedMonths.find { it.id == month1.id }
        val updatedMonth2 = updatedMonths[1]

        assertTrue("Month 1 should now be completed", updatedMonth1?.isCompleted == true)
        assertFalse("Month 1 should no longer be current", updatedMonth1?.isCurrentMonth == true)
        assertTrue("Month 2 should now be current", updatedMonth2.isCurrentMonth)
        assertEquals(2, repository.userProfile.value.currentMonth)
    }

    @Test
    fun testPhase5AdaptiveScheduleRecovery() {
        // Add a low priority task and a normal task
        val lowPriorityTask = TaskItem(
            id = "low_pri_1",
            title = "Optional documentation browsing",
            category = TaskCategory.DEV,
            whyItMatters = "Nice to have",
            instructions = "Read blog post",
            durationMinutes = 20,
            priority = Priority.LOW,
            difficulty = Difficulty.BEGINNER,
            resourceName = "Blog",
            definitionOfDone = "Read"
        )
        repository.addNewTask(lowPriorityTask.title, lowPriorityTask.category, lowPriorityTask.durationMinutes, lowPriorityTask.priority, lowPriorityTask.whyItMatters, lowPriorityTask.definitionOfDone)

        // Trigger adaptive recovery
        repository.triggerAdaptiveRecovery()

        val tasks = repository.tasks.value
        val recoveryTask = tasks.find { it.title.contains("Adaptive Catch-Up Sprint", ignoreCase = true) }
        assertNotNull("High-priority adaptive catch up task must be spawned", recoveryTask)
        assertEquals(Priority.HIGH, recoveryTask?.priority)
    }
}
