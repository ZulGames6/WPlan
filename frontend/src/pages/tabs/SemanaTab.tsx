import { FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../../lib/api";
import { HorarioDia, SemanaResponse, toTimeInput } from "../../lib/semana";

export default function SemanaTab({ planId }: { planId: number }) {
  const [dias, setDias] = useState<HorarioDia[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [ok, setOk] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.get<SemanaResponse>(`/api/planificaciones/${planId}/semana`);
      setDias(data.dias);
    } catch (e: any) {
      setError(e.message || "No se pudo cargar la semana");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [planId]);

  const patch = (idx: number, patch: Partial<HorarioDia>) => {
    setDias((prev) => prev.map((d, i) => (i === idx ? { ...d, ...patch } : d)));
  };

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setOk(null);
    try {
      const body = {
        dias: dias.map((d) => ({
          diaSemana: d.diaSemana,
          activo: d.activo,
          horaInicio: d.activo && d.horaInicio ? d.horaInicio : null,
          horaFin: d.activo && d.horaFin ? d.horaFin : null,
          lugar: d.activo ? (d.lugar?.trim() || null) : null,
          conGimnasio: d.activo && d.conGimnasio,
          conNatacion: d.activo && d.conNatacion,
          conWaterpolo: d.activo && d.conWaterpolo,
        })),
      };
      const data = await api.put<SemanaResponse>(`/api/planificaciones/${planId}/semana`, body);
      setDias(data.dias);
      setOk("Horario semanal guardado.");
    } catch (e: any) {
      setError(e.message || "Error al guardar");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p className="muted">Cargando horario…</p>;

  return (
    <div>
      <p className="muted" style={{ marginTop: 0 }}>
        Define una vez el horario y las partes de cada día. Al crear sesiones en el calendario se aplicará automáticamente.
        {" "}
        <Link to="..">Esta semana</Link>
        {" · "}
        <Link to="../calendario">Calendario</Link>
      </p>

      {error && <p className="error">{error}</p>}
      {ok && <p className="success">{ok}</p>}

      <form onSubmit={onSubmit} className="card">
        <table className="semana-table">
          <thead>
            <tr>
              <th>Día</th>
              <th>Entreno</th>
              <th>Inicio</th>
              <th>Fin</th>
              <th>Lugar</th>
              <th>Gym</th>
              <th>Nat</th>
              <th>WP</th>
            </tr>
          </thead>
          <tbody>
            {dias.map((d, idx) => (
              <tr key={d.diaSemana}>
                <td><strong>{d.nombreDia}</strong></td>
                <td>
                  <input
                    type="checkbox"
                    checked={d.activo}
                    onChange={(e) => patch(idx, { activo: e.target.checked })}
                    aria-label={`${d.nombreDia} activo`}
                  />
                </td>
                <td>
                  <input
                    type="time"
                    disabled={!d.activo}
                    value={toTimeInput(d.horaInicio)}
                    onChange={(e) => patch(idx, { horaInicio: e.target.value || null })}
                  />
                </td>
                <td>
                  <input
                    type="time"
                    disabled={!d.activo}
                    value={toTimeInput(d.horaFin)}
                    onChange={(e) => patch(idx, { horaFin: e.target.value || null })}
                  />
                </td>
                <td>
                  <input
                    type="text"
                    disabled={!d.activo}
                    value={d.lugar ?? ""}
                    onChange={(e) => patch(idx, { lugar: e.target.value })}
                    placeholder="Piscina"
                  />
                </td>
                <td>
                  <input
                    type="checkbox"
                    disabled={!d.activo}
                    checked={d.conGimnasio}
                    onChange={(e) => patch(idx, { conGimnasio: e.target.checked })}
                  />
                </td>
                <td>
                  <input
                    type="checkbox"
                    disabled={!d.activo}
                    checked={d.conNatacion}
                    onChange={(e) => patch(idx, { conNatacion: e.target.checked })}
                  />
                </td>
                <td>
                  <input
                    type="checkbox"
                    disabled={!d.activo}
                    checked={d.conWaterpolo}
                    onChange={(e) => patch(idx, { conWaterpolo: e.target.checked })}
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="toolbar" style={{ marginTop: "1rem" }}>
          <button type="button" className="ghost" onClick={load}>Descartar cambios</button>
          <div className="spacer" />
          <button type="submit" className="primary" disabled={saving}>
            {saving ? "Guardando…" : "Guardar semana"}
          </button>
        </div>
      </form>
    </div>
  );
}
