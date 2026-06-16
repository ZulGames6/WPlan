package com.poloplan.dto;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HorarioSemanalDtos {

  public record HorarioDiaResponse(
    int diaSemana,
    String nombreDia,
    boolean activo,
    LocalTime horaInicio,
    LocalTime horaFin,
    String lugar,
    boolean conGimnasio,
    boolean conNatacion,
    boolean conWaterpolo
  ) {}

  public record SemanaResponse(List<HorarioDiaResponse> dias) {}

  public record HorarioDiaRequest(
    @NotNull @Min(1) @Max(7)
    Integer diaSemana,
    boolean activo,
    LocalTime horaInicio,
    LocalTime horaFin,
    @Size(max = 160)
    String lugar,
    boolean conGimnasio,
    boolean conNatacion,
    boolean conWaterpolo
  ) {}

  public record GuardarSemanaRequest(
    @Valid
    @NotNull
    List<HorarioDiaRequest> dias
  ) {}
}
