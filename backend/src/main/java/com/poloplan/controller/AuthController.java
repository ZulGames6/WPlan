package com.poloplan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.AuthDtos.LoginRequest;
import com.poloplan.dto.AuthDtos.MeResponse;
import com.poloplan.dto.AuthDtos.RegisterRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
    this.authService = authService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  public ResponseEntity<MeResponse> register(
    @Valid @RequestBody RegisterRequest request,
    HttpServletRequest httpRequest,
    HttpServletResponse httpResponse
  ) {
    MeResponse created = authService.register(request);
    establishSession(request.email(), request.password(), httpRequest, httpResponse);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PostMapping("/login")
  public ResponseEntity<MeResponse> login(
    @Valid @RequestBody LoginRequest request,
    HttpServletRequest httpRequest,
    HttpServletResponse httpResponse
  ) {
    establishSession(request.email(), request.password(), httpRequest, httpResponse);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AppUser user)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }
    return ResponseEntity.ok(authService.me(user));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<MeResponse> me(Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof AppUser user)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }

    return ResponseEntity.ok(authService.me(user));
  }

  private void establishSession(String email, String rawPassword, HttpServletRequest request, HttpServletResponse response) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
      email.trim().toLowerCase(),
      rawPassword
    );
    Authentication auth = authenticationManager.authenticate(token);
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);
    securityContextRepository.saveContext(context, request, response);
  }
}
