package com.poloplan.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.dto.AuthDtos.MeResponse;
import com.poloplan.dto.AuthDtos.RegisterRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.repository.AppUserRepository;

@Service
public class AuthService {

  private final AppUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
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
    return new MeResponse(user.getId(), user.getEmail(), user.getName());
  }

  public MeResponse me(AppUser user) {
    return new MeResponse(user.getId(), user.getEmail(), user.getName());
  }
}
