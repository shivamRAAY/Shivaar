import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  FolderGit2, 
  GitBranch, 
  ExternalLink, 
  Plus, 
  Sparkles, 
  CalendarPlus, 
  Award,
  Layers,
  X 
} from 'lucide-react';
import { ProjectItem } from '../../types';

export const ProjectsView: React.FC = () => {
  const { state, addProject, scheduleProjectSprint } = useApp();
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // New Project form state
  const [newTitle, setNewTitle] = useState('');
  const [newTier, setNewTier] = useState<ProjectItem['tier']>('Intermediate');
  const [newTagline, setNewTagline] = useState('');
  const [newDescription, setNewDescription] = useState('');
  const [newTechStack, setNewTechStack] = useState('');
  const [newGithub, setNewGithub] = useState('');
  const [newLiveUrl, setNewLiveUrl] = useState('');
  const [newRecruiterHook, setNewRecruiterHook] = useState('');

  const averageReadiness = state.projects.length > 0
    ? Math.round(state.projects.reduce((acc, p) => acc + p.readinessScore, 0) / state.projects.length)
    : 70;

  const handleCreateProject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;

    addProject({
      title: newTitle.trim(),
      tier: newTier,
      tagline: newTagline.trim() || 'Modern software architecture project',
      description: newDescription.trim() || 'Engineered with clean code principles and comprehensive documentation.',
      techStack: newTechStack.split(',').map((s) => s.trim()).filter(Boolean),
      githubUrl: newGithub.trim() || 'https://github.com/username/project',
      liveUrl: newLiveUrl.trim() || undefined,
      recruiterHook: newRecruiterHook.trim() || 'Demonstrates clean modular architecture, error handling, and deployment.',
      status: 'In Progress',
      readinessScore: 80
    });

    setNewTitle('');
    setNewTagline('');
    setNewDescription('');
    setNewTechStack('');
    setNewGithub('');
    setNewLiveUrl('');
    setNewRecruiterHook('');
    setIsAddModalOpen(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
            Project Studio
          </h2>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            4-Tier High-Impact SDE Portfolio Architecture
          </p>
        </div>

        <button
          onClick={() => setIsAddModalOpen(true)}
          className="btn-primary"
          style={{ padding: '6px 12px', fontSize: '12px' }}
        >
          <Plus size={14} />
          <span>New Project</span>
        </button>
      </div>

      {/* Portfolio Readiness Score Card */}
      <div
        className="shivaar-card"
        style={{
          background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(6, 182, 212, 0.1) 100%)',
          borderColor: 'rgba(16, 185, 129, 0.3)',
          padding: '16px'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontSize: '11px', color: '#10B981', fontWeight: 700, textTransform: 'uppercase' }}>
              Portfolio Recruiter Readiness
            </div>
            <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)', marginTop: '2px' }}>
              {averageReadiness} / 100
            </div>
            <p style={{ fontSize: '11px', color: 'var(--text-secondary)', marginTop: '2px' }}>
              Evaluates live URLs, README documentation, test suites, and distributed complexity.
            </p>
          </div>

          <div
            style={{
              width: '56px',
              height: '56px',
              borderRadius: '50%',
              background: 'rgba(16, 185, 129, 0.2)',
              border: '3px solid #10B981',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 800,
              fontSize: '18px',
              color: '#10B981'
            }}
          >
            {averageReadiness}%
          </div>
        </div>
      </div>

      {/* 4-Tier Ladder Explanation */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
        <div style={{ padding: '10px', borderRadius: '10px', background: 'var(--bg-card)', border: '1px solid var(--border-subtle)', fontSize: '11px' }}>
          <span style={{ fontWeight: 700, color: '#38BDF8' }}>Tier 1: Foundation</span>
          <div style={{ color: 'var(--text-muted)', marginTop: '2px' }}>CLI, POSIX, C/C++ memory tools</div>
        </div>
        <div style={{ padding: '10px', borderRadius: '10px', background: 'var(--bg-card)', border: '1px solid var(--border-subtle)', fontSize: '11px' }}>
          <span style={{ fontWeight: 700, color: '#10B981' }}>Tier 2: Full-Stack</span>
          <div style={{ color: 'var(--text-muted)', marginTop: '2px' }}>React 19, WebSockets, DB clustering</div>
        </div>
        <div style={{ padding: '10px', borderRadius: '10px', background: 'var(--bg-card)', border: '1px solid var(--border-subtle)', fontSize: '11px' }}>
          <span style={{ fontWeight: 700, color: '#F59E0B' }}>Tier 3: Distributed</span>
          <div style={{ color: 'var(--text-muted)', marginTop: '2px' }}>Raft, KV-store, gRPC, Docker</div>
        </div>
        <div style={{ padding: '10px', borderRadius: '10px', background: 'var(--bg-card)', border: '1px solid var(--border-subtle)', fontSize: '11px' }}>
          <span style={{ fontWeight: 700, color: '#A78BFA' }}>Tier 4: Capstone</span>
          <div style={{ color: 'var(--text-muted)', marginTop: '2px' }}>Production scale with real metrics</div>
        </div>
      </div>

      {/* Projects List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {state.projects.map((proj) => {
          const tierColor =
            proj.tier === 'Foundation' ? '#38BDF8' :
            proj.tier === 'Intermediate' ? '#10B981' :
            proj.tier === 'Advanced' ? '#F59E0B' : '#A78BFA';

          return (
            <div key={proj.id} className="shivaar-card" style={{ padding: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span
                      style={{
                        fontSize: '10px',
                        padding: '2px 6px',
                        borderRadius: '4px',
                        background: `${tierColor}22`,
                        color: tierColor,
                        fontWeight: 700
                      }}
                    >
                      {proj.tier}
                    </span>
                    <span
                      style={{
                        fontSize: '10px',
                        padding: '2px 6px',
                        borderRadius: '4px',
                        background: proj.status === 'Shipped' ? 'rgba(16, 185, 129, 0.15)' : 'rgba(245, 158, 11, 0.15)',
                        color: proj.status === 'Shipped' ? '#10B981' : '#F59E0B',
                        fontWeight: 600
                      }}
                    >
                      {proj.status}
                    </span>
                  </div>

                  <h3 style={{ fontSize: '15px', fontWeight: 800, color: 'var(--text-primary)', marginTop: '4px' }}>
                    {proj.title}
                  </h3>
                  <div style={{ fontSize: '12px', color: 'var(--accent-indigo)', fontWeight: 600, marginTop: '2px' }}>
                    {proj.tagline}
                  </div>
                </div>

                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '10px', color: 'var(--text-muted)' }}>Score</div>
                  <div style={{ fontSize: '14px', fontWeight: 800, color: '#10B981' }}>{proj.readinessScore}%</div>
                </div>
              </div>

              <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '8px', lineHeight: '1.4' }}>
                {proj.description}
              </p>

              {/* Recruiter Hook */}
              <div style={{ marginTop: '10px', padding: '8px 10px', background: 'var(--bg-elevated)', borderRadius: '8px', fontSize: '11px' }}>
                <span style={{ fontWeight: 700, color: '#F59E0B' }}>Recruiter Hook: </span>
                <span style={{ color: 'var(--text-secondary)' }}>{proj.recruiterHook}</span>
              </div>

              {/* Tech Stack Chips */}
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginTop: '10px' }}>
                {proj.techStack.map((tech, i) => (
                  <span
                    key={i}
                    style={{
                      fontSize: '10px',
                      padding: '2px 6px',
                      borderRadius: '4px',
                      background: 'var(--bg-elevated)',
                      color: 'var(--text-muted)',
                      border: '1px solid var(--border-subtle)'
                    }}
                  >
                    {tech}
                  </span>
                ))}
              </div>

              {/* Action Buttons */}
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '14px', paddingTop: '10px', borderTop: '1px solid var(--border-subtle)' }}>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <a
                    href={proj.githubUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '4px',
                      fontSize: '12px',
                      fontWeight: 600,
                      color: 'var(--text-primary)',
                      textDecoration: 'none'
                    }}
                  >
                    <GitBranch size={14} />
                    <span>Repo</span>
                  </a>

                  {proj.liveUrl && (
                    <a
                      href={proj.liveUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px',
                        fontSize: '12px',
                        fontWeight: 600,
                        color: 'var(--accent-indigo)',
                        textDecoration: 'none'
                      }}
                    >
                      <ExternalLink size={14} />
                      <span>Live Demo</span>
                    </a>
                  )}
                </div>

                <button
                  onClick={() => scheduleProjectSprint(proj)}
                  style={{
                    background: 'var(--accent-indigo)',
                    border: 'none',
                    color: '#fff',
                    borderRadius: '8px',
                    padding: '6px 10px',
                    fontSize: '11px',
                    fontWeight: 700,
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px'
                  }}
                >
                  <CalendarPlus size={12} />
                  <span>Sprint Today</span>
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* Add Project Modal */}
      {isAddModalOpen && (
        <div className="modal-overlay" onClick={() => setIsAddModalOpen(false)}>
          <div className="modal-sheet" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Add Portfolio Project</h3>
              <button onClick={() => setIsAddModalOpen(false)} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCreateProject} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Project Title *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Distributed In-Memory Cache"
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
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Tier</label>
                  <select
                    value={newTier}
                    onChange={(e) => setNewTier(e.target.value as any)}
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
                    <option value="Foundation">Tier 1: Foundation (CLI/C++)</option>
                    <option value="Intermediate">Tier 2: Full-Stack (Web)</option>
                    <option value="Advanced">Tier 3: Distributed / AI</option>
                    <option value="Capstone">Tier 4: Production Capstone</option>
                  </select>
                </div>

                <div>
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Tech Stack (comma separated)</label>
                  <input
                    type="text"
                    placeholder="Go, gRPC, Docker"
                    value={newTechStack}
                    onChange={(e) => setNewTechStack(e.target.value)}
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
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Tagline</label>
                <input
                  type="text"
                  placeholder="e.g. Sub-millisecond latency distributed cache with LRU eviction"
                  value={newTagline}
                  onChange={(e) => setNewTagline(e.target.value)}
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
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>GitHub URL</label>
                <input
                  type="url"
                  placeholder="https://github.com/username/repo"
                  value={newGithub}
                  onChange={(e) => setNewGithub(e.target.value)}
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
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Live Demo URL (optional)</label>
                <input
                  type="url"
                  placeholder="https://project.vercel.app"
                  value={newLiveUrl}
                  onChange={(e) => setNewLiveUrl(e.target.value)}
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
                <span>Save to Project Studio</span>
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
