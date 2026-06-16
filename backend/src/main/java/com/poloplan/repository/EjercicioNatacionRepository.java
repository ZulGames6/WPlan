package com.poloplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.EjercicioNatacion;

public interface EjercicioNatacionRepository extends JpaRepository<EjercicioNatacion, Long> {

  @Query("SELECT e FROM EjercicioNatacion e WHERE e.owner.id = :userId ORDER BY e.nombre ASC, e.id ASC")
  List<EjercicioNatacion> listByOwner(@Param("userId") Long userId);

  @Query("SELECT e FROM EjercicioNatacion e WHERE e.id = :id AND e.owner.id = :userId")
  Optional<EjercicioNatacion> findByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);
}
