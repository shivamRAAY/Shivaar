package com.example.data.model

/**
 * SHIVAAR 2.0 Domain Models
 * Covers:
 * - Progressive 6-Step Onboarding (Education, Skills, Goals, Interests, Constraints, AI Analysis)
 * - Path Choice & Comparison
 * - Public Roadmap Library Definitions
 * - Roadmap Adaptation & Import
 */

enum class EducationStage(val label: String) {
    SCHOOL_STUDENT("School Student"),
    DIPLOMA_STUDENT("Diploma Student"),
    UNDERGRADUATE("Undergraduate (B.Tech / B.E. / BCA / B.Sc)"),
    POSTGRADUATE("Postgraduate (M.Tech / MCA / M.Sc)"),
    WORKING_PROFESSIONAL("Working Professional"),
    CAREER_SWITCHER("Career Switcher"),
    SELF_LEARNER("Independent Self-Learner")
}

data class OnboardingEducation(
    val qualification: String = "Undergraduate",
    val stage: EducationStage = EducationStage.UNDERGRADUATE,
    val fieldOfStudy: String = "Computer Science & Engineering",
    val currentYear: Int? = 1,
    val currentSemester: Int? = 1,
    val institution: String = "Lloyd Institute of Engineering & Technology",
    val curriculum: String = "AKTU (Uttar Pradesh)",
    val expectedGraduationDate: String = "2028",
    val studiedSubjects: List<String> = listOf("Programming in C", "Engineering Mathematics", "Digital Electronics")
)

enum class SkillConfidence(val label: String) {
    BEGINNER("Beginner (Exploring syntax & basics)"),
    FAMILIAR("Familiar (Can solve guided problems)"),
    INTERMEDIATE("Intermediate (Builds small projects independently)"),
    CONFIDENT("Confident (Comfortable with debugging & architecture)"),
    ADVANCED("Advanced (Production systems, concurrency, optimization)")
}

data class DiagnosticQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val topic: String
)

data class DiagnosticResult(
    val totalScore: Int = 0,
    val maxScore: Int = 5,
    val topicScores: Map<String, Int> = emptyMap(),
    val verifiedLevel: String = "Diagnostic Completed",
    val summaryFeedback: String = ""
)

data class OnboardingSkills(
    val languagesKnown: List<String> = listOf("C", "Python"),
    val topicsCompleted: List<String> = listOf("Variables & Loops", "Functions & Arrays", "Pointers Basics"),
    val projectsBuilt: List<String> = listOf("CLI Student Record System"),
    val dsaExperience: String = "Arrays, Strings, and Basic Recursion",
    val devExperience: String = "Basic Web Frontend & Scripting",
    val aiExperience: String = "API Usage & Prompt Engineering",
    val portfolioOrGithubUrl: String = "",
    val confidenceLevel: SkillConfidence = SkillConfidence.FAMILIAR,
    val diagnosticResult: DiagnosticResult? = null
)

enum class CareerGoal(val label: String, val category: String) {
    ACADEMIC_EXCELLENCE("Academic Excellence & High GPA", "Academics"),
    SWE_PLACEMENT("Software Engineering Placement", "Industry"),
    INTERNSHIP("High-Impact Tech Internship", "Industry"),
    AI_ML_ENGINEERING("AI / Machine Learning Engineering", "Artificial Intelligence"),
    AGENTIC_AI("Agentic AI & Autonomous Systems", "Artificial Intelligence"),
    FULL_STACK_DEV("Full-Stack Software Development", "Software Engineering"),
    CYBERSECURITY("Cybersecurity & Network Defense", "Infrastructure"),
    CLOUD_DEVOPS("Cloud Architecture & DevOps", "Infrastructure"),
    FREELANCING("Technical Freelancing & Consulting", "Career"),
    STARTUP_BUILDING("Building a Tech Startup / MVP", "Entrepreneurship"),
    RESEARCH_HIGHER_ED("Research & Higher Studies (GATE/MS)", "Academia"),
    CAREER_CHANGE("Career Transition into Tech", "Transition"),
    OPEN_EXPLORATION("Open Exploration without a Fixed Goal", "Exploration")
}

data class OnboardingGoals(
    val selectedGoals: List<CareerGoal> = listOf(CareerGoal.SWE_PLACEMENT, CareerGoal.FULL_STACK_DEV),
    val customGoal: String = "",
    val targetTimelineMonths: Int = 24,
    val preferredRoles: List<String> = listOf("Junior Backend Engineer", "Full-Stack Developer"),
    val primaryPriority: String = "Build strong CS fundamentals and production-ready portfolio"
)

enum class InterestArea(val label: String, val description: String) {
    BUILDING_APPS("Building Applications", "Web apps, mobile apps, cross-platform tools"),
    SOLVING_ALGORITHMS("Algorithmic Problem Solving", "DSA, contest programming, optimizations"),
    WORKING_WITH_DATA("Working with Data", "Pipelines, visualization, relational & vector databases"),
    BUILDING_AI_SYSTEMS("Building AI Systems", "LLM integrations, agentic loops, fine-tuning"),
    DESIGNING_INTERFACES("Designing User Interfaces", "UX research, design systems, clean animations"),
    OPERATING_SYSTEMS("Systems & Operating Systems", "Linux, C memory management, network sockets"),
    SECURITY_NETWORKING("Security & Protocols", "Web security, cryptography, penetration testing"),
    RESEARCH_EXPERIMENTATION("Research & Theory", "Academic papers, proof of concepts, algorithm analysis"),
    ENTREPRENEURSHIP("Product Building & Startups", "MVPs, customer problems, full lifecycle shipping"),
    UNSURE_EXPLORING("Unsure / Exploring Broadly", "Sampling multiple domains before specializing")
}

