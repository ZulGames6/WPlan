import { FormEvent, useState } from "react";
import { api } from "../../lib/api";
import ExclusionPicker from "../ExclusionPicker";

export interface SugerenciaGym {
  ejercicioId: number;
  nombre: string;
  grupoMuscular?: string;
  patron?: string;
  equipamiento?: string;
  series?: number;
  repeticiones?: number;
  porcRm?: number;
  descansoSeg?: number;
  puntuacion?: number;
  motivo?: string;
}

const GRUPOS = ["PECHO", "ESPALDA", "HOMBRO", "BICEPS", "TRICEPS", "CUADRICEPS", "ISQUIOS", "GLUTEO", "GEMELO", "ANTEBRAZO", "CORE", "FULL_BODY"];
const EQUIPOS = ["BARRA", "MANCUERNA", "MAQUINA", "POLEA", "KETTLEBELL", "PESO_CORPORAL", "BANDAS"];

const GRUPO_LABEL: Record<string, string> = {
  PECHO: "Pecho", ESPALDA: "Espalda", HOMBRO: "Hombro", BICEPS: "Bíceps",
  TRICEPS: "Tríceps", CUADRICEPS: "Cuádriceps", ISQUIOS: "Isquios",
  GLUTEO: "Glúteo", GEMELO: "Gemelo", ANTEBRAZO: "Antebrazo",
  CORE: "Core", FULL_BODY: "Full Body",
};

const TIPO_LABEL: Record<string, string> = {
  FUERZA: "Fuerza", HIPERTROFIA: "Hipertrofia", POTENCIA: "Potencia",
  RESISTENCIA: "Resistencia", MOVILIDAD: "Movilidad", ACTIVACION: "Activación",
};

