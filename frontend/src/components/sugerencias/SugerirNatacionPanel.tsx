import { FormEvent, useState } from "react";
import { api } from "../../lib/api";

interface BloqueGenerado {
  tipo: string;
  descripcion?: string;
  intensidadAE?: string;
  series?: number;
  metrosPorSerie?: number;
  descansoSeg?: number;
  material?: string;
  metrosTotales?: number;
  cargaEstimada?: number;
  motivo?: string;
}

interface SugerenciaNatResponse {
  objetivo: string;
  volumenTotal: number;
  cargaTotal: number;
  bloques: BloqueGenerado[];
}

const OBJETIVOS: { value: string; label: string }[] = [
  { value: "VELOCIDAD", label: "Velocidad" },
  { value: "POTENCIA_ANAEROBICA_LACTICA", label: "Potencia Anaeróbica Láctica" },
  { value: "CAPACIDAD_ANAEROBICA_LACTICA", label: "Capacidad Anaeróbica Láctica" },
  { value: "UMBRAL_ANAEROBICO_LACTICO", label: "Umbral Anaeróbico Láctico" },
  { value: "POTENCIA_AEROBICA", label: "Potencia Aeróbica" },
  { value: "CAPACIDAD_AEROBICA", label: "Capacidad Aeróbica" },
  { value: "UMBRAL_AEROBICO", label: "Umbral Aeróbico" },
];

const AE_COLORS: Record<string, { bg: string; color: string }> = {
  AE1: { bg: "#dcfce7", color: "#15803d" },
  AE2: { bg: "#dbeafe", color: "#1d4ed8" },
  AE3: { bg: "#fef3c7", color: "#b45309" },
  AE4: { bg: "#fee2e2", color: "#b91c1c" },
  AE5: { bg: "#f3e8ff", color: "#7e22ce" },
};

const TIPO_LABEL: Record<string, string> = {
  CALENTAMIENTO: "Calentamiento",
  TECNICA: "Técnica",
  PIERNAS: "Piernas",
  MATERIAL: "Material",
  PRINCIPAL: "Principal",
  VUELTA_CALMA: "Vuelta a la calma",
};

