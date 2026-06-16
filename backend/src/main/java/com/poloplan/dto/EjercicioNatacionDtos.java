package com.poloplan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EjercicioNatacionDtos {

  public record CrearEjercicioNatacionRequest(
    @NotBlank @Size(max = 140) String nombre,
    @Size(max = 30) String estilo,
    @Size(max = 30) String tipoBloque,
    @Size(max = 20) String intensidad,
    @Size(max = 60) String material,
    @Min(1) Integer metrosBase,
    @Size(max = 2000) String descripcion
  ) {}

  public record ActualizarEjercicioNatacionRequest(
    @NotBlank @Size(max = 140) String nombre,
    @Size(max = 30) String estilo,
    @Size(max = 30) String tipoBloque,
    @Size(max = 20) String intensidad,
    @Size(max = 60) String material,
    @Min(1) Integer metrosBase,
    @Size(max = 2000) String descripcion
  ) {}

  public record EjercicioNatacionResponse(
    Long id,
    String nombre,
    String estilo,
    String tipoBloque,
    String intensidad,
    String material,
    Integer metrosBase,
    String descripcion
  ) {}
}
