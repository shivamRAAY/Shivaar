import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import confetti from 'canvas-confetti';
import { 
  UserProfile, 
  TaskItem, 
  DsaProblem, 
  RoadmapMonth, 
  ProjectItem, 
  CareerApplication, 
  CoreCsFlashcard, 
  SubjectAttendance, 
  AppTab, 
  FocusSession 
} from '../types';
import { StorageService, AppState, INITIAL_STATE } from '../services/storage';

interface AppContextType {
  state: AppState;
  currentTab: AppTab;
  focusSession: FocusSession;
  isInstallable: boolean;
  toastMessage: string | null;
  setCurrentTab: (tab: AppTab) => void;
  updateUser: (profile: Partial<UserProfile>) => void;
  completeOnboarding: (data: Partial<UserProfile>) => void;
  toggleTask: (id: string) => void;
  addTask: (task: Omit<TaskItem, 'id'>) => void;
  deleteTask: (id: string) => void;
  toggleDsaStatus: (id: string, status: 'Unsolved' | 'Revising' | 'Solved') => void;
  addDsaProblem: (problem: Omit<DsaProblem, 'id'>) => void;
  scheduleDsaTask: (problem: DsaProblem) => void;
  toggleRoadmapMonth: (monthNumber: number) => void;
  addProject: (project: Omit<ProjectItem, 'id'>) => void;
  scheduleProjectSprint: (project: ProjectItem) => void;
  addCareerApp: (app: Omit<CareerApplication, 'id'>) => void;
  updateCareerAppStatus: (id: string, status: CareerApplication['status']) => void;
  scheduleCareerPrepTask: (app: CareerApplication) => void;
  toggleFlashcardStatus: (id: string, status: 'New' | 'Mastered' | 'Review') => void;
  addSubject: (subject: Omit<SubjectAttendance, 'id'>) => void;
  updateAttendance: (id: string, attended: number, total: number) => void;
  scheduleExamPrepTask: (subject: SubjectAttendance) => void;
  startFocusSession: (minutes: number) => void;
  pauseFocusSession: () => void;
  resumeFocusSession: () => void;
  resetFocusSession: () => void;
  toggleTheme: () => void;
  triggerInstall: () => Promise<void>;
  showToast: (msg: string) => void;
  exportBackup: () => void;
  importBackup: (file: File) => Promise<void>;
  resetAllData: () => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export const AppProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const storage = StorageService.getInstance();
  const [state, setState] = useState<AppState>(() => storage.loadState());
  const [currentTab, setCurrentTab] = useState<AppTab>('today');
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [installPrompt, setInstallPrompt] = useState<any>(null);
  const [isInstallable, setIsInstallable] = useState(false);

  // Focus Session Pomodoro State
  const [focusSession, setFocusSession] = useState<FocusSession>({
    isActive: false,
    mode: '25/5',
    secondsLeft: 25 * 60,
    totalSeconds: 25 * 60,
    isPaused: false,
    completedSessionsToday: 0
  });

  // Save to localStorage on state changes
  useEffect(() => {
    storage.saveState(state);
  }, [state]);

  // Apply dark/light theme class to documentElement
  useEffect(() => {
    if (state.theme === 'light') {
      document.documentElement.classList.remove('dark');
      document.documentElement.classList.add('light');
    } else {
      document.documentElement.classList.remove('light');
      document.documentElement.classList.add('dark');
    }
  }, [state.theme]);

