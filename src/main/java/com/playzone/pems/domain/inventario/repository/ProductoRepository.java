package com.playzone.pems.domain.inventario.repository;

import com.playzone.pems.domain.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    Optional<Producto> findById(Long id);

    Page<Producto> findBySede(Long idSede, Pageable pageable);

    Page<Producto> findBySedeAndCategoria(Long idSede, Long idCategoria, Pageable pageable);

    Page<Producto> findBySedeAndNombre(Long idSede, String nombre, Pageable pageable);

    List<Producto> findEnAlertaDeStock(Long idSede);

    Producto save(Producto producto);
}