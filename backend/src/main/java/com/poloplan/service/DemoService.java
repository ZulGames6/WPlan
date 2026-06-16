package com.poloplan.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poloplan.entity.AppUser;
import com.poloplan.entity.Asistencia;
import com.poloplan.entity.EjercicioGimnasio;
import com.poloplan.entity.EjercicioWaterpolo;
import com.poloplan.entity.HorarioSemanal;
import com.poloplan.entity.Jugador;
import com.poloplan.entity.Planificacion;
import com.poloplan.entity.SesionDia;
import com.poloplan.entity.SesionGimnasio;
import com.poloplan.entity.SesionGimnasioEjercicio;
import com.poloplan.entity.SesionNatacion;
import com.poloplan.entity.SesionNatacionBloque;
import com.poloplan.entity.SesionWaterpolo;
import com.poloplan.entity.SesionWaterpoloEjercicio;
import com.poloplan.repository.AsistenciaRepository;
import com.poloplan.repository.EjercicioGimnasioRepository;
import com.poloplan.repository.EjercicioWaterpoloRepository;
import com.poloplan.repository.HorarioSemanalRepository;
import com.poloplan.repository.JugadorRepository;
import com.poloplan.repository.PlanificacionRepository;
import com.poloplan.repository.SesionDiaRepository;

/**
 * Crea una planificación demo completa para que el usuario pueda probar las
 * métricas sin tener que rellenar todo a mano: jugadores, horario semanal,
 * 12 semanas de sesiones con bloques de natación, ejercicios de waterpolo
 * y gimnasio, y asistencias variadas.
 */
@Service
public class DemoService {

  private final PlanificacionRepository planificacionRepo;
  private final JugadorRepository jugadorRepo;
  private final HorarioSemanalRepository horarioRepo;
  private final SesionDiaRepository sesionRepo;
  private final AsistenciaRepository asistenciaRepo;
  private final EjercicioWaterpoloRepository waterpoloEjRepo;
  private final EjercicioGimnasioRepository gimnasioEjRepo;
  private final CatalogoSeedService catalogoSeedService;

  public DemoService(
    PlanificacionRepository planificacionRepo,
    JugadorRepository jugadorRepo,
    HorarioSemanalRepository horarioRepo,
    SesionDiaRepository sesionRepo,
    AsistenciaRepository asistenciaRepo,
    EjercicioWaterpoloRepository waterpoloEjRepo,
    EjercicioGimnasioRepository gimnasioEjRepo,
    CatalogoSeedService catalogoSeedService
  ) {
    this.planificacionRepo = planificacionRepo;
    this.jugadorRepo = jugadorRepo;
    this.horarioRepo = horarioRepo;
    this.sesionRepo = sesionRepo;
    this.asistenciaRepo = asistenciaRepo;
    this.waterpoloEjRepo = waterpoloEjRepo;
    this.gimnasioEjRepo = gimnasioEjRepo;
    this.catalogoSeedService = catalogoSeedService;
  }

  public record DemoResponse(
    long planNumero,
    String nombre,
    int jugadores,
    int sesionesCreadas,
    int bloquesNatacion,
    int ejerciciosWaterpolo,
    int ejerciciosGimnasio,
    int asistencias
  ) {}

