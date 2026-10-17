package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShivaarRepository
import com.example.data.model.*
import com.example.ui.ShivaarViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TodayDashboardActionsTest {

    private lateinit var application: Application
    private lateinit var repository: ShivaarRepository
    private lateinit var viewModel: ShivaarViewModel

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        // Clear prefs for a clean test run
        application.getSharedPreferences("shivaar_storage_prefs", Application.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        repository = ShivaarRepository(application)
        viewModel = ShivaarViewModel(application, repository)
    }

    @Test
    fun testInitialTasksLoadedAndDefaultsPresent() {
        val initialTasks = viewModel.tasks.value
        assertTrue("Initial tasks should not be empty", initialTasks.isNotEmpty())
        val initialTomorrow = viewModel.tomorrowTasks.value
        assertTrue("Tomorrow preview tasks should not be empty", initialTomorrow.isNotEmpty())
    }

    @Test
    fun testTaskCreationUpdatesStateAndPersists() {
        val initialCount = viewModel.tasks.value.size
        val title = "Write C Doubly Linked List in CLion"

        viewModel.addNewTask(
            title = title,
            category = TaskCategory.C_PROGRAMMING,
            duration = 45,
            priority = Priority.HIGH,
            why = "Core foundation for tree and graph pointers",
            dod = "Compile clean with zero memory leaks"
        )

        val updatedTasks = viewModel.tasks.value
        assertEquals("Task count should increase by 1", initialCount + 1, updatedTasks.size)
        val created = updatedTasks.first { it.title == title }
        assertEquals(TaskCategory.C_PROGRAMMING, created.category)
        assertEquals(45, created.durationMinutes)
        assertEquals(Priority.HIGH, created.priority)

        // Verify persistence: instantiate fresh repository with same context
        val freshRepo = ShivaarRepository(application)
        val persistedTasks = freshRepo.tasks.value
        assertTrue("Created task should persist in fresh repository", persistedTasks.any { it.title == title })
    }

    @Test
    fun testTaskCompletionRecalculatesProgressAndRecordsHistory() {
        val task = viewModel.tasks.value.first()
        val initialCompleted = viewModel.completedTasksCount.value
        val initialMins = viewModel.completedMinutes.value

        viewModel.markTaskComplete(task.id, notes = "Completed all pointer test cases", minutesSpent = 35)

        val updatedTasks = viewModel.tasks.value
        val completedTask = updatedTasks.first { it.id == task.id }
        assertEquals(TaskStatus.COMPLETED, completedTask.status)
        assertEquals("Completed all pointer test cases", completedTask.userNotes)
        assertEquals(35, completedTask.actualMinutes)
        assertNotNull(completedTask.completedAt)

        // Check history
        val history = viewModel.completionHistory.value
        assertTrue("Completion history should contain the completed task", history.any { it.taskId == task.id })
        val record = history.first { it.taskId == task.id }
        assertEquals("Completed all pointer test cases", record.notes)
        assertEquals(35, record.actualMinutes)

        // Verify persistence in fresh repository
        val freshRepo = ShivaarRepository(application)
        assertTrue("Completion history should persist", freshRepo.completionHistory.value.any { it.taskId == task.id })
    }

    @Test
    fun testTaskPauseAndResumeActions() {
        val task = viewModel.tasks.value.first()

        // Start task
        viewModel.markTaskInProgress(task.id)
        assertEquals(TaskStatus.IN_PROGRESS, viewModel.tasks.value.first { it.id == task.id }.status)

        // Pause task
        viewModel.markTaskPaused(task.id)
        assertEquals(TaskStatus.PAUSED, viewModel.tasks.value.first { it.id == task.id }.status)

        // Restore or resume
        viewModel.markTaskInProgress(task.id)
        assertEquals(TaskStatus.IN_PROGRESS, viewModel.tasks.value.first { it.id == task.id }.status)
    }

    @Test
    fun testTaskSkipWithReason() {
        val task = viewModel.tasks.value.first()

        viewModel.markTaskSkipped(task.id, "Covered in Lloyd college lecture")

        val skipped = viewModel.tasks.value.first { it.id == task.id }
        assertEquals(TaskStatus.SKIPPED, skipped.status)
        assertEquals("Covered in Lloyd college lecture", skipped.blockerReason)

        // Restoring skipped task
        viewModel.restoreTask(task.id)
        val restored = viewModel.tasks.value.first { it.id == task.id }
        assertEquals(TaskStatus.PENDING, restored.status)
        assertNull(restored.blockerReason)
    }

    @Test
    fun testTaskReschedulingBidirectional() {
        val todayTask = viewModel.tasks.value.first()
        val todayInitialSize = viewModel.tasks.value.size
        val tomorrowInitialSize = viewModel.tomorrowTasks.value.size

        // Reschedule to tomorrow
        viewModel.rescheduleTask(todayTask.id, toTomorrow = true)

        assertEquals(todayInitialSize - 1, viewModel.tasks.value.size)
        assertEquals(tomorrowInitialSize + 1, viewModel.tomorrowTasks.value.size)
        assertTrue(viewModel.tomorrowTasks.value.any { it.id == todayTask.id })

        // Move back to today
        viewModel.moveTomorrowTaskToToday(todayTask.id)

        assertEquals(todayInitialSize, viewModel.tasks.value.size)
        assertEquals(tomorrowInitialSize, viewModel.tomorrowTasks.value.size)
        assertTrue(viewModel.tasks.value.any { it.id == todayTask.id })
    }

    @Test
    fun testTaskNotesUpdateWithoutStatusChange() {
        val task = viewModel.tasks.value.first()
        val originalStatus = task.status

        viewModel.updateTaskNotes(task.id, "Pointer arithmetic rule: *(p + i) == p[i]")

        val updated = viewModel.tasks.value.first { it.id == task.id }
        assertEquals(originalStatus, updated.status)
        assertEquals("Pointer arithmetic rule: *(p + i) == p[i]", updated.userNotes)
    }

    @Test
    fun testFocusSessionLifecycle() {
        val task = viewModel.tasks.value.first()

        // Start session
        viewModel.startFocusSession(task)
        val session1 = viewModel.focusSession.value
        assertTrue(session1.isActive)
        assertEquals(task.id, session1.task?.id)
        assertFalse(session1.isPaused)
        assertEquals(TaskStatus.IN_PROGRESS, viewModel.tasks.value.first { it.id == task.id }.status)

        // Pause session
        viewModel.togglePauseFocusSession()
        val session2 = viewModel.focusSession.value
        assertTrue(session2.isActive)
        assertTrue(session2.isPaused)
        assertEquals(TaskStatus.PAUSED, viewModel.tasks.value.first { it.id == task.id }.status)

        // Resume session
        viewModel.togglePauseFocusSession()
        val session3 = viewModel.focusSession.value
        assertTrue(session3.isActive)
        assertFalse(session3.isPaused)
        assertEquals(TaskStatus.IN_PROGRESS, viewModel.tasks.value.first { it.id == task.id }.status)

        // Complete session
        viewModel.completeFocusSession(notes = "Mastered recursion base conditions")
        assertFalse(viewModel.focusSession.value.isActive)
        val finishedTask = viewModel.tasks.value.first { it.id == task.id }
        assertEquals(TaskStatus.COMPLETED, finishedTask.status)
        assertEquals("Mastered recursion base conditions", finishedTask.userNotes)
    }
}
