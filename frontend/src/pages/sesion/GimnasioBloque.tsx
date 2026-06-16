import { FormEvent, useEffect, useState } from "react";
import { api } from "../../lib/api";
import { Dialog } from "../../lib/dialog";
import SugerirGimnasioPanel from "../../components/sugerencias/SugerirGimnasioPanel";

interface Item {
  id: number;
  ejercicioId: number;
  ejercicioNombre: string;
  grupoMuscular?: string;
  patron?: string;
  equipamiento?: string;
  orden: number;
  series?: number;
  repeticiones?: number;
  pesoKg?: number;
  rir?: number;
  descansoSeg?: number;
  notas?: string;
}

interface Ejercicio {
  id: number;
  nombre: string;
  grupoMuscular?: string | null;
  patron?: string | null;
  equipamiento?: string | null;
  tipo?: string | null;
  unilateral?: boolean | null;
  descripcion?: string | null;
}

const GRUPOS_ORDEN = [
  "PECHO", "ESPALDA", "HOMBRO", "BICEPS", "TRICEPS",
  "CUADRICEPS", "ISQUIOS", "GLUTEO", "GEMELO", "ANTEBRAZO",
  "CORE", "FULL_BODY",
  "PIERNAS", "HOMBROS", "BRAZOS",
];

const GRUPO_LABEL: Record<string, string> = {
  PECHO: "Pecho", ESPALDA: "Espalda", HOMBRO: "Hombro", BICEPS: "Bíceps",
  TRICEPS: "Tríceps", CUADRICEPS: "Cuádriceps", ISQUIOS: "Isquios",
  GLUTEO: "Glúteo", GEMELO: "Gemelo", ANTEBRAZO: "Antebrazo",
  CORE: "Core", FULL_BODY: "Full Body",
  PIERNAS: "Piernas", HOMBROS: "Hombros", BRAZOS: "Brazos",
};

const TIPO_COLOR: Record<string, string> = {
  FUERZA: "#1d4ed8", HIPERTROFIA: "#7c3aed", POTENCIA: "#dc2626",
  RESISTENCIA: "#059669", CORE: "#0891b2", MOVILIDAD: "#d97706",
};

