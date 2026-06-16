package com.poloplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.HorarioSemanal;

public interface HorarioSemanalRepository extends JpaRepository<HorarioSemanal, Long> {

  @Query("""
    SELECT h FROM HorarioSemanal h
    WHERE h.planificacion.id = :planId
    ORDER BY h.diaSemana ASC
    """)
  List<HorarioSemanal> listByPlanificacionId(@Param("planId") Long planId);

  @Query("""
    SELECT h FROM HorarioSemanal h
    JOIN h.planificacion p
    WHERE p.numero = :planNumero AND p.user.id = :userId AND h.diaSemana = :diaSemana
    """)
  Optional<HorarioSemanal> findByPlanNumeroAndDia(
    @Param("planNumero") Long planNumero,
    @Param("userId") Long userId,
    @Param("diaSemana") int diaSemana
  );
}
