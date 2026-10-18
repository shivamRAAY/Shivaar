import React from 'react';
import { useApp } from '../../context/AppContext';
import { CheckSquare, Map, Code2, FolderGit2, LayoutGrid } from 'lucide-react';
import { AppTab } from '../../types';

export const BottomNav: React.FC<{ onOpenHub: () => void }> = ({ onOpenHub }) => {
  const { currentTab, setCurrentTab } = useApp();

  const isPrimaryTab = (tab: AppTab) => {
    return ['today', 'roadmap', 'dsa', 'projects'].includes(tab);
  };

  return (
    <nav className="shivaar-bottom-nav">
      <button
        onClick={() => setCurrentTab('today')}
        className={`nav-tab-btn ${currentTab === 'today' ? 'active' : ''}`}
        aria-label="Today Daily Planner"
      >
        <CheckSquare size={20} />
        <span>Today</span>
      </button>

      <button
        onClick={() => setCurrentTab('roadmap')}
        className={`nav-tab-btn ${currentTab === 'roadmap' ? 'active' : ''}`}
        aria-label="4-Year B.Tech Roadmap"
      >
        <Map size={20} />
        <span>Roadmap</span>
      </button>

      <button
        onClick={() => setCurrentTab('dsa')}
        className={`nav-tab-btn ${currentTab === 'dsa' ? 'active' : ''}`}
        aria-label="DSA Placement Tracker"
      >
        <Code2 size={20} />
        <span>DSA</span>
      </button>

      <button
        onClick={() => setCurrentTab('projects')}
        className={`nav-tab-btn ${currentTab === 'projects' ? 'active' : ''}`}
        aria-label="Project Studio"
      >
        <FolderGit2 size={20} />
        <span>Projects</span>
      </button>

      <button
        onClick={onOpenHub}
        className={`nav-tab-btn ${!isPrimaryTab(currentTab) ? 'active' : ''}`}
        aria-label="More Features Hub"
      >
        <LayoutGrid size={20} />
        <span>Hub</span>
      </button>
    </nav>
  );
};
