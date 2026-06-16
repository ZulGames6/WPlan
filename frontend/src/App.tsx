import { useState } from "react";
import { NavLink, Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./lib/auth";
import ThemeToggle from "./components/ThemeToggle";
import WPlanLogo from "./components/WPlanLogo";
import ProfileModal from "./components/ProfileModal";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import PlanificacionesPage from "./pages/PlanificacionesPage";
import PlanificacionDetailPage from "./pages/PlanificacionDetailPage";
import EjerciciosPage from "./pages/EjerciciosPage";


function IconCalendar() {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="4" width="18" height="18" rx="2" />
      <line x1="16" y1="2" x2="16" y2="6" />
      <line x1="8" y1="2" x2="8" y2="6" />
      <line x1="3" y1="10" x2="21" y2="10" />
    </svg>
  );
}

function IconDumbbell() {
  return (
    <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M6 4v16M18 4v16" />
      <line x1="2" y1="8" x2="6" y2="8" />
      <line x1="2" y1="16" x2="6" y2="16" />
      <line x1="18" y1="8" x2="22" y2="8" />
      <line x1="18" y1="16" x2="22" y2="16" />
      <line x1="6" y1="12" x2="18" y2="12" />
    </svg>
  );
}


export default function App() {
  const { user, loading, logout } = useAuth();

  if (loading) {
    return (
      <div className="center-page">
        <span className="muted" style={{ fontSize: "0.875rem" }}>Cargando…</span>
      </div>
    );
  }

  if (!user) {
    return (
      <Routes>
        <Route path="/login"    element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="*"         element={<Navigate to="/login" replace />} />
      </Routes>
    );
  }

  const [profileOpen, setProfileOpen] = useState(false);
  const displayName = user.name || user.email;
  const initial = displayName.charAt(0).toUpperCase();

  return (
    <div className="app-shell">
      <aside className="sidebar">

        {/* Brand */}
        <div className="sidebar-brand">
          <WPlanLogo size={34} />
          <div>
            <div className="sidebar-brand-name">WPlan</div>
            <div className="sidebar-brand-sub">Planificación deportiva</div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="sidebar-nav">
          <span className="sidebar-section-label">Menú</span>

          <NavLink
            to="/planificaciones"
            className={({ isActive }) => `sidebar-link${isActive ? " active" : ""}`}
          >
            <IconCalendar />
            Planificaciones
          </NavLink>

          <NavLink
            to="/ejercicios"
            className={({ isActive }) => `sidebar-link${isActive ? " active" : ""}`}
          >
            <IconDumbbell />
            Ejercicios
          </NavLink>
        </nav>

        {/* Footer */}
        <div className="sidebar-footer">
          <button
            type="button"
            className="sidebar-user sidebar-user-btn"
            onClick={() => setProfileOpen(true)}
            title="Editar perfil"
          >
            <div className="sidebar-avatar">{initial}</div>
            <div className="sidebar-user-info">
              <span className="sidebar-user-name" title={displayName}>{displayName}</span>
              <span className="sidebar-user-sub">Editar perfil</span>
            </div>
          </button>
          <div className="sidebar-footer-actions">
            <ThemeToggle />
            <button type="button" className="sidebar-logout" onClick={logout}>
              Salir
            </button>
          </div>
        </div>

      <ProfileModal open={profileOpen} onClose={() => setProfileOpen(false)} />

      </aside>

      <main className="main-content">
        <div className="content">
          <Routes>
            <Route path="/planificaciones"     element={<PlanificacionesPage />} />
            <Route path="/planificaciones/:id/*" element={<PlanificacionDetailPage />} />
            <Route path="/ejercicios/*"        element={<EjerciciosPage />} />
            <Route path="*"                    element={<Navigate to="/planificaciones" replace />} />
          </Routes>
        </div>
      </main>
    </div>
  );
}
