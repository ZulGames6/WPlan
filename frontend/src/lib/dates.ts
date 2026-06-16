/** Fecha local YYYY-MM-DD (sin desfase UTC). */
export function isoLocal(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

const DIAS_CORTO = ["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"];
const DIAS_LARGO = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"];

export function nombreDiaCorto(d: Date) {
  return DIAS_CORTO[d.getDay()];
}

export function nombreDiaLargo(d: Date) {
  return DIAS_LARGO[d.getDay()];
}

/** Lunes–domingo de la semana que contiene `anchor`, con desplazamiento en semanas. */
export function getWeekRange(anchor = new Date(), weekOffset = 0) {
  const ref = new Date(anchor.getFullYear(), anchor.getMonth(), anchor.getDate(), 12);
  const dow = ref.getDay();
  const diffMonday = dow === 0 ? -6 : 1 - dow;
  const monday = new Date(ref);
  monday.setDate(ref.getDate() + diffMonday + weekOffset * 7);

  const days: Date[] = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(monday);
    d.setDate(monday.getDate() + i);
    days.push(d);
  }
  return { start: days[0], end: days[6], days };
}

export function formatWeekTitle(start: Date, end: Date) {
  const opts: Intl.DateTimeFormatOptions = { day: "numeric", month: "long" };
  const y = start.getFullYear() === end.getFullYear() ? start.getFullYear() : undefined;
  const s = start.toLocaleDateString("es-ES", { ...opts, year: start.getMonth() !== end.getMonth() ? "numeric" : undefined });
  const e = end.toLocaleDateString("es-ES", { ...opts, year: "numeric" });
  if (start.getMonth() === end.getMonth() && start.getFullYear() === end.getFullYear()) {
    return `${start.getDate()} – ${end.getDate()} de ${end.toLocaleDateString("es-ES", { month: "long", year: "numeric" })}`;
  }
  return `${s} – ${e}`;
}

export function isSameDay(a: Date, b: Date) {
  return isoLocal(a) === isoLocal(b);
}
