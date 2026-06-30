package com.playzone.pems.infrastructure.persistence.usuario.adapter;

import com.playzone.pems.domain.usuario.model.Sede;
import com.playzone.pems.domain.usuario.repository.SedeRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.mapper.SedeEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SedePersistenceAdapter implements SedeRepository {

    private final SedeJpaRepository sedeJpaRepository;
    private final SedeEntityMapper  sedeEntityMapper;

    @org.springframework.cache.annotation.Cacheable("sedes")
    @Override
    public Optional<Sede> findById(Long id) {
        return sedeJpaRepository.findById(id).map(sedeEntityMapper::toDomain);
    }

    @Override
    public List<Sede> findAllActivas() {
        return sedeJpaRepository.findByDeletedAtIsNull().stream()
                .map(sedeEntityMapper::toDomain)
                .toList();
    }

    @org.springframework.cache.annotation.CacheEvict(value = "sedes", allEntries = true)
    @Override
    public Sede save(Sede sede) {
        return sedeEntityMapper.toDomain(
                sedeJpaRepository.save(sedeEntityMapper.toEntity(sede))
        );
    }

    @Override
    public boolean existsByRuc(String ruc) {
        return sedeJpaRepository.existsByRuc(ruc);
    }
}
