package com.poloplan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EjercicioGimnasioDtos {

  public record CrearEjercicioGimnasioRequest(
    @NotBlank @Size(max = 140) String nombre,
    @Size(max = 30) String grupoMuscular,
    @Size(max = 40) String patron,
    @Size(max = 40) String equipamiento,
    @Size(max = 30) String tipo,
    Boolean unilateral,
    @Size(max = 2000) String descripcion
  ) {}

  public record ActualizarEjercicioGimnasioRequest(
    @NotBlank @Size(max = 140) String nombre,
    @Size(max = 30) String grupoMuscular,
    @Size(max = 40) String patron,
    @Size(max = 40) String equipamiento,
    @Size(max = 30) String tipo,
    Boolean unilateral,
    @Size(max = 2000) String descripcion
  ) {}

  public record EjercicioGimnasioResponse(
    Long id,
    String nombre,
    String grupoMuscular,
    String patron,
    String equipamiento,
    String tipo,
    Boolean unilateral,
    String descripcion
  ) {}
}
