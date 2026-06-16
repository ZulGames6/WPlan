package com.poloplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.Jugador;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {

  @Query("""
    SELECT j
    FROM Jugador j
    JOIN j.planificacion p
    WHERE p.numero = :planNumero
      AND p.user.id = :userId
    ORDER BY j.id ASC
  """)
  List<Jugador> listByPlanNumeroAndOwner(@Param("planNumero") Long planNumero, @Param("userId") Long userId);

  @Query("""
    SELECT j
    FROM Jugador j
    JOIN j.planificacion p
    WHERE j.id = :jugadorId
      AND p.numero = :planNumero
      AND p.user.id = :userId
  """)
  Optional<Jugador> findByIdAndPlanNumeroAndOwner(
    @Param("jugadorId") Long jugadorId,
    @Param("planNumero") Long planNumero,
    @Param("userId") Long userId
  );
}

