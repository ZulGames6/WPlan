package com.poloplan.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SesionGimnasioEjercicioDtos {

  public record AnadirRequest(
    @NotNull
    Long ejercicioId,

    @Min(1) @Max(50)
    Integer orden,

    @Min(1) @Max(30)
    Integer series,

    @Min(1) @Max(100)
    Integer repeticiones,

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "500.0", inclusive = true)
    BigDecimal pesoKg,

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    BigDecimal porcRm,

    @Min(0) @Max(10)
    Integer rir,

    @Min(0) @Max(600)
    Integer descansoSeg,

    @Size(max = 20)
    String tempo,

    @Size(max = 500)
    String notas
  ) {}

  public record ActualizarRequest(
    @Min(1) @Max(50)
    Integer orden,

    @Min(1) @Max(30)
    Integer series,

    @Min(1) @Max(100)
    Integer repeticiones,

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "500.0", inclusive = true)
    BigDecimal pesoKg,

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "100.0", inclusive = true)
    BigDecimal porcRm,

    @Min(0) @Max(10)
    Integer rir,

    @Min(0) @Max(600)
    Integer descansoSeg,

    @Size(max = 20)
    String tempo,

    @Size(max = 500)
    String notas
  ) {}

  public record SesionGimnasioEjercicioResponse(
    Long id,
    Long ejercicioId,
    String ejercicioNombre,
    String grupoMuscular,
    String patron,
    String equipamiento,
    Integer orden,
    Integer series,
    Integer repeticiones,
    BigDecimal pesoKg,
    BigDecimal porcRm,
    Integer rir,
    Integer descansoSeg,
    String tempo,
    String notas
  ) {}
}
