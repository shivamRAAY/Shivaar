package com.example.data

import com.example.data.model.*

/**
 * SHIVAAR 2.0 — Phase 4 Personalized Roadmap Generator
 *
 * Implements an adaptive, deterministic roadmap generation engine that creates
 * structured roadmaps customized to the user's:
 * - Education Stage (Full 48 months for B.Tech CSE; tailored 6-18 months for switchers/professionals)
 * - Chosen Career Path (Full-Stack & Cloud, Systems & DSA, Agentic AI, or Custom)
 * - Available Weekly Hours (scaled realistically)
 * - Budget Constraints (Free/Open-Source vs Paid tools)
 * - Existing Skills & Diagnostic Baseline
 *
 * Hierarchical levels:
 * 1. Long-Term Goal
 * 2. Year or Phase
 * 3. Semester or Major Milestone
 * 4. Month
 * 5. Week (Weekly allocations & focus areas)
 * 6. Daily Task (Bridged to Today's dashboard)
 */
object PersonalizedRoadmapEngine {

    fun generatePersonalizedRoadmap(data: OnboardingData): List<RoadmapMonth> {
        val stage = data.step1Education.stage
        val chosenPath = data.chosenPathId ?: "path_fullstack_cloud"
        val weeklyHours = data.step5Constraints.availableHoursPerWeek.coerceIn(8, 40)
        val budgetRange = data.step5Constraints.budgetRangeInr

        return when {
            stage == EducationStage.UNDERGRADUATE || stage == EducationStage.DIPLOMA_STUDENT -> {
                // Full 48-Month / 8-Semester University Adapted Roadmap
                generateBTech48MonthRoadmap(data, chosenPath, weeklyHours, budgetRange)
            }
            stage == EducationStage.CAREER_SWITCHER || stage == EducationStage.WORKING_PROFESSIONAL -> {
                // Accelerated 12-Month Professional Transition Roadmap
                generateAcceleratedProfessionalRoadmap(data, chosenPath, weeklyHours, budgetRange, totalMonths = 12)
            }
            else -> {
                // 18-Month Comprehensive Independent Self-Learner Roadmap
                generateAcceleratedProfessionalRoadmap(data, chosenPath, weeklyHours, budgetRange, totalMonths = 18)
            }
        }
    }

