import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../../lib/api";
import { isoLocal } from "../../lib/dates";
import { horarioParaFecha, SemanaResponse, toTimeInput } from "../../lib/semana";
import SesionCreateDialog from "../../components/SesionCreateDialog";

interface Sesion {
  id: number;
  fecha: string;
  horaInicio?: string;
  horaFin?: string;
  lugar?: string;
  tipo?: string;
  gimnasio?: object | null;
  natacion?: object | null;
  waterpolo?: object | null;
}

interface AplicarResp { creadas: number; omitidas: number; sinHorario: number; }

const DIAS = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"];

function SesionPill({ s, onClick }: { s: Sesion; onClick: () => void }) {
  const classes = [
    "sesion-pill",
    s.gimnasio && "has-gym",
    s.natacion && "has-nat",
    s.waterpolo && "has-wp",
  ].filter(Boolean).join(" ");
  const hora = s.horaInicio ? s.horaInicio.slice(0, 5) : "—";

  return (
    <button type="button" className={classes} onClick={onClick}>
      <span className="sesion-time">{hora}</span>
      <span className="sesion-label">{s.tipo ?? "Entreno"}</span>
      {(s.gimnasio || s.natacion || s.waterpolo) && (
        <span className="sesion-dots" aria-hidden>
          {s.gimnasio && <span className="dot dot-gym" />}
          {s.natacion && <span className="dot dot-nat" />}
          {s.waterpolo && <span className="dot dot-wp" />}
        </span>
      )}
    </button>
  );
}

interface HoraSlot {
  horaInicio: string;
  conGimnasio: boolean;
  conNatacion: boolean;
  conWaterpolo: boolean;
  onClick: () => void;
}

function HorarioGhost({ slot }: { slot: HoraSlot }) {
  return (
    <button type="button" className="sesion-pill is-ghost" onClick={slot.onClick} title="Crear sesión desde el horario semanal">
      <span className="sesion-time">{slot.horaInicio}</span>
      <span className="sesion-label">Programado</span>
      <span className="sesion-dots" aria-hidden>
        {slot.conGimnasio && <span className="dot dot-gym" />}
        {slot.conNatacion && <span className="dot dot-nat" />}
        {slot.conWaterpolo && <span className="dot dot-wp" />}
      </span>
    </button>
  );
}

