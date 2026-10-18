import React from 'react';
import { useApp } from '../../context/AppContext';
import { 
  GraduationCap, 
  Briefcase, 
  Compass, 
  BarChart3, 
  Settings, 
  Sparkles, 
  X, 
  CheckSquare, 
  Map, 
  Code2, 
  FolderGit2,
  ChevronRight
} from 'lucide-react';
import { AppTab } from '../../types';

export const HubModal: React.FC<{ isOpen: boolean; onClose: () => void; onReplaySplash: () => void }> = ({
  isOpen,
  onClose,
  onReplaySplash
}) => {
  const { currentTab, setCurrentTab, state } = useApp();

  if (!isOpen) return null;

  const navigateTo = (tab: AppTab) => {
    setCurrentTab(tab);
    onClose();
  };

  const hubItems: { tab: AppTab; title: string; subtitle: string; icon: any; color: string; badge?: string }[] = [
    {
      tab: 'today',
      title: 'Daily Planner',
      subtitle: 'Today tasks, focus pomodoro, energy levels',
      icon: CheckSquare,
      color: '#4F46E5'
    },
    {
      tab: 'roadmap',
      title: '4-Year B.Tech Roadmap',
      subtitle: 'Semesters 1-8, 48-month milestones & gates',
      icon: Map,
      color: '#7C3AED'
    },
    {
      tab: 'dsa',
      title: 'DSA Placement Engine',
      subtitle: 'Striver & NeetCode tracker, pattern categorization',
      icon: Code2,
      color: '#F59E0B',
      badge: `${state.dsaProblems.filter(p => p.status === 'Solved').length} Solved`
    },
    {
      tab: 'projects',
      title: 'Project Studio',
      subtitle: '4-tier portfolio framework & sprint tasks',
      icon: FolderGit2,
      color: '#10B981',
      badge: `${state.projects.length} Projs`
    },
    {
      tab: 'academics',
      title: 'Academics & SGPA Simulator',
      subtitle: 'Attendance tracker, "Can I Bunk?" calculator & GPA target',
      icon: GraduationCap,
      color: '#3B82F6'
    },
    {
      tab: 'career',
      title: 'Career Hub & Interview Prep',
      subtitle: 'Job/Internship Kanban & Core CS flashcards',
      icon: Briefcase,
      color: '#EC4899',
      badge: `${state.careerApps.length} Apps`
    },
    {
      tab: 'explore',
      title: 'Explore Roadmaps Library',
      subtitle: 'Full-Stack, AI/ML, DevOps, Systems, Cyber tracks',
      icon: Compass,
      color: '#06B6D4'
    },
    {
      tab: 'analytics',
      title: 'Analytics & Consistency',
      subtitle: 'Weekly completion trends, time breakdown & heatmaps',
      icon: BarChart3,
      color: '#8B5CF6'
    },
    {
      tab: 'settings',
      title: 'Settings, Sync & Deployment',
      subtitle: 'Profile, free hosting guide, JSON backup & restore',
      icon: Settings,
      color: '#64748B'
    }
  ];

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-sheet" onClick={(e) => e.stopPropagation()}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingBottom: '4px' }}>
          <div>
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Shivaar System Hub</h3>
            <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              {state.user.name} • Year {state.user.currentYear} Sem {state.user.currentSemester} • {state.user.targetCareerTrack}
            </p>
          </div>
          <button
            onClick={onClose}
            aria-label="Close Hub"
            style={{
              background: 'var(--bg-elevated)',
              border: '1px solid var(--border-subtle)',
              color: 'var(--text-secondary)',
              width: '32px',
              height: '32px',
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer'
            }}
          >
            <X size={16} />
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '4px' }}>
          {hubItems.map((item) => {
            const Icon = item.icon;
            const isActive = currentTab === item.tab;
            return (
              <div
                key={item.tab}
                onClick={() => navigateTo(item.tab)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '12px 14px',
                  borderRadius: '14px',
                  background: isActive ? 'var(--accent-indigo-glow)' : 'var(--bg-card)',
                  border: `1px solid ${isActive ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
                  cursor: 'pointer',
                  transition: 'transform 0.1s ease'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <div
                    style={{
                      width: '38px',
                      height: '38px',
                      borderRadius: '10px',
                      background: `rgba(${item.color === '#4F46E5' ? '79, 70, 229' : '124, 58, 237'}, 0.15)`,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      color: item.color
                    }}
                  >
                    <Icon size={20} />
                  </div>
                  <div>
                    <div style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-primary)' }}>
                      {item.title}
                    </div>
                    <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                      {item.subtitle}
                    </div>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  {item.badge && (
                    <span
                      style={{
                        fontSize: '10px',
                        padding: '3px 8px',
                        borderRadius: '12px',
                        background: 'var(--bg-elevated)',
                        color: 'var(--text-secondary)',
                        fontWeight: 600
                      }}
                    >
                      {item.badge}
                    </span>
                  )}
                  <ChevronRight size={16} color="var(--text-muted)" />
                </div>
              </div>
            );
          })}
        </div>

        {/* Replay Opening Animation Action */}
        <button
          onClick={() => {
            onClose();
            onReplaySplash();
          }}
          className="btn-secondary"
          style={{ width: '100%', marginTop: '6px' }}
        >
          <Sparkles size={16} color="#F59E0B" />
          <span>Replay Opening Splash Animation</span>
        </button>
      </div>
    </div>
  );
};
