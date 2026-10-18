import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  GraduationCap, 
  Plus, 
  CalendarPlus, 
  Sliders, 
  AlertTriangle, 
  CheckCircle2, 
  BookOpen, 
  Clock,
  X 
} from 'lucide-react';
import { SubjectAttendance } from '../../types';

export const AcademicsView: React.FC = () => {
  const { state, addSubject, updateAttendance, scheduleExamPrepTask } = useApp();
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // Simulator state
  const [selectedSubId, setSelectedSubId] = useState<string>(state.subjects[0]?.id || '');
  const [hypotheticalBunks, setHypotheticalBunks] = useState(2);

  // Add Subject Form
  const [newCode, setNewCode] = useState('');
  const [newName, setNewName] = useState('');
  const [newCredits, setNewCredits] = useState(4);
  const [newTotal, setNewTotal] = useState(30);
  const [newAttended, setNewAttended] = useState(25);
  const [newExamDate, setNewExamDate] = useState('');

  const totalClassesAll = state.subjects.reduce((sum, s) => sum + s.totalClasses, 0);
  const attendedClassesAll = state.subjects.reduce((sum, s) => sum + s.attendedClasses, 0);
  const aggregatePercentage = totalClassesAll > 0 ? Math.round((attendedClassesAll / totalClassesAll) * 100) : 100;

  const currentSubject = state.subjects.find((s) => s.id === selectedSubId) || state.subjects[0];

  // Calculate Bunk Simulator
  const calculateBunkStatus = (sub?: SubjectAttendance, bunksToSkip: number = 0) => {
    if (!sub || sub.totalClasses === 0) return { safe: true, safeBunksCount: 0, requiredToAttend: 0, projectedPercentage: 100 };
    const currentPct = (sub.attendedClasses / sub.totalClasses) * 100;
    
    // How many can bunk without falling below 75%
    // (attended) / (total + x) >= 0.75  =>  x <= (attended / 0.75) - total
    const maxSafeTotal = Math.floor(sub.attendedClasses / 0.75);
    const safeBunksCount = Math.max(0, maxSafeTotal - sub.totalClasses);

    // If currently below 75%, how many to attend:
    // (attended + y) / (total + y) >= 0.75  =>  y >= (0.75*total - attended) / 0.25
    const requiredToAttend = currentPct < 75 
      ? Math.ceil((0.75 * sub.totalClasses - sub.attendedClasses) / 0.25) 
      : 0;

    // Projected if skipping bunksToSkip:
    const projectedTotal = sub.totalClasses + bunksToSkip;
    const projectedPct = Math.round((sub.attendedClasses / projectedTotal) * 100);

    return {
      currentPct: Math.round(currentPct),
      safe: projectedPct >= 75,
      safeBunksCount,
      requiredToAttend,
      projectedPercentage: projectedPct
    };
  };

  const simResult = calculateBunkStatus(currentSubject, hypotheticalBunks);

  const handleAddSubject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCode.trim() || !newName.trim()) return;

    addSubject({
      code: newCode.trim().toUpperCase(),
      name: newName.trim(),
      credits: Number(newCredits),
      totalClasses: Number(newTotal),
      attendedClasses: Number(newAttended),
      targetPercentage: 75,
      examDate: newExamDate.trim() || undefined
    });

    setNewCode('');
    setNewName('');
    setNewExamDate('');
    setIsAddModalOpen(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Top Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
            Academics & SGPA Engine
          </h2>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            Attendance Tracker, "Can I Bunk?" Simulator & Exam Prep
          </p>
        </div>

        <button
          onClick={() => setIsAddModalOpen(true)}
          className="btn-primary"
          style={{ padding: '6px 12px', fontSize: '12px' }}
        >
          <Plus size={14} />
          <span>Subject</span>
        </button>
      </div>

      {/* Aggregate Attendance Card */}
      <div
        className="shivaar-card"
        style={{
          background: 'linear-gradient(135deg, rgba(59, 130, 246, 0.15) 0%, rgba(30, 27, 75, 0.5) 100%)',
          borderColor: aggregatePercentage >= 75 ? 'rgba(59, 130, 246, 0.3)' : 'rgba(239, 68, 68, 0.4)',
          padding: '16px'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontSize: '11px', color: '#60A5FA', fontWeight: 700, textTransform: 'uppercase' }}>
              Overall Semester Attendance
            </div>
            <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)', marginTop: '2px' }}>
              {aggregatePercentage}%
            </div>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
              {attendedClassesAll} attended out of {totalClassesAll} total lectures (75% Minimum Mandatory)
            </div>
          </div>

          <div
            style={{
              padding: '6px 12px',
              borderRadius: '20px',
              background: aggregatePercentage >= 75 ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)',
              color: aggregatePercentage >= 75 ? '#10B981' : '#EF4444',
              fontWeight: 800,
              fontSize: '12px'
            }}
          >
            {aggregatePercentage >= 75 ? 'SAFE FOR EXAMS' : 'SHORTAGE ALERT'}
          </div>
        </div>
      </div>

      {/* "CAN I BUNK?" SIMULATOR */}
      {currentSubject && (
        <div
          className="shivaar-card"
          style={{
            background: 'linear-gradient(135deg, rgba(30, 27, 75, 0.6) 0%, rgba(18, 24, 41, 0.8) 100%)',
            borderColor: 'rgba(99, 102, 241, 0.35)',
            padding: '16px'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px' }}>
            <Sliders size={18} color="#38BDF8" />
            <span style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)' }}>
              "Can I Bunk?" What-If Simulator
            </span>
          </div>

          {/* Subject Picker for Simulator */}
          <div style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '8px' }}>
            {state.subjects.map((sub) => (
              <button
                key={sub.id}
                onClick={() => setSelectedSubId(sub.id)}
                style={{
                  background: sub.id === currentSubject.id ? 'var(--accent-indigo)' : 'var(--bg-card)',
                  color: sub.id === currentSubject.id ? '#fff' : 'var(--text-secondary)',
                  border: `1px solid ${sub.id === currentSubject.id ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
                  padding: '4px 8px',
                  borderRadius: '8px',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer',
                  whiteSpace: 'nowrap'
                }}
              >
                {sub.code}
              </button>
            ))}
          </div>

          {/* Slider */}
          <div style={{ marginTop: '10px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: 'var(--text-secondary)' }}>
              <span>Simulate skipping next:</span>
              <span style={{ fontWeight: 700, color: '#38BDF8' }}>{hypotheticalBunks} classes</span>
            </div>
            <input
              type="range"
              min="0"
              max="10"
              value={hypotheticalBunks}
              onChange={(e) => setHypotheticalBunks(Number(e.target.value))}
              style={{ width: '100%', marginTop: '6px' }}
            />
          </div>

          {/* Simulator Outcome Display */}
          <div
            style={{
              marginTop: '12px',
              padding: '10px 12px',
              borderRadius: '10px',
              background: simResult.safe ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
              border: `1px solid ${simResult.safe ? 'rgba(16, 185, 129, 0.3)' : 'rgba(239, 68, 68, 0.3)'}`,
              fontSize: '12px'
            }}
          >
            {simResult.safe ? (
              <div style={{ color: '#10B981', display: 'flex', alignItems: 'center', gap: '6px' }}>
                <CheckCircle2 size={16} />
                <span>
                  <strong>Safe to bunk!</strong> Skipping {hypotheticalBunks} classes drops {currentSubject.code} from {simResult.currentPct}% to {simResult.projectedPercentage}% (still above 75%).
                </span>
              </div>
            ) : (
              <div style={{ color: '#EF4444', display: 'flex', alignItems: 'center', gap: '6px' }}>
                <AlertTriangle size={16} />
                <span>
                  <strong>Shortage Danger!</strong> Skipping {hypotheticalBunks} classes drops you to {simResult.projectedPercentage}%. You must attend at least <strong>{simResult.requiredToAttend} consecutive classes</strong> to recover!
                </span>
              </div>
            )}
            <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '4px' }}>
              Current margin: You can safely skip up to <strong>{simResult.safeBunksCount} classes</strong> total without falling below 75%.
            </div>
          </div>
        </div>
      )}

      {/* Subject Cards List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-secondary)' }}>
          Enrolled Subjects ({state.subjects.length})
        </div>

        {state.subjects.map((sub) => {
          const pct = sub.totalClasses > 0 ? Math.round((sub.attendedClasses / sub.totalClasses) * 100) : 100;
          const isSafe = pct >= 75;

          return (
            <div key={sub.id} className="shivaar-card" style={{ padding: '14px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{ fontSize: '11px', fontWeight: 800, color: 'var(--accent-indigo)' }}>
                      {sub.code}
                    </span>
                    <span style={{ fontSize: '10px', color: 'var(--text-muted)', background: 'var(--bg-elevated)', padding: '1px 5px', borderRadius: '4px' }}>
                      {sub.credits} Credits
                    </span>
                  </div>
                  <div style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '2px' }}>
                    {sub.name}
                  </div>
                </div>

                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '16px', fontWeight: 800, color: isSafe ? '#10B981' : '#EF4444' }}>
                    {pct}%
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                    {sub.attendedClasses}/{sub.totalClasses}
                  </div>
                </div>
              </div>

              {/* Attendance Bar */}
              <div style={{ width: '100%', height: '6px', background: 'var(--bg-elevated)', borderRadius: '3px', overflow: 'hidden', marginTop: '10px' }}>
                <div
                  style={{
                    width: `${Math.min(pct, 100)}%`,
                    height: '100%',
                    background: isSafe ? '#10B981' : '#EF4444',
                    borderRadius: '3px'
                  }}
                />
              </div>

              {/* Actions: +1 Attended, +1 Missed, and Exam Revision Task */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '12px', paddingTop: '8px', borderTop: '1px solid var(--border-subtle)' }}>
                <div style={{ display: 'flex', gap: '6px' }}>
                  <button
                    onClick={() => updateAttendance(sub.id, sub.attendedClasses + 1, sub.totalClasses + 1)}
                    style={{
                      background: 'rgba(16, 185, 129, 0.15)',
                      border: '1px solid rgba(16, 185, 129, 0.3)',
                      color: '#10B981',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px',
                      fontWeight: 700,
                      cursor: 'pointer'
                    }}
                  >
                    +1 Attended
                  </button>

                  <button
                    onClick={() => updateAttendance(sub.id, sub.attendedClasses, sub.totalClasses + 1)}
                    style={{
                      background: 'rgba(239, 68, 68, 0.15)',
                      border: '1px solid rgba(239, 68, 68, 0.3)',
                      color: '#EF4444',
                      padding: '4px 8px',
                      borderRadius: '6px',
                      fontSize: '11px',
                      fontWeight: 700,
                      cursor: 'pointer'
                    }}
                  >
                    +1 Missed
                  </button>
                </div>

                <button
                  onClick={() => scheduleExamPrepTask(sub)}
                  style={{
                    background: 'transparent',
                    border: 'none',
                    color: 'var(--accent-indigo)',
                    fontSize: '11px',
                    fontWeight: 600,
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px'
                  }}
                >
                  <CalendarPlus size={12} />
                  <span>Exam Task</span>
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* Add Subject Modal */}
      {isAddModalOpen && (
        <div className="modal-overlay" onClick={() => setIsAddModalOpen(false)}>
          <div className="modal-sheet" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Add Academic Course</h3>
              <button onClick={() => setIsAddModalOpen(false)} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddSubject} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '10px' }}>
                <div>
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Code *</label>
                  <input
                    type="text"
                    required
                    placeholder="CS-401"
                    value={newCode}
                    onChange={(e) => setNewCode(e.target.value)}
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
                  />
                </div>

                <div>
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Subject Name *</label>
                  <input
                    type="text"
                    required
                    placeholder="Operating Systems"
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
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
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px' }}>
                <div>
                  <label style={{ fontSize: '11px', fontWeight: 600, color: 'var(--text-secondary)' }}>Credits</label>
                  <input
                    type="number"
                    min="1"
                    max="6"
                    value={newCredits}
                    onChange={(e) => setNewCredits(Number(e.target.value))}
                    style={{
                      width: '100%',
                      padding: '8px',
                      borderRadius: '8px',
                      background: 'var(--bg-elevated)',
                      border: '1px solid var(--border-subtle)',
                      color: 'var(--text-primary)',
                      fontSize: '13px',
                      marginTop: '4px'
                    }}
                  />
                </div>

                <div>
                  <label style={{ fontSize: '11px', fontWeight: 600, color: 'var(--text-secondary)' }}>Attended</label>
                  <input
                    type="number"
                    min="0"
                    value={newAttended}
                    onChange={(e) => setNewAttended(Number(e.target.value))}
                    style={{
                      width: '100%',
                      padding: '8px',
                      borderRadius: '8px',
                      background: 'var(--bg-elevated)',
                      border: '1px solid var(--border-subtle)',
                      color: 'var(--text-primary)',
                      fontSize: '13px',
                      marginTop: '4px'
                    }}
                  />
                </div>

                <div>
                  <label style={{ fontSize: '11px', fontWeight: 600, color: 'var(--text-secondary)' }}>Total Held</label>
                  <input
                    type="number"
                    min="1"
                    value={newTotal}
                    onChange={(e) => setNewTotal(Number(e.target.value))}
                    style={{
                      width: '100%',
                      padding: '8px',
                      borderRadius: '8px',
                      background: 'var(--bg-elevated)',
                      border: '1px solid var(--border-subtle)',
                      color: 'var(--text-primary)',
                      fontSize: '13px',
                      marginTop: '4px'
                    }}
                  />
                </div>
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Upcoming Exam Date</label>
                <input
                  type="date"
                  value={newExamDate}
                  onChange={(e) => setNewExamDate(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '8px',
                    borderRadius: '8px',
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
                <span>Save Course</span>
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
