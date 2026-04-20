package com.playzone.pems.infrastructure.persistence.fidelizacion.adapter;

import com.playzone.pems.domain.fidelizacion.model.HistorialFidelizacion;
import com.playzone.pems.domain.fidelizacion.repository.HistorialFidelizacionRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.ReservaPublicaJpaRepository;
import com.playzone.pems.infrastructure.persistence.fidelizacion.jpa.HistorialFidelizacionJpaRepository;
import com.playzone.pems.infrastructure.persistence.fidelizacion.mapper.HistorialFidelizacionEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.ClienteJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FidelizacionPersistenceAdapter implements HistorialFidelizacionRepository {

    private final HistorialFidelizacionJpaRepository historialJpa;
    private final ClienteJpaRepository               clienteJpa;
    private final ReservaPublicaJpaRepository        reservaJpa;
    private final HistorialFidelizacionEntityMapper  mapper;

    @Override public Optional<HistorialFidelizacion> findById(Long id) {
        return historialJpa.findById(id).map(mapper::toDomain);
    }

    @Override public Optional<HistorialFidelizacion> findByReservaPublica(Long idReservaPublica) {
        return historialJpa.findByReservaPublica_Id(idReservaPublica).map(mapper::toDomain);
    }

    @Override public Page<HistorialFidelizacion> findByCliente(Long idCliente, Pageable pageable) {
        return historialJpa.findByCliente_IdOrderByVisitaNumeroDesc(idCliente, pageable).map(mapper::toDomain);
    }

    @Override public int countVisitasByCliente(Long idCliente) {
        return historialJpa.countByCliente(idCliente);
    }

    @Override public boolean existsBeneficioAplicadoByCliente(Long idCliente, int visitaNumero) {
        return historialJpa.existsBeneficioAplicadoByClienteAndVisita(idCliente, visitaNumero);
    }

    @Override
    @Transactional
    public HistorialFidelizacion save(HistorialFidelizacion h) {
        var cliente  = clienteJpa.findById(h.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", h.getIdCliente()));
        var reserva  = h.getIdReservaPublica() != null
                ? reservaJpa.findById(h.getIdReservaPublica()).orElse(null) : null;
        return mapper.toDomain(historialJpa.save(mapper.toEntity(h, cliente, reserva)));
    }
}