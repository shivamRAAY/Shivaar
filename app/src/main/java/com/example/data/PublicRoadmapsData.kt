package com.example.data

import com.example.data.model.Difficulty
import com.example.data.model.PublicRoadmap
import com.example.data.model.PublicRoadmapMilestone
import com.example.data.model.RoadmapResourceVerification

object PublicRoadmapsData {

    val allRoadmaps: List<PublicRoadmap> = listOf(
        PublicRoadmap(
            id = "c_programming",
            title = "C Programming & Memory Architecture",
            tagline = "Low-level memory management, pointers, and systems programming fundamentals.",
            category = "Programming Languages",
            skillLevel = Difficulty.BEGINNER,
            audience = "First-year engineering students, systems engineers, and programmers wanting true low-level mastery.",
            prerequisitesSummary = "No prior programming experience required. Basic logical reasoning.",
            estimatedTotalHours = 75,
            verificationLevel = RoadmapResourceVerification.OFFICIAL_DOCUMENTATION,
            officialDocLinks = listOf("https://en.cppreference.com/w/c", "https://iso-9899.info/wiki/The_Standard"),
            optionalNextRoadmaps = listOf("cpp_programming", "dsa_mastery", "operating_systems"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "c_m1",
                    title = "Syntax, Types & Compilation Model",
                    level = Difficulty.BEGINNER,
                    description = "Master preprocessing, compilation, assembly, and linking phases alongside standard I/O.",
                    estimatedHours = 15,
                    topics = listOf("GCC toolchain", "Primitive types & signedness", "Control structures", "Preprocessor macros"),
                    prerequisites = listOf("Basic terminal navigation"),
                    practiceExercises = listOf("Bitwise operations calculator", "Base conversions without library routines"),
                    projectOutputs = listOf("Command-line scientific unit converter in pure C"),
                    assessmentCriteria = "Clean build with -Wall -Wextra -Werror flags without warnings.",
                    recommendedResources = listOf("The C Programming Language (K&R 2nd Ed)", "Modern C by Jens Gustedt"),
                    definitionOfDone = "Understand memory layout of compiled binaries and pass compiler flags test."
                ),
                PublicRoadmapMilestone(
                    id = "c_m2",
                    title = "Pointers, Arrays & Memory Allocation",
                    level = Difficulty.INTERMEDIATE,
                    description = "Stack vs heap memory, pointer arithmetic, dynamic allocation with malloc/calloc/free.",
                    estimatedHours = 25,
                    topics = listOf("Address operator & dereferencing", "Double pointers", "malloc/free lifecycle", "Memory leaks and Valgrind"),
                    prerequisites = listOf("c_m1"),
                    practiceExercises = listOf("Pointer-based string library implementation", "Matrix allocation & deallocation"),
                    projectOutputs = listOf("Dynamically resizing vector (dynamic array) with capacity scaling"),
                    assessmentCriteria = "Zero memory leaks verified by Valgrind under all stress scenarios.",
                    recommendedResources = listOf("Valgrind Documentation (valgrind.org)", "SEI CERT C Coding Standard"),
                    definitionOfDone = "100% leak-free code verified by memory profilers."
                ),
                PublicRoadmapMilestone(
                    id = "c_m3",
                    title = "Data Structures & File I/O in C",
                    level = Difficulty.INTERMEDIATE,
                    description = "Custom structs, unions, linked lists, binary file parsing, and system calls.",
                    estimatedHours = 35,
                    topics = listOf("Struct memory alignment & padding", "Function pointers", "fopen/fread/fwrite", "Error handling with errno"),
                    prerequisites = listOf("c_m2"),
                    practiceExercises = listOf("Singly & doubly linked list from scratch", "Generic hash table with void* keys"),
                    projectOutputs = listOf("Persistent binary database for student academic records with index lookup"),
                    assessmentCriteria = "Proper boundary testing, O(1) average lookup in hash map, zero buffer overflows.",
                    recommendedResources = listOf("Computer Systems: A Programmer's Perspective (CS:APP)", "GNU C Library Manual"),
                    definitionOfDone = "Working persistent storage engine with robust error recovery."
                )
            )
        ),

