/** ISO date (YYYY-MM-DD) → día semana backend (1=lunes … 7=domingo). */
export function diaSemanaFromIso(iso: string): number {
  const [y, m, d] = iso.split("-").map(Number);
  const dow = new Date(y, m - 1, d).getDay();
  return dow === 0 ? 7 : dow;
}

export interface HorarioDia {
  diaSemana: number;
  nombreDia: string;
  activo: boolean;
  horaInicio?: string | null;
  horaFin?: string | null;
  lugar?: string | null;
  conGimnasio: boolean;
  conNatacion: boolean;
  conWaterpolo: boolean;
}

export interface SemanaResponse {
  dias: HorarioDia[];
}

export function horarioParaFecha(semana: SemanaResponse, iso: string): HorarioDia | undefined {
  const dia = diaSemanaFromIso(iso);
  return semana.dias.find((d) => d.diaSemana === dia);
}

export function toTimeInput(v?: string | null) {
  if (!v) return "";
  return v.length >= 5 ? v.slice(0, 5) : v;
}
