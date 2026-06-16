package com.poloplan.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.SesionDtos.ActualizarSesionDiaRequest;
import com.poloplan.dto.SesionDtos.AplicarHorarioResponse;
import com.poloplan.dto.SesionDtos.CrearSesionDiaRequest;
import com.poloplan.dto.SesionDtos.CrearSesionParteRequest;
import com.poloplan.dto.SesionDtos.SesionDiaResponse;
import com.poloplan.dto.SesionDtos.SesionParteResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.HorarioSemanal;
import com.poloplan.entity.Planificacion;
import com.poloplan.entity.SesionDia;
import com.poloplan.entity.SesionGimnasio;
import com.poloplan.entity.SesionNatacion;
import com.poloplan.entity.SesionWaterpolo;
import com.poloplan.repository.HorarioSemanalRepository;
import com.poloplan.repository.SesionDiaRepository;

@Service
public class SesionService {

  private final PlanificacionResolver planificacionResolver;
  private final SesionDiaRepository sesionDiaRepository;
  private final HorarioSemanalRepository horarioSemanalRepository;

  public SesionService(
    PlanificacionResolver planificacionResolver,
    SesionDiaRepository sesionDiaRepository,
    HorarioSemanalRepository horarioSemanalRepository
  ) {
    this.planificacionResolver = planificacionResolver;
    this.sesionDiaRepository = sesionDiaRepository;
    this.horarioSemanalRepository = horarioSemanalRepository;
  }

  /**
   * Aplica el horario semanal de la planificación a un rango de fechas,
   * creando una sesión por cada fecha cuyo día de la semana esté activo
   * en el horario y no tenga ya sesión creada. Idempotente: las fechas
   * con sesión existente no se duplican.
   */
  @Transactional
  public AplicarHorarioResponse aplicarHorario(
    AppUser propietario,
    Long planNumero,
    LocalDate desde,
    LocalDate hasta
  ) {
    if (desde == null || hasta == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes enviar desde y hasta");
    }
    if (hasta.isBefore(desde)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hasta no puede ser anterior a desde");
    }
    Planificacion plan = planificacionResolver.require(propietario, planNumero);

    Map<Integer, HorarioSemanal> porDia = new HashMap<>();
    for (HorarioSemanal h : horarioSemanalRepository.listByPlanificacionId(plan.getId())) {
      porDia.put(h.getDiaSemana(), h);
    }

    Set<LocalDate> fechasYaConSesion = new HashSet<>();
    for (SesionDia s : sesionDiaRepository.listByPlanNumeroAndOwnerBetweenFechas(
        plan.getNumero(), propietario.getId(), desde, hasta)) {
      fechasYaConSesion.add(s.getFecha());
    }

    int creadas = 0;
    int omitidas = 0;
    int sinHorario = 0;
    LocalDate cursor = desde;
    while (!cursor.isAfter(hasta)) {
      int dia = cursor.getDayOfWeek().getValue();
      HorarioSemanal h = porDia.get(dia);
      if (h == null || !h.isActivo()) {
        sinHorario++;
      } else if (fechasYaConSesion.contains(cursor)) {
        omitidas++;
      } else {
        SesionDia s = new SesionDia();
        s.setPlanificacion(plan);
        s.setFecha(cursor);
        s.setHoraInicio(h.getHoraInicio());
        s.setHoraFin(h.getHoraFin());
        s.setLugar(h.getLugar());
        s.setTipo("ENTRENAMIENTO");
        if (h.isConGimnasio()) {
          SesionGimnasio g = new SesionGimnasio();
          g.setSesionDia(s);
          s.setGimnasio(g);
        }
        if (h.isConNatacion()) {
          SesionNatacion n = new SesionNatacion();
          n.setSesionDia(s);
          s.setNatacion(n);
        }
        if (h.isConWaterpolo()) {
          SesionWaterpolo w = new SesionWaterpolo();
          w.setSesionDia(s);
          s.setWaterpolo(w);
        }
        sesionDiaRepository.save(s);
        creadas++;
      }
      cursor = cursor.plusDays(1);
    }
    return new AplicarHorarioResponse(creadas, omitidas, sinHorario);
  }

  @Transactional
  public SesionDiaResponse crear(AppUser propietario, Long planNumero, CrearSesionDiaRequest request) {
    validarHoras(request.horaInicio(), request.horaFin());
    Planificacion plan = planificacionResolver.require(propietario, planNumero);

    SesionDia s = new SesionDia();
    s.setPlanificacion(plan);
    aplicarCampos(s, request.fecha(), request.horaInicio(), request.horaFin(), request.lugar(), request.tipo(),
      request.estado(), request.objetivo(), request.notas());
    aplicarPartes(s, request.gimnasio(), request.natacion(), request.waterpolo());

    sesionDiaRepository.save(Objects.requireNonNull(s));
    return aRespuesta(s);
  }

