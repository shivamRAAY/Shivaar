package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShivaarRepository
import com.example.data.ShivaarStorage
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
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var app: Application

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        app = ApplicationProvider.getApplicationContext<Application>()
        // Clear prefs before test
        val prefs = context.getSharedPreferences("shivaar_storage_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Shivaar", appName)
    }

    @Test
    fun `test task creation and persistence`() {
        val repo = ShivaarRepository(context)
        val initialCount = repo.tasks.value.size

        repo.addNewTask(
            title = "Write C Binary Search Function",
            category = TaskCategory.C_PROGRAMMING,
            duration = 45,
            priority = Priority.HIGH,
            why = "Foundation for DSA search algorithms",
            dod = "All test cases pass without bounds errors"
        )

        assertEquals(initialCount + 1, repo.tasks.value.size)
        val created = repo.tasks.value.first()
        assertEquals("Write C Binary Search Function", created.title)
        assertEquals(TaskCategory.C_PROGRAMMING, created.category)
        assertEquals(45, created.durationMinutes)
        assertEquals(TaskStatus.PENDING, created.status)

        // Verify loaded in new repo instance
        val newRepo = ShivaarRepository(context)
        val reloadedTask = newRepo.tasks.value.find { it.title == "Write C Binary Search Function" }
        assertNotNull(reloadedTask)
        assertEquals(45, reloadedTask?.durationMinutes)
    }

    @Test
    fun `test task pause and resume lifecycle`() {
        val repo = ShivaarRepository(context)
        val firstTask = repo.tasks.value.first()

        // Start task
        repo.updateTaskStatus(firstTask.id, TaskStatus.IN_PROGRESS)
        assertEquals(TaskStatus.IN_PROGRESS, repo.tasks.value.find { it.id == firstTask.id }?.status)

        // Pause task
        repo.markTaskPaused(firstTask.id)
        assertEquals(TaskStatus.PAUSED, repo.tasks.value.find { it.id == firstTask.id }?.status)

        // Restore / resume
        repo.restoreTask(firstTask.id)
        assertEquals(TaskStatus.PENDING, repo.tasks.value.find { it.id == firstTask.id }?.status)
    }

    @Test
    fun `test task completion and completion history persistence`() {
        val repo = ShivaarRepository(context)
        val task = repo.tasks.value.first()

        repo.updateTaskStatus(
            taskId = task.id,
            newStatus = TaskStatus.COMPLETED,
            notes = "Completed with zero memory leaks",
            minutesSpent = 35
        )

        val updated = repo.tasks.value.find { it.id == task.id }
        assertEquals(TaskStatus.COMPLETED, updated?.status)
        assertEquals(35, updated?.actualMinutes)
        assertEquals("Completed with zero memory leaks", updated?.userNotes)

        // Verify completion history record
        val history = repo.completionHistory.value
        assertTrue("Completion history should not be empty", history.isNotEmpty())
        val record = history.first()
        assertEquals(task.id, record.taskId)
        assertEquals(task.title, record.taskTitle)
        assertEquals("Completed with zero memory leaks", record.notes)
        assertEquals(35, record.actualMinutes)

        // Verify persistence in independent repository instance
        val newRepo = ShivaarRepository(context)
        val reloadedHistory = newRepo.completionHistory.value
        assertEquals(1, reloadedHistory.size)
        assertEquals(task.title, reloadedHistory.first().taskTitle)
    }

    @Test
    fun `test task skip with reason`() {
        val repo = ShivaarRepository(context)
        val task = repo.tasks.value.first()

        repo.markTaskSkipped(task.id, "Attended university placement talk instead")
        val skipped = repo.tasks.value.find { it.id == task.id }
        assertEquals(TaskStatus.SKIPPED, skipped?.status)
        assertEquals("Attended university placement talk instead", skipped?.blockerReason)

        // Restore from skip
        repo.restoreTask(task.id)
        val restored = repo.tasks.value.find { it.id == task.id }
        assertEquals(TaskStatus.PENDING, restored?.status)
        assertNull(restored?.blockerReason)
    }

    @Test
    fun `test rescheduling to tomorrow and back to today`() {
        val repo = ShivaarRepository(context)
        val initialTodayCount = repo.tasks.value.size
        val initialTomorrowCount = repo.tomorrowTasks.value.size
        val taskToMove = repo.tasks.value.first()

        // Reschedule to tomorrow
        repo.rescheduleTask(taskToMove.id, toTomorrow = true)
        assertEquals(initialTodayCount - 1, repo.tasks.value.size)
        assertEquals(initialTomorrowCount + 1, repo.tomorrowTasks.value.size)
        val movedTask = repo.tomorrowTasks.value.find { it.id == taskToMove.id }
        assertNotNull(movedTask)
        assertEquals("Tomorrow", movedTask?.scheduledDate)

        // Move back to today
        repo.moveTomorrowTaskToToday(taskToMove.id)
        assertEquals(initialTodayCount, repo.tasks.value.size)
        assertEquals(initialTomorrowCount, repo.tomorrowTasks.value.size)
        val movedBack = repo.tasks.value.find { it.id == taskToMove.id }
        assertNotNull(movedBack)
        assertEquals("Today", movedBack?.scheduledDate)
    }

    @Test
    fun `test task notes update`() {
        val repo = ShivaarRepository(context)
        val task = repo.tasks.value.first()

        repo.updateTaskNotes(task.id, "Remember: *(p + i) accesses offset i in C pointers")
        val updated = repo.tasks.value.find { it.id == task.id }
        assertEquals("Remember: *(p + i) accesses offset i in C pointers", updated?.userNotes)

        // Verify persistent
        val reloaded = ShivaarRepository(context)
        assertEquals("Remember: *(p + i) accesses offset i in C pointers", reloaded.tasks.value.find { it.id == task.id }?.userNotes)
    }

    @Test
    fun `test viewmodel focus session flow`() {
        val repo = ShivaarRepository(context)
        val viewModel = ShivaarViewModel(app, repo)
        val task = repo.tasks.value.first()

        // Start focus session
        viewModel.startFocusSession(task)
        val sessionState = viewModel.focusSession.value
        assertTrue(sessionState.isActive)
        assertEquals(task.id, sessionState.task?.id)
        assertEquals(TaskStatus.IN_PROGRESS, repo.tasks.value.find { it.id == task.id }?.status)

        // Pause focus session
        viewModel.togglePauseFocusSession()
        assertTrue(viewModel.focusSession.value.isPaused)
        assertEquals(TaskStatus.PAUSED, repo.tasks.value.find { it.id == task.id }?.status)

        // Complete focus session
        viewModel.completeFocusSession("Session finished successfully")
        assertFalse(viewModel.focusSession.value.isActive)
        val completedTask = repo.tasks.value.find { it.id == task.id }
        assertEquals(TaskStatus.COMPLETED, completedTask?.status)
        assertEquals("Session finished successfully", completedTask?.userNotes)
    }
}
