package com.poloplan.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.SesionNatacionBloque;

public interface SesionNatacionBloqueRepository extends JpaRepository<SesionNatacionBloque, Long> {

  /** Bloques de natación de la planificación dentro del rango (para métricas de carga). */
  @Query("""
    SELECT b
    FROM SesionNatacionBloque b
    JOIN b.sesionNatacion sn
    JOIN sn.sesionDia s
    JOIN s.planificacion p
    WHERE p.numero = :planNumero
      AND p.user.id = :userId
      AND s.fecha >= :desde
      AND s.fecha <= :hasta
    ORDER BY s.fecha ASC
  """)
  List<SesionNatacionBloque> listByPlanAndOwnerBetween(
    @Param("planNumero") Long planNumero,
    @Param("userId") Long userId,
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
  );

  @Query("""
    SELECT b
    FROM SesionNatacionBloque b
    JOIN b.sesionNatacion sn
    JOIN sn.sesionDia s
    JOIN s.planificacion p
    WHERE s.id = :sesionId
      AND p.numero = :planNumero
      AND p.user.id = :userId
    ORDER BY b.orden ASC, b.id ASC
  """)
  List<SesionNatacionBloque> listBySesionAndOwner(@Param("planNumero") Long planNumero,
                                                  @Param("sesionId") Long sesionId,
                                                  @Param("userId") Long userId);

  @Query("""
    SELECT b
    FROM SesionNatacionBloque b
    JOIN b.sesionNatacion sn
    JOIN sn.sesionDia s
    JOIN s.planificacion p
    WHERE b.id = :bloqueId
      AND s.id = :sesionId
      AND p.numero = :planNumero
      AND p.user.id = :userId
  """)
  Optional<SesionNatacionBloque> findOwned(@Param("planNumero") Long planNumero,
                                           @Param("sesionId") Long sesionId,
                                           @Param("bloqueId") Long bloqueId,
                                           @Param("userId") Long userId);

  @Query("""
    SELECT COALESCE(MAX(b.orden), 0)
    FROM SesionNatacionBloque b
    WHERE b.sesionNatacion.id = :sesionNatacionId
  """)
  int maxOrden(@Param("sesionNatacionId") Long sesionNatacionId);
}
