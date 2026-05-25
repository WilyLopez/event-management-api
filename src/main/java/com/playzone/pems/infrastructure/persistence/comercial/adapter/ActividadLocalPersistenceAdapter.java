package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.ActividadLocal;
import com.playzone.pems.domain.comercial.repository.ActividadLocalRepository;
import com.playzone.pems.infrastructure.persistence.comercial.entity.ZonaJuegoEntity;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.ActividadLocalJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.ZonaJuegoJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.mapper.ActividadLocalEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ActividadLocalPersistenceAdapter implements ActividadLocalRepository {

    private final ActividadLocalJpaRepository jpaRepo;
    private final ZonaJuegoJpaRepository      zonaJpaRepo;
    private final ActividadLocalEntityMapper  mapper;

    @Override
    public Optional<ActividadLocal> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ActividadLocal> findAllActivas() {
        return jpaRepo.findByActivaTrueOrderByOrdenAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ActividadLocal> findEspeciales() {
        return jpaRepo.findByEsEspecialTrueAndActivaTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ActividadLocal> findByZona(Long idZona) {
        return jpaRepo.findByZona_Id(idZona).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public ActividadLocal save(ActividadLocal actividad) {
        ZonaJuegoEntity zona = actividad.getIdZona() != null
                ? zonaJpaRepo.findById(actividad.getIdZona()).orElse(null)
                : null;
        return mapper.toDomain(jpaRepo.save(mapper.toEntity(actividad, zona)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }
}
