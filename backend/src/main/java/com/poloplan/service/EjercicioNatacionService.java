package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.EjercicioNatacionDtos.ActualizarEjercicioNatacionRequest;
import com.poloplan.dto.EjercicioNatacionDtos.CrearEjercicioNatacionRequest;
import com.poloplan.dto.EjercicioNatacionDtos.EjercicioNatacionResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioNatacion;
import com.poloplan.repository.EjercicioNatacionRepository;

@Service
public class EjercicioNatacionService {

  private final EjercicioNatacionRepository repo;

  public EjercicioNatacionService(EjercicioNatacionRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public EjercicioNatacionResponse crear(AppUser propietario, CrearEjercicioNatacionRequest req) {
    EjercicioNatacion e = new EjercicioNatacion();
    e.setOwner(propietario);
    aplicar(e, req.nombre(), req.estilo(), req.tipoBloque(), req.intensidad(),
      req.material(), req.metrosBase(), req.descripcion());
    repo.save(e);
    return aRespuesta(e);
  }

  public List<EjercicioNatacionResponse> listar(AppUser propietario) {
    return repo.listByOwner(propietario.getId()).stream().map(this::aRespuesta).toList();
  }

  public EjercicioNatacionResponse obtener(AppUser propietario, Long id) {
    return aRespuesta(require(propietario, id));
  }

  @Transactional
  public EjercicioNatacionResponse actualizar(AppUser propietario, Long id, ActualizarEjercicioNatacionRequest req) {
    EjercicioNatacion e = require(propietario, id);
    aplicar(e, req.nombre(), req.estilo(), req.tipoBloque(), req.intensidad(),
      req.material(), req.metrosBase(), req.descripcion());
    repo.save(e);
    return aRespuesta(e);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long id) {
    EjercicioNatacion e = require(propietario, id);
    repo.deleteById(Objects.requireNonNull(e.getId()));
  }

  private EjercicioNatacion require(AppUser propietario, Long id) {
    return repo.findByIdAndOwner(id, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio de natación no encontrado"));
  }

  private static void aplicar(EjercicioNatacion e, String nombre, String estilo,
                              String tipoBloque, String intensidad, String material,
                              Integer metrosBase, String descripcion) {
    e.setNombre(nombre.trim());
    e.setEstilo(normalizarMayus(estilo));
    e.setTipoBloque(normalizarMayus(tipoBloque));
    e.setIntensidad(normalizarMayus(intensidad));
    e.setMaterial(normalizarMayus(material));
    e.setMetrosBase(metrosBase);
    e.setDescripcion(blancoANulo(descripcion));
  }

  private static String blancoANulo(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private static String normalizarMayus(String s) {
    String t = blancoANulo(s);
    return t == null ? null : t.toUpperCase();
  }

  private EjercicioNatacionResponse aRespuesta(EjercicioNatacion e) {
    return new EjercicioNatacionResponse(
      e.getId(), e.getNombre(), e.getEstilo(), e.getTipoBloque(),
      e.getIntensidad(), e.getMaterial(), e.getMetrosBase(), e.getDescripcion()
    );
  }
}