    /**
     * B.Tech 48-Month Engineering Roadmap covering all 8 semesters, adapted to chosen path.
     */
    private fun generateBTech48MonthRoadmap(
        data: OnboardingData,
        chosenPath: String,
        weeklyHours: Int,
        budgetRange: String
    ): List<RoadmapMonth> {
        // Base curriculum from verified 4-year AKTU CSE foundation
        val baseYear1 = RoadmapYear1Data.getMonths()
        val baseYear2 = RoadmapYear2Data.getMonths()
        val baseYear3 = RoadmapYear3Data.getMonths()
        val baseYear4 = RoadmapYear4Data.getMonths()
        val allBase = baseYear1 + baseYear2 + baseYear3 + baseYear4

        val monthlyHours = weeklyHours * 4
        val dsaRatio = when (chosenPath) {
            "path_systems_dsa" -> 0.40f
            "path_agentic_ai" -> 0.25f
            else -> 0.30f
        }
        val devRatio = when (chosenPath) {
            "path_agentic_ai" -> 0.40f
            "path_fullstack_cloud" -> 0.45f
            else -> 0.30f
        }
        val coreCsRatio = 0.20f
        val academicsRatio = 1f - (dsaRatio + devRatio + coreCsRatio)

        val dsaWeekly = (weeklyHours * dsaRatio).toInt().coerceAtLeast(2)
        val devWeekly = (weeklyHours * devRatio).toInt().coerceAtLeast(3)
        val coreCsWeekly = (weeklyHours * coreCsRatio).toInt().coerceAtLeast(2)
        val acadWeekly = (weeklyHours - dsaWeekly - devWeekly - coreCsWeekly).coerceAtLeast(2)

        val scaledAllocation = WeeklyTimeAllocation(
            dsaHours = dsaWeekly,
            developmentHours = devWeekly,
            coreCsHours = coreCsWeekly,
            academicsHours = acadWeekly,
            totalWeeklyHours = weeklyHours
        )

        return allBase.map { month ->
            val phaseName = when (month.year) {
                1 -> "Year 1: Foundations & Core Programming"
                2 -> "Year 2: Core Computer Science & Architecture"
                3 -> "Year 3: Advanced Systems, Cloud & Placement Gate"
                else -> "Year 4: Capstone Engineering, Production & Careers"
            }
            val semName = "Semester ${month.semester} • Major Milestone ${month.semester}"

            // Inject Phase 4 enrichment criteria
            val objectives = listOf(
                "Master ${month.mainFocus} with theoretical and hands-on depth",
                "Complete ${month.dsaTarget} with independent test coverage",
                "Implement and ship milestone project: ${month.projectMilestone}"
            )

            val exercises = listOf(
                "Complete 15-20 practice problems targeting ${month.dsaTarget}",
                "Write modular test harness for ${month.mainFocus}",
                "Document technical design trade-offs in README"
            )

            val resources = listOf(
                "Official Documentation (en.cppreference.com / python.org / MDN)",
                "AKTU CSE Syllabus & Curated NPTEL / MIT OCW lecture notes",
                "Standard Open Source Repositories on GitHub"
            )

            val dod = "All unit tests pass, project demo runs cleanly from terminal/browser, and milestone code is committed to GitHub."
            val revSchedule = "Weekly Sunday recall: 45-min active flashcard review + 1 timed mock problem from previous month."

            month.copy(
                estimatedMonthlyHours = monthlyHours,
                weeklyAllocation = scaledAllocation,
                phaseOrYearName = phaseName,
                semesterOrMilestoneName = semName,
                learningObjectives = objectives,
                practiceExercises = exercises,
                assessmentCriteria = month.readinessCheckpoint,
                learningResources = resources,
                definitionOfDone = dod,
                revisionSchedule = revSchedule,
                budget = BudgetEstimate(
                    zeroCostStack = "Linux (Ubuntu/Fedora), VS Code / NeoVim, GCC/G++, Python 3, Git, PostgreSQL Free, Docker CE",
                    lowCostEssentials = if (budgetRange.contains("Free", ignoreCase = true)) "None required (₹0)" else "₹500-1,000/yr for domain/VPS testing",
                    optionalPaid = "None required for core academic & SWE mastery",
                    totalInrEstimate = if (budgetRange.contains("Free", ignoreCase = true)) "₹0" else "₹0 - ₹1,500"
                )
            )
        }
    }

