package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.AsistenciaDtos.AsistenciaResponse;
import com.poloplan.dto.AsistenciaDtos.MarcarAsistenciaRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.Asistencia;
import com.poloplan.entity.Jugador;
import com.poloplan.entity.SesionDia;
import com.poloplan.repository.AsistenciaRepository;
import com.poloplan.repository.JugadorRepository;
import com.poloplan.repository.SesionDiaRepository;

@Service
public class AsistenciaService {

  private final SesionDiaRepository sesionDiaRepository;
  private final JugadorRepository jugadorRepository;
  private final AsistenciaRepository asistenciaRepository;

  public AsistenciaService(
    SesionDiaRepository sesionDiaRepository,
    JugadorRepository jugadorRepository,
    AsistenciaRepository asistenciaRepository
  ) {
    this.sesionDiaRepository = sesionDiaRepository;
    this.jugadorRepository = jugadorRepository;
    this.asistenciaRepository = asistenciaRepository;
  }

  public List<AsistenciaResponse> listar(AppUser propietario, Long planNumero, Long sesionId) {
    // 404 si la sesión no es del usuario / no existe
    requireSesion(propietario, planNumero, sesionId);
    return asistenciaRepository.listBySesionAndOwner(planNumero, sesionId, propietario.getId()).stream()
      .map(this::aRespuesta)
      .toList();
  }

  @Transactional
  public AsistenciaResponse marcar(
    AppUser propietario,
    Long planNumero,
    Long sesionId,
    Long jugadorId,
    MarcarAsistenciaRequest request
  ) {
    SesionDia sesion = requireSesion(propietario, planNumero, sesionId);
    Jugador jugador = requireJugador(propietario, planNumero, jugadorId);

    if (!Objects.equals(sesion.getPlanificacion().getId(), jugador.getPlanificacion().getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El jugador no pertenece a la misma planificación de la sesión");
    }

    Asistencia a = asistenciaRepository.findBySesionAndJugadorAndOwner(planNumero, sesionId, jugadorId, propietario.getId())
      .orElseGet(() -> {
        Asistencia nueva = new Asistencia();
        nueva.setSesionDia(sesion);
        nueva.setJugador(jugador);
        return nueva;
      });

    a.setEstado(request.estado().trim().toUpperCase());
    a.setNota(blancoANulo(request.nota()));

    asistenciaRepository.save(Objects.requireNonNull(a));
    return aRespuesta(a);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long planNumero, Long sesionId, Long jugadorId) {
    Asistencia a = asistenciaRepository.findBySesionAndJugadorAndOwner(planNumero, sesionId, jugadorId, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asistencia no encontrada"));
    asistenciaRepository.deleteById(Objects.requireNonNull(a.getId()));
  }

  private SesionDia requireSesion(AppUser propietario, Long planNumero, Long sesionId) {
    return sesionDiaRepository.findByIdAndPlanNumeroAndOwner(sesionId, planNumero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
  }

  private Jugador requireJugador(AppUser propietario, Long planNumero, Long jugadorId) {
    return jugadorRepository.findByIdAndPlanNumeroAndOwner(jugadorId, planNumero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jugador no encontrado"));
  }

  private static String blancoANulo(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private AsistenciaResponse aRespuesta(Asistencia a) {
    return new AsistenciaResponse(
      a.getId(),
      a.getJugador().getId(),
      a.getEstado(),
      a.getNota()
    );
  }
}

