package com.poloplan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JugadorDtos {

  public record CrearJugadorRequest(
    @NotBlank
    @Size(max = 120)
    String nombre,

    @Size(max = 200)
    String apellidos,

    LocalDate fechaNacimiento,

    @Size(max = 60)
    String posicion,

    @Size(max = 1000)
    String notas,

    @DecimalMin(value = "20.0", inclusive = true)
    @DecimalMax(value = "200.0", inclusive = true)
    BigDecimal pesoKg,

    @Size(max = 20)
    String dni,

    @Size(max = 20)
    String tallaBanador,

    @Size(max = 10)
    String tallaCamiseta
  ) {}

  public record ActualizarJugadorRequest(
    @NotBlank
    @Size(max = 120)
    String nombre,

    @Size(max = 200)
    String apellidos,

    LocalDate fechaNacimiento,

    @Size(max = 60)
    String posicion,

    @Size(max = 1000)
    String notas,

    @DecimalMin(value = "20.0", inclusive = true)
    @DecimalMax(value = "200.0", inclusive = true)
    BigDecimal pesoKg,

    @Size(max = 20)
    String dni,

    @Size(max = 20)
    String tallaBanador,

    @Size(max = 10)
    String tallaCamiseta
  ) {}

  public record JugadorResponse(
    Long id,
    String nombre,
    String apellidos,
    LocalDate fechaNacimiento,
    String posicion,
    String notas,
    BigDecimal pesoKg,
    String dni,
    String tallaBanador,
    String tallaCamiseta
  ) {}
}
