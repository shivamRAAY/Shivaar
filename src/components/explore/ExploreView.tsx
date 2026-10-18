import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  Globe, 
  Cpu, 
  Cloud, 
  Terminal, 
  ChevronDown, 
  ChevronUp, 
  ExternalLink, 
  Sparkles, 
  CheckCircle2, 
  TrendingUp,
  Compass 
} from 'lucide-react';
import { PUBLIC_ROADMAP_TRACKS } from '../../data/initialData';
import { PublicRoadmapTrack } from '../../types';

export const ExploreView: React.FC = () => {
  const { state, updateUser, showToast } = useApp();
  const [expandedTrackId, setExpandedTrackId] = useState<string>(PUBLIC_ROADMAP_TRACKS[0].id);

  const getTrackIcon = (iconName: string) => {
    switch (iconName) {
      case 'Globe': return <Globe size={20} color="#38BDF8" />;
      case 'Cpu': return <Cpu size={20} color="#F59E0B" />;
      case 'Cloud': return <Cloud size={20} color="#10B981" />;
      case 'Terminal': return <Terminal size={20} color="#A78BFA" />;
      default: return <Compass size={20} color="#6366F1" />;
    }
  };

  const handleAdoptTrack = (track: PublicRoadmapTrack) => {
    updateUser({ targetCareerTrack: track.title });
    showToast(`Adopted ${track.title} as your primary specialization! 🚀`);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Header */}
      <div>
        <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
          Roadmap Library & Specializations
        </h2>
        <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
          Deep-Dive Engineering Tracks Curated for Top-Tier SDE Offers
        </p>
      </div>

      {/* Tracks List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {PUBLIC_ROADMAP_TRACKS.map((track) => {
          const isExpanded = expandedTrackId === track.id;
          const isSelected = state.user.targetCareerTrack === track.title;

          return (
            <div
              key={track.id}
              className="shivaar-card"
              style={{
                border: isSelected ? '1px solid var(--accent-indigo)' : '1px solid var(--border-subtle)',
                background: isSelected ? 'linear-gradient(135deg, rgba(30, 27, 75, 0.4) 0%, var(--bg-card) 100%)' : 'var(--bg-card)'
              }}
            >
              <div
                onClick={() => setExpandedTrackId(isExpanded ? '' : track.id)}
                style={{ cursor: 'pointer' }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div
                      style={{
                        width: '40px',
                        height: '40px',
                        borderRadius: '10px',
                        background: 'var(--bg-elevated)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                    >
                      {getTrackIcon(track.icon)}
                    </div>
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                        <span style={{ fontSize: '15px', fontWeight: 800, color: 'var(--text-primary)' }}>
                          {track.title}
                        </span>
                        {isSelected && (
                          <span style={{ fontSize: '10px', background: 'var(--accent-indigo)', color: '#fff', padding: '1px 6px', borderRadius: '4px', fontWeight: 700 }}>
                            Active Goal
                          </span>
                        )}
                      </div>
                      <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
                        {track.durationMonths} Months • <span style={{ color: '#10B981', fontWeight: 600 }}>{track.averageCTC}</span>
                      </div>
                    </div>
                  </div>

                  <div style={{ color: 'var(--text-muted)' }}>
                    {isExpanded ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                  </div>
                </div>

                <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '8px', lineHeight: '1.4' }}>
                  {track.subtitle}
                </p>

                {/* Tech Tags */}
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginTop: '8px' }}>
                  {track.tags.map((tag, i) => (
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
                      {tag}
                    </span>
                  ))}
                </div>
              </div>

              {/* Expanded Curriculum Phases */}
              {isExpanded && (
                <div
                  style={{
                    marginTop: '14px',
                    paddingTop: '12px',
                    borderTop: '1px solid var(--border-subtle)',
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '12px'
                  }}
                >
                  <div style={{ fontSize: '12px', fontWeight: 700, color: 'var(--accent-indigo)' }}>
                    Curriculum Phases & Free Industry Resources:
                  </div>

                  {track.phases.map((phase) => (
                    <div
                      key={phase.phaseNumber}
                      style={{
                        background: 'var(--bg-elevated)',
                        borderRadius: '10px',
                        padding: '10px 12px',
                        border: '1px solid var(--border-subtle)'
                      }}
                    >
                      <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-primary)' }}>
                        Phase {phase.phaseNumber}: {phase.title}
                      </div>

                      {/* Skills List */}
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginTop: '6px' }}>
                        {phase.skills.map((skill, sIdx) => (
                          <span
                            key={sIdx}
                            style={{
                              fontSize: '10px',
                              padding: '2px 6px',
                              borderRadius: '4px',
                              background: 'rgba(99, 102, 241, 0.1)',
                              color: '#818CF8'
                            }}
                          >
                            ✓ {skill}
                          </span>
                        ))}
                      </div>

                      {/* Free Resources Links */}
                      <div style={{ marginTop: '8px', display: 'flex', flexDirection: 'column', gap: '4px' }}>
                        {phase.resources.map((res, rIdx) => (
                          <a
                            key={rIdx}
                            href={res.url}
                            target="_blank"
                            rel="noopener noreferrer"
                            style={{
                              display: 'flex',
                              alignItems: 'center',
                              gap: '4px',
                              fontSize: '11px',
                              color: '#38BDF8',
                              textDecoration: 'none'
                            }}
                          >
                            <ExternalLink size={11} />
                            <span>{res.name} (100% Free)</span>
                          </a>
                        ))}
                      </div>
                    </div>
                  ))}

                  {/* Adopt Track Action */}
                  <div style={{ marginTop: '6px' }}>
                    {!isSelected ? (
                      <button
                        onClick={() => handleAdoptTrack(track)}
                        className="btn-primary"
                        style={{ width: '100%', padding: '10px' }}
                      >
                        <Sparkles size={16} />
                        <span>Adopt as My Target Career Track</span>
                      </button>
                    ) : (
                      <div style={{ textAlign: 'center', fontSize: '12px', color: '#10B981', fontWeight: 700, padding: '6px' }}>
                        ✓ Currently active in your personalized dashboard
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
