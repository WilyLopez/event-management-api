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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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
        return jpaRepo.findByActivoTrueOrderByOrdenAsc().stream()
                .map(e -> mapper.toDomain(e, beneficioRepo.findByPaquete_IdOrderByOrdenAsc(e.getId())))
                .toList();
    }

    @Override
    public List<PaqueteEvento> findAll() {
        return jpaRepo.findAllByOrderByOrdenAsc().stream()
                .map(e -> mapper.toDomain(e, beneficioRepo.findByPaquete_IdOrderByOrdenAsc(e.getId())))
                .toList();
    }

    @Override
    @Transactional
    public PaqueteEvento save(PaqueteEvento paquete) {
        PaqueteEventoEntity entity = mapper.toEntity(paquete);
        PaqueteEventoEntity saved = jpaRepo.save(entity);
        beneficioRepo.deleteByPaquete_Id(saved.getId());
        if (paquete.getBeneficios() != null) {
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
        beneficioRepo.deleteByPaquete_Id(id);
        jpaRepo.deleteById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepo.existsBySlug(slug);
    }
}