export default function SugerirNatacionPanel({
  planId,
  sesionId,
  onApplied,
  defaultOpen = false,
}: {
  planId: number;
  sesionId: number;
  onApplied: () => void;
  defaultOpen?: boolean;
}) {
  const [open, setOpen] = useState(defaultOpen);
  const [objetivo, setObjetivo] = useState("POTENCIA_AEROBICA");
  const [metros, setMetros] = useState("2500");
  const [calentamiento, setCalentamiento] = useState(true);
  const [vuelta, setVuelta] = useState(true);
  const [resp, setResp] = useState<SugerenciaNatResponse | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [applying, setApplying] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    setOk(null);
    setResp(null);
    try {
      const r = await api.post<SugerenciaNatResponse>("/api/sugerencias/natacion", {
        objetivo,
        metrosObjetivo: Number(metros),
        conCalentamiento: calentamiento,
        conVueltaCalma: vuelta,
      });
      setResp(r);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error al generar");
    } finally {
      setSubmitting(false);
    }
  };

  const aplicar = async () => {
    if (!resp?.bloques.length) return;
    setApplying(true);
    setError(null);
    try {
      for (const b of resp.bloques) {
        await api.post(
          `/api/planificaciones/${planId}/sesiones/${sesionId}/natacion/bloques`,
          {
            tipoBloque: b.tipo,
            nombre: TIPO_LABEL[b.tipo] ?? b.tipo,
            descripcion: b.descripcion ?? null,
            intensidadAE: b.intensidadAE ?? null,
            series: b.series ?? null,
            metrosPorSerie: b.metrosPorSerie ?? null,
            descansoSeg: b.descansoSeg ?? null,
            material: b.material ?? null,
            notas: b.motivo ?? null,
          }
        );
      }
      setOk(`${resp.bloques.length} bloques creados · ${resp.volumenTotal} m · carga ${resp.cargaTotal.toFixed(1)}`);
      setResp(null);
      setOpen(false);
      onApplied();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error al aplicar");
    } finally {
      setApplying(false);
    }
  };

  return (
    <div className="sugerencia-panel" style={{ marginBottom: "1.25rem" }}>
      <div className="sugerencia-panel-header">
        <div>
          <strong>Asistente de sesión</strong>
          <p className="muted" style={{ margin: "0.2rem 0 0", fontSize: "0.83rem" }}>
            Genera una sesión completa por objetivo y volumen.
          </p>
        </div>
        <button type="button" className={open ? "ghost" : "primary"} onClick={() => setOpen(!open)}>
          {open ? "Ocultar" : "Generar sesión"}
        </button>
      </div>

      {ok && <p className="success" style={{ marginTop: "0.5rem" }}>{ok}</p>}
      {error && <p className="error" style={{ marginTop: "0.5rem" }}>{error}</p>}

      {open && (
        <form onSubmit={onSubmit} className="sugerencia-form">
          <div className="row cols-2" style={{ marginTop: "0.75rem" }}>
            <div>
              <label>Objetivo de la sesión</label>
              <select value={objetivo} onChange={(e) => setObjetivo(e.target.value)}>
                {OBJETIVOS.map((o) => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            </div>
            <div>
              <label>Metros objetivo</label>
              <input
                type="number" min={500} max={8000} step={100}
                value={metros} onChange={(e) => setMetros(e.target.value)}
              />
            </div>
          </div>

          <div style={{ display: "flex", gap: "1rem", marginTop: "0.5rem" }}>
            <label style={{ display: "flex", alignItems: "center", gap: "0.4rem", margin: 0, fontSize: "0.9rem", color: "var(--text)", cursor: "pointer" }}>
              <input type="checkbox" checked={calentamiento} onChange={(e) => setCalentamiento(e.target.checked)} style={{ width: "auto" }} />
              Incluir calentamiento
            </label>
            <label style={{ display: "flex", alignItems: "center", gap: "0.4rem", margin: 0, fontSize: "0.9rem", color: "var(--text)", cursor: "pointer" }}>
              <input type="checkbox" checked={vuelta} onChange={(e) => setVuelta(e.target.checked)} style={{ width: "auto" }} />
              Incluir vuelta a la calma
            </label>
          </div>

          <div className="toolbar" style={{ marginTop: "0.75rem" }}>
            <button type="submit" className="primary" disabled={submitting}>
              {submitting ? "Calculando…" : "Generar"}
            </button>
            {resp && resp.bloques.length > 0 && (
              <button type="button" className="primary" disabled={applying} onClick={aplicar}>
                {applying ? "Aplicando…" : `Aplicar ${resp.bloques.length} bloques a la sesión`}
              </button>
            )}
          </div>

          {resp && (
            <div className="sugerencia-resultados" style={{ marginTop: "0.75rem" }}>
              <p className="muted" style={{ marginBottom: "0.75rem", fontSize: "0.85rem" }}>
                <strong>{OBJETIVOS.find(o => o.value === resp.objetivo)?.label ?? resp.objetivo}</strong>
                {" · "}<strong>{resp.volumenTotal} m</strong>
                {" · carga "}
                <strong>{resp.cargaTotal.toFixed(1)}</strong>
              </p>
              <div style={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}>
                {resp.bloques.map((b, i) => {
                  const aeStyle = b.intensidadAE ? AE_COLORS[b.intensidadAE] ?? {} : {};
                  return (
                    <div key={i} style={{
                      background: "var(--surface)",
                      border: "1px solid var(--border)",
                      borderRadius: "var(--radius-sm)",
                      padding: "0.75rem 1rem",
                    }}>
                      <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginBottom: "0.35rem", flexWrap: "wrap" }}>
                        <span style={{
                          background: "var(--accent-nat-soft)", color: "var(--accent-nat)",
                          fontSize: "0.78rem", fontWeight: 700, padding: "0.15rem 0.5rem",
                          borderRadius: "999px", letterSpacing: "0.04em",
                        }}>
                          {TIPO_LABEL[b.tipo] ?? b.tipo}
                        </span>
                        {b.intensidadAE && (
                          <span style={{
                            ...aeStyle, fontSize: "0.78rem", fontWeight: 700,
                            padding: "0.15rem 0.5rem", borderRadius: "999px",
                          }}>
                            {b.intensidadAE}
                          </span>
                        )}
                        <span style={{ fontSize: "0.85rem", fontWeight: 600, marginLeft: "auto" }}>
                          {b.series && b.metrosPorSerie
                            ? `${b.series}×${b.metrosPorSerie} m`
                            : b.metrosTotales ? `${b.metrosTotales} m` : ""}
                        </span>
                        {b.descansoSeg != null && b.descansoSeg > 0 && (
                          <span className="muted" style={{ fontSize: "0.8rem" }}>{b.descansoSeg}s desc.</span>
                        )}
                        {b.cargaEstimada != null && (
                          <span className="muted" style={{ fontSize: "0.8rem" }}>carga {b.cargaEstimada.toFixed(1)}</span>
                        )}
                      </div>
                      {b.material && (
                        <p style={{ margin: "0 0 0.25rem", fontSize: "0.82rem", color: "var(--accent-nat)", fontWeight: 600 }}>
                          Material: {b.material}
                        </p>
                      )}
                      {b.descripcion && (
                        <p style={{ margin: 0, fontSize: "0.83rem", color: "var(--muted)", lineHeight: 1.4 }}>
                          {b.descripcion}
                        </p>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </form>
      )}
    </div>
  );
}
