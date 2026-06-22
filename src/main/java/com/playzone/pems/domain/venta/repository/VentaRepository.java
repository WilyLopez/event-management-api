package com.playzone.pems.domain.venta.repository;

import com.playzone.pems.domain.venta.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface VentaRepository {

    Optional<Venta> findById(Long id);

    List<Venta> findByEventoId(Long eventoId);

    Page<Venta> findBySedeAndFechasBetween(
            Long idSede,
            OffsetDateTime desde,
            OffsetDateTime hasta,
            Pageable pageable);

    Page<Venta> findBySedeAndFechasBetweenAndSearch(
            Long idSede,
            OffsetDateTime desde,
            OffsetDateTime hasta,
            String search,
            Pageable pageable);

    Page<Venta> findByUsuario(UUID idUsuario, Pageable pageable);

    Venta save(Venta venta);

    void deleteById(Long id);
}