        PublicRoadmap(
            id = "cpp_programming",
            title = "Modern C++ (C++17/20) & Systems Design",
            tagline = "Object-oriented design, RAII, move semantics, and standard template library mastery.",
            category = "Programming Languages",
            skillLevel = Difficulty.INTERMEDIATE,
            audience = "Engineers looking for high-performance development, competitive programming, and game engine tech.",
            prerequisitesSummary = "Understanding of basic C syntax and memory pointers.",
            estimatedTotalHours = 90,
            verificationLevel = RoadmapResourceVerification.OFFICIAL_DOCUMENTATION,
            officialDocLinks = listOf("https://en.cppreference.com/w/cpp", "https://isocpp.org"),
            optionalNextRoadmaps = listOf("dsa_mastery", "system_design"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "cpp_m1",
                    title = "RAII & Modern Resource Management",
                    level = Difficulty.INTERMEDIATE,
                    description = "Eliminate manual pointer bugs using smart pointers, constructors/destructors, and Rule of 5.",
                    estimatedHours = 25,
                    topics = listOf("std::unique_ptr & std::shared_ptr", "Rule of Three/Five/Zero", "Move semantics & std::move"),
                    prerequisites = listOf("C Pointers"),
                    practiceExercises = listOf("Custom smart pointer wrapper", "Buffer pool class with move semantics"),
                    projectOutputs = listOf("Memory-managed logging engine with automatic resource release"),
                    assessmentCriteria = "No raw delete calls in code; proper exception safety guarantee.",
                    recommendedResources = listOf("Effective Modern C++ by Scott Meyers", "CppCoreGuidelines"),
                    definitionOfDone = "Modern idiomatic resource management with zero leaks."
                ),
                PublicRoadmapMilestone(
                    id = "cpp_m2",
                    title = "Standard Template Library (STL) Deep Dive",
                    level = Difficulty.INTERMEDIATE,
                    description = "Vectors, deques, unordered_maps, sets, iterators, and std::algorithm pipelines.",
                    estimatedHours = 35,
                    topics = listOf("Sequential vs associative containers", "Iterators & ranges", "Custom functors and lambdas", "Time complexities"),
                    prerequisites = listOf("cpp_m1"),
                    practiceExercises = listOf("Custom comparator for priority_queue", "In-place string tokenization"),
                    projectOutputs = listOf("Stock order-book matching engine simulator using std::multimap & priority queues"),
                    assessmentCriteria = "Amortized O(log N) order insertions and O(1) top matches.",
                    recommendedResources = listOf("The C++ Standard Library by Nicolai Josuttis", "cppreference STL guides"),
                    definitionOfDone = "Fluency with all core STL collections and algorithms."
                ),
                PublicRoadmapMilestone(
                    id = "cpp_m3",
                    title = "Templates, Concurrency & Modern Standards",
                    level = Difficulty.ADVANCED,
                    description = "Generic programming with concepts, std::thread, mutexes, and atomics.",
                    estimatedHours = 30,
                    topics = listOf("Template specialization & SFINAE", "C++20 Concepts", "std::jthread & std::mutex", "Lock-free atomic operations"),
                    prerequisites = listOf("cpp_m2"),
                    practiceExercises = listOf("Generic circular queue template", "Producer-consumer thread pool"),
                    projectOutputs = listOf("Multi-threaded asynchronous task worker pool with thread-safe queue"),
                    assessmentCriteria = "Thread-sanitizer verified zero race conditions or deadlocks.",
                    recommendedResources = listOf("C++ Concurrency in Action by Anthony Williams", "CppCon Performance Talks"),
                    definitionOfDone = "Robust multi-threaded application with thread-safety guarantees."
                )
            )
        ),

        PublicRoadmap(
            id = "python_mastery",
            title = "Python 3 & Applied Systems Engineering",
            tagline = "Idiomatic Python, data structures, concurrency, OOP, and automation.",
            category = "Programming Languages",
            skillLevel = Difficulty.BEGINNER,
            audience = "Students, data scientists, and backend engineers seeking robust scripting and production engineering.",
            prerequisitesSummary = "No prerequisites.",
            estimatedTotalHours = 60,
            verificationLevel = RoadmapResourceVerification.OFFICIAL_DOCUMENTATION,
            officialDocLinks = listOf("https://docs.python.org/3/", "https://peps.python.org/pep-0008/"),
            optionalNextRoadmaps = listOf("ai_machine_learning", "backend_development"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "py_m1",
                    title = "Idiomatic Core & Data Structures",
                    level = Difficulty.BEGINNER,
                    description = "List comprehensions, dicts, sets, generator expressions, and Pythonic conventions.",
                    estimatedHours = 15,
                    topics = listOf("PEP 8 guidelines", "Comprehensions & generators", "Context managers with `with`", "Type hinting"),
                    prerequisites = listOf("None"),
                    practiceExercises = listOf("File parser using generator pipelines", "Custom context manager for timing blocks"),
                    projectOutputs = listOf("Automated log analyzer and anomaly summarizer CLI"),
                    assessmentCriteria = "Passed mypy static type checking with zero errors.",
                    recommendedResources = listOf("Fluent Python by Luciano Ramalho", "Official Python Tutorial"),
                    definitionOfDone = "Clean, type-annotated code adhering to PEP 8 standards."
                ),
                PublicRoadmapMilestone(
                    id = "py_m2",
                    title = "OOP, Metaprogramming & Testing",
                    level = Difficulty.INTERMEDIATE,
                    description = "Dunder methods, decorators, dataclasses, pytest suite, and packaging.",
                    estimatedHours = 20,
                    topics = listOf("Special methods (__iter__, __call__)", "Decorators with args", "Dataclasses vs Pydantic", "pytest & mock fixtures"),
                    prerequisites = listOf("py_m1"),
                    practiceExercises = listOf("Rate-limiting function decorator", "Custom iterator with state persistence"),
                    projectOutputs = listOf("Modular HTTP request caching library with full unit test coverage"),
                    assessmentCriteria = "90%+ code coverage verified with pytest-cov.",
                    recommendedResources = listOf("pytest Documentation", "Python Testing with pytest by Brian Okken"),
                    definitionOfDone = "Fully tested and packaged Python module."
                ),
                PublicRoadmapMilestone(
                    id = "py_m3",
                    title = "AsyncIO, Threading & Web Services",
                    level = Difficulty.INTERMEDIATE,
                    description = "Event loop, async/await, multi-processing vs threading, and FastAPI services.",
                    estimatedHours = 25,
                    topics = listOf("GIL (Global Interpreter Lock)", "asyncio tasks & gather", "FastAPI dependency injection", "Pydantic V2 schemas"),
                    prerequisites = listOf("py_m2"),
                    practiceExercises = listOf("Concurrent URL fetcher with rate limit", "Async WebSocket echo server"),
                    projectOutputs = listOf("Production-grade asynchronous REST API with JWT auth and database sessions"),
                    assessmentCriteria = "Benchmark handles 500+ concurrent requests without thread starvation.",
                    recommendedResources = listOf("FastAPI Official Docs (fastapi.tiangolo.com)", "Real Python Async Tutorials"),
                    definitionOfDone = "High-throughput asynchronous web microservice."
                )
            )
        ),

        PublicRoadmap(
            id = "dsa_mastery",
            title = "Data Structures & Algorithms Mastery",
            tagline = "Comprehensive algorithmic problem-solving for top-tier technical interviews.",
            category = "Computer Science",
            skillLevel = Difficulty.INTERMEDIATE,
            audience = "Engineering students preparing for product company placements, coding competitions, and system efficiency.",
            prerequisitesSummary = "Working knowledge of C++, Java, or Python.",
            estimatedTotalHours = 120,
            verificationLevel = RoadmapResourceVerification.INDEPENDENTLY_REVIEWED,
            officialDocLinks = listOf("https://visualgo.net", "https://cp-algorithms.com/"),
            optionalNextRoadmaps = listOf("system_design", "technical_interviews"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "dsa_m1",
                    title = "Linear Structures & Two-Pointers",
                    level = Difficulty.BEGINNER,
                    description = "Arrays, strings, linked lists, sliding window, and two-pointer patterns.",
                    estimatedHours = 30,
                    topics = listOf("Asymptotic notation (Big O)", "Sliding window variable/fixed", "Prefix sums & difference arrays", "Linked list reversing & cycles"),
                    prerequisites = listOf("Basic Programming"),
                    practiceExercises = listOf("50 curated LeetCode/GeeksforGeeks problems", "Implement LRU Cache from scratch"),
                    projectOutputs = listOf("Interactive algorithmic visualization console for sorting & sliding window"),
                    assessmentCriteria = "Solve medium array/string problems in under 25 minutes with optimal time/space.",
                    recommendedResources = listOf("Introduction to Algorithms (CLRS)", "Striver A2Z DSA Sheet"),
                    definitionOfDone = "Fluent application of prefix sums, two pointers, and hash maps."
                ),
                PublicRoadmapMilestone(
                    id = "dsa_m2",
                    title = "Trees, Heaps & Binary Search",
                    level = Difficulty.INTERMEDIATE,
                    description = "Binary trees, BSTs, Trie, segment trees, and binary search on answers.",
                    estimatedHours = 40,
                    topics = listOf("Recursive & iterative traversals", "Lowest Common Ancestor", "Trie prefix matching", "Binary search monotonicity"),
                    prerequisites = listOf("dsa_m1"),
                    practiceExercises = listOf("Word Search II with Trie", "Median of Two Sorted Arrays", "Serialize & Deserialize Binary Tree"),
                    projectOutputs = listOf("Autocomplete search engine prototype backed by an in-memory Trie"),
                    assessmentCriteria = "Clear verbal explanation of recurrence relations and depth constraints.",
                    recommendedResources = listOf("CP-Algorithms (cp-algorithms.com)", "MIT OpenCourseWare 6.006"),
                    definitionOfDone = "Mastery of tree invariants, traversals, and logarithmic searches."
                ),
                PublicRoadmapMilestone(
                    id = "dsa_m3",
                    title = "Graphs & Dynamic Programming",
                    level = Difficulty.ADVANCED,
                    description = "BFS/DFS, Dijkstra, Bellman-Ford, Kruskal/Prim, and 1D/2D DP optimization.",
                    estimatedHours = 50,
                    topics = listOf("Topological sort & Kahn's algorithm", "Disjoint Set Union (Union-Find)", "Shortest path algorithms", "DP states, transitions & space optimization"),
                    prerequisites = listOf("dsa_m2"),
                    practiceExercises = listOf("Alien Dictionary", "Word Break II", "Travelling Salesperson Problem (TSP)", "0/1 Knapsack variants"),
                    projectOutputs = listOf("Real-world metro route & traffic optimization pathfinder CLI"),
                    assessmentCriteria = "Identify DP state space and solve complex graph cycles in under 30 minutes.",
                    recommendedResources = listOf("Competitive Programmer's Handbook by Antti Laaksonen", "NeetCode 150"),
                    definitionOfDone = "Consistent problem-solving across hard graph and dynamic programming challenges."
                )
            )
        ),

        PublicRoadmap(
            id = "fullstack_development",
            title = "Modern Full-Stack Web Engineering",
            tagline = "End-to-end web architecture from responsive frontends to scalable databases and deployment.",
            category = "Software Engineering",
            skillLevel = Difficulty.INTERMEDIATE,
            audience = "Aspiring full-stack developers, project creators, and product builders.",
            prerequisitesSummary = "Basic HTML/CSS and JavaScript/TypeScript knowledge.",
            estimatedTotalHours = 110,
            verificationLevel = RoadmapResourceVerification.INDEPENDENTLY_REVIEWED,
            officialDocLinks = listOf("https://developer.mozilla.org/", "https://react.dev/", "https://nodejs.org/docs/"),
            optionalNextRoadmaps = listOf("cloud_devops", "system_design"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "fs_m1",
                    title = "Modern Frontend & State Management",
                    level = Difficulty.INTERMEDIATE,
                    description = "React / Next.js, hooks, server components, Tailwind CSS, and optimistic UI.",
                    estimatedHours = 40,
                    topics = listOf("React component lifecycle", "Server vs Client Components", "Zustand/Redux state", "Responsive M3 layouts"),
                    prerequisites = listOf("JavaScript fundamentals"),
                    practiceExercises = listOf("Accessible modal dialog from scratch", "Virtual scrolling infinite list"),
                    projectOutputs = listOf("High-performance collaborative Kanban dashboard with offline persistence"),
                    assessmentCriteria = "Lighthouse performance score 95+, 100% keyboard accessibility.",
                    recommendedResources = listOf("React.dev official interactive documentation", "MDN Web Docs"),
                    definitionOfDone = "Responsive, accessible frontend with robust local state management."
                ),
                PublicRoadmapMilestone(
                    id = "fs_m2",
                    title = "Backend APIs, Authentication & Database Layer",
                    level = Difficulty.INTERMEDIATE,
                    description = "Node.js/Express/FastAPI, PostgreSQL schemas, indexing, connection pooling, and OAuth2.",
                    estimatedHours = 40,
                    topics = listOf("RESTful API design principles", "Relational normal forms & ACID", "SQL indexing & EXPLAIN ANALYZE", "JWT vs Session cookies"),
                    prerequisites = listOf("fs_m1"),
                    practiceExercises = listOf("Rate-limiting middleware with Redis token bucket", "SQL query optimization on 1M rows"),
                    projectOutputs = listOf("Multi-tenant SaaS backend with role-based access control (RBAC) and Stripe billing"),
                    assessmentCriteria = "Zero SQL injection vulnerabilities, proper password hashing with bcrypt/argon2.",
                    recommendedResources = listOf("Designing Data-Intensive Applications (DDIA)", "PostgreSQL Official Manual"),
                    definitionOfDone = "Secure, relational database-backed API with automated test suite."
                ),
                PublicRoadmapMilestone(
                    id = "fs_m3",
                    title = "CI/CD, Docker & Production Observability",
                    level = Difficulty.ADVANCED,
                    description = "Containerization, GitHub Actions pipelines, cloud deployments, and telemetry.",
                    estimatedHours = 30,
                    topics = listOf("Multi-stage Dockerfiles", "GitHub Actions workflows", "Reverse proxies (Nginx/Caddy)", "Logging & health checks"),
                    prerequisites = listOf("fs_m2"),
                    practiceExercises = listOf("Dockerize full-stack application under 150MB", "Automate lint-test-deploy pipeline"),
                    projectOutputs = listOf("Fully deployed production web application with custom domain and automated staging/prod environments"),
                    assessmentCriteria = "Zero-downtime rolling updates, automated test verification prior to deployment.",
                    recommendedResources = listOf("Docker Official Guides", "12-Factor App Methodology (12factor.net)"),
                    definitionOfDone = "Production deployment with automated CI/CD and monitoring."
                )
            )
        ),

        PublicRoadmap(
            id = "ai_machine_learning",
            title = "Artificial Intelligence & Applied Machine Learning",
            tagline = "From statistical mathematics and linear algebra to PyTorch, neural networks, and inference.",
            category = "Artificial Intelligence",
            skillLevel = Difficulty.INTERMEDIATE,
            audience = "Students and developers transitioning into AI/ML engineering with rigorous mathematical grounding.",
            prerequisitesSummary = "Python fluency and college-level linear algebra & calculus.",
            estimatedTotalHours = 100,
            verificationLevel = RoadmapResourceVerification.OFFICIAL_DOCUMENTATION,
            officialDocLinks = listOf("https://pytorch.org/docs/", "https://scikit-learn.org/stable/"),
            optionalNextRoadmaps = listOf("generative_ai", "agentic_ai"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "ai_m1",
                    title = "Mathematical Foundations & Classical ML",
                    level = Difficulty.INTERMEDIATE,
                    description = "Vectors, matrices, gradient descent, regression, classification, and evaluation metrics.",
                    estimatedHours = 30,
                    topics = listOf("NumPy vectorization", "Cost functions & backprop derivation", "Bias-variance tradeoff", "Precision, Recall, ROC-AUC"),
                    prerequisites = listOf("Python & Linear Algebra"),
                    practiceExercises = listOf("Implement linear & logistic regression from scratch using pure NumPy", "K-Means clustering"),
                    projectOutputs = listOf("House price prediction and customer churn prediction pipeline with cross-validation"),
                    assessmentCriteria = "Derive gradient equations on paper; explain over-fitting countermeasures.",
                    recommendedResources = listOf("Mathematics for Machine Learning (Deisenroth)", "Andrew Ng Machine Learning Specialization"),
                    definitionOfDone = "Solid mathematical understanding of classical ML models."
                ),
                PublicRoadmapMilestone(
                    id = "ai_m2",
                    title = "Deep Learning & PyTorch Architecture",
                    level = Difficulty.INTERMEDIATE,
                    description = "Multilayer perceptrons, convolutional neural networks (CNNs), and PyTorch tensors.",
                    estimatedHours = 40,
                    topics = listOf("torch.nn modules & autograd", "Custom Dataset & DataLoader", "Transfer learning & fine-tuning", "Learning rate schedulers"),
                    prerequisites = listOf("ai_m1"),
                    practiceExercises = listOf("Train CIFAR-10 image classifier reaching 90%+ accuracy", "Implement custom loss function"),
                    projectOutputs = listOf("Medical imaging diagnosis assistant using pretrained ResNet with Grad-CAM visualization"),
                    assessmentCriteria = "Demonstrate correct validation split without data leakage; inspect gradient norms.",
                    recommendedResources = listOf("Deep Learning by Goodfellow, Bengio, Courville", "PyTorch Official Tutorials"),
                    definitionOfDone = "Competence in training and evaluating deep neural networks in PyTorch."
                ),
                PublicRoadmapMilestone(
                    id = "ai_m3",
                    title = "Sequence Models & Transformer Foundations",
                    level = Difficulty.ADVANCED,
                    description = "RNNs, LSTMs, attention mechanisms, and the self-attention transformer architecture.",
                    estimatedHours = 30,
                    topics = listOf("Scaled dot-product attention", "Multi-head attention implementation", "Positional encodings", "Encoder-decoder models"),
                    prerequisites = listOf("ai_m2"),
                    practiceExercises = listOf("Implement single-head self-attention module in PyTorch", "BPE tokenizer from scratch"),
                    projectOutputs = listOf("Character-level language model trained on Shakespeare / code repositories from scratch"),
                    assessmentCriteria = "Accurate tensor shape transformations through attention matrices.",
                    recommendedResources = listOf("'Attention Is All You Need' (Vaswani et al.)", "The Illustrated Transformer by Jay Alammar"),
                    definitionOfDone = "Mathematical and practical grasp of the Transformer architecture."
                )
            )
        ),

        PublicRoadmap(
            id = "agentic_ai",
            title = "Agentic AI & Autonomous LLM Systems",
            tagline = "Build self-correcting AI agents, tool-calling systems, memory architectures, and multi-agent workflows.",
            category = "Artificial Intelligence",
            skillLevel = Difficulty.ADVANCED,
            audience = "Advanced engineers building autonomous software agents, enterprise assistants, and autonomous coding tools.",
            prerequisitesSummary = "Python fluency, API integrations, and familiarity with LLM generation concepts.",
            estimatedTotalHours = 80,
            verificationLevel = RoadmapResourceVerification.INDEPENDENTLY_REVIEWED,
            officialDocLinks = listOf("https://ai.google.dev/gemini-api/docs", "https://langchain-ai.github.io/langgraph/"),
            optionalNextRoadmaps = listOf("system_design", "cloud_devops"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "ag_m1",
                    title = "Function Calling, Tools & Structured Outputs",
                    level = Difficulty.INTERMEDIATE,
                    description = "Deterministic JSON schemas, function calling protocols, error retry loops, and grounding.",
                    estimatedHours = 20,
                    topics = listOf("Gemini Function Calling schemas", "Type-safe JSON extraction", "Tool execution sandboxing", "Handling hallucinated tool calls"),
                    prerequisites = listOf("REST APIs & Python"),
                    practiceExercises = listOf("Tool-calling agent that interacts with local SQLite database", "Live currency & weather agent"),
                    projectOutputs = listOf("Terminal AI coding assistant with real file read, edit, and bash execution capabilities"),
                    assessmentCriteria = "Safe argument validation, graceful error recovery on invalid tool inputs.",
                    recommendedResources = listOf("Google AI Studio Gemini API Docs", "Prompt Engineering Guide"),
                    definitionOfDone = "100% reliable structured tool calling with automatic validation."
                ),
                PublicRoadmapMilestone(
                    id = "ag_m2",
                    title = "Agentic Loops, Memory & Self-Correction",
                    level = Difficulty.ADVANCED,
                    description = "ReAct (Reason + Act) prompting, stateful memory (short/long-term), reflection, and planning.",
                    estimatedHours = 30,
                    topics = listOf("ReAct framework implementation", "Vector store memory retrieval (RAG)", "Self-reflection & retry mechanisms", "Human-in-the-loop approvals"),
                    prerequisites = listOf("ag_m1"),
                    practiceExercises = listOf("Self-correcting Python script fixer that runs pytest and iterates until clean", "Summarization with citation auditing"),
                    projectOutputs = listOf("Autonomous academic research agent that queries arXiv, cross-checks claims, and writes verified markdown reports"),
                    assessmentCriteria = "Demonstrate automated loop termination, cycle detection, and zero infinite loops.",
                    recommendedResources = listOf("'ReAct: Synergizing Reasoning and Acting in Language Models' (Yao et al.)", "LangGraph Architectural Patterns"),
                    definitionOfDone = "Autonomous agent capable of multi-step problem solving with self-correction."
                ),
                PublicRoadmapMilestone(
                    id = "ag_m3",
                    title = "Multi-Agent Coordination & Production Guardrails",
                    level = Difficulty.ADVANCED,
                    description = "Supervisor architectures, agent collaboration, rate limiting, and safety evaluation.",
                    estimatedHours = 30,
                    topics = listOf("Supervisor/Worker patterns", "Consensus mechanisms", "Latency & token cost optimization", "Guardrails and red-teaming"),
                    prerequisites = listOf("ag_m2"),
                    practiceExercises = listOf("Writer-Critic two-agent essay refiner", "Multi-agent debate system for fact verification"),
                    projectOutputs = listOf("Autonomous software development pod (Product Architect, Coder, QA Engineer agents collaborating on GitHub issues)"),
                    assessmentCriteria = "Cost accounting per run, bounded execution budget, comprehensive logging trace.",
                    recommendedResources = listOf("Anthropic Building Effective Agents Guide", "OpenAI Swarm / AutoGen Architecture Papers"),
                    definitionOfDone = "Production-grade multi-agent workflow with cost and safety guardrails."
                )
            )
        ),

        PublicRoadmap(
            id = "backend_development",
            title = "High-Performance Backend Engineering",
            tagline = "Distributed architectures, microservices, database transactions, caching, and gRPC.",
            category = "Software Engineering",
            skillLevel = Difficulty.INTERMEDIATE,
            audience = "Engineers aspiring to design scalable server backends for large-scale distributed systems.",
            prerequisitesSummary = "Proficiency in at least one backend language (Kotlin, Go, Java, Python, or Node.js).",
            estimatedTotalHours = 95,
            verificationLevel = RoadmapResourceVerification.INDEPENDENTLY_REVIEWED,
            officialDocLinks = listOf("https://grpc.io/docs/", "https://redis.io/docs/"),
            optionalNextRoadmaps = listOf("system_design", "cloud_devops"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "be_m1",
                    title = "Database Internals, ACID & Transactions",
                    level = Difficulty.INTERMEDIATE,
                    description = "Isolation levels, deadlocks, B-Trees vs LSM-Trees, and database migrations.",
                    estimatedHours = 30,
                    topics = listOf("Read Committed vs Serializable", "Two-Phase Locking (2PL)", "Connection pooling tuning", "Flyway / Liquibase migrations"),
                    prerequisites = listOf("Basic SQL"),
                    practiceExercises = listOf("Simulate and resolve bank balance race condition", "Optimize slow query with composite index"),
                    projectOutputs = listOf("Transactional ledger system with strict idempotency keys and audit history"),
                    assessmentCriteria = "Zero phantom reads under serializable isolation level test.",
                    recommendedResources = listOf("Designing Data-Intensive Applications (DDIA)", "High Performance MySQL"),
                    definitionOfDone = "Deep comprehension of database transactions and performance optimization."
                ),
                PublicRoadmapMilestone(
                    id = "be_m2",
                    title = "Distributed Caching & Message Queues",
                    level = Difficulty.INTERMEDIATE,
                    description = "Redis caching strategies (Cache-aside, Write-through), Kafka/RabbitMQ event pipelines.",
                    estimatedHours = 35,
                    topics = listOf("Cache stampede mitigation", "Redis eviction policies", "Idempotent event consumers", "Dead-letter queues"),
                    prerequisites = listOf("be_m1"),
                    practiceExercises = listOf("Distributed rate limiter using Redis Lua scripts", "Pub/sub notification fan-out"),
                    projectOutputs = listOf("Event-driven video processing pipeline with background workers and webhook notifications"),
                    assessmentCriteria = "Zero message loss during worker crashes; at-least-once delivery guarantees.",
                    recommendedResources = listOf("Redis System Architecture Guide", "Kafka: The Definitive Guide"),
                    definitionOfDone = "Scalable asynchronous event pipeline with caching."
                ),
                PublicRoadmapMilestone(
                    id = "be_m3",
                    title = "gRPC, Protobuf & API Gateways",
                    level = Difficulty.ADVANCED,
                    description = "High-throughput binary RPCs, bidirectional streaming, circuit breakers, and rate limiting.",
                    estimatedHours = 30,
                    topics = listOf("Protocol Buffers (proto3)", "HTTP/2 multiplexing in gRPC", "Resilience4j / Envoy circuit breaking", "Distributed tracing with OpenTelemetry"),
                    prerequisites = listOf("be_m2"),
                    practiceExercises = listOf("Bidirectional chat microservice over gRPC", "Circuit breaker fallback on simulated network latency"),
                    projectOutputs = listOf("Microservices mesh with unified API gateway, JWT translation, and telemetry dashboard"),
                    assessmentCriteria = "Sub-10ms inter-service latency, automated service recovery under fault injection.",
                    recommendedResources = listOf("gRPC Documentation (grpc.io)", "Envoy Proxy Architecture"),
                    definitionOfDone = "Production-ready microservices architecture with binary RPC communication."
                )
            )
        ),

        PublicRoadmap(
            id = "cybersecurity",
            title = "Cybersecurity, Network Defense & Ethical Hacking",
            tagline = "Web security vulnerabilities (OWASP Top 10), cryptography, network protocols, and secure coding.",
            category = "Infrastructure & Security",
            skillLevel = Difficulty.INTERMEDIATE,
            audience = "Developers, network engineers, and security analysts wanting deep defensibility.",
            prerequisitesSummary = "Understanding of networking protocols (TCP/IP, HTTP) and basic Linux command line.",
            estimatedTotalHours = 85,
            verificationLevel = RoadmapResourceVerification.OFFICIAL_DOCUMENTATION,
            officialDocLinks = listOf("https://owasp.org/www-project-top-ten/", "https://csrc.nist.gov/"),
            optionalNextRoadmaps = listOf("cloud_devops", "system_design"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "sec_m1",
                    title = "Network Protocols & Traffic Analysis",
                    level = Difficulty.BEGINNER,
                    description = "OSI 7 layers, TCP 3-way handshake, DNS, TLS 1.3 handshake, and packet inspection.",
                    estimatedHours = 25,
                    topics = listOf("Wireshark packet capture", "TCP reset attacks & SYN floods", "TLS certificates & PKI", "DNS poisoning mitigation"),
                    prerequisites = listOf("Basic Networking"),
                    practiceExercises = listOf("Analyze unencrypted vs HTTPS traffic", "Build simple port scanner in Python"),
                    projectOutputs = listOf("Network security audit tool that scans open ports and checks SSL/TLS cipher suites"),
                    assessmentCriteria = "Accurate identification of anomalous packet captures.",
                    recommendedResources = listOf("TCP/IP Illustrated Vol. 1", "Wireshark University Tutorials"),
                    definitionOfDone = "Ability to inspect and diagnose network traffic at the packet level."
                ),
                PublicRoadmapMilestone(
                    id = "sec_m2",
                    title = "Web Security & OWASP Top 10 Exploitation/Defense",
                    level = Difficulty.INTERMEDIATE,
                    description = "SQLi, XSS, CSRF, SSRF, IDOR, and security headers (CSP, CORS, HSTS).",
                    estimatedHours = 35,
                    topics = listOf("SQL Injection & prepared statements", "Stored vs Reflected XSS", "Content Security Policy (CSP)", "Server-Side Request Forgery (SSRF)"),
                    prerequisites = listOf("sec_m1"),
                    practiceExercises = listOf("PortSwigger Web Security Academy Labs", "Bypass basic input sanitization safely"),
                    projectOutputs = listOf("Vulnerable web app demo accompanied by secure patch repository and automated regression security tests"),
                    assessmentCriteria = "Defeat OWASP Top 10 vulnerabilities with verified defenses.",
                    recommendedResources = listOf("OWASP Top 10 Official Documentation", "PortSwigger Web Security Academy"),
                    definitionOfDone = "Practical defense against major web exploitation vectors."
                ),
                PublicRoadmapMilestone(
                    id = "sec_m3",
                    title = "Applied Cryptography & Authentication Security",
                    level = Difficulty.ADVANCED,
                    description = "Symmetric vs asymmetric encryption (AES, RSA, ECC), password hashing, zero-trust architectures.",
                    estimatedHours = 25,
                    topics = listOf("AES-GCM encryption", "Argon2id password hashing", "Public Key Infrastructure (PKI)", "OAuth 2.1 & OIDC token verification"),
                    prerequisites = listOf("sec_m2"),
                    practiceExercises = listOf("Implement end-to-end encrypted messaging proof of concept", "Validate JWT signatures from scratch"),
                    projectOutputs = listOf("Zero-knowledge encrypted password vault CLI with PBKDF2/Argon2 key derivation"),
                    assessmentCriteria = "Zero cryptographic nonce reuse; cryptographically secure pseudo-random generation.",
                    recommendedResources = listOf("Serious Cryptography by Jean-Philippe Aumasson", "NIST Cryptographic Standards"),
                    definitionOfDone = "Production implementation of secure cryptographic protocols."
                )
            )
        ),

        PublicRoadmap(
            id = "system_design",
            title = "System Design & Distributed Scalability",
            tagline = "Architect resilient, high-availability software handling millions of requests with ease.",
            category = "Architecture",
            skillLevel = Difficulty.ADVANCED,
            audience = "Senior students and engineers preparing for Tier-1 engineering architecture interviews and system scaling.",
            prerequisitesSummary = "Solid understanding of databases, networking, and backend development.",
            estimatedTotalHours = 70,
            verificationLevel = RoadmapResourceVerification.INDEPENDENTLY_REVIEWED,
            officialDocLinks = listOf("https://github.com/donnemartin/system-design-primer", "https://aws.amazon.com/architecture/"),
            optionalNextRoadmaps = listOf("cloud_devops", "technical_interviews"),
            milestones = listOf(
                PublicRoadmapMilestone(
                    id = "sd_m1",
                    title = "Distributed Building Blocks & Trade-offs",
                    level = Difficulty.INTERMEDIATE,
                    description = "CAP theorem, PACELC theorem, consistent hashing, load balancing algorithms, and CDNs.",
                    estimatedHours = 20,
                    topics = listOf("Consistent hashing with virtual nodes", "L4 vs L7 load balancing", "Read-heavy vs write-heavy architectures", "CDN edge caching"),
                    prerequisites = listOf("Backend fundamentals"),
                    practiceExercises = listOf("Simulate consistent hashing ring in code", "Capacity estimation math for 100M DAU"),
                    projectOutputs = listOf("Distributed URL shortener architecture blueprint (TinyURL scale: 10,000 writes/sec, 100,000 reads/sec)"),
                    assessmentCriteria = "Accurate latency, QPS, and storage sizing calculations.",
                    recommendedResources = listOf("System Design Primer by Donne Martin", "Designing Data-Intensive Applications"),
                    definitionOfDone = "Rigorous mental model of distributed trade-offs and capacity planning."
                ),
                PublicRoadmapMilestone(
                    id = "sd_m2",
                    title = "Real-World Architecture Deep Dives",
                    level = Difficulty.ADVANCED,
                    description = "Design Twitter/X feed, Uber geospatial dispatch, WhatsApp messaging, and YouTube video ingestion.",
                    estimatedHours = 30,
                    topics = listOf("Fanout-on-write vs fanout-on-read", "Geohashing & QuadTrees", "WebSocket connection management", "Chunked video transcoding queues"),
                    prerequisites = listOf("sd_m1"),
                    practiceExercises = listOf("Diagram multi-region disaster recovery flow", "Design distributed rate limiter with sliding window log"),
                    projectOutputs = listOf("Detailed architecture specification for real-time collaborative document editor (Google Docs scale)"),
                    assessmentCriteria = "Defend single points of failure, partition tolerance, and cache consistency.",
                    recommendedResources = listOf("System Design Interview Vol. 1 & 2 by Alex Xu", "Engineering Blogs (Netflix, Uber, Meta)"),
                    definitionOfDone = "Confidence in end-to-end design of major industrial software platforms."
                ),
                PublicRoadmapMilestone(
                    id = "sd_m3",
                    title = "Resilience, Fault Tolerance & Consensus",
                    level = Difficulty.ADVANCED,
                    description = "Raft consensus, split-brain mitigation, idempotency, and chaos engineering.",
                    estimatedHours = 20,
                    topics = listOf("Raft leader election & log replication", "Two-phase commit vs Sagas", "Deadlock detection", "Circuit breakers & bulkhead patterns"),
                    prerequisites = listOf("sd_m2"),
                    practiceExercises = listOf("Raft leader election state machine in code", "Distributed saga coordinator with compensation steps"),
                    projectOutputs = listOf("High-availability distributed key-value store cluster with leader election and replication"),
                    assessmentCriteria = "Explain consensus failure modes during network partitions.",
                    recommendedResources = listOf("'In Search of an Understandable Consensus Algorithm (Raft)' (Ongaro & Ousterhout)", "Martin Kleppmann Distributed Systems Lectures"),
                    definitionOfDone = "Mastery of distributed consensus and resilient architectures."
                )
            )
        )
    )

    fun getRoadmapById(id: String): PublicRoadmap? {
        return allRoadmaps.find { it.id == id }
    }

    fun searchRoadmaps(query: String, category: String? = null, level: Difficulty? = null): List<PublicRoadmap> {
        val q = query.trim().lowercase()
        return allRoadmaps.filter { roadmap ->
            val matchesQuery = q.isEmpty() ||
                roadmap.title.lowercase().contains(q) ||
                roadmap.tagline.lowercase().contains(q) ||
                roadmap.category.lowercase().contains(q) ||
                roadmap.milestones.any { m -> m.topics.any { it.lowercase().contains(q) } }

            val matchesCategory = category.isNullOrBlank() || category == "All" || roadmap.category.equals(category, ignoreCase = true)
            val matchesLevel = level == null || roadmap.skillLevel == level

            matchesQuery && matchesCategory && matchesLevel
        }
    }
}
