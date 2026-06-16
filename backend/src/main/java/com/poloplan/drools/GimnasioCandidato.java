package com.poloplan.drools;

/**
 * Hecho que representa un ejercicio del catálogo como candidato a ser sugerido.
 * Las reglas pueden leerlo, calcular su puntuación y rellenar series/reps/RIR/descanso.
 */
public class GimnasioCandidato {

  private Long ejercicioId;
  private String nombre;
  private String grupoMuscular;
  private String patron;
  private String equipamiento;
  private String tipo;
  private Boolean unilateral;

  /** Puntuación calculada por las reglas. Mayor = más recomendado. */
  private double puntuacion;

  /** Si la regla lo descarta, queda excluido del resultado. */
  private boolean excluido;

  /**
   * Posición que el servicio asigna al candidato dentro de la sesión (1..N) tras
   * ordenar por puntuación. Las reglas la usan para modular la prescripción
   * (los primeros ejercicios son los más pesados y con mayor descanso).
   * Sigue siendo null cuando se entra a Drools la primera vez.
   */
  private Integer orden;

  /**
   * Pseudoaleatoriedad por candidato para permitir que las reglas introduzcan
   * variabilidad reproducible (un mismo seed → misma sesión) sin caer en valores
   * idénticos para todos los ejercicios.
   */
  private int rand;

  /** Parámetros sugeridos por las reglas. */
  private Integer series;
  private Integer repeticiones;
  private Integer porcRm;
  /** Repeticiones en reserva (RIR). 0 = al fallo; 3 = tres reps antes del fallo. */
  private Integer rir;
  private Integer descansoSeg;
  /**
   * Tempo en formato "EXC-PAUSA-CONC" (por ejemplo "3-1-1"). Se asigna en función
   * del tipo de ejercicio: explosivo en potencia, controlado en hipertrofia, etc.
   */
  private String tempo;
  private String motivo;

  public GimnasioCandidato() {}

  public Long getEjercicioId() { return ejercicioId; }
  public void setEjercicioId(Long ejercicioId) { this.ejercicioId = ejercicioId; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getGrupoMuscular() { return grupoMuscular; }
  public void setGrupoMuscular(String g) { this.grupoMuscular = g; }
  public String getPatron() { return patron; }
  public void setPatron(String patron) { this.patron = patron; }
  public String getEquipamiento() { return equipamiento; }
  public void setEquipamiento(String e) { this.equipamiento = e; }
  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }
  public Boolean getUnilateral() { return unilateral; }
  public void setUnilateral(Boolean unilateral) { this.unilateral = unilateral; }
  public double getPuntuacion() { return puntuacion; }
  public void setPuntuacion(double puntuacion) { this.puntuacion = puntuacion; }
  public void sumarPuntos(double p) { this.puntuacion += p; }
  public boolean isExcluido() { return excluido; }
  public void setExcluido(boolean excluido) { this.excluido = excluido; }
  public Integer getOrden() { return orden; }
  public void setOrden(Integer orden) { this.orden = orden; }
  public int getRand() { return rand; }
  public void setRand(int rand) { this.rand = rand; }
  public Integer getSeries() { return series; }
  public void setSeries(Integer series) { this.series = series; }
  public Integer getRepeticiones() { return repeticiones; }
  public void setRepeticiones(Integer r) { this.repeticiones = r; }
  public Integer getPorcRm() { return porcRm; }
  public void setPorcRm(Integer porcRm) { this.porcRm = porcRm; }
  public Integer getRir() { return rir; }
  public void setRir(Integer rir) { this.rir = rir; }
  public Integer getDescansoSeg() { return descansoSeg; }
  public void setDescansoSeg(Integer d) { this.descansoSeg = d; }
  public String getTempo() { return tempo; }
  public void setTempo(String tempo) { this.tempo = tempo; }
  public String getMotivo() { return motivo; }
  public void setMotivo(String motivo) { this.motivo = motivo; }

  public void anadirMotivo(String texto) {
    if (texto == null) return;
    if (motivo == null || motivo.isBlank()) {
      motivo = texto;
    } else {
      motivo = motivo + "; " + texto;
    }
  }
}
