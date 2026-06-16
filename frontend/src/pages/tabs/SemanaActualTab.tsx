import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../../lib/api";
import { formatWeekTitle, getWeekRange, isoLocal, isSameDay, nombreDiaCorto } from "../../lib/dates";
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

export default function SemanaActualTab({ planId }: { planId: number }) {
  const navigate = useNavigate();
  const today = useMemo(() => new Date(), []);
  const [weekOffset, setWeekOffset] = useState(0);
  const [sesiones, setSesiones] = useState<Sesion[]>([]);
  const [horario, setHorario] = useState<SemanaResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState<string | null>(null);

  const { start, end, days } = useMemo(() => getWeekRange(today, weekOffset), [today, weekOffset]);
  const weekTitle = formatWeekTitle(start, end);
  const isCurrentWeek = weekOffset === 0;

  const load = async () => {
    setLoading(true);
    try {
      const desde = isoLocal(start);
      const hasta = isoLocal(end);
      const [sess, sem] = await Promise.all([
        api.get<Sesion[]>(`/api/planificaciones/${planId}/sesiones?desde=${desde}&hasta=${hasta}`),
        api.get<SemanaResponse>(`/api/planificaciones/${planId}/semana`),
      ]);
      setSesiones(sess);
      setHorario(sem);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); /* eslint-disable-next-line */ }, [planId, weekOffset]);

  return (
    <div className="week-hero">
      <header className="week-hero-header">
        <div>
          <p className="week-hero-kicker">{isCurrentWeek ? "Esta semana" : "Semana"}</p>
          <h2 className="week-hero-title">{weekTitle}</h2>
        </div>
        <div className="week-hero-actions">
          <div className="week-nav">
            <button type="button" className="ghost" onClick={() => setWeekOffset((o) => o - 1)} aria-label="Semana anterior">
              ‹
            </button>
            {!isCurrentWeek && (
              <button type="button" className="nav-today-btn" onClick={() => setWeekOffset(0)}>
                Hoy
              </button>
            )}
            <button type="button" className="ghost" onClick={() => setWeekOffset((o) => o + 1)} aria-label="Semana siguiente">
              ›
            </button>
          </div>
          <Link to="calendario" className="btn-outline">
            Calendario
          </Link>
        </div>
      </header>

      {loading ? (
        <p className="muted week-loading">Cargando entrenamientos…</p>
      ) : (
        <div className="week-grid">
          {days.map((day) => {
            const iso = isoLocal(day);
            const daySessions = sesiones.filter((s) => s.fecha === iso);
            const isToday = isSameDay(day, today);
            const isWeekend = day.getDay() === 0 || day.getDay() === 6;
            const plantilla = horario ? horarioParaFecha(horario, iso) : undefined;

            return (
              <article
                key={iso}
                className={[
                  "week-day",
                  isToday && "is-today",
                  isWeekend && "is-weekend",
                ].filter(Boolean).join(" ")}
              >
                <div className="week-day-top">
                  <span className="week-day-name">{nombreDiaCorto(day)}</span>
                  <span className="week-day-num">{day.getDate()}</span>
                </div>

                <div className="week-day-body">
                  {daySessions.length === 0 ? (
                    plantilla?.activo ? (
                      <button
                        type="button"
                        className="week-session is-ghost"
                        onClick={() => setCreating(iso)}
                        title="Crear sesión a partir del horario semanal"
                      >
                        <span className="week-session-time">
                          {toTimeInput(plantilla.horaInicio)}
                          {plantilla.horaFin ? ` – ${toTimeInput(plantilla.horaFin)}` : ""}
                        </span>
                        <span className="week-session-type">Programado</span>
                        {plantilla.lugar && <span className="week-session-place">{plantilla.lugar}</span>}
                        <span className="week-session-parts">
                          {plantilla.conGimnasio && <span className="part-dot gym" />}
                          {plantilla.conNatacion && <span className="part-dot nat" />}
                          {plantilla.conWaterpolo && <span className="part-dot wp" />}
                        </span>
                      </button>
                    ) : (
                      <p className="week-day-empty">Descanso</p>
                    )
                  ) : (
                    daySessions.map((s) => (
                      <button
                        key={s.id}
                        type="button"
                        className="week-session"
                        onClick={() => navigate(`sesiones/${s.id}`)}
                      >
                        <span className="week-session-time">
                          {s.horaInicio?.slice(0, 5) ?? "—"}
                          {s.horaFin ? ` – ${s.horaFin.slice(0, 5)}` : ""}
                        </span>
                        <span className="week-session-type">{s.tipo ?? "Entreno"}</span>
                        {s.lugar && <span className="week-session-place">{s.lugar}</span>}
                        <span className="week-session-parts">
                          {s.gimnasio && <span className="part-dot gym" />}
                          {s.natacion && <span className="part-dot nat" />}
                          {s.waterpolo && <span className="part-dot wp" />}
                        </span>
                      </button>
                    ))
                  )}
                </div>

                <button
                  type="button"
                  className="week-add"
                  onClick={() => setCreating(iso)}
                  title="Añadir sesión"
                >
                  + Añadir
                </button>
              </article>
            );
          })}
        </div>
      )}

      <SesionCreateDialog
        open={!!creating}
        fechaInicial={creating ?? ""}
        planId={planId}
        horarioLink="../horario"
        onClose={() => setCreating(null)}
        onCreated={(id) => {
          setCreating(null);
          load();
          navigate(`sesiones/${id}`);
        }}
      />
    </div>
  );
}
