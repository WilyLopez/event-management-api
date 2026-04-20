package com.playzone.pems.infrastructure.persistence.proveedor.jpa;

import com.playzone.pems.infrastructure.persistence.proveedor.entity.ProveedorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProveedorJpaRepository extends JpaRepository<ProveedorEntity, Long> {

    Optional<ProveedorEntity> findByRuc(String ruc);

    @Query("SELECT p FROM ProveedorEntity p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%',:texto,'%')) OR LOWER(p.tipoServicio) LIKE LOWER(CONCAT('%',:texto,'%'))")
    Page<ProveedorEntity> findByNombreOrTipoServicio(@Param("texto") String texto, Pageable pageable);

    Page<ProveedorEntity> findByActivoTrue(Pageable pageable);

    boolean existsByRuc(String ruc);
}