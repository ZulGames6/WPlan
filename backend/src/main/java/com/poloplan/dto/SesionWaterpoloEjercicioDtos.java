package com.poloplan.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SesionWaterpoloEjercicioDtos {

  public record AnadirEjercicioRequest(
    @NotNull
    Long ejercicioId,

    @Min(1) @Max(50)
    Integer orden,

    @Min(1) @Max(240)
    Integer duracionMin,

    @Size(max = 500)
    String notas
  ) {}

  public record ActualizarEjercicioRequest(
    @Min(1) @Max(50)
    Integer orden,

    @Min(1) @Max(240)
    Integer duracionMin,

    @Size(max = 500)
    String notas
  ) {}

  public record SesionWaterpoloEjercicioResponse(
    Long id,
    Long ejercicioId,
    String ejercicioNombre,
    String objetivo,
    String categoria,
    String intensidad,
    String material,
    Integer orden,
    Integer duracionMin,
    String notas
  ) {}
}
