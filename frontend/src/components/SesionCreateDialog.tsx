import { FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../lib/api";
import { Dialog } from "../lib/dialog";
import { horarioParaFecha, SemanaResponse, toTimeInput } from "../lib/semana";

export default function SesionCreateDialog({
  open,
  fechaInicial,
  planId,
  horarioLink = "../horario",
  onClose,
  onCreated,
}: {
  open: boolean;
  fechaInicial: string;
  planId: number;
  horarioLink?: string;
  onClose: () => void;
  onCreated: (id: number) => void;
}) {
  const [fecha, setFecha] = useState(fechaInicial);
  const [notas, setNotas] = useState("");
  const [preview, setPreview] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setFecha(fechaInicial);
    setNotas("");
    setError(null);
  }, [open, fechaInicial]);

  useEffect(() => {
    if (!open || !fecha) return;
    let cancelled = false;
    api.get<SemanaResponse>(`/api/planificaciones/${planId}/semana`)
      .then((semana) => {
        if (cancelled) return;
        const h = horarioParaFecha(semana, fecha);
        if (!h || !h.activo) {
          setPreview("Día sin entreno en el horario semanal. Se creará solo con la fecha.");
          return;
        }
        const partes = [
          h.conGimnasio && "gimnasio",
          h.conNatacion && "natación",
          h.conWaterpolo && "waterpolo",
        ].filter(Boolean).join(", ");
        setPreview(
          `${h.nombreDia}: ${toTimeInput(h.horaInicio)}–${toTimeInput(h.horaFin)}${h.lugar ? ` · ${h.lugar}` : ""}`
          + (partes ? ` · ${partes}` : "")
        );
      })
      .catch(() => { if (!cancelled) setPreview(null); });
    return () => { cancelled = true; };
  }, [open, fecha, planId]);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const semana = await api.get<SemanaResponse>(`/api/planificaciones/${planId}/semana`);
      const h = horarioParaFecha(semana, fecha);
      const activo = h?.activo ?? false;
      const body: Record<string, unknown> = {
        fecha,
        horaInicio: activo ? toTimeInput(h?.horaInicio) || null : null,
        horaFin: activo ? toTimeInput(h?.horaFin) || null : null,
        lugar: activo ? (h?.lugar ?? null) : null,
        tipo: "ENTRENAMIENTO",
        notas: notas.trim() || null,
      };
      const parte = () => ({ objetivo: null, notas: null });
      if (activo && h) {
        if (h.conGimnasio) body.gimnasio = parte();
        if (h.conNatacion) body.natacion = parte();
        if (h.conWaterpolo) body.waterpolo = parte();
      } else {
        body.gimnasio = parte();
        body.natacion = parte();
        body.waterpolo = parte();
      }
      const res = await api.post<{ id: number }>(`/api/planificaciones/${planId}/sesiones`, body);
      onCreated(res.id);
    } catch (err: any) {
      const detalle = err?.status ? ` (${err.status})` : "";
      setError((err.message || "Error creando sesión") + detalle);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      title="Nueva sesión"
      onClose={onClose}
      actions={
        <>
          <button type="button" onClick={onClose}>Cancelar</button>
          <button className="primary" form="form-sesion-create" type="submit" disabled={submitting}>
            {submitting ? "Creando…" : "Crear"}
          </button>
        </>
      }
    >
      <form id="form-sesion-create" onSubmit={onSubmit}>
        <p className="muted" style={{ marginTop: 0 }}>
          Horario según <Link to={horarioLink} onClick={onClose}>configuración semanal</Link>.
        </p>
        <div>
          <label>Fecha *</label>
          <input type="date" required value={fecha} onChange={(e) => setFecha(e.target.value)} />
        </div>
        {preview && <p className="preview-horario">{preview}</p>}
        <div>
          <label>Notas (opcional)</label>
          <textarea value={notas} onChange={(e) => setNotas(e.target.value)} rows={2} placeholder="Recordatorio del día…" />
        </div>
        {error && <p className="error">{error}</p>}
      </form>
    </Dialog>
  );
}
