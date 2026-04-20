package com.playzone.pems.infrastructure.persistence.venta.adapter;

import com.playzone.pems.domain.venta.model.Venta;
import com.playzone.pems.domain.venta.repository.VentaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.jpa.VentaJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.mapper.VentaEntityMapper;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VentaPersistenceAdapter implements VentaRepository {

    private final VentaJpaRepository          ventaJpa;
    private final SedeJpaRepository           sedeJpa;
    private final UsuarioAdminJpaRepository   adminJpa;
    private final ReservaPublicaJpaRepository reservaJpa;
    private final EventoPrivadoJpaRepository  eventoJpa;
    private final VentaEntityMapper           mapper;

    @Override
    public Optional<Venta> findById(Long id) {
        return ventaJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Venta> findBySedeAndFechasBetween(Long idSede, LocalDateTime desde,
                                                  LocalDateTime hasta, Pageable pageable) {
        return ventaJpa.findBySede_IdAndFechaVentaBetween(idSede, desde, hasta, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Venta> findByUsuario(Long idUsuario, Pageable pageable) {
        return ventaJpa.findByUsuario_Id(idUsuario, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Venta save(Venta venta) {
        var sede    = sedeJpa.findById(venta.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", venta.getIdSede()));
        var usuario = adminJpa.findById(venta.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", venta.getIdUsuario()));
        var reserva = venta.getIdReservaPublica() != null
                ? reservaJpa.findById(venta.getIdReservaPublica()).orElse(null) : null;
        var evento  = venta.getIdEventoPrivado() != null
                ? eventoJpa.findById(venta.getIdEventoPrivado()).orElse(null) : null;
        return mapper.toDomain(ventaJpa.save(mapper.toEntity(venta, sede, usuario, reserva, evento)));
    }
}