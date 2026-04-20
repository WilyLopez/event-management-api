package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.ContenidoWeb;
import com.playzone.pems.domain.cms.repository.ContenidoWebRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ContenidoWebJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContenidoWebPersistenceAdapter implements ContenidoWebRepository {

    private final ContenidoWebJpaRepository  contenidoJpa;
    private final UsuarioAdminJpaRepository  adminJpa;
    private final CmsEntityMapper            mapper;

    @Override
    public Optional<ContenidoWeb> findById(Long id) {
        return contenidoJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ContenidoWeb> findBySeccionAndClave(Long idSeccion, String clave) {
        return contenidoJpa.findByIdSeccionAndClave(idSeccion, clave).map(mapper::toDomain);
    }

    @Override
    public List<ContenidoWeb> findActivosBySeccion(Long idSeccion) {
        return contenidoJpa.findByIdSeccionAndActivoTrue(idSeccion).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ContenidoWeb> findAllActivos() {
        return contenidoJpa.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public ContenidoWeb save(ContenidoWeb c) {
        var editor = c.getIdUsuarioEditor() != null
                ? adminJpa.findById(c.getIdUsuarioEditor()).orElse(null) : null;
        return mapper.toDomain(contenidoJpa.save(mapper.toEntity(c, editor)));
    }

    @Override
    public boolean existsBySeccionAndClave(Long idSeccion, String clave) {
        return contenidoJpa.existsByIdSeccionAndClave(idSeccion, clave);
    }
}