export default function SesionesCalendarioTab({ planId }: { planId: number }) {
  const navigate = useNavigate();
  const todayIso = isoLocal(new Date());
  const [cursor, setCursor] = useState<Date>(() => {
    const d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), 1);
  });
  const [sesiones, setSesiones] = useState<Sesion[]>([]);
  const [horario, setHorario] = useState<SemanaResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState<string | null>(null);
  const [aplicando, setAplicando] = useState(false);
  const [resultMsg, setResultMsg] = useState<string | null>(null);

  const yearMonth = useMemo(() => ({
    year: cursor.getFullYear(),
    month: cursor.getMonth(),
  }), [cursor]);

  const firstDay = useMemo(() => new Date(yearMonth.year, yearMonth.month, 1), [yearMonth]);
  const lastDay = useMemo(() => new Date(yearMonth.year, yearMonth.month + 1, 0), [yearMonth]);
  const monthLabel = firstDay.toLocaleDateString("es-ES", { month: "long", year: "numeric" });

  const fetch = async () => {
    setLoading(true);
    try {
      const [sess, sem] = await Promise.all([
        api.get<Sesion[]>(`/api/planificaciones/${planId}/sesiones?desde=${isoLocal(firstDay)}&hasta=${isoLocal(lastDay)}`),
        api.get<SemanaResponse>(`/api/planificaciones/${planId}/semana`),
      ]);
      setSesiones(sess);
      setHorario(sem);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetch(); /* eslint-disable-next-line */ }, [planId, yearMonth.year, yearMonth.month]);

  const hayHorarioActivo = useMemo(
    () => (horario?.dias ?? []).some((d) => d.activo),
    [horario],
  );

  const onAplicarHorario = async () => {
    if (!hayHorarioActivo) return;
    const conf = confirm(
      `Se crearán automáticamente las sesiones del horario semanal para ${monthLabel}. ` +
      `Las fechas que ya tengan sesión no se duplican. ¿Continuar?`
    );
    if (!conf) return;
    setAplicando(true);
    setResultMsg(null);
    try {
      const r = await api.post<AplicarResp>(
        `/api/planificaciones/${planId}/sesiones/aplicar-horario?desde=${isoLocal(firstDay)}&hasta=${isoLocal(lastDay)}`,
        {}
      );
      setResultMsg(
        `Hecho. Creadas: ${r.creadas}` +
        (r.omitidas > 0 ? ` · Omitidas (ya existían): ${r.omitidas}` : "") +
        (r.sinHorario > 0 ? ` · Días sin horario: ${r.sinHorario}` : "")
      );
      await fetch();
    } catch (e: any) {
      setResultMsg(e.message || "Error aplicando el horario");
    } finally {
      setAplicando(false);
    }
  };

  const grid = useMemo(() => {
    const startWeekday = (firstDay.getDay() + 6) % 7;
    const totalDays = lastDay.getDate();
    const cells: Array<{ date: Date | null; sesiones: Sesion[] }> = [];
    for (let i = 0; i < startWeekday; i++) cells.push({ date: null, sesiones: [] });
    for (let d = 1; d <= totalDays; d++) {
      const date = new Date(yearMonth.year, yearMonth.month, d);
      const iso = isoLocal(date);
      cells.push({ date, sesiones: sesiones.filter((s) => s.fecha === iso) });
    }
    while (cells.length % 7 !== 0) cells.push({ date: null, sesiones: [] });
    return cells;
  }, [firstDay, lastDay, yearMonth, sesiones]);

  return (
    <div>
      <p className="muted" style={{ marginTop: 0 }}>
        <Link to="..">← Volver a esta semana</Link>
        {" · "}
        <Link to="../horario">Configurar horario</Link>
        {hayHorarioActivo && <span style={{ marginLeft: 8 }}>· Los días en gris son slots del horario sin sesión creada.</span>}
      </p>

      <div className="calendar-shell">
        <div className="calendar-nav">
          <div className="nav-group">
            <button type="button" className="nav-arrow" onClick={() => setCursor(new Date(yearMonth.year, yearMonth.month - 1, 1))} aria-label="Mes anterior">‹</button>
            <button type="button" className="nav-today" onClick={() => setCursor(new Date(new Date().getFullYear(), new Date().getMonth(), 1))}>Hoy</button>
            <button type="button" className="nav-arrow" onClick={() => setCursor(new Date(yearMonth.year, yearMonth.month + 1, 1))} aria-label="Mes siguiente">›</button>
          </div>
          <h2 className="month-title">{monthLabel}</h2>
          <div style={{ marginLeft: "auto", display: "flex", gap: 8 }}>
            {hayHorarioActivo && (
              <button
                type="button"
                className="btn-outline"
                onClick={onAplicarHorario}
                disabled={aplicando}
                title="Crea automáticamente las sesiones del horario semanal para este mes"
              >
                {aplicando ? "Aplicando…" : "Aplicar horario al mes"}
              </button>
            )}
            <button type="button" className="primary" onClick={() => setCreating(todayIso)}>
              + Nueva sesión
            </button>
          </div>
        </div>

        {resultMsg && <p className={resultMsg.startsWith("Hecho") ? "success" : "error"} style={{ marginBottom: "1rem" }}>{resultMsg}</p>}
        {loading && <p className="muted" style={{ marginBottom: "1rem" }}>Cargando…</p>}

        <div className="calendar-grid">
          {DIAS.map((d, i) => (
            <div key={d} className={`calendar-header${i >= 5 ? " is-weekend" : ""}`}>{d}</div>
          ))}
          {grid.map((cell, idx) => {
            if (!cell.date) return <div key={idx} className="calendar-day empty" />;
            const iso = isoLocal(cell.date);
            const isToday = iso === todayIso;
            const dow = cell.date.getDay();
            const isWeekend = dow === 0 || dow === 6;
            const plantilla = horario ? horarioParaFecha(horario, iso) : undefined;
            const tienesSesion = cell.sesiones.length > 0;
            const mostrarGhost = !tienesSesion && plantilla?.activo;

            return (
              <div
                key={idx}
                className={[
                  "calendar-day",
                  isToday && "is-today",
                  isWeekend && "is-weekend",
                  mostrarGhost && "has-ghost",
                ].filter(Boolean).join(" ")}
              >
                <div className="calendar-day-head">
                  <span className="day-num">{cell.date.getDate()}</span>
                  <button type="button" className="icon-btn" title="Añadir sesión" onClick={() => setCreating(iso)}>+</button>
                </div>
                <div className="calendar-day-sessions">
                  {cell.sesiones.map((s) => (
                    <SesionPill key={s.id} s={s} onClick={() => navigate(`../sesiones/${s.id}`)} />
                  ))}
                  {mostrarGhost && plantilla && (
                    <HorarioGhost
                      slot={{
                        horaInicio: toTimeInput(plantilla.horaInicio) || "—",
                        conGimnasio: plantilla.conGimnasio,
                        conNatacion: plantilla.conNatacion,
                        conWaterpolo: plantilla.conWaterpolo,
                        onClick: () => setCreating(iso),
                      }}
                    />
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <SesionCreateDialog
        open={!!creating}
        fechaInicial={creating ?? ""}
        planId={planId}
        horarioLink="../horario"
        onClose={() => setCreating(null)}
        onCreated={(id) => { setCreating(null); fetch(); navigate(`../sesiones/${id}`); }}
      />
    </div>
  );
}
