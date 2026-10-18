import React from 'react';
import { useApp } from '../../context/AppContext';
import { 
  Flame, 
  Clock, 
  CheckCircle2, 
  TrendingUp, 
  Award, 
  BarChart3, 
  Zap, 
  Calendar 
} from 'lucide-react';

export const AnalyticsView: React.FC = () => {
  const { state } = useApp();

  const totalTasks = state.tasks.length;
  const completedTasks = state.tasks.filter((t) => t.isCompleted).length;
  const totalDsaSolved = state.dsaProblems.filter((p) => p.status === 'Solved').length;
  const totalProjects = state.projects.length;

  const totalMinutes = state.tasks.reduce((sum, t) => sum + (t.isCompleted ? t.durationMinutes : 0), 0);
  const totalHours = (totalMinutes / 60).toFixed(1);

  // Time by category
  const dsaMins = state.tasks.filter((t) => t.category === 'DSA' && t.isCompleted).reduce((s, t) => s + t.durationMinutes, 0);
  const devMins = state.tasks.filter((t) => t.category === 'DEV' && t.isCompleted).reduce((s, t) => s + t.durationMinutes, 0);
  const csMins = state.tasks.filter((t) => t.category === 'CS' && t.isCompleted).reduce((s, t) => s + t.durationMinutes, 0);
  const acadMins = state.tasks.filter((t) => t.category === 'ACADEMICS' && t.isCompleted).reduce((s, t) => s + t.durationMinutes, 0);
  const coreMins = state.tasks.filter((t) => t.category === 'CORE' && t.isCompleted).reduce((s, t) => s + t.durationMinutes, 0);

  const totalCompletedMins = dsaMins + devMins + csMins + acadMins + coreMins || 1;

  // Mock days of week completion
  const weekDays = [
    { day: 'Mon', count: 4 },
    { day: 'Tue', count: 6 },
    { day: 'Wed', count: 5 },
    { day: 'Thu', count: 7 },
    { day: 'Fri', count: 4 },
    { day: 'Sat', count: 8 },
    { day: 'Sun', count: completedTasks || 5 }
  ];

  const maxDayCount = Math.max(...weekDays.map((d) => d.count), 8);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Header */}
      <div>
        <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
          Consistency & Analytics
        </h2>
        <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
          Weekly Productivity Trends, Time Distribution & Streak History
        </p>
      </div>

      {/* Top Metrics Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
        <div className="shivaar-card" style={{ padding: '14px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#F59E0B' }}>
            <Flame size={16} />
            <span style={{ fontSize: '11px', fontWeight: 700 }}>STUDY STREAK</span>
          </div>
          <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)', marginTop: '4px' }}>
            {state.user.streakDays} <span style={{ fontSize: '14px', color: 'var(--text-muted)', fontWeight: 500 }}>Days</span>
          </div>
          <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '2px' }}>
            Personal best: {state.user.streakDays + 14} days
          </div>
        </div>

        <div className="shivaar-card" style={{ padding: '14px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#38BDF8' }}>
            <Clock size={16} />
            <span style={{ fontSize: '11px', fontWeight: 700 }}>TIME LOGGED</span>
          </div>
          <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)', marginTop: '4px' }}>
            {totalHours} <span style={{ fontSize: '14px', color: 'var(--text-muted)', fontWeight: 500 }}>Hours</span>
          </div>
          <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '2px' }}>
            {completedTasks} completed sessions
          </div>
        </div>
      </div>

      {/* Weekly Activity Bar Chart */}
      <div className="shivaar-card" style={{ padding: '16px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
          <div style={{ fontSize: '13px', fontWeight: 700 }}>Weekly Engineering Activity</div>
          <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Tasks Cleared / Day</span>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', height: '120px', paddingBottom: '8px', borderBottom: '1px solid var(--border-subtle)' }}>
          {weekDays.map((w, idx) => {
            const barHeight = Math.round((w.count / maxDayCount) * 100);
            const isToday = idx === 6;
            return (
              <div key={w.day} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px', flex: 1 }}>
                <span style={{ fontSize: '10px', color: isToday ? 'var(--accent-indigo)' : 'var(--text-muted)', fontWeight: isToday ? 700 : 500 }}>
                  {w.count}
                </span>
                <div
                  style={{
                    width: '18px',
                    height: `${barHeight}px`,
                    background: isToday ? 'linear-gradient(180deg, #6366F1, #38BDF8)' : 'var(--bg-elevated)',
                    borderRadius: '4px',
                    transition: 'height 0.3s ease'
                  }}
                />
                <span style={{ fontSize: '10px', color: isToday ? 'var(--accent-indigo)' : 'var(--text-muted)', fontWeight: isToday ? 800 : 500 }}>
                  {w.day}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* Category Time Distribution */}
      <div className="shivaar-card" style={{ padding: '16px' }}>
        <div style={{ fontSize: '13px', fontWeight: 700, marginBottom: '12px' }}>
          Effort Distribution by Domain
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', marginBottom: '4px' }}>
              <span style={{ color: '#F59E0B', fontWeight: 600 }}>DSA & Problem Solving</span>
              <span style={{ color: 'var(--text-muted)' }}>{Math.round((dsaMins / totalCompletedMins) * 100)}%</span>
            </div>
            <div style={{ height: '6px', background: 'var(--bg-elevated)', borderRadius: '3px', overflow: 'hidden' }}>
              <div style={{ width: `${(dsaMins / totalCompletedMins) * 100}%`, height: '100%', background: '#F59E0B' }} />
            </div>
          </div>

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', marginBottom: '4px' }}>
              <span style={{ color: '#10B981', fontWeight: 600 }}>Development & Projects</span>
              <span style={{ color: 'var(--text-muted)' }}>{Math.round((devMins / totalCompletedMins) * 100)}%</span>
            </div>
            <div style={{ height: '6px', background: 'var(--bg-elevated)', borderRadius: '3px', overflow: 'hidden' }}>
              <div style={{ width: `${(devMins / totalCompletedMins) * 100}%`, height: '100%', background: '#10B981' }} />
            </div>
          </div>

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', marginBottom: '4px' }}>
              <span style={{ color: '#06B6D4', fontWeight: 600 }}>Core CS Theory (OS, DBMS, CN)</span>
              <span style={{ color: 'var(--text-muted)' }}>{Math.round((csMins / totalCompletedMins) * 100)}%</span>
            </div>
            <div style={{ height: '6px', background: 'var(--bg-elevated)', borderRadius: '3px', overflow: 'hidden' }}>
              <div style={{ width: `${(csMins / totalCompletedMins) * 100}%`, height: '100%', background: '#06B6D4' }} />
            </div>
          </div>

          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', marginBottom: '4px' }}>
              <span style={{ color: '#3B82F6', fontWeight: 600 }}>College Academics & SGPA</span>
              <span style={{ color: 'var(--text-muted)' }}>{Math.round((acadMins / totalCompletedMins) * 100)}%</span>
            </div>
            <div style={{ height: '6px', background: 'var(--bg-elevated)', borderRadius: '3px', overflow: 'hidden' }}>
              <div style={{ width: `${(acadMins / totalCompletedMins) * 100}%`, height: '100%', background: '#3B82F6' }} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
