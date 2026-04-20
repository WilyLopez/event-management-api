package com.playzone.pems.infrastructure.persistence.inventario.adapter;

import com.playzone.pems.domain.inventario.model.CategoriaProducto;
import com.playzone.pems.domain.inventario.repository.CategoriaProductoRepository;
import com.playzone.pems.infrastructure.persistence.inventario.entity.CategoriaProductoEntity;
import com.playzone.pems.infrastructure.persistence.inventario.jpa.CategoriaProductoJpaRepository;
import com.playzone.pems.infrastructure.persistence.inventario.mapper.ProductoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoriaProductoPersistenceAdapter implements CategoriaProductoRepository {

    private final CategoriaProductoJpaRepository categoriaJpa;
    private final ProductoEntityMapper           mapper;

    @Override
    public Optional<CategoriaProducto> findById(Long id) {
        return categoriaJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CategoriaProducto> findAllActivas() {
        return categoriaJpa.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public CategoriaProducto save(CategoriaProducto cat) {
        CategoriaProductoEntity entity = CategoriaProductoEntity.builder()
                .id(cat.getId())
                .nombre(cat.getNombre())
                .descripcion(cat.getDescripcion())
                .activo(cat.isActivo())
                .build();
        return mapper.toDomain(categoriaJpa.save(entity));
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return categoriaJpa.existsByNombre(nombre);
    }
}