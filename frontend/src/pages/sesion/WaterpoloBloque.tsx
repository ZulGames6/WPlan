import { FormEvent, useEffect, useMemo, useState } from "react";
import { api } from "../../lib/api";
import { Dialog } from "../../lib/dialog";

interface Item {
  id: number;
  ejercicioId: number;
  ejercicioNombre: string;
  objetivo?: string;
  categoria?: string;
  intensidad?: string;
  material?: string;
  orden: number;
  duracionMin?: number;
  notas?: string;
}

interface Ejercicio {
  id: number;
  nombre: string;
  objetivo?: string | null;
  categoria?: string | null;
  intensidad?: string | null;
  material?: string | null;
  jugadoresMin?: number | null;
  jugadoresMax?: number | null;
  duracionMinSugerida?: number | null;
  descripcion?: string | null;
}

const CATEGORIAS = [
  { key: "ALL", label: "Todos", emoji: "•" },
  { key: "TECNICA", label: "Técnica", emoji: "🎯" },
  { key: "TACTICA", label: "Táctica", emoji: "♟" },
  { key: "JUEGO", label: "Juego", emoji: "🏆" },
  { key: "FISICA", label: "Físico", emoji: "💪" },
  { key: "OTRO", label: "Otros", emoji: "…" },
] as const;

