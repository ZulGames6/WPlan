package com.poloplan.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.MetricasDtos.AsistenciaHeatmapCelda;
import com.poloplan.dto.MetricasDtos.AsistenciaJugador;
import com.poloplan.dto.MetricasDtos.AsistenciaResponse;
import com.poloplan.dto.MetricasDtos.AsistenciaResumen;
import com.poloplan.dto.MetricasDtos.AsistenciaSemanal;
import com.poloplan.dto.MetricasDtos.AsistenciaSesion;
import com.poloplan.dto.MetricasDtos.CargaResponse;
import com.poloplan.dto.MetricasDtos.CargaResumen;
import com.poloplan.dto.MetricasDtos.CargaSemanal;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.Asistencia;
import com.poloplan.entity.EjercicioWaterpolo;
import com.poloplan.entity.Jugador;
import com.poloplan.entity.SesionDia;
import com.poloplan.entity.SesionGimnasioEjercicio;
import com.poloplan.entity.SesionNatacionBloque;
import com.poloplan.entity.SesionWaterpoloEjercicio;
import com.poloplan.repository.AsistenciaRepository;
import com.poloplan.repository.JugadorRepository;
import com.poloplan.repository.SesionDiaRepository;
import com.poloplan.repository.SesionGimnasioEjercicioRepository;
import com.poloplan.repository.SesionNatacionBloqueRepository;
import com.poloplan.repository.SesionWaterpoloEjercicioRepository;

@Service
public class MetricasService {

  private final PlanificacionResolver planificacionResolver;
  private final SesionDiaRepository sesionDiaRepository;
  private final SesionNatacionBloqueRepository natacionBloqueRepository;
  private final SesionWaterpoloEjercicioRepository waterpoloRepository;
  private final SesionGimnasioEjercicioRepository gimnasioRepository;
  private final AsistenciaRepository asistenciaRepository;
  private final JugadorRepository jugadorRepository;

  public MetricasService(
    PlanificacionResolver planificacionResolver,
    SesionDiaRepository sesionDiaRepository,
    SesionNatacionBloqueRepository natacionBloqueRepository,
    SesionWaterpoloEjercicioRepository waterpoloRepository,
    SesionGimnasioEjercicioRepository gimnasioRepository,
    AsistenciaRepository asistenciaRepository,
    JugadorRepository jugadorRepository
  ) {
    this.planificacionResolver = planificacionResolver;
    this.sesionDiaRepository = sesionDiaRepository;
    this.natacionBloqueRepository = natacionBloqueRepository;
    this.waterpoloRepository = waterpoloRepository;
    this.gimnasioRepository = gimnasioRepository;
    this.asistenciaRepository = asistenciaRepository;
    this.jugadorRepository = jugadorRepository;
  }

  // ═════════════════════════════════════════════════════════════════════════
  //  CARGA
  // ═════════════════════════════════════════════════════════════════════════

