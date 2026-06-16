/**
 * Gráfico de línea simple en SVG (sin dependencias). Una sola serie, con
 * puntos clickables y tooltips nativos. Pensado para evolución temporal:
 * carga total semanal, porcentaje de asistencia semanal, etc.
 */
import { useMemo } from "react";

export interface LinePoint {
  label: string;
  tooltipLabel?: string;
  value: number;
}

export interface LineChartProps {
  data: LinePoint[];
  color?: string;
  height?: number;
  yMaxOverride?: number;
  yAxisLabel?: string;
  formatValue?: (n: number) => string;
  /** Si está activo, el tick de Y final no se redondea hacia arriba (útil con %). */
  fixedMax?: boolean;
}

export default function LineChart({
  data,
  color = "#3b82f6",
  height = 220,
  yMaxOverride,
  yAxisLabel,
  formatValue = (n) => Math.round(n).toLocaleString("es-ES"),
  fixedMax = false,
}: LineChartProps) {
  const padding = { top: 12, right: 16, bottom: 32, left: 44 };

  const max = useMemo(() => {
    if (yMaxOverride !== undefined) return yMaxOverride;
    let m = 0;
    for (const p of data) if (p.value > m) m = p.value;
    return m || 1;
  }, [data, yMaxOverride]);

  const ticks = useMemo(() => {
    if (fixedMax) {
      const step = max / 4;
      return [0, step, step * 2, step * 3, max];
    }
    return niceTicks(0, max, 4);
  }, [max, fixedMax]);
  const niceMax = ticks[ticks.length - 1];

  const innerWidth = Math.max(300, data.length * 60);
  const width = innerWidth + padding.left + padding.right;
  const chartHeight = height - padding.top - padding.bottom;

  const xStep = data.length > 1 ? innerWidth / (data.length - 1) : 0;
  const x = (i: number) => padding.left + i * xStep;
  const y = (v: number) => padding.top + chartHeight - (v / niceMax) * chartHeight;

  const path = data.map((p, i) => `${i === 0 ? "M" : "L"} ${x(i)} ${y(p.value)}`).join(" ");
  const area = data.length > 0
    ? `${path} L ${x(data.length - 1)} ${padding.top + chartHeight} L ${x(0)} ${padding.top + chartHeight} Z`
    : "";

  return (
    <div className="chart-wrapper">
      <svg width="100%" viewBox={`0 0 ${width} ${height}`} className="chart-svg">
        {ticks.map((t, i) => (
          <g key={i} className="chart-grid">
            <line x1={padding.left} y1={y(t)} x2={width - padding.right} y2={y(t)} />
            <text x={padding.left - 8} y={y(t) + 4} textAnchor="end" className="chart-tick-label">
              {formatValue(t)}
            </text>
          </g>
        ))}

        <line
          x1={padding.left}
          y1={padding.top + chartHeight}
          x2={width - padding.right}
          y2={padding.top + chartHeight}
          className="chart-axis"
        />

        {data.length > 0 && (
          <>
            <path d={area} fill={color} opacity={0.12} />
            <path d={path} fill="none" stroke={color} strokeWidth={2.2} strokeLinecap="round" strokeLinejoin="round" />
            {data.map((p, i) => (
              <g key={i}>
                <circle cx={x(i)} cy={y(p.value)} r={4} fill="var(--bg)" stroke={color} strokeWidth={2}>
                  <title>{`${p.tooltipLabel ?? p.label}: ${formatValue(p.value)}`}</title>
                </circle>
                <text
                  x={x(i)}
                  y={padding.top + chartHeight + 16}
                  textAnchor="middle"
                  className="chart-x-label"
                >
                  {p.label}
                </text>
              </g>
            ))}
          </>
        )}

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
    </div>
  );
}

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
