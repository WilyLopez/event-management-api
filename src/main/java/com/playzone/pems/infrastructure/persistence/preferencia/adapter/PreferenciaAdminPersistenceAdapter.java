package com.playzone.pems.infrastructure.persistence.preferencia.adapter;

import com.playzone.pems.domain.preferencia.model.PreferenciaAdmin;
import com.playzone.pems.domain.preferencia.repository.PreferenciaAdminRepository;
import com.playzone.pems.infrastructure.persistence.preferencia.jpa.PreferenciaAdminJpaRepository;
import com.playzone.pems.infrastructure.persistence.preferencia.mapper.PreferenciaAdminMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PreferenciaAdminPersistenceAdapter implements PreferenciaAdminRepository {

    private final PreferenciaAdminJpaRepository jpaRepository;
    private final UsuarioAdminJpaRepository     adminJpa;
    private final PreferenciaAdminMapper        mapper;

    @Override
    public Optional<PreferenciaAdmin> findByIdUsuarioAdmin(Long idUsuarioAdmin) {
        return jpaRepository.findByUsuarioAdminId(idUsuarioAdmin)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByIdUsuarioAdmin(Long idUsuarioAdmin) {
        return jpaRepository.existsByUsuarioAdminId(idUsuarioAdmin);
    }

    @Override
    @Transactional
    public PreferenciaAdmin save(PreferenciaAdmin domain) {
        var usuario = adminJpa.findById(domain.getIdUsuarioAdmin())
                .orElseThrow(() -> new ResourceNotFoundException("UsuarioAdmin", domain.getIdUsuarioAdmin()));
        var entity = mapper.toEntity(domain, usuario);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteByIdUsuarioAdmin(Long idUsuarioAdmin) {
        jpaRepository.deleteByUsuarioAdminId(idUsuarioAdmin);
    }
}
