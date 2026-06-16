package com.poloplan.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

  /** Asistencias de todas las sesiones de la planificación en el rango. */
  @Query("""
    SELECT a
    FROM Asistencia a
    JOIN a.sesionDia s
    JOIN s.planificacion p
    WHERE p.numero = :planNumero
      AND p.user.id = :userId
      AND s.fecha >= :desde
      AND s.fecha <= :hasta
    ORDER BY s.fecha ASC, s.id ASC
  """)
  List<Asistencia> listByPlanAndOwnerBetween(
    @Param("planNumero") Long planNumero,
    @Param("userId") Long userId,
    @Param("desde") LocalDate desde,
    @Param("hasta") LocalDate hasta
  );

  @Query("""
    SELECT a
    FROM Asistencia a
    JOIN a.sesionDia s
    JOIN s.planificacion p
    JOIN a.jugador j
    WHERE s.id = :sesionId
      AND p.numero = :planNumero
      AND p.user.id = :userId
      AND j.planificacion.id = p.id
    ORDER BY j.id ASC, a.id ASC
  """)
  List<Asistencia> listBySesionAndOwner(@Param("planNumero") Long planNumero, @Param("sesionId") Long sesionId, @Param("userId") Long userId);

  @Query("""
    SELECT a
    FROM Asistencia a
    JOIN a.sesionDia s
    JOIN s.planificacion p
    JOIN a.jugador j
    WHERE s.id = :sesionId
      AND j.id = :jugadorId
      AND p.numero = :planNumero
      AND p.user.id = :userId
      AND j.planificacion.id = p.id
  """)
  Optional<Asistencia> findBySesionAndJugadorAndOwner(
    @Param("planNumero") Long planNumero,
    @Param("sesionId") Long sesionId,
    @Param("jugadorId") Long jugadorId,
    @Param("userId") Long userId
  );
}

