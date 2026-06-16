package com.poloplan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poloplan.dto.CatalogoDtos.SeedCatalogoResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.service.CatalogoSeedService;
import com.poloplan.service.CurrentUserService;

@RestController
@RequestMapping("/api/ejercicios/catalogo")
public class CatalogoController {

  private final CurrentUserService currentUserService;
  private final CatalogoSeedService catalogoSeedService;

  public CatalogoController(CurrentUserService currentUserService, CatalogoSeedService catalogoSeedService) {
    this.currentUserService = currentUserService;
    this.catalogoSeedService = catalogoSeedService;
  }

  /** Carga el catálogo base. Por defecto solo si está vacío; ?forzar=true añade los que falten. */
  @PostMapping("/inicializar")
  public ResponseEntity<SeedCatalogoResponse> inicializar(
    Authentication authentication,
    @RequestParam(defaultValue = "false") boolean forzar
  ) {
    AppUser user = currentUserService.requireUser(authentication);
    SeedCatalogoResponse res = forzar
      ? catalogoSeedService.seedForzado(user)
      : catalogoSeedService.seedSiVacio(user);
    return ResponseEntity.ok(res);
  }
}
