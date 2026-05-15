package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.ConfiguracionPublica;
import com.playzone.pems.domain.cms.repository.ConfiguracionPublicaRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ConfiguracionPublicaJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConfiguracionPublicaPersistenceAdapter implements ConfiguracionPublicaRepository {

    private final ConfiguracionPublicaJpaRepository configJpa;
    private final CmsEntityMapper                   mapper;

    @Override
    public Optional<ConfiguracionPublica> findFirst() {
        return configJpa.findFirstByOrderByIdAsc().map(mapper::toDomain);
    }

    @Override
    public ConfiguracionPublica save(ConfiguracionPublica config) {
        return mapper.toDomain(configJpa.save(mapper.toEntity(config)));
    }
}
