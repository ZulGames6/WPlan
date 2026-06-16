/**
 * Heatmap de asistencia: filas = jugadores, columnas = sesiones.
 * Cada celda colorea según el estado (PRESENTE/AUSENTE/JUSTIFICADA/otro).
 */

export interface HeatmapRow {
  id: number | string;
  label: string;
}

export interface HeatmapCol {
  id: number | string;
  label: string;
  tooltipLabel?: string;
}

export interface HeatmapCellValue {
  rowId: number | string;
  colId: number | string;
  estado: string;
}

interface Props {
  rows: HeatmapRow[];
  cols: HeatmapCol[];
  cells: HeatmapCellValue[];
}

const COLOR_BY_ESTADO: Record<string, string> = {
  PRESENTE: "#16a34a",
  AUSENTE: "#dc2626",
  JUSTIFICADA: "#f59e0b",
  JUSTIFICADO: "#f59e0b",
  LESION: "#9333ea",
  LESIONADO: "#9333ea",
  ENFERMO: "#9333ea",
  OTRA: "#94a3b8",
};

function colorFor(estado: string): string {
  return COLOR_BY_ESTADO[estado?.toUpperCase()] ?? "#94a3b8";
}

export default function Heatmap({ rows, cols, cells }: Props) {
  if (rows.length === 0 || cols.length === 0) {
    return <p className="muted">Sin datos para mostrar.</p>;
  }
  // Mapa rápido de celdas
  const map = new Map<string, string>();
  for (const c of cells) {
    map.set(`${c.rowId}::${c.colId}`, c.estado);
  }

  const cellW = 26;
  const cellH = 22;
  const labelW = 140;
  const headerH = 30;
  const width = labelW + cols.length * cellW + 20;
  const height = headerH + rows.length * cellH + 16;

  return (
    <div className="heatmap-wrapper">
      <svg width={width} height={height} className="chart-svg heatmap-svg">
        {/* Encabezado: índice de columna */}
        {cols.map((col, i) => (
          <g key={col.id}>
            <text
              x={labelW + i * cellW + cellW / 2}
              y={headerH - 8}
              textAnchor="middle"
              className="heatmap-col-label"
            >
              {col.label}
            </text>
          </g>
        ))}

        {/* Filas */}
        {rows.map((row, r) => (
          <g key={row.id}>
            <text
              x={labelW - 8}
              y={headerH + r * cellH + cellH / 2 + 4}
              textAnchor="end"
              className="heatmap-row-label"
            >
              {row.label}
            </text>
            {cols.map((col, c) => {
              const estado = map.get(`${row.id}::${col.id}`);
              const fill = estado ? colorFor(estado) : "var(--surface-alt)";
              const isMissing = !estado;
              return (
                <rect
                  key={col.id}
                  x={labelW + c * cellW + 2}
                  y={headerH + r * cellH + 2}
                  width={cellW - 4}
                  height={cellH - 4}
                  fill={fill}
                  opacity={isMissing ? 0.25 : 0.92}
                  rx={3}
                >
                  <title>
                    {row.label} · {col.tooltipLabel ?? col.label}: {estado ?? "Sin registrar"}
                  </title>
                </rect>
              );
            })}
          </g>
        ))}
      </svg>

      <div className="heatmap-legend">
        {[
          { k: "PRESENTE", l: "Presente" },
          { k: "AUSENTE", l: "Ausente" },
          { k: "JUSTIFICADA", l: "Justificada" },
          { k: "LESION", l: "Lesión" },
          { k: "MISSING", l: "Sin registrar" },
        ].map((it) => (
          <span key={it.k} className="heatmap-legend-item">
            <span
              className="heatmap-legend-swatch"
              style={{
                background: it.k === "MISSING" ? "var(--surface-alt)" : colorFor(it.k),
                opacity: it.k === "MISSING" ? 0.5 : 0.9,
              }}
            />
            {it.l}
          </span>
        ))}
      </div>
    </div>
  );
}
