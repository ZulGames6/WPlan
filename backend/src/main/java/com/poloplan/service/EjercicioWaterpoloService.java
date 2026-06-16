package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.EjercicioWaterpoloDtos.ActualizarEjercicioWaterpoloRequest;
import com.poloplan.dto.EjercicioWaterpoloDtos.CrearEjercicioWaterpoloRequest;
import com.poloplan.dto.EjercicioWaterpoloDtos.EjercicioWaterpoloResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioWaterpolo;
import com.poloplan.repository.EjercicioWaterpoloRepository;

@Service
public class EjercicioWaterpoloService {

  private final EjercicioWaterpoloRepository repo;

  public EjercicioWaterpoloService(EjercicioWaterpoloRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public EjercicioWaterpoloResponse crear(AppUser propietario, CrearEjercicioWaterpoloRequest req) {
    validarJugadores(req.jugadoresMin(), req.jugadoresMax());
    EjercicioWaterpolo e = new EjercicioWaterpolo();
    e.setOwner(propietario);
    aplicar(e, req.nombre(), req.objetivo(), req.categoria(), req.intensidad(),
      req.material(), req.duracionMinSugerida(), req.jugadoresMin(), req.jugadoresMax(), req.descripcion());
    repo.save(e);
    return aRespuesta(e);
  }

  public List<EjercicioWaterpoloResponse> listar(AppUser propietario) {
    return repo.listByOwner(propietario.getId()).stream().map(this::aRespuesta).toList();
  }

  public EjercicioWaterpoloResponse obtener(AppUser propietario, Long id) {
    return aRespuesta(require(propietario, id));
  }

  @Transactional
  public EjercicioWaterpoloResponse actualizar(AppUser propietario, Long id, ActualizarEjercicioWaterpoloRequest req) {
    validarJugadores(req.jugadoresMin(), req.jugadoresMax());
    EjercicioWaterpolo e = require(propietario, id);
    aplicar(e, req.nombre(), req.objetivo(), req.categoria(), req.intensidad(),
      req.material(), req.duracionMinSugerida(), req.jugadoresMin(), req.jugadoresMax(), req.descripcion());
    repo.save(e);
    return aRespuesta(e);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long id) {
    EjercicioWaterpolo e = require(propietario, id);
    repo.deleteById(Objects.requireNonNull(e.getId()));
  }

  private EjercicioWaterpolo require(AppUser propietario, Long id) {
    return repo.findByIdAndOwner(id, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio de waterpolo no encontrado"));
  }

  private static void validarJugadores(Integer min, Integer max) {
    if (min != null && max != null && min > max) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jugadoresMin no puede ser mayor que jugadoresMax");
    }
  }

  private static void aplicar(EjercicioWaterpolo e, String nombre, String objetivo, String categoria,
                              String intensidad, String material, Integer duracion,
                              Integer jMin, Integer jMax, String descripcion) {
    e.setNombre(nombre.trim());
    e.setObjetivo(blancoANulo(objetivo));
    e.setCategoria(normalizarMayus(categoria));
    e.setIntensidad(normalizarMayus(intensidad));
    e.setMaterial(blancoANulo(material));
    e.setDuracionMinSugerida(duracion);
    e.setJugadoresMin(jMin);
    e.setJugadoresMax(jMax);
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

  private EjercicioWaterpoloResponse aRespuesta(EjercicioWaterpolo e) {
    return new EjercicioWaterpoloResponse(
      e.getId(), e.getNombre(), e.getObjetivo(), e.getCategoria(),
      e.getIntensidad(), e.getMaterial(), e.getDuracionMinSugerida(),
      e.getJugadoresMin(), e.getJugadoresMax(), e.getDescripcion()
    );
  }
}
