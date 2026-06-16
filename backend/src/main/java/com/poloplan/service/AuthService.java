package com.poloplan.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.AuthDtos.MeResponse;
import com.poloplan.dto.AuthDtos.RegisterRequest;
import com.poloplan.dto.AuthDtos.UpdateProfileRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.repository.AppUserRepository;

@Service
public class AuthService {

  private final AppUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CatalogoSeedService catalogoSeedService;

  public AuthService(
    AppUserRepository userRepository,
    PasswordEncoder passwordEncoder,
    CatalogoSeedService catalogoSeedService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.catalogoSeedService = catalogoSeedService;
  }

  public MeResponse register(RegisterRequest request) {
    String email = request.email().trim().toLowerCase();

    if (userRepository.existsByEmail(email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
    }

    AppUser user = new AppUser();
    user.setEmail(email);
    user.setName(request.name());
    user.setPasswordHash(passwordEncoder.encode(request.password()));

    userRepository.save(user);
    catalogoSeedService.seedSiVacio(user);
    return new MeResponse(user.getId(), user.getEmail(), user.getName());
  }

  public MeResponse me(AppUser user) {
    return new MeResponse(user.getId(), user.getEmail(), user.getName());
  }

  public MeResponse updateProfile(AppUser user, UpdateProfileRequest req) {
    if (req.name() != null && !req.name().isBlank()) {
      user.setName(req.name().trim());
    }
    if (req.newPassword() != null && !req.newPassword().isBlank()) {
      if (req.currentPassword() == null || !passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contraseña actual incorrecta");
      }
      user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    }
    userRepository.save(user);
    // Reload to get fresh data
    AppUser saved = userRepository.findByEmail(user.getEmail())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return new MeResponse(saved.getId(), saved.getEmail(), saved.getName());
  }
}
