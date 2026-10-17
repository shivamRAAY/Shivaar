package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test

class ShivaarRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private val aliceEmail = "alice@example.com"
    private val bobEmail = "bob@example.com"

    @Test
    fun testUnauthenticatedUserCannotAccessFirestore() = runBlocking {
        auth.signOut()
        val repo = ShivaarFirestoreRepository(firestore, auth)
        try {
            repo.saveUserProfile(UserProfile())
            fail("Expected IllegalStateException for unauthenticated user")
        } catch (e: IllegalStateException) {
            assertTrue(e.message!!.contains("User must be signed in"))
        }
    }

    @Test
    fun testAuthenticatedUserCanSaveAndRetrieveProfileAndTasks() = runBlocking {
        val aliceUid = signInTestUser(aliceEmail)
        val repo = ShivaarFirestoreRepository(firestore, auth)

        // 1. Profile
        val profile = UserProfile(
            name = "Alice Agarwal",
            degree = "B.Tech CSE",
            currentSemester = 2,
            dailyBudgetMinutes = 180
        )
        repo.saveUserProfile(profile)
        val loadedProfile = repo.getUserProfile()
        assertNotNull(loadedProfile)
        assertEquals("Alice Agarwal", loadedProfile?.name)
        assertEquals(2, loadedProfile?.currentSemester)

        // 2. Task
        val task = TaskItem(
            id = "alice_task_1",
            title = "C Pointers & Dynamic Allocation",
            category = TaskCategory.C_PROGRAMMING,
            whyItMatters = "Core OS and memory management",
            instructions = "Allocate dynamic memory using malloc and free",
            durationMinutes = 60,
            priority = Priority.HIGH,
            difficulty = Difficulty.INTERMEDIATE,
            resourceName = "K&R C Book",
            definitionOfDone = "Valgrind reports zero leaks",
            status = TaskStatus.IN_PROGRESS
        )
        repo.saveTask(task)

        // Observe tasks
        val tasks = withTimeout(10000L) {
            repo.observeTasks().first { list -> list.any { it.id == "alice_task_1" } }
        }
        assertTrue(tasks.any { it.id == "alice_task_1" && it.title == "C Pointers & Dynamic Allocation" })
    }

    @Test
    fun testUserCannotReadAnotherUsersRecords() = runBlocking {
        // Alice creates a task
        val aliceUid = signInTestUser(aliceEmail)
        val aliceRepo = ShivaarFirestoreRepository(firestore, auth)
        val aliceTask = TaskItem(
            id = "alice_secret_task",
            title = "Alice's Secret Project",
            category = TaskCategory.DEV,
            whyItMatters = "Private",
            instructions = "Private",
            durationMinutes = 30,
            priority = Priority.LOW,
            difficulty = Difficulty.BEGINNER,
            resourceName = "Docs",
            definitionOfDone = "Done",
            status = TaskStatus.PENDING
        )
        aliceRepo.saveTask(aliceTask)

        // Bob signs in
        val bobUid = signInTestUser(bobEmail)
        assertNotEquals(aliceUid, bobUid)

        // Bob tries directly to read Alice's task from Firestore
        try {
            firestore.collection("users").document(aliceUid).collection("tasks").document("alice_secret_task").get().await()
            fail("Bob should have been denied permission to read Alice's task")
        } catch (e: Exception) {
            // Expected PERMISSION_DENIED
            val isPermissionDenied = e is FirebaseFirestoreException &&
                e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
            assertTrue("Expected PERMISSION_DENIED, got $e", isPermissionDenied || e.message?.contains("PERMISSION_DENIED") == true)
        }
    }

    @Test
    fun testCompletionRecordAndStudySessionPersistence() = runBlocking {
        signInTestUser(aliceEmail)
        val repo = ShivaarFirestoreRepository(firestore, auth)

        val record = TaskCompletionRecord(
            id = "comp_rec_1",
            taskId = "task_1",
            taskTitle = "Binary Trees Traversal",
            category = TaskCategory.DSA,
            completedAt = System.currentTimeMillis(),
            actualMinutes = 45,
            notes = "Mastered In-order and Pre-order iterative loops"
        )
        repo.addCompletionRecord(record)

        val history = withTimeout(10000L) {
            repo.observeCompletionHistory().first { it.isNotEmpty() }
        }
        assertEquals(1, history.size)
        assertEquals("Binary Trees Traversal", history.first().taskTitle)

        // Study session
        val session = StudySessionItem(
            id = "sess_1",
            taskId = "task_1",
            taskTitle = "Binary Trees Traversal",
            startedAt = System.currentTimeMillis() - 2700000,
            endedAt = System.currentTimeMillis(),
            elapsedSeconds = 2700,
            notes = "Deep focus without distractions",
            completed = true
        )
        repo.saveStudySession(session)
    }
}
