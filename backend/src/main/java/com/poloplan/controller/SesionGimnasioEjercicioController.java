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

import com.poloplan.dto.SesionGimnasioEjercicioDtos.ActualizarRequest;
import com.poloplan.dto.SesionGimnasioEjercicioDtos.AnadirRequest;
import com.poloplan.dto.SesionGimnasioEjercicioDtos.SesionGimnasioEjercicioResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.SesionGimnasioEjercicioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones/{planNumero}/sesiones/{sesionId}/gimnasio/ejercicios")
public class SesionGimnasioEjercicioController {

  private final CurrentUserService currentUserService;
  private final SesionGimnasioEjercicioService service;

  public SesionGimnasioEjercicioController(CurrentUserService currentUserService, SesionGimnasioEjercicioService service) {
    this.currentUserService = currentUserService;
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<SesionGimnasioEjercicioResponse> anadir(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @Valid @RequestBody AnadirRequest req
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.anadir(user, planNumero, sesionId, req));
  }

  @GetMapping
  public ResponseEntity<List<SesionGimnasioEjercicioResponse>> listar(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(service.listar(user, planNumero, sesionId));
  }

  @PutMapping("/{itemId}")
  public ResponseEntity<SesionGimnasioEjercicioResponse> actualizar(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long itemId,
    @Valid @RequestBody ActualizarRequest req
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(service.actualizar(user, planNumero, sesionId, itemId, req));
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> eliminar(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long itemId
  ) {
    AppUser user = currentUserService.requireUser(auth);
    service.eliminar(user, planNumero, sesionId, itemId);
    return ResponseEntity.noContent().build();
  }
}
