import { useEffect, useMemo, useState } from "react";
import { useParams, NavLink, Routes, Route, Navigate } from "react-router-dom";
import { api } from "../lib/api";
import JugadoresTab from "./tabs/JugadoresTab";
import SesionesCalendarioTab from "./tabs/SesionesCalendarioTab";
import SesionDetailTab from "./tabs/SesionDetailTab";
import SemanaTab from "./tabs/SemanaTab";
import SemanaActualTab from "./tabs/SemanaActualTab";
import MetricasTab from "./tabs/MetricasTab";

interface PlanResponse {
  id: number;
  nombre: string;
  fechaInicio?: string | null;
  fechaFin?: string | null;
  notas?: string | null;
}

export default function PlanificacionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const planId = useMemo(() => Number(id), [id]);
  const [plan, setPlan] = useState<PlanResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!planId) return;
    let cancelled = false;
    api.get<PlanResponse>(`/api/planificaciones/${planId}`)
      .then((p) => { if (!cancelled) setPlan(p); })
      .catch((e) => { if (!cancelled) setError(e.message || "No se pudo cargar la planificación"); });
    return () => { cancelled = true; };
  }, [planId]);

  if (error) return <p className="error">{error}</p>;
  if (!plan) return <p className="muted">Cargando…</p>;

  return (
    <div className="plan-layout">
      <header className="plan-header">
        <NavLink to="/planificaciones" className="back-link">← Planificaciones</NavLink>
        <h1 className="plan-title">{plan.nombre}</h1>
        {(plan.fechaInicio || plan.fechaFin) && (
          <p className="plan-meta muted">
            {plan.fechaInicio ?? "?"} → {plan.fechaFin ?? "?"}
          </p>
        )}
      </header>

      <nav className="plan-nav">
        <NavLink end to="." className={({ isActive }) => (isActive ? "active" : "")}>
          Esta semana
        </NavLink>
        <NavLink to="calendario" className={({ isActive }) => (isActive ? "active" : "")}>
          Calendario
        </NavLink>
        <NavLink to="horario" className={({ isActive }) => (isActive ? "active" : "")}>
          Horario
        </NavLink>
        <NavLink to="jugadores" className={({ isActive }) => (isActive ? "active" : "")}>
          Jugadores
        </NavLink>
        <NavLink to="metricas" className={({ isActive }) => (isActive ? "active" : "")}>
          Métricas
        </NavLink>
      </nav>

      <Routes>
        <Route index element={<SemanaActualTab planId={planId} />} />
        <Route path="calendario" element={<SesionesCalendarioTab planId={planId} />} />
        <Route path="horario" element={<SemanaTab planId={planId} />} />
        <Route path="sesiones/:sesionId/*" element={<SesionDetailTab planId={planId} />} />
        <Route path="jugadores" element={<JugadoresTab planId={planId} />} />
        <Route path="metricas" element={<MetricasTab planId={planId} />} />
        <Route path="semana" element={<Navigate to="../horario" replace />} />
        <Route path="*" element={<Navigate to="." replace />} />
      </Routes>
    </div>
  );
}
