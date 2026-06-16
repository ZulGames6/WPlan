package com.poloplan.dto;

import java.time.LocalDate;
import java.util.List;

public class MetricasDtos {

  // ── CARGA ────────────────────────────────────────────────────────────────

  /**
   * Resumen acumulado de carga en el rango consultado, por modalidad.
   * Las unidades no son comparables directamente entre modalidades; cada gráfico
   * usa la suya y se ofrece también una vista "porcentaje" para detectar
   * desequilibrios.
   *  - cargaNatacion : metros × factor AE (AE1=1 … AE5=8)
   *  - cargaGimnasio : Σ series × reps × (%RM/100); si no hay %RM, suma series×reps
   *  - cargaWaterpolo: Σ duracionMin × peso de intensidad (BAJA=1, MEDIA=1.5, ALTA=2)
   */
  public record CargaResumen(
    double totalNatacion,
    double totalGimnasio,
    double totalWaterpolo,
    int metrosNatacion,
    int minutosWaterpolo,
    int volumenGimnasio,
    int sesionesTotal
  ) {}

  /** Distribución semanal por modalidad y por zona AE de natación. */
  public record CargaSemanal(
    String semana,            // "YYYY-Www" (ISO week)
    LocalDate lunes,          // lunes de la semana ISO
    double cargaNatacion,
    double cargaGimnasio,
    double cargaWaterpolo,
    int metrosAE1,
    int metrosAE2,
    int metrosAE3,
    int metrosAE4,
    int metrosAE5,
    int minutosWaterpolo,
    int volumenGimnasio,
    int sesiones
  ) {}

  public record CargaResponse(
    CargaResumen resumen,
    List<CargaSemanal> semanas
  ) {}

  // ── ASISTENCIA ───────────────────────────────────────────────────────────

  public record AsistenciaJugador(
    Long jugadorId,
    String nombre,
    String apellidos,
    int presentes,
    int ausentes,
    int justificadas,
    int otras,
    int totalRegistradas,
    int sesionesPosibles,
    double porcentajePresente   // presentes / sesionesPosibles
  ) {}

  public record AsistenciaSemanal(
    String semana,              // "YYYY-Www"
    LocalDate lunes,
    int sesiones,
    int presentes,
    int total,                  // sesiones × jugadores plantilla
    double porcentajePresente
  ) {}

  /** Una celda del heatmap: estado individual de un jugador en una sesión concreta. */
  public record AsistenciaHeatmapCelda(
    Long jugadorId,
    Long sesionDiaId,
    String estado               // PRESENTE, AUSENTE, JUSTIFICADA, …
  ) {}

  public record AsistenciaSesion(
    Long sesionDiaId,
    LocalDate fecha,
    int presentes,
    int total
  ) {}

  public record AsistenciaResumen(
    int sesiones,
    int jugadores,
    int totalRegistros,
    int totalPresentes,
    double porcentajeMedio
  ) {}

  public record AsistenciaResponse(
    AsistenciaResumen resumen,
    List<AsistenciaSemanal> semanas,
    List<AsistenciaJugador> jugadores,
    List<AsistenciaSesion> sesiones,
    List<AsistenciaHeatmapCelda> heatmap
  ) {}
}
