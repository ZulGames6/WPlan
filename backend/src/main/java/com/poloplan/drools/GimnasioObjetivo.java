package com.poloplan.drools;

import java.util.ArrayList;
import java.util.List;

/**
 * Hecho de entrada para el motor de reglas de gimnasio.
 * Describe el objetivo del entrenamiento que el entrenador quiere planificar.
 */
public class GimnasioObjetivo {

  /** PRINCIPAL, AUXILIAR, ACTIVACION, RECUPERACION. */
  private String fase;

  /** Lista de grupos musculares prioritarios (PECTORAL, ESPALDA, ...). */
  private List<String> grupos = new ArrayList<>();

  /** Tipo dominante: FUERZA, HIPERTROFIA, POTENCIA, RESISTENCIA, MOVILIDAD, ACTIVACION. */
  private String tipo;

  /** Intensidad subjetiva del jugador: BAJA, MEDIA, ALTA. */
  private String intensidad;

  /** Minutos disponibles para esta sesión. */
  private Integer minutos;

  /** Si excluir ejercicios con equipamiento específico (por ejemplo MAQUINA si no hay sala). */
  private List<String> excluirEquipamiento = new ArrayList<>();

  /**
   * Variante de periodización (DUP): 0=Volumen, 1=Estándar, 2=Intensidad.
   * El servicio lo elige aleatoriamente para que los parámetros roten sesión a sesión.
   */
  private int variante = 0;

  /**
   * Nivel del deportista. Modula la prescripción: el principiante recibe menos
   * series y un RIR más conservador; el avanzado puede tolerar más volumen
   * y trabajar con RIR más cercano al fallo. Valores: INICIACION, INTERMEDIO, AVANZADO.
   */
  private String nivel = "INTERMEDIO";

  public GimnasioObjetivo() {}

  public String getFase() { return fase; }
  public void setFase(String fase) { this.fase = fase; }
  public List<String> getGrupos() { return grupos; }
  public void setGrupos(List<String> grupos) { this.grupos = grupos == null ? new ArrayList<>() : grupos; }
  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }
  public String getIntensidad() { return intensidad; }
  public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
  public Integer getMinutos() { return minutos; }
  public void setMinutos(Integer minutos) { this.minutos = minutos; }
  public List<String> getExcluirEquipamiento() { return excluirEquipamiento; }
  public void setExcluirEquipamiento(List<String> v) { this.excluirEquipamiento = v == null ? new ArrayList<>() : v; }

  public int getVariante() { return variante; }
  public void setVariante(int variante) { this.variante = variante; }

  public String getNivel() { return nivel; }
  public void setNivel(String nivel) { this.nivel = (nivel == null || nivel.isBlank()) ? "INTERMEDIO" : nivel; }

  public boolean tieneGrupo(String g) {
    return g != null && grupos.contains(g);
  }

  public boolean equipamientoExcluido(String e) {
    return e != null && excluirEquipamiento.contains(e);
  }
}
