import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  Briefcase, 
  Plus, 
  CalendarPlus, 
  CheckCircle2, 
  RotateCw, 
  Award, 
  Layers, 
  HelpCircle,
  ChevronRight,
  Sparkles,
  X 
} from 'lucide-react';
import { CareerApplication, CoreCsFlashcard } from '../../types';

export const CareerView: React.FC = () => {
  const { 
    state, 
    addCareerApp, 
    updateCareerAppStatus, 
    scheduleCareerPrepTask, 
    toggleFlashcardStatus 
  } = useApp();

  const [activeSubTab, setActiveSubTab] = useState<'pipeline' | 'flashcards'>('pipeline');
  const [selectedStatusFilter, setSelectedStatusFilter] = useState<string>('ALL');
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // Flashcards state
  const [selectedSubject, setSelectedSubject] = useState<string>('ALL');
  const [flippedCardId, setFlippedCardId] = useState<string | null>(null);

  // Add App Form State
  const [newCompany, setNewCompany] = useState('');
  const [newRole, setNewRole] = useState('Software Engineering Intern');
  const [newStatus, setNewStatus] = useState<CareerApplication['status']>('Applied');
  const [newDeadline, setNewDeadline] = useState('');
  const [newPackage, setNewPackage] = useState('');
  const [newNotes, setNewNotes] = useState('');

  const statuses: CareerApplication['status'][] = ['Wishlist', 'Applied', 'OA', 'Technical', 'HR', 'Offer'];

  const filteredApps = state.careerApps.filter((a) => {
    if (selectedStatusFilter === 'ALL') return true;
    return a.status === selectedStatusFilter;
  });

  const filteredFlashcards = state.flashcards.filter((f) => {
    if (selectedSubject === 'ALL') return true;
    return f.subject === selectedSubject;
  });

  const handleAddApplication = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCompany.trim()) return;

    addCareerApp({
      company: newCompany.trim(),
      role: newRole.trim() || 'Software Engineer',
      status: newStatus,
      deadline: newDeadline.trim() || new Date(Date.now() + 14 * 86400000).toISOString().split('T')[0],
      stipendOrPackage: newPackage.trim() || 'Competitive',
      notes: newNotes.trim() || undefined
    });

    setNewCompany('');
    setNewRole('Software Engineering Intern');
    setNewDeadline('');
    setNewPackage('');
    setNewNotes('');
    setIsAddModalOpen(false);
  };

  const getNextStatus = (current: CareerApplication['status']): CareerApplication['status'] | null => {
    const idx = statuses.indexOf(current);
    if (idx >= 0 && idx < statuses.length - 1) {
      return statuses[idx + 1];
    }
    return null;
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Top Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
            Career Pipeline & Interview Hub
          </h2>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            Application Kanban Tracker & Core CS Technical Flashcards
          </p>
        </div>

        {activeSubTab === 'pipeline' && (
          <button
            onClick={() => setIsAddModalOpen(true)}
            className="btn-primary"
            style={{ padding: '6px 12px', fontSize: '12px' }}
          >
            <Plus size={14} />
            <span>Apply</span>
          </button>
        )}
      </div>

      {/* Sub-tab Switcher */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
        <button
          onClick={() => setActiveSubTab('pipeline')}
          style={{
            padding: '10px',
            borderRadius: '12px',
            background: activeSubTab === 'pipeline' ? 'var(--accent-indigo)' : 'var(--bg-card)',
            color: activeSubTab === 'pipeline' ? '#FFFFFF' : 'var(--text-secondary)',
            border: `1px solid ${activeSubTab === 'pipeline' ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
            fontSize: '13px',
            fontWeight: 700,
            cursor: 'pointer'
          }}
        >
          Job & Internship Pipeline ({state.careerApps.length})
        </button>

        <button
          onClick={() => setActiveSubTab('flashcards')}
          style={{
            padding: '10px',
            borderRadius: '12px',
            background: activeSubTab === 'flashcards' ? 'var(--accent-indigo)' : 'var(--bg-card)',
            color: activeSubTab === 'flashcards' ? '#FFFFFF' : 'var(--text-secondary)',
            border: `1px solid ${activeSubTab === 'flashcards' ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
            fontSize: '13px',
            fontWeight: 700,
            cursor: 'pointer'
          }}
        >
          Core CS Flashcards ({state.flashcards.length})
        </button>
      </div>

      {/* PIPELINE VIEW */}
      {activeSubTab === 'pipeline' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {/* Status Breakdown Pills */}
          <div style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '4px' }}>
            {['ALL', ...statuses].map((st) => {
              const count = st === 'ALL' ? state.careerApps.length : state.careerApps.filter((a) => a.status === st).length;
              return (
                <button
                  key={st}
                  onClick={() => setSelectedStatusFilter(st)}
                  style={{
                    background: selectedStatusFilter === st ? 'var(--accent-indigo)' : 'var(--bg-card)',
                    color: selectedStatusFilter === st ? '#FFFFFF' : 'var(--text-secondary)',
                    border: `1px solid ${selectedStatusFilter === st ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
                    padding: '4px 10px',
                    borderRadius: '16px',
                    fontSize: '11px',
                    fontWeight: 600,
                    cursor: 'pointer',
                    whiteSpace: 'nowrap',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px'
                  }}
                >
                  <span>{st}</span>
                  <span style={{ opacity: 0.8, fontSize: '10px' }}>({count})</span>
                </button>
              );
            })}
          </div>

          {/* Application Cards */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {filteredApps.length === 0 ? (
              <div className="shivaar-card" style={{ textAlign: 'center', padding: '32px 16px', color: 'var(--text-muted)' }}>
                <Briefcase size={28} style={{ margin: '0 auto 8px', color: 'var(--accent-indigo)' }} />
                <div style={{ fontSize: '14px', fontWeight: 600 }}>No applications in this stage</div>
                <p style={{ fontSize: '12px', marginTop: '4px' }}>Tap "+ Apply" above to track jobs, internships, and OAs.</p>
              </div>
            ) : (
              filteredApps.map((app) => {
                const next = getNextStatus(app.status);
                const isOffer = app.status === 'Offer';
                return (
                  <div
                    key={app.id}
                    className="shivaar-card"
                    style={{
                      padding: '14px 16px',
                      borderLeft: isOffer ? '4px solid #10B981' : app.status === 'OA' ? '4px solid #F59E0B' : '4px solid var(--accent-indigo)'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <div>
                        <div style={{ fontSize: '16px', fontWeight: 800, color: 'var(--text-primary)' }}>
                          {app.company}
                        </div>
                        <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                          {app.role} • <span style={{ color: '#10B981', fontWeight: 600 }}>{app.stipendOrPackage}</span>
                        </div>
                      </div>

                      <span
                        style={{
                          fontSize: '10px',
                          padding: '3px 8px',
                          borderRadius: '12px',
                          background:
                            isOffer ? 'rgba(16, 185, 129, 0.2)' :
                            app.status === 'Technical' ? 'rgba(124, 58, 237, 0.2)' :
                            app.status === 'OA' ? 'rgba(245, 158, 11, 0.2)' : 'var(--bg-elevated)',
                          color:
                            isOffer ? '#10B981' :
                            app.status === 'Technical' ? '#A78BFA' :
                            app.status === 'OA' ? '#FBBF24' : 'var(--text-secondary)',
                          fontWeight: 700
                        }}
                      >
                        {app.status}
                      </span>
                    </div>

                    {app.notes && (
                      <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '8px', lineHeight: '1.4' }}>
                        {app.notes}
                      </p>
                    )}

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '12px', paddingTop: '10px', borderTop: '1px solid var(--border-subtle)' }}>
                      <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                        Deadline: {app.deadline}
                      </div>

                      <div style={{ display: 'flex', gap: '8px' }}>
                        <button
                          onClick={() => scheduleCareerPrepTask(app)}
                          style={{
                            background: 'var(--bg-elevated)',
                            border: '1px solid var(--border-subtle)',
                            color: 'var(--accent-indigo)',
                            padding: '5px 8px',
                            borderRadius: '8px',
                            fontSize: '11px',
                            fontWeight: 600,
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '4px'
                          }}
                        >
                          <CalendarPlus size={12} />
                          <span>Prep Task</span>
                        </button>

                        {next && (
                          <button
                            onClick={() => updateCareerAppStatus(app.id, next)}
                            style={{
                              background: 'var(--accent-indigo)',
                              border: 'none',
                              color: '#fff',
                              padding: '5px 10px',
                              borderRadius: '8px',
                              fontSize: '11px',
                              fontWeight: 700,
                              cursor: 'pointer',
                              display: 'flex',
                              alignItems: 'center',
                              gap: '4px'
                            }}
                          >
                            <span>Advance to {next}</span>
                            <ChevronRight size={13} />
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}

      {/* CORE CS FLASHCARDS VIEW */}
      {activeSubTab === 'flashcards' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {/* Subject Pills */}
          <div style={{ display: 'flex', gap: '6px' }}>
            {['ALL', 'OS', 'DBMS', 'CN', 'OOP'].map((sub) => (
              <button
                key={sub}
                onClick={() => setSelectedSubject(sub)}
                style={{
                  flex: 1,
                  padding: '6px 4px',
                  borderRadius: '8px',
                  border: `1px solid ${selectedSubject === sub ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
                  background: selectedSubject === sub ? 'var(--accent-indigo)' : 'var(--bg-card)',
                  color: selectedSubject === sub ? '#FFFFFF' : 'var(--text-secondary)',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                {sub}
              </button>
            ))}
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {filteredFlashcards.map((fc) => {
              const isFlipped = flippedCardId === fc.id;
              return (
                <div
                  key={fc.id}
                  className="shivaar-card"
                  onClick={() => setFlippedCardId(isFlipped ? null : fc.id)}
                  style={{
                    padding: '16px',
                    cursor: 'pointer',
                    background: isFlipped ? 'var(--bg-elevated)' : 'var(--bg-card)',
                    border: `1px solid ${isFlipped ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '10px', padding: '2px 6px', borderRadius: '4px', background: 'var(--accent-indigo-glow)', color: 'var(--accent-indigo)', fontWeight: 700 }}>
                      {fc.subject}
                    </span>
                    <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                      {isFlipped ? 'Tap to see Question' : 'Tap to reveal Answer'}
                    </span>
                  </div>

                  <div style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '8px', lineHeight: '1.4' }}>
                    {!isFlipped ? fc.question : fc.answer}
                  </div>

                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '12px', paddingTop: '8px', borderTop: '1px solid var(--border-subtle)' }}>
                    <span
                      style={{
                        fontSize: '10px',
                        color: fc.status === 'Mastered' ? '#10B981' : '#F59E0B',
                        fontWeight: 700
                      }}
                    >
                      Status: {fc.status}
                    </span>

                    <div style={{ display: 'flex', gap: '6px' }} onClick={(e) => e.stopPropagation()}>
                      <button
                        onClick={() => toggleFlashcardStatus(fc.id, 'Mastered')}
                        style={{
                          background: fc.status === 'Mastered' ? 'rgba(16, 185, 129, 0.2)' : 'var(--bg-elevated)',
                          border: '1px solid rgba(16, 185, 129, 0.3)',
                          color: '#10B981',
                          padding: '3px 8px',
                          borderRadius: '6px',
                          fontSize: '10px',
                          fontWeight: 700,
                          cursor: 'pointer'
                        }}
                      >
                        Got It ✓
                      </button>

                      <button
                        onClick={() => toggleFlashcardStatus(fc.id, 'Review')}
                        style={{
                          background: fc.status === 'Review' ? 'rgba(245, 158, 11, 0.2)' : 'var(--bg-elevated)',
                          border: '1px solid rgba(245, 158, 11, 0.3)',
                          color: '#F59E0B',
                          padding: '3px 8px',
                          borderRadius: '6px',
                          fontSize: '10px',
                          fontWeight: 700,
                          cursor: 'pointer'
                        }}
                      >
                        Review Later
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Add Application Modal */}
      {isAddModalOpen && (
        <div className="modal-overlay" onClick={() => setIsAddModalOpen(false)}>
          <div className="modal-sheet" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Track Job / Internship</h3>
              <button onClick={() => setIsAddModalOpen(false)} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddApplication} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Company Name *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Google, Atlassian, Microsoft"
                  value={newCompany}
                  onChange={(e) => setNewCompany(e.target.value)}
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
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Role</label>
                  <input
                    type="text"
                    value={newRole}
                    onChange={(e) => setNewRole(e.target.value)}
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
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Current Stage</label>
                  <select
                    value={newStatus}
                    onChange={(e) => setNewStatus(e.target.value as any)}
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
                    <option value="Wishlist">Wishlist</option>
                    <option value="Applied">Applied</option>
                    <option value="OA">Online Assessment (OA)</option>
                    <option value="Technical">Technical Round</option>
                    <option value="HR">HR Round</option>
                    <option value="Offer">Offer</option>
                  </select>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                <div>
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Stipend / CTC</label>
                  <input
                    type="text"
                    placeholder="e.g. ₹1L/month or ₹24 LPA"
                    value={newPackage}
                    onChange={(e) => setNewPackage(e.target.value)}
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
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Deadline</label>
                  <input
                    type="date"
                    value={newDeadline}
                    onChange={(e) => setNewDeadline(e.target.value)}
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

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Notes & Referral Contact</label>
                <input
                  type="text"
                  placeholder="e.g. Referred by Senior, OA link expires on Friday"
                  value={newNotes}
                  onChange={(e) => setNewNotes(e.target.value)}
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
                <span>Save to Pipeline</span>
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
