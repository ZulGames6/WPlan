package com.poloplan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
  public record RegisterRequest(
    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(min = 6, max = 72)
    String password,

    @Size(max = 120)
    String name
  ) {}

  public record LoginRequest(
    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(min = 6, max = 72)
    String password
  ) {}

  public record MeResponse(Long id, String email, String name) {}

  public record UpdateProfileRequest(
    @Size(max = 120)
    String name,
    String currentPassword,
    @Size(min = 6, max = 72)
    String newPassword
  ) {}
}
