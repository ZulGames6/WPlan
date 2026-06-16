/**
 * Ranking horizontal: una barra por fila, ordenadas por valor. Pensado para
 * % de asistencia por jugador. Color por umbral (verde >= 80, amarillo 60-80,
 * rojo < 60).
 */

export interface RankingItem {
  id: number | string;
  label: string;
  value: number;       // 0..100 (se usa como ancho de la barra)
  rightText?: string;  // texto a la derecha (opcional)
  subtitle?: string;   // texto debajo del label (opcional)
}

interface Props {
  items: RankingItem[];
  /** valor máximo a representar; por defecto 100 (porcentaje). */
  maxValue?: number;
}

function colorByThreshold(pct: number): string {
  if (pct >= 80) return "#16a34a";
  if (pct >= 60) return "#f59e0b";
  return "#dc2626";
}

export default function RankingBar({ items, maxValue = 100 }: Props) {
  if (items.length === 0) {
    return <p className="muted">Sin datos para mostrar.</p>;
  }
  return (
    <div className="ranking-bar">
      {items.map((it) => {
        const pct = Math.max(0, Math.min(100, (it.value / maxValue) * 100));
        return (
          <div key={it.id} className="ranking-bar-row">
            <div className="ranking-bar-label">
              <span className="ranking-bar-name" title={it.label}>{it.label}</span>
              {it.subtitle && <span className="ranking-bar-sub">{it.subtitle}</span>}
            </div>
            <div className="ranking-bar-track">
              <div
                className="ranking-bar-fill"
                style={{ width: `${pct}%`, background: colorByThreshold(it.value) }}
              />
              <span className="ranking-bar-value">{it.rightText ?? `${pct.toFixed(0)}%`}</span>
            </div>
          </div>
        );
      })}
    </div>
  );
}
