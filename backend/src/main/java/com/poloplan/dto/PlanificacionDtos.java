package com.poloplan.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlanificacionDtos {

  public record CrearPlanificacionRequest(
    @NotBlank
    @Size(max = 140)
    String nombre,

    LocalDate fechaInicio,
    LocalDate fechaFin,

    @Size(max = 1000)
    String notas
  ) {}

  public record ActualizarPlanificacionRequest(
    @NotBlank
    @Size(max = 140)
    String nombre,

    LocalDate fechaInicio,
    LocalDate fechaFin,

    @Size(max = 1000)
    String notas
  ) {}

  public record PlanificacionResponse(
    Long id,
    String nombre,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    String notas
  ) {}
}
