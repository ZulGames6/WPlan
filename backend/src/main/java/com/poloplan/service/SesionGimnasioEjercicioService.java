package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.SesionGimnasioEjercicioDtos.ActualizarRequest;
import com.poloplan.dto.SesionGimnasioEjercicioDtos.AnadirRequest;
import com.poloplan.dto.SesionGimnasioEjercicioDtos.SesionGimnasioEjercicioResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioGimnasio;
import com.poloplan.entity.SesionDia;
import com.poloplan.entity.SesionGimnasio;
import com.poloplan.entity.SesionGimnasioEjercicio;
import com.poloplan.repository.EjercicioGimnasioRepository;
import com.poloplan.repository.SesionDiaRepository;
import com.poloplan.repository.SesionGimnasioEjercicioRepository;

@Service
public class SesionGimnasioEjercicioService {

  private final SesionDiaRepository sesionDiaRepository;
  private final EjercicioGimnasioRepository ejercicioRepository;
  private final SesionGimnasioEjercicioRepository junctionRepository;
  private final PlanificacionResolver planificacionResolver;

  public SesionGimnasioEjercicioService(
    SesionDiaRepository sesionDiaRepository,
    EjercicioGimnasioRepository ejercicioRepository,
    SesionGimnasioEjercicioRepository junctionRepository,
    PlanificacionResolver planificacionResolver
  ) {
    this.sesionDiaRepository = sesionDiaRepository;
    this.ejercicioRepository = ejercicioRepository;
    this.junctionRepository = junctionRepository;
    this.planificacionResolver = planificacionResolver;
  }

  @Transactional
  public SesionGimnasioEjercicioResponse anadir(AppUser propietario, Long planNumero, Long sesionId, AnadirRequest req) {
    SesionDia s = requireSesion(propietario, planNumero, sesionId);
    SesionGimnasio sg = ensureGimnasioPart(s);
    EjercicioGimnasio ej = ejercicioRepository.findByIdAndOwner(req.ejercicioId(), propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio de gimnasio no encontrado"));

    int orden = req.orden() != null
      ? req.orden()
      : junctionRepository.maxOrden(Objects.requireNonNull(sg.getId())) + 1;

    SesionGimnasioEjercicio i = new SesionGimnasioEjercicio();
    i.setSesionGimnasio(sg);
    i.setEjercicio(ej);
    i.setOrden(orden);
    i.setSeries(req.series());
    i.setRepeticiones(req.repeticiones());
    i.setPesoKg(req.pesoKg());
    i.setPorcRm(req.porcRm());
    i.setRir(req.rir());
    i.setDescansoSeg(req.descansoSeg());
    i.setTempo(blancoANulo(req.tempo()));
    i.setNotas(blancoANulo(req.notas()));

    junctionRepository.save(i);
    return aRespuesta(i);
  }

  public List<SesionGimnasioEjercicioResponse> listar(AppUser propietario, Long planNumero, Long sesionId) {
    requireSesion(propietario, planNumero, sesionId);
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    return junctionRepository.listBySesionAndOwner(numero, sesionId, propietario.getId())
      .stream().map(this::aRespuesta).toList();
  }

  @Transactional
  public SesionGimnasioEjercicioResponse actualizar(AppUser propietario, Long planNumero, Long sesionId, Long itemId, ActualizarRequest req) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    SesionGimnasioEjercicio i = junctionRepository.findOwned(numero, sesionId, itemId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada"));
    if (req.orden() != null) i.setOrden(req.orden());
    if (req.series() != null) i.setSeries(req.series());
    if (req.repeticiones() != null) i.setRepeticiones(req.repeticiones());
    if (req.pesoKg() != null) i.setPesoKg(req.pesoKg());
    if (req.porcRm() != null) i.setPorcRm(req.porcRm());
    if (req.rir() != null) i.setRir(req.rir());
    if (req.descansoSeg() != null) i.setDescansoSeg(req.descansoSeg());
    i.setTempo(blancoANulo(req.tempo()));
    i.setNotas(blancoANulo(req.notas()));
    junctionRepository.save(i);
    return aRespuesta(i);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long planNumero, Long sesionId, Long itemId) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    SesionGimnasioEjercicio i = junctionRepository.findOwned(numero, sesionId, itemId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada"));
    junctionRepository.deleteById(Objects.requireNonNull(i.getId()));
  }

  private SesionDia requireSesion(AppUser propietario, Long planNumero, Long sesionId) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    return sesionDiaRepository.findByIdAndPlanNumeroAndOwner(sesionId, numero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
  }

  private SesionGimnasio ensureGimnasioPart(SesionDia s) {
    if (s.getGimnasio() != null) return s.getGimnasio();
    SesionGimnasio sg = new SesionGimnasio();
    sg.setSesionDia(s);
    s.setGimnasio(sg);
    sesionDiaRepository.save(s);
    return sg;
  }

  private static String blancoANulo(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private SesionGimnasioEjercicioResponse aRespuesta(SesionGimnasioEjercicio i) {
    EjercicioGimnasio ej = i.getEjercicio();
    return new SesionGimnasioEjercicioResponse(
      i.getId(),
      ej.getId(),
      ej.getNombre(),
      ej.getGrupoMuscular(),
      ej.getPatron(),
      ej.getEquipamiento(),
      i.getOrden(),
      i.getSeries(),
      i.getRepeticiones(),
      i.getPesoKg(),
      i.getPorcRm(),
      i.getRir(),
      i.getDescansoSeg(),
      i.getTempo(),
      i.getNotas()
    );
  }
}