  public List<SesionDiaResponse> listar(AppUser propietario, Long planNumero, LocalDate desde, LocalDate hasta) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);

    List<SesionDia> sesiones;
    if (desde != null && hasta != null) {
      if (hasta.isBefore(desde)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hasta no puede ser anterior a desde");
      }
      sesiones = sesionDiaRepository.listByPlanNumeroAndOwnerBetweenFechas(numero, propietario.getId(), desde, hasta);
    } else if (desde == null && hasta == null) {
      sesiones = sesionDiaRepository.listByPlanNumeroAndOwner(numero, propietario.getId());
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes enviar ambos parámetros: desde y hasta");
    }

    return sesiones.stream().map(this::aRespuesta).toList();
  }

  public SesionDiaResponse obtener(AppUser propietario, Long planNumero, Long sesionId) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    SesionDia s = sesionDiaRepository.findByIdAndPlanNumeroAndOwner(sesionId, numero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
    return aRespuesta(s);
  }

  @Transactional
  public SesionDiaResponse actualizar(
    AppUser propietario,
    Long planNumero,
    Long sesionId,
    ActualizarSesionDiaRequest request
  ) {
    validarHoras(request.horaInicio(), request.horaFin());
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    SesionDia s = sesionDiaRepository.findByIdAndPlanNumeroAndOwner(sesionId, numero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

    aplicarCampos(s, request.fecha(), request.horaInicio(), request.horaFin(), request.lugar(), request.tipo(),
      request.estado(), request.objetivo(), request.notas());
    aplicarPartes(s, request.gimnasio(), request.natacion(), request.waterpolo());

    sesionDiaRepository.save(Objects.requireNonNull(s));
    return aRespuesta(s);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long planNumero, Long sesionId) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    SesionDia s = sesionDiaRepository.findByIdAndPlanNumeroAndOwner(sesionId, numero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
    sesionDiaRepository.deleteById(Objects.requireNonNull(s.getId()));
  }

  private static void validarHoras(LocalTime inicio, LocalTime fin) {
    if (inicio != null && fin != null && fin.isBefore(inicio)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "horaFin no puede ser anterior a horaInicio");
    }
  }

  private static void aplicarCampos(
    SesionDia s,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    String lugar,
    String tipo,
    String estado,
    String objetivo,
    String notas
  ) {
    s.setFecha(fecha);
    s.setHoraInicio(horaInicio);
    s.setHoraFin(horaFin);
    s.setLugar(blancoANulo(lugar));
    s.setTipo(blancoANulo(tipo));
    s.setEstado(blancoANulo(estado));
    s.setObjetivo(blancoANulo(objetivo));
    s.setNotas(blancoANulo(notas));
  }

  private static void aplicarPartes(
    SesionDia s,
    CrearSesionParteRequest gimnasio,
    CrearSesionParteRequest natacion,
    CrearSesionParteRequest waterpolo
  ) {
    s.setGimnasio(upsertGimnasio(s, gimnasio));
    s.setNatacion(upsertNatacion(s, natacion));
    s.setWaterpolo(upsertWaterpolo(s, waterpolo));
  }

  private static SesionGimnasio upsertGimnasio(SesionDia s, CrearSesionParteRequest req) {
    if (req == null) {
      if (s.getGimnasio() != null) {
        s.getGimnasio().setSesionDia(null);
      }
      return null;
    }
    SesionGimnasio g = s.getGimnasio();
    if (g == null) {
      g = new SesionGimnasio();
      g.setSesionDia(s);
    }
    g.setObjetivo(blancoANulo(req.objetivo()));
    g.setNotas(blancoANulo(req.notas()));
    return g;
  }

  private static SesionNatacion upsertNatacion(SesionDia s, CrearSesionParteRequest req) {
    if (req == null) {
      if (s.getNatacion() != null) {
        s.getNatacion().setSesionDia(null);
      }
      return null;
    }
    SesionNatacion n = s.getNatacion();
    if (n == null) {
      n = new SesionNatacion();
      n.setSesionDia(s);
    }
    n.setObjetivo(blancoANulo(req.objetivo()));
    n.setNotas(blancoANulo(req.notas()));
    return n;
  }

  private static SesionWaterpolo upsertWaterpolo(SesionDia s, CrearSesionParteRequest req) {
    if (req == null) {
      if (s.getWaterpolo() != null) {
        s.getWaterpolo().setSesionDia(null);
      }
      return null;
    }
    SesionWaterpolo w = s.getWaterpolo();
    if (w == null) {
      w = new SesionWaterpolo();
      w.setSesionDia(s);
    }
    w.setObjetivo(blancoANulo(req.objetivo()));
    w.setNotas(blancoANulo(req.notas()));
    return w;
  }

  private static String blancoANulo(String s) {
    if (s == null) {
      return null;
    }
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private SesionDiaResponse aRespuesta(SesionDia s) {
    return new SesionDiaResponse(
      s.getId(),
      s.getFecha(),
      s.getHoraInicio(),
      s.getHoraFin(),
      s.getLugar(),
      s.getTipo(),
      s.getEstado(),
      s.getObjetivo(),
      s.getNotas(),
      aParte(s.getGimnasio()),
      aParte(s.getNatacion()),
      aParte(s.getWaterpolo())
    );
  }

  private static SesionParteResponse aParte(SesionGimnasio g) {
    if (g == null) return null;
    return new SesionParteResponse(g.getObjetivo(), g.getNotas());
  }

  private static SesionParteResponse aParte(SesionNatacion n) {
    if (n == null) return null;
    return new SesionParteResponse(n.getObjetivo(), n.getNotas());
  }

  private static SesionParteResponse aParte(SesionWaterpolo w) {
    if (w == null) return null;
    return new SesionParteResponse(w.getObjetivo(), w.getNotas());
  }
}

