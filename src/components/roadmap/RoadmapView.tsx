import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  CheckCircle2, 
  Circle, 
  ShieldCheck, 
  Sparkles, 
  Award, 
  Layers, 
  Sliders, 
  ExternalLink,
  ChevronRight,
  BookOpen
} from 'lucide-react';
import { RoadmapMonth } from '../../types';

export const RoadmapView: React.FC = () => {
  const { state, toggleRoadmapMonth, updateUser, showToast } = useApp();
  const [selectedYear, setSelectedYear] = useState<number>(state.user.currentYear || 1);
  const [isPersonalizeOpen, setIsPersonalizeOpen] = useState(false);

  // Personalized parameters
  const [targetTrack, setTargetTrack] = useState(state.user.targetCareerTrack);
  const [targetCgpa, setTargetCgpa] = useState(state.user.cgpaTarget);
  const [weeklyHours, setWeeklyHours] = useState(state.user.weeklyBudgetHours);

  // Filter months for selected year (Year 1: 1-12, Year 2: 13-24, Year 3: 25-36, Year 4: 37-48)
  const startMonth = (selectedYear - 1) * 12 + 1;
  const endMonth = selectedYear * 12;
  const currentYearMonths = state.roadmapMonths.filter(
    (m) => m.monthNumber >= startMonth && m.monthNumber <= endMonth
  );

  const completedInYear = currentYearMonths.filter((m) => m.isCompleted).length;
  const totalInYear = currentYearMonths.length;
  const yearProgressPercent = totalInYear > 0 ? Math.round((completedInYear / totalInYear) * 100) : 0;

  const getGateRequirement = (year: number) => {
    switch (year) {
      case 1:
        return {
          title: 'Year 1 Foundation Gate',
          reqs: ['Pass Semester 1 & 2 with 8.0+ SGPA', 'Build 1 CLI/System tool in C/C++', 'Solve first 50 basic DSA problems', 'Set up GitHub & clean commit history']
        };
      case 2:
        return {
          title: 'Year 2 Core Engineering Gate',
          reqs: ['Maintain 8.2+ CGPA', '100+ LeetCode problems (Trees, Graphs, DP)', 'Ship 1 Full-Stack project with live URL & database', 'Merge 1 Open Source pull request']
        };
      case 3:
        return {
          title: 'Year 3 Internship & Placement Gate',
          reqs: ['Core CS mastery: OS, DBMS, Computer Networks', '200+ LeetCode problems (Blind 75 & Striver)', 'High-Level System Design (HLD) basics', 'Apply to 30+ summer internship postings']
        };
      case 4:
        return {
          title: 'Year 4 Offer & Graduation Gate',
          reqs: ['Secure 12+ LPA Day 1 / Product SDE Offer', 'Major B.Tech Capstone Project completed', 'Docker & Kubernetes CI/CD deployment', 'Graduate with First Class with Distinction']
        };
      default:
        return { title: 'Engineering Gate', reqs: [] };
    }
  };

  const gate = getGateRequirement(selectedYear);

  const handleSavePersonalization = (e: React.FormEvent) => {
    e.preventDefault();
    updateUser({
      targetCareerTrack: targetTrack,
      cgpaTarget: Number(targetCgpa),
      weeklyBudgetHours: Number(weeklyHours)
    });
    setIsPersonalizeOpen(false);
    showToast('Personalized roadmap adapted to your career goals! 🚀');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
            4-Year B.Tech Master Roadmap
          </h2>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            48-Month Structured Curriculum from First Code to Dream SDE Offer
          </p>
        </div>

        <button
          onClick={() => setIsPersonalizeOpen(true)}
          className="btn-secondary"
          style={{ padding: '6px 10px', fontSize: '11px', display: 'flex', alignItems: 'center', gap: '4px' }}
        >
          <Sliders size={13} color="var(--accent-indigo)" />
          <span>Tune</span>
        </button>
      </div>

      {/* Year Tabs */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr', gap: '8px' }}>
        {[1, 2, 3, 4].map((year) => {
          const isSelected = selectedYear === year;
          return (
            <button
              key={year}
              onClick={() => setSelectedYear(year)}
              style={{
                background: isSelected ? 'var(--accent-indigo)' : 'var(--bg-card)',
                color: isSelected ? '#FFFFFF' : 'var(--text-secondary)',
                border: `1px solid ${isSelected ? 'var(--accent-indigo)' : 'var(--border-subtle)'}`,
                padding: '10px 4px',
                borderRadius: '12px',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '2px',
                cursor: 'pointer',
                transition: 'all 0.15s ease'
              }}
            >
              <span style={{ fontSize: '12px', fontWeight: 700 }}>Year {year}</span>
              <span style={{ fontSize: '10px', opacity: 0.8 }}>Sem {year * 2 - 1} & {year * 2}</span>
            </button>
          );
        })}
      </div>

      {/* Year Progress & Status */}
      <div className="shivaar-card" style={{ padding: '14px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
          <div>
            <span style={{ fontSize: '13px', fontWeight: 700 }}>Year {selectedYear} Milestones</span>
            <span style={{ fontSize: '11px', color: 'var(--text-muted)', marginLeft: '6px' }}>
              ({completedInYear}/{totalInYear} months cleared)
            </span>
          </div>
          <span style={{ fontSize: '13px', fontWeight: 800, color: 'var(--accent-indigo)' }}>
            {yearProgressPercent}%
          </span>
        </div>

        <div style={{ width: '100%', height: '6px', background: 'var(--bg-elevated)', borderRadius: '3px', overflow: 'hidden' }}>
          <div
            style={{
              width: `${yearProgressPercent}%`,
              height: '100%',
              background: 'linear-gradient(90deg, #6366F1, #38BDF8)',
              borderRadius: '3px'
            }}
          />
        </div>
      </div>

      {/* Semester Gate Requirement Card */}
      <div
        className="shivaar-card"
        style={{
          background: 'linear-gradient(135deg, rgba(30, 27, 75, 0.4) 0%, rgba(15, 23, 42, 0.4) 100%)',
          borderColor: 'rgba(99, 102, 241, 0.3)',
          padding: '14px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
          <ShieldCheck size={18} color="#38BDF8" />
          <span style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-primary)' }}>
            {gate.title}
          </span>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          {gate.reqs.map((req, idx) => (
            <div key={idx} style={{ display: 'flex', alignItems: 'flex-start', gap: '6px', fontSize: '12px', color: 'var(--text-secondary)' }}>
              <span style={{ color: '#38BDF8', fontWeight: 700 }}>•</span>
              <span>{req}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Monthly Roadmap Cards */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-secondary)' }}>
          Year {selectedYear} Monthly Milestones
        </div>

        {currentYearMonths.map((m) => (
          <div
            key={m.monthNumber}
            className="shivaar-card"
            style={{
              borderLeft: m.isCompleted ? '4px solid #10B981' : '4px solid var(--border-subtle)',
              opacity: m.isCompleted ? 0.8 : 1
            }}
          >
            <div style={{ display: 'flex', alignItems: 'flex-start', gap: '10px' }}>
              <button
                onClick={() => toggleRoadmapMonth(m.monthNumber)}
                aria-label={m.isCompleted ? 'Mark incomplete' : 'Mark completed'}
                style={{
                  background: 'none',
                  border: 'none',
                  color: m.isCompleted ? '#10B981' : 'var(--text-muted)',
                  cursor: 'pointer',
                  padding: '2px',
                  marginTop: '2px'
                }}
              >
                {m.isCompleted ? <CheckCircle2 size={22} fill="rgba(16, 185, 129, 0.2)" /> : <Circle size={22} />}
              </button>

              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', flexWrap: 'wrap' }}>
                  <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--accent-indigo)' }}>
                    Month {m.monthNumber} • Semester {m.semester}
                  </span>
                  {m.isCompleted && (
                    <span style={{ fontSize: '10px', background: 'rgba(16, 185, 129, 0.15)', color: '#10B981', padding: '1px 6px', borderRadius: '4px', fontWeight: 700 }}>
                      Completed
                    </span>
                  )}
                </div>

                <div style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '2px' }}>
                  {m.title}
                </div>

                <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '4px', lineHeight: '1.4' }}>
                  {m.summary}
                </p>

                {/* Topics Chips */}
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginTop: '8px' }}>
                  {m.keyTopics.map((topic, i) => (
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
                      {topic}
                    </span>
                  ))}
                </div>

                {/* Milestone Project */}
                <div style={{ marginTop: '10px', padding: '8px 10px', background: 'var(--bg-elevated)', borderRadius: '8px', fontSize: '11px' }}>
                  <div style={{ fontWeight: 700, color: '#38BDF8', display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <Layers size={12} />
                    <span>Deliverable Project:</span>
                  </div>
                  <div style={{ color: 'var(--text-primary)', marginTop: '2px' }}>
                    {m.milestoneProject}
                  </div>
                </div>

                {/* Verification Gate */}
                <div style={{ marginTop: '6px', fontSize: '11px', color: 'var(--text-muted)' }}>
                  <span style={{ fontWeight: 700, color: '#F59E0B' }}>Gate Exit Criteria: </span>
                  <span>{m.verificationGate}</span>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Personalization Modal */}
      {isPersonalizeOpen && (
        <div className="modal-overlay" onClick={() => setIsPersonalizeOpen(false)}>
          <div className="modal-sheet" onClick={(e) => e.stopPropagation()}>
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Personalize Engineering Roadmap</h3>
            <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              Tailor milestones and daily priority recommendations to your specific dream career track.
            </p>

            <form onSubmit={handleSavePersonalization} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Target Career Specialization
                </label>
                <select
                  value={targetTrack}
                  onChange={(e) => setTargetTrack(e.target.value)}
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
                  <option value="Full-Stack Engineer">Full-Stack Engineer (React, Node, Cloud)</option>
                  <option value="AI / Machine Learning">AI / Machine Learning Specialist (PyTorch, LLMs)</option>
                  <option value="Cloud & DevOps">Cloud & DevOps Architect (K8s, Terraform, AWS)</option>
                  <option value="Systems & Embedded">Systems & Low-Level Engineer (C++, Rust, OS)</option>
                  <option value="Quant / High-Frequency">Quant / High-Frequency Developer (C++, Math)</option>
                </select>
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  CGPA Target: {targetCgpa}
                </label>
                <input
                  type="range"
                  min="7.0"
                  max="10.0"
                  step="0.1"
                  value={targetCgpa}
                  onChange={(e) => setTargetCgpa(Number(e.target.value))}
                  style={{ width: '100%', marginTop: '4px' }}
                />
              </div>

              <div>
                <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>
                  Weekly Study Hours Budget: {weeklyHours} Hours / week
                </label>
                <input
                  type="range"
                  min="10"
                  max="50"
                  step="5"
                  value={weeklyHours}
                  onChange={(e) => setWeeklyHours(Number(e.target.value))}
                  style={{ width: '100%', marginTop: '4px' }}
                />
              </div>

              <button type="submit" className="btn-primary" style={{ marginTop: '8px' }}>
                <Sparkles size={16} />
                <span>Save Roadmap Configuration</span>
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
