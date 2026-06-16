import { useEffect, useMemo, useState } from "react";
import { api } from "../../lib/api";
import LineChart, { LinePoint } from "../../components/charts/LineChart";
import Heatmap, { HeatmapRow, HeatmapCol, HeatmapCellValue } from "../../components/charts/Heatmap";
import RankingBar, { RankingItem } from "../../components/charts/RankingBar";

interface Resumen {
  sesiones: number;
  jugadores: number;
  totalRegistros: number;
  totalPresentes: number;
  porcentajeMedio: number;
}
interface SemanaAsis {
  semana: string;
  lunes: string;
  sesiones: number;
  presentes: number;
  total: number;
  porcentajePresente: number;
}
interface JugadorAsis {
  jugadorId: number;
  nombre: string;
  apellidos?: string;
  presentes: number;
  ausentes: number;
  justificadas: number;
  otras: number;
  totalRegistradas: number;
  sesionesPosibles: number;
  porcentajePresente: number;
}
interface SesionAsis {
  sesionDiaId: number;
  fecha: string;
  presentes: number;
  total: number;
}
interface HeatmapEntry {
  jugadorId: number;
  sesionDiaId: number;
  estado: string;
}
interface AsistenciaResp {
  resumen: Resumen;
  semanas: SemanaAsis[];
  jugadores: JugadorAsis[];
  sesiones: SesionAsis[];
  heatmap: HeatmapEntry[];
}

interface Props {
  planNumero: number;
  desde: string;
  hasta: string;
}

function shortSemana(semana: string) {
  const idx = semana.lastIndexOf("W");
  return idx >= 0 ? semana.slice(idx) : semana;
}

function fechaCorta(iso: string) {
  // "2026-06-15" → "15/06"
  const parts = iso.split("-");
  if (parts.length !== 3) return iso;
  return `${parts[2]}/${parts[1]}`;
}

export default function AsistenciaPanel({ planNumero, desde, hasta }: Props) {
  const [data, setData] = useState<AsistenciaResp | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    api.get<AsistenciaResp>(`/api/metricas/${planNumero}/asistencia?desde=${desde}&hasta=${hasta}`)
      .then(setData)
      .catch((e: any) => setError(e.message || "Error cargando asistencia"))
      .finally(() => setLoading(false));
  }, [planNumero, desde, hasta]);

  const dataLineaSemanal: LinePoint[] = useMemo(() => {
    if (!data) return [];
    return data.semanas.map((s) => ({
      label: shortSemana(s.semana),
      tooltipLabel: `Semana ${s.semana} (lun ${s.lunes})`,
      value: s.porcentajePresente,
    }));
  }, [data]);

  const rankingJugadores: RankingItem[] = useMemo(() => {
    if (!data) return [];
    return data.jugadores.map((j) => ({
      id: j.jugadorId,
      label: `${j.nombre}${j.apellidos ? " " + j.apellidos : ""}`,
      subtitle: `${j.presentes}/${j.sesionesPosibles} sesiones`,
      value: j.porcentajePresente,
      rightText: `${j.porcentajePresente.toFixed(0)}%`,
    }));
  }, [data]);

  const rankingSesiones: RankingItem[] = useMemo(() => {
    if (!data || data.resumen.jugadores === 0) return [];
    return data.sesiones
      .map((s) => ({
        id: s.sesionDiaId,
        label: fechaCorta(s.fecha),
        subtitle: `${s.presentes}/${s.total} jugadores`,
        value: (s.presentes / Math.max(1, s.total)) * 100,
        rightText: `${((s.presentes / Math.max(1, s.total)) * 100).toFixed(0)}%`,
      }))
      // ordenar por menor asistencia para destacar sesiones problemáticas
      .sort((a, b) => a.value - b.value)
      .slice(0, 10);
  }, [data]);

  const heatmapRows: HeatmapRow[] = useMemo(
    () => data?.jugadores.map((j) => ({
      id: j.jugadorId,
      label: `${j.nombre}${j.apellidos ? " " + j.apellidos.charAt(0) + "." : ""}`,
    })) ?? [],
    [data],
  );
  const heatmapCols: HeatmapCol[] = useMemo(
    () => data?.sesiones.map((s, i) => ({
      id: s.sesionDiaId,
      label: data.sesiones.length <= 16 ? fechaCorta(s.fecha) : `${i + 1}`,
      tooltipLabel: s.fecha,
    })) ?? [],
    [data],
  );
  const heatmapCells: HeatmapCellValue[] = useMemo(
    () => data?.heatmap.map((c) => ({
      rowId: c.jugadorId,
      colId: c.sesionDiaId,
      estado: c.estado,
    })) ?? [],
    [data],
  );

  if (loading) return <p className="muted">Cargando asistencia…</p>;
  if (error) return <p className="error">{error}</p>;
  if (!data) return null;

  const sinDatos = data.resumen.totalRegistros === 0;

  return (
    <div>
      <div className="metricas-kpis">
        <div className="metricas-kpi">
          <div className="metricas-kpi-label">Asistencia media</div>
          <div className="metricas-kpi-value">{data.resumen.porcentajeMedio.toFixed(1)}%</div>
          <div className="metricas-kpi-sub">{data.resumen.totalPresentes} presencias registradas</div>
        </div>
        <div className="metricas-kpi">
          <div className="metricas-kpi-label">Sesiones</div>
          <div className="metricas-kpi-value">{data.resumen.sesiones}</div>
          <div className="metricas-kpi-sub">en el rango</div>
        </div>
        <div className="metricas-kpi">
          <div className="metricas-kpi-label">Jugadores</div>
          <div className="metricas-kpi-value">{data.resumen.jugadores}</div>
          <div className="metricas-kpi-sub">en plantilla</div>
        </div>
        <div className="metricas-kpi">
          <div className="metricas-kpi-label">Registros</div>
          <div className="metricas-kpi-value">{data.resumen.totalRegistros}</div>
          <div className="metricas-kpi-sub">total de asistencias</div>
        </div>
      </div>

      {sinDatos && (
        <p className="muted" style={{ marginBottom: 16 }}>
          No hay registros de asistencia en el rango. Marca presencias en las sesiones para que aparezcan.
        </p>
      )}

      <div className="chart-card">
        <h3>Asistencia semanal del equipo (%)</h3>
        <p className="chart-card-sub">Promedio de presencias por sesión, agregado por semana.</p>
        <LineChart data={dataLineaSemanal} color="#16a34a" fixedMax yMaxOverride={100} formatValue={(n) => `${n.toFixed(0)}%`} />
      </div>

      <div className="chart-card">
        <h3>Ranking de asistencia por jugador</h3>
        <p className="chart-card-sub">
          Verde ≥ 80% · ámbar 60-80% · rojo &lt; 60%.
        </p>
        <RankingBar items={rankingJugadores} />
      </div>

      {rankingSesiones.length > 0 && (
        <div className="chart-card">
          <h3>Sesiones con menor asistencia</h3>
          <p className="chart-card-sub">Top 10 ordenadas por porcentaje de presencias.</p>
          <RankingBar items={rankingSesiones} />
        </div>
      )}

      <div className="chart-card">
        <h3>Heatmap jugador × sesión</h3>
        <p className="chart-card-sub">Cada columna es una sesión cronológica. Pasa el cursor sobre las celdas para ver el detalle.</p>
        <Heatmap rows={heatmapRows} cols={heatmapCols} cells={heatmapCells} />
      </div>
    </div>
  );
}