  @Transactional
  public DemoResponse crear(AppUser user) {
    // 1) Asegurar catálogo
    catalogoSeedService.seedSiVacio(user);

    List<EjercicioWaterpolo> catalogoWp = waterpoloEjRepo.listByOwner(user.getId());
    List<EjercicioGimnasio> catalogoGym = gimnasioEjRepo.listByOwner(user.getId());

    Random rnd = new Random(42); // semilla fija → reproducible

    // 2) Planificación
    LocalDate hoy = LocalDate.now();
    LocalDate inicio = hoy.minusWeeks(12).with(DayOfWeek.MONDAY);
    LocalDate fin = hoy.plusWeeks(4).with(DayOfWeek.SUNDAY);

    long siguiente = planificacionRepo.maxNumeroByOwner(user.getId()) + 1L;
    Planificacion plan = new Planificacion();
    plan.setUser(user);
    plan.setNumero(siguiente);
    plan.setNombre("Prueba");
    plan.setFechaInicio(inicio);
    plan.setFechaFin(fin);
    plan.setNotas("Planificación de prueba con datos ficticios. Puedes eliminarla cuando quieras.");
    planificacionRepo.save(plan);

    // 3) Jugadores
    List<Jugador> jugadores = crearJugadores(plan);

    // 4) Horario semanal
    crearHorario(plan);

    // 5) Sesiones para cada lunes/miercoles/martes/jueves/viernes desde inicio hasta hoy (no futuro)
    int sesionesCreadas = 0;
    int totalBloquesNat = 0;
    int totalItemsWp = 0;
    int totalItemsGym = 0;
    int totalAsistencias = 0;

    LocalDate cursor = inicio;
    int weekIdx = 0;
    while (!cursor.isAfter(hoy)) {
      DayOfWeek dow = cursor.getDayOfWeek();
      boolean gym, nat, wp;
      LocalTime horaInicio, horaFin;
      String lugar;
      switch (dow) {
        case MONDAY:    gym = true;  nat = false; wp = true;  horaInicio = LocalTime.of(18, 0); horaFin = LocalTime.of(20, 0); lugar = "Polideportivo"; break;
        case TUESDAY:   gym = false; nat = true;  wp = false; horaInicio = LocalTime.of(18, 0); horaFin = LocalTime.of(19, 30); lugar = "Piscina";     break;
        case WEDNESDAY: gym = true;  nat = false; wp = true;  horaInicio = LocalTime.of(18, 0); horaFin = LocalTime.of(20, 0); lugar = "Polideportivo"; break;
        case THURSDAY:  gym = false; nat = true;  wp = false; horaInicio = LocalTime.of(18, 0); horaFin = LocalTime.of(19, 30); lugar = "Piscina";     break;
        case FRIDAY:    gym = true;  nat = false; wp = true;  horaInicio = LocalTime.of(18, 0); horaFin = LocalTime.of(20, 0); lugar = "Polideportivo"; break;
        default:        cursor = cursor.plusDays(1); continue;
      }
      if (dow == DayOfWeek.MONDAY) weekIdx++;

      SesionDia s = new SesionDia();
      s.setPlanificacion(plan);
      s.setFecha(cursor);
      s.setHoraInicio(horaInicio);
      s.setHoraFin(horaFin);
      s.setLugar(lugar);
      s.setTipo("ENTRENAMIENTO");

      if (gym) {
        SesionGimnasio g = new SesionGimnasio();
        g.setSesionDia(s);
        g.setObjetivo(objetivoGimnasioPorSemana(weekIdx));
        rellenarGimnasio(g, catalogoGym, rnd, weekIdx);
        totalItemsGym += g.getEjercicios().size();
        s.setGimnasio(g);
      }
      if (nat) {
        SesionNatacion n = new SesionNatacion();
        n.setSesionDia(s);
        n.setObjetivo(objetivoNatacionPorSemana(weekIdx));
        rellenarNatacion(n, rnd, weekIdx);
        totalBloquesNat += n.getBloques().size();
        s.setNatacion(n);
      }
      if (wp) {
        SesionWaterpolo w = new SesionWaterpolo();
        w.setSesionDia(s);
        w.setObjetivo(objetivoWaterpoloPorSemana(weekIdx));
        rellenarWaterpolo(w, catalogoWp, rnd);
        totalItemsWp += w.getEjercicios().size();
        s.setWaterpolo(w);
      }

      sesionRepo.save(s);
      sesionesCreadas++;

      // Asistencias: presencia con pequeñas variaciones por jugador
      totalAsistencias += crearAsistencias(s, jugadores, rnd);

      cursor = cursor.plusDays(1);
    }

    return new DemoResponse(
      plan.getNumero(),
      plan.getNombre(),
      jugadores.size(),
      sesionesCreadas,
      totalBloquesNat,
      totalItemsWp,
      totalItemsGym,
      totalAsistencias
    );
  }

