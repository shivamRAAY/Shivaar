import React, { useState } from 'react';
import { useApp } from './context/AppContext';
import { Header } from './components/common/Header';
import { BottomNav } from './components/common/BottomNav';
import { HubModal } from './components/common/HubModal';
import { PwaInstallBanner } from './components/common/PwaInstallBanner';
import { TodayView } from './components/today/TodayView';
import { RoadmapView } from './components/roadmap/RoadmapView';
import { DsaView } from './components/dsa/DsaView';
import { ProjectsView } from './components/projects/ProjectsView';
import { CareerView } from './components/career/CareerView';
import { AcademicsView } from './components/academics/AcademicsView';
import { ExploreView } from './components/explore/ExploreView';
import { AnalyticsView } from './components/analytics/AnalyticsView';
import { SettingsView } from './components/settings/SettingsView';
import { OnboardingWizard } from './components/onboarding/OnboardingWizard';
import { SplashScreen } from './components/splash/SplashScreen';

export const App: React.FC = () => {
  const { state, currentTab, toastMessage } = useApp();
  const [showSplash, setShowSplash] = useState<boolean>(true);
  const [isHubOpen, setIsHubOpen] = useState<boolean>(false);

  // 1. Opening Animation Splash
  if (showSplash) {
    return <SplashScreen onComplete={() => setShowSplash(false)} />;
  }

  // 2. Personalized Onboarding Flow
  if (!state.user.isOnboarded) {
    return <OnboardingWizard />;
  }

  // 3. Main Application Shell
  const renderTabContent = () => {
    switch (currentTab) {
      case 'today':
        return <TodayView />;
      case 'roadmap':
        return <RoadmapView />;
      case 'dsa':
        return <DsaView />;
      case 'projects':
        return <ProjectsView />;
      case 'career':
        return <CareerView />;
      case 'academics':
        return <AcademicsView />;
      case 'explore':
        return <ExploreView />;
      case 'analytics':
        return <AnalyticsView />;
      case 'settings':
        return <SettingsView />;
      default:
        return <TodayView />;
    }
  };

  return (
    <div className="shivaar-app-shell">
      <Header onOpenMenu={() => setIsHubOpen(true)} />

      <main className="shivaar-content">
        <PwaInstallBanner />
        {renderTabContent()}
      </main>

      <BottomNav onOpenHub={() => setIsHubOpen(true)} />

      <HubModal
        isOpen={isHubOpen}
        onClose={() => setIsHubOpen(false)}
        onReplaySplash={() => setShowSplash(true)}
      />

      {toastMessage && (
        <div className="toast-banner">
          <span>{toastMessage}</span>
        </div>
      )}
    </div>
  );
};
