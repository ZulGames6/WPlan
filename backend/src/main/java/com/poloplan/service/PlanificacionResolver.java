package com.poloplan.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.poloplan.entity.AppUser;
import com.poloplan.entity.Planificacion;
import com.poloplan.repository.PlanificacionRepository;

/**
 * Resuelve la planificación por número público o, en datos legacy, por id interno.
 * Garantiza que {@link Planificacion#getNumero()} no quede nulo.
 */
@Service
public class PlanificacionResolver {

  private final PlanificacionRepository planificacionRepository;

  public PlanificacionResolver(PlanificacionRepository planificacionRepository) {
    this.planificacionRepository = planificacionRepository;
  }

  @Transactional
  public Planificacion require(AppUser propietario, Long ref) {
    Planificacion p = planificacionRepository.findByRefAndOwner(ref, propietario.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Planificación no encontrada"));
    ensureNumero(propietario, p);
    return p;
  }

  @Transactional
  public long requireNumero(AppUser propietario, Long ref) {
    return require(propietario, ref).getNumero();
  }

  @Transactional
  public void backfillNumerosFaltantes(AppUser propietario) {
    for (Planificacion p : planificacionRepository.listByOwner(propietario.getId())) {
      if (p.getNumero() == null) {
        ensureNumero(propietario, p);
      }
    }
  }

  private void ensureNumero(AppUser propietario, Planificacion p) {
    if (p.getNumero() == null) {
      p.setNumero(planificacionRepository.maxNumeroByOwner(propietario.getId()) + 1);
      planificacionRepository.save(p);
    }
  }
}
