package com.poloplan.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
  name = "horario_semana",
  uniqueConstraints = {@UniqueConstraint(columnNames = {"planificacion_id", "dia_semana"})}
)
public class HorarioSemanal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "planificacion_id", nullable = false)
  private Planificacion planificacion;

  /** 1 = lunes … 7 = domingo (ISO). */
  @Column(name = "dia_semana", nullable = false)
  private int diaSemana;

  @Column(nullable = false)
  private boolean activo;

  @Column(nullable = true)
  private LocalTime horaInicio;

  @Column(nullable = true)
  private LocalTime horaFin;

  @Column(nullable = true, length = 160)
  private String lugar;

  @Column(nullable = false)
  private boolean conGimnasio;

  @Column(nullable = false)
  private boolean conNatacion;

  @Column(nullable = false)
  private boolean conWaterpolo;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Planificacion getPlanificacion() { return planificacion; }
  public void setPlanificacion(Planificacion planificacion) { this.planificacion = planificacion; }

  public int getDiaSemana() { return diaSemana; }
  public void setDiaSemana(int diaSemana) { this.diaSemana = diaSemana; }

  public boolean isActivo() { return activo; }
  public void setActivo(boolean activo) { this.activo = activo; }

  public LocalTime getHoraInicio() { return horaInicio; }
  public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

  public LocalTime getHoraFin() { return horaFin; }
  public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

  public String getLugar() { return lugar; }
  public void setLugar(String lugar) { this.lugar = lugar; }

  public boolean isConGimnasio() { return conGimnasio; }
  public void setConGimnasio(boolean conGimnasio) { this.conGimnasio = conGimnasio; }

  public boolean isConNatacion() { return conNatacion; }
  public void setConNatacion(boolean conNatacion) { this.conNatacion = conNatacion; }

  public boolean isConWaterpolo() { return conWaterpolo; }
  public void setConWaterpolo(boolean conWaterpolo) { this.conWaterpolo = conWaterpolo; }
}
