package com.poloplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.Planificacion;

public interface PlanificacionRepository extends JpaRepository<Planificacion, Long> {

  @Query("SELECT p FROM Planificacion p WHERE p.user.id = :userId ORDER BY p.numero DESC, p.id DESC")
  List<Planificacion> listByOwner(@Param("userId") Long userId);

  @Query("SELECT p FROM Planificacion p WHERE p.numero = :numero AND p.user.id = :userId")
  Optional<Planificacion> findByNumeroAndOwner(@Param("numero") Long numero, @Param("userId") Long userId);

  /** Número público o id interno (planificaciones antiguas sin {@code numero}). */
  @Query("SELECT p FROM Planificacion p WHERE p.user.id = :userId AND (p.numero = :ref OR p.id = :ref)")
  Optional<Planificacion> findByRefAndOwner(@Param("ref") Long ref, @Param("userId") Long userId);

  @Query("SELECT COALESCE(MAX(p.numero), 0) FROM Planificacion p WHERE p.user.id = :userId")
  long maxNumeroByOwner(@Param("userId") Long userId);
}
