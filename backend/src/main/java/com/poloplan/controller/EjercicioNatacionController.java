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

import com.poloplan.dto.EjercicioNatacionDtos.ActualizarEjercicioNatacionRequest;
import com.poloplan.dto.EjercicioNatacionDtos.CrearEjercicioNatacionRequest;
import com.poloplan.dto.EjercicioNatacionDtos.EjercicioNatacionResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.EjercicioNatacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ejercicios/natacion")
public class EjercicioNatacionController {

  private final CurrentUserService currentUserService;
  private final EjercicioNatacionService service;

  public EjercicioNatacionController(CurrentUserService currentUserService, EjercicioNatacionService service) {
    this.currentUserService = currentUserService;
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<EjercicioNatacionResponse> crear(
    Authentication authentication,
    @Valid @RequestBody CrearEjercicioNatacionRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(user, request));
  }

  @GetMapping
  public ResponseEntity<List<EjercicioNatacionResponse>> listar(Authentication authentication) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(service.listar(user));
  }

  @GetMapping("/{id}")
  public ResponseEntity<EjercicioNatacionResponse> obtener(Authentication authentication, @PathVariable Long id) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(service.obtener(user, id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<EjercicioNatacionResponse> actualizar(
    Authentication authentication,
    @PathVariable Long id,
    @Valid @RequestBody ActualizarEjercicioNatacionRequest request
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    return ResponseEntity.ok(service.actualizar(user, id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(Authentication authentication, @PathVariable Long id) {
    AppUser user = currentUserService.requireUser(authentication);
    service.eliminar(user, id);
    return ResponseEntity.noContent().build();
  }
}
