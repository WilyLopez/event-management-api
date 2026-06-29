package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.TipoLegal;
import com.playzone.pems.domain.cms.repository.TipoLegalRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.TipoLegalJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TipoLegalPersistenceAdapter implements TipoLegalRepository {

    private final TipoLegalJpaRepository tipoLegalJpa;
    private final CmsEntityMapper        mapper;

    @Override
    public boolean existsByCodigo(String codigo) {
        return tipoLegalJpa.existsById(codigo);
    }

    @Override
    public Optional<TipoLegal> findByCodigo(String codigo) {
        return tipoLegalJpa.findById(codigo).map(mapper::toDomain);
    }

    @Override
    public Optional<TipoLegal> findBySlug(String slug) {
        return tipoLegalJpa.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public List<TipoLegal> findAllOrdenado() {
        return tipoLegalJpa.findAllByOrderByOrdenAsc().stream().map(mapper::toDomain).toList();
    }
}
