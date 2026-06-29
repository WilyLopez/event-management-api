package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.ContenidoLegalHistorial;
import com.playzone.pems.domain.cms.repository.ContenidoLegalHistorialRepository;
import com.playzone.pems.infrastructure.persistence.cms.entity.ContenidoLegalHistorialEntity;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ContenidoLegalHistorialJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContenidoLegalHistorialPersistenceAdapter implements ContenidoLegalHistorialRepository {

    private final ContenidoLegalHistorialJpaRepository historialJpa;
    private final CmsEntityMapper                       mapper;

    @Override
    @Transactional
    public void guardar(ContenidoLegalHistorial historial) {
        historialJpa.save(ContenidoLegalHistorialEntity.builder()
                .legalId(historial.getLegalId())
                .tipo(historial.getTipo())
                .titulo(historial.getTitulo())
                .contenido(historial.getContenido())
                .version(historial.getVersion())
                .createdBy(historial.getCreatedBy())
                .build());
    }

    @Override
    public List<ContenidoLegalHistorial> findByTipo(String tipo) {
        return historialJpa.findByTipoOrderByVersionDesc(tipo).stream()
                .map(mapper::toDomain).toList();
    }
}