    /**
     * Accelerated 12 to 18-month structured roadmap for Career Switchers and Working Professionals.
     * Focuses on maximum engineering depth and career conversion without university exam overhead.
     */
    private fun generateAcceleratedProfessionalRoadmap(
        data: OnboardingData,
        chosenPath: String,
        weeklyHours: Int,
        budgetRange: String,
        totalMonths: Int
    ): List<RoadmapMonth> {
        val monthlyHours = weeklyHours * 4
        val dsaWeekly = (weeklyHours * 0.35f).toInt().coerceAtLeast(3)
        val devWeekly = (weeklyHours * 0.45f).toInt().coerceAtLeast(4)
        val coreCsWeekly = (weeklyHours - dsaWeekly - devWeekly).coerceAtLeast(2)

        val allocation = WeeklyTimeAllocation(
            dsaHours = dsaWeekly,
            developmentHours = devWeekly,
            coreCsHours = coreCsWeekly,
            academicsHours = 0, // No college exams for professionals
            totalWeeklyHours = weeklyHours
        )

        val curriculumTemplates = when (chosenPath) {
            "path_systems_dsa" -> getSystemsCurriculum(totalMonths)
            "path_agentic_ai" -> getAgenticAiCurriculum(totalMonths)
            else -> getFullStackCurriculum(totalMonths)
        }

        return curriculumTemplates.mapIndexed { index, template ->
            val monthNum = index + 1
            val phaseNum = ((monthNum - 1) / 3) + 1
            val phaseName = "Phase $phaseNum: ${template.phaseTitle}"
            val milestoneName = "Milestone $monthNum: ${template.title}"

            RoadmapMonth(
                id = "acc_month_$monthNum",
                monthNumber = monthNum,
                year = ((monthNum - 1) / 12) + 1,
                semester = ((monthNum - 1) / 6) + 1,
                title = template.title,
                mainFocus = template.mainFocus,
                secondarySkill = template.secondarySkill,
                maintenanceSkill = "Git, Clean Architecture & Unit Testing",
                primaryTrack = template.track,
                topics = template.topics,
                dsaTarget = template.dsaTarget,
                cumulativeDsaGoal = (monthNum * 25).coerceAtMost(350),
                projectMilestone = template.project,
                collegeAcademicFocus = "Industry Best Practices & RFC Standards",
                coreCsTarget = template.coreCs,
                weeklyAllocation = allocation,
                estimatedMonthlyHours = monthlyHours,
                measurableOutput = template.measurableOutput,
                readinessCheckpoint = template.checkpoint,
                budget = BudgetEstimate(
                    zeroCostStack = "Open-source Linux, VS Code, Git, Docker, Postgres, GitHub Actions",
                    lowCostEssentials = if (budgetRange.contains("Free", ignoreCase = true)) "₹0" else "₹800/yr for custom domain & cloud testing",
                    optionalPaid = "None mandatory",
                    totalInrEstimate = if (budgetRange.contains("Free", ignoreCase = true)) "₹0" else "₹0 - ₹1,200"
                ),
                optionalActivities = listOf("Attend local tech meetups", "Contribute minor PR to open source documentation"),
                prerequisites = template.prerequisites,
                revisionRequirements = "Spaced repetition of core algorithms and architectural design patterns every 14 days",
                failureRecoveryInstructions = "If falling behind by more than 1 week, reduce secondary exercises and complete core project PR first",
                isCompleted = index == 0 && (data.step2Skills.diagnosticResult?.totalScore ?: 0) >= 4,
                isCurrentMonth = index == 0,
                learningObjectives = template.learningObjectives,
                practiceExercises = template.practiceExercises,
                assessmentCriteria = template.checkpoint,
                learningResources = template.resources,
                definitionOfDone = "Production-grade code reviewed, passing CI tests, and deployed live with working demo URL.",
                revisionSchedule = "Bi-weekly Sunday code review & algorithmic speed audit.",
                phaseOrYearName = phaseName,
                semesterOrMilestoneName = milestoneName
            )
        }
    }

    private data class MonthTemplate(
        val phaseTitle: String,
        val title: String,
        val mainFocus: String,
        val secondarySkill: String,
        val track: SkillTrack,
        val topics: List<String>,
        val dsaTarget: String,
        val project: String,
        val coreCs: String,
        val measurableOutput: String,
        val checkpoint: String,
        val prerequisites: List<String>,
        val learningObjectives: List<String>,
        val practiceExercises: List<String>,
        val resources: List<String>
    )

