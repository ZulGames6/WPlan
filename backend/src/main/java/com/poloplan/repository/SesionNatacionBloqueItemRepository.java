package com.poloplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poloplan.entity.SesionNatacionBloqueItem;

public interface SesionNatacionBloqueItemRepository extends JpaRepository<SesionNatacionBloqueItem, Long> {

  @Query("""
    SELECT i
    FROM SesionNatacionBloqueItem i
    JOIN i.bloque b
    JOIN b.sesionNatacion sn
    JOIN sn.sesionDia s
    JOIN s.planificacion p
    WHERE i.id = :itemId
      AND b.id = :bloqueId
      AND s.id = :sesionId
      AND p.numero = :planNumero
      AND p.user.id = :userId
  """)
  Optional<SesionNatacionBloqueItem> findOwned(@Param("planNumero") Long planNumero,
                                               @Param("sesionId") Long sesionId,
                                               @Param("bloqueId") Long bloqueId,
                                               @Param("itemId") Long itemId,
                                               @Param("userId") Long userId);

  @Query("""
    SELECT COALESCE(MAX(i.orden), 0)
    FROM SesionNatacionBloqueItem i
    WHERE i.bloque.id = :bloqueId
  """)
  int maxOrden(@Param("bloqueId") Long bloqueId);
}
