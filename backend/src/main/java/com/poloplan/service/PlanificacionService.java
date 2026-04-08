package com.poloplan.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

  public PlanificacionService(PlanificacionRepository planificacionRepository) {
    this.planificacionRepository = planificacionRepository;
  }

  public PlanificacionResponse crear(AppUser propietario, CrearPlanificacionRequest request) {
    validarRangoFechas(request.fechaInicio(), request.fechaFin());

    Planificacion p = new Planificacion();
    p.setUser(propietario);
    p.setNombre(request.nombre().trim());
    p.setFechaInicio(request.fechaInicio());
    p.setFechaFin(request.fechaFin());
    p.setNotas(blancoANulo(request.notas()));

    planificacionRepository.save(p);
    return aRespuesta(p);
  }

  public List<PlanificacionResponse> listarPorUsuario(AppUser propietario) {
    return planificacionRepository.listByOwner(propietario.getId()).stream()
      .map(this::aRespuesta)
      .toList();
  }

  public PlanificacionResponse obtener(AppUser propietario, Long planificacionId) {
    Planificacion p = planificacionRepository.findByIdAndOwner(planificacionId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planificación no encontrada"));
    return aRespuesta(p);
  }

  public PlanificacionResponse actualizar(
    AppUser propietario,
    Long planificacionId,
    ActualizarPlanificacionRequest request
  ) {
    validarRangoFechas(request.fechaInicio(), request.fechaFin());

    Planificacion p = planificacionRepository.findByIdAndOwner(planificacionId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planificación no encontrada"));

    p.setNombre(request.nombre().trim());
    p.setFechaInicio(request.fechaInicio());
    p.setFechaFin(request.fechaFin());
    p.setNotas(blancoANulo(request.notas()));

    planificacionRepository.save(p);
    return aRespuesta(p);
  }

  public void eliminar(AppUser propietario, Long planificacionId) {
    Planificacion p = planificacionRepository.findByIdAndOwner(planificacionId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planificación no encontrada"));
    planificacionRepository.delete(p);
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
      p.getId(),
      p.getNombre(),
      p.getFechaInicio(),
      p.getFechaFin(),
      p.getNotas()
    );
  }
}
