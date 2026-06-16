package com.poloplan.drools;

/**
 * Candidato natación. Las reglas calculan puntuación y rellenan series/metros/descanso.
 */
public class NatacionCandidato {

  private Long ejercicioId;
  private String nombre;
  private String estilo;
  private String tipoBloque;
  private String material;
  private String intensidad;

  private double puntuacion;
  private boolean excluido;

  private Integer series;
  private Integer metrosPorSerie;
  private Integer descansoSeg;
  private Integer ordenBloque;
  private String tipoBloqueSugerido;
  private String motivo;

  public NatacionCandidato() {}

  public Long getEjercicioId() { return ejercicioId; }
  public void setEjercicioId(Long ejercicioId) { this.ejercicioId = ejercicioId; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getEstilo() { return estilo; }
  public void setEstilo(String estilo) { this.estilo = estilo; }
  public String getTipoBloque() { return tipoBloque; }
  public void setTipoBloque(String tipoBloque) { this.tipoBloque = tipoBloque; }
  public String getMaterial() { return material; }
  public void setMaterial(String material) { this.material = material; }
  public String getIntensidad() { return intensidad; }
  public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
  public double getPuntuacion() { return puntuacion; }
  public void setPuntuacion(double puntuacion) { this.puntuacion = puntuacion; }
  public void sumarPuntos(double p) { this.puntuacion += p; }
  public boolean isExcluido() { return excluido; }
  public void setExcluido(boolean excluido) { this.excluido = excluido; }
  public Integer getSeries() { return series; }
  public void setSeries(Integer series) { this.series = series; }
  public Integer getMetrosPorSerie() { return metrosPorSerie; }
  public void setMetrosPorSerie(Integer m) { this.metrosPorSerie = m; }
  public Integer getDescansoSeg() { return descansoSeg; }
  public void setDescansoSeg(Integer d) { this.descansoSeg = d; }
  public Integer getOrdenBloque() { return ordenBloque; }
  public void setOrdenBloque(Integer ordenBloque) { this.ordenBloque = ordenBloque; }
  public String getTipoBloqueSugerido() { return tipoBloqueSugerido; }
  public void setTipoBloqueSugerido(String t) { this.tipoBloqueSugerido = t; }
  public String getMotivo() { return motivo; }
  public void setMotivo(String motivo) { this.motivo = motivo; }

  public int metrosTotales() {
    int s = series == null ? 0 : series;
    int m = metrosPorSerie == null ? 0 : metrosPorSerie;
    return s * m;
  }

  public void anadirMotivo(String texto) {
    if (texto == null) return;
    if (motivo == null || motivo.isBlank()) {
      motivo = texto;
    } else {
      motivo = motivo + "; " + texto;
    }
  }
}
