package com.playzone.pems.infrastructure.persistence.marketing.adapter;

import com.playzone.pems.domain.marketing.model.TipoEmail;
import com.playzone.pems.domain.marketing.repository.TipoEmailRepository;
import com.playzone.pems.infrastructure.persistence.marketing.entity.TipoEmailEntity;
import com.playzone.pems.infrastructure.persistence.marketing.jpa.TipoEmailJpaRepository;
import com.playzone.pems.infrastructure.persistence.marketing.mapper.MarketingEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TipoEmailPersistenceAdapter implements TipoEmailRepository {

    private final TipoEmailJpaRepository jpa;
    private final MarketingEntityMapper  mapper;

    @Override
    public List<TipoEmail> findAllActivos() {
        return jpa.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TipoEmail> findById(String codigo) {
        return jpa.findById(codigo).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public TipoEmail save(TipoEmail tipo) {
        TipoEmailEntity entity = TipoEmailEntity.builder()
                .codigo(tipo.getCodigo())
                .nombre(tipo.getNombre())
                .descripcion(tipo.getDescripcion())
                .esSistema(false)
                .orden(tipo.getOrden())
                .activo(true)
                .build();
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(String codigo) {
        jpa.deleteById(codigo);
    }
}
