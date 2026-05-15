package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.SeccionWeb;
import com.playzone.pems.domain.cms.repository.SeccionWebRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.SeccionWebJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SeccionWebPersistenceAdapter implements SeccionWebRepository {

    private final SeccionWebJpaRepository seccionWebJpa;
    private final CmsEntityMapper         mapper;

    @Override
    public Optional<SeccionWeb> findById(Long id) {
        return seccionWebJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<SeccionWeb> findByCodigo(String codigo) {
        return seccionWebJpa.findByCodigo(codigo).map(mapper::toDomain);
    }

    @Override
    public List<SeccionWeb> findAll() {
        return seccionWebJpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SeccionWeb> findVisibles() {
        return seccionWebJpa.findByVisibleTrueOrderByOrdenVisualizacionAsc()
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public SeccionWeb save(SeccionWeb seccionWeb) {
        return mapper.toDomain(seccionWebJpa.save(mapper.toEntity(seccionWeb)));
    }

    @Override
    public void deleteById(Long id) {
        seccionWebJpa.deleteById(id);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return seccionWebJpa.existsByCodigo(codigo);
    }
}
