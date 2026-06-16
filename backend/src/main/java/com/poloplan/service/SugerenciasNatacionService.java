package com.poloplan.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.poloplan.drools.NatacionBloqueGenerado;
import com.poloplan.drools.NatacionObjetivo;
import com.poloplan.dto.SugerenciasDtos.SugerenciaNatacionBloque;
import com.poloplan.dto.SugerenciasDtos.SugerenciaNatacionResponse;
import com.poloplan.dto.SugerenciasDtos.SugerirNatacionRequest;
import com.poloplan.entity.AppUser;

@Service
public class SugerenciasNatacionService {

  private static final Logger log = LoggerFactory.getLogger(SugerenciasNatacionService.class);

  private final KieContainer kieContainer;

  public SugerenciasNatacionService(KieContainer kieContainer) {
    this.kieContainer = kieContainer;
  }

  public SugerenciaNatacionResponse sugerir(AppUser propietario, SugerirNatacionRequest req) {
    NatacionObjetivo objetivo = new NatacionObjetivo();
    objetivo.setObjetivo(toUpper(req.objetivo()));
    objetivo.setVolumenMetros(req.metrosObjetivo() != null ? req.metrosObjetivo() : 2000);
    objetivo.setConCalentamiento(req.conCalentamiento() == null || req.conCalentamiento());
    objetivo.setConVueltaCalma(req.conVueltaCalma() == null || req.conVueltaCalma());
    objetivo.setNivel(toUpper(req.nivel()));
    objetivo.setEstiloPreferente(toUpper(req.estiloPreferente()));

    Random random = new Random();
    // Variante estructural del bloque principal y resto: rota entre 3 formatos
    // para que dos llamadas consecutivas no produzcan la misma sesión.
    objetivo.setVariante(random.nextInt(3));
    objetivo.setRand(random.nextInt(100));

    List<NatacionBloqueGenerado> bloques = List.of(
      new NatacionBloqueGenerado("CALENTAMIENTO"),
      new NatacionBloqueGenerado("TECNICA"),
      new NatacionBloqueGenerado("PIERNAS"),
      new NatacionBloqueGenerado("MATERIAL"),
      new NatacionBloqueGenerado("PRINCIPAL"),
      new NatacionBloqueGenerado("VUELTA_CALMA")
    );

    List<Object> hechos = new ArrayList<>();
    hechos.add(objetivo);
    hechos.addAll(bloques);

    log.info("Drools natacion: objetivo={} vol={} variante={} nivel={}",
             objetivo.getObjetivo(), objetivo.getVolumenMetros(),
             objetivo.getVariante(), objetivo.getNivel());
    StatelessKieSession session = kieContainer.newStatelessKieSession("natacionSession");
    long t0 = System.currentTimeMillis();
    session.execute(hechos);
    log.info("Drools natacion: execute() finalizado en {} ms", System.currentTimeMillis() - t0);

    List<NatacionBloqueGenerado> activos = bloques.stream()
      .filter(NatacionBloqueGenerado::isActivo)
      .filter(b -> objetivo.isConCalentamiento() || !"CALENTAMIENTO".equals(b.getTipo()))
      .filter(b -> objetivo.isConVueltaCalma() || !"VUELTA_CALMA".equals(b.getTipo()))
      .sorted(Comparator.comparingInt(b -> b.getOrden() != null ? b.getOrden() : 99))
      .toList();

    int volTotal = activos.stream().mapToInt(NatacionBloqueGenerado::metrosTotales).sum();
    double cargaTotal = activos.stream().mapToDouble(NatacionBloqueGenerado::cargaEstimada).sum();

    List<SugerenciaNatacionBloque> respBloques = activos.stream()
      .map(SugerenciasNatacionService::aBloque)
      .toList();

    return new SugerenciaNatacionResponse(objetivo.getObjetivo(), volTotal, cargaTotal, respBloques);
  }

  private static SugerenciaNatacionBloque aBloque(NatacionBloqueGenerado b) {
    return new SugerenciaNatacionBloque(
      b.getTipo(),
      b.getDescripcion(),
      b.getIntensidadAE(),
      b.getSeries(),
      b.getMetrosPorSerie(),
      b.getDescansoSeg(),
      b.getMaterial(),
      b.metrosTotales(),
      b.cargaEstimada(),
      b.getMotivo()
    );
  }

  private static String toUpper(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t.toUpperCase();
  }
}
