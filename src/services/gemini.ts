export interface AiMentorResponse {
  content: string;
  isMockFallback: boolean;
  modelUsed: string;
}

const EXPERT_MENTOR_KNOWLEDGE_BASE: Record<string, string> = {
  dsa: `### Shivaar DSA Master Strategy for Placement Success
1. **The 80/20 Rule of DSA**: 85% of interview questions come from 7 core patterns:
   - Two Pointers & Sliding Window (Arrays/Strings)
   - Fast & Slow Pointers (Linked Lists)
   - Level-Order & DFS Traversal (Binary Trees)
   - Breadth-First & Depth-First Search with Visited Sets (Graphs)
   - Top-Down Memoization vs Bottom-Up Tabulation (Dynamic Programming)
   - Modified Binary Search (Sorted / Rotated Arrays)
   - Monotonic Stack & Min/Max Heaps
2. **Daily Discipline**: Solve 2 problems daily rather than 14 on Saturday. Active recall is essential.
3. **Mock Assessment**: Once a week, solve 2 Mediums with a 45-minute countdown without looking at hints.`,

  resume: `### Shivaar High-Impact SDE Resume Formula
1. **Google XYZ Formula**: Accomplished [X] as measured by [Y], by doing [Z].
   - *Weak*: "Built a chat app using WebSockets."
   - *High-Impact*: "Architected a real-time multiplayer collaborative canvas serving 500+ concurrent users with <45ms cursor synchronization latency using React 19, CRDTs, and WebSockets."
2. **Project Section Architecture**:
   - Each project must have: Tech Stack tags, Live Production URL, GitHub Repo, and 3 bullet points with quantifiable metrics.
   - Include at least 1 systems or distributed project (Raft, custom HTTP server, DB engine) alongside 1 modern full-stack web/app.`,

  roadmap: `### 4-Year B.Tech CSE Progression Blueprint
- **Year 1**: Solidify C/C++ memory mechanics, Git/GitHub, Linux CLI, and first 50 basic DSA problems. Maintain 8.5+ CGPA.
- **Year 2**: Advanced DSA (Trees/Graphs/DP), relational databases (DBMS Normalization & SQL), Full-Stack project, and first Open Source contribution.
- **Year 3**: Core CS subjects (OS, CN, System Design), 200+ LeetCode problems, participate in hackathons, apply to 30+ summer internships.
- **Year 4**: Placement OAs, super-tier campus drives, capstone project, cloud architecture (Docker/Kubernetes), and corporate transition.`
};

export class GeminiService {
  private static instance: GeminiService;

  public static getInstance(): GeminiService {
    if (!GeminiService.instance) {
      GeminiService.instance = new GeminiService();
    }
    return GeminiService.instance;
  }

  public async askMentor(prompt: string, userApiKey?: string): Promise<AiMentorResponse> {
    const trimmedKey = userApiKey?.trim();

    // If user provided a key, call live Gemini 2.5 Flash
    if (trimmedKey) {
      try {
        const url = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${encodeURIComponent(trimmedKey)}`;
        const body = {
          contents: [
            {
              role: 'user',
              parts: [
                {
                  text: `You are Shivaar AI Mentor, an elite Senior Software Engineer and career mentor for B.Tech CSE students.
Provide structured, concise, highly actionable engineering advice with practical code snippets, algorithmic time/space complexities, or interview strategies.

Student Query: ${prompt}`
                }
              ]
            }
          ]
        };

        const res = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        });

        if (!res.ok) {
          const errData = await res.json().catch(() => ({}));
          throw new Error(errData.error?.message || `API error ${res.status}: ${res.statusText}`);
        }

        const data = await res.json();
        const candidateText = data.candidates?.[0]?.content?.parts?.[0]?.text;
        if (candidateText) {
          return {
            content: candidateText,
            isMockFallback: false,
            modelUsed: 'gemini-2.5-flash (Live)'
          };
        }
      } catch (err: any) {
        console.warn('Gemini API call failed, falling back to curated expert guidance:', err.message);
        return {
          content: `⚠️ Note: Live Gemini API request encountered an issue (${err.message}). Showing curated Shivaar engineering guidance below:\n\n` + this.getCuratedFallback(prompt),
          isMockFallback: true,
          modelUsed: 'Shivaar Curated Knowledge Engine'
        };
      }
    }

    // Default zero-cost curated mentor guidance
    return {
      content: this.getCuratedFallback(prompt),
      isMockFallback: true,
      modelUsed: 'Shivaar Offline Knowledge Base (No API Key Required)'
    };
  }

  private getCuratedFallback(prompt: string): string {
    const lower = prompt.toLowerCase();
    if (lower.includes('dsa') || lower.includes('leetcode') || lower.includes('algorithm') || lower.includes('striver')) {
      return EXPERT_MENTOR_KNOWLEDGE_BASE.dsa;
    }
    if (lower.includes('resume') || lower.includes('project') || lower.includes('cv') || lower.includes('interview')) {
      return EXPERT_MENTOR_KNOWLEDGE_BASE.resume;
    }
    return EXPERT_MENTOR_KNOWLEDGE_BASE.roadmap;
  }
}
