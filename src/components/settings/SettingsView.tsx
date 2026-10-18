import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { 
  Download, 
  Upload, 
  RotateCcw, 
  ShieldCheck, 
  Cloud, 
  Key, 
  Server, 
  Smartphone, 
  Globe, 
  Sun, 
  Moon, 
  CheckCircle2, 
  AlertCircle,
  ExternalLink 
} from 'lucide-react';

export const SettingsView: React.FC = () => {
  const { 
    state, 
    updateUser, 
    toggleTheme, 
    exportBackup, 
    importBackup, 
    resetAllData, 
    showToast 
  } = useApp();

  const [name, setName] = useState(state.user.name);
  const [college, setCollege] = useState(state.user.college);
  const [tier, setTier] = useState(state.user.tier);
  const [year, setYear] = useState(state.user.currentYear);
  const [semester, setSemester] = useState(state.user.currentSemester);
  const [apiKey, setApiKey] = useState(state.user.geminiApiKey || '');

  const [activeGuideTab, setActiveGuideTab] = useState<'pwa' | 'apk' | 'vercel'>('pwa');

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    updateUser({
      name: name.trim(),
      college: college.trim(),
      tier,
      currentYear: Number(year),
      currentSemester: Number(semester),
      geminiApiKey: apiKey.trim() || undefined
    });
    showToast('Profile updated successfully!');
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      importBackup(file);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* Header */}
      <div>
        <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
          Settings, Storage & Deployment
        </h2>
        <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
          Data Persistence, Free-Tier Hosting & Direct Phone Installation
        </p>
      </div>

      {/* Profile Form */}
      <div className="shivaar-card" style={{ padding: '16px' }}>
        <h3 style={{ fontSize: '15px', fontWeight: 700, marginBottom: '12px' }}>Student Profile</h3>

        <form onSubmit={handleSaveProfile} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <div>
            <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Student Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              style={{
                width: '100%',
                padding: '9px 12px',
                borderRadius: '10px',
                background: 'var(--bg-elevated)',
                border: '1px solid var(--border-subtle)',
                color: 'var(--text-primary)',
                fontSize: '13px',
                marginTop: '4px'
              }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '10px' }}>
            <div>
              <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>College / University</label>
              <input
                type="text"
                value={college}
                onChange={(e) => setCollege(e.target.value)}
                style={{
                  width: '100%',
                  padding: '9px 12px',
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
              <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>College Tier</label>
              <select
                value={tier}
                onChange={(e) => setTier(e.target.value as any)}
                style={{
                  width: '100%',
                  padding: '9px 10px',
                  borderRadius: '10px',
                  background: 'var(--bg-elevated)',
                  border: '1px solid var(--border-subtle)',
                  color: 'var(--text-primary)',
                  fontSize: '13px',
                  marginTop: '4px'
                }}
              >
                <option value="Tier 1">Tier 1 (IIT/NIT/BITS)</option>
                <option value="Tier 2">Tier 2 (State Govt/Top Univ)</option>
                <option value="Tier 3">Tier 3 (Affiliated Private)</option>
              </select>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            <div>
              <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Current B.Tech Year</label>
              <select
                value={year}
                onChange={(e) => setYear(Number(e.target.value))}
                style={{
                  width: '100%',
                  padding: '9px 10px',
                  borderRadius: '10px',
                  background: 'var(--bg-elevated)',
                  border: '1px solid var(--border-subtle)',
                  color: 'var(--text-primary)',
                  fontSize: '13px',
                  marginTop: '4px'
                }}
              >
                <option value={1}>Year 1 (Freshman)</option>
                <option value={2}>Year 2 (Sophomore)</option>
                <option value={3}>Year 3 (Pre-final)</option>
                <option value={4}>Year 4 (Final Year)</option>
              </select>
            </div>

            <div>
              <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)' }}>Current Semester</label>
              <select
                value={semester}
                onChange={(e) => setSemester(Number(e.target.value))}
                style={{
                  width: '100%',
                  padding: '9px 10px',
                  borderRadius: '10px',
                  background: 'var(--bg-elevated)',
                  border: '1px solid var(--border-subtle)',
                  color: 'var(--text-primary)',
                  fontSize: '13px',
                  marginTop: '4px'
                }}
              >
                {[1, 2, 3, 4, 5, 6, 7, 8].map((s) => (
                  <option key={s} value={s}>Semester {s}</option>
                ))}
              </select>
            </div>
          </div>

          {/* Gemini API Key */}
          <div style={{ marginTop: '4px' }}>
            <label style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Key size={13} color="#F59E0B" />
              <span>Personal Gemini API Key (Optional Free Tier)</span>
            </label>
            <input
              type="password"
              placeholder="Paste AI Studio API key (stored in local browser only)"
              value={apiKey}
              onChange={(e) => setApiKey(e.target.value)}
              style={{
                width: '100%',
                padding: '9px 12px',
                borderRadius: '10px',
                background: 'var(--bg-elevated)',
                border: '1px solid var(--border-subtle)',
                color: 'var(--text-primary)',
                fontSize: '13px',
                marginTop: '4px'
              }}
            />
            <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '4px' }}>
              Free key available at <a href="https://aistudio.google.com/app/apikey" target="_blank" rel="noopener noreferrer" style={{ color: 'var(--accent-indigo)' }}>aistudio.google.com</a>. If blank, Shivaar uses curated offline engineering mentors with zero fees.
            </div>
          </div>

          <button type="submit" className="btn-primary" style={{ marginTop: '4px' }}>
            <span>Save Profile</span>
          </button>
        </form>
      </div>

      {/* Storage & Persistence Transparency Card */}
      <div className="shivaar-card" style={{ padding: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
          <ShieldCheck size={18} color="#10B981" />
          <h3 style={{ fontSize: '15px', fontWeight: 700 }}>Data Storage & Persistence Status</h3>
        </div>

        <p style={{ fontSize: '12px', color: 'var(--text-secondary)', lineHeight: '1.4' }}>
          <strong>Active Engine:</strong> Local Device Storage (LocalStorage + Service Worker Cache). All task completions, DSA problem solves, flashcard reviews, and projects are persistently saved on your phone and load instantly even when completely offline.
        </p>

        <div style={{ marginTop: '10px', padding: '10px', background: 'var(--bg-elevated)', borderRadius: '10px', fontSize: '11px', color: 'var(--text-muted)' }}>
          <span style={{ fontWeight: 700, color: '#38BDF8' }}>Cloud Synchronization Note: </span>
          Firestore backend rules (<code style={{ color: 'var(--text-primary)' }}>firestore.rules</code>) are included in this project. Because this app is designed to run with $0 hosting and without mandatory paid servers, Shivaar transparently saves your data on-device and provides one-click JSON backup/restore below so you never lose progress.
        </div>

        {/* Backup & Restore Action Buttons */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginTop: '14px' }}>
          <button
            onClick={exportBackup}
            className="btn-secondary"
            style={{ fontSize: '12px', padding: '10px' }}
          >
            <Download size={14} color="#10B981" />
            <span>Download Backup</span>
          </button>

          <label
            className="btn-secondary"
            style={{ fontSize: '12px', padding: '10px', cursor: 'pointer', textAlign: 'center' }}
          >
            <Upload size={14} color="#38BDF8" />
            <span>Restore Backup</span>
            <input
              type="file"
              accept=".json"
              onChange={handleFileUpload}
              style={{ display: 'none' }}
            />
          </label>
        </div>

        <button
          onClick={() => {
            if (confirm('Are you sure you want to reset all tasks and demo progress?')) {
              resetAllData();
            }
          }}
          style={{
            background: 'none',
            border: 'none',
            color: '#EF4444',
            fontSize: '11px',
            fontWeight: 600,
            marginTop: '10px',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '4px'
          }}
        >
          <RotateCcw size={12} />
          <span>Reset to initial demo data</span>
        </button>
      </div>

      {/* Free Hosting & Deployment Guides */}
      <div className="shivaar-card" style={{ padding: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px' }}>
          <Globe size={18} color="var(--accent-indigo)" />
          <h3 style={{ fontSize: '15px', fontWeight: 700 }}>Free-Tier Deployment & Installation</h3>
        </div>

        {/* Guide Switcher Tabs */}
        <div style={{ display: 'flex', gap: '6px', marginBottom: '12px' }}>
          <button
            onClick={() => setActiveGuideTab('pwa')}
            style={{
              flex: 1,
              padding: '6px',
              borderRadius: '8px',
              background: activeGuideTab === 'pwa' ? 'var(--accent-indigo)' : 'var(--bg-elevated)',
              color: activeGuideTab === 'pwa' ? '#fff' : 'var(--text-secondary)',
              border: 'none',
              fontSize: '11px',
              fontWeight: 700,
              cursor: 'pointer'
            }}
          >
            Android PWA Install
          </button>

          <button
            onClick={() => setActiveGuideTab('apk')}
            style={{
              flex: 1,
              padding: '6px',
              borderRadius: '8px',
              background: activeGuideTab === 'apk' ? 'var(--accent-indigo)' : 'var(--bg-elevated)',
              color: activeGuideTab === 'apk' ? '#fff' : 'var(--text-secondary)',
              border: 'none',
              fontSize: '11px',
              fontWeight: 700,
              cursor: 'pointer'
            }}
          >
            Direct APK (No Store)
          </button>

          <button
            onClick={() => setActiveGuideTab('vercel')}
            style={{
              flex: 1,
              padding: '6px',
              borderRadius: '8px',
              background: activeGuideTab === 'vercel' ? 'var(--accent-indigo)' : 'var(--bg-elevated)',
              color: activeGuideTab === 'vercel' ? '#fff' : 'var(--text-secondary)',
              border: 'none',
              fontSize: '11px',
              fontWeight: 700,
              cursor: 'pointer'
            }}
          >
            Free Cloud Hosting
          </button>
        </div>

        {/* PWA Guide */}
        {activeGuideTab === 'pwa' && (
          <div style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '8px', lineHeight: '1.4' }}>
            <p><strong>How to install on Android phone without Google Play Store:</strong></p>
            <ol style={{ paddingLeft: '18px', display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <li>Open the deployed app URL in Google Chrome on your Android phone.</li>
              <li>Tap the three dots (⋮) in the top-right corner of Chrome.</li>
              <li>Select <strong>"Add to Home screen"</strong> (or <strong>"Install app"</strong>).</li>
              <li>Shivaar will install as a native standalone app with its custom launcher icon, working 100% offline via the included Service Worker.</li>
            </ol>
          </div>
        )}

        {/* Direct APK Guide */}
        {activeGuideTab === 'apk' && (
          <div style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '8px', lineHeight: '1.4' }}>
            <p><strong>How to install the native Android APK directly on your phone:</strong></p>
            <ol style={{ paddingLeft: '18px', display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <li>In Google AI Studio, click on the top-right project settings or export menu.</li>
              <li>Choose <strong>"Download APK"</strong> (or <strong>"Export ZIP"</strong>).</li>
              <li>Transfer or download the APK directly on your Android phone.</li>
              <li>Tap the downloaded <code style={{ color: 'var(--accent-indigo)' }}>.apk</code> file and tap <strong>"Install"</strong> (allow "Install unknown apps" from Chrome/Files).</li>
              <li><strong>Zero Google Play Store fee</strong>, zero computer needed, and zero hosting costs!</li>
            </ol>
          </div>
        )}

        {/* Free Cloud Hosting Guide */}
        {activeGuideTab === 'vercel' && (
          <div style={{ fontSize: '12px', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '8px', lineHeight: '1.4' }}>
            <p><strong>Deploy for Free (Vercel, Netlify, or Firebase Hosting):</strong></p>
            <div style={{ padding: '8px 10px', background: 'var(--bg-elevated)', borderRadius: '8px', fontFamily: 'monospace', fontSize: '11px', color: 'var(--text-primary)' }}>
              # 1. Build the production web bundle<br />
              npm run build<br /><br />
              # 2. Deploy to Vercel (100% Free Tier, No Card)<br />
              npx vercel deploy --prod ./dist<br /><br />
              # Or deploy to Netlify Free<br />
              npx netlify deploy --prod --dir=./dist<br /><br />
              # Or Firebase Free Hosting<br />
              npx firebase deploy --only hosting
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
