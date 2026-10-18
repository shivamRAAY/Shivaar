import { 
  DsaProblem, 
  RoadmapMonth, 
  PublicRoadmapTrack, 
  ProjectItem, 
  CareerApplication, 
  CoreCsFlashcard, 
  SubjectAttendance, 
  TaskItem 
} from '../types';

export const MOTIVATIONAL_QUOTES = [
  { text: "Consistency is what transforms average into excellence. 1 problem a day beats 10 problems on Sunday.", author: "Shivaar Engineering Principle" },
  { text: "Talk is cheap. Show me the code.", author: "Linus Torvalds" },
  { text: "The master has failed more times than the beginner has even tried.", author: "Stephen McCranie" },
  { text: "First, solve the problem. Then, write the code.", author: "John Johnson" },
  { text: "Simplicity is prerequisite for reliability.", author: "Edsger W. Dijkstra" },
  { text: "Your Tier 3 college doesn't define your ceiling; your GitHub and LeetCode graph do.", author: "Senior SDE Mentor" }
];

export const INITIAL_TASKS: TaskItem[] = [
  {
    id: 'task-1',
    title: 'Solve LeetCode #15: 3Sum (Two Pointers Pattern)',
    category: 'DSA',
    priority: 'HIGH',
    durationMinutes: 45,
    isCompleted: false,
    whyItMatters: 'Essential medium problem testing sorting, pointer manipulation, and duplicate pruning. Asked frequently at Google & Amazon.',
    instructions: '1. Sort array in O(n log n). 2. Fix the first element with a loop. 3. Use two pointers for remaining two elements. 4. Skip duplicates carefully.',
    definitionOfDone: 'All test cases pass in LeetCode with O(n^2) time and O(1) extra space. Write notes on duplicate handling.',
    resourceName: 'NeetCode 150 - 3Sum Video & Solution',
    resourceUrl: 'https://leetcode.com/problems/3sum/',
    scheduledDate: new Date().toISOString().split('T')[0]
  },
  {
    id: 'task-2',
    title: 'Implement Database Connection Pooling & JWT Auth Middleware',
    category: 'DEV',
    priority: 'HIGH',
    durationMinutes: 60,
    isCompleted: false,
    whyItMatters: 'Foundational backend skill. Unauthenticated routes and connection leaks destroy production scalability.',
    instructions: '1. Create postgres/mongo pool singleton. 2. Verify Bearer token in Express/FastAPI middleware. 3. Return 401 on expired tokens.',
    definitionOfDone: 'Endpoint responds with 200 on valid JWT and 401 on invalid/missing header. Postman tests saved.',
    resourceName: 'MDN Web Docs - JWT Authentication Guide',
    resourceUrl: 'https://jwt.io/introduction',
    scheduledDate: new Date().toISOString().split('T')[0]
  },
  {
    id: 'task-3',
    title: 'Review Operating Systems: Process vs Thread & Context Switching',
    category: 'CS',
    priority: 'MEDIUM',
    durationMinutes: 30,
    isCompleted: false,
    whyItMatters: 'Standard interview question in every technical interview round at Microsoft, Adobe, and startups.',
    instructions: '1. Contrast memory layout (stack vs heap). 2. Explain why thread context switch is cheaper. 3. Draw PCB diagram.',
    definitionOfDone: 'Answer 5 Core CS flashcards in Shivaar Career Hub without checking notes.',
    resourceName: 'OSTEP - Three Easy Pieces (Concurrency)',
    resourceUrl: 'https://pages.cs.wisc.edu/~remzi/OSTEP/',
    scheduledDate: new Date().toISOString().split('T')[0]
  },
  {
    id: 'task-4',
    title: 'Prepare Unit 2 Summary Notes for DBMS Mid-Sem Exam',
    category: 'ACADEMICS',
    priority: 'MEDIUM',
    durationMinutes: 45,
    isCompleted: false,
    whyItMatters: 'Maintains 8.5+ CGPA required to clear high-package placement cutoff filters.',
    instructions: '1. Summarize BCNF vs 3NF. 2. Practice lossy vs lossless decomposition proof questions.',
    definitionOfDone: '1-page cheatsheet created covering canonical cover and normal forms with examples.',
    resourceName: 'Silberschatz Database Concepts',
    resourceUrl: 'https://www.db-book.com/',
    scheduledDate: new Date().toISOString().split('T')[0]
  }
];