export default function GimnasioBloque({
  planId,
  sesionId,
  editable = true,
}: {
  planId: number;
  sesionId: number;
  editable?: boolean;
}) {
  const [items, setItems] = useState<Item[]>([]);
  const [adding, setAdding] = useState(false);

  const refresh = async () => {
    const data = await api.get<Item[]>(`/api/planificaciones/${planId}/sesiones/${sesionId}/gimnasio/ejercicios`);
    setItems(data);
  };
  useEffect(() => { refresh(); /* eslint-disable-next-line */ }, [planId, sesionId]);

  const eliminar = async (it: Item) => {
    if (!confirm(`¿Quitar "${it.ejercicioNombre}"?`)) return;
    await api.delete(`/api/planificaciones/${planId}/sesiones/${sesionId}/gimnasio/ejercicios/${it.id}`);
    refresh();
  };

  return (
    <div>
      {editable && <SugerirGimnasioPanel planId={planId} sesionId={sesionId} onApplied={refresh} />}
      <div className="toolbar">
        <span className="muted" style={{ fontSize: "0.88rem" }}>{items.length} ejercicio(s)</span>
        <div className="spacer" />
        {editable && (
          <button className="primary" onClick={() => setAdding(true)}>Añadir ejercicio</button>
        )}
      </div>

      {items.length === 0 && (
        <p className="muted" style={{ textAlign: "center", padding: "2rem 0" }}>
          {editable
            ? "Sin ejercicios. Usa el asistente o añade uno manualmente."
            : "Sin ejercicios registrados."}
        </p>
      )}

      {items.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>#</th><th>Nombre</th><th>Grupo</th>
              <th>Series × Reps</th><th>Peso (kg)</th><th>RIR</th><th>Desc (s)</th>
              {editable && <th></th>}
            </tr>
          </thead>
          <tbody>
            {items.map((it) => (
              <tr key={it.id}>
                <td>{it.orden}</td>
                <td><strong>{it.ejercicioNombre}</strong></td>
                <td className="muted" style={{ fontSize: "0.83rem" }}>
                  {GRUPO_LABEL[it.grupoMuscular ?? ""] ?? it.grupoMuscular ?? ""}
                </td>
                <td>{it.series ?? "—"}×{it.repeticiones ?? "—"}</td>
                <td>{it.pesoKg ?? ""}</td>
                <td>{it.rir ?? ""}</td>
                <td>{it.descansoSeg ?? ""}</td>
                {editable && (
                  <td>
                    <button className="ghost" style={{ color: "var(--danger)" }} onClick={() => eliminar(it)}>
                      Quitar
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <AnadirGymDialog
        open={adding}
        onClose={() => setAdding(false)}
        onSaved={() => { setAdding(false); refresh(); }}
        planId={planId}
        sesionId={sesionId}
      />
    </div>
  );
}

function AnadirGymDialog({ open, onClose, onSaved, planId, sesionId }: {
  open: boolean; onClose: () => void; onSaved: () => void; planId: number; sesionId: number;
}) {
  const [catalogo, setCatalogo] = useState<Ejercicio[]>([]);
  const [tab, setTab] = useState("PECHO");
  const [seleccionado, setSeleccionado] = useState<Ejercicio | null>(null);
  const [series, setSeries] = useState("");
  const [reps, setReps] = useState("");
  const [descansoSeg, setDescansoSeg] = useState("");
  const [pesoKg, setPesoKg] = useState("");
  const [rir, setRir] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setError(null);
    setSeleccionado(null);
    setSeries(""); setReps(""); setDescansoSeg(""); setPesoKg(""); setRir("");
    api.get<Ejercicio[]>("/api/ejercicios/gimnasio").then(data => {
      setCatalogo(data);
      if (data.length > 0) {
        const primerGrupo = GRUPOS_ORDEN.find(g => data.some(e => e.grupoMuscular === g));
        if (primerGrupo) setTab(primerGrupo);
      }
    }).catch((e) => setError(e.message));
  }, [open]);

  const tabsDisponibles = GRUPOS_ORDEN.filter(g => catalogo.some(e => e.grupoMuscular === g));
  const ejerciciosTab = catalogo.filter(e => e.grupoMuscular === tab);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!seleccionado) return;
    setSubmitting(true);
    setError(null);
    try {
      await api.post(`/api/planificaciones/${planId}/sesiones/${sesionId}/gimnasio/ejercicios`, {
        ejercicioId: seleccionado.id,
        series: series ? Number(series) : null,
        repeticiones: reps ? Number(reps) : null,
        descansoSeg: descansoSeg ? Number(descansoSeg) : null,
        pesoKg: pesoKg ? Number(pesoKg) : null,
        rir: rir ? Number(rir) : null,
      });
      onSaved();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      title="Añadir ejercicio"
      onClose={onClose}
      actions={
        <>
          <button onClick={onClose}>Cancelar</button>
          <button
            className="primary"
            form="form-gym-visual"
            type="submit"
            disabled={submitting || !seleccionado}
          >
            {submitting ? "Añadiendo…" : seleccionado ? `Añadir "${seleccionado.nombre}"` : "Selecciona un ejercicio"}
          </button>
        </>
      }
    >
      {catalogo.length === 0 && !error ? (
        <p className="muted">No tienes ejercicios de gimnasio. Ve a "Ejercicios" y carga el catálogo.</p>
      ) : (
        <form id="form-gym-visual" onSubmit={onSubmit}>
          {/* Tab bar */}
          <div style={{
            display: "flex", gap: "0.2rem", flexWrap: "wrap",
            marginBottom: "0.75rem", borderBottom: "2px solid var(--border)", paddingBottom: "0.5rem",
          }}>
            {tabsDisponibles.map(g => (
              <button
                key={g}
                type="button"
                onClick={() => { setTab(g); setSeleccionado(null); }}
                style={{
                  border: "none", borderRadius: "var(--radius-sm)",
                  padding: "0.3rem 0.7rem", fontSize: "0.8rem",
                  fontWeight: tab === g ? 700 : 500,
                  background: tab === g ? "var(--primary)" : "var(--surface-muted)",
                  color: tab === g ? "#fff" : "var(--muted)",
                  cursor: "pointer",
                }}
              >
                {GRUPO_LABEL[g] ?? g}
              </button>
            ))}
          </div>

          {/* Exercise grid */}
          <div style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(170px, 1fr))",
            gap: "0.5rem",
            maxHeight: "240px",
            overflowY: "auto",
            marginBottom: "1rem",
          }}>
            {ejerciciosTab.map(ej => {
              const selected = seleccionado?.id === ej.id;
              return (
                <button
                  key={ej.id}
                  type="button"
                  onClick={() => setSeleccionado(selected ? null : ej)}
                  style={{
                    border: selected ? "2px solid var(--primary)" : "1px solid var(--border)",
                    borderRadius: "var(--radius-sm)",
                    background: selected ? "var(--primary-muted)" : "var(--surface)",
                    padding: "0.6rem 0.75rem",
                    textAlign: "left",
                    cursor: "pointer",
                    transition: "all 0.15s",
                  }}
                >
                  <p style={{ margin: "0 0 0.3rem", fontSize: "0.82rem", fontWeight: 600, lineHeight: 1.3, color: selected ? "var(--primary)" : "var(--text)" }}>
                    {ej.nombre}
                  </p>
                  <div style={{ display: "flex", gap: "0.25rem", flexWrap: "wrap" }}>
                    {ej.equipamiento && (
                      <span style={{
                        background: "var(--bg-subtle)", color: "var(--muted)",
                        fontSize: "0.68rem", padding: "0.05rem 0.4rem",
                        borderRadius: "999px", border: "1px solid var(--border)",
                      }}>
                        {ej.equipamiento}
                      </span>
                    )}
                    {ej.unilateral && (
                      <span style={{
                        background: "var(--primary-muted)", color: "var(--primary)",
                        fontSize: "0.68rem", padding: "0.05rem 0.4rem", borderRadius: "999px",
                      }}>
                        1L
                      </span>
                    )}
                  </div>
                </button>
              );
            })}
            {ejerciciosTab.length === 0 && (
              <p className="muted" style={{ fontSize: "0.85rem", gridColumn: "1/-1" }}>Sin ejercicios en este grupo.</p>
            )}
          </div>

          {/* Parameters */}
          {seleccionado && (
            <div style={{ borderTop: "1px solid var(--border)", paddingTop: "0.75rem" }}>
              <p style={{ margin: "0 0 0.6rem", fontSize: "0.85rem", color: "var(--muted)" }}>
                Parámetros para <strong style={{ color: "var(--text)" }}>{seleccionado.nombre}</strong>
              </p>
              <div className="row cols-3">
                <div>
                  <label>Series</label>
                  <input type="number" min={1} max={30} value={series} onChange={e => setSeries(e.target.value)} />
                </div>
                <div>
                  <label>Repeticiones</label>
                  <input type="number" min={1} max={100} value={reps} onChange={e => setReps(e.target.value)} />
                </div>
                <div>
                  <label>Descanso (s)</label>
                  <input type="number" min={0} max={600} value={descansoSeg} onChange={e => setDescansoSeg(e.target.value)} />
                </div>
              </div>
              <div className="row cols-2" style={{ marginTop: "0.5rem" }}>
                <div>
                  <label>Peso (kg) — opcional</label>
                  <input type="number" step="0.5" min={0} max={500} value={pesoKg} onChange={e => setPesoKg(e.target.value)} />
                </div>
                <div>
                  <label>RIR — opcional</label>
                  <input type="number" min={0} max={10} value={rir} onChange={e => setRir(e.target.value)} />
                </div>
              </div>
            </div>
          )}

          {error && <p className="error">{error}</p>}
        </form>
      )}
    </Dialog>
  );
}
