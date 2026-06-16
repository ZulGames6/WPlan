package com.poloplan.drools;

/**
 * Hecho de entrada para el motor de reglas de natación.
 * El objetivo define la zona de entrenamiento de la sesión.
 * Valores: VELOCIDAD, POTENCIA_ANAEROBICA_LACTICA, CAPACIDAD_ANAEROBICA_LACTICA,
 *          UMBRAL_ANAEROBICO_LACTICO, POTENCIA_AEROBICA, CAPACIDAD_AEROBICA, UMBRAL_AEROBICO
 */
public class NatacionObjetivo {

  private String objetivo;
  private Integer volumenMetros;
  private boolean conCalentamiento = true;
  private boolean conVueltaCalma = true;

  /**
   * Variante estructural del bloque principal (0, 1 o 2). El servicio la elige
   * aleatoriamente en cada llamada para que dos sesiones consecutivas con el mismo
   * objetivo NO sean idénticas:
   *   0 — serie clásica (todas las series iguales)
   *   1 — pirámide o progresión interna
   *   2 — alternancia de ritmos (fast/slow)
   */
  private int variante = 0;

  /**
   * Nivel del nadador: INICIACION, INTERMEDIO, AVANZADO. Modula volumen, número
   * de series y descanso (el principiante recibe menos volumen y más descanso).
   */
  private String nivel = "INTERMEDIO";

  /**
   * Estilo preferente: LIBRE, ESPALDA, BRAZA, MARIPOSA, COMBINADO. Modula la
   * descripción del bloque técnico y de piernas.
   */
  private String estiloPreferente;

  /**
   * Pseudoaleatoriedad por sesión (0..99) para variar metros base en bloques
   * secundarios sin caer en valores siempre idénticos.
   */
  private int rand = 0;

  public NatacionObjetivo() {}

  public String getObjetivo() { return objetivo; }
  public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
  public Integer getVolumenMetros() { return volumenMetros; }
  public void setVolumenMetros(Integer v) { this.volumenMetros = v; }
  public boolean isConCalentamiento() { return conCalentamiento; }
  public void setConCalentamiento(boolean v) { this.conCalentamiento = v; }
  public boolean isConVueltaCalma() { return conVueltaCalma; }
  public void setConVueltaCalma(boolean v) { this.conVueltaCalma = v; }

  public int getVariante() { return variante; }
  public void setVariante(int variante) { this.variante = variante; }

  public String getNivel() { return nivel; }
  public void setNivel(String nivel) { this.nivel = (nivel == null || nivel.isBlank()) ? "INTERMEDIO" : nivel; }

  public String getEstiloPreferente() { return estiloPreferente; }
  public void setEstiloPreferente(String e) { this.estiloPreferente = e; }

  public int getRand() { return rand; }
  public void setRand(int rand) { this.rand = rand; }
}