  @Transactional(readOnly = true)
  public CargaResponse carga(AppUser user, Long planNumero, LocalDate desde, LocalDate hasta) {
    validarRango(desde, hasta);
    // Comprobación de propiedad
    planificacionResolver.require(user, planNumero);
    long uid = user.getId();

    List<SesionDia> sesiones = sesionDiaRepository.listByPlanNumeroAndOwnerBetweenFechas(
      planNumero, uid, desde, hasta);
    List<SesionNatacionBloque> bloques = natacionBloqueRepository.listByPlanAndOwnerBetween(
      planNumero, uid, desde, hasta);
    List<SesionWaterpoloEjercicio> wpItems = waterpoloRepository.listByPlanAndOwnerBetween(
      planNumero, uid, desde, hasta);
    List<SesionGimnasioEjercicio> gymItems = gimnasioRepository.listByPlanAndOwnerBetween(
      planNumero, uid, desde, hasta);

    // Mapas auxiliares por semana ISO
    Map<String, CargaSemanaBucket> porSemana = new TreeMap<>();

    // Inicializar buckets para todas las semanas del rango (aunque no haya datos)
    LocalDate cursor = desde.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate finSemana = hasta.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    while (!cursor.isAfter(finSemana)) {
      final LocalDate lunes = cursor;
      porSemana.computeIfAbsent(claveSemana(lunes), k -> new CargaSemanaBucket(lunes));
      cursor = cursor.plusWeeks(1);
    }

    // Contar sesiones por semana
    for (SesionDia s : sesiones) {
      bucket(porSemana, s.getFecha()).sesiones++;
    }

    // Natación: agregar carga y metros por zona AE
    int metrosTotalesNat = 0;
    double cargaTotalNat = 0;
    for (SesionNatacionBloque b : bloques) {
      LocalDate fecha = fechaDeBloqueNatacion(b);
      if (fecha == null) continue;
      int series = nullable(b.getSeries());
      int mps = nullable(b.getMetrosPorSerie());
      int metros = series * mps;
      String ae = b.getIntensidadAE() == null ? "AE1" : b.getIntensidadAE().toUpperCase();
      double factor = aeFactor(ae);
      double carga = metros * factor;

      CargaSemanaBucket sem = bucket(porSemana, fecha);
      sem.cargaNatacion += carga;
      switch (ae) {
        case "AE1" -> sem.metrosAE1 += metros;
        case "AE2" -> sem.metrosAE2 += metros;
        case "AE3" -> sem.metrosAE3 += metros;
        case "AE4" -> sem.metrosAE4 += metros;
        case "AE5" -> sem.metrosAE5 += metros;
        default    -> sem.metrosAE1 += metros;
      }
      metrosTotalesNat += metros;
      cargaTotalNat += carga;
    }

    // Waterpolo: minutos × peso de intensidad
    int minutosTotalWp = 0;
    double cargaTotalWp = 0;
    for (SesionWaterpoloEjercicio it : wpItems) {
      LocalDate fecha = fechaDeWaterpolo(it);
      if (fecha == null) continue;
      int min = it.getDuracionMin() == null
        ? defaultDuracion(it.getEjercicio())
        : it.getDuracionMin();
      double pesoInt = pesoIntensidad(it.getEjercicio() == null ? null : it.getEjercicio().getIntensidad());
      double carga = min * pesoInt;

      CargaSemanaBucket sem = bucket(porSemana, fecha);
      sem.cargaWaterpolo += carga;
      sem.minutosWaterpolo += min;
      minutosTotalWp += min;
      cargaTotalWp += carga;
    }

    // Gimnasio: Σ series × reps × (%RM/100); si no hay %RM, series × reps
    int volumenTotalGym = 0;
    double cargaTotalGym = 0;
    for (SesionGimnasioEjercicio it : gymItems) {
      LocalDate fecha = fechaDeGimnasio(it);
      if (fecha == null) continue;
      int series = nullable(it.getSeries());
      int reps = nullable(it.getRepeticiones());
      int volumen = series * reps;
      BigDecimal porcRm = it.getPorcRm();
      double carga;
      if (porcRm == null) {
        carga = volumen;
      } else {
        carga = volumen * (porcRm.doubleValue() / 100.0);
      }

      CargaSemanaBucket sem = bucket(porSemana, fecha);
      sem.cargaGimnasio += carga;
      sem.volumenGimnasio += volumen;
      volumenTotalGym += volumen;
      cargaTotalGym += carga;
    }

    List<CargaSemanal> semanas = new ArrayList<>(porSemana.size());
    for (CargaSemanaBucket b : porSemana.values()) {
      semanas.add(new CargaSemanal(
        claveSemana(b.lunes),
        b.lunes,
        round2(b.cargaNatacion),
        round2(b.cargaGimnasio),
        round2(b.cargaWaterpolo),
        b.metrosAE1, b.metrosAE2, b.metrosAE3, b.metrosAE4, b.metrosAE5,
        b.minutosWaterpolo,
        b.volumenGimnasio,
        b.sesiones
      ));
    }

    CargaResumen resumen = new CargaResumen(
      round2(cargaTotalNat),
      round2(cargaTotalGym),
      round2(cargaTotalWp),
      metrosTotalesNat,
      minutosTotalWp,
      volumenTotalGym,
      sesiones.size()
    );
    return new CargaResponse(resumen, semanas);
  }

  // ═════════════════════════════════════════════════════════════════════════
  //  ASISTENCIA
  // ═════════════════════════════════════════════════════════════════════════

