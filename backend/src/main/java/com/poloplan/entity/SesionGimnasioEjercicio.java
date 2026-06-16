package com.poloplan.entity;

import java.math.BigDecimal;

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
@Table(name = "sesion_gimnasio_ejercicio")
public class SesionGimnasioEjercicio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sesion_gimnasio_id", nullable = false)
  private SesionGimnasio sesionGimnasio;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ejercicio_id", nullable = false)
  private EjercicioGimnasio ejercicio;

  @Column(nullable = false)
  private Integer orden;

  @Column(nullable = true)
  private Integer series;

  @Column(nullable = true)
  private Integer repeticiones;

  /** Peso en kilogramos (opcional, p.ej. peso corporal). */
  @Column(name = "peso_kg", nullable = true, precision = 6, scale = 2)
  private BigDecimal pesoKg;

  /** Porcentaje del RM, p.ej. 70 (= 70 % 1RM). */
  @Column(name = "porc_rm", nullable = true, precision = 5, scale = 2)
  private BigDecimal porcRm;

  /** Reps in reserve (0-5). */
  @Column(nullable = true)
  private Integer rir;

  /** Descanso entre series en segundos. */
  @Column(name = "descanso_seg", nullable = true)
  private Integer descansoSeg;

  /** Tempo (p.ej. 3-1-1). */
  @Column(nullable = true, length = 20)
  private String tempo;

  @Column(nullable = true, length = 500)
  private String notas;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public SesionGimnasio getSesionGimnasio() { return sesionGimnasio; }
  public void setSesionGimnasio(SesionGimnasio s) { this.sesionGimnasio = s; }
  public EjercicioGimnasio getEjercicio() { return ejercicio; }
  public void setEjercicio(EjercicioGimnasio e) { this.ejercicio = e; }
  public Integer getOrden() { return orden; }
  public void setOrden(Integer orden) { this.orden = orden; }
  public Integer getSeries() { return series; }
  public void setSeries(Integer series) { this.series = series; }
  public Integer getRepeticiones() { return repeticiones; }
  public void setRepeticiones(Integer r) { this.repeticiones = r; }
  public BigDecimal getPesoKg() { return pesoKg; }
  public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
  public BigDecimal getPorcRm() { return porcRm; }
  public void setPorcRm(BigDecimal porcRm) { this.porcRm = porcRm; }
  public Integer getRir() { return rir; }
  public void setRir(Integer rir) { this.rir = rir; }
  public Integer getDescansoSeg() { return descansoSeg; }
  public void setDescansoSeg(Integer d) { this.descansoSeg = d; }
  public String getTempo() { return tempo; }
  public void setTempo(String tempo) { this.tempo = tempo; }
  public String getNotas() { return notas; }
  public void setNotas(String notas) { this.notas = notas; }
}
