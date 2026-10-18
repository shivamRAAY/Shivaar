import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  Sparkles, 
  ChevronRight, 
  ChevronLeft, 
  GraduationCap, 
  Target, 
  Code2, 
  Clock, 
  ShieldCheck,
  CheckCircle2 
} from 'lucide-react';
import { UserProfile } from '../../types';

export const OnboardingWizard: React.FC = () => {
  const { completeOnboarding, state } = useApp();
  const [step, setStep] = useState(1);

  // Form state
  const [name, setName] = useState(state.user.name || '');
  const [college, setCollege] = useState(state.user.college || '');
  const [tier, setTier] = useState<UserProfile['tier']>('Tier 2');
  const [year, setYear] = useState(state.user.currentYear || 2);
  const [semester, setSemester] = useState(state.user.currentSemester || 3);
  const [careerTrack, setCareerTrack] = useState(state.user.targetCareerTrack || 'Full-Stack Engineer');
  const [dsaLevel, setDsaLevel] = useState<UserProfile['dsaLevel']>('Intermediate');
  const [weeklyHours, setWeeklyHours] = useState(25);
  const [cgpaTarget, setCgpaTarget] = useState(8.5);

  const totalSteps = 5;

  const handleFinish = () => {
    completeOnboarding({
      name: name.trim() || 'B.Tech Engineer',
      college: college.trim() || 'Engineering College',
      tier,
      currentYear: Number(year),
      currentSemester: Number(semester),
      targetCareerTrack: careerTrack,
      dsaLevel,
      weeklyBudgetHours: Number(weeklyHours),
      cgpaTarget: Number(cgpaTarget)
    });
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        background: 'linear-gradient(180deg, #0A0E1A 0%, #111827 50%, #030712 100%)',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: '24px 20px calc(24px + var(--safe-bottom)) 20px',
        color: '#F8FAFC'
      }}
    >
      {/* Top Header & Progress */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div
              style={{
                width: '32px',
                height: '32px',
                borderRadius: '8px',
                background: 'linear-gradient(135deg, #4F46E5, #7C3AED)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 800,
                fontSize: '14px'
              }}
            >
              S
            </div>
            <span style={{ fontWeight: 800, letterSpacing: '0.5px', fontSize: '15px' }}>
              SHIVAAR ONBOARDING
            </span>
          </div>

          <span style={{ fontSize: '12px', color: '#94A3B8', fontWeight: 600 }}>
            Step {step} of {totalSteps}
          </span>
        </div>

        {/* Step Indicator Bar */}
        <div style={{ width: '100%', height: '4px', background: '#1F2A44', borderRadius: '2px', overflow: 'hidden', marginTop: '12px' }}>
          <div
            style={{
              width: `${(step / totalSteps) * 100}%`,
              height: '100%',
              background: 'linear-gradient(90deg, #4F46E5, #06B6D4)',
              transition: 'width 0.3s ease'
            }}
          />
        </div>
      </div>

      {/* Main Step Content */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', margin: '30px 0' }}>
        {/* STEP 1: Student Identity */}
        {step === 1 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', animation: 'fadeIn 0.25s ease' }}>
            <div>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#38BDF8', textTransform: 'uppercase' }}>
                Phase 1: Student Identity
              </div>
              <h2 style={{ fontSize: '24px', fontWeight: 800, marginTop: '4px' }}>
                What should we call you?
              </h2>
              <p style={{ fontSize: '13px', color: '#94A3B8', marginTop: '4px' }}>
                Personalize your Shivaar academic and placement operating system.
              </p>
            </div>

            <div>
              <label style={{ fontSize: '12px', fontWeight: 600, color: '#CBD5E1' }}>Full Name</label>
              <input
                type="text"
                placeholder="e.g. Shivam Yadav"
                value={name}
                onChange={(e) => setName(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 14px',
                  borderRadius: '12px',
                  background: '#182035',
                  border: '1px solid #24304F',
                  color: '#FFFFFF',
                  fontSize: '15px',
                  marginTop: '6px'
                }}
              />
            </div>

            <div>
              <label style={{ fontSize: '12px', fontWeight: 600, color: '#CBD5E1' }}>College / Institute</label>
              <input
                type="text"
                placeholder="e.g. National Institute of Technology"
                value={college}
                onChange={(e) => setCollege(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 14px',
                  borderRadius: '12px',
                  background: '#182035',
                  border: '1px solid #24304F',
                  color: '#FFFFFF',
                  fontSize: '15px',
                  marginTop: '6px'
                }}
              />
            </div>

            <div>
              <label style={{ fontSize: '12px', fontWeight: 600, color: '#CBD5E1' }}>College Tier</label>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px', marginTop: '6px' }}>
                {(['Tier 1', 'Tier 2', 'Tier 3'] as const).map((t) => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => setTier(t)}
                    style={{
                      padding: '10px 6px',
                      borderRadius: '10px',
                      border: `1px solid ${tier === t ? '#4F46E5' : '#24304F'}`,
                      background: tier === t ? 'rgba(79, 70, 229, 0.25)' : '#182035',
                      color: tier === t ? '#FFFFFF' : '#94A3B8',
                      fontSize: '12px',
                      fontWeight: 700,
                      cursor: 'pointer'
                    }}
                  >
                    {t}
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* STEP 2: Current Semester & Target CGPA */}
        {step === 2 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', animation: 'fadeIn 0.25s ease' }}>
            <div>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#10B981', textTransform: 'uppercase' }}>
                Phase 2: Academic Standing
              </div>
              <h2 style={{ fontSize: '24px', fontWeight: 800, marginTop: '4px' }}>
                Where are you in B.Tech?
              </h2>
              <p style={{ fontSize: '13px', color: '#94A3B8', marginTop: '4px' }}>
                Shivaar aligns its 48-month syllabus gates to your current semester.
              </p>
            </div>

            <div>
              <label style={{ fontSize: '12px', fontWeight: 600, color: '#CBD5E1' }}>Current Semester</label>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px', marginTop: '6px' }}>
                {[1, 2, 3, 4, 5, 6, 7, 8].map((s) => (
                  <button
                    key={s}
                    type="button"
                    onClick={() => {
                      setSemester(s);
                      setYear(Math.ceil(s / 2));
                    }}
                    style={{
                      padding: '12px 6px',
                      borderRadius: '10px',
                      border: `1px solid ${semester === s ? '#10B981' : '#24304F'}`,
                      background: semester === s ? 'rgba(16, 185, 129, 0.25)' : '#182035',
                      color: semester === s ? '#FFFFFF' : '#94A3B8',
                      fontSize: '13px',
                      fontWeight: 700,
                      cursor: 'pointer'
                    }}
                  >
                    Sem {s}
                  </button>
                ))}
              </div>
            </div>

            <div style={{ marginTop: '8px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#CBD5E1' }}>
                <span>Target CGPA:</span>
                <span style={{ fontWeight: 800, color: '#10B981', fontSize: '14px' }}>{cgpaTarget} CGPA</span>
              </div>
              <input
                type="range"
                min="7.0"
                max="10.0"
                step="0.1"
                value={cgpaTarget}
                onChange={(e) => setCgpaTarget(Number(e.target.value))}
                style={{ width: '100%', marginTop: '6px' }}
              />
              <p style={{ fontSize: '11px', color: '#64748B', marginTop: '4px' }}>
                8.5+ CGPA automatically clears cutoff criteria for Google, Microsoft, and Goldman Sachs.
              </p>
            </div>
          </div>
        )}

        {/* STEP 3: Dream Specialization */}
        {step === 3 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', animation: 'fadeIn 0.25s ease' }}>
            <div>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#F59E0B', textTransform: 'uppercase' }}>
                Phase 3: Career Direction
              </div>
              <h2 style={{ fontSize: '24px', fontWeight: 800, marginTop: '4px' }}>
                Your Dream Specialization
              </h2>
              <p style={{ fontSize: '13px', color: '#94A3B8', marginTop: '4px' }}>
                Select your North Star engineering discipline.
              </p>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {[
                { id: 'Full-Stack Engineer', label: 'Full-Stack Web Engineer', desc: 'React 19, TypeScript, Node.js, PostgreSQL, Docker' },
                { id: 'AI / Machine Learning', label: 'AI / Machine Learning Specialist', desc: 'PyTorch, Neural Networks, Transformers, LLM fine-tuning' },
                { id: 'Cloud & DevOps', label: 'Cloud & DevOps Architect', desc: 'Linux kernel, Kubernetes, Terraform, AWS, CI/CD' },
                { id: 'Systems & Embedded', label: 'Systems & Low-Level Engineer', desc: 'C++, Rust, Concurrency, Operating Systems, Compilers' },
                { id: 'Quant / High-Frequency', label: 'Quant & High-Frequency Developer', desc: 'Ultra-low latency C++, Stochastic Math, Order Books' }
              ].map((item) => (
                <div
                  key={item.id}
                  onClick={() => setCareerTrack(item.id)}
                  style={{
                    padding: '12px 14px',
                    borderRadius: '12px',
                    border: `1px solid ${careerTrack === item.id ? '#F59E0B' : '#24304F'}`,
                    background: careerTrack === item.id ? 'rgba(245, 158, 11, 0.15)' : '#182035',
                    cursor: 'pointer'
                  }}
                >
                  <div style={{ fontSize: '14px', fontWeight: 700, color: careerTrack === item.id ? '#FFFFFF' : '#CBD5E1' }}>
                    {item.label}
                  </div>
                  <div style={{ fontSize: '11px', color: '#94A3B8', marginTop: '2px' }}>
                    {item.desc}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* STEP 4: Current DSA Level */}
        {step === 4 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', animation: 'fadeIn 0.25s ease' }}>
            <div>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#A78BFA', textTransform: 'uppercase' }}>
                Phase 4: DSA Baseline
              </div>
              <h2 style={{ fontSize: '24px', fontWeight: 800, marginTop: '4px' }}>
                Current Coding Experience
              </h2>
              <p style={{ fontSize: '13px', color: '#94A3B8', marginTop: '4px' }}>
                We calibrate daily problem recommendations to your current comfort level.
              </p>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {[
                { id: 'Beginner', label: 'Absolute Beginner / Learning C/C++', desc: 'Comfortable with loops and arrays, but new to pointers and recursion.' },
                { id: 'Intermediate', label: 'Intermediate (30-100 LeetCode solved)', desc: 'Solved easy arrays/strings; currently mastering Trees, Graphs & DP.' },
                { id: 'Advanced', label: 'Advanced (150+ LeetCode / Contestant)', desc: 'Fluently solve Mediums in 20 minutes; prepping for super-tier OAs.' }
              ].map((item) => (
                <div
                  key={item.id}
                  onClick={() => setDsaLevel(item.id as any)}
                  style={{
                    padding: '12px 14px',
                    borderRadius: '12px',
                    border: `1px solid ${dsaLevel === item.id ? '#A78BFA' : '#24304F'}`,
                    background: dsaLevel === item.id ? 'rgba(124, 58, 237, 0.2)' : '#182035',
                    cursor: 'pointer'
                  }}
                >
                  <div style={{ fontSize: '14px', fontWeight: 700, color: dsaLevel === item.id ? '#FFFFFF' : '#CBD5E1' }}>
                    {item.label}
                  </div>
                  <div style={{ fontSize: '11px', color: '#94A3B8', marginTop: '2px' }}>
                    {item.desc}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* STEP 5: Commitment Budget & Launch */}
        {step === 5 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', animation: 'fadeIn 0.25s ease' }}>
            <div>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#38BDF8', textTransform: 'uppercase' }}>
                Phase 5: Commitment Protocol
              </div>
              <h2 style={{ fontSize: '24px', fontWeight: 800, marginTop: '4px' }}>
                Weekly Engineering Budget
              </h2>
              <p style={{ fontSize: '13px', color: '#94A3B8', marginTop: '4px' }}>
                Real placement transformation requires consistent weekly deliberate practice.
              </p>
            </div>

            <div style={{ padding: '16px', borderRadius: '14px', background: '#182035', border: '1px solid #24304F' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ fontSize: '13px', fontWeight: 600 }}>Target Weekly Study Time:</span>
                <span style={{ fontSize: '20px', fontWeight: 800, color: '#38BDF8' }}>{weeklyHours} hrs / week</span>
              </div>
              <input
                type="range"
                min="10"
                max="50"
                step="5"
                value={weeklyHours}
                onChange={(e) => setWeeklyHours(Number(e.target.value))}
                style={{ width: '100%', marginTop: '12px' }}
              />
              <div style={{ fontSize: '11px', color: '#94A3B8', marginTop: '8px' }}>
                Roughly {(weeklyHours / 7).toFixed(1)} hours of deep work per day.
              </div>
            </div>

            {/* Profile Recap Summary */}
            <div style={{ padding: '14px', borderRadius: '12px', background: 'rgba(79, 70, 229, 0.15)', border: '1px solid rgba(79, 70, 229, 0.3)' }}>
              <div style={{ fontSize: '12px', fontWeight: 700, color: '#818CF8' }}>Profile Initialized:</div>
              <div style={{ fontSize: '13px', marginTop: '4px', color: '#FFFFFF' }}>
                <strong>{name || 'Engineer'}</strong> • Semester {semester} • {careerTrack}
              </div>
              <div style={{ fontSize: '11px', color: '#94A3B8', marginTop: '2px' }}>
                4-Year Roadmap, Striver DSA problem bank, and Today Planner will be configured.
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Bottom Navigation Buttons */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '12px' }}>
        {step > 1 ? (
          <button
            onClick={() => setStep(step - 1)}
            className="btn-secondary"
            style={{ padding: '12px 18px', minWidth: '90px' }}
          >
            <ChevronLeft size={16} />
            <span>Back</span>
          </button>
        ) : <div />}

        {step < totalSteps ? (
          <button
            onClick={() => setStep(step + 1)}
            className="btn-primary"
            style={{ padding: '12px 24px', flex: 1 }}
          >
            <span>Continue</span>
            <ChevronRight size={16} />
          </button>
        ) : (
          <button
            onClick={handleFinish}
            className="btn-primary"
            style={{
              padding: '14px 24px',
              flex: 1,
              background: 'linear-gradient(135deg, #10B981, #06B6D4)'
            }}
          >
            <Sparkles size={16} />
            <span>Launch My Shivaar OS</span>
          </button>
        )}
      </div>
    </div>
  );
};
