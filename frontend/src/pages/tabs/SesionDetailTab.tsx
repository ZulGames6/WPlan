import { ReactNode, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { api } from "../../lib/api";
import GimnasioBloque from "../sesion/GimnasioBloque";
import NatacionBloques from "../sesion/NatacionBloques";
import WaterpoloBloque from "../sesion/WaterpoloBloque";
import AsistenciaBloque from "../sesion/AsistenciaBloque";

interface Parte {
  objetivo?: string;
  notas?: string;
}

interface Sesion {
  id: number;
  fecha: string;
  horaInicio?: string;
  horaFin?: string;
  lugar?: string;
  tipo?: string;
  estado?: string;
  objetivo?: string;
  notas?: string;
  gimnasio?: Parte | null;
  natacion?: Parte | null;
  waterpolo?: Parte | null;
}

type ParteNombre = "gimnasio" | "natacion" | "waterpolo";

function sesionBody(sesion: Sesion, overrides?: Partial<Record<ParteNombre, Parte | null>>) {
  const g = overrides && "gimnasio" in overrides ? overrides.gimnasio : sesion.gimnasio;
  const n = overrides && "natacion" in overrides ? overrides.natacion : sesion.natacion;
  const w = overrides && "waterpolo" in overrides ? overrides.waterpolo : sesion.waterpolo;
  return {
    fecha: sesion.fecha,
    horaInicio: sesion.horaInicio ?? null,
    horaFin: sesion.horaFin ?? null,
    lugar: sesion.lugar ?? null,
    tipo: sesion.tipo ?? null,
    estado: sesion.estado ?? null,
    objetivo: sesion.objetivo ?? null,
    notas: sesion.notas ?? null,
    gimnasio: g ? { objetivo: g.objetivo ?? null, notas: g.notas ?? null } : null,
    natacion: n ? { objetivo: n.objetivo ?? null, notas: n.notas ?? null } : null,
    waterpolo: w ? { objetivo: w.objetivo ?? null, notas: w.notas ?? null } : null,
  };
}

function ParteSeccion({
  titulo,
  className,
  activa,
  activando,
  editable,
  onActivar,
  children,
}: {
  titulo: string;
  className: string;
  activa: boolean;
  activando: boolean;
  editable: boolean;
  onActivar: () => void;
  children: ReactNode;
}) {
  return (
    <div className={`card ${className}`}>
      <div className="toolbar" style={{ marginBottom: activa ? "0.75rem" : 0 }}>
        <h3 style={{ margin: 0 }}>{titulo}</h3>
        <div className="spacer" />
        {!activa && editable && (
          <button type="button" className="primary" disabled={activando} onClick={onActivar}>
            {activando ? "Activando…" : "Activar sección"}
          </button>
        )}
        {!activa && !editable && (
          <span className="muted" style={{ fontSize: "0.83rem", fontStyle: "italic" }}>
            No activa
          </span>
        )}
      </div>
      {activa ? (
        children
      ) : (
        <p className="muted" style={{ margin: 0 }}>
          {editable
            ? "Esta parte no está activa en la sesión. Actívala para planificar ejercicios y usar sugerencias."
            : "Esta parte no está activa en la sesión."}
        </p>
      )}
    </div>
  );
}

type Tab = "entreno" | "asistencia";

const MESES = ["enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"];
const DIAS_SEMANA = ["domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"];

/** "18:30:00" → "18:30"; "18:30" → "18:30"; null/undefined → "" */
function hhmm(t?: string | null): string {
  if (!t) return "";
  return t.length >= 5 ? t.slice(0, 5) : t;
}

/** "2026-06-07" → { dia: "Sábado", fecha: "7 de junio de 2026" } */
function formatearFecha(iso: string): { diaSemana: string; fecha: string } {
  const [y, m, d] = iso.split("-").map(Number);
  const dt = new Date(y, m - 1, d);
  return {
    diaSemana: DIAS_SEMANA[dt.getDay()],
    fecha: `${d} de ${MESES[m - 1]} de ${y}`,
  };
}

/** Calcula la duración entre dos horas "HH:MM" → "1h 30min". */
function calcDuracion(hi: string, hf: string): string | null {
  const [h1, m1] = hi.split(":").map(Number);
  const [h2, m2] = hf.split(":").map(Number);
  const mins = (h2 * 60 + m2) - (h1 * 60 + m1);
  if (mins <= 0) return null;
  const h = Math.floor(mins / 60);
  const m = mins % 60;
  if (h === 0) return `${m}min`;
  if (m === 0) return `${h}h`;
  return `${h}h ${m}min`;
}

export default function SesionDetailTab({ planId }: { planId: number }) {
  const { sesionId } = useParams<{ sesionId: string }>();
  const navigate = useNavigate();
  const [sesion, setSesion] = useState<Sesion | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [activando, setActivando] = useState<ParteNombre | null>(null);
  const [editable, setEditable] = useState(false);
  const [tab, setTab] = useState<Tab>("entreno");
  const id = Number(sesionId);

  const refresh = async () => {
    try {
      const s = await api.get<Sesion>(`/api/planificaciones/${planId}/sesiones/${id}`);
      setSesion(s);
    } catch (e: any) {
      setError(e.message);
    }
  };

  useEffect(() => { if (id) refresh(); /* eslint-disable-next-line */ }, [id]);

  const activarParte = async (parte: ParteNombre) => {
    if (!sesion) return;
    setActivando(parte);
    setError(null);
    try {
      const overrides: Partial<Record<ParteNombre, Parte | null>> = {
        [parte]: { objetivo: null, notas: null },
      };
      await api.put(`/api/planificaciones/${planId}/sesiones/${id}`, sesionBody(sesion, overrides));
      await refresh();
    } catch (e: any) {
      setError(e.message);
    } finally {
      setActivando(null);
    }
  };

  const eliminar = async () => {
    if (!confirm("¿Eliminar esta sesión?")) return;
    await api.delete(`/api/planificaciones/${planId}/sesiones/${id}`);
    navigate("../../calendario");
  };

  if (error) return <p className="error">{error}</p>;
  if (!sesion) return <p className="muted">Cargando…</p>;

  return (
    <div>
      <div className="toolbar">
        <Link to="../../calendario" className="muted">← Calendario</Link>
        <Link to="../../horario" className="muted">Horario</Link>
        <div className="spacer" />
        {!editable ? (
          <button
            className="primary"
            onClick={() => setEditable(true)}
            title="Activar modo edición para añadir o modificar ejercicios"
          >
            ✏ Modificar sesión
          </button>
        ) : (
          <>
            <button
              className="ghost"
              onClick={() => setEditable(false)}
              title="Volver al modo visualización (los cambios ya están guardados)"
            >
              ✓ Hecho
            </button>
            <button className="danger" onClick={eliminar}>Eliminar sesión</button>
          </>
        )}
      </div>

      {!editable && (
        <div
          className="card"
          style={{
            background: "var(--primary-muted)",
            border: "1px solid var(--primary)",
            color: "var(--primary)",
            padding: "0.6rem 0.9rem",
            marginBottom: "1rem",
            fontSize: "0.88rem",
            display: "flex",
            alignItems: "center",
            gap: "0.5rem",
          }}
        >
          <span style={{ fontSize: "1rem" }}>👁</span>
          Estás viendo la sesión en modo visualización. Pulsa <strong>Modificar sesión</strong> para añadir o cambiar contenido.
        </div>
      )}

      {(() => {
        const f = formatearFecha(sesion.fecha);
        const hi = hhmm(sesion.horaInicio);
        const hf = hhmm(sesion.horaFin);
        const duracion = (hi && hf) ? calcDuracion(hi, hf) : null;
        const partesActivas = [
          sesion.gimnasio && { k: "gym", label: "Gimnasio" },
          sesion.natacion && { k: "nat", label: "Natación" },
          sesion.waterpolo && { k: "wp", label: "Waterpolo" },
        ].filter(Boolean) as { k: string; label: string }[];

        return (
          <header className="sesion-header">
            <div className="sesion-header-main">
              <div className="sesion-header-date">
                <span className="sesion-header-weekday">{f.diaSemana}</span>
                <h2 className="sesion-header-fecha">{f.fecha}</h2>
              </div>
              <div className="sesion-header-chips">
                {hi && (
                  <span className="sesion-chip sesion-chip-hora" title="Horario">
                    <span className="sesion-chip-icon">🕐</span>
                    {hi}{hf ? ` – ${hf}` : ""}
                    {duracion && <span className="sesion-chip-sub">{duracion}</span>}
                  </span>
                )}
                {sesion.lugar && (
                  <span className="sesion-chip" title="Lugar">
                    <span className="sesion-chip-icon">📍</span>
                    {sesion.lugar}
                  </span>
                )}
                {sesion.tipo && (
                  <span className="sesion-chip sesion-chip-tipo" title="Tipo de sesión">
                    {sesion.tipo}
                  </span>
                )}
                {sesion.estado && (
                  <span className="sesion-chip sesion-chip-estado" title="Estado">
                    {sesion.estado}
                  </span>
                )}
              </div>
            </div>
            {partesActivas.length > 0 && (
              <div className="sesion-header-partes">
                {partesActivas.map((p) => (
                  <span key={p.k} className={`sesion-parte-pill parte-${p.k}`}>
                    {p.label}
                  </span>
                ))}
              </div>
            )}
            {(sesion.objetivo || sesion.notas) && (
              <div className="sesion-header-extra">
                {sesion.objetivo && <p className="sesion-header-objetivo"><strong>Objetivo:</strong> {sesion.objetivo}</p>}
                {sesion.notas && <p className="sesion-header-notas">{sesion.notas}</p>}
              </div>
            )}
          </header>
        );
      })()}

      {/* Pestañas: Entreno (gym/nat/wp) | Asistencia */}
      <div className="sesion-tabs" role="tablist">
        <button
          role="tab"
          aria-selected={tab === "entreno"}
          className={`sesion-tab${tab === "entreno" ? " is-active" : ""}`}
          onClick={() => setTab("entreno")}
        >
          <span className="sesion-tab-icon">📋</span>
          Entreno
        </button>
        <button
          role="tab"
          aria-selected={tab === "asistencia"}
          className={`sesion-tab${tab === "asistencia" ? " is-active" : ""}`}
          onClick={() => setTab("asistencia")}
        >
          <span className="sesion-tab-icon">👥</span>
          Asistencia
        </button>
      </div>

      {tab === "entreno" && (
        <>
          <ParteSeccion
            titulo="Gimnasio"
            className="section-gimnasio"
            activa={!!sesion.gimnasio}
            activando={activando === "gimnasio"}
            editable={editable}
            onActivar={() => activarParte("gimnasio")}
          >
            <GimnasioBloque planId={planId} sesionId={id} editable={editable} />
          </ParteSeccion>

          <ParteSeccion
            titulo="Natación"
            className="section-natacion"
            activa={!!sesion.natacion}
            activando={activando === "natacion"}
            editable={editable}
            onActivar={() => activarParte("natacion")}
          >
            <NatacionBloques planId={planId} sesionId={id} editable={editable} />
          </ParteSeccion>

          <ParteSeccion
            titulo="Waterpolo"
            className="section-waterpolo"
            activa={!!sesion.waterpolo}
            activando={activando === "waterpolo"}
            editable={editable}
            onActivar={() => activarParte("waterpolo")}
          >
            <WaterpoloBloque planId={planId} sesionId={id} editable={editable} />
          </ParteSeccion>
        </>
      )}

      {tab === "asistencia" && (
        <div className="card">
          {/*
            La asistencia siempre es editable, independientemente del modo
            visualización: marcar presencias es trabajo del día a día y no
            forma parte de la planificación del entreno que sí se "modifica".
          */}
          <AsistenciaBloque planId={planId} sesionId={id} editable={true} />
        </div>
      )}
    </div>
  );
}
