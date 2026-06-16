package com.poloplan.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.SesionWaterpoloEjercicio;

public interface SesionWaterpoloEjercicioRepository extends JpaRepository<SesionWaterpoloEjercicio, Long> {

  /** Ejercicios de waterpolo de la planificación dentro del rango (para métricas). */
  @Query("""
    SELECT se
    FROM SesionWaterpoloEjercicio se
    JOIN se.sesionWaterpolo sw
    JOIN sw.sesionDia s
    JOIN s.planificacion p
    JOIN FETCH se.ejercicio e
    WHERE p.numero = :planNumero
      AND p.user.id = :userId
      AND s.fecha >= :desde
      AND s.fecha <= :hasta
    ORDER BY s.fecha ASC
  """)
  List<SesionWaterpoloEjercicio> listByPlanAndOwnerBetween(
    @Param("planNumero") Long planNumero,
    @Param("userId") Long userId,
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
  );

  @Query("""
    SELECT se
    FROM SesionWaterpoloEjercicio se
    JOIN se.sesionWaterpolo sw
    JOIN sw.sesionDia s
    JOIN s.planificacion p
    WHERE s.id = :sesionId
      AND p.numero = :planNumero
      AND p.user.id = :userId
    ORDER BY se.orden ASC, se.id ASC
  """)
  List<SesionWaterpoloEjercicio> listBySesionAndOwner(@Param("planNumero") Long planNumero,
                                                      @Param("sesionId") Long sesionId,
                                                      @Param("userId") Long userId);

  @Query("""
    SELECT se
    FROM SesionWaterpoloEjercicio se
    JOIN se.sesionWaterpolo sw
    JOIN sw.sesionDia s
    JOIN s.planificacion p
    WHERE se.id = :itemId
      AND s.id = :sesionId
      AND p.numero = :planNumero
      AND p.user.id = :userId
  """)
  Optional<SesionWaterpoloEjercicio> findOwned(@Param("planNumero") Long planNumero,
                                               @Param("sesionId") Long sesionId,
                                               @Param("itemId") Long itemId,
                                               @Param("userId") Long userId);

  @Query("""
    SELECT COALESCE(MAX(se.orden), 0)
    FROM SesionWaterpoloEjercicio se
    WHERE se.sesionWaterpolo.id = :sesionWaterpoloId
  """)
  int maxOrden(@Param("sesionWaterpoloId") Long sesionWaterpoloId);
}
