package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.Resena;
import com.playzone.pems.domain.cms.repository.ResenaRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ResenaJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResenaPersistenceAdapter implements ResenaRepository {

    private final ResenaJpaRepository resenaJpa;
    private final CmsEntityMapper     mapper;

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
        return mapper.toDomain(resenaJpa.save(mapper.toEntity(r)));
    }

    @Override
    public void deleteById(Long id) {
        resenaJpa.deleteById(id);
    }
}
