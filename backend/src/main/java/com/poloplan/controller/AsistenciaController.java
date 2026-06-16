package com.poloplan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.dto.AsistenciaDtos.AsistenciaResponse;
import com.poloplan.dto.AsistenciaDtos.MarcarAsistenciaRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.service.AsistenciaService;
import com.poloplan.service.CurrentUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones/{planId}/sesiones/{sesionId}/asistencias")
public class AsistenciaController {

  private final CurrentUserService currentUserService;
  private final AsistenciaService asistenciaService;

  public AsistenciaController(CurrentUserService currentUserService, AsistenciaService asistenciaService) {
    this.currentUserService = currentUserService;
    this.asistenciaService = asistenciaService;
  }

  @GetMapping
  public ResponseEntity<List<AsistenciaResponse>> listar(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long sesionId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(asistenciaService.listar(user, planId, sesionId));
  }

  @PutMapping("/{jugadorId}")
  public ResponseEntity<AsistenciaResponse> marcar(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long sesionId,
    @PathVariable Long jugadorId,
    @Valid @RequestBody MarcarAsistenciaRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(asistenciaService.marcar(user, planId, sesionId, jugadorId, request));
  }

  @DeleteMapping("/{jugadorId}")
  public ResponseEntity<Void> eliminar(
    Authentication authentication,
    @PathVariable Long planId,
    @PathVariable Long sesionId,
    @PathVariable Long jugadorId
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    asistenciaService.eliminar(user, planId, sesionId, jugadorId);
    return ResponseEntity.noContent().build();
  }
}

