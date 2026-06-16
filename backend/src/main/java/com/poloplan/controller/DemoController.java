package com.poloplan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.DemoService;
import com.poloplan.service.DemoService.DemoResponse;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

  private final CurrentUserService currentUserService;
  private final DemoService demoService;

  public DemoController(CurrentUserService currentUserService, DemoService demoService) {
    this.currentUserService = currentUserService;
    this.demoService = demoService;
  }

  /**
   * Crea una planificación de demostración para el usuario autenticado.
   * Incluye catálogo, jugadores, horario semanal, 12 semanas de sesiones con
   * contenido real (bloques de natación, ejercicios de waterpolo y gimnasio)
   * y asistencias variadas. Pensado para que el usuario pueda probar métricas
   * sin tener que rellenar todo a mano.
   */
  @PostMapping("/planificacion")
  public ResponseEntity<DemoResponse> crearDemo(Authentication auth) {
    AppUser user = currentUserService.requireUser(auth);
    DemoResponse resp = demoService.crear(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(resp);
  }
}
