import { useEffect, useMemo, useState } from "react";
import { api } from "../../lib/api";
import CargaPanel from "../metricas/CargaPanel";
import AsistenciaPanel from "../metricas/AsistenciaPanel";

interface PlanRange {
  fechaInicio?: string | null;
  fechaFin?: string | null;
}

function isoLocal(d: Date) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

type Preset = "1m" | "3m" | "6m" | "temporada" | "personalizado";

export default function MetricasTab({ planId }: { planId: number }) {
  const [planRange, setPlanRange] = useState<PlanRange | null>(null);
  const [preset, setPreset] = useState<Preset>("3m");
  const [desde, setDesde] = useState<string>("");
  const [hasta, setHasta] = useState<string>("");
  const [tab, setTab] = useState<"carga" | "asistencia">("carga");

  // Carga el rango de fechas de la planificación para el preset "temporada"
  useEffect(() => {
    api.get<PlanRange>(`/api/planificaciones/${planId}`)
      .then(setPlanRange)
      .catch(() => setPlanRange({}));
  }, [planId]);

  useEffect(() => {
    if (preset === "personalizado") return;
    const today = new Date();
    let inicio: Date;
    let fin = today;
    if (preset === "1m") {
      inicio = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    } else if (preset === "3m") {
      inicio = new Date(today.getFullYear(), today.getMonth() - 3, today.getDate());
    } else if (preset === "6m") {
      inicio = new Date(today.getFullYear(), today.getMonth() - 6, today.getDate());
    } else {
      if (!planRange) return;
      inicio = planRange.fechaInicio ? new Date(planRange.fechaInicio) : new Date(today.getFullYear(), 0, 1);
      fin = planRange.fechaFin ? new Date(planRange.fechaFin) : today;
    }
    setDesde(isoLocal(inicio));
    setHasta(isoLocal(fin));
  }, [preset, planRange]);

  const ready = useMemo(() => Boolean(desde && hasta), [desde, hasta]);

  return (
    <div>
      <p className="muted" style={{ marginTop: 0 }}>
        Carga acumulada por modalidad y asistencia del equipo. Cambia el rango para recalcular.
      </p>

      <div className="metricas-toolbar">
        <div style={{ display: "flex", flexDirection: "column" }}>
          <label>Rango</label>
          <select value={preset} onChange={(e) => setPreset(e.target.value as Preset)}>
            <option value="1m">Último mes</option>
            <option value="3m">Últimos 3 meses</option>
            <option value="6m">Últimos 6 meses</option>
            <option value="temporada">Toda la temporada</option>
            <option value="personalizado">Personalizado</option>
          </select>
        </div>
        <div style={{ display: "flex", flexDirection: "column" }}>
          <label>Desde</label>
          <input type="date" value={desde} onChange={(e) => { setDesde(e.target.value); setPreset("personalizado"); }} />
        </div>
        <div style={{ display: "flex", flexDirection: "column" }}>
          <label>Hasta</label>
          <input type="date" value={hasta} onChange={(e) => { setHasta(e.target.value); setPreset("personalizado"); }} />
        </div>
      </div>

      <div className="metricas-tabs" role="tablist">
        <button
          role="tab"
          aria-selected={tab === "carga"}
          className={`metricas-tab${tab === "carga" ? " is-active" : ""}`}
          onClick={() => setTab("carga")}
        >
          Carga
        </button>
        <button
          role="tab"
          aria-selected={tab === "asistencia"}
          className={`metricas-tab${tab === "asistencia" ? " is-active" : ""}`}
          onClick={() => setTab("asistencia")}
        >
          Asistencia
        </button>
      </div>

      {ready && (
        tab === "carga"
          ? <CargaPanel planNumero={planId} desde={desde} hasta={hasta} />
          : <AsistenciaPanel planNumero={planId} desde={desde} hasta={hasta} />
      )}
    </div>
  );
}
