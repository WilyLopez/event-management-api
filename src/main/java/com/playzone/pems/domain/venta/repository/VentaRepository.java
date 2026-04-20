package com.playzone.pems.domain.venta.repository;

import com.playzone.pems.domain.venta.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VentaRepository {

    Optional<Venta> findById(Long id);

    Page<Venta> findBySedeAndFechasBetween(
            Long idSede,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable);

    Page<Venta> findByUsuario(Long idUsuario, Pageable pageable);

    Venta save(Venta venta);
}