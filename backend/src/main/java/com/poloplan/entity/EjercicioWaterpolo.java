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
@Table(name = "ejercicio_waterpolo")
public class EjercicioWaterpolo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private AppUser owner;

  @Column(nullable = false, length = 140)
  private String nombre;

  /** Objetivo libre del ejercicio: superioridad, defensa, transición, tiro, etc. */
  @Column(nullable = true, length = 200)
  private String objetivo;

  /** Categoría: TACTICA, TECNICA, FISICA, JUEGO, OTRO. */
  @Column(nullable = true, length = 30)
  private String categoria;

  /** BAJA, MEDIA, ALTA. */
  @Column(nullable = true, length = 30)
  private String intensidad;

  @Column(nullable = true, length = 200)
  private String material;

  @Column(nullable = true)
  private Integer duracionMinSugerida;

  @Column(nullable = true)
  private Integer jugadoresMin;

  @Column(nullable = true)
  private Integer jugadoresMax;

  @Column(nullable = true, length = 2000)
  private String descripcion;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public AppUser getOwner() { return owner; }
  public void setOwner(AppUser owner) { this.owner = owner; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getObjetivo() { return objetivo; }
  public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

  public String getCategoria() { return categoria; }
  public void setCategoria(String categoria) { this.categoria = categoria; }

  public String getIntensidad() { return intensidad; }
  public void setIntensidad(String intensidad) { this.intensidad = intensidad; }

  public String getMaterial() { return material; }
  public void setMaterial(String material) { this.material = material; }

  public Integer getDuracionMinSugerida() { return duracionMinSugerida; }
  public void setDuracionMinSugerida(Integer duracionMinSugerida) { this.duracionMinSugerida = duracionMinSugerida; }

  public Integer getJugadoresMin() { return jugadoresMin; }
  public void setJugadoresMin(Integer jugadoresMin) { this.jugadoresMin = jugadoresMin; }

  public Integer getJugadoresMax() { return jugadoresMax; }
  public void setJugadoresMax(Integer jugadoresMax) { this.jugadoresMax = jugadoresMax; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
