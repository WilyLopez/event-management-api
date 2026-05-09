package com.playzone.pems.infrastructure.persistence.contrato.adapter;

import com.playzone.pems.domain.contrato.model.enums.EstadoContrato;
import com.playzone.pems.domain.contrato.model.Contrato;
import com.playzone.pems.domain.contrato.repository.ContratoRepository;
import com.playzone.pems.infrastructure.persistence.contrato.jpa.ContratoJpaRepository;
import com.playzone.pems.infrastructure.persistence.contrato.mapper.ContratoEntityMapper;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContratoPersistenceAdapter implements ContratoRepository {

    private final ContratoJpaRepository      contratoJpa;
    private final EventoPrivadoJpaRepository eventoJpa;
    private final UsuarioAdminJpaRepository  adminJpa;
    private final ContratoEntityMapper       mapper;

    @Override
    public Optional<Contrato> findById(Long id) {
        return contratoJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Contrato> findByEventoPrivado(Long idEventoPrivado) {
        return contratoJpa.findByEventoPrivado_Id(idEventoPrivado).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Contrato save(Contrato contrato) {
        var evento = eventoJpa.findById(contrato.getIdEventoPrivado())
                .orElseThrow(() -> new ResourceNotFoundException("EventoPrivado", contrato.getIdEventoPrivado()));
        var redactor = adminJpa.findById(contrato.getIdUsuarioRedactor())
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", contrato.getIdUsuarioRedactor()));
        return mapper.toDomain(contratoJpa.save(mapper.toEntity(contrato, evento, redactor)));
    }

    @Override
    public boolean existsByEventoPrivado(Long idEventoPrivado) {
        return contratoJpa.existsByEventoPrivado_Id(idEventoPrivado);
    }

    @Override
    public boolean existsById(Long id) {
        return contratoJpa.existsById(id);
    }

    @Override
    public Page<Contrato> buscarConFiltros(String search, String estado, Long idSede, Pageable pageable) {
        EstadoContrato estadoEnum = null;
        if (estado != null && !estado.isBlank()) {
            try {
                estadoEnum = EstadoContrato.valueOf(estado);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return contratoJpa.buscarConFiltros(search, estadoEnum, idSede, pageable)
                .map(mapper::toDomain);
    }
}