package com.poloplan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.Planificacion;

public interface PlanificacionRepository extends JpaRepository<Planificacion, Long> {

  @Query("SELECT p FROM Planificacion p WHERE p.user.id = :userId ORDER BY p.id DESC")
  List<Planificacion> listByOwner(@Param("userId") Long userId);

  @Query("SELECT p FROM Planificacion p WHERE p.id = :id AND p.user.id = :userId")
  Optional<Planificacion> findByIdAndOwner(@Param("id") Long id, @Param("userId") Long userId);
}