export const INITIAL_DSA_PROBLEMS: DsaProblem[] = [
  // Arrays & Hashing
  { id: 'dsa-1', title: 'Two Sum', category: 'Arrays & Hashing', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Hash Map Lookup', url: 'https://leetcode.com/problems/two-sum/' },
  { id: 'dsa-2', title: 'Valid Anagram', category: 'Arrays & Hashing', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Frequency Counter', url: 'https://leetcode.com/problems/valid-anagram/' },
  { id: 'dsa-3', title: 'Group Anagrams', category: 'Arrays & Hashing', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Hash Map with Sorted Key', url: 'https://leetcode.com/problems/group-anagrams/' },
  { id: 'dsa-4', title: 'Top K Frequent Elements', category: 'Arrays & Hashing', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Bucket Sort / Min Heap', url: 'https://leetcode.com/problems/top-k-frequent-elements/' },
  { id: 'dsa-5', title: 'Product of Array Except Self', category: 'Arrays & Hashing', difficulty: 'Medium', status: 'Revising', platform: 'LeetCode', pattern: 'Prefix & Suffix Products', url: 'https://leetcode.com/problems/product-of-array-except-self/' },
  { id: 'dsa-6', title: 'Longest Consecutive Sequence', category: 'Arrays & Hashing', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Hash Set Sequence Start', url: 'https://leetcode.com/problems/longest-consecutive-sequence/' },
  
  // Two Pointers & Sliding Window
  { id: 'dsa-7', title: 'Valid Palindrome', category: 'Two Pointers', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Opposite Pointers', url: 'https://leetcode.com/problems/valid-palindrome/' },
  { id: 'dsa-8', title: '3Sum', category: 'Two Pointers', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Sorted Array + Two Pointers', url: 'https://leetcode.com/problems/3sum/' },
  { id: 'dsa-9', title: 'Container With Most Water', category: 'Two Pointers', difficulty: 'Medium', status: 'Solved', platform: 'LeetCode', pattern: 'Greedy Width Shrinking', url: 'https://leetcode.com/problems/container-with-most-water/' },
  { id: 'dsa-10', title: 'Best Time to Buy and Sell Stock', category: 'Sliding Window', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Dynamic Window Minimum', url: 'https://leetcode.com/problems/best-time-to-buy-and-sell-stock/' },
  { id: 'dsa-11', title: 'Longest Substring Without Repeating Characters', category: 'Sliding Window', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Sliding Window + Last Seen Index', url: 'https://leetcode.com/problems/longest-substring-without-repeating-characters/' },
  { id: 'dsa-12', title: 'Longest Repeating Character Replacement', category: 'Sliding Window', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Sliding Window + Max Frequency', url: 'https://leetcode.com/problems/longest-repeating-character-replacement/' },

  // Linked Lists
  { id: 'dsa-13', title: 'Reverse Linked List', category: 'Linked Lists', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Iterative 3-Pointers', url: 'https://leetcode.com/problems/reverse-linked-list/' },
  { id: 'dsa-14', title: 'Merge Two Sorted Lists', category: 'Linked Lists', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Dummy Head Pointer', url: 'https://leetcode.com/problems/merge-two-sorted-lists/' },
  { id: 'dsa-15', title: 'Reorder List', category: 'Linked Lists', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Fast/Slow + Reverse + Interleave', url: 'https://leetcode.com/problems/reorder-list/' },
  { id: 'dsa-16', title: 'Remove Nth Node From End of List', category: 'Linked Lists', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Two Pointers with K-Gap', url: 'https://leetcode.com/problems/remove-nth-node-from-end-of-list/' },
  { id: 'dsa-17', title: 'Linked List Cycle', category: 'Linked Lists', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: "Floyd's Tortoise and Hare", url: 'https://leetcode.com/problems/linked-list-cycle/' },

  // Binary Search
  { id: 'dsa-18', title: 'Binary Search', category: 'Binary Search', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Divide and Conquer', url: 'https://leetcode.com/problems/binary-search/' },
  { id: 'dsa-19', title: 'Search in Rotated Sorted Array', category: 'Binary Search', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Modified Binary Search', url: 'https://leetcode.com/problems/search-in-rotated-sorted-array/' },
  { id: 'dsa-20', title: 'Find Minimum in Rotated Sorted Array', category: 'Binary Search', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Binary Search Inflection Point', url: 'https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/' },

  // Trees & BST
  { id: 'dsa-21', title: 'Invert Binary Tree', category: 'Trees', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'DFS Recursion', url: 'https://leetcode.com/problems/invert-binary-tree/' },
  { id: 'dsa-22', title: 'Maximum Depth of Binary Tree', category: 'Trees', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'DFS / BFS Level Order', url: 'https://leetcode.com/problems/maximum-depth-of-binary-tree/' },
  { id: 'dsa-23', title: 'Lowest Common Ancestor of a BST', category: 'Trees', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'BST Value Split', url: 'https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/' },
  { id: 'dsa-24', title: 'Binary Tree Level Order Traversal', category: 'Trees', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Queue BFS', url: 'https://leetcode.com/problems/binary-tree-level-order-traversal/' },
  { id: 'dsa-25', title: 'Validate Binary Search Tree', category: 'Trees', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Range Boundary DFS (min, max)', url: 'https://leetcode.com/problems/validate-binary-search-tree/' },

  // Graphs
  { id: 'dsa-26', title: 'Number of Islands', category: 'Graphs', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Grid DFS/BFS Sink Traversal', url: 'https://leetcode.com/problems/number-of-islands/' },
  { id: 'dsa-27', title: 'Clone Graph', category: 'Graphs', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Graph DFS + Hash Map Cache', url: 'https://leetcode.com/problems/clone-graph/' },
  { id: 'dsa-28', title: 'Course Schedule', category: 'Graphs', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: "Topological Sort / Kahn's Algo", url: 'https://leetcode.com/problems/course-schedule/' },
  { id: 'dsa-29', title: 'Pacific Atlantic Water Flow', category: 'Graphs', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Reverse Ocean Border DFS', url: 'https://leetcode.com/problems/pacific-atlantic-water-flow/' },

  // Dynamic Programming
  { id: 'dsa-30', title: 'Climbing Stairs', category: 'Dynamic Programming', difficulty: 'Easy', status: 'Solved', platform: 'LeetCode', pattern: 'Fibonacci Space Optimization', url: 'https://leetcode.com/problems/climbing-stairs/' },
  { id: 'dsa-31', title: 'Coin Change', category: 'Dynamic Programming', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Bottom-Up Tabulation (Unbounded Knapsack)', url: 'https://leetcode.com/problems/coin-change/' },
  { id: 'dsa-32', title: 'Longest Increasing Subsequence', category: 'Dynamic Programming', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'DP O(n^2) or Binary Search O(n log n)', url: 'https://leetcode.com/problems/longest-increasing-subsequence/' },
  { id: 'dsa-33', title: 'Word Break', category: 'Dynamic Programming', difficulty: 'Medium', status: 'Unsolved', platform: 'LeetCode', pattern: 'Prefix DP + Hash Set', url: 'https://leetcode.com/problems/word-break/' }
];

export const INITIAL_ROADMAP_MONTHS: RoadmapMonth[] = [
  // Year 1 (Months 1-12)
  { monthNumber: 1, semester: 1, title: 'C & Programming Logic Foundations', summary: 'Variables, loops, pointers, memory allocation, and debugging mental models.', keyTopics: ['C Syntax', 'Pointers & Memory', 'Recursion basics', 'Time Complexity intro'], milestoneProject: 'Terminal-based Banking & Account Manager', verificationGate: 'Able to write and debug recursive functions and pointer arithmetic without segfaults.', isCompleted: true },
  { monthNumber: 2, semester: 1, title: 'Linux, Git & Developer Environment', summary: 'Bash commands, SSH keys, Git branching, open source collaboration workflow.', keyTopics: ['Bash Scripting', 'Git Branching/Merging', 'GitHub Workflows', 'Neovim/VSCode Setup'], milestoneProject: 'Automated System Health Monitoring Script', verificationGate: 'GitHub profile configured with clean commits and README documentation.', isCompleted: true },
  { monthNumber: 3, semester: 1, title: 'Basic Data Structures in C++', summary: 'Static vs dynamic arrays, STL vectors, strings, and custom matrix math.', keyTopics: ['C++ STL Basics', 'Dynamic Arrays', 'String Manipulation', 'Basic Sorting (Merge, Quick)'], milestoneProject: 'High-performance Matrix Operations Library', verificationGate: 'Implement Merge Sort from scratch without looking up references.', isCompleted: true },
  { monthNumber: 4, semester: 1, title: 'Discrete Mathematics & Boolean Logic', summary: 'Set theory, propositional logic, graph theory foundations, combinatorics.', keyTopics: ['Truth Tables', 'Recurrence Relations', 'Pigeonhole Principle', 'Combinatorics'], milestoneProject: 'Propositional Logic Solver CLI', verificationGate: 'Solve 10 recurrence relation problems using Master Theorem.', isCompleted: false },
  { monthNumber: 5, semester: 1, title: 'Object-Oriented Programming (OOP) in C++', summary: 'Classes, inheritance, polymorphism, encapsulation, memory management.', keyTopics: ['Constructors & Destructors', 'Virtual Functions', 'Friend Classes', 'RAII Pattern'], milestoneProject: 'CLI RPG Game Engine with Polymorphic Characters', verificationGate: 'Demonstrate deep understanding of virtual tables (vtable) and dynamic dispatch.', isCompleted: false },
  { monthNumber: 6, semester: 1, title: 'Semester 1 Gate & First Hackathon Prep', summary: 'Revision of Core Subjects, CGPA target check, entering first beginner hackathon.', keyTopics: ['Mid-term & End-term Prep', 'Hackathon Brainstorming', 'Presentation Skills'], milestoneProject: 'Hackathon Submission: Student Utility Web App', verificationGate: 'Maintain 8.0+ SGPA in Semester 1 exams.', isCompleted: false },

  // Year 2 (Months 13-24)
  { monthNumber: 13, semester: 3, title: 'Advanced Data Structures & Algorithms', summary: 'Binary Trees, BST, Heaps, Priority Queues, Graph BFS/DFS.', keyTopics: ['Binary Search Trees', 'Heaps & MinHeap STL', 'Graph Representations', 'Dijkstra Algorithm'], milestoneProject: 'Shortest Path Campus Navigator using Graph Search', verificationGate: 'Solve 50 LeetCode Medium problems across Trees and Graphs.', isCompleted: false },
  { monthNumber: 14, semester: 3, title: 'Database Management Systems (DBMS) & SQL', summary: 'Relational algebra, ER diagrams, Normalization (1NF to BCNF), indexing, transactions.', keyTopics: ['SQL DDL/DML', 'B-Tree Indexing', 'ACID Transactions', 'Joins & Subqueries'], milestoneProject: 'E-Commerce Database Schema with Stored Procedures & Indexing', verificationGate: 'Write complex nested queries with subqueries and explain query execution plans.', isCompleted: false },
  { monthNumber: 15, semester: 3, title: 'Full-Stack Web Development Foundations', summary: 'TypeScript, React 19, Node.js, Express, RESTful API architecture.', keyTopics: ['Modern React Hooks', 'Tailwind/Modern CSS', 'REST API Design', 'PostgreSQL / Prisma ORM'], milestoneProject: 'Full-Stack Developer Portfolio with CMS & Project Showcase', verificationGate: 'Deploy a full-stack CRUD application to free hosting with zero errors.', isCompleted: false },
  { monthNumber: 18, semester: 3, title: 'Semester 3 Gate: 100 LeetCode & Open Source', summary: 'Core placement threshold: 100 quality DSA problems solved + 1 merged open source PR.', keyTopics: ['Striver SDE Sheet 1-30', 'GitHub Pull Request Workflow', 'Competitive Programming basics'], milestoneProject: 'Merged Pull Request to an active Open Source repository', verificationGate: 'LeetCode profile has 100+ problems solved; GitHub has active green commit streaks.', isCompleted: false },

  // Year 3 (Months 25-36)
  { monthNumber: 25, semester: 5, title: 'Operating Systems & Concurrency Deep Dive', summary: 'Processes, threads, deadlocks, virtual memory, paging, POSIX system calls.', keyTopics: ['Mutex & Semaphores', 'Page Replacement Algos', 'Fork & Exec', 'CPU Scheduling'], milestoneProject: 'Multi-threaded Web Server with Worker Pool in C/C++', verificationGate: 'Implement a deadlock-free producer-consumer pipeline with mutexes.', isCompleted: false },
  { monthNumber: 26, semester: 5, title: 'Computer Networks & Distributed Systems Basics', summary: 'OSI 7 layers, TCP/IP 3-way handshake, DNS, WebSockets, HTTP 2/3.', keyTopics: ['Socket Programming', 'TCP Congestion Control', 'REST vs GraphQL vs gRPC', 'Reverse Proxies (Nginx)'], milestoneProject: 'Real-time WebSocket Collaborative Chat with Room Management', verificationGate: 'Explain packet lifecycle from browser URL enter to rendering in deep detail.', isCompleted: false },
  { monthNumber: 29, semester: 5, title: 'High-Level System Design (HLD) Foundations', summary: 'Scalability, microservices, load balancers, caching (Redis), message queues (Kafka).', keyTopics: ['CAP Theorem', 'Consistent Hashing', 'Rate Limiting', 'Database Sharding'], milestoneProject: 'High-Scale URL Shortener (Bitly Architecture) with Redis & PostgreSQL', verificationGate: 'Perform an end-to-end 45-minute mock system design interview.', isCompleted: false },
  { monthNumber: 30, semester: 5, title: 'Internship Season & Online Assessments (OAs)', summary: 'Company application sprint, resume tailoring, tackling tricky platform test patterns.', keyTopics: ['OA HackerRank/CodeSignal prep', 'Behavioral STAR method', 'Cold Outreach on LinkedIn'], milestoneProject: 'Production-ready Resume with 3 live hosted projects & metrics', verificationGate: 'Apply to 30+ verified internship postings; clear at least 2 technical OAs.', isCompleted: false },

  // Year 4 (Months 37-48)
  { monthNumber: 37, semester: 7, title: 'Final Placement Season: Day 1 Campus Hiring', summary: 'Super-tier Day 1 company interviews: Google, Microsoft, Amazon, Atlassian, Oracle.', keyTopics: ['Blind 75 Speed Drills', 'Live Whiteboard Coding', 'Mock Technical Interviews'], milestoneProject: 'Capstone Production Platform (Real Users & Live Domain)', verificationGate: 'Secure confirmed full-time SDE job offer with 12+ LPA baseline.', isCompleted: false },
  { monthNumber: 42, semester: 7, title: 'Advanced Cloud, Docker & Kubernetes Deployment', summary: 'Container orchestration, CI/CD pipelines with GitHub Actions, AWS infrastructure.', keyTopics: ['Docker Multi-stage builds', 'Kubernetes Deployments', 'Terraform IaC', 'Monitoring with Prometheus'], milestoneProject: 'Zero-Downtime Microservices Pipeline on Kubernetes', verificationGate: 'All projects containerized with automated CI/CD unit testing and deployment.', isCompleted: false },
  { monthNumber: 48, semester: 8, title: 'Graduation & Senior Engineering Transition', summary: 'Major Degree Capstone submission, offer negotiation, corporate SDE prep.', keyTopics: ['Offer Negotiation Strategies', 'Corporate Team Communication', 'Code Review Best Practices'], milestoneProject: 'B.Tech CSE Final Year Major Project & Research Publication', verificationGate: 'Graduate with First Class Distinction (8.0+ CGPA) and signed offer letter.', isCompleted: false }
];

export const PUBLIC_ROADMAP_TRACKS: PublicRoadmapTrack[] = [
  {
    id: 'track-fullstack',
    title: 'Full-Stack Web Engineer',
    subtitle: 'From frontend ergonomics to distributed microservices and database clustering',
    durationMonths: 6,
    level: 'Intermediate',
    averageCTC: '₹14 - 28 LPA',
    icon: 'Globe',
    tags: ['React', 'TypeScript', 'Node.js', 'PostgreSQL', 'Docker', 'AWS'],
    phases: [
      {
        phaseNumber: 1,
        title: 'Modern Frontend Architecture',
        skills: ['TypeScript', 'React 19 Hooks', 'State Management (Zustand/Redux)', 'Tailwind CSS', 'Responsive Mobile-First UI'],
        resources: [
          { name: 'React Official Docs & Deep Dive', url: 'https://react.dev', free: true },
          { name: 'Full Stack Open - University of Helsinki', url: 'https://fullstackopen.com', free: true }
        ]
      },
      {
        phaseNumber: 2,
        title: 'Scalable Backend & APIs',
        skills: ['Node.js / Express', 'REST & GraphQL', 'PostgreSQL & Prisma', 'Redis Caching', 'Authentication (JWT & OAuth2)'],
        resources: [
          { name: 'Node.js Design Patterns Guide', url: 'https://nodejs.org', free: true },
          { name: 'PostgreSQL Tutorial & Exercises', url: 'https://www.postgresqltutorial.com', free: true }
        ]
      },
      {
        phaseNumber: 3,
        title: 'DevOps, Containers & Cloud Deployment',
        skills: ['Docker & Docker Compose', 'GitHub Actions CI/CD', 'AWS ECS / Vercel', 'Nginx Reverse Proxy', 'Monitoring & Logging'],
        resources: [
          { name: 'Docker 101 Tutorial', url: 'https://docker-curriculum.com', free: true },
          { name: 'Free AWS Cloud Practitioner Essentials', url: 'https://aws.amazon.com/training', free: true }
        ]
      }
    ]
  },
  {
    id: 'track-aiml',
    title: 'AI / Machine Learning Specialist',
    subtitle: 'Mathematical intuition, deep learning, PyTorch, LLM fine-tuning and MLOps',
    durationMonths: 8,
    level: 'Advanced',
    averageCTC: '₹18 - 36 LPA',
    icon: 'Cpu',
    tags: ['Python', 'PyTorch', 'Transformers', 'Math for ML', 'Vector DBs', 'FastAPI'],
    phases: [
      {
        phaseNumber: 1,
        title: 'Mathematical Rigor & Data Foundations',
        skills: ['Linear Algebra & Matrix Decompositions', 'Multivariable Calculus', 'Probability & Statistics', 'NumPy, Pandas, Matplotlib'],
        resources: [
          { name: '3Blue1Brown - Essence of Linear Algebra', url: 'https://www.youtube.com/playlist?list=PLZHQObOWTQDPD3MizzM2xVFitgF8hE_ab', free: true },
          { name: 'Stanford CS229: Machine Learning Notes', url: 'https://cs229.stanford.edu', free: true }
        ]
      },
      {
        phaseNumber: 2,
        title: 'Deep Learning & Modern NLP',
        skills: ['PyTorch Foundations', 'CNNs for Computer Vision', 'RNNs & Attention Mechanisms', 'Transformer Architecture', 'HuggingFace Ecosystem'],
        resources: [
          { name: 'Andrej Karpathy - Neural Networks Zero to Hero', url: 'https://karpathy.ai/zero-to-hero.html', free: true },
          { name: 'Fast.ai Practical Deep Learning for Coders', url: 'https://course.fast.ai', free: true }
        ]
      },
      {
        phaseNumber: 3,
        title: 'Generative AI, RAG & MLOps',
        skills: ['LLM Orchestration (LangChain, LlamaIndex)', 'Vector Databases (Chroma, Pinecone)', 'Quantization (QLoRA)', 'MLflow & Docker Deployment'],
        resources: [
          { name: 'DeepLearning.AI Short Courses on RAG & LLMs', url: 'https://www.deeplearning.ai', free: true }
        ]
      }
    ]
  },
  {
    id: 'track-devops',
    title: 'Cloud & DevOps Platform Architect',
    subtitle: 'Linux kernel, Infrastructure as Code, Kubernetes orchestration, and 99.99% uptime',
    durationMonths: 6,
    level: 'Intermediate',
    averageCTC: '₹16 - 32 LPA',
    icon: 'Cloud',
    tags: ['Linux', 'Kubernetes', 'Terraform', 'Prometheus', 'AWS', 'Go'],
    phases: [
      {
        phaseNumber: 1,
        title: 'Systems & Networking Mastery',
        skills: ['Advanced Linux CLI & Bash', 'Systemd & Process Management', 'TCP/IP, DNS, TLS Certificates', 'Networking namespaces & firewalls'],
        resources: [
          { name: 'Linux Journey - Interactive OS Guide', url: 'https://linuxjourney.com', free: true }
        ]
      },
      {
        phaseNumber: 2,
        title: 'Infrastructure as Code & CI/CD',
        skills: ['Terraform (HCL)', 'Ansible automation', 'GitHub Actions & GitLab CI', 'Secret management (Vault)', 'Multi-cloud architectures'],
        resources: [
          { name: 'HashiCorp Terraform Associate Tutorials', url: 'https://developer.hashicorp.com/terraform', free: true }
        ]
      },
      {
        phaseNumber: 3,
        title: 'Kubernetes & Site Reliability (SRE)',
        skills: ['Kubernetes Pods, Services, Ingress', 'Helm charts', 'Prometheus & Grafana alerting', 'Chaos engineering & Incident response'],
        resources: [
          { name: 'Kubernetes the Hard Way by Kelsey Hightower', url: 'https://github.com/kelseyhightower/kubernetes-the-hard-way', free: true }
        ]
      }
    ]
  },
  {
    id: 'track-systems',
    title: 'Systems & Low-Level Engineer',
    subtitle: 'Rust, C++, Kernel hacking, high-throughput distributed protocols and compilers',
    durationMonths: 9,
    level: 'Advanced',
    averageCTC: '₹22 - 45 LPA',
    icon: 'Terminal',
    tags: ['Rust', 'C++', 'Operating Systems', 'Concurrency', 'Networking', 'x86_64'],
    phases: [
      {
        phaseNumber: 1,
        title: 'Rust Memory Safety & Concurrency',
        skills: ['Ownership, Borrowing & Lifetimes', 'Smart Pointers (Rc, Arc, RefCell)', 'Async Rust (Tokio)', 'Zero-cost abstractions'],
        resources: [
          { name: 'The Rust Programming Language Book', url: 'https://doc.rust-lang.org/book/', free: true }
        ]
      },
      {
        phaseNumber: 2,
        title: 'Low-Level Operating System Internals',
        skills: ['Virtual Memory & Page Tables', 'Lock-Free Data Structures', 'Custom Allocators', 'CPU Caching & False Sharing'],
        resources: [
          { name: 'Writing an OS in Rust by Philipp Oppermann', url: 'https://os.phil-opp.com', free: true }
        ]
      }
    ]
  }
];

export const INITIAL_PROJECTS: ProjectItem[] = [
  {
    id: 'proj-1',
    title: 'Distributed Key-Value Store with Raft Consensus',
    tier: 'Advanced',
    tagline: 'Fault-tolerant replicated storage cluster adhering to Raft distributed consensus',
    description: 'Implements leader election, log replication, and client redirect protocols. Survives minority node partitions without data corruption.',
    techStack: ['Go', 'gRPC', 'Protobuf', 'Docker'],
    githubUrl: 'https://github.com/username/raft-kvstore',
    liveUrl: 'https://raft-cluster-demo.web.app',
    recruiterHook: 'Demonstrates deep grasp of distributed systems, network partitions, and thread-safe state machines.',
    status: 'In Progress',
    readinessScore: 85
  },
  {
    id: 'proj-2',
    title: 'DevPulse: Real-Time Developer Collaboration & Code Canvas',
    tier: 'Intermediate',
    tagline: 'Multiplayer code editor with live AST linting and audio rooms',
    description: 'Built with React 19, CRDTs (Yjs) for conflict-free concurrent editing, and WebSockets for low-latency cursor synchronization.',
    techStack: ['React', 'TypeScript', 'WebSockets', 'Tailwind', 'Node.js', 'PostgreSQL'],
    githubUrl: 'https://github.com/username/devpulse-collab',
    liveUrl: 'https://devpulse-demo.vercel.app',
    recruiterHook: 'Showcases production-grade WebSockets, real-time synchronization, and sleek UX.',
    status: 'Shipped',
    readinessScore: 95
  },
  {
    id: 'proj-3',
    title: 'Linux Kernel Process Monitor & Syscall Tracer (CLI)',
    tier: 'Foundation',
    tagline: 'Lightweight htop alternative inspecting procfs and memory statistics',
    description: 'Parses /proc pseudo-filesystem to extract real-time process CPU utilization, memory maps, and open file descriptors.',
    techStack: ['C++', 'Linux POSIX', 'Make'],
    githubUrl: 'https://github.com/username/proc-tracer-cpp',
    recruiterHook: 'Proves mastery of Linux POSIX APIs, file descriptors, and low-level memory usage.',
    status: 'Shipped',
    readinessScore: 80
  }
];

export const INITIAL_CAREER_APPS: CareerApplication[] = [
  {
    id: 'app-1',
    company: 'Google',
    role: 'Software Engineering Intern (Summer)',
    status: 'OA',
    deadline: '2026-11-15',
    stipendOrPackage: '₹1.1L / month',
    notes: 'Cleared resume screen. Received 90-min CodeSignal assessment link. Focus on Graphs and 2D DP.'
  },
  {
    id: 'app-2',
    company: 'Microsoft',
    role: 'Software Engineer (New Grad SDE-1)',
    status: 'Technical',
    deadline: '2026-10-30',
    stipendOrPackage: '₹24 LPA CTC',
    notes: 'Round 1 technical scheduled: Live coding on Binary Trees and Low-Level Design of Parking Lot.'
  },
  {
    id: 'app-3',
    company: 'Atlassian',
    role: 'Graduate Software Engineer',
    status: 'Applied',
    deadline: '2026-11-01',
    stipendOrPackage: '₹26 LPA CTC',
    notes: 'Applied via employee referral from college alumnus. Highlighted DevPulse project on resume.'
  },
  {
    id: 'app-4',
    company: 'Amazon',
    role: 'SDE Intern',
    status: 'Wishlist',
    deadline: '2026-12-01',
    stipendOrPackage: '₹80K / month',
    notes: 'Portal opens next week. Need to brush up on 16 Leadership Principles.'
  }
];

export const INITIAL_CORE_CS_FLASHCARDS: CoreCsFlashcard[] = [
  {
    id: 'fc-1',
    subject: 'OS',
    question: 'What is the fundamental difference between a Process and a Thread?',
    answer: 'A Process is an executing program instance with its own isolated address space (code, data, heap, stack). A Thread is a lightweight dispatch unit within a process that shares the code, data, and heap segments with other peer threads, maintaining only its own private stack and register state. Thread context switching is significantly faster because it avoids flushing the CPU Translation Lookaside Buffer (TLB).',
    status: 'Mastered'
  },
  {
    id: 'fc-2',
    subject: 'OS',
    question: 'What are the 4 Coffman conditions required for a Deadlock to occur?',
    answer: '1. Mutual Exclusion: At least one resource must be held in a non-shareable mode.\n2. Hold and Wait: A process holds resources while requesting additional ones.\n3. No Preemption: Resources cannot be forcibly taken; only released voluntarily.\n4. Circular Wait: A closed loop exists where each process waits for a resource held by the next.',
    status: 'Review'
  },
  {
    id: 'fc-3',
    subject: 'DBMS',
    question: 'Explain ACID properties in relational database transactions.',
    answer: 'Atomicity: All operations in a transaction succeed completely or none do (All-or-Nothing via WAL/rollback).\nConsistency: Database transitions strictly from one valid state to another, satisfying all schema constraints.\nIsolation: Concurrent transactions execute without interfering with one another (serializability levels).\nDurability: Once committed, state changes survive system crashes and power failures.',
    status: 'Mastered'
  },
  {
    id: 'fc-4',
    subject: 'DBMS',
    question: 'Why do relational databases use B+ Trees instead of Binary Search Trees for indexes?',
    answer: 'B+ Trees have high fan-out (order 100+), drastically reducing tree height to 3-4 levels. This minimizes expensive disk/SSD block I/O operations. In addition, all actual data pointers reside solely in leaf nodes, which are sequentially linked in a doubly linked list, enabling lightning-fast range queries (e.g. BETWEEN 10 AND 50) without traversing root nodes.',
    status: 'Review'
  },
  {
    id: 'fc-5',
    subject: 'CN',
    question: 'Explain the TCP 3-Way Handshake step by step.',
    answer: 'Step 1 (SYN): Client sends SYN packet with a random initial sequence number X (ISN) to server.\nStep 2 (SYN-ACK): Server responds with SYN bit set, its own ISN Y, and ACK number = X + 1.\nStep 3 (ACK): Client acknowledges with ACK packet having sequence number X + 1 and ACK number = Y + 1. Connection is now ESTABLISHED.',
    status: 'Mastered'
  },
  {
    id: 'fc-6',
    subject: 'OOP',
    question: 'Explain the difference between Overloading (Compile-time) and Overriding (Runtime Polymorphism).',
    answer: 'Overloading (Compile-time / Early Binding): Same method name in the same class with different parameters (count or types). Resolved by compiler based on method signature.\nOverriding (Runtime / Late Binding): Subclass provides a specific implementation of a virtual method declared in its parent class. Resolved at runtime using the Virtual Method Table (vtable) based on actual object type.',
    status: 'Review'
  }
];

export const INITIAL_SUBJECTS: SubjectAttendance[] = [
  { id: 'sub-1', code: 'CS-401', name: 'Operating Systems & Concurrency', credits: 4, totalClasses: 36, attendedClasses: 30, targetPercentage: 75, examDate: '2026-11-20' },
  { id: 'sub-2', code: 'CS-402', name: 'Database Management Systems', credits: 4, totalClasses: 32, attendedClasses: 27, targetPercentage: 75, examDate: '2026-11-24' },
  { id: 'sub-3', code: 'CS-403', name: 'Design & Analysis of Algorithms', credits: 4, totalClasses: 40, attendedClasses: 35, targetPercentage: 75, examDate: '2026-11-28' },
  { id: 'sub-4', code: 'CS-404', name: 'Computer Networks & Protocols', credits: 3, totalClasses: 30, attendedClasses: 21, targetPercentage: 75, examDate: '2026-12-02' }
];
