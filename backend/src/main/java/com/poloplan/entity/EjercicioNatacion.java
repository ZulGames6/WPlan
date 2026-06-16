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
@Table(name = "ejercicio_natacion")
public class EjercicioNatacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private AppUser owner;

  @Column(nullable = false, length = 140)
  private String nombre;

  /** CROL, ESPALDA, BRAZA, MARIPOSA, ESTILOS, MIXTO, PIES, BRAZOS. */
  @Column(nullable = true, length = 30)
  private String estilo;

  /** CALENTAMIENTO, PRINCIPAL, VELOCIDAD, RESISTENCIA, VUELTA_CALMA, RECUPERACION, TECNICA. */
  @Column(nullable = true, length = 30)
  private String tipoBloque;

  /** BAJA, MEDIA, ALTA. */
  @Column(nullable = true, length = 20)
  private String intensidad;

  /** PALAS, PULL, ALETAS, TUBO, NINGUNO. */
  @Column(nullable = true, length = 60)
  private String material;

  @Column(nullable = true)
  private Integer metrosBase;

  @Column(nullable = true, length = 2000)
  private String descripcion;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public AppUser getOwner() { return owner; }
  public void setOwner(AppUser owner) { this.owner = owner; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getEstilo() { return estilo; }
  public void setEstilo(String estilo) { this.estilo = estilo; }

  public String getTipoBloque() { return tipoBloque; }
  public void setTipoBloque(String tipoBloque) { this.tipoBloque = tipoBloque; }

  public String getIntensidad() { return intensidad; }
  public void setIntensidad(String intensidad) { this.intensidad = intensidad; }

  public String getMaterial() { return material; }
  public void setMaterial(String material) { this.material = material; }

  public Integer getMetrosBase() { return metrosBase; }
  public void setMetrosBase(Integer metrosBase) { this.metrosBase = metrosBase; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
