package com.poloplan.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SesionNatacionDtos {

  public record CrearBloqueRequest(
    @Min(1) @Max(50) Integer orden,
    @Size(max = 60) String nombre,
    @Size(max = 30) String tipoBloque,
    @Size(max = 1000) String descripcion,
    @Min(1) @Max(100) Integer series,
    @Min(10) @Max(10000) Integer metrosPorSerie,
    @Min(0) @Max(600) Integer descansoSeg,
    @Size(max = 5) String intensidadAE,
    @Size(max = 100) String material,
    @Size(max = 1000) String notas
  ) {}

  public record ActualizarBloqueRequest(
    @Min(1) @Max(50) Integer orden,
    @Size(max = 60) String nombre,
    @Size(max = 30) String tipoBloque,
    @Size(max = 1000) String descripcion,
    @Min(1) @Max(100) Integer series,
    @Min(10) @Max(10000) Integer metrosPorSerie,
    @Min(0) @Max(600) Integer descansoSeg,
    @Size(max = 5) String intensidadAE,
    @Size(max = 100) String material,
    @Size(max = 1000) String notas
  ) {}

  public record AnadirItemRequest(
    @NotNull Long ejercicioId,
    @Min(1) @Max(50) Integer orden,
    @Min(1) @Max(50) Integer series,
    @Min(25) @Max(5000) Integer metrosPorSerie,
    @Min(0) @Max(600) Integer descansoSeg,
    @Size(max = 200) String material,
    @Size(max = 500) String notas
  ) {}

  public record ActualizarItemRequest(
    @Min(1) @Max(50) Integer orden,
    @Min(1) @Max(50) Integer series,
    @Min(25) @Max(5000) Integer metrosPorSerie,
    @Min(0) @Max(600) Integer descansoSeg,
    @Size(max = 200) String material,
    @Size(max = 500) String notas
  ) {}

  public record ItemResponse(
    Long id,
    Long ejercicioId,
    String ejercicioNombre,
    String estilo,
    Integer orden,
    Integer series,
    Integer metrosPorSerie,
    Integer descansoSeg,
    String material,
    String notas,
    Integer metrosTotales
  ) {}

  public record BloqueResponse(
    Long id,
    Integer orden,
    String nombre,
    String tipoBloque,
    String descripcion,
    Integer series,
    Integer metrosPorSerie,
    Integer descansoSeg,
    String intensidadAE,
    String material,
    String notas,
    List<ItemResponse> items,
    Integer metrosTotales,
    Double cargaEstimada
  ) {}
}
