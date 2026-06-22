package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.PaqueteEvento;
import com.playzone.pems.domain.comercial.repository.PaqueteEventoRepository;
import com.playzone.pems.infrastructure.persistence.comercial.entity.BeneficioPaqueteEntity;
import com.playzone.pems.infrastructure.persistence.comercial.entity.PaqueteEventoEntity;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.BeneficioPaqueteJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.PaqueteEventoJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.mapper.PaqueteEventoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaqueteEventoPersistenceAdapter implements PaqueteEventoRepository {

    private final PaqueteEventoJpaRepository    jpaRepo;
    private final BeneficioPaqueteJpaRepository beneficioRepo;
    private final PaqueteEventoEntityMapper     mapper;

    @Override
    public Optional<PaqueteEvento> findById(Long id) {
        return jpaRepo.findById(id).map(e -> {
            List<BeneficioPaqueteEntity> beneficios = beneficioRepo.findByPaquete_IdOrderByOrdenAsc(id);
            return mapper.toDomain(e, beneficios);
        });
    }

    @Override
    public List<PaqueteEvento> findAllActivos() {
        List<PaqueteEventoEntity> entities = jpaRepo.findByEsActivoTrueAndDeletedAtIsNullOrderByOrdenAsc();
        return mapEntitiesToDomain(entities);
    }

    @Override
    public List<PaqueteEvento> findAll() {
        List<PaqueteEventoEntity> entities = jpaRepo.findByDeletedAtIsNullOrderByOrdenAsc();
        return mapEntitiesToDomain(entities);
    }

    private List<PaqueteEvento> mapEntitiesToDomain(List<PaqueteEventoEntity> entities) {
        if (entities.isEmpty()) return Collections.emptyList();
        List<Long> ids = entities.stream().map(PaqueteEventoEntity::getId).toList();
        Map<Long, List<BeneficioPaqueteEntity>> beneficiosMap = beneficioRepo.findByPaquete_IdInOrderByOrdenAsc(ids).stream()
                .collect(Collectors.groupingBy(b -> b.getPaquete().getId()));
        return entities.stream()
                .map(e -> mapper.toDomain(e, beneficiosMap.getOrDefault(e.getId(), Collections.emptyList())))
                .toList();
    }

    @Override
    @Transactional
    public PaqueteEvento save(PaqueteEvento paquete) {
        PaqueteEventoEntity entity = mapper.toEntity(paquete);
        PaqueteEventoEntity saved = jpaRepo.save(entity);

        if (paquete.getBeneficios() != null) {
            beneficioRepo.deleteByPaquete_Id(saved.getId());
            AtomicInteger idx = new AtomicInteger(0);
            List<BeneficioPaqueteEntity> beneficios = paquete.getBeneficios().stream()
                    .map(desc -> BeneficioPaqueteEntity.builder()
                            .paquete(saved)
                            .descripcion(desc)
                            .orden(idx.getAndIncrement())
                            .build())
                    .toList();
            beneficioRepo.saveAll(beneficios);
        }

        List<BeneficioPaqueteEntity> savedBeneficios = beneficioRepo.findByPaquete_IdOrderByOrdenAsc(saved.getId());
        return mapper.toDomain(saved, savedBeneficios);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepo.findById(id).ifPresent(entity -> {
            entity.setDeletedAt(OffsetDateTime.now());
            jpaRepo.save(entity);
        });
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepo.existsBySlugAndDeletedAtIsNull(slug);
    }
}
