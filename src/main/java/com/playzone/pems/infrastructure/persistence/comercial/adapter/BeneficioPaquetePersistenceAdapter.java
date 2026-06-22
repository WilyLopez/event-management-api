package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.BeneficioPaquete;
import com.playzone.pems.domain.comercial.repository.BeneficioPaqueteRepository;
import com.playzone.pems.infrastructure.persistence.comercial.entity.BeneficioPaqueteEntity;
import com.playzone.pems.infrastructure.persistence.comercial.entity.PaqueteEventoEntity;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.BeneficioPaqueteJpaRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.PaqueteEventoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BeneficioPaquetePersistenceAdapter implements BeneficioPaqueteRepository {

    private final BeneficioPaqueteJpaRepository jpaRepo;
    private final PaqueteEventoJpaRepository paqueteJpaRepo;

    @Override
    public List<BeneficioPaquete> findByPaquete(Long idPaquete) {
        return jpaRepo.findByPaquete_IdOrderByOrdenAsc(idPaquete).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Optional<BeneficioPaquete> findById(Long id) {
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public BeneficioPaquete save(BeneficioPaquete b) {
        PaqueteEventoEntity paquete = paqueteJpaRepo.findById(b.getIdPaquete())
                .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + b.getIdPaquete()));
        
        BeneficioPaqueteEntity entity = BeneficioPaqueteEntity.builder()
                .id(b.getId())
                .paquete(paquete)
                .descripcion(b.getDescripcion())
                .orden(b.getOrden())
                .build();
        
        return toDomain(jpaRepo.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }

    private BeneficioPaquete toDomain(BeneficioPaqueteEntity e) {
        return BeneficioPaquete.builder()
                .id(e.getId())
                .idPaquete(e.getPaquete().getId())
                .descripcion(e.getDescripcion())
                .orden(e.getOrden())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
