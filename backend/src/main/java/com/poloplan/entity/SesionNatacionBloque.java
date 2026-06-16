package com.poloplan.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesion_natacion_bloque")
public class SesionNatacionBloque {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sesion_natacion_id", nullable = false)
  private SesionNatacion sesionNatacion;

  @Column(nullable = false)
  private Integer orden;

  @Column(nullable = true, length = 60)
  private String nombre;

  @Column(name = "tipo_bloque", nullable = true, length = 30)
  private String tipoBloque;

  @Column(nullable = true, length = 1000)
  private String descripcion;

  @Column(nullable = true)
  private Integer series;

  @Column(name = "metros_por_serie", nullable = true)
  private Integer metrosPorSerie;

  @Column(name = "descanso_seg", nullable = true)
  private Integer descansoSeg;

  @Column(name = "intensidad_ae", nullable = true, length = 5)
  private String intensidadAE;

  @Column(nullable = true, length = 100)
  private String material;

  @Column(nullable = true, length = 1000)
  private String notas;

  @OneToMany(mappedBy = "bloque", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("orden ASC, id ASC")
  private List<SesionNatacionBloqueItem> items = new ArrayList<>();

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

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public SesionNatacion getSesionNatacion() { return sesionNatacion; }
  public void setSesionNatacion(SesionNatacion s) { this.sesionNatacion = s; }
  public Integer getOrden() { return orden; }
  public void setOrden(Integer orden) { this.orden = orden; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public String getTipoBloque() { return tipoBloque; }
  public void setTipoBloque(String tipoBloque) { this.tipoBloque = tipoBloque; }
  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
  public Integer getSeries() { return series; }
  public void setSeries(Integer series) { this.series = series; }
  public Integer getMetrosPorSerie() { return metrosPorSerie; }
  public void setMetrosPorSerie(Integer metros) { this.metrosPorSerie = metros; }
  public Integer getDescansoSeg() { return descansoSeg; }
  public void setDescansoSeg(Integer d) { this.descansoSeg = d; }
  public String getIntensidadAE() { return intensidadAE; }
  public void setIntensidadAE(String ae) { this.intensidadAE = ae; }
  public String getMaterial() { return material; }
  public void setMaterial(String material) { this.material = material; }
  public String getNotas() { return notas; }
  public void setNotas(String notas) { this.notas = notas; }
  public List<SesionNatacionBloqueItem> getItems() { return items; }
  public void setItems(List<SesionNatacionBloqueItem> items) { this.items = items; }
}
