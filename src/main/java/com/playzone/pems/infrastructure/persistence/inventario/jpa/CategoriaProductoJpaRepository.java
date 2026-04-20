package com.playzone.pems.infrastructure.persistence.inventario.jpa;

import com.playzone.pems.infrastructure.persistence.inventario.entity.CategoriaProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaProductoJpaRepository extends JpaRepository<CategoriaProductoEntity, Long> {

    List<CategoriaProductoEntity> findByActivoTrue();

    boolean existsByNombre(String nombre);
}