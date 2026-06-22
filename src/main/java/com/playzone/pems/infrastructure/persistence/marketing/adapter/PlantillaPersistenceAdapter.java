package com.playzone.pems.infrastructure.persistence.marketing.adapter;

import com.playzone.pems.domain.marketing.model.PlantillaEmail;
import com.playzone.pems.domain.marketing.repository.PlantillaEmailRepository;
import com.playzone.pems.infrastructure.persistence.marketing.entity.PlantillaEmailEntity;
import com.playzone.pems.infrastructure.persistence.marketing.jpa.PlantillaEmailJpaRepository;
import com.playzone.pems.infrastructure.persistence.marketing.mapper.MarketingEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlantillaPersistenceAdapter implements PlantillaEmailRepository {

    private final PlantillaEmailJpaRepository jpa;
    private final MarketingEntityMapper        mapper;

    @Override
    public Optional<PlantillaEmail> findById(Long id) {
        return jpa.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(mapper::toDomain);
    }

    @Override
    public List<PlantillaEmail> findAllById(List<Long> ids) {
        return jpa.findAllById(ids).stream()
                .filter(e -> e.getDeletedAt() == null)
                .map(mapper::toDomain).toList();
    }

    @Override
    public Page<PlantillaEmail> findAll(Pageable pageable) {
        return jpa.findAllActivas(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<PlantillaEmail> findAllMarketing(Pageable pageable) {
        return jpa.findAllMarketing(pageable).map(mapper::toDomain);
    }

    @Override
    public List<PlantillaEmail> findAllActivasByTipo(String tipoEmailCodigo) {
        return jpa.findByTipoEmailCodigoAndEsActivaTrue(tipoEmailCodigo)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public PlantillaEmail save(PlantillaEmail plantilla) {
        PlantillaEmailEntity entity = PlantillaEmailEntity.builder()
                .id(plantilla.getId())
                .tipoEmailCodigo(plantilla.getTipoEmailCodigo())
                .nombre(plantilla.getNombre())
                .asunto(plantilla.getAsunto())
                .contenidoHtml(plantilla.getContenidoHtml())
                .contenidoFallback(plantilla.getContenidoFallback())
                .variablesPermitidas(plantilla.getVariablesPermitidas())
                .contenidoBloques(plantilla.getContenidoBloques())
                .esActiva(plantilla.isActiva())
                .createdBy(plantilla.getCreatedBy())
                .updatedBy(plantilla.getUpdatedBy())
                .build();
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        jpa.softDelete(id, OffsetDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }
}
