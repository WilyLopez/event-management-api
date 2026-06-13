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

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlantillaPersistenceAdapter implements PlantillaEmailRepository {

    private final PlantillaEmailJpaRepository jpa;
    private final MarketingEntityMapper        mapper;

    @Override
    public Optional<PlantillaEmail> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<PlantillaEmail> findAll(Pageable pageable) {
        return jpa.findAll(pageable).map(mapper::toDomain);
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
                .esActiva(plantilla.isActiva())
                .createdBy(plantilla.getCreatedBy())
                .updatedBy(plantilla.getUpdatedBy())
                .build();
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }
}