    private fun getFullStackCurriculum(totalMonths: Int): List<MonthTemplate> {
        val list = mutableListOf(
            MonthTemplate(
                phaseTitle = "Foundation & Modern Language Mastery",
                title = "TypeScript & Asynchronous Programming",
                mainFocus = "TypeScript strict typing, closures, Promises, Event Loop",
                secondarySkill = "Modern Git workflow, linting, Prettier",
                track = SkillTrack.SOFTWARE_DEVELOPMENT,
                topics = listOf("Type inference vs annotations", "Generics & Utility types", "Async/Await internals", "Node.js runtime basics"),
                dsaTarget = "Arrays, Strings, Hash Maps (Two-pointer, Sliding window)",
                project = "CLI Task Management Tool with JSON persistence and unit tests",
                coreCs = "Process memory model & Single-threaded concurrency",
                measurableOutput = "Tested npm CLI package with 90%+ branch coverage",
                checkpoint = "Can explain Promise microtasks vs macrotasks and solve 15 LeetCode array/hashmap problems.",
                prerequisites = listOf("Basic programming syntax"),
                learningObjectives = listOf("Master TypeScript type systems", "Write robust asynchronous code", "Build real CLI applications"),
                practiceExercises = listOf("Build custom EventEmitter", "Implement Promise.all polyfill", "Solve 15 Two-Pointer/HashMap problems"),
                resources = listOf("typescriptlang.org handbook", "developer.mozilla.org (MDN)", "Node.js official documentation")
            ),
            MonthTemplate(
                phaseTitle = "Foundation & Modern Language Mastery",
                title = "Backend Services & REST/gRPC APIs",
                mainFocus = "Express/FastAPI, HTTP RFC specs, middleware, input validation",
                secondarySkill = "Automated API testing with Jest/Supertest",
                track = SkillTrack.SOFTWARE_DEVELOPMENT,
                topics = listOf("RESTful architecture", "Request/Response lifecycle", "Zod/Pydantic validation", "JWT Authentication & CORS"),
                dsaTarget = "Linked Lists & Stacks (Monotonic stack, cycle detection)",
                project = "Secure Multi-tenant Authentication & Notes API",
                coreCs = "TCP/IP 3-way handshake, HTTP/1.1 vs HTTP/2 vs HTTP/3",
                measurableOutput = "Documented Swagger/OpenAPI spec + passing Postman test suite",
                checkpoint = "Can construct secure JWT auth with refresh tokens and solve 15 Stack/Linked List problems.",
                prerequisites = listOf("TypeScript Async programming"),
                learningObjectives = listOf("Design clean RESTful architectures", "Implement secure JWT/OAuth flows", "Write automated HTTP tests"),
                practiceExercises = listOf("Build custom rate-limiting middleware", "Implement sliding window token bucket", "Solve 15 Stack problems"),
                resources = listOf("expressjs.com", "jwt.io", "Postman Learning Center")
            ),
            MonthTemplate(
                phaseTitle = "Data Systems & Relational Modeling",
                title = "PostgreSQL & Database Internals",
                mainFocus = "Relational modeling, ACID transactions, B-Tree indexes, EXPLAIN ANALYZE",
                secondarySkill = "Database migrations (Prisma / Drizzle ORM)",
                track = SkillTrack.CORE_CS,
                topics = listOf("Normalization (1NF-BCNF)", "Connection pooling", "Index scans vs Seq scans", "Isolation levels & race conditions"),
                dsaTarget = "Queues & Recursion (Backtracking basics)",
                project = "High-Concurrency E-Commerce Inventory & Order Service",
                coreCs = "Write-Ahead Logging (WAL) and B-Tree disk layout",
                measurableOutput = "Live database schema benchmarked to 1,000 req/sec without deadlocks",
                checkpoint = "Can explain MVCC and write optimized SQL queries with multi-column composite indexes.",
                prerequisites = listOf("REST API development"),
                learningObjectives = listOf("Master relational database design", "Understand database internals & indexing", "Prevent concurrent race conditions"),
                practiceExercises = listOf("Write recursive CTE queries", "Benchmark index performance with pgbench", "Solve 15 Recursion problems"),
                resources = listOf("postgresql.org/docs", "use-the-index-luke.com", "Prisma Documentation")
            ),
            MonthTemplate(
                phaseTitle = "Modern Frontend & State Architecture",
                title = "React & Clean Component Architecture",
                mainFocus = "React 19, custom hooks, state machines, reconciliation engine",
                secondarySkill = "Tailwind CSS & Material UI component libraries",
                track = SkillTrack.SOFTWARE_DEVELOPMENT,
                topics = listOf("Virtual DOM & Fiber reconciler", "Server vs Client components", "Optimistic UI updates", "Accessibility (a11y)"),
                dsaTarget = "Binary Trees & BSTs (In-order, BFS, DFS)",
                project = "Real-Time Collaborative Dashboard with Optimistic Updates",
                coreCs = "Browser rendering pipeline (DOM, CSSOM, Layout, Paint)",
                measurableOutput = "Lighthouse 95+ score interactive web application",
                checkpoint = "Can prevent unnecessary React re-renders using proper memoization and solve 20 Binary Tree problems.",
                prerequisites = listOf("TypeScript", "REST API APIs"),
                learningObjectives = listOf("Build accessible responsive UIs", "Manage complex frontend state", "Optimize web vitals"),
                practiceExercises = listOf("Build virtualized list component", "Implement custom useDebounce & useThrottle", "Solve 20 Tree problems"),
                resources = listOf("react.dev", "web.dev", "TailwindCSS Documentation")
            ),
            MonthTemplate(
                phaseTitle = "Distributed Systems & Cloud",
                title = "Docker, Caching & Cloud Infrastructure",
                mainFocus = "Containerization, Multi-stage Docker builds, Redis caching, CI/CD",
                secondarySkill = "GitHub Actions workflows & automated deployment",
                track = SkillTrack.SOFTWARE_DEVELOPMENT,
                topics = listOf("Docker layers & caching", "Redis Cache-aside & Pub/Sub", "Linux cgroups & namespaces", "Reverse proxy with Nginx"),
                dsaTarget = "Heaps & Priority Queues (Top-K elements)",
                project = "Distributed Task Queue with Redis & Worker Fleet",
                coreCs = "Operating system virtualization & networking bridge",
                measurableOutput = "Fully automated CI/CD pipeline deploying containers on cloud VPS",
                checkpoint = "Can write multi-stage Dockerfiles < 100MB and solve 15 Heap problems.",
                prerequisites = listOf("PostgreSQL", "Backend APIs"),
                learningObjectives = listOf("Master containerization", "Implement Redis caching strategies", "Automate deployment pipelines"),
                practiceExercises = listOf("Dockerize full-stack app with docker-compose", "Implement Redis distributed lock", "Solve 15 Heap problems"),
                resources = listOf("docker.com/docs", "redis.io/docs", "GitHub Actions Guide")
            ),
            MonthTemplate(
                phaseTitle = "Capstone & Career Placement",
                title = "Production Capstone & Interview Readiness",
                mainFocus = "Full-scale production deployment, monitoring, system design interview prep",
                secondarySkill = "System design diagrams & behavioral interview prep",
                track = SkillTrack.CAREER_PREPARATION,
                topics = listOf("Horizontal scaling & load balancers", "Rate limiting & CAP Theorem", "Log aggregation & metrics", "Behavioral STAR method"),
                dsaTarget = "Graphs (Dijkstra, Topological Sort) & Dynamic Programming basics",
                project = "End-to-End SaaS Platform with Stripe Billing & Live Analytics",
                coreCs = "Distributed consensus & High availability patterns",
                measurableOutput = "Live production app with users + polished portfolio website and resume",
                checkpoint = "Can whiteboard system design for URL shortener/Twitter and pass mock technical interview.",
                prerequisites = listOf("All previous 5 months"),
                learningObjectives = listOf("Architect scalable distributed systems", "Aces technical & behavioral interviews", "Launch production portfolio"),
                practiceExercises = listOf("Design TinyURL on whiteboard", "Solve 20 Graph/DP problems", "Conduct 3 peer mock interviews"),
                resources = listOf("System Design Primer (GitHub)", "NeetCode 150", "Tech Interview Handbook")
            )
        )

        // Expand to requested total months if 12 or 18
        while (list.size < totalMonths) {
            val m = list.size + 1
            list.add(
                MonthTemplate(
                    phaseTitle = "Advanced Specialization & Scale",
                    title = "Advanced Scale & Microservices (Month $m)",
                    mainFocus = "gRPC, Apache Kafka / RabbitMQ event streaming, Kubernetes basics",
                    secondarySkill = "Security auditing (OWASP Top 10)",
                    track = SkillTrack.SOFTWARE_DEVELOPMENT,
                    topics = listOf("Event-driven architecture", "Saga pattern", "Idempotency", "Database sharding"),
                    dsaTarget = "Advanced DP & Trie structures",
                    project = "Event-Driven Microservices Architecture with Saga Pattern",
                    coreCs = "Raft Consensus and Byzantine fault tolerance",
                    measurableOutput = "Multi-service event-driven project with full integration tests",
                    checkpoint = "Can design resilient microservices and solve advanced DP questions.",
                    prerequisites = listOf("Month ${m - 1}"),
                    learningObjectives = listOf("Build decoupled event architectures", "Implement distributed transactions", "Master advanced DSA"),
                    practiceExercises = listOf("Build Kafka consumer group", "Solve 15 DP problems", "Profile memory leaks"),
                    resources = listOf("kafka.apache.org", "microservices.io")
                )
            )
        }

        return list.take(totalMonths)
    }

