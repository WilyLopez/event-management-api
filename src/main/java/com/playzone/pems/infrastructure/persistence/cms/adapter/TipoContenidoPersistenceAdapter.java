package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.TipoContenido;
import com.playzone.pems.domain.cms.repository.TipoContenidoRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.TipoContenidoJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TipoContenidoPersistenceAdapter implements TipoContenidoRepository {

    private final TipoContenidoJpaRepository tipoContenidoJpa;
    private final CmsEntityMapper            mapper;

    @Override
    public List<TipoContenido> findAll() {
        return tipoContenidoJpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TipoContenido> findByCodigo(String codigo) {
        return tipoContenidoJpa.findByCodigo(codigo).map(mapper::toDomain);
    }
}
