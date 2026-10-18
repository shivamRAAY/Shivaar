import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  CheckCircle2, 
  RotateCw, 
  Circle, 
  ExternalLink, 
  Search, 
  Plus, 
  CalendarPlus, 
  Code2, 
  Sparkles,
  X
} from 'lucide-react';
import { DsaProblem } from '../../types';

export const DsaView: React.FC = () => {
  const { state, toggleDsaStatus, addDsaProblem, scheduleDsaTask } = useApp();

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // New problem form
  const [newTitle, setNewTitle] = useState('');
  const [newCategory, setNewCategory] = useState('Arrays & Hashing');
  const [newDifficulty, setNewDifficulty] = useState<'Easy' | 'Medium' | 'Hard'>('Medium');
  const [newPattern, setNewPattern] = useState('');
  const [newUrl, setNewUrl] = useState('');

  const totalProblems = state.dsaProblems.length;
  const solvedProblems = state.dsaProblems.filter((p) => p.status === 'Solved').length;
  const revisingProblems = state.dsaProblems.filter((p) => p.status === 'Revising').length;

  const easySolved = state.dsaProblems.filter((p) => p.difficulty === 'Easy' && p.status === 'Solved').length;
  const easyTotal = state.dsaProblems.filter((p) => p.difficulty === 'Easy').length;

  const medSolved = state.dsaProblems.filter((p) => p.difficulty === 'Medium' && p.status === 'Solved').length;
  const medTotal = state.dsaProblems.filter((p) => p.difficulty === 'Medium').length;

  const hardSolved = state.dsaProblems.filter((p) => p.difficulty === 'Hard' && p.status === 'Solved').length;
  const hardTotal = state.dsaProblems.filter((p) => p.difficulty === 'Hard').length;

  // Extract unique categories
  const categories = ['ALL', ...Array.from(new Set(state.dsaProblems.map((p) => p.category)))];

  const filteredProblems = state.dsaProblems.filter((p) => {
    const matchesSearch = p.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          p.pattern.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'ALL' || p.category === selectedCategory;
    const matchesStatus = selectedStatus === 'ALL' || p.status === selectedStatus;
    return matchesSearch && matchesCategory && matchesStatus;
  });

  const handleAddProblem = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;

    addDsaProblem({
      title: newTitle.trim(),
      category: newCategory,
      difficulty: newDifficulty,
      status: 'Unsolved',
      platform: 'LeetCode',
      pattern: newPattern.trim() || 'General Algorithm',
      url: newUrl.trim() || `https://leetcode.com/problemset/all/?search=${encodeURIComponent(newTitle.trim())}`
    });

    setNewTitle('');
    setNewPattern('');
    setNewUrl('');
    setIsAddModalOpen(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
            DSA Placement Engine
          </h2>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            Striver & Blind 75 Pattern Mastery for Product Companies
          </p>
        </div>

        <button
          onClick={() => setIsAddModalOpen(true)}
          className="btn-primary"
          style={{ padding: '6px 12px', fontSize: '12px' }}
        >
          <Plus size={14} />
          <span>Add</span>
        </button>
      </div>

      {/* Stats Summary Card */}
      <div className="shivaar-card" style={{ padding: '16px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
          <div>
            <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Mastery Progress
            </div>
            <div style={{ fontSize: '22px', fontWeight: 800, color: 'var(--text-primary)' }}>
              {solvedProblems} <span style={{ fontSize: '14px', color: 'var(--text-muted)', fontWeight: 500 }}>/ {totalProblems} Solved</span>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '12px', textAlign: 'right' }}>
            <div>
              <div style={{ fontSize: '10px', color: '#10B981', fontWeight: 700 }}>EASY</div>
              <div style={{ fontSize: '13px', fontWeight: 700 }}>{easySolved}/{easyTotal}</div>
            </div>
            <div>
              <div style={{ fontSize: '10px', color: '#F59E0B', fontWeight: 700 }}>MED</div>
              <div style={{ fontSize: '13px', fontWeight: 700 }}>{medSolved}/{medTotal}</div>
            </div>
            <div>
              <div style={{ fontSize: '10px', color: '#EF4444', fontWeight: 700 }}>HARD</div>
              <div style={{ fontSize: '13px', fontWeight: 700 }}>{hardSolved}/{hardTotal}</div>
            </div>
          </div>
        </div>

        {/* Triple Color Bar */}
        <div style={{ width: '100%', height: '8px', background: 'var(--bg-elevated)', borderRadius: '4px', overflow: 'hidden', display: 'flex' }}>
          <div style={{ width: `${totalProblems > 0 ? (easySolved / totalProblems) * 100 : 0}%`, background: '#10B981' }} />
          <div style={{ width: `${totalProblems > 0 ? (medSolved / totalProblems) * 100 : 0}%`, background: '#F59E0B' }} />
          <div style={{ width: `${totalProblems > 0 ? (hardSolved / totalProblems) * 100 : 0}%`, background: '#EF4444' }} />
        </div>
      </div>

      {/* Search Bar */}
      <div style={{ position: 'relative' }}>
        <Search size={16} color="var(--text-muted)" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
        <input
          type="text"
          placeholder="Search problems by name or pattern..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          style={{
            width: '100%',
            padding: '10px 12px 10px 36px',
            borderRadius: '12px',
            background: 'var(--bg-card)',
            border: '1px solid var(--border-subtle)',
            color: 'var(--text-primary)',
            fontSize: '13px'
          }}
        />
      </div>

      {/* Status Filter Tabs */}
      <div style={{ display: 'flex', gap: '6px' }}>
        {['ALL', 'Solved', 'Revising', 'Unsolved'].map((st) => (
          <button
            key={st}
            onClick={() => setSelectedStatus(st)}
            style={{
              flex: 1,
              padding: '6px 4px',
              borderRadius: '8px',
              border: `1px solid ${selectedStatus === st ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
              background: selectedStatus === st ? 'var(--accent-indigo)' : 'var(--bg-card)',
              color: selectedStatus === st ? '#FFFFFF' : 'var(--text-secondary)',
              fontSize: '11px',
              fontWeight: 600,
              cursor: 'pointer'
            }}
          >
            {st}
          </button>
        ))}
      </div>

      {/* Category Horizontal Scrolling Pills */}
      <div style={{ display: 'flex', gap: '6px', overflowX: 'auto', paddingBottom: '4px' }}>
        {categories.map((cat) => (
          <button
            key={cat}
            onClick={() => setSelectedCategory(cat)}
            style={{
              background: selectedCategory === cat ? 'var(--accent-indigo-glow)' : 'var(--bg-card)',
              color: selectedCategory === cat ? 'var(--accent-indigo)' : 'var(--text-secondary)',
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

      {/* Problem List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {filteredProblems.length === 0 ? (
          <div className="shivaar-card" style={{ textAlign: 'center', padding: '32px 16px', color: 'var(--text-muted)' }}>
            <Code2 size={28} style={{ margin: '0 auto 8px', color: 'var(--accent-indigo)' }} />
            <div style={{ fontSize: '14px', fontWeight: 600 }}>No problems match your filters</div>
          </div>
        ) : (
          filteredProblems.map((p) => {
            const diffColor = p.difficulty === 'Easy' ? '#10B981' : p.difficulty === 'Medium' ? '#F59E0B' : '#EF4444';
            return (
              <div key={p.id} className="shivaar-card" style={{ padding: '12px 14px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '10px' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                      <span style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)' }}>
                        {p.title}
                      </span>
                      <a
                        href={p.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        style={{ color: 'var(--text-muted)', display: 'flex', alignItems: 'center' }}
                        aria-label={`Open ${p.title} on ${p.platform}`}
                      >
                        <ExternalLink size={13} />
                      </a>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginTop: '6px', flexWrap: 'wrap' }}>
                      <span
                        style={{
                          fontSize: '10px',
                          padding: '1px 6px',
                          borderRadius: '4px',
                          background: `${diffColor}22`,
                          color: diffColor,
                          fontWeight: 700
                        }}
                      >
                        {p.difficulty}
                      </span>
                      <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                        {p.category}
                      </span>
                      <span style={{ fontSize: '11px', color: '#38BDF8', background: 'rgba(56, 189, 248, 0.1)', padding: '1px 6px', borderRadius: '4px' }}>
                        {p.pattern}
                      </span>
                    </div>
                  </div>

                  {/* Status Toggle Button */}
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '6px' }}>
                    <button
                      onClick={() => {
                        const next = p.status === 'Solved' ? 'Unsolved' : p.status === 'Unsolved' ? 'Revising' : 'Solved';
                        toggleDsaStatus(p.id, next);
                      }}
                      style={{
                        background:
                          p.status === 'Solved' ? 'rgba(16, 185, 129, 0.15)' :
                          p.status === 'Revising' ? 'rgba(245, 158, 11, 0.15)' : 'var(--bg-elevated)',
                        color:
                          p.status === 'Solved' ? '#10B981' :
                          p.status === 'Revising' ? '#F59E0B' : 'var(--text-muted)',
                        border: `1px solid ${
                          p.status === 'Solved' ? 'rgba(16, 185, 129, 0.3)' :
                          p.status === 'Revising' ? 'rgba(245, 158, 11, 0.3)' : 'var(--border-subtle)'
                        }`,
                        padding: '4px 8px',
                        borderRadius: '6px',
                        fontSize: '11px',
                        fontWeight: 700,
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px'
                      }}
                    >
                      {p.status === 'Solved' && <CheckCircle2 size={12} />}
                      {p.status === 'Revising' && <RotateCw size={12} />}
                      {p.status === 'Unsolved' && <Circle size={12} />}
                      <span>{p.status}</span>
                    </button>

                    {/* Schedule Daily Practice Action */}
                    <button
                      onClick={() => scheduleDsaTask(p)}
                      title="Schedule practice task for today"
                      style={{
                        background: 'transparent',
                        border: 'none',
                        color: 'var(--accent-indigo)',
                        fontSize: '11px',
                        fontWeight: 600,
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '3px'
                      }}
                    >
                      <CalendarPlus size={12} />
                      <span>Practice Today</span>
                    </button>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Add Custom Problem Modal */}
      {isAddModalOpen && (
        <div className="modal-overlay" onClick={() => setIsAddModalOpen(false)}>
          <div className="modal-sheet" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Add DSA Problem</h3>
              <button onClick={() => setIsAddModalOpen(false)} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}>
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddProblem} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Problem Title *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Trapping Rain Water"
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
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Difficulty</label>
                  <select
                    value={newDifficulty}
                    onChange={(e) => setNewDifficulty(e.target.value as any)}
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
                    <option value="Easy">Easy</option>
                    <option value="Medium">Medium</option>
                    <option value="Hard">Hard</option>
                  </select>
                </div>

                <div>
                  <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Category</label>
                  <select
                    value={newCategory}
                    onChange={(e) => setNewCategory(e.target.value)}
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
                    <option value="Arrays & Hashing">Arrays & Hashing</option>
                    <option value="Two Pointers">Two Pointers</option>
                    <option value="Sliding Window">Sliding Window</option>
                    <option value="Linked Lists">Linked Lists</option>
                    <option value="Binary Search">Binary Search</option>
                    <option value="Trees">Trees</option>
                    <option value="Graphs">Graphs</option>
                    <option value="Dynamic Programming">Dynamic Programming</option>
                  </select>
                </div>
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Algorithmic Pattern</label>
                <input
                  type="text"
                  placeholder="e.g. Monotonic Stack, Two Pointers"
                  value={newPattern}
                  onChange={(e) => setNewPattern(e.target.value)}
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
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Problem URL</label>
                <input
                  type="url"
                  placeholder="https://leetcode.com/problems/..."
                  value={newUrl}
                  onChange={(e) => setNewUrl(e.target.value)}
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
                <span>Save to DSA Bank</span>
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
