import { useEffect, useMemo, useState } from "react";
import { api } from "../../lib/api";
import StackedBarChart, { BarPoint } from "../../components/charts/StackedBarChart";
import LineChart, { LinePoint } from "../../components/charts/LineChart";

interface CargaSemanal {
  semana: string;
  lunes: string;
  cargaNatacion: number;
  cargaGimnasio: number;
  cargaWaterpolo: number;
  metrosAE1: number;
  metrosAE2: number;
  metrosAE3: number;
  metrosAE4: number;
  metrosAE5: number;
  minutosWaterpolo: number;
  volumenGimnasio: number;
  sesiones: number;
}
interface CargaResumen {
  totalNatacion: number;
  totalGimnasio: number;
  totalWaterpolo: number;
  metrosNatacion: number;
  minutosWaterpolo: number;
  volumenGimnasio: number;
  sesionesTotal: number;
}
interface CargaResponse {
  resumen: CargaResumen;
  semanas: CargaSemanal[];
}

interface Props {
  planNumero: number;
  desde: string;
  hasta: string;
}

const COLOR = {
  nat: "#0ea5e9",
  gym: "#f97316",
  wp:  "#10b981",
  AE1: "#cbd5e1",
  AE2: "#7dd3fc",
  AE3: "#38bdf8",
  AE4: "#0ea5e9",
  AE5: "#0c4a6e",
};

function shortSemana(semana: string) {
  // "2026-W12" → "W12"
  const idx = semana.lastIndexOf("W");
  return idx >= 0 ? semana.slice(idx) : semana;
}

