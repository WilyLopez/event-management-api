package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.Resena;
import com.playzone.pems.domain.cms.repository.ResenaRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ResenaJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import com.playzone.pems.infrastructure.persistence.evento.jpa.EventoPrivadoJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.ClienteJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResenaPersistenceAdapter implements ResenaRepository {

    private final ResenaJpaRepository          resenaJpa;
    private final ClienteJpaRepository         clienteJpa;
    private final UsuarioAdminJpaRepository    adminJpa;
    private final EventoPrivadoJpaRepository   eventoJpa;
    private final CmsEntityMapper              mapper;

    @Override
    public Optional<Resena> findById(Long id) {
        return resenaJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Resena> findAprobadas(Pageable pageable) {
        return resenaJpa.findByAprobadaTrueOrderByFechaCreacionDesc(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Resena> findPendientes(Pageable pageable) {
        return resenaJpa.findPendientes(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Resena> findAll(Pageable pageable) {
        return resenaJpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Resena save(Resena r) {
        var cliente = r.getIdCliente() != null
                ? clienteJpa.findById(r.getIdCliente()).orElse(null) : null;
        var aprueba = r.getIdUsuarioAprueba() != null
                ? adminJpa.findById(r.getIdUsuarioAprueba()).orElse(null) : null;
        var evento  = r.getIdEventoPrivado() != null
                ? eventoJpa.findById(r.getIdEventoPrivado()).orElse(null) : null;
        return mapper.toDomain(resenaJpa.save(mapper.toEntity(r, cliente, aprueba, evento)));
    }

    @Override
    public void deleteById(Long id) {
        resenaJpa.deleteById(id);
    }
}