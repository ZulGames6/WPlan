package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.EjercicioGimnasioDtos.ActualizarEjercicioGimnasioRequest;
import com.poloplan.dto.EjercicioGimnasioDtos.CrearEjercicioGimnasioRequest;
import com.poloplan.dto.EjercicioGimnasioDtos.EjercicioGimnasioResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioGimnasio;
import com.poloplan.repository.EjercicioGimnasioRepository;

@Service
public class EjercicioGimnasioService {

  private final EjercicioGimnasioRepository repo;

  public EjercicioGimnasioService(EjercicioGimnasioRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public EjercicioGimnasioResponse crear(AppUser propietario, CrearEjercicioGimnasioRequest req) {
    EjercicioGimnasio e = new EjercicioGimnasio();
    e.setOwner(propietario);
    aplicar(e, req.nombre(), req.grupoMuscular(), req.patron(), req.equipamiento(),
      req.tipo(), req.unilateral(), req.descripcion());
    repo.save(e);
    return aRespuesta(e);
  }

  public List<EjercicioGimnasioResponse> listar(AppUser propietario) {
    return repo.listByOwner(propietario.getId()).stream().map(this::aRespuesta).toList();
  }

  public EjercicioGimnasioResponse obtener(AppUser propietario, Long id) {
    return aRespuesta(require(propietario, id));
  }

  @Transactional
  public EjercicioGimnasioResponse actualizar(AppUser propietario, Long id, ActualizarEjercicioGimnasioRequest req) {
    EjercicioGimnasio e = require(propietario, id);
    aplicar(e, req.nombre(), req.grupoMuscular(), req.patron(), req.equipamiento(),
      req.tipo(), req.unilateral(), req.descripcion());
    repo.save(e);
    return aRespuesta(e);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long id) {
    EjercicioGimnasio e = require(propietario, id);
    repo.deleteById(Objects.requireNonNull(e.getId()));
  }

  private EjercicioGimnasio require(AppUser propietario, Long id) {
    return repo.findByIdAndOwner(id, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio de gimnasio no encontrado"));
  }

  private static void aplicar(EjercicioGimnasio e, String nombre, String grupoMuscular,
                              String patron, String equipamiento, String tipo,
                              Boolean unilateral, String descripcion) {
    e.setNombre(nombre.trim());
    e.setGrupoMuscular(normalizarMayus(grupoMuscular));
    e.setPatron(normalizarMayus(patron));
    e.setEquipamiento(normalizarMayus(equipamiento));
    e.setTipo(normalizarMayus(tipo));
    e.setUnilateral(unilateral);
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

  private EjercicioGimnasioResponse aRespuesta(EjercicioGimnasio e) {
    return new EjercicioGimnasioResponse(
      e.getId(), e.getNombre(), e.getGrupoMuscular(), e.getPatron(),
      e.getEquipamiento(), e.getTipo(), e.getUnilateral(), e.getDescripcion()
    );
  }
}
