package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.TipoIngreso;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;
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
    public Optional<TipoIngreso> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TipoIngreso> findActivoByCategoria(CategoriaIngreso categoria) {
        return jpaRepository.findFirstByCategoriaAndActivoTrue(categoria).map(this::toDomain);
    }

    @Override
    @Transactional
    public TipoIngreso save(TipoIngreso tipoIngreso) {
        TipoIngresoEntity entity = TipoIngresoEntity.builder()
                .id(tipoIngreso.getId())
                .nombre(tipoIngreso.getNombre())
                .descripcion(tipoIngreso.getDescripcion())
                .categoria(tipoIngreso.getCategoria())
                .activo(tipoIngreso.isActivo())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        jpaRepository.desactivar(id);
    }

    private TipoIngreso toDomain(TipoIngresoEntity e) {
        return TipoIngreso.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .categoria(e.getCategoria())
                .activo(e.isActivo())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
