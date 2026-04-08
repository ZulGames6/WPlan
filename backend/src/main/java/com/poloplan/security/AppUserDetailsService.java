package com.poloplan.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poloplan.repository.AppUserRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

  private final AppUserRepository userRepository;

  public AppUserDetailsService(AppUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String email = username.trim().toLowerCase();
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
  }
}
