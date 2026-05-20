package com.playzone.pems.infrastructure.persistence.marketing.adapter;

import com.playzone.pems.domain.marketing.model.CampanaEmail;
import com.playzone.pems.domain.marketing.repository.CampanaEmailRepository;
import com.playzone.pems.infrastructure.persistence.marketing.entity.CampanaEmailEntity;
import com.playzone.pems.infrastructure.persistence.marketing.entity.PlantillaEmailEntity;
import com.playzone.pems.infrastructure.persistence.marketing.jpa.CampanaEmailJpaRepository;
import com.playzone.pems.infrastructure.persistence.marketing.jpa.PlantillaEmailJpaRepository;
import com.playzone.pems.infrastructure.persistence.marketing.mapper.MarketingEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CampanaPersistenceAdapter implements CampanaEmailRepository {

    private final CampanaEmailJpaRepository  jpa;
    private final PlantillaEmailJpaRepository plantillaJpa;
    private final MarketingEntityMapper        mapper;

    @Override
    public Optional<CampanaEmail> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<CampanaEmail> findAll(Pageable pageable) {
        return jpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<CampanaEmail> findProgramadasParaEnviar() {
        return jpa.findProgramadasParaEnviar(Instant.now())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public CampanaEmail save(CampanaEmail campana) {
        PlantillaEmailEntity plantilla = plantillaJpa.getReferenceById(campana.getIdPlantillaEmail());
        CampanaEmailEntity entity = CampanaEmailEntity.builder()
                .id(campana.getId())
                .nombre(campana.getNombre())
                .descripcion(campana.getDescripcion())
                .plantillaEmail(plantilla)
                .estado(campana.getEstado())
                .fechaProgramada(campana.getFechaProgramada())
                .totalDestinatarios(campana.getTotalDestinatarios())
                .totalEnviados(campana.getTotalEnviados())
                .totalFallidos(campana.getTotalFallidos())
                .idUsuarioCreador(campana.getIdUsuarioCreador())
                .build();
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    @Transactional
    public void actualizarEstado(Long id, String estado) {
        jpa.actualizarEstado(id, estado);
    }

    @Override
    @Transactional
    public void incrementarEnviados(Long id, int cantidad) {
        jpa.incrementarEnviados(id, cantidad);
    }

    @Override
    @Transactional
    public void incrementarFallidos(Long id, int cantidad) {
        jpa.incrementarFallidos(id, cantidad);
    }
}
