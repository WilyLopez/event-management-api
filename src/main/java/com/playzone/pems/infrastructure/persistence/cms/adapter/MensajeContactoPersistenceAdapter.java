package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.MensajeContacto;
import com.playzone.pems.domain.cms.repository.MensajeContactoRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.MensajeContactoJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MensajeContactoPersistenceAdapter implements MensajeContactoRepository {

    private final MensajeContactoJpaRepository jpaRepo;
    private final CmsEntityMapper              mapper;

    @Override
    @Transactional
    public MensajeContacto save(MensajeContacto mensaje) {
        return mapper.toDomain(jpaRepo.save(mapper.toEntity(mensaje)));
    }

    @Override
    public Optional<MensajeContacto> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<MensajeContacto> findByEstado(String estado, Pageable pageable) {
        return jpaRepo.findByEstado(estado, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<MensajeContacto> findAll(Pageable pageable) {
        return jpaRepo.findAllNotDeleted(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepo.findById(id).ifPresent(entity -> {
            entity.setDeletedAt(java.time.OffsetDateTime.now());
            jpaRepo.save(entity);
        });
    }
}
