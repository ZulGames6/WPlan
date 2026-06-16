package com.poloplan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AsistenciaDtos {

  public record MarcarAsistenciaRequest(
    @NotBlank
    @Size(max = 30)
    String estado,

    @Size(max = 300)
    String nota
  ) {}

  public record AsistenciaResponse(
    Long id,
    Long jugadorId,
    String estado,
    String nota
  ) {}
}

