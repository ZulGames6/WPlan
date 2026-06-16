package com.poloplan.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SesionDtos {

  public record CrearSesionDiaRequest(
    @NotNull
    LocalDate fecha,

    LocalTime horaInicio,
    LocalTime horaFin,

    @Size(max = 160)
    String lugar,

    @Size(max = 40)
    String tipo,

    @Size(max = 40)
    String estado,

    @Size(max = 300)
    String objetivo,

    @Size(max = 1000)
    String notas,

    @Valid
    CrearSesionParteRequest gimnasio,

    @Valid
    CrearSesionParteRequest natacion,

    @Valid
    CrearSesionParteRequest waterpolo
  ) {}

  public record ActualizarSesionDiaRequest(
    @NotNull
    LocalDate fecha,

    LocalTime horaInicio,
    LocalTime horaFin,

    @Size(max = 160)
    String lugar,

    @Size(max = 40)
    String tipo,

    @Size(max = 40)
    String estado,

    @Size(max = 300)
    String objetivo,

    @Size(max = 1000)
    String notas,

    @Valid
    CrearSesionParteRequest gimnasio,

    @Valid
    CrearSesionParteRequest natacion,

    @Valid
    CrearSesionParteRequest waterpolo
  ) {}

  public record CrearSesionParteRequest(
    @Size(max = 300)
    String objetivo,

    @Size(max = 1000)
    String notas
  ) {}

  public record SesionParteResponse(String objetivo, String notas) {}

  public record SesionDiaResponse(
    Long id,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    String lugar,
    String tipo,
    String estado,
    String objetivo,
    String notas,
    SesionParteResponse gimnasio,
    SesionParteResponse natacion,
    SesionParteResponse waterpolo
  ) {}

  /**
   * Resumen de la operación de aplicar horario a un rango: cuántas sesiones
   * se crearon nuevas, cuántas fechas se omitieron por tener ya sesión y
   * cuántos días del rango no tenían horario activo.
   */
  public record AplicarHorarioResponse(
    int creadas,
    int omitidas,
    int sinHorario
  ) {}
}

