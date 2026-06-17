package com.playzone.pems.infrastructure.persistence.fidelizacion.adapter;

import com.playzone.pems.domain.fidelizacion.model.FidelizacionConfig;
import com.playzone.pems.domain.fidelizacion.repository.FidelizacionConfigRepository;
import com.playzone.pems.infrastructure.persistence.fidelizacion.entity.FidelizacionConfigEntity;
import com.playzone.pems.infrastructure.persistence.fidelizacion.jpa.FidelizacionConfigJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FidelizacionConfigPersistenceAdapter implements FidelizacionConfigRepository {
    private final FidelizacionConfigJpaRepository jpaRepository;

    @Override
    public Optional<FidelizacionConfig> findByIdSede(Long idSede) {
        return jpaRepository.findByIdSede(idSede).map(this::toDomain);
    }

    @Override
    public FidelizacionConfig save(FidelizacionConfig config) {
        FidelizacionConfigEntity entity = jpaRepository.findByIdSede(config.getIdSede())
                .orElse(new FidelizacionConfigEntity());
        
        entity.setIdSede(config.getIdSede());
        entity.setUmbral(config.getUmbral());
        
        return toDomain(jpaRepository.save(entity));
    }

    private FidelizacionConfig toDomain(FidelizacionConfigEntity e) {
        return FidelizacionConfig.builder()
                .id(e.getId())
                .idSede(e.getIdSede())
                .umbral(e.getUmbral())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