  // ═════════════════════════════════════════════════════════════════════════
  //  Jugadores
  // ═════════════════════════════════════════════════════════════════════════

  private static final String[][] NOMBRES = {
    {"Pablo",    "García",    "PORTERO"},
    {"Hugo",     "Martínez",  "DEFENSA"},
    {"Lucas",    "López",     "DEFENSA"},
    {"Mateo",    "Rodríguez", "BOYA"},
    {"Daniel",   "Sánchez",   "BOYA"},
    {"Adrián",   "Pérez",     "CENTRAL"},
    {"Iván",     "Gómez",     "CENTRAL"},
    {"Carlos",   "Fernández", "EXTERIOR"},
    {"Álvaro",   "Jiménez",   "EXTERIOR"},
    {"Sergio",   "Ruiz",      "EXTERIOR"},
    {"Diego",    "Hernández", "EXTERIOR"},
    {"Javier",   "Díaz",      "PORTERO"},
  };

  private List<Jugador> crearJugadores(Planificacion plan) {
    List<Jugador> creados = new ArrayList<>(NOMBRES.length);
    for (String[] n : NOMBRES) {
      Jugador j = new Jugador();
      j.setPlanificacion(plan);
      j.setNombre(n[0]);
      j.setApellidos(n[1]);
      j.setPosicion(n[2]);
      creados.add(jugadorRepo.save(j));
    }
    return creados;
  }

  // ═════════════════════════════════════════════════════════════════════════
  //  Horario semanal
  // ═════════════════════════════════════════════════════════════════════════

  private void crearHorario(Planificacion plan) {
    // Lun: Gym+WP (Polideportivo, 18-20)
    // Mar: Nat (Piscina, 18-19:30)
    // Mié: Gym+WP
    // Jue: Nat
    // Vie: Gym+WP
    // Sáb/Dom: descanso
    int[] dias = {1, 2, 3, 4, 5, 6, 7};
    for (int d : dias) {
      HorarioSemanal h = new HorarioSemanal();
      h.setPlanificacion(plan);
      h.setDiaSemana(d);
      if (d == 1 || d == 3 || d == 5) {
        h.setActivo(true);
        h.setHoraInicio(LocalTime.of(18, 0));
        h.setHoraFin(LocalTime.of(20, 0));
        h.setLugar("Polideportivo");
        h.setConGimnasio(true);
        h.setConNatacion(false);
        h.setConWaterpolo(true);
      } else if (d == 2 || d == 4) {
        h.setActivo(true);
        h.setHoraInicio(LocalTime.of(18, 0));
        h.setHoraFin(LocalTime.of(19, 30));
        h.setLugar("Piscina");
        h.setConGimnasio(false);
        h.setConNatacion(true);
        h.setConWaterpolo(false);
      } else {
        h.setActivo(false);
        h.setConGimnasio(false);
        h.setConNatacion(false);
        h.setConWaterpolo(false);
      }
      horarioRepo.save(h);
    }
  }

  // ═════════════════════════════════════════════════════════════════════════
  //  Contenido de sesiones
  // ═════════════════════════════════════════════════════════════════════════

  private static String objetivoGimnasioPorSemana(int week) {
    int phase = week % 4;
    return switch (phase) {
      case 0 -> "Hipertrofia · volumen alto";
      case 1 -> "Fuerza · cargas medias-altas";
      case 2 -> "Potencia · explosividad";
      default -> "Resistencia muscular · descarga";
    };
  }

  private static String objetivoNatacionPorSemana(int week) {
    int phase = week % 5;
    return switch (phase) {
      case 0 -> "Base aeróbica";
      case 1 -> "Umbral aeróbico";
      case 2 -> "Potencia aeróbica (VO2max)";
      case 3 -> "Capacidad anaeróbica";
      default -> "Velocidad";
    };
  }

