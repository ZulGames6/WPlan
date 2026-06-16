package com.poloplan.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.PlanificacionDtos.ActualizarPlanificacionRequest;
import com.poloplan.dto.PlanificacionDtos.CrearPlanificacionRequest;
import com.poloplan.dto.PlanificacionDtos.PlanificacionResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.Planificacion;
import com.poloplan.repository.PlanificacionRepository;

@Service
public class PlanificacionService {

  private final PlanificacionRepository planificacionRepository;
  private final PlanificacionResolver planificacionResolver;

  public PlanificacionService(
    PlanificacionRepository planificacionRepository,
    PlanificacionResolver planificacionResolver
  ) {
    this.planificacionRepository = planificacionRepository;
    this.planificacionResolver = planificacionResolver;
  }

  @Transactional
  public PlanificacionResponse crear(AppUser propietario, CrearPlanificacionRequest request) {
    validarRangoFechas(request.fechaInicio(), request.fechaFin());

    Planificacion p = new Planificacion();
    p.setUser(propietario);
    p.setNumero(siguienteNumero(propietario));
    p.setNombre(request.nombre().trim());
    p.setFechaInicio(request.fechaInicio());
    p.setFechaFin(request.fechaFin());
    p.setNotas(blancoANulo(request.notas()));

    planificacionRepository.save(p);
    return aRespuesta(p);
  }

  public List<PlanificacionResponse> listarPorUsuario(AppUser propietario) {
    planificacionResolver.backfillNumerosFaltantes(propietario);
    return planificacionRepository.listByOwner(propietario.getId()).stream()
      .map(this::aRespuesta)
      .toList();
  }

  public PlanificacionResponse obtener(AppUser propietario, Long planNumero) {
    return aRespuesta(planificacionResolver.require(propietario, planNumero));
  }

  @Transactional
  public PlanificacionResponse actualizar(
    AppUser propietario,
    Long planNumero,
    ActualizarPlanificacionRequest request
  ) {
    validarRangoFechas(request.fechaInicio(), request.fechaFin());

    Planificacion p = planificacionRepository.findByNumeroAndOwner(planNumero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planificación no encontrada"));

    p.setNombre(request.nombre().trim());
    p.setFechaInicio(request.fechaInicio());
    p.setFechaFin(request.fechaFin());
    p.setNotas(blancoANulo(request.notas()));

    planificacionRepository.save(p);
    return aRespuesta(p);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long planNumero) {
    Planificacion p = planificacionResolver.require(propietario, planNumero);
    planificacionRepository.deleteById(Objects.requireNonNull(p.getId()));
  }

  private long siguienteNumero(AppUser propietario) {
    // Nota: para concurrencia real habría que bloquear por usuario (p. ej. tabla auxiliar o SELECT ... FOR UPDATE).
    // Para este TFG (una instancia) suele ser suficiente.
    return planificacionRepository.maxNumeroByOwner(propietario.getId()) + 1;
  }

  private static void validarRangoFechas(LocalDate inicio, LocalDate fin) {
    if (inicio != null && fin != null && fin.isBefore(inicio)) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "fechaFin no puede ser anterior a fechaInicio"
      );
    }
  }

  private static String blancoANulo(String s) {
    if (s == null) {
      return null;
    }
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private PlanificacionResponse aRespuesta(Planificacion p) {
    return new PlanificacionResponse(
      p.getNumero(),
      p.getNombre(),
      p.getFechaInicio(),
      p.getFechaFin(),
      p.getNotas()
    );
  }
}
