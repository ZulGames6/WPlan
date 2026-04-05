package com.poloplan.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.AuthDtos.AuthResponse;
import com.poloplan.dto.AuthDtos.LoginRequest;
import com.poloplan.dto.AuthDtos.MeResponse;
import com.poloplan.dto.AuthDtos.RegisterRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.repository.AppUserRepository;
import com.poloplan.security.JwtService;

@Service
public class AuthService {
  private final AppUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
    AppUserRepository userRepository,
    PasswordEncoder passwordEncoder,
    JwtService jwtService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthResponse register(RegisterRequest request) {
    String email = request.email().trim().toLowerCase();

    if (userRepository.existsByEmail(email)) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Email ya registrado"
      );
    }

    AppUser user = new AppUser();
    user.setEmail(email);
    user.setName(request.name());
    user.setPasswordHash(passwordEncoder.encode(request.password()));

    userRepository.save(user);
    return new AuthResponse(jwtService.generateToken(user));
  }

  public AuthResponse login(LoginRequest request) {
    String email = request.email().trim().toLowerCase();

    AppUser user = userRepository.findByEmail(email).orElseThrow(() ->
      new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    return new AuthResponse(jwtService.generateToken(user));
  }

  public MeResponse me(AppUser user) {
    return new MeResponse(user.getId(), user.getEmail(), user.getName());
  }
}

