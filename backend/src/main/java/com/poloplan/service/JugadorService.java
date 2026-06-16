package com.poloplan.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.JugadorDtos.ActualizarJugadorRequest;
import com.poloplan.dto.JugadorDtos.CrearJugadorRequest;
import com.poloplan.dto.JugadorDtos.JugadorResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.Jugador;
import com.poloplan.entity.Planificacion;
import com.poloplan.repository.JugadorRepository;
@Service
public class JugadorService {

  private final PlanificacionResolver planificacionResolver;
  private final JugadorRepository jugadorRepository;

  public JugadorService(PlanificacionResolver planificacionResolver, JugadorRepository jugadorRepository) {
    this.planificacionResolver = planificacionResolver;
    this.jugadorRepository = jugadorRepository;
  }

  @Transactional
  public JugadorResponse crear(AppUser propietario, Long planNumero, CrearJugadorRequest request) {
    Planificacion plan = planificacionResolver.require(propietario, planNumero);

    Jugador j = new Jugador();
    j.setPlanificacion(plan);
    aplicarCamposCrear(j, request);

    jugadorRepository.save(j);
    return aRespuesta(j);
  }

  private static void aplicarCamposCrear(Jugador j, CrearJugadorRequest request) {
    j.setNombre(request.nombre().trim());
    j.setApellidos(blancoANulo(request.apellidos()));
    j.setFechaNacimiento(request.fechaNacimiento());
    j.setPosicion(blancoANulo(request.posicion()));
    j.setNotas(blancoANulo(request.notas()));
    j.setPesoKg(request.pesoKg());
    j.setDni(blancoANulo(request.dni()));
    j.setTallaBanador(blancoANulo(request.tallaBanador()));
    j.setTallaCamiseta(blancoANulo(request.tallaCamiseta()));
  }

  public List<JugadorResponse> listar(AppUser propietario, Long planNumero) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    return jugadorRepository.listByPlanNumeroAndOwner(numero, propietario.getId()).stream()
      .map(this::aRespuesta)
      .toList();
  }

  public JugadorResponse obtener(AppUser propietario, Long planNumero, Long jugadorId) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    Jugador j = jugadorRepository.findByIdAndPlanNumeroAndOwner(jugadorId, numero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jugador no encontrado"));
    return aRespuesta(j);
  }

  @Transactional
  public JugadorResponse actualizar(
    AppUser propietario,
    Long planNumero,
    Long jugadorId,
    ActualizarJugadorRequest request
  ) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    Jugador j = jugadorRepository.findByIdAndPlanNumeroAndOwner(jugadorId, numero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jugador no encontrado"));

    j.setNombre(request.nombre().trim());
    j.setApellidos(blancoANulo(request.apellidos()));
    j.setFechaNacimiento(request.fechaNacimiento());
    j.setPosicion(blancoANulo(request.posicion()));
    j.setNotas(blancoANulo(request.notas()));
    j.setPesoKg(request.pesoKg());
    j.setDni(blancoANulo(request.dni()));
    j.setTallaBanador(blancoANulo(request.tallaBanador()));
    j.setTallaCamiseta(blancoANulo(request.tallaCamiseta()));

    jugadorRepository.save(j);
    return aRespuesta(j);
  }

  @Transactional
  public void eliminar(AppUser propietario, Long planNumero, Long jugadorId) {
    long numero = planificacionResolver.requireNumero(propietario, planNumero);
    Jugador j = jugadorRepository.findByIdAndPlanNumeroAndOwner(jugadorId, numero, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jugador no encontrado"));
    jugadorRepository.deleteById(Objects.requireNonNull(j.getId()));
  }

  private static String blancoANulo(String s) {
    if (s == null) {
      return null;
    }
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private JugadorResponse aRespuesta(Jugador j) {
    return new JugadorResponse(
      j.getId(),
      j.getNombre(),
      j.getApellidos(),
      j.getFechaNacimiento(),
      j.getPosicion(),
      j.getNotas(),
      j.getPesoKg(),
      j.getDni(),
      j.getTallaBanador(),
      j.getTallaCamiseta()
    );
  }
}

