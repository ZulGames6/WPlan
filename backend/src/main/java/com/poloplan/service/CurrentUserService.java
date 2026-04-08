package com.poloplan.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.entity.AppUser;

@Service
public class CurrentUserService {
  public AppUser requireUser(Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof AppUser user)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }
    return user;
  }
}

