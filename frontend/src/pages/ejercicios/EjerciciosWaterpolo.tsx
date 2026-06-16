import { FormEvent, useEffect, useState } from "react";
import { api } from "../../lib/api";
import { Dialog } from "../../lib/dialog";

interface Ejercicio {
  id: number;
  nombre: string;
  objetivo?: string | null;
  categoria?: string | null;
  jugadoresMin?: number | null;
  jugadoresMax?: number | null;
  intensidad?: string | null;
  material?: string | null;
  duracionMinSugerida?: number | null;
  descripcion?: string | null;
}

export default function EjerciciosWaterpolo() {
  const [list, setList] = useState<Ejercicio[]>([]);
  const [editing, setEditing] = useState<Ejercicio | null>(null);
  const [creating, setCreating] = useState(false);

  const fetch = async () => {
    const data = await api.get<Ejercicio[]>("/api/ejercicios/waterpolo");
    setList(data);
  };

  useEffect(() => { fetch(); }, []);

  const eliminar = async (e: Ejercicio) => {
    if (!confirm(`Eliminar ${e.nombre}?`)) return;
    await api.delete(`/api/ejercicios/waterpolo/${e.id}`);
    fetch();
  };

  return (
    <div>
      <div className="toolbar">
        <span className="muted">{list.length} ejercicio(s)</span>
        <div className="spacer" />
        <button className="primary" onClick={() => setCreating(true)}>Nuevo ejercicio</button>
      </div>
      {list.length === 0 && <p className="muted">No tienes ejercicios. Crea el primero.</p>}
      {list.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Nombre</th><th>Objetivo</th><th>Categoría</th><th>Intensidad</th><th>Jug.</th><th>Material</th><th>Min</th><th>Notas / estaciones</th><th></th>
            </tr>
          </thead>
          <tbody>
            {list.map((e) => (
              <tr key={e.id}>
                <td><strong>{e.nombre}</strong></td>
                <td className="muted" style={{ fontSize: "0.85rem" }}>{e.objetivo ?? ""}</td>
                <td><span className="badge">{e.categoria ?? ""}</span></td>
                <td>{e.intensidad ?? ""}</td>
                <td>{e.jugadoresMin ?? "—"}–{e.jugadoresMax ?? "—"}</td>
                <td>{e.material ?? ""}</td>
                <td>{e.duracionMinSugerida ?? ""}</td>
                <td><span className="desc-cell" title={e.descripcion ?? ""}>{e.descripcion ?? ""}</span></td>
                <td>
                  <button className="ghost" onClick={() => setEditing(e)}>Editar</button>
                  <button className="ghost" style={{ color: "var(--danger)" }} onClick={() => eliminar(e)}>Borrar</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <EjerWpDialog
        open={creating}
        title="Nuevo ejercicio de waterpolo"
        initial={null}
        onClose={() => setCreating(false)}
        onSave={async (p) => { await api.post("/api/ejercicios/waterpolo", p); setCreating(false); fetch(); }}
      />
      <EjerWpDialog
        open={!!editing}
        title="Editar ejercicio"
        initial={editing}
        onClose={() => setEditing(null)}
        onSave={async (p) => { if (!editing) return; await api.put(`/api/ejercicios/waterpolo/${editing.id}`, p); setEditing(null); fetch(); }}
      />
    </div>
  );
}

function EjerWpDialog({ open, title, initial, onClose, onSave }: {
  open: boolean; title: string; initial: Ejercicio | null; onClose: () => void; onSave: (p: Partial<Ejercicio>) => Promise<void>;
}) {
  const [form, setForm] = useState<Partial<Ejercicio>>({});
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => { setForm(initial ?? {}); setError(null); }, [initial, open]);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true); setError(null);
    try {
      await onSave({
        nombre: form.nombre ?? "",
        objetivo: form.objetivo || null,
        categoria: form.categoria || null,
        jugadoresMin: form.jugadoresMin ? Number(form.jugadoresMin) : undefined,
        jugadoresMax: form.jugadoresMax ? Number(form.jugadoresMax) : undefined,
        intensidad: form.intensidad || null,
        material: form.material || null,
        duracionMinSugerida: form.duracionMinSugerida ? Number(form.duracionMinSugerida) : undefined,
        descripcion: form.descripcion || null,
      });
    } catch (err: any) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      title={title}
      onClose={onClose}
      actions={
        <>
          <button onClick={onClose}>Cancelar</button>
          <button className="primary" form="form-wp-cat" type="submit" disabled={submitting}>
            {submitting ? "Guardando…" : "Guardar"}
          </button>
        </>
      }
    >
      <form id="form-wp-cat" onSubmit={onSubmit}>
        <div className="row">
          <div>
            <label>Nombre *</label>
            <input required value={form.nombre ?? ""} onChange={(e) => setForm({ ...form, nombre: e.target.value })} />
          </div>
          <div className="row cols-3">
            <div>
              <label>Categoría</label>
              <select value={form.categoria ?? ""} onChange={(e) => setForm({ ...form, categoria: e.target.value })}>
                <option value="">—</option>
                <option value="TECNICA">Técnica</option>
                <option value="TACTICA">Táctica</option>
                <option value="JUEGO">Juego</option>
                <option value="FISICA">Físico</option>
              </select>
            </div>
            <div>
              <label>Intensidad</label>
              <select value={form.intensidad ?? ""} onChange={(e) => setForm({ ...form, intensidad: e.target.value })}>
                <option value="">—</option>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
              </select>
            </div>
            <div>
              <label>Duración (min)</label>
              <input type="number" min={1} max={240} value={form.duracionMinSugerida ?? ""} onChange={(e) => setForm({ ...form, duracionMinSugerida: e.target.value ? Number(e.target.value) : undefined })} />
            </div>
          </div>
          <div className="row cols-2">
            <div>
              <label>Jugadores mín.</label>
              <input type="number" min={1} max={40} value={form.jugadoresMin ?? ""} onChange={(e) => setForm({ ...form, jugadoresMin: e.target.value ? Number(e.target.value) : undefined })} />
            </div>
            <div>
              <label>Jugadores máx.</label>
              <input type="number" min={1} max={40} value={form.jugadoresMax ?? ""} onChange={(e) => setForm({ ...form, jugadoresMax: e.target.value ? Number(e.target.value) : undefined })} />
            </div>
          </div>
          <div>
            <label>Material</label>
            <input value={form.material ?? ""} onChange={(e) => setForm({ ...form, material: e.target.value })} placeholder="balones, conos, cuerdas…" />
          </div>
          <div>
            <label>Objetivo</label>
            <input value={form.objetivo ?? ""} onChange={(e) => setForm({ ...form, objetivo: e.target.value })} />
          </div>
          <div>
            <label>Descripción / estaciones (circuitos)</label>
            <textarea value={form.descripcion ?? ""} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} placeholder="Para circuitos: lista de estaciones, tiempos, material…" />
          </div>
        </div>
        {error && <p className="error">{error}</p>}
      </form>
    </Dialog>
  );
}
