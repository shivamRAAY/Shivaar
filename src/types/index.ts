export type TaskCategory = 'CORE' | 'DSA' | 'DEV' | 'CS' | 'ACADEMICS';
export type TaskPriority = 'HIGH' | 'MEDIUM' | 'LOW';

export interface UserProfile {
  id: string;
  name: string;
  college: string;
  tier: 'Tier 1' | 'Tier 2' | 'Tier 3';
  currentYear: number;
  currentSemester: number;
  targetCareerTrack: string;
  dsaLevel: 'Beginner' | 'Intermediate' | 'Advanced';
  weeklyBudgetHours: number;
  cgpaTarget: number;
  streakDays: number;
  lastActiveDate: string;
  isOnboarded: boolean;
  cloudSyncEnabled: boolean;
  firebaseProjectId?: string;
  geminiApiKey?: string;
}

export interface TaskItem {
  id: string;
  title: string;
  category: TaskCategory;
  priority: TaskPriority;
  durationMinutes: number;
  isCompleted: boolean;
  whyItMatters: string;
  instructions?: string;
  definitionOfDone?: string;
  resourceName?: string;
  resourceUrl?: string;
  scheduledDate: string;
  completedAt?: number;
}

export interface DsaProblem {
  id: string;
  title: string;
  category: string;
  difficulty: 'Easy' | 'Medium' | 'Hard';
  status: 'Unsolved' | 'Revising' | 'Solved';
  platform: 'LeetCode' | 'GFG' | 'Striver SDE';
  url: string;
  pattern: string;
  notes?: string;
}

export interface RoadmapMonth {
  monthNumber: number;
  semester: number;
  title: string;
  summary: string;
  keyTopics: string[];
  milestoneProject: string;
  verificationGate: string;
  isCompleted: boolean;
}

export interface CareerTrackPhase {
  phaseNumber: number;
  title: string;
  skills: string[];
  resources: { name: string; url: string; free: boolean }[];
}

export interface PublicRoadmapTrack {
  id: string;
  title: string;
  subtitle: string;
  durationMonths: number;
  level: 'Beginner' | 'Intermediate' | 'Advanced';
  averageCTC: string;
  icon: string;
  tags: string[];
  phases: CareerTrackPhase[];
}

export interface ProjectItem {
  id: string;
  title: string;
  tier: 'Foundation' | 'Intermediate' | 'Advanced' | 'Capstone';
  tagline: string;
  description: string;
  techStack: string[];
  githubUrl: string;
  liveUrl?: string;
  recruiterHook: string;
  status: 'Idea' | 'In Progress' | 'Shipped';
  readinessScore: number;
}

export interface CareerApplication {
  id: string;
  company: string;
  role: string;
  status: 'Wishlist' | 'Applied' | 'OA' | 'Technical' | 'HR' | 'Offer';
  deadline: string;
  stipendOrPackage: string;
  notes?: string;
}

export interface CoreCsFlashcard {
  id: string;
  subject: 'OS' | 'DBMS' | 'CN' | 'OOP';
  question: string;
  answer: string;
  status: 'New' | 'Mastered' | 'Review';
}

export interface SubjectAttendance {
  id: string;
  code: string;
  name: string;
  credits: number;
  totalClasses: number;
  attendedClasses: number;
  targetPercentage: number;
  examDate?: string;
}

export interface FocusSession {
  isActive: boolean;
  mode: '25/5' | '50/10';
  secondsLeft: number;
  totalSeconds: number;
  isPaused: boolean;
  completedSessionsToday: number;
}

export type AppTab = 
  | 'today' 
  | 'roadmap' 
  | 'dsa' 
  | 'projects' 
  | 'career' 
  | 'academics' 
  | 'explore' 
  | 'analytics' 
  | 'settings';
