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
@Table(name = "sesion_waterpolo_ejercicio")
public class SesionWaterpoloEjercicio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sesion_waterpolo_id", nullable = false)
  private SesionWaterpolo sesionWaterpolo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ejercicio_id", nullable = false)
  private EjercicioWaterpolo ejercicio;

  /** Posición dentro de la sesión (1, 2, 3…). */
  @Column(nullable = false)
  private Integer orden;

  /** Duración concreta para esta sesión, puede sobreescribir la del catálogo. */
  @Column(name = "duracion_min", nullable = true)
  private Integer duracionMin;

  @Column(nullable = true, length = 500)
  private String notas;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public SesionWaterpolo getSesionWaterpolo() { return sesionWaterpolo; }
  public void setSesionWaterpolo(SesionWaterpolo s) { this.sesionWaterpolo = s; }
  public EjercicioWaterpolo getEjercicio() { return ejercicio; }
  public void setEjercicio(EjercicioWaterpolo e) { this.ejercicio = e; }
  public Integer getOrden() { return orden; }
  public void setOrden(Integer orden) { this.orden = orden; }
  public Integer getDuracionMin() { return duracionMin; }
  public void setDuracionMin(Integer duracionMin) { this.duracionMin = duracionMin; }
  public String getNotas() { return notas; }
  public void setNotas(String notas) { this.notas = notas; }
}
