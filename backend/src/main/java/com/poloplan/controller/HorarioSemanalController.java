package com.poloplan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.dto.HorarioSemanalDtos.GuardarSemanaRequest;
import com.poloplan.dto.HorarioSemanalDtos.SemanaResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.HorarioSemanalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones/{planNumero}/semana")
public class HorarioSemanalController {

  private final CurrentUserService currentUserService;
  private final HorarioSemanalService horarioSemanalService;

  public HorarioSemanalController(CurrentUserService currentUserService, HorarioSemanalService horarioSemanalService) {
    this.currentUserService = currentUserService;
    this.horarioSemanalService = horarioSemanalService;
  }

  @GetMapping
  public ResponseEntity<SemanaResponse> obtener(Authentication auth, @PathVariable Long planNumero) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(horarioSemanalService.obtener(user, planNumero));
  }

  @PutMapping
  public ResponseEntity<SemanaResponse> guardar(
    Authentication auth,
    @PathVariable Long planNumero,
    @Valid @RequestBody GuardarSemanaRequest request
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(horarioSemanalService.guardar(user, planNumero, request));
  }
}
