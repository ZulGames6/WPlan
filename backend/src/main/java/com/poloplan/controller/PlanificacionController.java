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

import com.poloplan.dto.PlanificacionDtos.ActualizarPlanificacionRequest;
import com.poloplan.dto.PlanificacionDtos.CrearPlanificacionRequest;
import com.poloplan.dto.PlanificacionDtos.PlanificacionResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.PlanificacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones")
public class PlanificacionController {

  private final CurrentUserService currentUserService;
  private final PlanificacionService planificacionService;

  public PlanificacionController(
    CurrentUserService currentUserService,
    PlanificacionService planificacionService
  ) {
    this.currentUserService = currentUserService;
    this.planificacionService = planificacionService;
  }

  @PostMapping
  public ResponseEntity<PlanificacionResponse> crear(
    Authentication authentication,
    @Valid @RequestBody CrearPlanificacionRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    PlanificacionResponse creada = planificacionService.crear(user, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(creada);
  }

  @GetMapping
  public ResponseEntity<List<PlanificacionResponse>> listar(Authentication authentication) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(planificacionService.listarPorUsuario(user));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlanificacionResponse> obtener(
    Authentication authentication,
    @PathVariable Long id
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(planificacionService.obtener(user, id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PlanificacionResponse> actualizar(
    Authentication authentication,
    @PathVariable Long id,
    @Valid @RequestBody ActualizarPlanificacionRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(planificacionService.actualizar(user, id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(Authentication authentication, @PathVariable Long id) {
    AppUser user = currentUserService.requireUser(authentication);
    planificacionService.eliminar(user, id);
    return ResponseEntity.noContent().build();
  }
}