  // PWA beforeinstallprompt handler
  useEffect(() => {
    const handleBeforeInstall = (e: Event) => {
      e.preventDefault();
      setInstallPrompt(e);
      setIsInstallable(true);
    };

    window.addEventListener('beforeinstallprompt', handleBeforeInstall);

    // Register Service Worker
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.register('./sw.js').catch((err) => {
        console.warn('Service worker registration failed:', err);
      });
    }

    return () => {
      window.removeEventListener('beforeinstallprompt', handleBeforeInstall);
    };
  }, []);

  // Pomodoro countdown timer
  useEffect(() => {
    let interval: any = null;
    if (focusSession.isActive && !focusSession.isPaused && focusSession.secondsLeft > 0) {
      interval = setInterval(() => {
        setFocusSession((prev) => ({
          ...prev,
          secondsLeft: prev.secondsLeft - 1
        }));
      }, 1000);
    } else if (focusSession.isActive && focusSession.secondsLeft === 0) {
      // Session finished celebration
      confetti({ particleCount: 75, spread: 70, origin: { y: 0.6 } });
      if ('vibrate' in navigator) {
        navigator.vibrate([200, 100, 200]);
      }
      showToast('🎉 Focus Sprint Completed! Take a well-earned break.');
      setFocusSession((prev) => ({
        ...prev,
        isActive: false,
        completedSessionsToday: prev.completedSessionsToday + 1
      }));
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [focusSession.isActive, focusSession.isPaused, focusSession.secondsLeft]);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage((cur) => (cur === msg ? null : cur));
    }, 3500);
  };

  const updateUser = (profile: Partial<UserProfile>) => {
    setState((prev) => ({
      ...prev,
      user: { ...prev.user, ...profile }
    }));
  };

  const completeOnboarding = (data: Partial<UserProfile>) => {
    setState((prev) => ({
      ...prev,
      user: {
        ...prev.user,
        ...data,
        isOnboarded: true
      },
      isFirstLaunch: false
    }));
    confetti({ particleCount: 100, spread: 80, origin: { y: 0.5 } });
    showToast(`Welcome to Shivaar, ${data.name || 'Engineer'}! Roadmap initialized.`);
  };

  const toggleTask = (id: string) => {
    setState((prev) => {
      const updated = prev.tasks.map((task) => {
        if (task.id === id) {
          const nextState = !task.isCompleted;
          if (nextState) {
            confetti({ particleCount: 40, spread: 60, origin: { y: 0.7 } });
            if ('vibrate' in navigator) navigator.vibrate(50);
          }
          return {
            ...task,
            isCompleted: nextState,
            completedAt: nextState ? Date.now() : undefined
          };
        }
        return task;
      });
      return { ...prev, tasks: updated };
    });
  };

  const addTask = (task: Omit<TaskItem, 'id'>) => {
    const newTask: TaskItem = {
      ...task,
      id: 'task-' + Date.now()
    };
    setState((prev) => ({
      ...prev,
      tasks: [newTask, ...prev.tasks]
    }));
    showToast('Task added to Today planner');
  };

  const deleteTask = (id: string) => {
    setState((prev) => ({
      ...prev,
      tasks: prev.tasks.filter((t) => t.id !== id)
    }));
    showToast('Task removed');
  };

  const toggleDsaStatus = (id: string, status: 'Unsolved' | 'Revising' | 'Solved') => {
    setState((prev) => ({
      ...prev,
      dsaProblems: prev.dsaProblems.map((p) => (p.id === id ? { ...p, status } : p))
    }));
    if (status === 'Solved') {
      confetti({ particleCount: 50, spread: 70, origin: { y: 0.6 } });
      showToast('Problem marked Solved! 🔥');
    }
  };

  const addDsaProblem = (problem: Omit<DsaProblem, 'id'>) => {
    const newProblem: DsaProblem = {
      ...problem,
      id: 'dsa-' + Date.now()
    };
    setState((prev) => ({
      ...prev,
      dsaProblems: [newProblem, ...prev.dsaProblems]
    }));
    showToast('DSA Problem added to tracker');
  };

  const scheduleDsaTask = (problem: DsaProblem) => {
    const task: Omit<TaskItem, 'id'> = {
      title: `Practice: ${problem.title} (${problem.category})`,
      category: 'DSA',
      priority: problem.difficulty === 'Hard' ? 'HIGH' : 'MEDIUM',
      durationMinutes: problem.difficulty === 'Hard' ? 60 : 45,
      isCompleted: false,
      whyItMatters: `Master the ${problem.pattern} pattern for placement interview assessments.`,
      instructions: `1. Code solution on ${problem.platform}. 2. Analyze time & space complexity. 3. Test edge cases (null, empty, duplicates).`,
      definitionOfDone: `All test cases pass. Notes added to Shivaar DSA problem card.`,
      resourceName: `${problem.platform} — ${problem.title}`,
      resourceUrl: problem.url,
      scheduledDate: new Date().toISOString().split('T')[0]
    };
    addTask(task);
    setCurrentTab('today');
  };

  const toggleRoadmapMonth = (monthNumber: number) => {
    setState((prev) => ({
      ...prev,
      roadmapMonths: prev.roadmapMonths.map((m) =>
        m.monthNumber === monthNumber ? { ...m, isCompleted: !m.isCompleted } : m
      )
    }));
  };

  const addProject = (project: Omit<ProjectItem, 'id'>) => {
    const newProj: ProjectItem = {
      ...project,
      id: 'proj-' + Date.now()
    };
    setState((prev) => ({
      ...prev,
      projects: [newProj, ...prev.projects]
    }));
    showToast('Project added to Studio');
  };

  const scheduleProjectSprint = (project: ProjectItem) => {
    const task: Omit<TaskItem, 'id'> = {
      title: `Project Sprint: ${project.title}`,
      category: 'DEV',
      priority: 'HIGH',
      durationMinutes: 90,
      isCompleted: false,
      whyItMatters: project.recruiterHook,
      instructions: `Implement next milestone for ${project.title}. Update README and verify GitHub commits.`,
      definitionOfDone: 'Feature branch merged and deployed to live preview.',
      resourceName: 'GitHub Repository',
      resourceUrl: project.githubUrl,
      scheduledDate: new Date().toISOString().split('T')[0]
    };
    addTask(task);
    setCurrentTab('today');
  };

  const addCareerApp = (app: Omit<CareerApplication, 'id'>) => {
    const newApp: CareerApplication = {
      ...app,
      id: 'app-' + Date.now()
    };
    setState((prev) => ({
      ...prev,
      careerApps: [newApp, ...prev.careerApps]
    }));
    showToast(`Application for ${app.company} tracked`);
  };

  const updateCareerAppStatus = (id: string, status: CareerApplication['status']) => {
    setState((prev) => ({
      ...prev,
      careerApps: prev.careerApps.map((a) => (a.id === id ? { ...a, status } : a))
    }));
    if (status === 'Offer') {
      confetti({ particleCount: 150, spread: 100, origin: { y: 0.4 } });
      showToast('🎉 CONGRATULATIONS! Offer status achieved! Outstanding work!');
    } else {
      showToast(`Status updated to ${status}`);
    }
  };

  const scheduleCareerPrepTask = (app: CareerApplication) => {
    const task: Omit<TaskItem, 'id'> = {
      title: `Interview Prep: ${app.company} (${app.role})`,
      category: 'CS',
      priority: 'HIGH',
      durationMinutes: 60,
      isCompleted: false,
      whyItMatters: `Prepare company-specific interview patterns for ${app.company} before the deadline (${app.deadline}).`,
      instructions: `1. Review recent interview experiences on LeetCode Discuss / Glassdoor. 2. Practice Core CS questions. 3. Prepare 3 questions for the interviewer.`,
      definitionOfDone: `Resume reviewed and 1 mock interview completed.`,
      scheduledDate: new Date().toISOString().split('T')[0]
    };
    addTask(task);
    setCurrentTab('today');
  };

  const toggleFlashcardStatus = (id: string, status: 'New' | 'Mastered' | 'Review') => {
    setState((prev) => ({
      ...prev,
      flashcards: prev.flashcards.map((f) => (f.id === id ? { ...f, status } : f))
    }));
  };

  const addSubject = (subject: Omit<SubjectAttendance, 'id'>) => {
    const newSub: SubjectAttendance = {
      ...subject,
      id: 'sub-' + Date.now()
    };
    setState((prev) => ({
      ...prev,
      subjects: [newSub, ...prev.subjects]
    }));
    showToast(`Subject ${subject.code} added`);
  };

  const updateAttendance = (id: string, attended: number, total: number) => {
    setState((prev) => ({
      ...prev,
      subjects: prev.subjects.map((s) => (s.id === id ? { ...s, attendedClasses: attended, totalClasses: total } : s))
    }));
  };

  const scheduleExamPrepTask = (subject: SubjectAttendance) => {
    const task: Omit<TaskItem, 'id'> = {
      title: `Exam Revision: ${subject.name} (${subject.code})`,
      category: 'ACADEMICS',
      priority: 'HIGH',
      durationMinutes: 60,
      isCompleted: false,
      whyItMatters: `Target SGPA requirement for ${subject.credits} credit course. Exam on ${subject.examDate || 'upcoming date'}.`,
      instructions: `1. Solve previous year exam papers (PYQs). 2. Revise numerical questions and theorems.`,
      definitionOfDone: `2 PYQs solved under timed condition.`,
      scheduledDate: new Date().toISOString().split('T')[0]
    };
    addTask(task);
    setCurrentTab('today');
  };

  const startFocusSession = (minutes: number) => {
    setFocusSession({
      isActive: true,
      mode: minutes >= 50 ? '50/10' : '25/5',
      secondsLeft: minutes * 60,
      totalSeconds: minutes * 60,
      isPaused: false,
      completedSessionsToday: focusSession.completedSessionsToday
    });
    showToast(`Focus sprint started: ${minutes} minutes. Zero distractions.`);
  };

  const pauseFocusSession = () => {
    setFocusSession((prev) => ({ ...prev, isPaused: true }));
  };

  const resumeFocusSession = () => {
    setFocusSession((prev) => ({ ...prev, isPaused: false }));
  };

  const resetFocusSession = () => {
    setFocusSession((prev) => ({
      ...prev,
      isActive: false,
      isPaused: false,
      secondsLeft: prev.totalSeconds
    }));
  };

  const toggleTheme = () => {
    setState((prev) => ({
      ...prev,
      theme: prev.theme === 'dark' ? 'light' : 'dark'
    }));
  };

  const triggerInstall = async () => {
    if (installPrompt) {
      installPrompt.prompt();
      const choice = await installPrompt.userChoice;
      if (choice.outcome === 'accepted') {
        showToast('Thank you! Shivaar installed to home screen.');
        setIsInstallable(false);
      }
      setInstallPrompt(null);
    } else {
      showToast('To install: tap Chrome menu (⋮) and choose "Add to Home screen"');
    }
  };

  const exportBackup = () => {
    storage.exportBackupJson(state);
    showToast('Backup JSON downloaded successfully');
  };

  const importBackup = async (file: File) => {
    try {
      const imported = await storage.importBackupJson(file);
      setState(imported);
      showToast('Backup restored successfully!');
    } catch (err: any) {
      showToast(`Restore failed: ${err.message}`);
    }
  };

  const resetAllData = () => {
    storage.clearAllData();
    setState(INITIAL_STATE);
    showToast('All progress reset to initial demo state');
  };

  return (
    <AppContext.Provider
      value={{
        state,
        currentTab,
        focusSession,
        isInstallable,
        toastMessage,
        setCurrentTab,
        updateUser,
        completeOnboarding,
        toggleTask,
        addTask,
        deleteTask,
        toggleDsaStatus,
        addDsaProblem,
        scheduleDsaTask,
        toggleRoadmapMonth,
        addProject,
        scheduleProjectSprint,
        addCareerApp,
        updateCareerAppStatus,
        scheduleCareerPrepTask,
        toggleFlashcardStatus,
        addSubject,
        updateAttendance,
        scheduleExamPrepTask,
        startFocusSession,
        pauseFocusSession,
        resumeFocusSession,
        resetFocusSession,
        toggleTheme,
        triggerInstall,
        showToast,
        exportBackup,
        importBackup,
        resetAllData
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};
