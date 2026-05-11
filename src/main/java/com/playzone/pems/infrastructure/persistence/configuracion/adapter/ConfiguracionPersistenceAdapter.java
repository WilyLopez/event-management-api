package com.playzone.pems.infrastructure.persistence.configuracion.adapter;

import com.playzone.pems.domain.configuracion.model.ConfiguracionSistema;
import com.playzone.pems.domain.configuracion.repository.ConfiguracionSistemaRepository;
import com.playzone.pems.infrastructure.persistence.configuracion.entity.ConfiguracionSistemaEntity;
import com.playzone.pems.infrastructure.persistence.configuracion.jpa.ConfiguracionSistemaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConfiguracionPersistenceAdapter implements ConfiguracionSistemaRepository {

    private final ConfiguracionSistemaJpaRepository jpaRepo;

    @Override
    public List<ConfiguracionSistema> findAll() {
        return jpaRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<ConfiguracionSistema> findByClave(String clave) {
        return jpaRepo.findByClave(clave).map(this::toDomain);
    }

    @Override
    public ConfiguracionSistema save(ConfiguracionSistema config) {
        return toDomain(jpaRepo.save(toEntity(config)));
    }

    @Override
    public List<ConfiguracionSistema> saveAll(List<ConfiguracionSistema> configs) {
        return jpaRepo.saveAll(configs.stream().map(this::toEntity).toList())
                .stream().map(this::toDomain).toList();
    }

    private ConfiguracionSistema toDomain(ConfiguracionSistemaEntity e) {
        return ConfiguracionSistema.builder()
                .id(e.getId())
                .clave(e.getClave())
                .valor(e.getValor())
                .descripcion(e.getDescripcion())
                .tipo(e.getTipo())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    private ConfiguracionSistemaEntity toEntity(ConfiguracionSistema d) {
        return ConfiguracionSistemaEntity.builder()
                .id(d.getId())
                .clave(d.getClave())
                .valor(d.getValor())
                .descripcion(d.getDescripcion())
                .tipo(d.getTipo())
                .fechaActualizacion(d.getFechaActualizacion())
                .build();
    }
}
