package com.playzone.pems.domain.inventario.repository;

import com.playzone.pems.domain.inventario.model.CategoriaProducto;

import java.util.List;
import java.util.Optional;

public interface CategoriaProductoRepository {

    Optional<CategoriaProducto> findById(Long id);

    List<CategoriaProducto> findAllActivas();

    CategoriaProducto save(CategoriaProducto categoria);

    boolean existsByNombre(String nombre);
}