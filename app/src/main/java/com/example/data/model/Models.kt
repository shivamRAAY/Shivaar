package com.example.data.model

enum class TaskCategory(val label: String) {
    C_PROGRAMMING("C Language"),
    PYTHON("AKTU Python"),
    DSA("Data Structures"),
    ACADEMICS("College Academics"),
    DEV("Software Dev"),
    CAREER("Placement & Career")
}

enum class Priority { HIGH, MEDIUM, LOW }
enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }
enum class TaskStatus { PENDING, IN_PROGRESS, PAUSED, COMPLETED, SKIPPED }

sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Syncing : SyncStatus
    data object Synced : SyncStatus
    data class Error(val message: String) : SyncStatus
}

data class TaskCompletionRecord(
    val id: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val category: TaskCategory = TaskCategory.C_PROGRAMMING,
    val completedAt: Long = 0L,
    val actualMinutes: Int = 0,
    val notes: String = ""
)

data class RoadmapMilestoneItem(
    val id: String = "",
    val userId: String = "",
    val monthId: String = "",
    val monthNumber: Int = 1,
    val year: Int = 1,
    val semester: Int = 1,
    val title: String = "",
    val focus: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val checkpointPassed: Boolean = false,
    val notes: String = ""
)

data class StudySessionItem(
    val id: String = "",
    val userId: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val startedAt: Long = 0L,
    val endedAt: Long? = null,
    val elapsedSeconds: Int = 0,
    val notes: String = "",
    val completed: Boolean = false
)

data class TaskItem(
    val id: String,
    val title: String,
    val category: TaskCategory,
    val whyItMatters: String,
    val instructions: String,
    val durationMinutes: Int,
    val priority: Priority,
    val difficulty: Difficulty,
    val resourceName: String,
    val resourceUrl: String = "",
    val definitionOfDone: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val scheduledDate: String = "Today",
    val completedAt: Long? = null,
    val userNotes: String = "",
    val actualMinutes: Int = 0,
    val blockerReason: String? = null
)

data class MilestoneItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val prerequisites: String,
    val outcomes: List<String>,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
    val category: String
)

data class RoadmapStage(
    val year: Int,
    val semester: Int,
    val phaseName: String,
    val focusTheme: String,
    val milestones: List<MilestoneItem>
)

data class SubjectItem(
    val id: String,
    val name: String,
    val code: String,
    val credits: Int,
    val topics: List<String>,
    val completedTopics: List<String> = emptyList(),
    val attendedClasses: Int = 18,
    val totalClasses: Int = 22,
    val nextExamDate: String = "Mid-Term: Nov 24",
    val notes: String = ""
)

enum class ProblemStatus(val label: String) {
    SOLVED("Solved"),
    ATTEMPTED("Attempted"),
    REVISE("Needs Revision"),
    NOT_STARTED("To Do")
}

data class DsaProblem(
    val id: String,
    val title: String,
    val platform: String,
    val topic: String,
    val language: String,
    val difficulty: String,
    val status: ProblemStatus = ProblemStatus.NOT_STARTED,
    val mistakeNote: String = "",
    val solutionSnippet: String = "",
    val url: String = ""
)

data class ProjectFeature(
    val title: String,
    val isDone: Boolean
)

data class ProjectItem(
    val id: String,
    val title: String,
    val tagline: String,
    val year: Int,
    val semester: Int,
    val techStack: List<String>,
    val features: List<ProjectFeature>,
    val whyItMattersForPlacement: String,
    val githubUrl: String = "",
    val liveDemoUrl: String = "",
    val isCompleted: Boolean = false
)

enum class ApplicationStatus(val label: String) {
    WISHLIST("Wishlist"),
    APPLIED("Applied"),
    OA_ROUND("Online Assessment"),
    INTERVIEW("Technical Interview"),
    OFFER("Offer Received"),
    REJECTED("Archived")
}

data class CareerApplication(
    val id: String,
    val company: String,
    val role: String,
    val status: ApplicationStatus = ApplicationStatus.WISHLIST,
    val deadline: String,
    val notes: String = "",
    val stipendOrPackage: String = ""
)

data class GroundingSource(
    val title: String,
    val url: String,
    val domain: String,
    val isOfficial: Boolean = false
)

data class GroundingCitation(
    val chunkIndex: Int,
    val title: String,
    val url: String,
    val segmentText: String = ""
)