  private static String objetivoWaterpoloPorSemana(int week) {
    int phase = week % 4;
    return switch (phase) {
      case 0 -> "Fundamentos técnicos";
      case 1 -> "Trabajo táctico colectivo";
      case 2 -> "Situaciones de juego real";
      default -> "Físico específico con balón";
    };
  }

  private void rellenarNatacion(SesionNatacion n, Random rnd, int weekIdx) {
    // 6 bloques: calentamiento + técnica + piernas + material + principal + vuelta
    int orden = 1;
    // El bloque principal cambia de AE según semana (rotación cíclica)
    String[] aePrincipalPorSemana = {"AE2", "AE2", "AE3", "AE3", "AE4"};
    String aePrincipal = aePrincipalPorSemana[weekIdx % aePrincipalPorSemana.length];

    n.getBloques().add(bloque(n, orden++, "CALENTAMIENTO", "AE1", 1, 400 + rnd.nextInt(3) * 100, 0));
    n.getBloques().add(bloque(n, orden++, "TECNICA",       "AE2", 4, 100, 15));
    n.getBloques().add(bloque(n, orden++, "PIERNAS",       "AE2", 4, 50, 15));
    if (!"AE5".equals(aePrincipal)) {
      n.getBloques().add(bloque(n, orden++, "MATERIAL",    "AE2", 4, 150, 20));
    }
    // Bloque principal según objetivo de la semana
    int seriesPrinc;
    int metrosPorSerie;
    int descansoPrinc;
    switch (aePrincipal) {
      case "AE2" -> { seriesPrinc = 3 + rnd.nextInt(2); metrosPorSerie = 400; descansoPrinc = 30; }
      case "AE3" -> { seriesPrinc = 5 + rnd.nextInt(3); metrosPorSerie = 200; descansoPrinc = 25; }
      case "AE4" -> { seriesPrinc = 6 + rnd.nextInt(3); metrosPorSerie = 100; descansoPrinc = 40; }
      case "AE5" -> { seriesPrinc = 8 + rnd.nextInt(4); metrosPorSerie = 25;  descansoPrinc = 45; }
      default    -> { seriesPrinc = 4; metrosPorSerie = 200; descansoPrinc = 30; }
    }
    n.getBloques().add(bloque(n, orden++, "PRINCIPAL", aePrincipal, seriesPrinc, metrosPorSerie, descansoPrinc));
    n.getBloques().add(bloque(n, orden, "VUELTA_CALMA", "AE1", 1, 200, 0));
  }

  private static SesionNatacionBloque bloque(SesionNatacion n, int orden, String tipo, String ae,
                                             int series, int metrosPorSerie, int descansoSeg) {
    SesionNatacionBloque b = new SesionNatacionBloque();
    b.setSesionNatacion(n);
    b.setOrden(orden);
    b.setTipoBloque(tipo);
    b.setIntensidadAE(ae);
    b.setSeries(series);
    b.setMetrosPorSerie(metrosPorSerie);
    b.setDescansoSeg(descansoSeg);
    b.setNombre(tipo);
    return b;
  }

  private void rellenarWaterpolo(SesionWaterpolo w, List<EjercicioWaterpolo> catalogo, Random rnd) {
    if (catalogo.isEmpty()) return;
    int n = 4 + rnd.nextInt(3); // 4-6 ejercicios
    List<EjercicioWaterpolo> shuffle = new ArrayList<>(catalogo);
    java.util.Collections.shuffle(shuffle, rnd);
    int take = Math.min(n, shuffle.size());
    for (int i = 0; i < take; i++) {
      EjercicioWaterpolo ej = shuffle.get(i);
      SesionWaterpoloEjercicio it = new SesionWaterpoloEjercicio();
      it.setSesionWaterpolo(w);
      it.setEjercicio(ej);
      it.setOrden(i + 1);
      Integer sugerida = ej.getDuracionMinSugerida();
      int dur = sugerida != null ? sugerida : 10 + rnd.nextInt(15);
      it.setDuracionMin(dur);
      w.getEjercicios().add(it);
    }
  }

