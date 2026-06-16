package com.poloplan.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.dto.SesionDtos.ActualizarSesionDiaRequest;
import com.poloplan.dto.SesionDtos.AplicarHorarioResponse;
import com.poloplan.dto.SesionDtos.CrearSesionDiaRequest;
import com.poloplan.dto.SesionDtos.SesionDiaResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.SesionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones/{planId}/sesiones")
public class SesionController {

  private final CurrentUserService currentUserService;
  private final SesionService sesionService;

  public SesionController(CurrentUserService currentUserService, SesionService sesionService) {
    this.currentUserService = currentUserService;
    this.sesionService = sesionService;
  }

  @PostMapping
  public ResponseEntity<SesionDiaResponse> crear(
    Authentication authentication,
    @PathVariable Long planId,
    @Valid @RequestBody CrearSesionDiaRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    SesionDiaResponse creada = sesionService.crear(user, planId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(creada);
  }

  @GetMapping
  public ResponseEntity<List<SesionDiaResponse>> listar(
    Authentication authentication,
    @PathVariable Long planId,
    @RequestParam(required = false) LocalDate desde,
    @RequestParam(required = false) LocalDate hasta
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(sesionService.listar(user, planId, desde, hasta));
  }

  @GetMapping("/{sesionId}")
  public ResponseEntity<SesionDiaResponse> obtener(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long sesionId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(sesionService.obtener(user, planId, sesionId));
  }

  @PutMapping("/{sesionId}")
  public ResponseEntity<SesionDiaResponse> actualizar(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long sesionId,
    @Valid @RequestBody ActualizarSesionDiaRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(sesionService.actualizar(user, planId, sesionId, request));
  }

  @DeleteMapping("/{sesionId}")
  public ResponseEntity<Void> eliminar(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long sesionId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    sesionService.eliminar(user, planId, sesionId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Aplica el horario semanal al rango {@code [desde, hasta]} creando una sesión
   * por cada día activo del horario que no tenga ya una sesión en esa fecha.
   * Las sesiones existentes no se tocan (idempotente). Las partes activas
   * (gimnasio/natación/waterpolo) se crean según los flags del horario.
   */
  @PostMapping("/aplicar-horario")
  public ResponseEntity<AplicarHorarioResponse> aplicarHorario(
    Authentication authentication,
    @PathVariable Long planId,
    @RequestParam LocalDate desde,
    @RequestParam LocalDate hasta
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(sesionService.aplicarHorario(user, planId, desde, hasta));
  }
}

