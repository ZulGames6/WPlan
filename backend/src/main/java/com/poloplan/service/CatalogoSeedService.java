package com.poloplan.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poloplan.catalogo.CatalogoPlantilla;
import com.poloplan.dto.CatalogoDtos.SeedCatalogoResponse;
import com.poloplan.entity.AppUser;
import com.poloplan.entity.EjercicioGimnasio;
import com.poloplan.entity.EjercicioNatacion;
import com.poloplan.entity.EjercicioWaterpolo;
import com.poloplan.repository.EjercicioGimnasioRepository;
import com.poloplan.repository.EjercicioNatacionRepository;
import com.poloplan.repository.EjercicioWaterpoloRepository;

@Service
public class CatalogoSeedService {

  private final EjercicioGimnasioRepository gimnasioRepo;
  private final EjercicioNatacionRepository natacionRepo;
  private final EjercicioWaterpoloRepository waterpoloRepo;

  public CatalogoSeedService(
    EjercicioGimnasioRepository gimnasioRepo,
    EjercicioNatacionRepository natacionRepo,
    EjercicioWaterpoloRepository waterpoloRepo
  ) {
    this.gimnasioRepo = gimnasioRepo;
    this.natacionRepo = natacionRepo;
    this.waterpoloRepo = waterpoloRepo;
  }

  @Transactional
  public SeedCatalogoResponse seedSiVacio(AppUser owner) {
    if (!gimnasioRepo.listByOwner(owner.getId()).isEmpty()
      || !natacionRepo.listByOwner(owner.getId()).isEmpty()
      || !waterpoloRepo.listByOwner(owner.getId()).isEmpty()) {
      return new SeedCatalogoResponse(0, 0, 0, false, "El catálogo ya tiene ejercicios");
    }
    return seedForzado(owner);
  }

  @Transactional
  public SeedCatalogoResponse seedForzado(AppUser owner) {
    Long userId = owner.getId();
    Set<String> nombresGym = gimnasioRepo.listByOwner(userId).stream()
      .map(e -> e.getNombre().toLowerCase())
      .collect(Collectors.toCollection(HashSet::new));
    Set<String> nombresNat = natacionRepo.listByOwner(userId).stream()
      .map(e -> e.getNombre().toLowerCase())
      .collect(Collectors.toCollection(HashSet::new));
    Set<String> nombresWp = waterpoloRepo.listByOwner(userId).stream()
      .map(e -> e.getNombre().toLowerCase())
      .collect(Collectors.toCollection(HashSet::new));

    int g = 0;
    int n = 0;
    int w = 0;

    for (CatalogoPlantilla.Gym t : CatalogoPlantilla.gimnasio()) {
      if (nombresGym.add(t.nombre().toLowerCase())) {
        EjercicioGimnasio e = new EjercicioGimnasio();
        e.setOwner(owner);
        e.setNombre(t.nombre());
        e.setGrupoMuscular(t.grupo());
        e.setPatron(t.patron());
        e.setEquipamiento(t.equipamiento());
        e.setTipo(t.tipo());
        e.setUnilateral(t.unilateral());
        e.setDescripcion(t.descripcion());
        gimnasioRepo.save(e);
        g++;
      }
    }

    for (CatalogoPlantilla.Natacion t : CatalogoPlantilla.natacion()) {
      if (nombresNat.add(t.nombre().toLowerCase())) {
        EjercicioNatacion e = new EjercicioNatacion();
        e.setOwner(owner);
        e.setNombre(t.nombre());
        e.setEstilo(t.estilo());
        e.setTipoBloque(t.tipoBloque());
        e.setIntensidad(t.intensidad());
        e.setMaterial(t.material());
        e.setMetrosBase(t.metrosBase());
        e.setDescripcion(t.descripcion());
        natacionRepo.save(e);
        n++;
      }
    }

    for (CatalogoPlantilla.Waterpolo t : CatalogoPlantilla.waterpolo()) {
      if (nombresWp.add(t.nombre().toLowerCase())) {
        EjercicioWaterpolo e = new EjercicioWaterpolo();
        e.setOwner(owner);
        e.setNombre(t.nombre());
        e.setObjetivo(t.objetivo());
        e.setCategoria(t.categoria());
        e.setIntensidad(t.intensidad());
        e.setMaterial(t.material());
        e.setDuracionMinSugerida(t.duracionMin());
        e.setJugadoresMin(t.jMin());
        e.setJugadoresMax(t.jMax());
        e.setDescripcion(t.descripcion());
        waterpoloRepo.save(e);
        w++;
      }
    }

    return new SeedCatalogoResponse(g, n, w, true, "Catálogo cargado");
  }
}
