package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
class Phase789EnginesRobolectricTest {

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
    fun testPhase7AddNewSubjectAndExamTaskGeneration() {
        val initialSubjectsCount = repository.subjects.value.size
        val initialTasksCount = repository.tasks.value.size

        // Add new subject
        repository.addNewSubject(
            name = "Operating Systems",
            code = "KCS-401",
            credits = 4,
            nextExamDate = "Dec 15, 2026"
        )

        val updatedSubjects = repository.subjects.value
        assertEquals(initialSubjectsCount + 1, updatedSubjects.size)

        val addedSubject = updatedSubjects.first { it.code == "KCS-401" }
        assertEquals("Operating Systems", addedSubject.name)
        assertEquals(4, addedSubject.credits)
        assertEquals("Dec 15, 2026", addedSubject.nextExamDate)
        assertTrue("Subject should have units initialized", addedSubject.topics.isNotEmpty())

        // Generate Exam Prep Task
        repository.createExamPrepTask(addedSubject)

        val updatedTasks = repository.tasks.value
        assertEquals(initialTasksCount + 1, updatedTasks.size)

        val examTask = updatedTasks.first()
        assertTrue("Task title must mention subject", examTask.title.contains("Operating Systems"))
        assertEquals(TaskCategory.ACADEMICS, examTask.category)
        assertEquals(45, examTask.durationMinutes)
        assertEquals(Priority.HIGH, examTask.priority)
    }

    @Test
    fun testPhase8AddNewDsaProblemAndDailyPracticeTaskScheduling() {
        val initialProblemsCount = repository.dsaProblems.value.size
        val initialTasksCount = repository.tasks.value.size

        // Add new DSA problem
        repository.addNewDsaProblem(
            title = "Trapping Rain Water",
            platform = "LeetCode",
            topic = "Two Pointers",
            language = "C++",
            difficulty = "Hard",
            url = "https://leetcode.com/problems/trapping-rain-water/"
        )

        val updatedProblems = repository.dsaProblems.value
        assertEquals(initialProblemsCount + 1, updatedProblems.size)

        val problem = updatedProblems.first { it.title == "Trapping Rain Water" }
        assertEquals("LeetCode", problem.platform)
        assertEquals("Two Pointers", problem.topic)
        assertEquals(ProblemStatus.NOT_STARTED, problem.status)

        // Update status and lesson notes
        repository.updateDsaProblemStatus(
            problemId = problem.id,
            status = ProblemStatus.SOLVED,
            notes = "Keep track of leftMax and rightMax bounds.",
            snippet = "// Two pointers approach O(N) time O(1) space"
        )

        val solvedProblem = repository.dsaProblems.value.first { it.id == problem.id }
        assertEquals(ProblemStatus.SOLVED, solvedProblem.status)
        assertEquals("Keep track of leftMax and rightMax bounds.", solvedProblem.mistakeNote)

        // Schedule Daily Practice Task
        repository.createDsaDailyPracticeTask(solvedProblem)

        val updatedTasks = repository.tasks.value
        assertEquals(initialTasksCount + 1, updatedTasks.size)

        val dsaTask = updatedTasks.first()
        assertTrue("Task title must mention problem title", dsaTask.title.contains("Trapping Rain Water"))
        assertEquals(TaskCategory.DSA, dsaTask.category)
        assertEquals(35, dsaTask.durationMinutes)
        assertEquals(Priority.HIGH, dsaTask.priority)
    }

    @Test
    fun testPhase9AddNewProjectAndSprintTaskGeneration() {
        val initialProjectsCount = repository.projects.value.size
        val initialTasksCount = repository.tasks.value.size

        // Add new project
        repository.addNewProject(
            title = "Distributed Cache Engine",
            tagline = "LRU & LFU with Raft clustering",
            techStack = listOf("Go", "gRPC", "Docker"),
            githubUrl = "https://github.com/shivam/dist-cache",
            whyItMatters = "Demonstrates distributed systems and concurrency."
        )

        val updatedProjects = repository.projects.value
        assertEquals(initialProjectsCount + 1, updatedProjects.size)

        val project = updatedProjects.first { it.title == "Distributed Cache Engine" }
        assertEquals("LRU & LFU with Raft clustering", project.tagline)
        assertEquals(3, project.techStack.size)
        assertEquals(4, project.features.size)

        // Generate Project Sprint Task
        repository.createProjectSprintTask(project)

        val updatedTasks = repository.tasks.value
        assertEquals(initialTasksCount + 1, updatedTasks.size)

        val sprintTask = updatedTasks.first()
        assertTrue("Sprint task must mention project title", sprintTask.title.contains("Distributed Cache Engine"))
        assertEquals(TaskCategory.DEV, sprintTask.category)
        assertEquals(45, sprintTask.durationMinutes)
    }

    @Test
    fun testPhase9AddNewCareerApplicationAndPrepTaskGeneration() {
        val initialAppsCount = repository.careerApplications.value.size
        val initialTasksCount = repository.tasks.value.size

        // Add new career application
        repository.addNewCareerApplication(
            company = "Google",
            role = "Software Engineering Intern 2027",
            status = ApplicationStatus.APPLIED,
            deadline = "Dec 01, 2026",
            stipendOrPackage = "₹1,25,000/mo"
        )

        val updatedApps = repository.careerApplications.value
        assertEquals(initialAppsCount + 1, updatedApps.size)

        val application = updatedApps.first { it.company == "Google" }
        assertEquals("Software Engineering Intern 2027", application.role)
        assertEquals(ApplicationStatus.APPLIED, application.status)

        // Update application status to OA_ROUND
        repository.updateApplicationStatus(application.id, ApplicationStatus.OA_ROUND)
        val oaApp = repository.careerApplications.value.first { it.id == application.id }
        assertEquals(ApplicationStatus.OA_ROUND, oaApp.status)

        // Generate Career Prep Task
        repository.createCareerPrepTask(oaApp)

        val updatedTasks = repository.tasks.value
        assertEquals(initialTasksCount + 1, updatedTasks.size)

        val prepTask = updatedTasks.first()
        assertTrue("Prep task must mention Google", prepTask.title.contains("Google"))
        assertEquals(TaskCategory.CAREER, prepTask.category)
        assertEquals(45, prepTask.durationMinutes)
    }
}
