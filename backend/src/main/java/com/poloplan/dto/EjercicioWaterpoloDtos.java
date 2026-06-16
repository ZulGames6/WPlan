package com.poloplan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EjercicioWaterpoloDtos {

  public record CrearEjercicioWaterpoloRequest(
    @NotBlank @Size(max = 140) String nombre,
    @Size(max = 200) String objetivo,
    @Size(max = 30) String categoria,
    @Size(max = 30) String intensidad,
    @Size(max = 200) String material,
    @Min(1) Integer duracionMinSugerida,
    @Min(1) Integer jugadoresMin,
    @Min(1) Integer jugadoresMax,
    @Size(max = 2000) String descripcion
  ) {}

  public record ActualizarEjercicioWaterpoloRequest(
    @NotBlank @Size(max = 140) String nombre,
    @Size(max = 200) String objetivo,
    @Size(max = 30) String categoria,
    @Size(max = 30) String intensidad,
    @Size(max = 200) String material,
    @Min(1) Integer duracionMinSugerida,
    @Min(1) Integer jugadoresMin,
    @Min(1) Integer jugadoresMax,
    @Size(max = 2000) String descripcion
  ) {}

  public record EjercicioWaterpoloResponse(
    Long id,
    String nombre,
    String objetivo,
    String categoria,
    String intensidad,
    String material,
    Integer duracionMinSugerida,
    Integer jugadoresMin,
    Integer jugadoresMax,
    String descripcion
  ) {}
}