  @Transactional(readOnly = true)
  public AsistenciaResponse asistencia(AppUser user, Long planNumero, LocalDate desde, LocalDate hasta) {
    validarRango(desde, hasta);
    planificacionResolver.require(user, planNumero);
    long uid = user.getId();

    List<SesionDia> sesiones = sesionDiaRepository.listByPlanNumeroAndOwnerBetweenFechas(
      planNumero, uid, desde, hasta);
    List<Asistencia> asistencias = asistenciaRepository.listByPlanAndOwnerBetween(
      planNumero, uid, desde, hasta);
    List<Jugador> jugadores = jugadorRepository.listByPlanNumeroAndOwner(planNumero, uid);

    // ─── Heatmap y conteos por jugador ─────────────────────────────────────
    Map<Long, JugadorBucket> porJugador = new LinkedHashMap<>();
    for (Jugador j : jugadores) {
      porJugador.put(j.getId(), new JugadorBucket(j));
    }
    List<AsistenciaHeatmapCelda> heatmap = new ArrayList<>(asistencias.size());
    for (Asistencia a : asistencias) {
      Long jid = a.getJugador().getId();
      Long sid = a.getSesionDia().getId();
      String estado = a.getEstado() == null ? "OTRA" : a.getEstado().toUpperCase();
      heatmap.add(new AsistenciaHeatmapCelda(jid, sid, estado));
      JugadorBucket b = porJugador.get(jid);
      if (b == null) continue;  // jugador eliminado de la planificación
      b.total++;
      switch (estado) {
        case "PRESENTE" -> b.presentes++;
        case "AUSENTE"  -> b.ausentes++;
        case "JUSTIFICADA", "JUSTIFICADO" -> b.justificadas++;
        default -> b.otras++;
      }
    }

    int sesionesPosibles = sesiones.size();
    List<AsistenciaJugador> jugadoresResp = new ArrayList<>(jugadores.size());
    for (JugadorBucket b : porJugador.values()) {
      double pct = sesionesPosibles == 0 ? 0.0
        : (b.presentes * 100.0) / sesionesPosibles;
      jugadoresResp.add(new AsistenciaJugador(
        b.jugador.getId(),
        b.jugador.getNombre(),
        b.jugador.getApellidos(),
        b.presentes, b.ausentes, b.justificadas, b.otras,
        b.total, sesionesPosibles,
        round2(pct)
      ));
    }
    jugadoresResp.sort(Comparator.comparingDouble(AsistenciaJugador::porcentajePresente).reversed());

    // ─── Resumen por sesión ────────────────────────────────────────────────
    Map<Long, int[]> porSesion = new HashMap<>();  // [presentes, total]
    int numJugadores = jugadores.size();
    for (Asistencia a : asistencias) {
      Long sid = a.getSesionDia().getId();
      int[] v = porSesion.computeIfAbsent(sid, k -> new int[]{0, 0});
      v[1]++;
      if ("PRESENTE".equalsIgnoreCase(a.getEstado())) {
        v[0]++;
      }
    }
    List<AsistenciaSesion> sesionesResp = new ArrayList<>(sesiones.size());
    for (SesionDia s : sesiones) {
      int[] v = porSesion.getOrDefault(s.getId(), new int[]{0, 0});
      int total = v[1] == 0 ? numJugadores : Math.max(v[1], numJugadores);
      sesionesResp.add(new AsistenciaSesion(s.getId(), s.getFecha(), v[0], total));
    }

    // ─── Series semanales ─────────────────────────────────────────────────
    Map<String, SemanaBucket> porSemana = new TreeMap<>();
    LocalDate cursor = desde.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    LocalDate finSemana = hasta.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    while (!cursor.isAfter(finSemana)) {
      final LocalDate lunes = cursor;
      porSemana.computeIfAbsent(claveSemana(lunes), k -> new SemanaBucket(lunes));
      cursor = cursor.plusWeeks(1);
    }
    Map<Long, LocalDate> fechaPorSesion = new HashMap<>();
    for (SesionDia s : sesiones) {
      fechaPorSesion.put(s.getId(), s.getFecha());
      semBucket(porSemana, s.getFecha()).sesiones++;
    }
    for (Asistencia a : asistencias) {
      LocalDate fecha = fechaPorSesion.get(a.getSesionDia().getId());
      if (fecha == null) continue;
      SemanaBucket b = semBucket(porSemana, fecha);
      b.totalRegistrados++;
      if ("PRESENTE".equalsIgnoreCase(a.getEstado())) {
        b.presentes++;
      }
    }
    List<AsistenciaSemanal> semanasResp = new ArrayList<>(porSemana.size());
    for (SemanaBucket b : porSemana.values()) {
      int total = b.sesiones * numJugadores;
      double pct = total == 0 ? 0.0 : (b.presentes * 100.0) / total;
      semanasResp.add(new AsistenciaSemanal(
        claveSemana(b.lunes), b.lunes,
        b.sesiones, b.presentes, total,
        round2(pct)
      ));
    }

    // ─── Resumen global ────────────────────────────────────────────────────
    int totalRegistros = asistencias.size();
    int totalPresentes = 0;
    for (Asistencia a : asistencias) {
      if ("PRESENTE".equalsIgnoreCase(a.getEstado())) totalPresentes++;
    }
    int posibleMax = sesionesPosibles * numJugadores;
    double pctMedio = posibleMax == 0 ? 0.0 : (totalPresentes * 100.0) / posibleMax;

    AsistenciaResumen resumen = new AsistenciaResumen(
      sesionesPosibles, numJugadores, totalRegistros, totalPresentes, round2(pctMedio)
    );

    // Filtramos heatmap a celdas cuyo jugador/sesión existan
    Set<Long> jugadoresActuales = new HashSet<>(porJugador.keySet());
    Set<Long> sesionesIds = new HashSet<>(fechaPorSesion.keySet());
    List<AsistenciaHeatmapCelda> heatmapFiltrado = new ArrayList<>();
    for (AsistenciaHeatmapCelda c : heatmap) {
      if (jugadoresActuales.contains(c.jugadorId()) && sesionesIds.contains(c.sesionDiaId())) {
        heatmapFiltrado.add(c);
      }
    }

    return new AsistenciaResponse(resumen, semanasResp, jugadoresResp, sesionesResp, heatmapFiltrado);
  }

