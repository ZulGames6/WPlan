package com.poloplan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesion_natacion_bloque_item")
public class SesionNatacionBloqueItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "bloque_id", nullable = false)
  private SesionNatacionBloque bloque;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ejercicio_id", nullable = false)
  private EjercicioNatacion ejercicio;

  @Column(nullable = false)
  private Integer orden;

  /** Repeticiones (series) del ejercicio. */
  @Column(nullable = true)
  private Integer series;

  /** Metros por serie/repetición. */
  @Column(name = "metros_por_serie", nullable = true)
  private Integer metrosPorSerie;

  /** Descanso entre series en segundos. */
  @Column(name = "descanso_seg", nullable = true)
  private Integer descansoSeg;

  /** Material concreto utilizado en este ítem. */
  @Column(nullable = true, length = 200)
  private String material;

  @Column(nullable = true, length = 500)
  private String notas;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public SesionNatacionBloque getBloque() { return bloque; }
  public void setBloque(SesionNatacionBloque b) { this.bloque = b; }
  public EjercicioNatacion getEjercicio() { return ejercicio; }
  public void setEjercicio(EjercicioNatacion e) { this.ejercicio = e; }
  public Integer getOrden() { return orden; }
  public void setOrden(Integer orden) { this.orden = orden; }
  public Integer getSeries() { return series; }
  public void setSeries(Integer series) { this.series = series; }
  public Integer getMetrosPorSerie() { return metrosPorSerie; }
  public void setMetrosPorSerie(Integer metros) { this.metrosPorSerie = metros; }
  public Integer getDescansoSeg() { return descansoSeg; }
  public void setDescansoSeg(Integer d) { this.descansoSeg = d; }
  public String getMaterial() { return material; }
  public void setMaterial(String material) { this.material = material; }
  public String getNotas() { return notas; }
  public void setNotas(String notas) { this.notas = notas; }

  public int metrosTotales() {
    int s = series == null ? 0 : series;
    int m = metrosPorSerie == null ? 0 : metrosPorSerie;
    return s * m;
  }
}
