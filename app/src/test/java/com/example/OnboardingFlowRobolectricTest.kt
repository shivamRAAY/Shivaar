package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShivaarRepository
import com.example.data.ShivaarStorage
import com.example.data.model.*
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class OnboardingFlowRobolectricTest {

    private lateinit var application: Application
    private lateinit var storage: ShivaarStorage
    private lateinit var repository: ShivaarRepository
    private lateinit var viewModel: ShivaarViewModel

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        application.getSharedPreferences("shivaar_storage_prefs", Application.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        storage = ShivaarStorage(application)
        repository = ShivaarRepository(application)
        viewModel = ShivaarViewModel(application, repository)
    }

    @Test
    fun testInitialOnboardingStateAndNavigation() {
        val onboarding = viewModel.onboardingData.value
        assertEquals(1, onboarding.currentStep)
        assertFalse(onboarding.isCompleted)

        viewModel.navigateTo(AppDestination.ONBOARDING)
        assertEquals(AppDestination.ONBOARDING, viewModel.currentTab.value)
    }

    @Test
    fun testProgressiveStepSavingAndProfileSync() {
        // Step 1: Education update
        val updatedEducation = OnboardingEducation(
            qualification = "B.Tech Computer Science & Engineering",
            stage = EducationStage.UNDERGRADUATE,
            fieldOfStudy = "Artificial Intelligence",
            currentYear = 2,
            currentSemester = 3,
            institution = "Lloyd Institute of Engineering & Technology",
            curriculum = "AKTU (Uttar Pradesh)",
            expectedGraduationDate = "2027",
            studiedSubjects = listOf("C Programming", "Discrete Mathematics", "Data Structures")
        )

        val updatedGoals = OnboardingGoals(
            selectedGoals = listOf(CareerGoal.SWE_PLACEMENT, CareerGoal.AGENTIC_AI),
            customGoal = "Build scalable multi-agent systems",
            targetTimelineMonths = 24,
            preferredRoles = listOf("AI Systems Engineer", "Backend Developer"),
            primaryPriority = "Master OS memory model and Agentic AI workflows"
        )

        val updatedConstraints = OnboardingConstraints(
            availableHoursPerWeek = 21,
            preferredSchedule = "Evenings & Weekends",
            budgetRangeInr = "Free resources only (₹0)",
            preferredLanguage = "English"
        )

        val stepData = OnboardingData(
            step1Education = updatedEducation,
            step2Skills = OnboardingSkills(
                languagesKnown = listOf("C", "Python", "Kotlin"),
                topicsCompleted = listOf("Memory Management", "Algorithms", "Goroutines"),
                projectsBuilt = listOf("In-Memory KV Cache"),
                dsaExperience = "Arrays, Graphs, Sliding Window",
                devExperience = "REST APIs and Ktor Backend",
                confidenceLevel = SkillConfidence.INTERMEDIATE,
                diagnosticResult = DiagnosticResult(totalScore = 4, maxScore = 5, verifiedLevel = "Advanced Foundation")
            ),
            step3Goals = updatedGoals,
            step4Interests = OnboardingInterests(
                interests = listOf(InterestArea.BUILDING_AI_SYSTEMS, InterestArea.OPERATING_SYSTEMS)
            ),
            step5Constraints = updatedConstraints,
            currentStep = 3,
            isCompleted = false
        )

        viewModel.saveOnboardingProgress(stepData)

        // Verify local reactive state
        val saved = viewModel.onboardingData.value
        assertEquals(3, saved.currentStep)
        assertEquals("B.Tech Computer Science & Engineering", saved.step1Education.qualification)
        assertEquals(4, saved.step2Skills.diagnosticResult?.totalScore)
        assertEquals(2, saved.step3Goals.selectedGoals.size)

        // Verify automatic profile synchronization: budget should equal (21 * 60) / 7 = 180 mins
        val profile = viewModel.userProfile.value
        assertEquals(180, profile.dailyBudgetMinutes)
        assertEquals(3, profile.currentSemester)
        assertTrue(profile.degree.contains("B.Tech"))

        // Verify cold persistence from disk
        val freshRepo = ShivaarRepository(application)
        val reloadedOnboarding = freshRepo.onboardingData.value
        assertEquals(3, reloadedOnboarding.currentStep)
        assertEquals(21, reloadedOnboarding.step5Constraints.availableHoursPerWeek)
    }

    @Test
    fun testOnboardingCompletionLifecycle() {
        val completedData = OnboardingData(
            step1Education = OnboardingEducation(),
            step2Skills = OnboardingSkills(),
            step3Goals = OnboardingGoals(),
            step4Interests = OnboardingInterests(),
            step5Constraints = OnboardingConstraints(),
            currentStep = 5,
            isCompleted = true,
            completedAt = System.currentTimeMillis()
        )

        viewModel.saveOnboardingProgress(completedData)

        assertTrue("Onboarding should be marked completed", viewModel.onboardingData.value.isCompleted)
        assertNotNull(viewModel.onboardingData.value.completedAt)

        val freshStorage = ShivaarStorage(application)
        val loaded = freshStorage.loadOnboardingData()
        assertNotNull(loaded)
        assertTrue(loaded?.isCompleted == true)
    }

    @Test
    fun testSelfReportedConfidenceVsDiagnosticDistinction() {
        val skillsWithDiagnostic = OnboardingSkills(
            confidenceLevel = SkillConfidence.ADVANCED, // Self-reported
            diagnosticResult = DiagnosticResult(
                totalScore = 2,
                maxScore = 5,
                verifiedLevel = "Intermediate",
                summaryFeedback = "Verified diagnostic baseline: 2/5."
            )
        )

        assertNotEquals(
            skillsWithDiagnostic.confidenceLevel.name,
            skillsWithDiagnostic.diagnosticResult?.verifiedLevel
        )
        assertEquals(2, skillsWithDiagnostic.diagnosticResult?.totalScore)
    }
}