export default function WaterpoloBloque({
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
    const data = await api.get<Item[]>(`/api/planificaciones/${planId}/sesiones/${sesionId}/waterpolo/ejercicios`);
    setItems(data);
  };

  useEffect(() => { refresh(); /* eslint-disable-next-line */ }, [planId, sesionId]);

  const eliminar = async (it: Item) => {
    if (!confirm(`Quitar ${it.ejercicioNombre} de la sesión?`)) return;
    await api.delete(`/api/planificaciones/${planId}/sesiones/${sesionId}/waterpolo/ejercicios/${it.id}`);
    refresh();
  };

  return (
    <div>
      <div className="toolbar">
        <span className="muted">{items.length} ejercicio(s)</span>
        <div className="spacer" />
        {editable && (
          <button className="primary" onClick={() => setAdding(true)}>Añadir ejercicio</button>
        )}
      </div>
      {items.length === 0 && (
        <p className="muted">{editable ? "Sin ejercicios." : "Sin ejercicios registrados."}</p>
      )}
      {items.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>#</th><th>Nombre</th><th>Categoría</th><th>Intensidad</th><th>Material</th><th>Min</th>
              {editable && <th></th>}
            </tr>
          </thead>
          <tbody>
            {items.map((it) => (
              <tr key={it.id}>
                <td>{it.orden}</td>
                <td>{it.ejercicioNombre}</td>
                <td>{it.categoria ?? ""}</td>
                <td>{it.intensidad ?? ""}</td>
                <td>{it.material ?? ""}</td>
                <td>{it.duracionMin ?? ""}</td>
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
      <AnadirDialog
        open={adding}
        onClose={() => setAdding(false)}
        onSaved={() => { setAdding(false); refresh(); }}
        planId={planId}
        sesionId={sesionId}
      />
    </div>
  );
}

function AnadirDialog({ open, onClose, onSaved, planId, sesionId }: {
  open: boolean; onClose: () => void; onSaved: () => void; planId: number; sesionId: number;
}) {
  const [catalogo, setCatalogo] = useState<Ejercicio[]>([]);
  const [selected, setSelected] = useState<Ejercicio | null>(null);
  const [tab, setTab] = useState<string>("ALL");
  const [query, setQuery] = useState("");
  const [duracionMin, setDuracionMin] = useState<string>("");
  const [notas, setNotas] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setError(null);
    setSelected(null);
    setTab("ALL");
    setQuery("");
    setDuracionMin("");
    setNotas("");
    api.get<Ejercicio[]>("/api/ejercicios/waterpolo").then(setCatalogo).catch((e) => setError(e.message));
  }, [open]);

  // Al elegir ejercicio, prefijar duración con la sugerida
  useEffect(() => {
    if (selected) setDuracionMin(selected.duracionMinSugerida?.toString() ?? "");
  }, [selected]);

  const conteos = useMemo(() => {
    const c: Record<string, number> = { ALL: catalogo.length };
    for (const e of catalogo) {
      const k = (e.categoria ?? "OTRO").toUpperCase();
      c[k] = (c[k] ?? 0) + 1;
    }
    return c;
  }, [catalogo]);

  const filtrados = useMemo(() => {
    const q = query.trim().toLowerCase();
    return catalogo
      .filter((e) => tab === "ALL" || (e.categoria ?? "OTRO").toUpperCase() === tab)
      .filter((e) => !q ||
        e.nombre.toLowerCase().includes(q) ||
        (e.objetivo ?? "").toLowerCase().includes(q) ||
        (e.material ?? "").toLowerCase().includes(q)
      )
      .sort((a, b) => a.nombre.localeCompare(b.nombre));
  }, [catalogo, tab, query]);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!selected) return;
    setSubmitting(true);
    setError(null);
    try {
      await api.post(`/api/planificaciones/${planId}/sesiones/${sesionId}/waterpolo/ejercicios`, {
        ejercicioId: selected.id,
        duracionMin: duracionMin ? Number(duracionMin) : null,
        notas: notas || null,
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
      title="Añadir ejercicio de waterpolo"
      onClose={onClose}
      actions={
        <>
          <button onClick={onClose}>Cancelar</button>
          <button className="primary" form="form-wp-add" type="submit" disabled={submitting || !selected}>
            {submitting ? "Añadiendo…" : selected ? `Añadir "${selected.nombre}"` : "Selecciona un ejercicio"}
          </button>
        </>
      }
    >
      {catalogo.length === 0 ? (
        <p className="muted">No tienes ejercicios de waterpolo. Crea uno en la sección "Ejercicios".</p>
      ) : (
        <form id="form-wp-add" onSubmit={onSubmit}>
          <div className="ej-picker">
            <div className="ej-picker-tabs" role="tablist" aria-label="Categoría de ejercicio">
              {CATEGORIAS.map((c) => (
                <button
                  key={c.key}
                  type="button"
                  role="tab"
                  aria-selected={tab === c.key}
                  className={`ej-tab${tab === c.key ? " is-active" : ""}`}
                  onClick={() => setTab(c.key)}
                >
                  <span className="ej-tab-emoji">{c.emoji}</span>
                  <span>{c.label}</span>
                  <span className="ej-tab-count">{conteos[c.key] ?? 0}</span>
                </button>
              ))}
            </div>

            <div className="ej-picker-search">
              <input
                type="search"
                placeholder="Buscar por nombre, objetivo o material…"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                aria-label="Buscar ejercicio"
              />
            </div>

            <div className="ej-picker-grid">
              {filtrados.length === 0 ? (
                <p className="muted ej-picker-empty">No hay ejercicios en esta categoría.</p>
              ) : (
                filtrados.map((e) => (
                  <button
                    key={e.id}
                    type="button"
                    className={`ej-card${selected?.id === e.id ? " is-selected" : ""}`}
                    onClick={() => setSelected(e)}
                  >
                    <div className="ej-card-head">
                      <strong className="ej-card-title">{e.nombre}</strong>
                      {e.intensidad && <span className={`ej-card-int int-${e.intensidad.toLowerCase()}`}>{e.intensidad}</span>}
                    </div>
                    {e.objetivo && <div className="ej-card-objetivo">{e.objetivo}</div>}
                    <div className="ej-card-meta">
                      {e.categoria && <span className="badge">{e.categoria}</span>}
                      {(e.jugadoresMin || e.jugadoresMax) && (
                        <span className="ej-card-tag">👥 {e.jugadoresMin ?? "?"}–{e.jugadoresMax ?? "?"}</span>
                      )}
                      {e.duracionMinSugerida && <span className="ej-card-tag">⏱ {e.duracionMinSugerida}'</span>}
                      {e.material && <span className="ej-card-tag" title={e.material}>🎒 {e.material.slice(0, 18)}{e.material.length > 18 ? "…" : ""}</span>}
                    </div>
                    {e.descripcion && <div className="ej-card-desc">{e.descripcion}</div>}
                  </button>
                ))
              )}
            </div>
          </div>

          <div className="row cols-2 ej-picker-params">
            <div>
              <label>Duración en sesión (min)</label>
              <input
                type="number"
                min={1}
                max={240}
                value={duracionMin}
                onChange={(e) => setDuracionMin(e.target.value)}
                placeholder={selected?.duracionMinSugerida ? `Sugerido: ${selected.duracionMinSugerida}` : "—"}
              />
            </div>
            <div>
              <label>Notas para esta sesión</label>
              <input value={notas} onChange={(e) => setNotas(e.target.value)} placeholder="Variante, foco, equipos…" />
            </div>
          </div>

          {error && <p className="error">{error}</p>}
        </form>
      )}
    </Dialog>
  );
}