    private fun getSystemsCurriculum(totalMonths: Int): List<MonthTemplate> {
        val list = mutableListOf(
            MonthTemplate(
                phaseTitle = "Low-Level Computing Foundations",
                title = "C Memory Model & Pointer Mechanics",
                mainFocus = "Pointers, dynamic memory (malloc/free), stack vs heap, structs & padding",
                secondarySkill = "GCC/Clang, Makefiles, GDB debugging",
                track = SkillTrack.PROGRAMMING,
                topics = listOf("Pointer arithmetic", "Function pointers & vtables", "Memory alignment", "Valgrind memory leak detection"),
                dsaTarget = "Dynamic Arrays & Linked Lists in pure C",
                project = "Custom Dynamic Memory Allocator (my_malloc and my_free)",
                coreCs = "Virtual memory & CPU cache lines (L1/L2/L3)",
                measurableOutput = "Passing unit test suite for custom malloc with zero memory leaks",
                checkpoint = "Can explain segfaults from core dumps in GDB and implement singly/doubly linked lists in C.",
                prerequisites = listOf("Basic C syntax"),
                learningObjectives = listOf("Master pointer arithmetic", "Understand virtual memory mapping", "Debug complex memory issues"),
                practiceExercises = listOf("Implement custom malloc with free list", "Write generic dynamic vector in C", "Debug segfaults in GDB"),
                resources = listOf("en.cppreference.com", "CS:APP (Bryant & O'Hallaron)", "Valgrind Manual")
            ),
            MonthTemplate(
                phaseTitle = "Low-Level Computing Foundations",
                title = "Modern C++ & OOP Architecture",
                mainFocus = "C++20, RAII, Smart Pointers (unique_ptr, shared_ptr), Move Semantics",
                secondarySkill = "CMake build systems, Catch2 / GoogleTest",
                track = SkillTrack.PROGRAMMING,
                topics = listOf("Lvalue vs Rvalue references", "Move constructors & perfect forwarding", "Rule of 5", "Templates & Concepts"),
                dsaTarget = "Stacks, Queues, and Priority Queues using C++ STL",
                project = "Thread-Safe Blocking Queue & Generic Thread Pool in C++",
                coreCs = "POSIX pthreads, mutexes, condition variables, atomic operations",
                measurableOutput = "Tested C++20 thread pool executing concurrent tasks without race conditions",
                checkpoint = "Can explain RAII and write modern modern leak-free C++ code.",
                prerequisites = listOf("C Memory Model"),
                learningObjectives = listOf("Master modern C++ RAII", "Understand move semantics", "Write multithreaded code"),
                practiceExercises = listOf("Implement custom unique_ptr", "Build thread pool with std::future", "Solve 15 Stack/Queue problems"),
                resources = listOf("cppreference.com", "Effective Modern C++ (Scott Meyers)", "CMake Tutorial")
            ),
            MonthTemplate(
                phaseTitle = "Core Operating Systems & Networking",
                title = "Linux Systems Programming & Sockets",
                mainFocus = "Linux system calls (fork, exec, pipe, epoll), POSIX sockets, non-blocking I/O",
                secondarySkill = "Linux shell scripting & strace profiling",
                track = SkillTrack.CORE_CS,
                topics = listOf("File descriptors & VFS", "Process scheduling & IPC", "Multiplexing with epoll/kqueue", "Signal handling"),
                dsaTarget = "Binary Trees & AVL Self-Balancing Trees",
                project = "Asynchronous HTTP/1.1 Web Server in C++ using epoll",
                coreCs = "Kernel space vs User space context switches",
                measurableOutput = "Benchmarked HTTP server handling 20,000 requests/sec with wrk",
                checkpoint = "Can explain epoll edge-triggered vs level-triggered and solve 20 Binary Tree problems.",
                prerequisites = listOf("Modern C++"),
                learningObjectives = listOf("Master POSIX system calls", "Build high-throughput socket servers", "Understand kernel scheduling"),
                practiceExercises = listOf("Build Unix shell with pipes", "Implement epoll echo server", "Solve 20 Tree problems"),
                resources = listOf("The Linux Programming Interface (Michael Kerrisk)", "man7.org", "Beej's Guide to Network Programming")
            )
        )

        while (list.size < totalMonths) {
            val m = list.size + 1
            list.add(
                MonthTemplate(
                    phaseTitle = "High-Performance Systems & Databases",
                    title = "Database Internals & In-Memory Storage (Month $m)",
                    mainFocus = "LSM Trees, Write-Ahead Logging (WAL), B-Trees, Cache-Oblivious Algorithms",
                    secondarySkill = "Benchmarking with Google Benchmark",
                    track = SkillTrack.SOFTWARE_DEVELOPMENT,
                    topics = listOf("SSTables & MemTable", "Bloom Filters", "Compaction strategies", "Page cache & fsync semantics"),
                    dsaTarget = "Graphs (Tarjan, Dijkstra) & Dynamic Programming",
                    project = "In-Memory Key-Value Store with Persistence Engine in C++",
                    coreCs = "Disk I/O latency numbers & Flash storage SSD page characteristics",
                    measurableOutput = "Key-Value database with snapshot isolation and benchmark reports",
                    checkpoint = "Can implement an LSM tree compaction engine and pass graph DSA evaluations.",
                    prerequisites = listOf("Month ${m - 1}"),
                    learningObjectives = listOf("Understand database storage engines", "Implement write-ahead logging", "Master graph algorithms"),
                    practiceExercises = listOf("Build Bloom Filter from scratch", "Implement B-Tree search/insert", "Solve 20 Graph problems"),
                    resources = listOf("Database Internals (Alex Petrov)", "LevelDB source code")
                )
            )
        }

        return list.take(totalMonths)
    }

