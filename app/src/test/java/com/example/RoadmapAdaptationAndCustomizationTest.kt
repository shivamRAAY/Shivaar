package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PersonalizedRoadmapEngine
import com.example.data.PublicRoadmapsData
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
class RoadmapAdaptationAndCustomizationTest {

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
    fun testPhase6PublicRoadmapConversionAndWeeklyHoursScaling() {
        val publicRoadmap = PublicRoadmapsData.allRoadmaps.first { it.id == "c_programming" }
        assertNotNull(publicRoadmap)

        val onboarding = OnboardingData(
            step5Constraints = OnboardingConstraints(
                availableHoursPerWeek = 24,
                budgetRangeInr = "Free resources only (₹0)"
            )
        )

        val adapted = PersonalizedRoadmapEngine.adaptPublicRoadmapToPersonalCurriculum(publicRoadmap, onboarding)

        // Matches milestone count of public roadmap
        assertEquals(publicRoadmap.milestones.size, adapted.size)

        val m1 = adapted[0]
        assertEquals("c_programming_month_1", m1.id)
        assertEquals(1, m1.monthNumber)
        assertEquals(publicRoadmap.milestones[0].title, m1.title)
        assertTrue(m1.isCurrentMonth)
        assertFalse(m1.isCompleted)
        assertEquals(24, m1.weeklyAllocation.totalWeeklyHours)
        assertTrue("Learning objectives must be populated", m1.learningObjectives.isNotEmpty())
        assertTrue("Practice exercises must be populated", m1.practiceExercises.isNotEmpty())
        assertTrue("Definition of done must be populated", m1.definitionOfDone.isNotBlank())
        assertEquals("₹0", m1.budget.totalInrEstimate)
        assertEquals(SkillTrack.PROGRAMMING_DSA, m1.primaryTrack)
    }

    @Test
    fun testPhase6AdoptPublicRoadmapInRepository() {
        val roadmapId = "c_programming"
        val initialTaskCount = repository.tasks.value.size

        // Adopt the public roadmap as active curriculum
        repository.adoptPublicRoadmap(roadmapId, autoSpawnTasks = true)

        assertEquals(roadmapId, repository.adoptedRoadmapId.value)
        assertTrue(repository.followedRoadmapIds.value.contains(roadmapId))

        val currentMonths = repository.roadmapMonths.value
        assertTrue(currentMonths.isNotEmpty())
        assertEquals("c_programming_month_1", currentMonths[0].id)
        assertTrue(currentMonths[0].isCurrentMonth)

        // Profile track updated
        assertTrue(repository.userProfile.value.primaryTrack.contains("C Programming", ignoreCase = true))

        // Daily tasks seeded to Today
        val updatedTasks = repository.tasks.value
        assertTrue(updatedTasks.size > initialTaskCount)
        val studyTask = updatedTasks.find { it.title.contains("Study:", ignoreCase = true) }
        assertNotNull(studyTask)
    }

    @Test
    fun testPhase6MilestoneCustomNotesPersistence() {
        val months = repository.roadmapMonths.value
        assertTrue(months.isNotEmpty())
        val targetMonth = months[0]

        val personalNotes = "Target: Read K&R Chapter 5 by Thursday and implement pointer tests on Linux."
        repository.updateMilestoneCustomNotes(targetMonth.id, personalNotes)

        // Verify state flow updated
        val updatedNotesMap = repository.milestoneCustomNotes.value
        assertEquals(personalNotes, updatedNotesMap[targetMonth.id])

        // Verify active month model reflects note
        val updatedMonth = repository.roadmapMonths.value.find { it.id == targetMonth.id }
        assertEquals(personalNotes, updatedMonth?.customNotes)

        // Verify storage persistence
        val reloadedStorage = ShivaarStorage(context)
        val loadedNotes = reloadedStorage.loadMilestoneCustomNotes()
        assertEquals(personalNotes, loadedNotes[targetMonth.id])
    }

    @Test
    fun testPhase6MilestoneExerciseCheckboxesToggle() {
        val months = repository.roadmapMonths.value
        assertTrue(months.isNotEmpty())
        val targetMonth = months[0]

        // Toggle exercise 0
        repository.toggleMilestoneExercise(targetMonth.id, 0)
        var updatedMonth = repository.roadmapMonths.value.find { it.id == targetMonth.id }
        assertTrue(updatedMonth?.completedExercises?.contains(0) == true)

        // Toggle exercise 1
        repository.toggleMilestoneExercise(targetMonth.id, 1)
        updatedMonth = repository.roadmapMonths.value.find { it.id == targetMonth.id }
        assertTrue(updatedMonth?.completedExercises?.contains(0) == true)
        assertTrue(updatedMonth?.completedExercises?.contains(1) == true)

        // Untoggle exercise 0
        repository.toggleMilestoneExercise(targetMonth.id, 0)
        updatedMonth = repository.roadmapMonths.value.find { it.id == targetMonth.id }
        assertFalse(updatedMonth?.completedExercises?.contains(0) == true)
        assertTrue(updatedMonth?.completedExercises?.contains(1) == true)
    }

    @Test
    fun testPhase6ExportCurriculumMarkdown() {
        // Adopt roadmap
        repository.adoptPublicRoadmap("c_programming", autoSpawnTasks = false)

        val markdown = repository.getCurriculumExportMarkdown()
        assertNotNull(markdown)
        assertTrue("Must include curriculum header", markdown.contains("# Shivaar 2.0 Study Curriculum:"))
        assertTrue("Must include month breakdown", markdown.contains("## Month 1:"))
        assertTrue("Must include Definition of Done", markdown.contains("Definition of Done"))
        assertTrue("Must include practice exercises", markdown.contains("### Practice Exercises"))
    }
}
