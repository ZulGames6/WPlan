import { useEffect, useState } from "react";
import { api } from "../../lib/api";

interface Jugador {
  id: number;
  nombre: string;
  apellidos?: string | null;
}

interface Asistencia {
  id: number;
  jugadorId: number;
  estado: string;
  nota?: string | null;
}

type Estado = "PRESENTE" | "AUSENTE" | "JUSTIFICADO" | "";

const BOTONES: { estado: Estado; label: string; icon: string; color: string; bg: string }[] = [
  { estado: "PRESENTE",    label: "Presente",    icon: "✓", color: "#059669", bg: "#dcfce7" },
  { estado: "AUSENTE",     label: "Ausente",     icon: "✗", color: "#dc2626", bg: "#fee2e2" },
  { estado: "JUSTIFICADO", label: "Justificado", icon: "~", color: "#d97706", bg: "#fef3c7" },
];

const ESTADO_STYLE: Record<string, { color: string; bg: string }> = {
  PRESENTE:    { color: "#059669", bg: "#dcfce7" },
  AUSENTE:     { color: "#dc2626", bg: "#fee2e2" },
  JUSTIFICADO: { color: "#d97706", bg: "#fef3c7" },
};

export default function AsistenciaBloque({
  planId,
  sesionId,
  editable = true,
}: {
  planId: number;
  sesionId: number;
  editable?: boolean;
}) {
  const [jugadores, setJugadores] = useState<Jugador[]>([]);
  const [estados, setEstados]     = useState<Record<number, Estado>>({});
  const [loading, setLoading]     = useState(true);
  const [lastSaved, setLastSaved] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      try {
        const [js, as] = await Promise.all([
          api.get<Jugador[]>(`/api/planificaciones/${planId}/jugadores`),
          api.get<Asistencia[]>(`/api/planificaciones/${planId}/sesiones/${sesionId}/asistencias`),
        ]);
        if (cancelled) return;
        setJugadores(js);
        const map: Record<number, Estado> = {};
        as.forEach((a) => { map[a.jugadorId] = (a.estado as Estado) || ""; });
        setEstados(map);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, [planId, sesionId]);

  const marcar = async (jugadorId: number, nuevo: Estado) => {
    if (!editable) return;
    const prev = estados[jugadorId] ?? "";
    const next = prev === nuevo ? "" : nuevo;
    setEstados((s) => ({ ...s, [jugadorId]: next }));
    try {
      if (!next) {
        await api.delete(`/api/planificaciones/${planId}/sesiones/${sesionId}/asistencias/${jugadorId}`);
      } else {
        await api.put(`/api/planificaciones/${planId}/sesiones/${sesionId}/asistencias/${jugadorId}`, {
          estado: next, nota: null,
        });
      }
      setLastSaved(new Date().toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" }));
    } catch (e: any) {
      setEstados((s) => ({ ...s, [jugadorId]: prev }));
    }
  };

  if (loading) return <p className="muted">Cargando jugadores…</p>;
  if (jugadores.length === 0) return (
    <p className="muted" style={{ textAlign: "center", padding: "1rem 0" }}>
      No hay jugadores en esta planificación. Añádelos en la pestaña "Jugadores".
    </p>
  );

  const presentes    = Object.values(estados).filter((e) => e === "PRESENTE").length;
  const ausentes     = Object.values(estados).filter((e) => e === "AUSENTE").length;
  const justificados = Object.values(estados).filter((e) => e === "JUSTIFICADO").length;
  const sinMarcar    = jugadores.length - presentes - ausentes - justificados;

  return (
    <div>
      {/* Summary chips */}
      <div className="asistencia-summary">
        <span className="asistencia-chip" style={{ background: "#dcfce7", color: "#059669" }}>
          ✓ {presentes} presentes
        </span>
        <span className="asistencia-chip" style={{ background: "#fee2e2", color: "#dc2626" }}>
          ✗ {ausentes} ausentes
        </span>
        <span className="asistencia-chip" style={{ background: "#fef3c7", color: "#d97706" }}>
          ~ {justificados} justificados
        </span>
        {sinMarcar > 0 && (
          <span className="asistencia-chip" style={{ background: "var(--surface-muted)", color: "var(--muted)" }}>
            · {sinMarcar} sin marcar
          </span>
        )}
        {lastSaved && (
          <span className="asistencia-chip" style={{ background: "var(--surface-muted)", color: "var(--muted)", marginLeft: "auto" }}>
            Guardado {lastSaved}
          </span>
        )}
      </div>

      {/* Player cards */}
      <div className="asistencia-grid">
        {jugadores.map((j) => {
          const estado = estados[j.id] ?? "";
          const style  = estado ? ESTADO_STYLE[estado] : null;
          return (
            <div
              key={j.id}
              className="asistencia-card"
              style={style ? { borderColor: style.color, background: style.bg } : undefined}
            >
              <div className="asistencia-card-name">
                <div
                  className="asistencia-avatar"
                  style={style ? { background: style.color, color: "#fff" } : undefined}
                >
                  {j.nombre.charAt(0).toUpperCase()}
                </div>
                <span>
                  <strong>{j.nombre}</strong>
                  {j.apellidos && <span className="muted"> {j.apellidos}</span>}
                </span>
              </div>
              <div className="asistencia-btns">
                {BOTONES.map(({ estado: e, label, icon, color }) => {
                  const active = estado === e;
                  // En modo lectura: solo se ve el botón del estado activo, sin botones de acción.
                  if (!editable && !active) return null;
                  return (
                    <button
                      key={e}
                      type="button"
                      onClick={() => marcar(j.id, e)}
                      disabled={!editable}
                      className="asistencia-btn"
                      style={{
                        ...(active
                          ? { background: color, color: "#fff", borderColor: color }
                          : { background: "var(--surface)", color: "var(--muted)", borderColor: "var(--border)" }),
                        cursor: editable ? "pointer" : "default",
                      }}
                      title={editable ? label : `${label} (modo visualización)`}
                    >
                      <span className="asistencia-btn-icon">{icon}</span>
                      <span className="asistencia-btn-label">{label}</span>
                    </button>
                  );
                })}
                {!editable && !estado && (
                  <span className="muted" style={{ fontSize: "0.8rem", fontStyle: "italic" }}>
                    Sin marcar
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
