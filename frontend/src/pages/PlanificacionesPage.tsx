import { FormEvent, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../lib/api";
import { Dialog } from "../lib/dialog";
import { useAuth } from "../lib/auth";

interface Planificacion {
  id: number;
  nombre: string;
  fechaInicio?: string | null;
  fechaFin?: string | null;
  notas?: string | null;
}

function saludo() {
  const h = new Date().getHours();
  if (h < 13) return "Buenos días";
  if (h < 20) return "Buenas tardes";
  return "Buenas noches";
}

function formatDate(iso: string) {
  return new Date(iso + "T00:00:00").toLocaleDateString("es-ES", {
    day: "numeric", month: "short", year: "numeric",
  });
}

function planProgress(inicio?: string | null, fin?: string | null): number | null {
  if (!inicio || !fin) return null;
  const start = new Date(inicio).getTime();
  const end   = new Date(fin).getTime();
  const now   = Date.now();
  if (now < start) return 0;
  if (now > end)   return 100;
  return Math.round(((now - start) / (end - start)) * 100);
}

export default function PlanificacionesPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [list, setList]       = useState<Planificacion[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState<string | null>(null);
  const [creating, setCreating] = useState(false);
  const [importingDemo, setImportingDemo] = useState(false);
  const [demoMsg, setDemoMsg] = useState<string | null>(null);

  const importarDemo = async () => {
    const confirma = confirm(
      "Se creará una planificación 'Prueba' con 12 jugadores, 12 semanas de sesiones, " +
      "contenido de natación/gimnasio/waterpolo y asistencias variadas. ¿Continuar?"
    );
    if (!confirma) return;
    setImportingDemo(true);
    setDemoMsg(null);
    try {
      const resp = await api.post<{
        planNumero: number;
        nombre: string;
        jugadores: number;
        sesionesCreadas: number;
        bloquesNatacion: number;
        ejerciciosWaterpolo: number;
        ejerciciosGimnasio: number;
        asistencias: number;
      }>("/api/demo/planificacion", {});
      setDemoMsg(
        `Demo creada: ${resp.sesionesCreadas} sesiones, ${resp.jugadores} jugadores, ` +
        `${resp.bloquesNatacion} bloques de natación, ${resp.ejerciciosWaterpolo + resp.ejerciciosGimnasio} ejercicios, ` +
        `${resp.asistencias} asistencias.`
      );
      // Pequeña pausa para que se vea el mensaje y luego ir a la planificación
      setTimeout(() => navigate(`/planificaciones/${resp.planNumero}/metricas`), 800);
    } catch (e: any) {
      setDemoMsg(`Error: ${e.message || "no se pudo crear la demo"}`);
    } finally {
      setImportingDemo(false);
    }
  };

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await api.get<Planificacion[]>("/api/planificaciones");
      setList(data);
      setError(null);
    } catch (e: any) {
      setError(e.message || "Error cargando planificaciones");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  const nombre = user?.name || user?.email || "Entrenador";

  return (
    <div>
      {/* ── Hero ── */}
      <div className="plans-hero">
        <div className="plans-hero-text">
          <p className="plans-hero-kicker">{saludo()}</p>
          <h1 className="plans-hero-title">{nombre}</h1>
          <p className="plans-hero-sub">
            {list.length === 0
              ? "Crea tu primera planificación para empezar"
              : `Tienes ${list.length} planificación${list.length > 1 ? "es" : ""} activa${list.length > 1 ? "s" : ""}`}
          </p>
        </div>
        <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
          <button
            className="btn-outline plans-hero-btn"
            onClick={importarDemo}
            disabled={importingDemo}
            title="Crea una planificación de prueba con datos ficticios para probar las métricas"
          >
            {importingDemo ? "Importando…" : "Importar demo"}
          </button>
          <button className="primary plans-hero-btn" onClick={() => setCreating(true)}>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
            Nueva planificación
          </button>
        </div>
      </div>
      {demoMsg && (
        <p className={demoMsg.startsWith("Error") ? "error" : "success"} style={{ textAlign: "center" }}>
          {demoMsg}
        </p>
      )}

      {loading && <p className="muted" style={{ textAlign: "center", padding: "2rem" }}>Cargando…</p>}
      {error   && <p className="error">{error}</p>}

      {!loading && list.length === 0 && (
        <div className="plans-empty">
          <div className="plans-empty-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
          </div>
          <h3>Sin planificaciones</h3>
          <p>Organiza la temporada completa con sesiones, asistencia y entrenamientos en un solo lugar.</p>
          <button className="primary" onClick={() => setCreating(true)}>Crear primera planificación</button>
        </div>
      )}

      {list.length > 0 && (
        <div className="plans-grid">
          {list.map((p, i) => {
            const pct = planProgress(p.fechaInicio, p.fechaFin);
            const colors = ["#2563eb","#7c3aed","#0891b2","#059669","#d97706","#dc2626"];
            const color  = colors[i % colors.length];
            return (
              <Link key={p.id} to={`/planificaciones/${p.id}`} className="plan-card" style={{ textDecoration: "none" }}>
                <div className="plan-card-bar" style={{ background: color }} />
                <div className="plan-card-body">
                  <div className="plan-card-top">
                    <h3 className="plan-card-name">{p.nombre}</h3>
                    <span className="plan-card-id" style={{ color }}>#{p.id}</span>
                  </div>
                  {(p.fechaInicio || p.fechaFin) && (
                    <p className="plan-card-dates">
                      {p.fechaInicio ? formatDate(p.fechaInicio) : "?"}
                      <span className="plan-card-arrow-sep">→</span>
                      {p.fechaFin ? formatDate(p.fechaFin) : "?"}
                    </p>
                  )}
                  {p.notas && <p className="plan-card-notes">{p.notas}</p>}
                  {pct !== null && (
                    <div className="plan-card-progress">
                      <div className="plan-card-progress-bar" style={{ width: `${pct}%`, background: color }} />
                    </div>
                  )}
                  {pct !== null && (
                    <p className="plan-card-pct">{pct}% completado</p>
                  )}
                </div>
                <div className="plan-card-chevron">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="9 18 15 12 9 6"/>
                  </svg>
                </div>
              </Link>
            );
          })}
        </div>
      )}

      <CrearPlanificacionDialog
        open={creating}
        onClose={() => setCreating(false)}
        onCreated={() => { setCreating(false); fetchAll(); }}
      />
    </div>
  );
}

function CrearPlanificacionDialog({ open, onClose, onCreated }: {
  open: boolean; onClose: () => void; onCreated: () => void;
}) {
  const [nombre, setNombre]           = useState("");
  const [notas, setNotas]             = useState("");
  const [fechaInicio, setFechaInicio] = useState("");
  const [fechaFin, setFechaFin]       = useState("");
  const [error, setError]             = useState<string | null>(null);
  const [submitting, setSubmitting]   = useState(false);

  useEffect(() => {
    if (!open) { setNombre(""); setNotas(""); setFechaInicio(""); setFechaFin(""); setError(null); }
  }, [open]);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true); setError(null);
    try {
      await api.post("/api/planificaciones", {
        nombre,
        fechaInicio: fechaInicio || null,
        fechaFin:    fechaFin    || null,
        notas:       notas       || null,
      });
      onCreated();
    } catch (err: any) {
      setError(err.message || "Error creando planificación");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} title="Nueva planificación" onClose={onClose} actions={
      <>
        <button onClick={onClose}>Cancelar</button>
        <button className="primary" form="form-crear-plan" type="submit" disabled={submitting}>
          {submitting ? "Creando…" : "Crear"}
        </button>
      </>
    }>
      <form id="form-crear-plan" onSubmit={onSubmit}>
        <div className="row">
          <div>
            <label>Nombre *</label>
            <input required value={nombre} onChange={(e) => setNombre(e.target.value)} placeholder="Ej. Temporada 2025-26" />
          </div>
          <div className="row cols-2">
            <div>
              <label>Fecha inicio</label>
              <input type="date" value={fechaInicio} onChange={(e) => setFechaInicio(e.target.value)} />
            </div>
            <div>
              <label>Fecha fin</label>
              <input type="date" value={fechaFin} onChange={(e) => setFechaFin(e.target.value)} />
            </div>
          </div>
          <div>
            <label>Notas</label>
            <textarea value={notas} onChange={(e) => setNotas(e.target.value)} placeholder="Objetivos de la temporada…" />
          </div>
        </div>
        {error && <p className="error">{error}</p>}
      </form>
    </Dialog>
  );
}
