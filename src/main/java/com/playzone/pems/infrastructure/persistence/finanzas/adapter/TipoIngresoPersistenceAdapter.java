package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.TipoIngreso;
import com.playzone.pems.domain.finanzas.repository.TipoIngresoRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.TipoIngresoEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.TipoIngresoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TipoIngresoPersistenceAdapter implements TipoIngresoRepository {

    private final TipoIngresoJpaRepository jpaRepository;

    @Override
    public List<TipoIngreso> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<TipoIngreso> findById(String codigo) {
        return jpaRepository.findById(codigo).map(this::toDomain);
    }

    @Override
    @Transactional
    public TipoIngreso save(TipoIngreso tipoIngreso) {
        TipoIngresoEntity entity = TipoIngresoEntity.builder()
                .codigo(tipoIngreso.getCodigo())
                .nombre(tipoIngreso.getNombre())
                .descripcion(tipoIngreso.getDescripcion())
                .esSistema(tipoIngreso.isEsSistema())
                .orden(tipoIngreso.getOrden())
                .activo(tipoIngreso.isActivo())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void desactivar(String codigo) {
        jpaRepository.desactivar(codigo);
    }

    private TipoIngreso toDomain(TipoIngresoEntity e) {
        return TipoIngreso.builder()
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .esSistema(e.isEsSistema())
                .orden(e.getOrden())
                .activo(e.isActivo())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt() : null)
                .updatedAt(e.getUpdatedAt() != null ? e.getUpdatedAt() : null)
                .build();
    }
}
