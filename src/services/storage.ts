import { 
  UserProfile, 
  TaskItem, 
  DsaProblem, 
  RoadmapMonth, 
  ProjectItem, 
  CareerApplication, 
  CoreCsFlashcard, 
  SubjectAttendance 
} from '../types';
import { 
  INITIAL_TASKS, 
  INITIAL_DSA_PROBLEMS, 
  INITIAL_ROADMAP_MONTHS, 
  INITIAL_PROJECTS, 
  INITIAL_CAREER_APPS, 
  INITIAL_CORE_CS_FLASHCARDS, 
  INITIAL_SUBJECTS 
} from '../data/initialData';

export interface AppState {
  user: UserProfile;
  tasks: TaskItem[];
  dsaProblems: DsaProblem[];
  roadmapMonths: RoadmapMonth[];
  projects: ProjectItem[];
  careerApps: CareerApplication[];
  flashcards: CoreCsFlashcard[];
  subjects: SubjectAttendance[];
  theme: 'dark' | 'light';
  isFirstLaunch: boolean;
}

const STORAGE_KEY = 'shivaar_app_state_v2';

export const DEFAULT_USER: UserProfile = {
  id: 'usr-' + Math.random().toString(36).substring(2, 9),
  name: 'B.Tech Explorer',
  college: 'National Institute of Technology',
  tier: 'Tier 2',
  currentYear: 2,
  currentSemester: 3,
  targetCareerTrack: 'Full-Stack Engineer',
  dsaLevel: 'Intermediate',
  weeklyBudgetHours: 25,
  cgpaTarget: 8.5,
  streakDays: 7,
  lastActiveDate: new Date().toISOString().split('T')[0],
  isOnboarded: false,
  cloudSyncEnabled: false
};

export const INITIAL_STATE: AppState = {
  user: DEFAULT_USER,
  tasks: INITIAL_TASKS,
  dsaProblems: INITIAL_DSA_PROBLEMS,
  roadmapMonths: INITIAL_ROADMAP_MONTHS,
  projects: INITIAL_PROJECTS,
  careerApps: INITIAL_CAREER_APPS,
  flashcards: INITIAL_CORE_CS_FLASHCARDS,
  subjects: INITIAL_SUBJECTS,
  theme: 'dark',
  isFirstLaunch: true
};

export class StorageService {
  private static instance: StorageService;

  public static getInstance(): StorageService {
    if (!StorageService.instance) {
      StorageService.instance = new StorageService();
    }
    return StorageService.instance;
  }

  public loadState(): AppState {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (!stored) {
        return INITIAL_STATE;
      }
      const parsed = JSON.parse(stored);
      // Merge with default schema to prevent missing key regressions
      return {
        ...INITIAL_STATE,
        ...parsed,
        user: { ...INITIAL_STATE.user, ...(parsed.user || {}) },
        tasks: Array.isArray(parsed.tasks) ? parsed.tasks : INITIAL_TASKS,
        dsaProblems: Array.isArray(parsed.dsaProblems) ? parsed.dsaProblems : INITIAL_DSA_PROBLEMS,
        roadmapMonths: Array.isArray(parsed.roadmapMonths) ? parsed.roadmapMonths : INITIAL_ROADMAP_MONTHS,
        projects: Array.isArray(parsed.projects) ? parsed.projects : INITIAL_PROJECTS,
        careerApps: Array.isArray(parsed.careerApps) ? parsed.careerApps : INITIAL_CAREER_APPS,
        flashcards: Array.isArray(parsed.flashcards) ? parsed.flashcards : INITIAL_CORE_CS_FLASHCARDS,
        subjects: Array.isArray(parsed.subjects) ? parsed.subjects : INITIAL_SUBJECTS
      };
    } catch (e) {
      console.error('Failed to load state from localStorage, using initial state:', e);
      return INITIAL_STATE;
    }
  }

  public saveState(state: AppState): void {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (e) {
      console.error('Failed to save state to localStorage:', e);
    }
  }

  public exportBackupJson(state: AppState): void {
    const dataStr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(state, null, 2));
    const downloadAnchor = document.createElement('a');
    downloadAnchor.setAttribute('href', dataStr);
    const date = new Date().toISOString().split('T')[0];
    downloadAnchor.setAttribute('download', `shivaar-backup-${date}.json`);
    document.body.appendChild(downloadAnchor);
    downloadAnchor.click();
    downloadAnchor.remove();
  }

  public async importBackupJson(file: File): Promise<AppState> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (event) => {
        try {
          const content = event.target?.result as string;
          const parsed = JSON.parse(content);
          if (!parsed.user || !parsed.tasks) {
            throw new Error('Invalid Shivaar backup format.');
          }
          const merged: AppState = {
            ...INITIAL_STATE,
            ...parsed,
            user: { ...INITIAL_STATE.user, ...(parsed.user || {}) }
          };
          this.saveState(merged);
          resolve(merged);
        } catch (err) {
          reject(err);
        }
      };
      reader.onerror = (err) => reject(err);
      reader.readAsText(file);
    });
  }

  public clearAllData(): void {
    localStorage.removeItem(STORAGE_KEY);
  }
}
