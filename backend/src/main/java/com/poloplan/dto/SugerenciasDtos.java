package com.poloplan.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SugerenciasDtos {

  // ── Gimnasio (sin cambios) ────────────────────────────────────────────────

  public record SugerirGimnasioRequest(
    @Size(max = 30) String fase,
    List<@Size(max = 30) String> grupos,
    @Size(max = 30) String tipo,
    @Size(max = 20) String intensidad,
    @Min(10) @Max(240) Integer minutos,
    List<@Size(max = 30) String> excluirEquipamiento,
    @Min(1) @Max(20) Integer maxEjercicios,
    @Size(max = 20) String nivel
  ) {}

  public record SugerenciaGimnasioItem(
    Long ejercicioId,
    String nombre,
    String grupoMuscular,
    String patron,
    String equipamiento,
    String tipo,
    Integer orden,
    Integer series,
    Integer repeticiones,
    Integer porcRm,
    Integer rir,
    Integer descansoSeg,
    String tempo,
    Double puntuacion,
    String motivo
  ) {}

  public record SugerenciaGimnasioResponse(List<SugerenciaGimnasioItem> sugerencias) {}

  // ── Natación ─────────────────────────────────────────────────────────────
  // Objetivos válidos: VELOCIDAD, POTENCIA_ANAEROBICA_LACTICA,
  //   CAPACIDAD_ANAEROBICA_LACTICA, UMBRAL_ANAEROBICO_LACTICO,
  //   POTENCIA_AEROBICA, CAPACIDAD_AEROBICA, UMBRAL_AEROBICO

  public record SugerirNatacionRequest(
    @NotBlank @Size(max = 40)
    String objetivo,

    @Min(500) @Max(8000)
    Integer metrosObjetivo,

    Boolean conCalentamiento,
    Boolean conVueltaCalma,

    @Size(max = 20) String nivel,
    @Size(max = 20) String estiloPreferente
  ) {}

  public record SugerenciaNatacionBloque(
    String tipo,
    String descripcion,
    String intensidadAE,
    Integer series,
    Integer metrosPorSerie,
    Integer descansoSeg,
    String material,
    Integer metrosTotales,
    Double cargaEstimada,
    String motivo
  ) {}

  public record SugerenciaNatacionResponse(
    String objetivo,
    Integer volumenTotal,
    Double cargaTotal,
    List<SugerenciaNatacionBloque> bloques
  ) {}
}
