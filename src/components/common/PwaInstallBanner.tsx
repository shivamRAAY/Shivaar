import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Download, X, Sparkles } from 'lucide-react';

export const PwaInstallBanner: React.FC = () => {
  const { isInstallable, triggerInstall } = useApp();
  const [isDismissed, setIsDismissed] = useState(false);

  if (!isInstallable || isDismissed) return null;

  return (
    <div
      style={{
        background: 'linear-gradient(135deg, rgba(79, 70, 229, 0.25) 0%, rgba(124, 58, 237, 0.2) 100%)',
        border: '1px solid rgba(99, 102, 241, 0.4)',
        borderRadius: '16px',
        padding: '12px 14px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '10px',
        boxShadow: '0 8px 20px rgba(0, 0, 0, 0.25)',
        animation: 'fadeIn 0.3s ease-out'
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
        <div
          style={{
            width: '40px',
            height: '40px',
            borderRadius: '10px',
            background: '#0B0F19',
            border: '1px solid #4F46E5',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexShrink: 0
          }}
        >
          <img src="./icons/icon-192.svg" alt="Shivaar" style={{ width: '28px', height: '28px' }} />
        </div>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <span style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-primary)' }}>
              Install Shivaar PWA
            </span>
            <Sparkles size={12} color="#F59E0B" />
          </div>
          <div style={{ fontSize: '11px', color: 'var(--text-secondary)', lineHeight: '1.3' }}>
            Add to Home screen for full offline app experience.
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
        <button
          onClick={triggerInstall}
          style={{
            background: 'linear-gradient(135deg, #4F46E5, #7C3AED)',
            border: 'none',
            color: '#FFFFFF',
            fontSize: '12px',
            fontWeight: 700,
            padding: '7px 12px',
            borderRadius: '10px',
            display: 'flex',
            alignItems: 'center',
            gap: '4px',
            cursor: 'pointer'
          }}
        >
          <Download size={13} />
          <span>Install</span>
        </button>
        <button
          onClick={() => setIsDismissed(true)}
          aria-label="Dismiss banner"
          style={{
            background: 'transparent',
            border: 'none',
            color: 'var(--text-muted)',
            padding: '4px',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center'
          }}
        >
          <X size={16} />
        </button>
      </div>
    </div>
  );
};
