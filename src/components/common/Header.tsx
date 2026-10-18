import React from 'react';
import { useApp } from '../../context/AppContext';
import { Flame, Clock, Moon, Sun, Download, Menu, Sparkles } from 'lucide-react';
import { AppTab } from '../../types';

export const Header: React.FC<{ onOpenMenu: () => void }> = ({ onOpenMenu }) => {
  const { state, focusSession, toggleTheme, currentTab, setCurrentTab, isInstallable, triggerInstall } = useApp();

  const getTabTitle = (tab: AppTab): string => {
    switch (tab) {
      case 'today': return 'Daily Planner';
      case 'roadmap': return '4-Year Roadmap';
      case 'dsa': return 'DSA Placement';
      case 'projects': return 'Project Studio';
      case 'career': return 'Career Pipeline';
      case 'academics': return 'Academics & SGPA';
      case 'explore': return 'Roadmap Library';
      case 'analytics': return 'Performance';
      case 'settings': return 'Settings & Sync';
      default: return 'Shivaar';
    }
  };

  const formatTimer = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  return (
    <header className="shivaar-header">
      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
        <button
          onClick={onOpenMenu}
          aria-label="Open Navigation Hub"
          style={{
            background: 'none',
            border: 'none',
            color: 'var(--text-primary)',
            display: 'flex',
            alignItems: 'center',
            cursor: 'pointer',
            padding: '6px',
            borderRadius: '8px'
          }}
        >
          <Menu size={22} />
        </button>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span style={{ fontWeight: 800, fontSize: '16px', letterSpacing: '0.5px', background: 'linear-gradient(135deg, #6366F1, #38BDF8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
              SHIVAAR
            </span>
            <span style={{ fontSize: '10px', padding: '2px 6px', borderRadius: '4px', background: 'var(--accent-indigo-glow)', color: 'var(--accent-indigo)', fontWeight: 600 }}>
              B.Tech OS
            </span>
          </div>
          <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
            {getTabTitle(currentTab)}
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
        {/* Streak Pill */}
        <div
          title="Current Study Streak"
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '4px',
            background: 'rgba(245, 158, 11, 0.15)',
            border: '1px solid rgba(245, 158, 11, 0.3)',
            color: '#F59E0B',
            padding: '4px 8px',
            borderRadius: '20px',
            fontSize: '12px',
            fontWeight: 700
          }}
        >
          <Flame size={14} fill="#F59E0B" />
          <span>{state.user.streakDays}d</span>
        </div>

        {/* Focus Timer Pill */}
        {focusSession.isActive && (
          <div
            onClick={() => setCurrentTab('today')}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              background: 'rgba(124, 58, 237, 0.15)',
              border: '1px solid rgba(124, 58, 237, 0.4)',
              color: '#A78BFA',
              padding: '4px 8px',
              borderRadius: '20px',
              fontSize: '11px',
              fontWeight: 600,
              cursor: 'pointer'
            }}
          >
            <Clock size={13} className="animate-spin" />
            <span>{formatTimer(focusSession.secondsLeft)}</span>
          </div>
        )}

        {/* Install Button (if PWA installable on Chrome) */}
        {isInstallable && (
          <button
            onClick={triggerInstall}
            title="Install Shivaar on Phone"
            style={{
              background: 'linear-gradient(135deg, #10B981, #06B6D4)',
              border: 'none',
              color: '#fff',
              padding: '5px 10px',
              borderRadius: '20px',
              fontSize: '11px',
              fontWeight: 700,
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              cursor: 'pointer'
            }}
          >
            <Download size={13} />
            <span>Install</span>
          </button>
        )}

        {/* Theme Toggle */}
        <button
          onClick={toggleTheme}
          aria-label="Toggle Theme"
          style={{
            background: 'var(--bg-elevated)',
            border: '1px solid var(--border-subtle)',
            color: 'var(--text-secondary)',
            width: '32px',
            height: '32px',
            borderRadius: '10px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer'
          }}
        >
          {state.theme === 'dark' ? <Sun size={15} /> : <Moon size={15} />}
        </button>
      </div>
    </header>
  );
};
