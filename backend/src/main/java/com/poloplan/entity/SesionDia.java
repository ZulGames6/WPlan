package com.poloplan.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesion_dia")
public class SesionDia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "planificacion_id", nullable = false)
  private Planificacion planificacion;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(nullable = true)
  private LocalTime horaInicio;

  @Column(nullable = true)
  private LocalTime horaFin;

  @Column(nullable = true, length = 160)
  private String lugar;

  @Column(nullable = true, length = 40)
  private String tipo;

  @Column(nullable = true, length = 40)
  private String estado;

  @Column(nullable = true, length = 300)
  private String objetivo;

  @Column(nullable = true, length = 1000)
  private String notas;

  @OneToOne(mappedBy = "sesionDia", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private SesionGimnasio gimnasio;

  @OneToOne(mappedBy = "sesionDia", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private SesionNatacion natacion;

  @OneToOne(mappedBy = "sesionDia", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private SesionWaterpolo waterpolo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Planificacion getPlanificacion() {
    return planificacion;
  }

  public void setPlanificacion(Planificacion planificacion) {
    this.planificacion = planificacion;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public LocalTime getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(LocalTime horaInicio) {
    this.horaInicio = horaInicio;
  }

  public LocalTime getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(LocalTime horaFin) {
    this.horaFin = horaFin;
  }

  public String getLugar() {
    return lugar;
  }

  public void setLugar(String lugar) {
    this.lugar = lugar;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public String getObjetivo() {
    return objetivo;
  }

  public void setObjetivo(String objetivo) {
    this.objetivo = objetivo;
  }

  public String getNotas() {
    return notas;
  }

  public void setNotas(String notas) {
    this.notas = notas;
  }

  public SesionGimnasio getGimnasio() {
    return gimnasio;
  }

  public void setGimnasio(SesionGimnasio gimnasio) {
    this.gimnasio = gimnasio;
  }

  public SesionNatacion getNatacion() {
    return natacion;
  }

  public void setNatacion(SesionNatacion natacion) {
    this.natacion = natacion;
  }

  public SesionWaterpolo getWaterpolo() {
    return waterpolo;
  }

  public void setWaterpolo(SesionWaterpolo waterpolo) {
    this.waterpolo = waterpolo;
  }
}

