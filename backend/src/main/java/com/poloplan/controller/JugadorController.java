package com.poloplan.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.dto.JugadorDtos.ActualizarJugadorRequest;
import com.poloplan.dto.JugadorDtos.CrearJugadorRequest;
import com.poloplan.dto.JugadorDtos.JugadorResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.JugadorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones/{planId}/jugadores")
public class JugadorController {

  private final CurrentUserService currentUserService;
  private final JugadorService jugadorService;

  public JugadorController(CurrentUserService currentUserService, JugadorService jugadorService) {
    this.currentUserService = currentUserService;
    this.jugadorService = jugadorService;
  }

  @PostMapping
  public ResponseEntity<JugadorResponse> crear(
    Authentication authentication,
    @PathVariable Long planId,
    @Valid @RequestBody CrearJugadorRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    JugadorResponse creado = jugadorService.crear(user, planId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(creado);
  }

  @GetMapping
  public ResponseEntity<List<JugadorResponse>> listar(Authentication authentication, @PathVariable Long planId) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(jugadorService.listar(user, planId));
  }

  @GetMapping("/{jugadorId}")
  public ResponseEntity<JugadorResponse> obtener(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long jugadorId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(jugadorService.obtener(user, planId, jugadorId));
  }

  @PutMapping("/{jugadorId}")
  public ResponseEntity<JugadorResponse> actualizar(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long jugadorId,
    @Valid @RequestBody ActualizarJugadorRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(jugadorService.actualizar(user, planId, jugadorId, request));
  }

  @DeleteMapping("/{jugadorId}")
  public ResponseEntity<Void> eliminar(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long jugadorId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    jugadorService.eliminar(user, planId, jugadorId);
    return ResponseEntity.noContent().build();
  }
}

