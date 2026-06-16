package com.poloplan.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.HorarioSemanalDtos.GuardarSemanaRequest;
import com.poloplan.dto.HorarioSemanalDtos.HorarioDiaRequest;
import com.poloplan.dto.HorarioSemanalDtos.HorarioDiaResponse;
import com.poloplan.dto.HorarioSemanalDtos.SemanaResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.HorarioSemanal;
import com.poloplan.entity.Planificacion;
import com.poloplan.repository.HorarioSemanalRepository;

@Service
public class HorarioSemanalService {

  private static final String[] NOMBRES = {"", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

  private final PlanificacionResolver planificacionResolver;
  private final HorarioSemanalRepository horarioRepository;

  public HorarioSemanalService(
    PlanificacionResolver planificacionResolver,
    HorarioSemanalRepository horarioRepository
  ) {
    this.planificacionResolver = planificacionResolver;
    this.horarioRepository = horarioRepository;
  }

  @Transactional(readOnly = true)
  public SemanaResponse obtener(AppUser propietario, Long planNumero) {
    Planificacion plan = planificacionResolver.require(propietario, planNumero);
    List<HorarioSemanal> existentes = horarioRepository.listByPlanificacionId(plan.getId());
    if (existentes.isEmpty()) {
      return plantillaPorDefecto();
    }
    Map<Integer, HorarioSemanal> porDia = existentes.stream()
      .collect(Collectors.toMap(HorarioSemanal::getDiaSemana, Function.identity()));
    List<HorarioDiaResponse> dias = new ArrayList<>();
    for (int d = 1; d <= 7; d++) {
      HorarioSemanal h = porDia.get(d);
      if (h != null) {
        dias.add(aRespuesta(h));
      } else {
        dias.add(filaDefecto(d));
      }
    }
    return new SemanaResponse(dias);
  }

  @Transactional
  public SemanaResponse guardar(AppUser propietario, Long planNumero, GuardarSemanaRequest request) {
    Planificacion plan = planificacionResolver.require(propietario, planNumero);
    Map<Integer, HorarioSemanal> porDia = horarioRepository.listByPlanificacionId(plan.getId()).stream()
      .collect(Collectors.toMap(HorarioSemanal::getDiaSemana, Function.identity()));

    for (HorarioDiaRequest req : request.dias()) {
      validar(req);
      HorarioSemanal h = porDia.get(req.diaSemana());
      if (h == null) {
        h = new HorarioSemanal();
        h.setPlanificacion(plan);
        h.setDiaSemana(req.diaSemana());
      }
      aplicar(h, req);
      horarioRepository.save(h);
    }
    return obtener(propietario, planNumero);
  }

  @Transactional(readOnly = true)
  public HorarioDiaResponse paraFecha(AppUser propietario, Long planNumero, java.time.LocalDate fecha) {
    int dia = fecha.getDayOfWeek().getValue();
    return horarioRepository.findByPlanNumeroAndDia(planNumero, propietario.getId(), dia)
      .map(this::aRespuesta)
      .orElse(filaDefecto(dia));
  }

  private static void validar(HorarioDiaRequest req) {
    if (req.activo() && req.horaInicio() != null && req.horaFin() != null && req.horaFin().isBefore(req.horaInicio())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "horaFin no puede ser anterior a horaInicio");
    }
  }

  private static void aplicar(HorarioSemanal h, HorarioDiaRequest req) {
    h.setActivo(req.activo());
    h.setHoraInicio(req.horaInicio());
    h.setHoraFin(req.horaFin());
    h.setLugar(blancoANulo(req.lugar()));
    h.setConGimnasio(req.conGimnasio());
    h.setConNatacion(req.conNatacion());
    h.setConWaterpolo(req.conWaterpolo());
  }

  private static String blancoANulo(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private HorarioDiaResponse aRespuesta(HorarioSemanal h) {
    return new HorarioDiaResponse(
      h.getDiaSemana(),
      nombreDia(h.getDiaSemana()),
      h.isActivo(),
      h.getHoraInicio(),
      h.getHoraFin(),
      h.getLugar(),
      h.isConGimnasio(),
      h.isConNatacion(),
      h.isConWaterpolo()
    );
  }

  private static SemanaResponse plantillaPorDefecto() {
    List<HorarioDiaResponse> dias = new ArrayList<>();
    for (int d = 1; d <= 7; d++) {
      dias.add(filaDefecto(d));
    }
    return new SemanaResponse(dias);
  }

  private static HorarioDiaResponse filaDefecto(int dia) {
    boolean laborable = dia >= 1 && dia <= 5;
    return new HorarioDiaResponse(
      dia,
      nombreDia(dia),
      laborable,
      laborable ? LocalTime.of(18, 0) : null,
      laborable ? LocalTime.of(20, 0) : null,
      laborable ? "Piscina" : null,
      laborable,
      laborable,
      laborable
    );
  }

  private static String nombreDia(int dia) {
    if (dia >= 1 && dia <= 7) return NOMBRES[dia];
    return DayOfWeek.of(dia).name();
  }
}
