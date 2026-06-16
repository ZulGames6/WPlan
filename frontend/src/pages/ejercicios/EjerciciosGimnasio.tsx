import { FormEvent, useEffect, useState } from "react";
import { api } from "../../lib/api";
import { Dialog } from "../../lib/dialog";

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
  // legacy names from older catalog entries
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
  ACTIVACION: "#64748b",
};

const EQUIPO_COLOR: Record<string, string> = {
  BARRA: "#374151", MANCUERNA: "#1e40af", MAQUINA: "#065f46",
  POLEA: "#7c3aed", KETTLEBELL: "#9a3412", PESO_CORPORAL: "#134e4a",
  BANDAS: "#92400e", OTRO: "#64748b",
};

const ALL_GRUPOS = ["PECHO","ESPALDA","HOMBRO","BICEPS","TRICEPS","CUADRICEPS","ISQUIOS","GLUTEO","GEMELO","ANTEBRAZO","CORE","FULL_BODY"];
const ALL_PATRONES = ["EMPUJE_HORIZONTAL","EMPUJE_VERTICAL","TRACCION_HORIZONTAL","TRACCION_VERTICAL","BISAGRA","SENTADILLA","ZANCADA","ROTACION","ESTABILIDAD","GENERAL"];
const ALL_EQUIPAMIENTO = ["BARRA","MANCUERNA","MAQUINA","POLEA","KETTLEBELL","PESO_CORPORAL","BANDAS","OTRO"];
const ALL_TIPOS = ["FUERZA","HIPERTROFIA","POTENCIA","RESISTENCIA","MOVILIDAD","ACTIVACION","CORE"];

