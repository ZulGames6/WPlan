package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.SesionWaterpoloEjercicioDtos.ActualizarEjercicioRequest;
import com.poloplan.dto.SesionWaterpoloEjercicioDtos.AnadirEjercicioRequest;
import com.poloplan.dto.SesionWaterpoloEjercicioDtos.SesionWaterpoloEjercicioResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioWaterpolo;
import com.poloplan.entity.SesionDia;
import com.poloplan.entity.SesionWaterpolo;
import com.poloplan.entity.SesionWaterpoloEjercicio;
import com.poloplan.repository.EjercicioWaterpoloRepository;
import com.poloplan.repository.SesionDiaRepository;
import com.poloplan.repository.SesionWaterpoloEjercicioRepository;

@Service
public class SesionWaterpoloEjercicioService {

  private final SesionDiaRepository sesionDiaRepository;
  private final EjercicioWaterpoloRepository ejercicioRepository;
  private final SesionWaterpoloEjercicioRepository junctionRepository;

  public SesionWaterpoloEjercicioService(
    SesionDiaRepository sesionDiaRepository,
    EjercicioWaterpoloRepository ejercicioRepository,
    SesionWaterpoloEjercicioRepository junctionRepository
  ) {
    this.sesionDiaRepository = sesionDiaRepository;
    this.ejercicioRepository = ejercicioRepository;
    this.junctionRepository = junctionRepository;
  }

  @Transactional
  public SesionWaterpoloEjercicioResponse anadir(
    AppUser propietario,
    Long planNumero,
    Long sesionId,
    AnadirEjercicioRequest request
  ) {
    SesionDia s = requireSesion(propietario, planNumero, sesionId);
    SesionWaterpolo sw = ensureWaterpoloPart(s);
    EjercicioWaterpolo ej = requireEjercicio(propietario, request.ejercicioId());

    int orden = request.orden() != null
      ? request.orden()
      : junctionRepository.maxOrden(Objects.requireNonNull(sw.getId())) + 1;

    SesionWaterpoloEjercicio se = new SesionWaterpoloEjercicio();
    se.setSesionWaterpolo(sw);
    se.setEjercicio(ej);
    se.setOrden(orden);
    se.setDuracionMin(request.duracionMin());
    se.setNotas(blancoANulo(request.notas()));

    junctionRepository.save(se);
    return aRespuesta(se);
  }

  public List<SesionWaterpoloEjercicioResponse> listar(AppUser propietario, Long planNumero, Long sesionId) {
    requireSesion(propietario, planNumero, sesionId);
    return junctionRepository.listBySesionAndOwner(planNumero, sesionId, propietario.getId())
      .stream().map(this::aRespuesta).toList();
  }

  @Transactional
  public SesionWaterpoloEjercicioResponse actualizar(
    AppUser propietario,
    Long planNumero,
    Long sesionId,
    Long itemId,
    ActualizarEjercicioRequest request
  ) {
    SesionWaterpoloEjercicio se = junctionRepository.findOwned(planNumero, sesionId, itemId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada"));

    if (request.orden() != null) se.setOrden(request.orden());
    if (request.duracionMin() != null) se.setDuracionMin(request.duracionMin());
    se.setNotas(blancoANulo(request.notas()));

    junctionRepository.save(se);
    return aRespuesta(se);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long planNumero, Long sesionId, Long itemId) {
    SesionWaterpoloEjercicio se = junctionRepository.findOwned(planNumero, sesionId, itemId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada"));
    junctionRepository.deleteById(Objects.requireNonNull(se.getId()));
  }

  private SesionDia requireSesion(AppUser propietario, Long planNumero, Long sesionId) {
    return sesionDiaRepository.findByIdAndPlanNumeroAndOwner(sesionId, planNumero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
  }

  private EjercicioWaterpolo requireEjercicio(AppUser propietario, Long ejercicioId) {
    return ejercicioRepository.findByIdAndOwner(ejercicioId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio de waterpolo no encontrado"));
  }

  private SesionWaterpolo ensureWaterpoloPart(SesionDia s) {
    if (s.getWaterpolo() != null) {
      return s.getWaterpolo();
    }
    SesionWaterpolo sw = new SesionWaterpolo();
    sw.setSesionDia(s);
    s.setWaterpolo(sw);
    sesionDiaRepository.save(s);
    return sw;
  }

  private static String blancoANulo(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private SesionWaterpoloEjercicioResponse aRespuesta(SesionWaterpoloEjercicio se) {
    EjercicioWaterpolo ej = se.getEjercicio();
    return new SesionWaterpoloEjercicioResponse(
      se.getId(),
      ej.getId(),
      ej.getNombre(),
      ej.getObjetivo(),
      ej.getCategoria(),
      ej.getIntensidad(),
      ej.getMaterial(),
      se.getOrden(),
      se.getDuracionMin(),
      se.getNotas()
    );
  }
}
