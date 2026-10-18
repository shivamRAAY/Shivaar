import React, { useEffect, useState } from 'react';
import { Sparkles, ArrowRight } from 'lucide-react';

export const SplashScreen: React.FC<{ onComplete: () => void }> = ({ onComplete }) => {
  const [isFading, setIsFading] = useState(false);

  useEffect(() => {
    // Automatically transition to app after 2.8s
    const timer = setTimeout(() => {
      handleProceed();
    }, 2800);

    return () => clearTimeout(timer);
  }, []);

  const handleProceed = () => {
    setIsFading(true);
    setTimeout(() => {
      onComplete();
    }, 400);
  };

  return (
    <div
      onClick={handleProceed}
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 9999,
        background: 'radial-gradient(circle at center, #1E1B4B 0%, #0A0E1A 70%, #030712 100%)',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '24px',
        opacity: isFading ? 0 : 1,
        transition: 'opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
        cursor: 'pointer',
        userSelect: 'none'
      }}
    >
      {/* Background Glowing Ring */}
      <div
        style={{
          position: 'absolute',
          width: '320px',
          height: '320px',
          borderRadius: '50%',
          background: 'radial-gradient(circle, rgba(99, 102, 241, 0.25) 0%, transparent 70%)',
          animation: 'pulseGlow 3s infinite ease-in-out',
          pointerEvents: 'none'
        }}
      />

      {/* Main Logo Emblem */}
      <div
        style={{
          width: '100px',
          height: '100px',
          borderRadius: '26px',
          background: 'linear-gradient(135deg, #1E1B4B 0%, #0B0F19 100%)',
          border: '2px solid rgba(99, 102, 241, 0.5)',
          boxShadow: '0 0 40px rgba(79, 70, 229, 0.45)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          position: 'relative',
          animation: 'fadeIn 0.6s ease-out'
        }}
      >
        <img
          src="./icons/icon-192.svg"
          alt="Shivaar Emblem"
          style={{ width: '70px', height: '70px', filter: 'drop-shadow(0 4px 12px rgba(99, 102, 241, 0.5))' }}
        />
      </div>

      {/* Brand Title */}
      <div style={{ textAlign: 'center', marginTop: '24px', animation: 'fadeIn 0.8s ease-out' }}>
        <h1
          style={{
            fontSize: '32px',
            fontWeight: 900,
            letterSpacing: '3px',
            background: 'linear-gradient(135deg, #FFFFFF 0%, #A5B4FC 50%, #38BDF8 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            textTransform: 'uppercase'
          }}
        >
          SHIVAAR
        </h1>
        <div
          style={{
            fontSize: '11px',
            fontWeight: 700,
            letterSpacing: '2px',
            color: '#F59E0B',
            textTransform: 'uppercase',
            marginTop: '4px'
          }}
        >
          B.Tech CSE Operating System
        </div>
      </div>

      {/* Slogan */}
      <p
        style={{
          fontSize: '13px',
          color: '#94A3B8',
          textAlign: 'center',
          maxWidth: '300px',
          marginTop: '12px',
          lineHeight: '1.5',
          animation: 'fadeIn 1s ease-out'
        }}
      >
        Engineered for B.Tech Excellence • From First Code to First Offer
      </p>

      {/* Quick Launch Button / Tap prompt */}
      <div
        style={{
          marginTop: '40px',
          display: 'flex',
          alignItems: 'center',
          gap: '6px',
          fontSize: '12px',
          color: '#38BDF8',
          fontWeight: 600,
          background: 'rgba(56, 189, 248, 0.1)',
          border: '1px solid rgba(56, 189, 248, 0.25)',
          padding: '8px 16px',
          borderRadius: '20px',
          animation: 'fadeIn 1.2s ease-out'
        }}
      >
        <span>Tap to Launch</span>
        <ArrowRight size={14} />
      </div>
    </div>
  );
};
