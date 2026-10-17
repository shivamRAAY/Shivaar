package com.example

import com.example.data.ShivaarRepository
import com.example.data.model.GateStatus
import com.example.data.model.ProblemStatus
import com.example.data.model.TaskStatus
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ShivaarUnitTest {

    private lateinit var repository: ShivaarRepository

    @Before
    fun setup() {
        repository = ShivaarRepository()
    }

    @Test
    fun testTaskCompletionUpdatesStateAndMinutes() {
        val initialTasks = repository.tasks.value
        assertFalse(initialTasks.isEmpty())

        val targetTask = initialTasks.first()
        repository.updateTaskStatus(targetTask.id, TaskStatus.COMPLETED, notes = "Verified code output", minutesSpent = 45)

        val updatedTask = repository.tasks.value.first { it.id == targetTask.id }
        assertEquals(TaskStatus.COMPLETED, updatedTask.status)
        assertEquals("Verified code output", updatedTask.userNotes)
        assertEquals(45, updatedTask.actualMinutes)
        assertNotNull(updatedTask.completedAt)
    }

    @Test
    fun testRescheduleTaskMovesToTomorrow() {
        val initialTodayCount = repository.tasks.value.size
        val initialTomorrowCount = repository.tomorrowTasks.value.size
        val targetTask = repository.tasks.value.first()

        repository.rescheduleTask(targetTask.id, toTomorrow = true)

        assertEquals(initialTodayCount - 1, repository.tasks.value.size)
        assertEquals(initialTomorrowCount + 1, repository.tomorrowTasks.value.size)
        assertTrue(repository.tomorrowTasks.value.any { it.id == targetTask.id })
    }

    @Test
    fun testAttendanceIncrement() {
        val subject = repository.subjects.value.first()
        val initialAttended = subject.attendedClasses
        val initialTotal = subject.totalClasses

        repository.incrementAttendance(subject.id, attended = true)

        val updated = repository.subjects.value.first { it.id == subject.id }
        assertEquals(initialAttended + 1, updated.attendedClasses)
        assertEquals(initialTotal + 1, updated.totalClasses)
    }

    @Test
    fun testDsaProblemStatusUpdate() {
        val prob = repository.dsaProblems.value.first()
        repository.updateDsaProblemStatus(prob.id, ProblemStatus.SOLVED, notes = "Remember O(n) space", snippet = "// solution")

        val updated = repository.dsaProblems.value.first { it.id == prob.id }
        assertEquals(ProblemStatus.SOLVED, updated.status)
        assertEquals("Remember O(n) space", updated.mistakeNote)
    }

    @Test
    fun testAll48MonthsExistAndHaveRequiredData() {
        val months = repository.roadmapMonths.value
        assertEquals("All 48 months must exist", 48, months.size)

        // Verify each month is populated with valid fields
        months.forEachIndexed { index, month ->
            assertEquals(index + 1, month.monthNumber)
            assertTrue("Month ${month.monthNumber} title must not be blank", month.title.isNotBlank())
            assertTrue("Month ${month.monthNumber} main focus must not be blank", month.mainFocus.isNotBlank())
            assertFalse("Month ${month.monthNumber} must have topics", month.topics.isEmpty())
            assertTrue("Month ${month.monthNumber} must have measurable output", month.measurableOutput.isNotBlank())
            assertTrue("Month ${month.monthNumber} must have readiness checkpoint", month.readinessCheckpoint.isNotBlank())
            assertTrue("Month ${month.monthNumber} weekly hours must be > 0", month.weeklyAllocation.totalWeeklyHours > 0)
            assertTrue("Month ${month.monthNumber} monthly hours must be > 0", month.estimatedMonthlyHours > 0)
            assertTrue("Month ${month.monthNumber} cumulative DSA must be > 0", month.cumulativeDsaGoal > 0)
        }

        // Check years and semesters distributions
        assertEquals(12, months.count { it.year == 1 })
        assertEquals(12, months.count { it.year == 2 })
        assertEquals(12, months.count { it.year == 3 })
        assertEquals(12, months.count { it.year == 4 })

        for (sem in 1..8) {
            assertEquals("Semester $sem must have 6 months", 6, months.count { it.semester == sem })
        }
    }

    @Test
    fun testAll8PrerequisiteGatesExist() {
        val gates = repository.prerequisiteGates.value
        assertEquals("All 8 prerequisite assessment gates must exist", 8, gates.size)

        gates.forEachIndexed { index, gate ->
            assertEquals(index + 1, gate.number)
            assertTrue(gate.name.isNotBlank())
            assertTrue(gate.practicalEvidence.isNotBlank())
            assertTrue(gate.remediationSteps.isNotBlank())
            assertFalse(gate.completionConditions.isEmpty())
        }
    }

    @Test
    fun testGenerateDailyTasksFromMonth() {
        val initialTaskCount = repository.tasks.value.size
        val month2 = repository.roadmapMonths.value[1] // Month 2

        repository.generateDailyTasksFromMonth(month2)

        val updatedTasks = repository.tasks.value
        assertEquals("Generating tasks from a month adds 3 structured tasks", initialTaskCount + 3, updatedTasks.size)
        assertTrue(updatedTasks.any { it.title.contains("DSA Sprint") })
        assertTrue(updatedTasks.any { it.title.contains("Study:") })
    }

    @Test
    fun testToggleMonthCompletion() {
        val targetMonth = repository.roadmapMonths.value.first()
        assertFalse(targetMonth.isCompleted)

        repository.toggleMonthCompletion(targetMonth.id)
        assertTrue(repository.roadmapMonths.value.first().isCompleted)

        repository.toggleMonthCompletion(targetMonth.id)
        assertFalse(repository.roadmapMonths.value.first().isCompleted)
    }

    @Test
    fun testUpdateGateStatus() {
        val gate1 = repository.prerequisiteGates.value.first()
        repository.updateGateStatus(gate1.id, GateStatus.MASTERED)

        val updatedGate = repository.prerequisiteGates.value.first { it.id == gate1.id }
        assertEquals(GateStatus.MASTERED, updatedGate.status)
    }
}
