package com.playzone.pems.infrastructure.persistence.venta.adapter;

import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.domain.venta.repository.VentaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.jpa.VentaJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.mapper.VentaEntityMapper;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VentaPersistenceAdapter implements VentaRepository {

    private final VentaJpaRepository ventaJpa;
    private final SedeJpaRepository  sedeJpa;
    private final VentaEntityMapper  mapper;

    @Override
    public Optional<Venta> findById(Long id) {
        return ventaJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Venta> findBySedeAndFechasBetween(Long idSede, OffsetDateTime desde,
                                                  OffsetDateTime hasta, Pageable pageable) {
        return ventaJpa.findBySede_IdAndCreatedAtBetween(
                        idSede,
                        desde,
                        hasta,
                        pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Venta> findByUsuario(UUID idUsuario, Pageable pageable) {
        return ventaJpa.findByCreatedBy(idUsuario, pageable).map(mapper::toDomain);
    }

    @Override
    public List<Venta> findByEventoId(Long eventoId) {
        return ventaJpa.findByEventoId(eventoId).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Venta save(Venta venta) {
        var sede = sedeJpa.findById(venta.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", venta.getIdSede()));
        return mapper.toDomain(ventaJpa.save(mapper.toEntity(venta, sede)));
    }
}
