package com.poloplan.drools;

/**
 * Bloque de entrenamiento generado por el motor de reglas de natación.
 * Representa un bloque completo (calentamiento, técnica, piernas, material, principal, vuelta).
 * Las reglas activan y configuran cada bloque según el objetivo de la sesión.
 */
public class NatacionBloqueGenerado {

  private String tipo;
  private boolean activo;
  private Integer orden;
  private String descripcion;
  private Integer series;
  private Integer metrosPorSerie;
  private Integer descansoSeg;
  private String intensidadAE;
  private String material;
  private String motivo;

  public NatacionBloqueGenerado(String tipo) {
    this.tipo = tipo;
    this.activo = false;
  }

  public int metrosTotales() {
    int s = series == null ? 0 : series;
    int m = metrosPorSerie == null ? 0 : metrosPorSerie;
    return s * m;
  }

  public double cargaEstimada() {
    return metrosTotales() * aeToFactor(intensidadAE);
  }

  private static double aeToFactor(String ae) {
    if (ae == null) return 1.0;
    return switch (ae.toUpperCase()) {
      case "AE1" -> 1.0;
      case "AE2" -> 2.0;
      case "AE3" -> 3.0;
      case "AE4" -> 5.0;
      case "AE5" -> 8.0;
      default -> 1.0;
    };
  }

  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }
  public boolean isActivo() { return activo; }
  public void setActivo(boolean activo) { this.activo = activo; }
  public Integer getOrden() { return orden; }
  public void setOrden(Integer orden) { this.orden = orden; }
  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
  public Integer getSeries() { return series; }
  public void setSeries(Integer series) { this.series = series; }
  public Integer getMetrosPorSerie() { return metrosPorSerie; }
  public void setMetrosPorSerie(Integer m) { this.metrosPorSerie = m; }
  public Integer getDescansoSeg() { return descansoSeg; }
  public void setDescansoSeg(Integer d) { this.descansoSeg = d; }
  public String getIntensidadAE() { return intensidadAE; }
  public void setIntensidadAE(String ae) { this.intensidadAE = ae; }
  public String getMaterial() { return material; }
  public void setMaterial(String material) { this.material = material; }
  public String getMotivo() { return motivo; }
  public void setMotivo(String motivo) { this.motivo = motivo; }
}
