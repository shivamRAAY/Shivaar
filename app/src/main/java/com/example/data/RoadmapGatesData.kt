package com.example.data

import com.example.data.model.GateStatus
import com.example.data.model.PrerequisiteGate

object RoadmapGatesData {
    fun getInitialGates(): List<PrerequisiteGate> = listOf(
        PrerequisiteGate(
            id = "gate_1",
            number = 1,
            name = "Programming Foundation",
            targetMonth = 4,
            requiredTopics = listOf(
                "C syntax, memory addresses, pointer arithmetic",
                "Dynamic memory allocation (malloc, calloc, realloc, free)",
                "Structures, unions, and memory alignment",
                "Git CLI, branching, and GitHub push workflow"
            ),
            practicalEvidence = "A working CLI program in C using dynamic memory with zero memory leaks verified by Valgrind.",
            assessments = "Solve 3 pointer manipulation problems without compiler warnings; explain stack vs heap in an oral viva.",
            projectRequirements = "CLI Student Record & Memory Manager in C pushed to a public GitHub repository.",
            completionConditions = listOf(
                "Zero segmentation faults in pointer exercises",
                "Clean GitHub repository with README and Makefile",
                "Pass college internal test on C fundamentals"
            ),
            remediationSteps = "Spend 1 additional week on pointer diagram exercises; re-implement malloc/free wrappers and review K&R Chapter 5.",
            status = GateStatus.IN_PROGRESS
        ),
        PrerequisiteGate(
            id = "gate_2",
            number = 2,
            name = "DSA Foundation",
            targetMonth = 8,
            requiredTopics = listOf(
                "Time & space complexity analysis (Big-O)",
                "C++ STL containers (vector, unordered_map, set, stack, queue)",
                "Arrays, two-pointers, sliding window, and binary search",
                "Linked lists and recursion tree visualization"
            ),
            practicalEvidence = "120+ unique solved problems logged on LeetCode/GFG with recorded time/space complexities.",
            assessments = "Solve 2 LeetCode Medium problems (Arrays/Binary Search) within 45 minutes without hints.",
            projectRequirements = "Custom implementation of a generic dynamic array and singly linked list in C++.",
            completionConditions = listOf(
                "Cumulative 120+ DSA problems solved",
                "Consistent O(n) or O(log n) analysis on all solutions",
                "No reliance on brute force solutions"
            ),
            remediationSteps = "Redo Striver's Array & Binary Search sheets; write out recurrence relations manually before coding.",
            status = GateStatus.NOT_STARTED
        ),
        PrerequisiteGate(
            id = "gate_3",
            number = 3,
            name = "Web Foundation",
            targetMonth = 14,
            requiredTopics = listOf(
                "Semantic HTML5 & modern CSS3 flexbox/grid",
                "JavaScript ES6+ (closures, event loop, promises, async/await)",
                "DOM manipulation and HTTP fetch requests",
                "Responsive design for mobile, tablet, and desktop"
            ),
            practicalEvidence = "A responsive, accessible multi-page web application communicating with a public REST API.",
            assessments = "Build a live weather or GitHub profile search app in vanilla JS in 3 hours.",
            projectRequirements = "Interactive web application hosted on Vercel/Netlify with zero console errors.",
            completionConditions = listOf(
                "Demonstrated mastery of async/await error handling",
                "Clean responsive layout without horizontal scroll",
                "Lighthouse accessibility score > 90"
            ),
            remediationSteps = "Review JavaScript30 exercises; practice DOM event bubbling and promise chaining drills.",
            status = GateStatus.NOT_STARTED
        ),
        PrerequisiteGate(
            id = "gate_4",
            number = 4,
            name = "Full-Stack Developer",
            targetMonth = 20,
            requiredTopics = listOf(
                "React functional components, hooks, state management",
                "TypeScript strict typing for props and API responses",
                "Node.js & Express RESTful API architecture",
                "PostgreSQL schema design, indexing, and SQL joins"
            ),
            practicalEvidence = "Full-stack application with JWT user authentication, protected routes, and relational database persistence.",
            assessments = "Implement an authenticated CRUD API endpoint with input validation and SQL join in 45 minutes.",
            projectRequirements = "DevStack — Student Portfolio & Project Showcase full-stack repository deployed with live database.",
            completionConditions = listOf(
                "Bcrypt password hashing and secure token storage",
                "Proper 3NF database schema with foreign keys",
                "Documented Postman / Swagger collection"
            ),
            remediationSteps = "Refactor backend API controllers; practice raw SQL queries on pgexercises.com.",
            status = GateStatus.NOT_STARTED
        ),
        PrerequisiteGate(
            id = "gate_5",
            number = 5,
            name = "Production-Level Developer",
            targetMonth = 24,
            requiredTopics = listOf(
                "Docker containerization (multi-stage builds)",
                "Automated testing (unit, integration, and E2E)",
                "CI/CD pipelines via GitHub Actions",
                "Core CS: Operating systems, DBMS transactions, and Computer Networks"
            ),
            practicalEvidence = "Containerized full-stack application with passing GitHub Actions CI pipeline and automated test suite.",
            assessments = "Mock technical interview explaining ACID properties, TCP handshakes, and Docker networking.",
            projectRequirements = "Production-deployed application with healthcheck endpoints, logging, and automated deployment.",
            completionConditions = listOf(
                "Passing test suite with > 70% coverage",
                "Automated CI workflow on every pull request",
                "Clear documentation and system architecture diagram"
            ),
            remediationSteps = "Review Docker documentation; write unit tests for edge cases and simulate network latency.",
            status = GateStatus.NOT_STARTED
        ),
        PrerequisiteGate(
            id = "gate_6",
            number = 6,
            name = "AI Engineer Foundations",
            targetMonth = 32,
            requiredTopics = listOf(
                "Python for data science (NumPy, Pandas vector operations)",
                "Machine learning basics (supervised, unsupervised, regression, classification)",
                "Vector embeddings, similarity search, and vector databases (Chroma/Pinecone)",
                "Retrieval-Augmented Generation (RAG) pipelines and LLM API integrations"
            ),
            practicalEvidence = "A working RAG question-answering assistant citing specific syllabus documents with evaluated accuracy.",
            assessments = "Build an end-to-end document question-answering script using Gemini API and vector embeddings in under 2 hours.",
            projectRequirements = "Shivaar Intel — RAG Document Assistant for University Subjects with live demo.",
            completionConditions = listOf(
                "Zero hallucinated source citations",
                "Vector retrieval latency under 500ms",
                "Clean evaluation metrics report"
            ),
            remediationSteps = "Review LangChain / LlamaIndex official tutorials; practice chunking strategies and embedding distance metrics.",
            status = GateStatus.NOT_STARTED
        ),
        PrerequisiteGate(
            id = "gate_7",
            number = 7,
            name = "Internship Ready",
            targetMonth = 36,
            requiredTopics = listOf(
                "Cumulative 450+ quality DSA problems solved across all patterns",
                "System design fundamentals (caching, load balancing, database sharding)",
                "ATS-optimized software engineering resume (1-page)",
                "STAR method behavioral answers and communication polish"
            ),
            practicalEvidence = "3 completed mock technical interviews with senior engineers or mentors scored above 80%.",
            assessments = "Live 45-minute coding interview clearing a medium/hard algorithmic problem with optimal complexity.",
            projectRequirements = "Portfolio website with 3 verified production project demos, GitHub links, and resume download.",
            completionConditions = listOf(
                "Applied to 30+ verified summer internship listings",
                "Active LinkedIn and GitHub contribution graph",
                "Clear vocalization of thought process while coding"
            ),
            remediationSteps = "Conduct weekly peer mock interviews on Pramp/Interviewing.io; review Blind 75 and Striver's SDE sheet.",
            status = GateStatus.NOT_STARTED
        ),
        PrerequisiteGate(
            id = "gate_8",
            number = 8,
            name = "Placement Ready",
            targetMonth = 44,
            requiredTopics = listOf(
                "550+ cumulative DSA problems solved with fast speed (<20 mins/medium)",
                "High-level and low-level system design for scalable architectures",
                "Thorough mastery of OS, DBMS, Networks, and OOP interview questions",
                "Flagship engineering capstone project with live production users"
            ),
            practicalEvidence = "Cleared multiple company Online Assessments (OA) and technical round simulations.",
            assessments = "Pass a 60-minute technical interview covering both DSA and system design tradeoffs.",
            projectRequirements = "Substantial capstone software engineering project with live traffic, monitoring, and comprehensive docs.",
            completionConditions = listOf(
                "Ready for on-campus and off-campus tier-1/tier-2 placement drives",
                "Confidence in salary and compensation negotiations",
                "Complete portfolio package ready for HR screening"
            ),
            remediationSteps = "Daily timed contest practice on LeetCode/Codeforces; revise Core CS flashcards every morning.",
            status = GateStatus.NOT_STARTED
        )
    )
}
