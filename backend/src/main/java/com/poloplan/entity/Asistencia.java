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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
  name = "asistencia",
  uniqueConstraints = {@UniqueConstraint(columnNames = {"sesion_dia_id", "jugador_id"})}
)
public class Asistencia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sesion_dia_id", nullable = false)
  private SesionDia sesionDia;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "jugador_id", nullable = false)
  private Jugador jugador;

  @Column(nullable = false, length = 30)
  private String estado;

  @Column(nullable = true, length = 300)
  private String nota;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SesionDia getSesionDia() {
    return sesionDia;
  }

  public void setSesionDia(SesionDia sesionDia) {
    this.sesionDia = sesionDia;
  }

  public Jugador getJugador() {
    return jugador;
  }

  public void setJugador(Jugador jugador) {
    this.jugador = jugador;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public String getNota() {
    return nota;
  }

  public void setNota(String nota) {
    this.nota = nota;
  }
}

