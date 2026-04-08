package com.poloplan.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Void> handleBadCredentials() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}
