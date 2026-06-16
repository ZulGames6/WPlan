import { FormEvent, useEffect, useState } from "react";
import { api } from "../../lib/api";
import { Dialog } from "../../lib/dialog";

interface Ejercicio {
  id: number;
  nombre: string;
  estilo?: string | null;
  tipoBloque?: string | null;
  material?: string | null;
  intensidad?: string | null;
  descripcion?: string | null;
}

export default function EjerciciosNatacion() {
  const [list, setList] = useState<Ejercicio[]>([]);
  const [editing, setEditing] = useState<Ejercicio | null>(null);
  const [creating, setCreating] = useState(false);

  const fetch = async () => setList(await api.get<Ejercicio[]>("/api/ejercicios/natacion"));

  useEffect(() => { fetch(); }, []);

  const eliminar = async (e: Ejercicio) => {
    if (!confirm(`Eliminar ${e.nombre}?`)) return;
    await api.delete(`/api/ejercicios/natacion/${e.id}`);
    fetch();
  };

  return (
    <div>
      <div className="toolbar">
        <span className="muted">{list.length} ejercicio(s)</span>
        <div className="spacer" />
        <button className="primary" onClick={() => setCreating(true)}>Nuevo ejercicio</button>
      </div>
      {list.length === 0 && <p className="muted">No tienes ejercicios.</p>}
      {list.length > 0 && (
        <table>
          <thead><tr><th>Nombre</th><th>Estilo</th><th>Tipo bloque</th><th>Material</th><th>Intensidad</th><th></th></tr></thead>
          <tbody>
            {list.map((e) => (
              <tr key={e.id}>
                <td>{e.nombre}</td>
                <td>{e.estilo ?? ""}</td>
                <td>{e.tipoBloque ?? ""}</td>
                <td>{e.material ?? ""}</td>
                <td>{e.intensidad ?? ""}</td>
                <td>
                  <button className="ghost" onClick={() => setEditing(e)}>Editar</button>
                  <button className="ghost" style={{ color: "var(--danger)" }} onClick={() => eliminar(e)}>Borrar</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <Editor open={creating} title="Nuevo ejercicio de natación" initial={null} onClose={() => setCreating(false)} onSave={async (p) => { await api.post("/api/ejercicios/natacion", p); setCreating(false); fetch(); }} />
      <Editor open={!!editing} title="Editar ejercicio" initial={editing} onClose={() => setEditing(null)} onSave={async (p) => { if (!editing) return; await api.put(`/api/ejercicios/natacion/${editing.id}`, p); setEditing(null); fetch(); }} />
    </div>
  );
}

function Editor({ open, title, initial, onClose, onSave }: { open: boolean; title: string; initial: Ejercicio | null; onClose: () => void; onSave: (p: Partial<Ejercicio>) => Promise<void>; }) {
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
        estilo: form.estilo || null,
        tipoBloque: form.tipoBloque || null,
        material: form.material || null,
        intensidad: form.intensidad || null,
        descripcion: form.descripcion || null,
      });
    } catch (err: any) { setError(err.message); } finally { setSubmitting(false); }
  };

  return (
    <Dialog open={open} title={title} onClose={onClose} actions={
      <>
        <button onClick={onClose}>Cancelar</button>
        <button className="primary" form="form-nat-cat" type="submit" disabled={submitting}>{submitting ? "Guardando…" : "Guardar"}</button>
      </>
    }>
      <form id="form-nat-cat" onSubmit={onSubmit}>
        <div className="row">
          <div><label>Nombre *</label><input required value={form.nombre ?? ""} onChange={(e) => setForm({ ...form, nombre: e.target.value })} /></div>
          <div className="row cols-3">
            <div>
              <label>Estilo</label>
              <select value={form.estilo ?? ""} onChange={(e) => setForm({ ...form, estilo: e.target.value })}>
                <option value="">—</option>
                <option value="LIBRE">Libre</option>
                <option value="ESPALDA">Espalda</option>
                <option value="BRAZA">Braza</option>
                <option value="MARIPOSA">Mariposa</option>
                <option value="PIES">Pies</option>
                <option value="BRAZOS">Brazos</option>
                <option value="COMBINADO">Combinado</option>
                <option value="TECNICA">Técnica</option>
                <option value="JUEGO">Juego</option>
              </select>
            </div>
            <div>
              <label>Tipo bloque</label>
              <select value={form.tipoBloque ?? ""} onChange={(e) => setForm({ ...form, tipoBloque: e.target.value })}>
                <option value="">—</option>
                <option value="CALENTAMIENTO">Calentamiento</option>
                <option value="PRINCIPAL">Principal</option>
                <option value="VELOCIDAD">Velocidad</option>
                <option value="RESISTENCIA">Resistencia</option>
                <option value="RECUPERACION">Recuperación</option>
                <option value="VUELTA_CALMA">Vuelta a la calma</option>
              </select>
            </div>
            <div>
              <label>Intensidad</label>
              <select value={form.intensidad ?? ""} onChange={(e) => setForm({ ...form, intensidad: e.target.value })}>
                <option value="">—</option>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
                <option value="MAX">Máx</option>
              </select>
            </div>
          </div>
          <div><label>Material recomendado</label><input value={form.material ?? ""} onChange={(e) => setForm({ ...form, material: e.target.value })} placeholder="palas, tabla, pull-buoy, aletas…" /></div>
          <div><label>Descripción</label><textarea value={form.descripcion ?? ""} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} /></div>
        </div>
        {error && <p className="error">{error}</p>}
      </form>
    </Dialog>
  );
}
