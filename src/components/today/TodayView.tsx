import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  CheckCircle2, 
  Circle, 
  Plus, 
  Trash2, 
  ExternalLink, 
  ChevronDown, 
  ChevronUp, 
  Zap, 
  Coffee, 
  BatteryCharging, 
  Play, 
  Pause, 
  RotateCcw, 
  Clock, 
  Quote, 
  Sparkles,
  Calendar,
  X
} from 'lucide-react';
import { TaskCategory, TaskPriority, TaskItem } from '../../types';
import { MOTIVATIONAL_QUOTES } from '../../data/initialData';

export const TodayView: React.FC = () => {
  const { 
    state, 
    toggleTask, 
    addTask, 
    deleteTask, 
    focusSession, 
    startFocusSession, 
    pauseFocusSession, 
    resumeFocusSession, 
    resetFocusSession 
  } = useApp();

  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [expandedTaskId, setExpandedTaskId] = useState<string | null>(null);
  const [energyLevel, setEnergyLevel] = useState<'high' | 'balanced' | 'low'>('high');
  const [isQuickAddOpen, setIsQuickAddOpen] = useState(false);

  // Quick Add form state
  const [newTitle, setNewTitle] = useState('');
  const [newCategory, setNewCategory] = useState<TaskCategory>('DSA');
  const [newPriority, setNewPriority] = useState<TaskPriority>('HIGH');
  const [newDuration, setNewDuration] = useState(45);
  const [newWhy, setNewWhy] = useState('');
  const [newInstructions, setNewInstructions] = useState('');
  const [newDoD, setNewDoD] = useState('');

  const completedCount = state.tasks.filter((t) => t.isCompleted).length;
  const totalCount = state.tasks.length;
  const progressPercent = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0;
  const totalMinutes = state.tasks.reduce((sum, t) => sum + t.durationMinutes, 0);

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  };

  const todayStr = new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    month: 'short',
    day: 'numeric'
  });

  const randomQuote = MOTIVATIONAL_QUOTES[0];

  const filteredTasks = state.tasks.filter((task) => {
    if (selectedCategory === 'ALL') return true;
    return task.category === selectedCategory;
  });

  const handleCreateTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;

    addTask({
      title: newTitle.trim(),
      category: newCategory,
      priority: newPriority,
      durationMinutes: Number(newDuration),
      isCompleted: false,
      whyItMatters: newWhy.trim() || 'Crucial for semester syllabus mastery and placement readiness.',
      instructions: newInstructions.trim() || undefined,
      definitionOfDone: newDoD.trim() || 'Complete implementation with test verifications.',
      scheduledDate: new Date().toISOString().split('T')[0]
    });

    setNewTitle('');
    setNewWhy('');
    setNewInstructions('');
    setNewDoD('');
    setIsQuickAddOpen(false);
  };

  const formatTimer = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  return (
    <div style={{ display: 'flex', flexFlow: 'column', gap: '16px' }}>
      {/* Top Greeting & Date Header */}
      <div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '12px' }}>
          <Calendar size={13} />
          <span>{todayStr}</span>
          <span>•</span>
          <span style={{ color: 'var(--accent-indigo)', fontWeight: 600 }}>Semester {state.user.currentSemester}</span>
        </div>
        <h2 style={{ fontSize: '22px', fontWeight: 800, marginTop: '2px', color: 'var(--text-primary)' }}>
          {getGreeting()}, {state.user.name.split(' ')[0]}
        </h2>
      </div>

      {/* Quote Banner */}
      <div
        className="shivaar-card"
        style={{
          background: 'linear-gradient(135deg, rgba(30, 27, 75, 0.4) 0%, rgba(15, 23, 42, 0.4) 100%)',
          borderColor: 'rgba(99, 102, 241, 0.25)',
          padding: '14px'
        }}
      >
        <div style={{ display: 'flex', gap: '10px' }}>
          <Quote size={20} color="var(--accent-indigo)" style={{ flexShrink: 0, marginTop: '2px' }} />
          <div>
            <p style={{ fontSize: '13px', fontStyle: 'italic', color: 'var(--text-primary)', lineHeight: '1.4' }}>
              "{randomQuote.text}"
            </p>
            <p style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '4px' }}>
              — {randomQuote.author}
            </p>
          </div>
        </div>
      </div>

      {/* Energy Mode Selector */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
            Daily Energy Level
          </span>
          <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
            {energyLevel === 'high' ? 'Optimal for DSA & Systems' : energyLevel === 'balanced' ? 'Balanced Study & Projects' : 'Flashcards & Revision'}
          </span>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px' }}>
          <button
            onClick={() => setEnergyLevel('high')}
            className="btn-secondary"
            style={{
              padding: '8px 10px',
              fontSize: '11px',
              fontWeight: 600,
              borderRadius: '10px',
              borderColor: energyLevel === 'high' ? '#F59E0B' : 'var(--border-subtle)',
              background: energyLevel === 'high' ? 'rgba(245, 158, 11, 0.15)' : 'var(--bg-card)',
              color: energyLevel === 'high' ? '#F59E0B' : 'var(--text-secondary)'
            }}
          >
            <Zap size={14} />
            <span>High Peak</span>
          </button>

          <button
            onClick={() => setEnergyLevel('balanced')}
            className="btn-secondary"
            style={{
              padding: '8px 10px',
              fontSize: '11px',
              fontWeight: 600,
              borderRadius: '10px',
              borderColor: energyLevel === 'balanced' ? '#10B981' : 'var(--border-subtle)',
              background: energyLevel === 'balanced' ? 'rgba(16, 185, 129, 0.15)' : 'var(--bg-card)',
              color: energyLevel === 'balanced' ? '#10B981' : 'var(--text-secondary)'
            }}
          >
            <BatteryCharging size={14} />
            <span>Balanced</span>
          </button>

          <button
            onClick={() => setEnergyLevel('low')}
            className="btn-secondary"
            style={{
              padding: '8px 10px',
              fontSize: '11px',
              fontWeight: 600,
              borderRadius: '10px',
              borderColor: energyLevel === 'low' ? '#3B82F6' : 'var(--border-subtle)',
              background: energyLevel === 'low' ? 'rgba(59, 130, 246, 0.15)' : 'var(--bg-card)',
              color: energyLevel === 'low' ? '#60A5FA' : 'var(--text-secondary)'
            }}
          >
            <Coffee size={14} />
            <span>Low / Fatigue</span>
          </button>
        </div>
      </div>

      {/* Focus Sprint Pomodoro Card */}
      <div
        className="shivaar-card"
        style={{
          background: 'linear-gradient(135deg, rgba(30, 27, 75, 0.6) 0%, rgba(17, 24, 39, 0.8) 100%)',
          borderColor: focusSession.isActive ? 'var(--accent-indigo)' : 'var(--border-subtle)',
          padding: '16px'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div
              style={{
                width: '32px',
                height: '32px',
                borderRadius: '8px',
                background: 'var(--accent-indigo-glow)',
                color: 'var(--accent-indigo)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <Clock size={16} />
            </div>
            <div>
              <div style={{ fontSize: '13px', fontWeight: 700 }}>Engineering Focus Sprint</div>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                {focusSession.completedSessionsToday} sprints completed today
              </div>
            </div>
          </div>

          <div style={{ fontSize: '24px', fontWeight: 800, fontFamily: 'monospace', color: focusSession.isActive ? '#38BDF8' : 'var(--text-primary)' }}>
            {formatTimer(focusSession.secondsLeft)}
          </div>
        </div>

        <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
          {!focusSession.isActive ? (
            <>
              <button
                onClick={() => startFocusSession(25)}
                className="btn-primary"
                style={{ flex: 1, padding: '9px 12px', fontSize: '12px' }}
              >
                <Play size={14} />
                <span>25m Sprint</span>
              </button>
              <button
                onClick={() => startFocusSession(50)}
                className="btn-secondary"
                style={{ flex: 1, padding: '9px 12px', fontSize: '12px' }}
              >
                <Zap size={14} color="#F59E0B" />
                <span>50m Deep Work</span>
              </button>
            </>
          ) : (
            <>
              {focusSession.isPaused ? (
                <button
                  onClick={resumeFocusSession}
                  className="btn-primary"
                  style={{ flex: 1, padding: '9px 12px', fontSize: '12px' }}
                >
                  <Play size={14} />
                  <span>Resume</span>
                </button>
              ) : (
                <button
                  onClick={pauseFocusSession}
                  className="btn-secondary"
                  style={{ flex: 1, padding: '9px 12px', fontSize: '12px' }}
                >
                  <Pause size={14} />
                  <span>Pause</span>
                </button>
              )}
              <button
                onClick={resetFocusSession}
                className="btn-secondary"
                style={{ padding: '9px 12px', fontSize: '12px' }}
              >
                <RotateCcw size={14} />
                <span>Reset</span>
              </button>
            </>
          )}
        </div>
      </div>

      {/* Daily Progress Bar */}
      <div className="shivaar-card" style={{ padding: '14px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
          <div>
            <span style={{ fontSize: '13px', fontWeight: 700 }}>Today's Completion</span>
            <span style={{ fontSize: '11px', color: 'var(--text-muted)', marginLeft: '6px' }}>
              ({completedCount}/{totalCount} tasks • {totalMinutes}m planned)
            </span>
          </div>
          <span style={{ fontSize: '14px', fontWeight: 800, color: 'var(--accent-indigo)' }}>
            {progressPercent}%
          </span>
        </div>

        <div style={{ width: '100%', height: '8px', background: 'var(--bg-elevated)', borderRadius: '4px', overflow: 'hidden' }}>
          <div
            style={{
              width: `${progressPercent}%`,
              height: '100%',
              background: 'linear-gradient(90deg, #4F46E5, #10B981)',
              borderRadius: '4px',
              transition: 'width 0.3s ease'
            }}
          />
        </div>
      </div>

      {/* Task Filters & Quick Add Button */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '4px' }}>
          {['ALL', 'CORE', 'DSA', 'DEV', 'CS', 'ACADEMICS'].map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              style={{
                background: selectedCategory === cat ? 'var(--accent-indigo)' : 'var(--bg-card)',
                color: selectedCategory === cat ? '#FFFFFF' : 'var(--text-secondary)',
                border: `1px solid ${selectedCategory === cat ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
                padding: '4px 10px',
                borderRadius: '16px',
                fontSize: '11px',
                fontWeight: 600,
                cursor: 'pointer',
                whiteSpace: 'nowrap'
              }}
            >
              {cat}
            </button>
          ))}
        </div>

        <button
          onClick={() => setIsQuickAddOpen(true)}
          style={{
            background: 'var(--accent-indigo)',
            color: '#FFFFFF',
            border: 'none',
            borderRadius: '10px',
            padding: '6px 10px',
            fontSize: '11px',
            fontWeight: 700,
            display: 'flex',
            alignItems: 'center',
            gap: '4px',
            cursor: 'pointer',
            flexShrink: 0
          }}
        >
          <Plus size={14} />
          <span>Add</span>
        </button>
      </div>

      {/* Task List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {filteredTasks.length === 0 ? (
          <div
            className="shivaar-card"
            style={{ textAlign: 'center', padding: '32px 16px', color: 'var(--text-muted)' }}
          >
            <Sparkles size={28} style={{ margin: '0 auto 8px', color: 'var(--accent-indigo)' }} />
            <div style={{ fontSize: '14px', fontWeight: 600 }}>No tasks in this category</div>
            <p style={{ fontSize: '12px', marginTop: '4px' }}>
              Tap "+ Add" above or generate daily tasks from the DSA, Projects, or Academics hubs.
            </p>
          </div>
        ) : (
          filteredTasks.map((task) => {
            const isExpanded = expandedTaskId === task.id;
            const badgeClass =
              task.category === 'CORE' ? 'badge-core' :
              task.category === 'DSA' ? 'badge-dsa' :
              task.category === 'DEV' ? 'badge-dev' :
              task.category === 'CS' ? 'badge-cs' : 'badge-academics';

            return (
              <div
                key={task.id}
                className="shivaar-card"
                style={{
                  opacity: task.isCompleted ? 0.75 : 1,
                  borderLeft: `4px solid ${
                    task.priority === 'HIGH' ? '#EF4444' : task.priority === 'MEDIUM' ? '#F59E0B' : '#10B981'
                  }`
                }}
              >
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: '10px' }}>
                  {/* Checkbox */}
                  <button
                    onClick={() => toggleTask(task.id)}
                    aria-label={task.isCompleted ? 'Mark incomplete' : 'Mark completed'}
                    style={{
                      background: 'none',
                      border: 'none',
                      cursor: 'pointer',
                      color: task.isCompleted ? '#10B981' : 'var(--text-muted)',
                      padding: '2px',
                      marginTop: '2px'
                    }}
                  >
                    {task.isCompleted ? <CheckCircle2 size={22} fill="rgba(16, 185, 129, 0.2)" /> : <Circle size={22} />}
                  </button>

                  {/* Title & Metadata */}
                  <div style={{ flex: 1 }}>
                    <div
                      onClick={() => setExpandedTaskId(isExpanded ? null : task.id)}
                      style={{
                        fontSize: '14px',
                        fontWeight: 600,
                        color: 'var(--text-primary)',
                        textDecoration: task.isCompleted ? 'line-through' : 'none',
                        cursor: 'pointer'
                      }}
                    >
                      {task.title}
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginTop: '6px', flexWrap: 'wrap' }}>
                      <span className={badgeClass} style={{ fontSize: '10px', padding: '2px 6px', borderRadius: '4px', fontWeight: 700 }}>
                        {task.category}
                      </span>
                      <span style={{ fontSize: '11px', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '3px' }}>
                        <Clock size={11} /> {task.durationMinutes}m
                      </span>
                      <span
                        style={{
                          fontSize: '10px',
                          padding: '1px 5px',
                          borderRadius: '4px',
                          background: task.priority === 'HIGH' ? 'rgba(239, 68, 68, 0.15)' : 'rgba(245, 158, 11, 0.15)',
                          color: task.priority === 'HIGH' ? '#F87171' : '#FBBF24',
                          fontWeight: 600
                        }}
                      >
                        {task.priority}
                      </span>
                    </div>
                  </div>

                  {/* Expand / Collapse Button */}
                  <button
                    onClick={() => setExpandedTaskId(isExpanded ? null : task.id)}
                    aria-label="Expand task details"
                    style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', padding: '4px' }}
                  >
                    {isExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                  </button>
                </div>

                {/* Expanded Details */}
                {isExpanded && (
                  <div
                    style={{
                      marginTop: '12px',
                      paddingTop: '10px',
                      borderTop: '1px solid var(--border-subtle)',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '8px',
                      fontSize: '12px',
                      animation: 'fadeIn 0.2s ease'
                    }}
                  >
                    <div>
                      <span style={{ fontWeight: 700, color: 'var(--accent-indigo)' }}>Why it matters: </span>
                      <span style={{ color: 'var(--text-secondary)' }}>{task.whyItMatters}</span>
                    </div>

                    {task.instructions && (
                      <div>
                        <span style={{ fontWeight: 700, color: '#38BDF8' }}>Instructions: </span>
                        <span style={{ color: 'var(--text-secondary)' }}>{task.instructions}</span>
                      </div>
                    )}

                    {task.definitionOfDone && (
                      <div>
                        <span style={{ fontWeight: 700, color: '#10B981' }}>Definition of Done: </span>
                        <span style={{ color: 'var(--text-secondary)' }}>{task.definitionOfDone}</span>
                      </div>
                    )}

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '4px' }}>
                      {task.resourceUrl ? (
                        <a
                          href={task.resourceUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          style={{
                            color: 'var(--accent-indigo)',
                            fontSize: '11px',
                            fontWeight: 600,
                            display: 'flex',
                            alignItems: 'center',
                            gap: '4px',
                            textDecoration: 'none'
                          }}
                        >
                          <ExternalLink size={12} />
                          <span>{task.resourceName || 'Open Resource Link'}</span>
                        </a>
                      ) : <div />}

                      <button
                        onClick={() => deleteTask(task.id)}
                        aria-label="Delete task"
                        style={{
                          background: 'none',
                          border: 'none',
                          color: '#EF4444',
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          gap: '3px',
                          fontSize: '11px'
                        }}
                      >
                        <Trash2 size={13} />
                        <span>Delete</span>
                      </button>
                    </div>
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>

      {/* Quick Add Task Modal */}
      {isQuickAddOpen && (
        <div className="modal-overlay" onClick={() => setIsQuickAddOpen(false)}>
          <div className="modal-sheet" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Add Daily Engineering Task</h3>
              <button
                onClick={() => setIsQuickAddOpen(false)}
                style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCreateTask} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Task Title *
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Implement Dijkstra's Algorithm in C++"
                  value={newTitle}
                  onChange={(e) => setNewTitle(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '10px 12px',
                    borderRadius: '10px',
                    background: 'var(--bg-elevated)',
                    border: '1px solid var(--border-subtle)',
                    color: 'var(--text-primary)',
                    fontSize: '14px',
                    marginTop: '4px'
                  }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                <div>
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Category</label>
                  <select
                    value={newCategory}
                    onChange={(e) => setNewCategory(e.target.value as TaskCategory)}
                    style={{
                      width: '100%',
                      padding: '10px',
                      borderRadius: '10px',
                      background: 'var(--bg-elevated)',
                      border: '1px solid var(--border-subtle)',
                      color: 'var(--text-primary)',
                      fontSize: '13px',
                      marginTop: '4px'
                    }}
                  >
                    <option value="DSA">DSA</option>
                    <option value="DEV">DEV</option>
                    <option value="CORE">CORE</option>
                    <option value="CS">CS Theory</option>
                    <option value="ACADEMICS">Academics</option>
                  </select>
                </div>

                <div>
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Priority</label>
                  <select
                    value={newPriority}
                    onChange={(e) => setNewPriority(e.target.value as TaskPriority)}
                    style={{
                      width: '100%',
                      padding: '10px',
                      borderRadius: '10px',
                      background: 'var(--bg-elevated)',
                      border: '1px solid var(--border-subtle)',
                      color: 'var(--text-primary)',
                      fontSize: '13px',
                      marginTop: '4px'
                    }}
                  >
                    <option value="HIGH">HIGH</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="LOW">LOW</option>
                  </select>
                </div>
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Duration: {newDuration} Minutes
                </label>
                <input
                  type="range"
                  min="15"
                  max="120"
                  step="15"
                  value={newDuration}
                  onChange={(e) => setNewDuration(Number(e.target.value))}
                  style={{ width: '100%', marginTop: '4px' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Why It Matters (Placement context)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Essential graph question tested at Amazon and Microsoft"
                  value={newWhy}
                  onChange={(e) => setNewWhy(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '8px 12px',
                    borderRadius: '10px',
                    background: 'var(--bg-elevated)',
                    border: '1px solid var(--border-subtle)',
                    color: 'var(--text-primary)',
                    fontSize: '13px',
                    marginTop: '4px'
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Definition of Done (DoD)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Pass 100% test cases on LeetCode with O(E log V) space/time"
                  value={newDoD}
                  onChange={(e) => setNewDoD(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '8px 12px',
                    borderRadius: '10px',
                    background: 'var(--bg-elevated)',
                    border: '1px solid var(--border-subtle)',
                    color: 'var(--text-primary)',
                    fontSize: '13px',
                    marginTop: '4px'
                  }}
                />
              </div>

              <button type="submit" className="btn-primary" style={{ marginTop: '8px' }}>
                <Plus size={16} />
                <span>Save to Today Planner</span>
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
