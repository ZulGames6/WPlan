import { FormEvent, useEffect, useState } from "react";
import { api } from "../../lib/api";
import { Dialog } from "../../lib/dialog";

interface Jugador {
  id: number;
  nombre: string;
  apellidos?: string | null;
  fechaNacimiento?: string | null;
  posicion?: string | null;
  notas?: string | null;
  pesoKg?: number | null;
  dni?: string | null;
  tallaBanador?: string | null;
  tallaCamiseta?: string | null;
}

export default function JugadoresTab({ planId }: { planId: number }) {
  const [list, setList] = useState<Jugador[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<Jugador | null>(null);
  const [creating, setCreating] = useState(false);

  const fetch = async () => {
    setLoading(true);
    try {
      const data = await api.get<Jugador[]>(`/api/planificaciones/${planId}/jugadores`);
      setList(data);
      setError(null);
    } catch (e: any) {
      setError(e.message || "Error cargando jugadores");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetch(); }, [planId]);

  const eliminar = async (j: Jugador) => {
    if (!confirm(`Eliminar a ${j.nombre}?`)) return;
    try {
      await api.delete(`/api/planificaciones/${planId}/jugadores/${j.id}`);
      fetch();
    } catch (e: any) {
      alert(e.message);
    }
  };

  return (
    <div>
      <div className="toolbar">
        <h2 style={{ margin: 0 }}>Plantilla</h2>
        <div className="spacer" />
        <button className="primary" onClick={() => setCreating(true)}>Añadir jugador</button>
      </div>

      {loading && <p className="muted">Cargando…</p>}
      {error && <p className="error">{error}</p>}

      {!loading && list.length === 0 && (
        <div className="card"><p className="muted">No hay jugadores. Añade el primero.</p></div>
      )}

      {list.length > 0 && (
        <div className="card" style={{ padding: 0, overflowX: "auto" }}>
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Apellidos</th>
                <th>Pos.</th>
                <th>F. Nac.</th>
                <th>Peso</th>
                <th>DNI</th>
                <th>T. Bañ.</th>
                <th>T. Cam.</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {list.map((j) => (
                <tr key={j.id}>
                  <td>{j.nombre}</td>
                  <td>{j.apellidos ?? ""}</td>
                  <td>{j.posicion ?? ""}</td>
                  <td>{j.fechaNacimiento ?? ""}</td>
                  <td>{j.pesoKg ?? ""}</td>
                  <td>{j.dni ?? ""}</td>
                  <td>{j.tallaBanador ?? ""}</td>
                  <td>{j.tallaCamiseta ?? ""}</td>
                  <td style={{ textAlign: "right", whiteSpace: "nowrap" }}>
                    <button className="ghost" onClick={() => setEditing(j)}>Editar</button>
                    <button className="ghost" onClick={() => eliminar(j)} style={{ color: "var(--danger)" }}>Borrar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <JugadorDialog
        open={creating}
        title="Nuevo jugador"
        initial={null}
        onClose={() => setCreating(false)}
        onSave={async (payload) => {
          await api.post(`/api/planificaciones/${planId}/jugadores`, payload);
          setCreating(false); fetch();
        }}
      />
      <JugadorDialog
        open={!!editing}
        title="Editar jugador"
        initial={editing}
        onClose={() => setEditing(null)}
        onSave={async (payload) => {
          if (!editing) return;
          await api.put(`/api/planificaciones/${planId}/jugadores/${editing.id}`, payload);
          setEditing(null); fetch();
        }}
      />
    </div>
  );
}

function JugadorDialog({
  open, title, initial, onClose, onSave,
}: {
  open: boolean;
  title: string;
  initial: Jugador | null;
  onClose: () => void;
  onSave: (payload: Partial<Jugador>) => Promise<void>;
}) {
  const [form, setForm] = useState<Partial<Jugador>>({});
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    setError(null);
    setForm(initial ?? {});
  }, [initial, open]);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const payload: Partial<Jugador> = {
        nombre: form.nombre ?? "",
        apellidos: blankToNull(form.apellidos),
        fechaNacimiento: blankToNull(form.fechaNacimiento),
        posicion: blankToNull(form.posicion),
        notas: blankToNull(form.notas),
        pesoKg: form.pesoKg ? Number(form.pesoKg) : undefined,
        dni: blankToNull(form.dni),
        tallaBanador: blankToNull(form.tallaBanador),
        tallaCamiseta: blankToNull(form.tallaCamiseta),
      };
      await onSave(payload);
    } catch (err: any) {
      setError(err.message || "No se pudo guardar");
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
          <button className="primary" type="submit" form="form-jugador" disabled={submitting}>
            {submitting ? "Guardando…" : "Guardar"}
          </button>
        </>
      }
    >
      <form id="form-jugador" onSubmit={onSubmit}>
        <div className="row">
          <div className="row cols-2">
            <div>
              <label>Nombre *</label>
              <input required value={form.nombre ?? ""} onChange={(e) => setForm({ ...form, nombre: e.target.value })} />
            </div>
            <div>
              <label>Apellidos</label>
              <input value={form.apellidos ?? ""} onChange={(e) => setForm({ ...form, apellidos: e.target.value })} />
            </div>
          </div>
          <div className="row cols-3">
            <div>
              <label>Fecha nacimiento</label>
              <input type="date" value={form.fechaNacimiento ?? ""} onChange={(e) => setForm({ ...form, fechaNacimiento: e.target.value })} />
            </div>
            <div>
              <label>Posición</label>
              <select value={form.posicion ?? ""} onChange={(e) => setForm({ ...form, posicion: e.target.value })}>
                <option value="">—</option>
                <option value="Portero">Portero</option>
                <option value="Defensa">Defensa</option>
                <option value="Boya">Boya</option>
                <option value="Lateral">Lateral</option>
                <option value="Centro">Centro</option>
              </select>
            </div>
            <div>
              <label>Peso (kg)</label>
              <input type="number" step="0.1" min={20} max={200} value={form.pesoKg ?? ""} onChange={(e) => setForm({ ...form, pesoKg: e.target.value ? Number(e.target.value) : undefined })} />
            </div>
          </div>
          <div className="row cols-3">
            <div>
              <label>DNI</label>
              <input value={form.dni ?? ""} onChange={(e) => setForm({ ...form, dni: e.target.value })} />
            </div>
            <div>
              <label>Talla bañador</label>
              <input value={form.tallaBanador ?? ""} onChange={(e) => setForm({ ...form, tallaBanador: e.target.value })} />
            </div>
            <div>
              <label>Talla camiseta</label>
              <input value={form.tallaCamiseta ?? ""} onChange={(e) => setForm({ ...form, tallaCamiseta: e.target.value })} placeholder="S, M, L, XL" />
            </div>
          </div>
          <div>
            <label>Notas</label>
            <textarea value={form.notas ?? ""} onChange={(e) => setForm({ ...form, notas: e.target.value })} />
          </div>
        </div>
        {error && <p className="error">{error}</p>}
      </form>
    </Dialog>
  );
}

function blankToNull(v: string | undefined | null) {
  if (v == null) return null;
  const t = String(v).trim();
  return t === "" ? null : t;
}
