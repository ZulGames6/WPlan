package com.poloplan.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.poloplan.entity.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
  private final SecretKey signingKey;
  private final Duration expiration;

  public JwtService(
    @Value("${jwt.secret}") String jwtSecret,
    @Value("${jwt.expiration-minutes}") long expirationMinutes) {

    // HS256 requiere una clave suficientemente larga; derivamos una clave de 256 bits.
    byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    byte[] digest;
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      digest = sha256.digest(secretBytes);
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo derivar clave JWT", e);
    }

    this.signingKey = Keys.hmacShaKeyFor(digest);
    this.expiration = Duration.ofMinutes(expirationMinutes);
  }

  public String generateToken(AppUser user) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expiration.toMillis());

    return Jwts.builder()
      .setSubject(user.getEmail())
      .setIssuedAt(now)
      .setExpiration(exp)
      .claim("userId", user.getId())
      .signWith(signingKey)
      .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = parseClaims(token);
      return claims.getExpiration() != null && claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public Optional<String> extractEmail(String token) {
    try {
      Claims claims = parseClaims(token);
      return Optional.ofNullable(claims.getSubject());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
      .verifyWith(signingKey)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
}

