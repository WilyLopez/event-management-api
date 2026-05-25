package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.NovedadLocal;
import com.playzone.pems.domain.comercial.repository.NovedadLocalRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.NovedadLocalJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.mapper.NovedadLocalEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NovedadLocalPersistenceAdapter implements NovedadLocalRepository {

    private final NovedadLocalJpaRepository  jpaRepo;
    private final NovedadLocalEntityMapper   mapper;

    @Override
    public Optional<NovedadLocal> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<NovedadLocal> findAllActivas() {
        return jpaRepo.findByActivaTrueOrderByPrioridadAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<NovedadLocal> findVisiblesHome() {
        return jpaRepo.findByVisibleHomeTrueAndActivaTrueOrderByPrioridadAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public NovedadLocal save(NovedadLocal novedad) {
        return mapper.toDomain(jpaRepo.save(mapper.toEntity(novedad)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }
}
