package com.playzone.pems.infrastructure.persistence.inventario.adapter;

import com.playzone.pems.domain.inventario.model.Producto;
import com.playzone.pems.domain.inventario.repository.ProductoRepository;
import com.playzone.pems.infrastructure.persistence.inventario.jpa.CategoriaProductoJpaRepository;
import com.playzone.pems.infrastructure.persistence.inventario.jpa.ProductoJpaRepository;
import com.playzone.pems.infrastructure.persistence.inventario.mapper.ProductoEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductoPersistenceAdapter implements ProductoRepository {

    private final ProductoJpaRepository          productoJpa;
    private final CategoriaProductoJpaRepository categoriaJpa;
    private final SedeJpaRepository              sedeJpa;
    private final ProductoEntityMapper           mapper;

    @Override
    public Optional<Producto> findById(Long id) {
        return productoJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Producto> findBySede(Long idSede, Pageable pageable) {
        return productoJpa.findBySede_Id(idSede, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Producto> findBySedeAndCategoria(Long idSede, Long idCategoria, Pageable pageable) {
        return productoJpa.findBySede_IdAndCategoria_Id(idSede, idCategoria, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Producto> findBySedeAndNombre(Long idSede, String nombre, Pageable pageable) {
        return productoJpa.findBySedeAndNombre(idSede, nombre, pageable).map(mapper::toDomain);
    }

    @Override
    public List<Producto> findEnAlertaDeStock(Long idSede) {
        return productoJpa.findEnAlertaDeStock(idSede).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        var categoria = categoriaJpa.findById(producto.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaProducto", producto.getIdCategoria()));
        var sede = sedeJpa.findById(producto.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", producto.getIdSede()));
        return mapper.toDomain(productoJpa.save(mapper.toEntity(producto, categoria, sede)));
    }
}