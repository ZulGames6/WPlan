/**
 * Gráfico de barras apiladas en SVG, sin dependencias.
 * Cada barra representa una "semana" (o cualquier categoría discreta) y se
 * apilan los segmentos definidos en `series`. Las series sin valor se omiten.
 *
 * Tooltips nativos vía <title>. Ejes con grid horizontal punteado.
 */
import { useMemo } from "react";

export interface SerieBar {
  key: string;
  label: string;
  color: string;
}

export interface BarPoint {
  /** Etiqueta corta del eje X (ej. "S12"). */
  label: string;
  /** Etiqueta larga para el tooltip. */
  tooltipLabel?: string;
  /** Valores por key de serie. */
  values: Record<string, number>;
}

export interface StackedBarChartProps {
  data: BarPoint[];
  series: SerieBar[];
  height?: number;
  yAxisLabel?: string;
  formatValue?: (n: number) => string;
}

export default function StackedBarChart({
  data,
  series,
  height = 240,
  yAxisLabel,
  formatValue = (n) => Math.round(n).toLocaleString("es-ES"),
}: StackedBarChartProps) {
  const padding = { top: 12, right: 16, bottom: 32, left: 44 };

  const max = useMemo(() => {
    let m = 0;
    for (const p of data) {
      let s = 0;
      for (const sr of series) s += p.values[sr.key] ?? 0;
      if (s > m) m = s;
    }
    return m || 1;
  }, [data, series]);

  const ticks = useMemo(() => niceTicks(0, max, 4), [max]);
  const niceMax = ticks[ticks.length - 1];

  // Ancho: 36px por barra + padding mínimo
  const barWidth = 28;
  const barGap = 10;
  const innerWidth = Math.max(
    300,
    data.length * (barWidth + barGap) - barGap
  );
  const width = innerWidth + padding.left + padding.right;
  const chartHeight = height - padding.top - padding.bottom;

  const yScale = (v: number) => padding.top + chartHeight - (v / niceMax) * chartHeight;
  const x0 = (i: number) => padding.left + i * (barWidth + barGap);

  return (
    <div className="chart-wrapper">
      <svg width="100%" viewBox={`0 0 ${width} ${height}`} className="chart-svg">
        {/* Grid horizontal */}
        {ticks.map((t, i) => (
          <g key={i} className="chart-grid">
            <line x1={padding.left} y1={yScale(t)} x2={width - padding.right} y2={yScale(t)} />
            <text x={padding.left - 8} y={yScale(t) + 4} textAnchor="end" className="chart-tick-label">
              {formatValue(t)}
            </text>
          </g>
        ))}

        {/* Eje X */}
        <line
          x1={padding.left}
          y1={padding.top + chartHeight}
          x2={width - padding.right}
          y2={padding.top + chartHeight}
          className="chart-axis"
        />

        {/* Barras */}
        {data.map((p, i) => {
          let acc = 0;
          const totalPunto = series.reduce((s, sr) => s + (p.values[sr.key] ?? 0), 0);
          return (
            <g key={i}>
              {series.map((sr) => {
                const v = p.values[sr.key] ?? 0;
                if (v <= 0) return null;
                const y = yScale(acc + v);
                const h = (v / niceMax) * chartHeight;
                acc += v;
                return (
                  <rect
                    key={sr.key}
                    x={x0(i)}
                    y={y}
                    width={barWidth}
                    height={h}
                    fill={sr.color}
                    rx={2}
                  >
                    <title>{`${p.tooltipLabel ?? p.label} · ${sr.label}: ${formatValue(v)}`}</title>
                  </rect>
                );
              })}
              {totalPunto > 0 && (
                <text
                  x={x0(i) + barWidth / 2}
                  y={yScale(totalPunto) - 4}
                  textAnchor="middle"
                  className="chart-bar-total"
                >
                  {formatValue(totalPunto)}
                </text>
              )}
              <text
                x={x0(i) + barWidth / 2}
                y={padding.top + chartHeight + 16}
                textAnchor="middle"
                className="chart-x-label"
              >
                {p.label}
              </text>
            </g>
          );
        })}

        {yAxisLabel && (
          <text
            x={14}
            y={padding.top + chartHeight / 2}
            transform={`rotate(-90 14 ${padding.top + chartHeight / 2})`}
            textAnchor="middle"
            className="chart-axis-label"
          >
            {yAxisLabel}
          </text>
        )}
      </svg>

      {/* Leyenda */}
      <div className="chart-legend">
        {series.map((sr) => (
          <span key={sr.key} className="chart-legend-item">
            <span className="chart-legend-swatch" style={{ background: sr.color }} />
            {sr.label}
          </span>
        ))}
      </div>
    </div>
  );
}

/** Devuelve N+1 ticks "agradables" entre min y max, redondeados. */
function niceTicks(min: number, max: number, target: number): number[] {
  if (max <= min) return [0, 1];
  const range = max - min;
  const step = niceStep(range / target);
  const start = Math.floor(min / step) * step;
  const end = Math.ceil(max / step) * step;
  const ticks: number[] = [];
  for (let v = start; v <= end + 1e-9; v += step) ticks.push(round(v));
  return ticks;
}

function niceStep(rough: number): number {
  const exp = Math.floor(Math.log10(rough));
  const f = rough / Math.pow(10, exp);
  let nf: number;
  if (f < 1.5) nf = 1;
  else if (f < 3) nf = 2;
  else if (f < 7) nf = 5;
  else nf = 10;
  return nf * Math.pow(10, exp);
}

function round(v: number): number {
  return Math.round(v * 1000) / 1000;
}
