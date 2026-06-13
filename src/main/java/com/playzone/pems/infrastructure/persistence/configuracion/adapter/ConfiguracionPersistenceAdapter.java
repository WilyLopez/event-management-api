package com.playzone.pems.infrastructure.persistence.configuracion.adapter;

import com.playzone.pems.domain.configuracion.model.ConfiguracionGlobal;
import com.playzone.pems.domain.configuracion.repository.ConfiguracionGlobalRepository;
import com.playzone.pems.infrastructure.persistence.configuracion.entity.ConfiguracionGlobalEntity;
import com.playzone.pems.infrastructure.persistence.configuracion.jpa.ConfiguracionGlobalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConfiguracionPersistenceAdapter implements ConfiguracionGlobalRepository {

    private final ConfiguracionGlobalJpaRepository jpaRepo;

    @Override
    public List<ConfiguracionGlobal> findAll() {
        return jpaRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<ConfiguracionGlobal> findByClave(String clave) {
        return jpaRepo.findById(clave).map(this::toDomain);
    }

    @Override
    public ConfiguracionGlobal save(ConfiguracionGlobal config) {
        return toDomain(jpaRepo.save(toEntity(config)));
    }

    @Override
    public List<ConfiguracionGlobal> saveAll(List<ConfiguracionGlobal> configs) {
        return jpaRepo.saveAll(configs.stream().map(this::toEntity).toList())
                .stream().map(this::toDomain).toList();
    }

    private ConfiguracionGlobal toDomain(ConfiguracionGlobalEntity e) {
        return ConfiguracionGlobal.builder()
                .clave(e.getClave())
                .valor(e.getValor())
                .descripcion(e.getDescripcion())
                .tipo(e.getTipo())
                .esSecreto(e.isEsSecreto())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private ConfiguracionGlobalEntity toEntity(ConfiguracionGlobal d) {
        return ConfiguracionGlobalEntity.builder()
                .clave(d.getClave())
                .valor(d.getValor())
                .descripcion(d.getDescripcion())
                .tipo(d.getTipo())
                .esSecreto(d.isEsSecreto())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
