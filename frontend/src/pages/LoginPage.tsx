import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../lib/auth";
import { ApiError } from "../lib/api";
import WPlanLogo from "../components/WPlanLogo";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await login(email, password);
      navigate("/planificaciones");
    } catch (err) {
      setError(
        err instanceof ApiError && err.status === 401
          ? "Email o contraseña incorrectos"
          : "No se ha podido conectar con el servidor"
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-split">
      {/* ── Panel izquierdo ── */}
      <div className="auth-brand-panel">
        <AuthDecor />

        <div className="auth-brand-content">
          <div style={{ marginBottom: "2rem" }}>
            <WPlanLogo size={52} />
          </div>

          <h1 className="auth-brand-name">WPlan</h1>
          <p className="auth-brand-tagline">
            La herramienta de planificación deportiva para entrenadores de waterpolo de élite.
          </p>

          <div className="auth-brand-features">
            {[
              { icon: "📅", text: "Planificaciones de temporada completa" },
              { icon: "🧠", text: "Sugerencias inteligentes con Drools" },
              { icon: "✓",  text: "Control de asistencia en tiempo real" },
              { icon: "💪", text: "Bloques de gimnasio y natación" },
            ].map(({ icon, text }) => (
              <div key={text} className="auth-brand-feature">
                <span className="auth-brand-feature-icon">{icon}</span>
                <span>{text}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* ── Panel derecho ── */}
      <div className="auth-form-panel">
        <div className="auth-form-box">
          <div className="auth-form-header">
            <h2 className="auth-form-title">Bienvenido</h2>
            <p className="auth-form-subtitle">Accede a tu cuenta para continuar</p>
          </div>

          <form onSubmit={onSubmit} className="auth-form-fields">
            <div className="auth-form-field">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                required
                autoFocus
                placeholder="tu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="auth-form-field">
              <label htmlFor="password">Contraseña</label>
              <input
                id="password"
                type="password"
                required
                minLength={6}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            {error && <p className="auth-error">{error}</p>}

            <button type="submit" className="primary auth-submit-btn" disabled={submitting}>
              {submitting ? "Entrando…" : "Entrar"}
            </button>
          </form>

          <p className="auth-switch">
            ¿No tienes cuenta? <Link to="/register">Crear cuenta</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

function AuthDecor() {
  return (
    <>
      {/* Big faint W watermark */}
      <div className="auth-decor-w" aria-hidden="true">W</div>

      {/* Bottom wave */}
      <svg className="auth-decor-wave" viewBox="0 0 600 120" preserveAspectRatio="none" aria-hidden="true">
        <path d="M0,60 C100,100 200,20 300,60 C400,100 500,20 600,60 L600,120 L0,120 Z"
          fill="rgba(37,99,235,0.08)" />
        <path d="M0,80 C150,40 300,110 450,70 C520,55 570,80 600,70 L600,120 L0,120 Z"
          fill="rgba(37,99,235,0.05)" />
      </svg>

      {/* Glow orb */}
      <div className="auth-decor-orb" aria-hidden="true" />
    </>
  );
}
