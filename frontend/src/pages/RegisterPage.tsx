import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../lib/auth";
import { ApiError } from "../lib/api";
import WPlanLogo from "../components/WPlanLogo";

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail]       = useState("");
  const [nombre, setNombre]     = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await register(email, nombre, password);
      navigate("/planificaciones");
    } catch (err) {
      setError(
        err instanceof ApiError && err.status === 409
          ? "Ese email ya está registrado"
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
            Empieza a planificar entrenamientos de waterpolo de forma inteligente y estructurada.
          </p>

          <div className="auth-brand-features">
            {[
              { icon: "🏊", text: "Bloques de natación con intensidad AE" },
              { icon: "🏋️", text: "Catálogo de ejercicios de gimnasio" },
              { icon: "🤽", text: "Sesiones de waterpolo integradas" },
              { icon: "📊", text: "Sugerencias basadas en reglas Drools" },
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
            <h2 className="auth-form-title">Crear cuenta</h2>
            <p className="auth-form-subtitle">Completa los datos para empezar</p>
          </div>

          <form onSubmit={onSubmit} className="auth-form-fields">
            <div className="auth-form-field">
              <label htmlFor="nombre">Nombre</label>
              <input
                id="nombre"
                required
                autoFocus
                placeholder="Tu nombre"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
              />
            </div>
            <div className="auth-form-field">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                required
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
                placeholder="••••••••  (mínimo 6 caracteres)"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            {error && <p className="auth-error">{error}</p>}

            <button type="submit" className="primary auth-submit-btn" disabled={submitting}>
              {submitting ? "Creando cuenta…" : "Crear cuenta"}
            </button>
          </form>

          <p className="auth-switch">
            ¿Ya tienes cuenta? <Link to="/login">Iniciar sesión</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

function AuthDecor() {
  return (
    <>
      <div className="auth-decor-w" aria-hidden="true">W</div>
      <svg className="auth-decor-wave" viewBox="0 0 600 120" preserveAspectRatio="none" aria-hidden="true">
        <path d="M0,60 C100,100 200,20 300,60 C400,100 500,20 600,60 L600,120 L0,120 Z"
          fill="rgba(37,99,235,0.08)" />
        <path d="M0,80 C150,40 300,110 450,70 C520,55 570,80 600,70 L600,120 L0,120 Z"
          fill="rgba(37,99,235,0.05)" />
      </svg>
      <div className="auth-decor-orb" aria-hidden="true" />
    </>
  );
}
