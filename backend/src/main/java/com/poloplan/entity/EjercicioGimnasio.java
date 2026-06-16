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
@Table(name = "ejercicio_gimnasio")
public class EjercicioGimnasio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private AppUser owner;

  @Column(nullable = false, length = 140)
  private String nombre;

  /** PIERNAS, PECHO, ESPALDA, HOMBROS, BRAZOS, CORE, FUNCIONAL, GLUTEOS. */
  @Column(nullable = true, length = 30)
  private String grupoMuscular;

  /**
   * Patrón motor: SENTADILLA, BISAGRA, EMPUJE_HORIZONTAL, EMPUJE_VERTICAL,
   * TRACCION_HORIZONTAL, TRACCION_VERTICAL, CORE, ESTABILIDAD, GENERAL.
   */
  @Column(nullable = true, length = 40)
  private String patron;

  /** BARRA, MANCUERNAS, MAQUINA, POLEA, GOMAS, PESO_CORPORAL, KETTLEBELL. */
  @Column(nullable = true, length = 40)
  private String equipamiento;

  /** FUERZA, HIPERTROFIA, POTENCIA, RESISTENCIA, MOVILIDAD, CORE. */
  @Column(nullable = true, length = 30)
  private String tipo;

  /** ¿Trabaja un lado del cuerpo cada vez? */
  @Column(nullable = true)
  private Boolean unilateral;

  @Column(nullable = true, length = 2000)
  private String descripcion;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public AppUser getOwner() { return owner; }
  public void setOwner(AppUser owner) { this.owner = owner; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getGrupoMuscular() { return grupoMuscular; }
  public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

  public String getPatron() { return patron; }
  public void setPatron(String patron) { this.patron = patron; }

  public String getEquipamiento() { return equipamiento; }
  public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }

  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }

  public Boolean getUnilateral() { return unilateral; }
  public void setUnilateral(Boolean unilateral) { this.unilateral = unilateral; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
