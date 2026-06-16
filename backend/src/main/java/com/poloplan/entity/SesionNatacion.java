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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "sesion_natacion")
public class SesionNatacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sesion_dia_id", nullable = false, unique = true)
  private SesionDia sesionDia;

  @Column(nullable = true, length = 300)
  private String objetivo;

  @Column(nullable = true, length = 1000)
  private String notas;

  @OneToMany(mappedBy = "sesionNatacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("orden ASC, id ASC")
  private List<SesionNatacionBloque> bloques = new ArrayList<>();

  public List<SesionNatacionBloque> getBloques() { return bloques; }
  public void setBloques(List<SesionNatacionBloque> bloques) { this.bloques = bloques; }

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
}

