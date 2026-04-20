package com.playzone.pems.infrastructure.persistence.proveedor.adapter;

import com.playzone.pems.domain.proveedor.model.Proveedor;
import com.playzone.pems.domain.proveedor.repository.ProveedorRepository;
import com.playzone.pems.infrastructure.persistence.proveedor.jpa.ProveedorJpaRepository;
import com.playzone.pems.infrastructure.persistence.proveedor.mapper.ProveedorEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProveedorPersistenceAdapter implements ProveedorRepository {

    private final ProveedorJpaRepository proveedorJpa;
    private final ProveedorEntityMapper  mapper;

    @Override public Optional<Proveedor> findById(Long id) {
        return proveedorJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Optional<Proveedor> findByRuc(String ruc) {
        return proveedorJpa.findByRuc(ruc).map(mapper::toDomain);
    }

    @Override public Page<Proveedor> findAll(Pageable pageable) {
        return proveedorJpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override public Page<Proveedor> findByNombreOrTipoServicio(String texto, Pageable pageable) {
        return proveedorJpa.findByNombreOrTipoServicio(texto, pageable).map(mapper::toDomain);
    }

    @Override public Page<Proveedor> findAllActivos(Pageable pageable) {
        return proveedorJpa.findByActivoTrue(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Proveedor save(Proveedor proveedor) {
        return mapper.toDomain(proveedorJpa.save(mapper.toEntity(proveedor)));
    }

    @Override public boolean existsByRuc(String ruc) { return proveedorJpa.existsByRuc(ruc); }
}