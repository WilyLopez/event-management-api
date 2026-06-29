package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.ContenidoLegal;
import com.playzone.pems.domain.cms.repository.ContenidoLegalRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ContenidoLegalJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContenidoLegalPersistenceAdapter implements ContenidoLegalRepository {

    private final ContenidoLegalJpaRepository legalJpa;
    private final CmsEntityMapper             mapper;

    @Override
    public Optional<ContenidoLegal> findById(Long id) {
        return legalJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ContenidoLegal> findByTipo(String tipo) {
        return legalJpa.findByTipo(tipo).map(mapper::toDomain);
    }

    @Override
    public Optional<ContenidoLegal> findActivoByTipo(String tipo) {
        return legalJpa.findByTipoAndActivoTrue(tipo).map(mapper::toDomain);
    }

    @Override
    public List<ContenidoLegal> findActivos() {
        return legalJpa.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ContenidoLegal> findAll() {
        return legalJpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public ContenidoLegal save(ContenidoLegal legal) {
        return mapper.toDomain(legalJpa.save(mapper.toEntity(legal)));
    }

    @Override
    @Transactional
    public void deleteByTipo(String tipo) {
        legalJpa.deleteByTipo(tipo);
    }
}