data class GroundedAiResponse(
    val answerText: String,
    val sources: List<GroundingSource> = emptyList(),
    val citations: List<GroundingCitation> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val isGrounded: Boolean = false,
    val isServiceAvailable: Boolean = true,
    val errorMessage: String? = null
)

data class ResearchSource(
    val id: String,
    val title: String,
    val category: String,
    val url: String,
    val domain: String,
    val summary: String,
    val isBookmarked: Boolean = false,
    val isOfficial: Boolean = false,
    val query: String = ""
)

enum class MessageSender { USER, MENTOR }

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedPrompt: String? = null,
    val sources: List<GroundingSource> = emptyList(),
    val citations: List<GroundingCitation> = emptyList(),
    val isGrounded: Boolean = false,
    val isServiceUnavailable: Boolean = false,
    val errorMessage: String? = null
)

data class UserProfile(
    val name: String = "Shivam Yadav",
    val degree: String = "B.Tech CSE Core",
    val institute: String = "Lloyd Institute of Engineering & Technology",
    val affiliatingUniversity: String = "AKTU (Uttar Pradesh)",
    val currentSemester: Int = 1,
    val currentMonth: Int = 1,
    val dailyBudgetMinutes: Int = 150, // 2.5 hours
    val primaryTrack: String = "Full-Stack Development & Applied AI Engineering",
    val currentLanguagePriority: String = "Complete C Fundamentals & Master AKTU Python"
)

// --- 48-Month Roadmap Core Data Structures ---

enum class SkillTrack(val label: String, val colorHex: Long) {
    PROGRAMMING_DSA("Programming & DSA", 0xFF2563EB), // Blue
    DEV_PROJECTS("Development & Projects", 0xFF16A34A), // Green
    AI_ML("AI & Machine Learning", 0xFF9333EA), // Purple
    CORE_CS("Core Computer Science", 0xFFEA580C), // Orange
    PLACEMENT_PREP("Placement Preparation", 0xFFDC2626), // Red
    CAREER_INTERNSHIPS("Internships & Career", 0xFFD97706), // Gold
    ACADEMICS("College Academics", 0xFF0284C7) // Sky Blue
}

enum class GateStatus(val label: String) {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    NEEDS_REVISION("Needs Revision"),
    READY_FOR_ASSESSMENT("Ready for Assessment"),
    MASTERED("Mastered")
}

data class PrerequisiteGate(
    val id: String,
    val number: Int,
    val name: String,
    val targetMonth: Int,
    val requiredTopics: List<String>,
    val practicalEvidence: String,
    val assessments: String,
    val projectRequirements: String,
    val completionConditions: List<String>,
    val remediationSteps: String,
    val status: GateStatus = GateStatus.NOT_STARTED
)

data class BudgetEstimate(
    val freeResources: String,
    val lowCostEssentials: String,
    val optionalPaid: String,
    val totalInrEstimate: String
)

data class WeeklyTimeAllocation(
    val dsaHours: Int,
    val developmentHours: Int,
    val coreCsHours: Int,
    val academicsHours: Int,
    val totalWeeklyHours: Int
)

data class RoadmapMonth(
    val id: String,
    val monthNumber: Int,
    val year: Int,
    val semester: Int,
    val title: String,
    val mainFocus: String,
    val secondarySkill: String,
    val maintenanceSkill: String,
    val primaryTrack: SkillTrack,
    val topics: List<String>,
    val dsaTarget: String,
    val cumulativeDsaGoal: Int,
    val projectMilestone: String,
    val collegeAcademicFocus: String,
    val coreCsTarget: String,
    val weeklyAllocation: WeeklyTimeAllocation,
    val estimatedMonthlyHours: Int,
    val measurableOutput: String,
    val readinessCheckpoint: String,
    val budget: BudgetEstimate,
    val optionalActivities: List<String>,
    val prerequisites: List<String>,
    val revisionRequirements: String,
    val failureRecoveryInstructions: String,
    val isCompleted: Boolean = false,
    val isCurrentMonth: Boolean = false,
    val learningObjectives: List<String> = emptyList(),
    val practiceExercises: List<String> = emptyList(),
    val assessmentCriteria: String = "",
    val learningResources: List<String> = emptyList(),
    val definitionOfDone: String = "",
    val revisionSchedule: String = "",
    val phaseOrYearName: String = "",
    val semesterOrMilestoneName: String = "",
    val customNotes: String = "",
    val completedExercises: List<Int> = emptyList()
)