    private fun getAgenticAiCurriculum(totalMonths: Int): List<MonthTemplate> {
        val list = mutableListOf(
            MonthTemplate(
                phaseTitle = "Python Depth & Data Foundations",
                title = "Python 3 & AsyncIO Systems",
                mainFocus = "Pythonic design, AsyncIO event loop, generators, context managers, type hints",
                secondarySkill = "Pytest, Pydantic v2, Ruff linter",
                track = SkillTrack.PROGRAMMING,
                topics = listOf("Async/Await coroutines", "Generators & Iterators", "Pydantic data modeling", "Packaging with Poetry"),
                dsaTarget = "Arrays, Hash Maps, and Priority Queues in Python",
                project = "Asynchronous Data Scraping & Pipeline Engine with Rate Limiting",
                coreCs = "Python Global Interpreter Lock (GIL) & multiprocessing",
                measurableOutput = "Tested asynchronous Python pipeline processing 500 URLs/minute",
                checkpoint = "Can explain Python GIL limitations and write clean async coroutines.",
                prerequisites = listOf("Basic Python syntax"),
                learningObjectives = listOf("Master Python AsyncIO", "Model validated data with Pydantic", "Write production-grade Python"),
                practiceExercises = listOf("Implement custom async worker pool", "Build streaming generator pipeline", "Solve 15 Python DSA problems"),
                resources = listOf("docs.python.org/3", "Pydantic official docs", "Real Python")
            ),
            MonthTemplate(
                phaseTitle = "Generative AI & Function Calling",
                title = "LLMs, Tool Calling & Structured Outputs",
                mainFocus = "Gemini API, OpenAI protocol, function calling, structured JSON output, prompt engineering",
                secondarySkill = "API resilience, exponential backoff, token budgeting",
                track = SkillTrack.AI_ML,
                topics = listOf("System instructions & temperature", "Function / Tool calling schemas", "Output schema validation", "Context window management"),
                dsaTarget = "Trees & Trie structures for prefix matching",
                project = "Autonomous Financial Analysis Bot with Live Stock Tool Calling",
                coreCs = "Transformer attention mechanism & Tokenization (BPE)",
                measurableOutput = "CLI & Web AI Copilot that dynamically executes 5+ tools and generates grounded reports",
                checkpoint = "Can write robust function calling schemas that handle edge-case validation errors.",
                prerequisites = listOf("Python AsyncIO"),
                learningObjectives = listOf("Master LLM API integrations", "Implement reliable tool calling", "Enforce strict schema validation"),
                practiceExercises = listOf("Build calculator tool for Gemini", "Implement token counter & sliding context window", "Solve 15 Tree problems"),
                resources = listOf("ai.google.dev", "platform.openai.com/docs")
            ),
            MonthTemplate(
                phaseTitle = "Autonomous Agents & Vector Systems",
                title = "Vector DBs, Embeddings & RAG Architectures",
                mainFocus = "Semantic search, vector embeddings, chunking strategies, hybrid search, rerankers",
                secondarySkill = "ChromaDB / Qdrant, LangChain/LlamaIndex evaluation",
                track = SkillTrack.AI_ML,
                topics = listOf("Cosine similarity vs Euclidean distance", "Hierarchical chunking", "Retrieval-Augmented Generation", "Hallucination evaluation metrics"),
                dsaTarget = "Graph traversal (BFS/DFS) and Topological Sort",
                project = "Enterprise Academic Paper QA Copilot with Multi-Document RAG",
                coreCs = "Vector indexing (HNSW, IVFFlat) & nearest-neighbor search",
                measurableOutput = "Tested RAG pipeline with 85%+ retrieval precision against benchmark dataset",
                checkpoint = "Can explain HNSW graph indexing and design an end-to-end RAG architecture.",
                prerequisites = listOf("LLM Function Calling"),
                learningObjectives = listOf("Design enterprise RAG pipelines", "Select optimal chunking strategies", "Evaluate retrieval quality"),
                practiceExercises = listOf("Implement custom cosine similarity", "Build hybrid keyword + semantic search", "Solve 15 Graph problems"),
                resources = listOf("pinecone.io/learn", "qdrant.tech/documentation")
            )
        )

        while (list.size < totalMonths) {
            val m = list.size + 1
            list.add(
                MonthTemplate(
                    phaseTitle = "Autonomous Multi-Agent Architectures",
                    title = "Multi-Agent Workflows & Autonomous Loops (Month $m)",
                    mainFocus = "LangGraph / CrewAI, stateful agent loops, human-in-the-loop, reflection & self-correction",
                    secondarySkill = "Observability with LangSmith / OpenTelemetry",
                    track = SkillTrack.AI_ML,
                    topics = listOf("ReAct prompting loop", "State graphs & conditional edges", "Agent memory (short-term & long-term)", "Safety guardrails"),
                    dsaTarget = "Dynamic Programming & Shortest Paths",
                    project = "Autonomous Full-Stack Code Reviewer & Auto-Fixer Agent",
                    coreCs = "Finite State Machines & Agentic loop convergence guarantees",
                    measurableOutput = "Autonomous multi-agent system executing complex multi-step workflows with zero human intervention",
                    checkpoint = "Can architect multi-agent decision systems with loop termination safeguards.",
                    prerequisites = listOf("Month ${m - 1}"),
                    learningObjectives = listOf("Build cyclic multi-agent graphs", "Implement self-correction loops", "Deploy observable AI agents"),
                    practiceExercises = listOf("Build self-reflecting code generator", "Implement agent retry backoff", "Solve 15 DP problems"),
                    resources = listOf("langchain-ai.github.io/langgraph", "deeplearning.ai")
                )
            )
        }

        return list.take(totalMonths)
    }
}
