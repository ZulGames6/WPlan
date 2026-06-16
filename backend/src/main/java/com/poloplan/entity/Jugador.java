package com.poloplan.entity;

import java.time.LocalDate;

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
@Table(name = "jugador")
public class Jugador {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "planificacion_id", nullable = false)
  private Planificacion planificacion;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(nullable = true, length = 200)
  private String apellidos;

  @Column(nullable = true)
  private LocalDate fechaNacimiento;

  @Column(nullable = true, length = 60)
  private String posicion;

  @Column(nullable = true, length = 1000)
  private String notas;

  @Column(name = "peso_kg", nullable = true, precision = 5, scale = 2)
  private java.math.BigDecimal pesoKg;

  @Column(nullable = true, length = 20)
  private String dni;

  @Column(name = "talla_banador", nullable = true, length = 20)
  private String tallaBanador;

  @Column(name = "talla_camiseta", nullable = true, length = 10)
  private String tallaCamiseta;

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

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellidos() {
    return apellidos;
  }

  public void setApellidos(String apellidos) {
    this.apellidos = apellidos;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public String getPosicion() {
    return posicion;
  }

  public void setPosicion(String posicion) {
    this.posicion = posicion;
  }

  public String getNotas() {
    return notas;
  }

  public void setNotas(String notas) {
    this.notas = notas;
  }

  public java.math.BigDecimal getPesoKg() {
    return pesoKg;
  }

  public void setPesoKg(java.math.BigDecimal pesoKg) {
    this.pesoKg = pesoKg;
  }

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  public String getTallaBanador() {
    return tallaBanador;
  }

  public void setTallaBanador(String tallaBanador) {
    this.tallaBanador = tallaBanador;
  }

  public String getTallaCamiseta() {
    return tallaCamiseta;
  }

  public void setTallaCamiseta(String tallaCamiseta) {
    this.tallaCamiseta = tallaCamiseta;
  }
}