data class OnboardingInterests(
    val interests: List<InterestArea> = listOf(
        InterestArea.BUILDING_APPS,
        InterestArea.SOLVING_ALGORITHMS,
        InterestArea.BUILDING_AI_SYSTEMS
    )
)

data class OnboardingConstraints(
    val availableHoursPerWeek: Int = 18,
    val preferredSchedule: String = "Evenings (2-3 hrs) + Weekends (4-5 hrs)",
    val budgetRangeInr: String = "Free resources only (₹0)",
    val preferredLanguage: String = "English / Bilingual (Hinglish for complex concepts)",
    val deviceConstraints: String = "Standard Laptop (8GB RAM, Core i5, Linux/Windows)",
    val upcomingExamsOrDeadlines: String = "AKTU Semester Examinations in 4 months",
    val learningStylePreferences: List<String> = listOf("Project-based learning", "Interactive coding exercises"),
    val existingCommitments: String = "College lectures (9 AM - 4 PM Monday to Friday)"
)

data class PathComparisonOption(
    val id: String,
    val name: String,
    val subtitle: String,
    val whyItSuitsYou: String,
    val requiredSkills: List<String>,
    val estimatedTotalHours: Int,
    val suggestedProjects: List<String>,
    val prerequisites: List<String>,
    val advantages: List<String>,
    val challenges: List<String>,
    val careerOpportunities: List<String>,
    val supportingSources: List<GroundingSource> = emptyList()
)

data class AiProfileAnalysis(
    val currentStartingLevel: String = "Early Intermediate Computer Science Student",
    val existingStrengths: List<String> = listOf(
        "Strong commitment to structured C and Python fundamentals",
        "Consistent daily study habits with active streak tracking",
        "Clear long-term vision toward software engineering placements"
    ),
    val knowledgeGaps: List<String> = listOf(
        "Production backend system architecture (REST/gRPC, SQL optimizations)",
        "Advanced data structures (Trees, Graphs, Disjoint Sets)",
        "End-to-end cloud deployment and CI/CD automation"
    ),
    val relevantPrerequisites: List<String> = listOf(
        "Solid mastery of C pointers and memory model before advanced DSA",
        "Relational database schemas before full-stack projects",
        "Git version control and testing discipline"
    ),
    val goalFeasibilityAnalysis: String = "Highly feasible with 18 hours/week dedication over a 24-month horizon. Feasibility increases significantly by maintaining consistent 75%+ college attendance while shipping one production portfolio project per semester.",
    val keyUncertainties: List<String> = listOf(
        "Balancing college exam preparation weeks with continuous coding momentum",
        "Shifting industry demand toward Agentic AI workflows alongside standard SWE"
    ),
    val recommendedPaths: List<PathComparisonOption> = emptyList(),
    val expectedWeeklyEffortHours: Int = 18,
    val recommendedProjects: List<String> = listOf(
        "High-Performance In-Memory Key-Value Store in C/C++",
        "Distributed Task & Learning Engine with Kotlin & Webhooks",
        "Agentic AI Research Copilot with Grounded Search"
    ),
    val estimatedCostBreakdown: String = "₹0 (100% Free Open Source Tooling, Official Documentation, Linux OS, GitHub Free Tier)",
    val tradeoffsAndDisadvantages: List<String> = listOf(
        "Requires disciplined trade-off against passive entertainment",
        "High cognitive load during university examination windows"
    ),
    val recommendedNextImmediateStep: String = "Complete the C Memory Management and Pointer milestone, then construct the In-Memory Store project.",
    val verifiedInfoSummary: String = "Verified academic curriculum: AKTU B.Tech CSE; verified local task completion records.",
    val userProvidedInfoSummary: String = "Self-reported: 18 hrs/week availability, ₹0 budget preference, intermediate C/Python interest.",
    val aiAssumptionsSummary: String = "Assumes regular access to broadband internet and standard x86-64 development workstation."
)

data class OnboardingData(
    val step1Education: OnboardingEducation = OnboardingEducation(),
    val step2Skills: OnboardingSkills = OnboardingSkills(),
    val step3Goals: OnboardingGoals = OnboardingGoals(),
    val step4Interests: OnboardingInterests = OnboardingInterests(),
    val step5Constraints: OnboardingConstraints = OnboardingConstraints(),
    val aiAnalysis: AiProfileAnalysis? = null,
    val chosenPathId: String? = null,
    val currentStep: Int = 1,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

// --- Public Roadmap Library Models ---

enum class RoadmapResourceVerification(val label: String) {
    OFFICIAL_DOCUMENTATION("Official Documentation"),
    INDEPENDENTLY_REVIEWED("Independently Reviewed"),
    AI_SUGGESTED("AI-Curated Recommendation")
}

data class PublicRoadmapMilestone(
    val id: String,
    val title: String,
    val level: Difficulty,
    val description: String,
    val estimatedHours: Int,
    val topics: List<String>,
    val prerequisites: List<String>,
    val practiceExercises: List<String>,
    val projectOutputs: List<String>,
    val assessmentCriteria: String,
    val recommendedResources: List<String>,
    val definitionOfDone: String
)

data class PublicRoadmap(
    val id: String,
    val title: String,
    val tagline: String,
    val category: String,
    val skillLevel: Difficulty,
    val audience: String,
    val prerequisitesSummary: String,
    val estimatedTotalHours: Int,
    val milestones: List<PublicRoadmapMilestone>,
    val verificationLevel: RoadmapResourceVerification,
    val officialDocLinks: List<String>,
    val optionalNextRoadmaps: List<String>,
    val isSaved: Boolean = false,
    val isFollowed: Boolean = false
)