  // ═════════════════════════════════════════════════════════════════════════
  //  Helpers
  // ═════════════════════════════════════════════════════════════════════════

  private static void validarRango(LocalDate desde, LocalDate hasta) {
    if (desde == null || hasta == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes enviar desde y hasta");
    }
    if (hasta.isBefore(desde)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hasta no puede ser anterior a desde");
    }
  }

  private static double aeFactor(String ae) {
    if (ae == null) return 1.0;
    return switch (ae.toUpperCase()) {
      case "AE1" -> 1.0;
      case "AE2" -> 2.0;
      case "AE3" -> 3.0;
      case "AE4" -> 5.0;
      case "AE5" -> 8.0;
      default    -> 1.0;
    };
  }

  private static double pesoIntensidad(String intensidad) {
    if (intensidad == null) return 1.0;
    return switch (intensidad.toUpperCase()) {
      case "BAJA"  -> 1.0;
      case "MEDIA" -> 1.5;
      case "ALTA"  -> 2.0;
      default      -> 1.0;
    };
  }

  private static int defaultDuracion(EjercicioWaterpolo ej) {
    if (ej == null || ej.getDuracionMinSugerida() == null) return 10;
    return ej.getDuracionMinSugerida();
  }

  private static int nullable(Integer i) {
    return i == null ? 0 : i;
  }

  private static double round2(double v) {
    return Math.round(v * 100.0) / 100.0;
  }

  private static String claveSemana(LocalDate fecha) {
    int year = fecha.get(IsoFields.WEEK_BASED_YEAR);
    int week = fecha.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    return String.format("%04d-W%02d", year, week);
  }

  private static CargaSemanaBucket bucket(Map<String, CargaSemanaBucket> map, LocalDate fecha) {
    LocalDate lunes = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    return map.computeIfAbsent(claveSemana(lunes), k -> new CargaSemanaBucket(lunes));
  }

  private static SemanaBucket semBucket(Map<String, SemanaBucket> map, LocalDate fecha) {
    LocalDate lunes = fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    return map.computeIfAbsent(claveSemana(lunes), k -> new SemanaBucket(lunes));
  }

  private static LocalDate fechaDeBloqueNatacion(SesionNatacionBloque b) {
    try {
      return b.getSesionNatacion().getSesionDia().getFecha();
    } catch (Exception e) {
      return null;
    }
  }

  private static LocalDate fechaDeWaterpolo(SesionWaterpoloEjercicio it) {
    try {
      return it.getSesionWaterpolo().getSesionDia().getFecha();
    } catch (Exception e) {
      return null;
    }
  }

  private static LocalDate fechaDeGimnasio(SesionGimnasioEjercicio it) {
    try {
      return it.getSesionGimnasio().getSesionDia().getFecha();
    } catch (Exception e) {
      return null;
    }
  }

  private static final class CargaSemanaBucket {
    final LocalDate lunes;
    double cargaNatacion;
    double cargaGimnasio;
    double cargaWaterpolo;
    int metrosAE1, metrosAE2, metrosAE3, metrosAE4, metrosAE5;
    int minutosWaterpolo;
    int volumenGimnasio;
    int sesiones;
    CargaSemanaBucket(LocalDate lunes) { this.lunes = lunes; }
  }

  private static final class SemanaBucket {
    final LocalDate lunes;
    int sesiones;
    int presentes;
    int totalRegistrados;
    SemanaBucket(LocalDate lunes) { this.lunes = lunes; }
  }

  private static final class JugadorBucket {
    final Jugador jugador;
    int presentes;
    int ausentes;
    int justificadas;
    int otras;
    int total;
    JugadorBucket(Jugador j) { this.jugador = j; }
  }
}