export default function CargaPanel({ planNumero, desde, hasta }: Props) {
  const [data, setData] = useState<CargaResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    api.get<CargaResponse>(`/api/metricas/${planNumero}/carga?desde=${desde}&hasta=${hasta}`)
      .then(setData)
      .catch((e: any) => setError(e.message || "Error cargando métricas"))
      .finally(() => setLoading(false));
  }, [planNumero, desde, hasta]);

  // Carga total por modalidad apilada por semana
  const dataPorModalidad: BarPoint[] = useMemo(() => {
    if (!data) return [];
    return data.semanas.map((s) => ({
      label: shortSemana(s.semana),
      tooltipLabel: `Semana ${s.semana} (lun ${s.lunes})`,
      values: {
        nat: s.cargaNatacion,
        gym: s.cargaGimnasio,
        wp:  s.cargaWaterpolo,
      },
    }));
  }, [data]);

  // Metros de natación por zona AE apilados
  const dataPorZonaAE: BarPoint[] = useMemo(() => {
    if (!data) return [];
    return data.semanas.map((s) => ({
      label: shortSemana(s.semana),
      tooltipLabel: `Semana ${s.semana} (lun ${s.lunes})`,
      values: {
        AE1: s.metrosAE1,
        AE2: s.metrosAE2,
        AE3: s.metrosAE3,
        AE4: s.metrosAE4,
        AE5: s.metrosAE5,
      },
    }));
  }, [data]);

  // Línea con carga total semanal (suma de las 3)
  const dataLinea: LinePoint[] = useMemo(() => {
    if (!data) return [];
    return data.semanas.map((s) => ({
      label: shortSemana(s.semana),
      tooltipLabel: `Semana ${s.semana}`,
      value: s.cargaNatacion + s.cargaGimnasio + s.cargaWaterpolo,
    }));
  }, [data]);

  // Distribución porcentual
  const porcentajes = useMemo(() => {
    if (!data) return null;
    const tot = data.resumen.totalNatacion + data.resumen.totalGimnasio + data.resumen.totalWaterpolo;
    if (tot === 0) return null;
    return {
      nat: (data.resumen.totalNatacion / tot) * 100,
      gym: (data.resumen.totalGimnasio / tot) * 100,
      wp:  (data.resumen.totalWaterpolo / tot) * 100,
    };
  }, [data]);

  if (loading) return <p className="muted">Cargando métricas…</p>;
  if (error) return <p className="error">{error}</p>;
  if (!data) return null;

  const sinDatos = data.resumen.sesionesTotal === 0
    || (data.resumen.totalNatacion === 0 && data.resumen.totalGimnasio === 0 && data.resumen.totalWaterpolo === 0);

  return (
    <div>
      <div className="metricas-kpis">
        <div className="metricas-kpi">
          <div className="metricas-kpi-label">Sesiones</div>
          <div className="metricas-kpi-value">{data.resumen.sesionesTotal}</div>
          <div className="metricas-kpi-sub">en el rango</div>
        </div>
        <div className="metricas-kpi" style={{ borderTop: `3px solid ${COLOR.nat}` }}>
          <div className="metricas-kpi-label">Natación</div>
          <div className="metricas-kpi-value">{data.resumen.metrosNatacion.toLocaleString("es-ES")} m</div>
          <div className="metricas-kpi-sub">Carga: {Math.round(data.resumen.totalNatacion).toLocaleString("es-ES")}</div>
        </div>
        <div className="metricas-kpi" style={{ borderTop: `3px solid ${COLOR.gym}` }}>
          <div className="metricas-kpi-label">Gimnasio</div>
          <div className="metricas-kpi-value">{data.resumen.volumenGimnasio.toLocaleString("es-ES")}</div>
          <div className="metricas-kpi-sub">reps · Carga: {Math.round(data.resumen.totalGimnasio).toLocaleString("es-ES")}</div>
        </div>
        <div className="metricas-kpi" style={{ borderTop: `3px solid ${COLOR.wp}` }}>
          <div className="metricas-kpi-label">Waterpolo</div>
          <div className="metricas-kpi-value">{data.resumen.minutosWaterpolo.toLocaleString("es-ES")} min</div>
          <div className="metricas-kpi-sub">Carga: {Math.round(data.resumen.totalWaterpolo).toLocaleString("es-ES")}</div>
        </div>
      </div>

      {sinDatos && (
        <p className="muted" style={{ marginBottom: 16 }}>
          Sin datos en el rango seleccionado. Crea sesiones con ejercicios/bloques para ver gráficos.
        </p>
      )}

      <div className="chart-card">
        <h3>Carga semanal por modalidad</h3>
        <p className="chart-card-sub">
          Cada barra es una semana. Unidades comparables solo dentro de la misma modalidad.
        </p>
        <StackedBarChart
          data={dataPorModalidad}
          series={[
            { key: "nat", label: "Natación", color: COLOR.nat },
            { key: "gym", label: "Gimnasio", color: COLOR.gym },
            { key: "wp",  label: "Waterpolo", color: COLOR.wp },
          ]}
        />
      </div>

      <div className="chart-card">
        <h3>Metros de natación por zona AE</h3>
        <p className="chart-card-sub">
          Distribución del volumen de natación entre las cinco zonas fisiológicas.
        </p>
        <StackedBarChart
          data={dataPorZonaAE}
          series={[
            { key: "AE1", label: "AE1 · recuperación", color: COLOR.AE1 },
            { key: "AE2", label: "AE2 · resistencia básica", color: COLOR.AE2 },
            { key: "AE3", label: "AE3 · umbral", color: COLOR.AE3 },
            { key: "AE4", label: "AE4 · VO2max", color: COLOR.AE4 },
            { key: "AE5", label: "AE5 · sprint", color: COLOR.AE5 },
          ]}
          yAxisLabel="metros"
        />
      </div>

      <div className="chart-card">
        <h3>Carga total semanal</h3>
        <p className="chart-card-sub">Suma de las tres modalidades. Útil para detectar picos y semanas de descarga.</p>
        <LineChart data={dataLinea} color="#6366f1" />
      </div>

      {porcentajes && (
        <div className="chart-card">
          <h3>Distribución entre modalidades</h3>
          <p className="chart-card-sub">¿Cuánto pesa cada modalidad sobre el total del rango?</p>
          <div style={{ display: "flex", height: 32, borderRadius: 16, overflow: "hidden", border: "1px solid var(--border)" }}>
            <div style={{ width: `${porcentajes.nat}%`, background: COLOR.nat, color: "white", textAlign: "center", lineHeight: "32px", fontSize: "0.78rem", fontWeight: 600 }}>
              {porcentajes.nat >= 8 && `Natación ${porcentajes.nat.toFixed(0)}%`}
            </div>
            <div style={{ width: `${porcentajes.gym}%`, background: COLOR.gym, color: "white", textAlign: "center", lineHeight: "32px", fontSize: "0.78rem", fontWeight: 600 }}>
              {porcentajes.gym >= 8 && `Gimnasio ${porcentajes.gym.toFixed(0)}%`}
            </div>
            <div style={{ width: `${porcentajes.wp}%`, background: COLOR.wp, color: "white", textAlign: "center", lineHeight: "32px", fontSize: "0.78rem", fontWeight: 600 }}>
              {porcentajes.wp >= 8 && `Waterpolo ${porcentajes.wp.toFixed(0)}%`}
            </div>
          </div>
          <p className="chart-card-sub" style={{ marginTop: 8 }}>
            Natación {porcentajes.nat.toFixed(1)}% · Gimnasio {porcentajes.gym.toFixed(1)}% · Waterpolo {porcentajes.wp.toFixed(1)}%
          </p>
        </div>
      )}
    </div>
  );
}
