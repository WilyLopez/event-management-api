package com.playzone.pems.domain.proveedor.repository;

import com.playzone.pems.domain.proveedor.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProveedorRepository {

    Optional<Proveedor> findById(Long id);

    Optional<Proveedor> findByRuc(String ruc);

    Page<Proveedor> findAll(Pageable pageable);

    Page<Proveedor> findByNombreOrTipoServicio(String texto, Pageable pageable);

    Page<Proveedor> findAllActivos(Pageable pageable);

    Proveedor save(Proveedor proveedor);

    boolean existsByRuc(String ruc);
}