export default function SugerirGimnasioPanel({
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
  const [fase, setFase] = useState("PRINCIPAL");
  const [tipo, setTipo] = useState("HIPERTROFIA");
  const [intensidad, setIntensidad] = useState("MEDIA");
  const [grupos, setGrupos] = useState<string[]>(["PECHO", "ESPALDA", "CUADRICEPS"]);
  const [excluirEq, setExcluirEq] = useState<string[]>([]);
  const [max, setMax] = useState("6");
  const [resultado, setResultado] = useState<SugerenciaGym[] | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [applying, setApplying] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);

  const toggle = (arr: string[], v: string) =>
    arr.includes(v) ? arr.filter((x) => x !== v) : [...arr, v];

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    setOk(null);
    setResultado(null);
    try {
      const res = await api.post<{ sugerencias: SugerenciaGym[] }>("/api/sugerencias/gimnasio", {
        fase,
        tipo,
        intensidad,
        grupos,
        excluirEquipamiento: excluirEq,
        maxEjercicios: max ? Number(max) : null,
      });
      setResultado(res.sugerencias);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error al sugerir");
    } finally {
      setSubmitting(false);
    }
  };

  const aplicarASesion = async () => {
    if (!resultado?.length) return;
    setApplying(true);
    setError(null);
    try {
      let orden = 1;
      for (const s of resultado) {
        await api.post(`/api/planificaciones/${planId}/sesiones/${sesionId}/gimnasio/ejercicios`, {
          ejercicioId: s.ejercicioId,
          orden: orden++,
          series: s.series ?? null,
          repeticiones: s.repeticiones ?? null,
          porcRm: s.porcRm ?? null,
          descansoSeg: s.descansoSeg ?? null,
        });
      }
      setOk(`${resultado.length} ejercicio(s) añadidos`);
      setResultado(null);
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
          <strong>Asistente de gimnasio</strong>
          <p className="muted" style={{ margin: "0.2rem 0 0", fontSize: "0.83rem" }}>
            Propone ejercicios de tu catálogo con parámetros según el objetivo.
          </p>
        </div>
        <button
          type="button"
          className={open ? "ghost" : "primary"}
          onClick={() => {
            if (!open) { setResultado(null); setError(null); setOk(null); }
            setOpen(!open);
          }}
        >
          {open ? "Ocultar" : "Sugerir entreno"}
        </button>
      </div>

      {ok && <p className="success" style={{ marginTop: "0.5rem" }}>{ok}</p>}
      {error && <p className="error" style={{ marginTop: "0.5rem" }}>{error}</p>}

      {open && (
        <form onSubmit={onSubmit} className="sugerencia-form">
          <div className="row cols-3" style={{ marginTop: "0.75rem" }}>
            <div>
              <label>Fase</label>
              <select value={fase} onChange={(e) => setFase(e.target.value)}>
                <option value="PRINCIPAL">Principal</option>
                <option value="AUXILIAR">Auxiliar</option>
                <option value="ACTIVACION">Activación</option>
                <option value="RECUPERACION">Recuperación</option>
              </select>
            </div>
            <div>
              <label>Objetivo de la sesión</label>
              <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
                {Object.entries(TIPO_LABEL).map(([v, l]) => (
                  <option key={v} value={v}>{l}</option>
                ))}
              </select>
            </div>
            <div>
              <label>Intensidad</label>
              <select value={intensidad} onChange={(e) => setIntensidad(e.target.value)}>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
              </select>
            </div>
          </div>

          <div style={{ marginTop: "0.6rem" }}>
            <label style={{ marginBottom: "0.4rem", display: "block" }}>Grupos musculares</label>
            <div className="chip-group">
              {GRUPOS.map((g) => (
                <label key={g} className={`chip ${grupos.includes(g) ? "chip-on" : ""}`}>
                  <input type="checkbox" checked={grupos.includes(g)} onChange={() => setGrupos(toggle(grupos, g))} />
                  {GRUPO_LABEL[g] ?? g}
                </label>
              ))}
            </div>
          </div>

          <ExclusionPicker
            label="Sin este material"
            options={EQUIPOS}
            selected={excluirEq}
            onChange={setExcluirEq}
            placeholder="Material no disponible…"
          />

          <div style={{ marginTop: "0.5rem" }}>
            <label>Nº ejercicios</label>
            <input
              type="number" min={1} max={20} value={max}
              onChange={(e) => setMax(e.target.value)}
              style={{ width: 80 }}
            />
          </div>

          <div className="toolbar" style={{ marginTop: "0.75rem" }}>
            <button type="submit" className="primary" disabled={submitting}>
              {submitting ? "Calculando…" : "Generar"}
            </button>
            {resultado && resultado.length > 0 && (
              <button type="button" className="primary" disabled={applying} onClick={aplicarASesion}>
                {applying ? "Aplicando…" : `Añadir ${resultado.length} ejercicios`}
              </button>
            )}
          </div>

          {resultado && (
            <div className="sugerencia-resultados" style={{ marginTop: "0.75rem" }}>
              {resultado.length === 0 ? (
                <p className="muted">Sin coincidencias. Carga el catálogo o ajusta los filtros.</p>
              ) : (
                <div style={{ display: "flex", flexDirection: "column", gap: "0.4rem" }}>
                  {resultado.map((r) => (
                    <div key={r.ejercicioId} style={{
                      background: "var(--surface)",
                      border: "1px solid var(--border)",
                      borderRadius: "var(--radius-sm)",
                      padding: "0.6rem 0.9rem",
                      display: "flex", alignItems: "center", gap: "0.75rem", flexWrap: "wrap",
                    }}>
                      <div style={{ flex: 1, minWidth: 160 }}>
                        <span style={{ fontWeight: 600, fontSize: "0.88rem" }}>{r.nombre}</span>
                        {r.grupoMuscular && (
                          <span className="muted" style={{ marginLeft: "0.5rem", fontSize: "0.78rem" }}>
                            {GRUPO_LABEL[r.grupoMuscular] ?? r.grupoMuscular}
                          </span>
                        )}
                      </div>
                      <span style={{ fontSize: "0.88rem", fontWeight: 600, whiteSpace: "nowrap" }}>
                        {r.series ?? "—"} × {r.repeticiones ?? "—"} reps
                      </span>
                      {r.porcRm != null && (
                        <span style={{
                          background: "var(--primary-muted)", color: "var(--primary)",
                          fontSize: "0.8rem", fontWeight: 700,
                          padding: "0.15rem 0.55rem", borderRadius: "999px",
                          whiteSpace: "nowrap",
                        }}>
                          {r.porcRm}% RM
                        </span>
                      )}
                      {r.descansoSeg != null && (
                        <span className="muted" style={{ fontSize: "0.78rem", whiteSpace: "nowrap" }}>
                          {r.descansoSeg}s desc.
                        </span>
                      )}
                      {r.motivo && (
                        <span className="muted" style={{ fontSize: "0.75rem", width: "100%", marginTop: "0.1rem" }}>
                          {r.motivo}
                        </span>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </form>
      )}
    </div>
  );
}
