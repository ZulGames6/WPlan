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

import com.poloplan.dto.SesionWaterpoloEjercicioDtos.ActualizarEjercicioRequest;
import com.poloplan.dto.SesionWaterpoloEjercicioDtos.AnadirEjercicioRequest;
import com.poloplan.dto.SesionWaterpoloEjercicioDtos.SesionWaterpoloEjercicioResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.SesionWaterpoloEjercicioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones/{planNumero}/sesiones/{sesionId}/waterpolo/ejercicios")
public class SesionWaterpoloEjercicioController {

  private final CurrentUserService currentUserService;
  private final SesionWaterpoloEjercicioService service;

  public SesionWaterpoloEjercicioController(CurrentUserService currentUserService, SesionWaterpoloEjercicioService service) {
    this.currentUserService = currentUserService;
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<SesionWaterpoloEjercicioResponse> anadir(
    Authentication authentication,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @Valid @RequestBody AnadirEjercicioRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    SesionWaterpoloEjercicioResponse creado = service.anadir(user, planNumero, sesionId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(creado);
  }

  @GetMapping
  public ResponseEntity<List<SesionWaterpoloEjercicioResponse>> listar(
    Authentication authentication,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(service.listar(user, planNumero, sesionId));
  }

  @PutMapping("/{itemId}")
  public ResponseEntity<SesionWaterpoloEjercicioResponse> actualizar(
    Authentication authentication,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long itemId,
    @Valid @RequestBody ActualizarEjercicioRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(service.actualizar(user, planNumero, sesionId, itemId, request));
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> eliminar(
    Authentication authentication,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long itemId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    service.eliminar(user, planNumero, sesionId, itemId);
    return ResponseEntity.noContent().build();
  }
}
