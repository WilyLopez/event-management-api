package com.playzone.pems.infrastructure.persistence.marketing.adapter;

import com.playzone.pems.domain.marketing.model.TipoEmail;
import com.playzone.pems.domain.marketing.repository.TipoEmailRepository;
import com.playzone.pems.infrastructure.persistence.marketing.jpa.TipoEmailJpaRepository;
import com.playzone.pems.infrastructure.persistence.marketing.mapper.MarketingEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TipoEmailPersistenceAdapter implements TipoEmailRepository {

    private final TipoEmailJpaRepository jpa;
    private final MarketingEntityMapper  mapper;

    @Override
    public List<TipoEmail> findAllActivos() {
        return jpa.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TipoEmail> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TipoEmail> findByCodigo(String codigo) {
        return jpa.findByCodigo(codigo).map(mapper::toDomain);
    }
}
