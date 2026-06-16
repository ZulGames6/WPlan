package com.poloplan.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
        .requestMatchers(new AntPathRequestMatcher("/api/planificaciones", HttpMethod.GET.name())).authenticated()
        .requestMatchers(new AntPathRequestMatcher("/api/planificaciones/**", HttpMethod.GET.name())).authenticated()
        .requestMatchers(new AntPathRequestMatcher("/api/planificaciones", HttpMethod.POST.name())).authenticated()
        .requestMatchers(new AntPathRequestMatcher("/api/planificaciones/**", HttpMethod.POST.name())).authenticated()
        .requestMatchers(new AntPathRequestMatcher("/api/planificaciones/**", HttpMethod.PUT.name())).authenticated()
        .requestMatchers(new AntPathRequestMatcher("/api/planificaciones/**", HttpMethod.DELETE.name())).authenticated()
        .requestMatchers(new AntPathRequestMatcher("/api/ejercicios/**")).authenticated()
        .requestMatchers(new AntPathRequestMatcher("/api/sugerencias/**")).authenticated()
        .anyRequest().authenticated()
      );

    return http.build();
  }
}
