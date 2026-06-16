package com.poloplan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.dto.SugerenciasDtos.SugerenciaGimnasioResponse;
import com.poloplan.dto.SugerenciasDtos.SugerenciaNatacionResponse;
import com.poloplan.dto.SugerenciasDtos.SugerirGimnasioRequest;
import com.poloplan.dto.SugerenciasDtos.SugerirNatacionRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.SugerenciasGimnasioService;
import com.poloplan.service.SugerenciasNatacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sugerencias")
public class SugerenciasController {

  private final CurrentUserService currentUserService;
  private final SugerenciasGimnasioService gimnasioService;
  private final SugerenciasNatacionService natacionService;

  public SugerenciasController(
    CurrentUserService currentUserService,
    SugerenciasGimnasioService gimnasioService,
    SugerenciasNatacionService natacionService
  ) {
    this.currentUserService = currentUserService;
    this.gimnasioService = gimnasioService;
    this.natacionService = natacionService;
  }

  @PostMapping("/gimnasio")
  public ResponseEntity<SugerenciaGimnasioResponse> sugerirGimnasio(Authentication auth, @Valid @RequestBody SugerirGimnasioRequest req) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(gimnasioService.sugerir(user, req));
  }

  @PostMapping("/natacion")
  public ResponseEntity<SugerenciaNatacionResponse> sugerirNatacion(Authentication auth, @Valid @RequestBody SugerirNatacionRequest req) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(natacionService.sugerir(user, req));
  }
}
