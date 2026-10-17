package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.PublicRoadmapsData
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
class PublicRoadmapsAndOnboardingStorageTest {

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
    fun testPublicRoadmapsDataCompleteness() {
        val roadmaps = PublicRoadmapsData.allRoadmaps
        assertTrue("Should contain comprehensive public roadmaps", roadmaps.size >= 8)

        // Verify C, C++, Python, DSA, Fullstack, AI, Agentic AI, Backend, Security, System Design
        val ids = roadmaps.map { it.id }
        assertTrue(ids.contains("c_programming"))
        assertTrue(ids.contains("cpp_programming"))
        assertTrue(ids.contains("python_mastery"))
        assertTrue(ids.contains("dsa_mastery"))
        assertTrue(ids.contains("fullstack_development"))
        assertTrue(ids.contains("ai_machine_learning"))
        assertTrue(ids.contains("agentic_ai"))
        assertTrue(ids.contains("backend_development"))
        assertTrue(ids.contains("cybersecurity"))
        assertTrue(ids.contains("system_design"))

        roadmaps.forEach { roadmap ->
            assertTrue("Roadmap ${roadmap.id} must have a title", roadmap.title.isNotBlank())
            assertTrue("Roadmap ${roadmap.id} must have estimated hours", roadmap.estimatedTotalHours > 0)
            assertTrue("Roadmap ${roadmap.id} must have milestones", roadmap.milestones.isNotEmpty())
            assertTrue("Roadmap ${roadmap.id} must have official doc links", roadmap.officialDocLinks.isNotEmpty())
        }
    }

    @Test
    fun testPublicRoadmapsSearchAndFilter() {
        val cResults = PublicRoadmapsData.searchRoadmaps("Pointers")
        assertTrue(cResults.any { it.id == "c_programming" })

        val aiResults = PublicRoadmapsData.searchRoadmaps(query = "", category = "Artificial Intelligence")
        assertTrue(aiResults.any { it.id == "ai_machine_learning" || it.id == "agentic_ai" })

        val advancedResults = PublicRoadmapsData.searchRoadmaps(query = "", level = Difficulty.ADVANCED)
        assertTrue(advancedResults.all { it.skillLevel == Difficulty.ADVANCED })
    }

    @Test
    fun testOnboardingDataStoragePersistence() {
        val testData = OnboardingData(
            step1Education = OnboardingEducation(
                qualification = "B.Tech CSE Core",
                stage = EducationStage.UNDERGRADUATE,
                fieldOfStudy = "Computer Science",
                currentYear = 1,
                currentSemester = 2,
                institution = "Lloyd Institute of Engineering & Technology",
                curriculum = "AKTU (Uttar Pradesh)",
                expectedGraduationDate = "2028",
                studiedSubjects = listOf("C Programming", "Engineering Mathematics", "Physics")
            ),
            step2Skills = OnboardingSkills(
                languagesKnown = listOf("C", "Python", "SQL"),
                topicsCompleted = listOf("Memory allocation", "Pointers"),
                projectsBuilt = listOf("CLI Student Record"),
                dsaExperience = "Arrays, Strings, Recursion",
                confidenceLevel = SkillConfidence.FAMILIAR
            ),
            step3Goals = OnboardingGoals(
                selectedGoals = listOf(CareerGoal.SWE_PLACEMENT, CareerGoal.AGENTIC_AI),
                targetTimelineMonths = 24,
                preferredRoles = listOf("Software Engineer", "AI Systems Engineer"),
                primaryPriority = "Core CS Foundations & Production Project"
            ),
            step4Interests = OnboardingInterests(
                interests = listOf(InterestArea.BUILDING_APPS, InterestArea.BUILDING_AI_SYSTEMS)
            ),
            step5Constraints = OnboardingConstraints(
                availableHoursPerWeek = 20,
                preferredSchedule = "Distributed Evenings",
                budgetRangeInr = "Free resources only (₹0)",
                preferredLanguage = "English"
            ),
            chosenPathId = "SWE & Agentic AI Track",
            currentStep = 4,
            isCompleted = false
        )

        storage.saveOnboardingData(testData)

        val loaded = storage.loadOnboardingData()
        assertNotNull(loaded)
        assertEquals("B.Tech CSE Core", loaded?.step1Education?.qualification)
        assertEquals(EducationStage.UNDERGRADUATE, loaded?.step1Education?.stage)
        assertEquals(4, loaded?.currentStep)
        assertEquals(2, loaded?.step3Goals?.selectedGoals?.size)
        assertEquals(20, loaded?.step5Constraints?.availableHoursPerWeek)
    }

    @Test
    fun testSavedAndFollowedRoadmapToggles() {
        val roadmapId = "agentic_ai"
        assertFalse(viewModel.savedRoadmapIds.value.contains(roadmapId))

        viewModel.toggleSaveRoadmap(roadmapId)
        assertTrue(viewModel.savedRoadmapIds.value.contains(roadmapId))

        // Verify fresh repository sees persisted value
        val freshRepo = ShivaarRepository(application)
        assertTrue(freshRepo.savedRoadmapIds.value.contains(roadmapId))

        viewModel.toggleSaveRoadmap(roadmapId)
        assertFalse(viewModel.savedRoadmapIds.value.contains(roadmapId))
    }

    @Test
    fun testAppDestinationTransitionsWithExploreRoadmaps() {
        assertEquals(AppDestination.TODAY, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.EXPLORE_ROADMAPS)
        assertEquals(AppDestination.EXPLORE_ROADMAPS, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.ROADMAP)
        assertEquals(AppDestination.ROADMAP, viewModel.currentTab.value)
    }
}
