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

    @Override
    public Optional<Sede> findById(Long id) {
        return sedeJpaRepository.findById(id).map(sedeEntityMapper::toDomain);
    }

    @Override
    public List<Sede> findAllActivas() {
        return sedeJpaRepository.findByActivoTrue().stream()
                .map(sedeEntityMapper::toDomain)
                .toList();
    }

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
