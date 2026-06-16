package com.poloplan.dto;

public class CatalogoDtos {

  public record SeedCatalogoResponse(
    int gimnasioAnadidos,
    int natacionAnadidos,
    int waterpoloAnadidos,
    boolean realizado,
    String mensaje
  ) {}
}
