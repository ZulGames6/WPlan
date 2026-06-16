package com.poloplan.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.poloplan.drools.GimnasioCandidato;
import com.poloplan.drools.GimnasioObjetivo;
import com.poloplan.dto.SugerenciasDtos.SugerenciaGimnasioItem;
import com.poloplan.dto.SugerenciasDtos.SugerenciaGimnasioResponse;
import com.poloplan.dto.SugerenciasDtos.SugerirGimnasioRequest;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioGimnasio;
import com.poloplan.repository.EjercicioGimnasioRepository;

@Service
public class SugerenciasGimnasioService {

  private static final Logger log = LoggerFactory.getLogger(SugerenciasGimnasioService.class);

  private final EjercicioGimnasioRepository repository;
  private final KieContainer kieContainer;

  public SugerenciasGimnasioService(EjercicioGimnasioRepository repository, KieContainer kieContainer) {
    this.repository = repository;
    this.kieContainer = kieContainer;
  }

  public SugerenciaGimnasioResponse sugerir(AppUser propietario, SugerirGimnasioRequest req) {
    GimnasioObjetivo objetivo = new GimnasioObjetivo();
    objetivo.setFase(toUpper(req.fase()));
    objetivo.setGrupos(toUpperList(req.grupos()));
    objetivo.setTipo(toUpper(req.tipo()));
    objetivo.setIntensidad(toUpper(req.intensidad()));
    objetivo.setMinutos(req.minutos());
    objetivo.setExcluirEquipamiento(toUpperList(req.excluirEquipamiento()));
    objetivo.setNivel(toUpper(req.nivel()));
    // DUP: rotate between Volume (0), Standard (1), Intensity (2) each call
    Random random = new Random();
    objetivo.setVariante(random.nextInt(3));

    List<EjercicioGimnasio> catalogo = repository.listByOwner(propietario.getId());
    List<GimnasioCandidato> candidatos = new ArrayList<>(catalogo.size());
    for (EjercicioGimnasio e : catalogo) {
      GimnasioCandidato c = aCandidato(e);
      // Pseudoaleatoriedad por candidato: las reglas la usan para añadir
      // variabilidad reproducible dentro de rangos sanos.
      c.setRand(random.nextInt(100));
      candidatos.add(c);
    }

    List<Object> hechos = new ArrayList<>();
    hechos.add(objetivo);
    hechos.addAll(candidatos);

    log.info("Drools gimnasio: hechos={} (objetivo + {} candidatos, variante DUP={})",
             hechos.size(), candidatos.size(), objetivo.getVariante());
    StatelessKieSession session = kieContainer.newStatelessKieSession("gimnasioSession");
    long t0 = System.currentTimeMillis();
    session.execute(hechos);
    log.info("Drools gimnasio: execute() finalizado en {} ms", System.currentTimeMillis() - t0);

    int max = req.maxEjercicios() != null ? req.maxEjercicios() : 6;

    // Shuffle primero para que candidatos con la misma puntuación roten entre llamadas;
    // el sort es estable.
    List<GimnasioCandidato> aptos = new ArrayList<>(
      candidatos.stream().filter(c -> !c.isExcluido()).toList()
    );
    Collections.shuffle(aptos);
    aptos.sort(Comparator.comparingDouble(GimnasioCandidato::getPuntuacion).reversed());

    List<GimnasioCandidato> seleccion = aptos.stream().limit(max).toList();

    // Asignar orden y aplicar modulación por posición (no se puede hacer
    // dentro de Drools porque el orden depende de la puntuación final).
    for (int i = 0; i < seleccion.size(); i++) {
      GimnasioCandidato c = seleccion.get(i);
      c.setOrden(i + 1);
      aplicarModulacionPorPosicion(c, seleccion.size());
    }

    List<SugerenciaGimnasioItem> items = seleccion.stream()
      .map(SugerenciasGimnasioService::aItem)
      .toList();

    return new SugerenciaGimnasioResponse(items);
  }

  /**
   * Apertura (posiciones 1-2): ejercicios pesados/compound. Suben %RM y descanso.
   * Accesorios (posiciones {@code orden >= 5}): bajan %RM, suben reps, bajan descanso.
   * No aplica a MOVILIDAD/ACTIVACION/CORE para no romper su semántica.
   */
  private static void aplicarModulacionPorPosicion(GimnasioCandidato c, int totalEnSesion) {
    Integer orden = c.getOrden();
    String tipo = c.getTipo();
    if (orden == null || c.getSeries() == null) return;
    boolean esSuave = "CORE".equals(tipo) || "MOVILIDAD".equals(tipo) || "ACTIVACION".equals(tipo);

    if (orden <= 2 && !esSuave) {
      if (c.getPorcRm() != null) {
        c.setPorcRm(Math.min(95, c.getPorcRm() + 4));
      }
      if (c.getDescansoSeg() != null) {
        c.setDescansoSeg(c.getDescansoSeg() + 30);
      }
      c.anadirMotivo("Apertura #" + orden + ": +%RM +30s desc");
    } else if (orden >= 5 && !"MOVILIDAD".equals(tipo)) {
      int reps = c.getRepeticiones() == null ? 0 : c.getRepeticiones();
      c.setRepeticiones(Math.min(25, reps + 2));
      if (c.getPorcRm() != null) {
        c.setPorcRm(Math.max(30, c.getPorcRm() - 5));
      }
      if (c.getDescansoSeg() != null) {
        c.setDescansoSeg(Math.max(30, c.getDescansoSeg() - 15));
      }
      c.anadirMotivo("Accesorio #" + orden + ": +reps -%RM -desc");
    }
  }

  private static GimnasioCandidato aCandidato(EjercicioGimnasio e) {
    GimnasioCandidato c = new GimnasioCandidato();
    c.setEjercicioId(e.getId());
    c.setNombre(e.getNombre());
    c.setGrupoMuscular(e.getGrupoMuscular());
    c.setPatron(e.getPatron());
    c.setEquipamiento(e.getEquipamiento());
    c.setTipo(e.getTipo());
    c.setUnilateral(e.getUnilateral());
    return c;
  }

  private static SugerenciaGimnasioItem aItem(GimnasioCandidato c) {
    return new SugerenciaGimnasioItem(
      c.getEjercicioId(),
      c.getNombre(),
      c.getGrupoMuscular(),
      c.getPatron(),
      c.getEquipamiento(),
      c.getTipo(),
      c.getOrden(),
      c.getSeries(),
      c.getRepeticiones(),
      c.getPorcRm(),
      c.getRir(),
      c.getDescansoSeg(),
      c.getTempo(),
      c.getPuntuacion(),
      c.getMotivo()
    );
  }

  private static String toUpper(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t.toUpperCase();
  }

  private static List<String> toUpperList(List<String> list) {
    if (list == null) return List.of();
    return list.stream().filter(s -> s != null && !s.isBlank()).map(s -> s.trim().toUpperCase()).toList();
  }
}
