package com.poloplan.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.SesionDia;

public interface SesionDiaRepository extends JpaRepository<SesionDia, Long> {

  @Query("""
    SELECT s
    FROM SesionDia s
    JOIN s.planificacion p
    WHERE p.numero = :planNumero
      AND p.user.id = :userId
    ORDER BY s.fecha ASC, s.horaInicio ASC, s.id ASC
  """)
  List<SesionDia> listByPlanNumeroAndOwner(@Param("planNumero") Long planNumero, @Param("userId") Long userId);

  @Query("""
    SELECT s
    FROM SesionDia s
    JOIN s.planificacion p
    WHERE p.numero = :planNumero
      AND p.user.id = :userId
      AND s.fecha >= :desde
      AND s.fecha <= :hasta
    ORDER BY s.fecha ASC, s.horaInicio ASC, s.id ASC
  """)
  List<SesionDia> listByPlanNumeroAndOwnerBetweenFechas(
    @Param("planNumero") Long planNumero,
    @Param("userId") Long userId,
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
  );

  @Query("""
    SELECT s
    FROM SesionDia s
    JOIN s.planificacion p
    WHERE s.id = :sesionId
      AND p.numero = :planNumero
      AND p.user.id = :userId
  """)
  Optional<SesionDia> findByIdAndPlanNumeroAndOwner(
    @Param("sesionId") Long sesionId,
    @Param("planNumero") Long planNumero,
    @Param("userId") Long userId
  );
}