export default function EjerciciosGimnasio() {
  const [list, setList] = useState<Ejercicio[]>([]);
  const [editing, setEditing] = useState<Ejercicio | null>(null);
  const [creating, setCreating] = useState(false);
  const [tab, setTab] = useState<string>("PECHO");
  const [search, setSearch] = useState("");

  const refresh = async () => setList(await api.get<Ejercicio[]>("/api/ejercicios/gimnasio"));
  useEffect(() => { refresh(); }, []);

  // Build available tabs from exercises
  const tabsDisponibles = GRUPOS_ORDEN.filter(g =>
    list.some(e => e.grupoMuscular === g)
  );

  // Select first available tab if current not available
  useEffect(() => {
    if (tabsDisponibles.length > 0 && !tabsDisponibles.includes(tab)) {
      setTab(tabsDisponibles[0]);
    }
  }, [tabsDisponibles, tab]);

  const ejerciciosTab = list.filter(e =>
    e.grupoMuscular === tab &&
    (!search || e.nombre.toLowerCase().includes(search.toLowerCase()))
  );

  const eliminar = async (e: Ejercicio) => {
    if (!confirm(`Eliminar "${e.nombre}"?`)) return;
    await api.delete(`/api/ejercicios/gimnasio/${e.id}`);
    refresh();
  };

  return (
    <div>
      <div className="toolbar" style={{ marginBottom: "1rem" }}>
        <span className="muted" style={{ fontSize: "0.88rem" }}>{list.length} ejercicios</span>
        <div className="spacer" />
        <input
          type="search"
          placeholder="Buscar…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: 180, padding: "0.4rem 0.7rem" }}
        />
        <button className="primary" onClick={() => setCreating(true)}>+ Nuevo</button>
      </div>

      {/* Tabs */}
      <div style={{ display: "flex", gap: "0.25rem", flexWrap: "wrap", marginBottom: "1rem", borderBottom: "2px solid var(--border)", paddingBottom: "0.5rem" }}>
        {tabsDisponibles.map(g => (
          <button
            key={g}
            onClick={() => setTab(g)}
            style={{
              border: "none",
              borderRadius: "var(--radius-sm)",
              padding: "0.35rem 0.85rem",
              fontSize: "0.83rem",
              fontWeight: tab === g ? 700 : 500,
              background: tab === g ? "var(--primary)" : "var(--surface)",
              color: tab === g ? "#fff" : "var(--muted)",
              cursor: "pointer",
              transition: "all 0.15s",
            }}
          >
            {GRUPO_LABEL[g] ?? g}
            <span style={{ marginLeft: "0.4rem", fontSize: "0.75rem", opacity: 0.8 }}>
              ({list.filter(e => e.grupoMuscular === g).length})
            </span>
          </button>
        ))}
      </div>

      {ejerciciosTab.length === 0 ? (
        <p className="muted" style={{ textAlign: "center", padding: "2rem" }}>
          {search ? "Sin resultados." : "No hay ejercicios en este grupo."}
        </p>
      ) : (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(240px, 1fr))", gap: "0.75rem" }}>
          {ejerciciosTab.map(e => (
            <div key={e.id} className="card" style={{ marginBottom: 0, padding: "1rem 1.1rem" }}>
              <p style={{ margin: "0 0 0.5rem", fontWeight: 600, fontSize: "0.9rem", lineHeight: 1.3 }}>
                {e.nombre}
              </p>
              <div style={{ display: "flex", gap: "0.35rem", flexWrap: "wrap", marginBottom: "0.5rem" }}>
                {e.equipamiento && (
                  <span style={{
                    background: EQUIPO_COLOR[e.equipamiento] ?? "#64748b",
                    color: "#fff", fontSize: "0.72rem", fontWeight: 600,
                    padding: "0.1rem 0.45rem", borderRadius: "999px",
                  }}>
                    {e.equipamiento}
                  </span>
                )}
                {e.patron && (
                  <span style={{
                    background: "var(--bg-subtle)", color: "var(--muted)",
                    fontSize: "0.72rem", padding: "0.1rem 0.45rem", borderRadius: "999px",
                    border: "1px solid var(--border)",
                  }}>
                    {e.patron.replace(/_/g, " ")}
                  </span>
                )}
                {e.unilateral && (
                  <span style={{
                    background: "var(--primary-muted)", color: "var(--primary)",
                    fontSize: "0.72rem", padding: "0.1rem 0.45rem", borderRadius: "999px",
                    border: "1px solid var(--primary-soft)",
                  }}>
                    Unilateral
                  </span>
                )}
              </div>
              {e.descripcion && (
                <p className="muted" style={{ margin: "0 0 0.6rem", fontSize: "0.78rem", lineHeight: 1.35 }}>
                  {e.descripcion}
                </p>
              )}
              <div style={{ display: "flex", gap: "0.5rem", marginTop: "auto" }}>
                <button className="ghost" style={{ fontSize: "0.8rem", padding: "0.2rem 0.5rem" }} onClick={() => setEditing(e)}>
                  Editar
                </button>
                <button className="ghost" style={{ fontSize: "0.8rem", padding: "0.2rem 0.5rem", color: "var(--danger)" }} onClick={() => eliminar(e)}>
                  Borrar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <Editor
        open={creating}
        title="Nuevo ejercicio de gimnasio"
        initial={null}
        onClose={() => setCreating(false)}
        onSave={async (p) => { await api.post("/api/ejercicios/gimnasio", p); setCreating(false); refresh(); }}
      />
      <Editor
        open={!!editing}
        title="Editar ejercicio"
        initial={editing}
        onClose={() => setEditing(null)}
        onSave={async (p) => { if (!editing) return; await api.put(`/api/ejercicios/gimnasio/${editing.id}`, p); setEditing(null); refresh(); }}
      />
    </div>
  );
}

function Editor({ open, title, initial, onClose, onSave }: {
  open: boolean; title: string; initial: Ejercicio | null;
  onClose: () => void; onSave: (p: Partial<Ejercicio>) => Promise<void>;
}) {
  const [form, setForm] = useState<Partial<Ejercicio>>({});
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => { setForm(initial ?? {}); setError(null); }, [initial, open]);

  const set = (k: keyof Ejercicio, v: unknown) => setForm(f => ({ ...f, [k]: v }));

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true); setError(null);
    try {
      await onSave({
        nombre: form.nombre ?? "",
        grupoMuscular: form.grupoMuscular || null,
        patron: form.patron || null,
        equipamiento: form.equipamiento || null,
        tipo: form.tipo || null,
        unilateral: form.unilateral ?? false,
        descripcion: form.descripcion || null,
      });
    } catch (err: any) { setError(err.message); } finally { setSubmitting(false); }
  };

  return (
    <Dialog open={open} title={title} onClose={onClose} actions={
      <>
        <button onClick={onClose}>Cancelar</button>
        <button className="primary" form="form-gym-cat" type="submit" disabled={submitting}>
          {submitting ? "Guardando…" : "Guardar"}
        </button>
      </>
    }>
      <form id="form-gym-cat" onSubmit={onSubmit}>
        <div className="row">
          <div>
            <label>Nombre *</label>
            <input required value={form.nombre ?? ""} onChange={(e) => set("nombre", e.target.value)} />
          </div>
          <div className="row cols-3">
            <div>
              <label>Grupo muscular</label>
              <select value={form.grupoMuscular ?? ""} onChange={(e) => set("grupoMuscular", e.target.value)}>
                <option value="">—</option>
                {ALL_GRUPOS.map((g) => <option key={g} value={g}>{GRUPO_LABEL[g] ?? g}</option>)}
              </select>
            </div>
            <div>
              <label>Patrón</label>
              <select value={form.patron ?? ""} onChange={(e) => set("patron", e.target.value)}>
                <option value="">—</option>
                {ALL_PATRONES.map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
            </div>
            <div>
              <label>Equipamiento</label>
              <select value={form.equipamiento ?? ""} onChange={(e) => set("equipamiento", e.target.value)}>
                <option value="">—</option>
                {ALL_EQUIPAMIENTO.map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
            </div>
          </div>
          <div>
            <label>Unilateral</label>
            <select value={String(form.unilateral ?? false)} onChange={(e) => set("unilateral", e.target.value === "true")}>
              <option value="false">No</option>
              <option value="true">Sí</option>
            </select>
          </div>
          <div>
            <label>Descripción</label>
            <textarea value={form.descripcion ?? ""} onChange={(e) => set("descripcion", e.target.value)} />
          </div>
        </div>
        {error && <p className="error">{error}</p>}
      </form>
    </Dialog>
  );
}
