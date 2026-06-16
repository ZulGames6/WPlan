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

import com.poloplan.dto.SesionNatacionDtos.ActualizarBloqueRequest;
import com.poloplan.dto.SesionNatacionDtos.ActualizarItemRequest;
import com.poloplan.dto.SesionNatacionDtos.AnadirItemRequest;
import com.poloplan.dto.SesionNatacionDtos.BloqueResponse;
import com.poloplan.dto.SesionNatacionDtos.CrearBloqueRequest;
import com.poloplan.dto.SesionNatacionDtos.ItemResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CurrentUserService;
import com.poloplan.service.SesionNatacionBloqueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planificaciones/{planNumero}/sesiones/{sesionId}/natacion/bloques")
public class SesionNatacionBloqueController {

  private final CurrentUserService currentUserService;
  private final SesionNatacionBloqueService service;

  public SesionNatacionBloqueController(CurrentUserService currentUserService, SesionNatacionBloqueService service) {
    this.currentUserService = currentUserService;
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<BloqueResponse> crearBloque(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @Valid @RequestBody CrearBloqueRequest req
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearBloque(user, planNumero, sesionId, req));
  }

  @GetMapping
  public ResponseEntity<List<BloqueResponse>> listarBloques(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(service.listarBloques(user, planNumero, sesionId));
  }

  @PutMapping("/{bloqueId}")
  public ResponseEntity<BloqueResponse> actualizarBloque(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long bloqueId,
    @Valid @RequestBody ActualizarBloqueRequest req
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(service.actualizarBloque(user, planNumero, sesionId, bloqueId, req));
  }

  @DeleteMapping("/{bloqueId}")
  public ResponseEntity<Void> eliminarBloque(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long bloqueId
  ) {
    AppUser user = currentUserService.requireUser(auth);
    service.eliminarBloque(user, planNumero, sesionId, bloqueId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{bloqueId}/items")
  public ResponseEntity<ItemResponse> anadirItem(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long bloqueId,
    @Valid @RequestBody AnadirItemRequest req
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.status(HttpStatus.CREATED).body(service.anadirItem(user, planNumero, sesionId, bloqueId, req));
  }

  @PutMapping("/{bloqueId}/items/{itemId}")
  public ResponseEntity<ItemResponse> actualizarItem(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long bloqueId,
    @PathVariable Long itemId,
    @Valid @RequestBody ActualizarItemRequest req
  ) {
    AppUser user = currentUserService.requireUser(auth);
    return ResponseEntity.ok(service.actualizarItem(user, planNumero, sesionId, bloqueId, itemId, req));
  }

  @DeleteMapping("/{bloqueId}/items/{itemId}")
  public ResponseEntity<Void> eliminarItem(
    Authentication auth,
    @PathVariable Long planNumero,
    @PathVariable Long sesionId,
    @PathVariable Long bloqueId,
    @PathVariable Long itemId
  ) {
    AppUser user = currentUserService.requireUser(auth);
    service.eliminarItem(user, planNumero, sesionId, bloqueId, itemId);
    return ResponseEntity.noContent().build();
  }
}
