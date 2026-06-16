package com.poloplan.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.dto.MetricasDtos.AsistenciaResponse;
import com.poloplan.dto.MetricasDtos.CargaResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.MetricasService;

@RestController
@RequestMapping("/api/metricas/{planNumero}")
public class MetricasController {

  private final CurrentUserService currentUserService;
  private final MetricasService metricasService;

  public MetricasController(CurrentUserService currentUserService, MetricasService metricasService) {
    this.currentUserService = currentUserService;
    this.metricasService = metricasService;
  }

  @GetMapping("/carga")
  public ResponseEntity<CargaResponse> carga(
    Authentication auth,
    @PathVariable Long planNumero,
    @RequestParam LocalDate desde,
    @RequestParam LocalDate hasta
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(metricasService.carga(user, planNumero, desde, hasta));
  }

  @GetMapping("/asistencia")
  public ResponseEntity<AsistenciaResponse> asistencia(
    Authentication auth,
    @PathVariable Long planNumero,
    @RequestParam LocalDate desde,
    @RequestParam LocalDate hasta
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(metricasService.asistencia(user, planNumero, desde, hasta));
  }
}