  private void rellenarGimnasio(SesionGimnasio g, List<EjercicioGimnasio> catalogo, Random rnd, int weekIdx) {
    if (catalogo.isEmpty()) return;
    int n = 5 + rnd.nextInt(3); // 5-7 ejercicios
    List<EjercicioGimnasio> shuffle = new ArrayList<>(catalogo);
    java.util.Collections.shuffle(shuffle, rnd);

    // DUP semanal: el peso/repeticiones rotan según la fase
    int phase = weekIdx % 4;
    int take = Math.min(n, shuffle.size());
    for (int i = 0; i < take; i++) {
      EjercicioGimnasio ej = shuffle.get(i);
      SesionGimnasioEjercicio it = new SesionGimnasioEjercicio();
      it.setSesionGimnasio(g);
      it.setEjercicio(ej);
      it.setOrden(i + 1);

      int series, reps, porc, rir, desc;
      String tempo;
      switch (phase) {
        case 0 -> { // Hipertrofia volumen
          series = 4; reps = 10 + rnd.nextInt(4); porc = 65 + rnd.nextInt(8); rir = 2; desc = 75; tempo = "3-1-1";
        }
        case 1 -> { // Fuerza
          series = 5; reps = 4 + rnd.nextInt(2); porc = 80 + rnd.nextInt(8); rir = 1; desc = 180; tempo = "2-1-1";
        }
        case 2 -> { // Potencia
          series = 5; reps = 3 + rnd.nextInt(2); porc = 55 + rnd.nextInt(10); rir = 2; desc = 150; tempo = "2-0-X";
        }
        default -> { // Resistencia / descarga
          series = 3; reps = 15 + rnd.nextInt(5); porc = 50 + rnd.nextInt(8); rir = 3; desc = 45; tempo = "2-0-2";
        }
      }
      // Ejercicios con peso corporal no llevan %RM
      if ("PESO_CORPORAL".equals(ej.getEquipamiento()) || "BANDAS".equals(ej.getEquipamiento())) {
        it.setPorcRm(null);
      } else {
        it.setPorcRm(BigDecimal.valueOf(porc));
      }
      it.setSeries(series);
      it.setRepeticiones(reps);
      it.setRir(rir);
      it.setDescansoSeg(desc);
      it.setTempo(tempo);
      g.getEjercicios().add(it);
    }
  }

  // ═════════════════════════════════════════════════════════════════════════
  //  Asistencias variadas
  // ═════════════════════════════════════════════════════════════════════════

  // Probabilidad de asistencia por jugador (en orden de la lista NOMBRES):
  // algunos muy regulares, otros con bajas frecuentes para que el ranking tenga variedad.
  private static final double[] PROB_PRESENTE = {
    0.97, 0.92, 0.88, 0.85, 0.95, 0.80, 0.78, 0.93, 0.70, 0.90, 0.65, 0.96,
  };

  private static final String[] ESTADOS_NO_PRESENTE = {
    "AUSENTE", "AUSENTE", "AUSENTE", "JUSTIFICADA", "LESION", "AUSENTE",
  };

  private int crearAsistencias(SesionDia s, List<Jugador> jugadores, Random rnd) {
    int n = 0;
    for (int i = 0; i < jugadores.size(); i++) {
      Jugador j = jugadores.get(i);
      double p = i < PROB_PRESENTE.length ? PROB_PRESENTE[i] : 0.85;
      String estado;
      if (rnd.nextDouble() < p) {
        estado = "PRESENTE";
      } else {
        estado = ESTADOS_NO_PRESENTE[rnd.nextInt(ESTADOS_NO_PRESENTE.length)];
      }
      Asistencia a = new Asistencia();
      a.setSesionDia(s);
      a.setJugador(j);
      a.setEstado(estado);
      asistenciaRepo.save(a);
      n++;
    }
    return n;
  }
}
