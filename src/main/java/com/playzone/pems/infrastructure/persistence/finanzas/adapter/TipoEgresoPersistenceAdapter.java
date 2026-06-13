package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.TipoEgreso;
import com.playzone.pems.domain.finanzas.model.enums.CategoriaEgreso;
import com.playzone.pems.domain.finanzas.repository.TipoEgresoRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.TipoEgresoJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.mapper.FinanzasEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TipoEgresoPersistenceAdapter implements TipoEgresoRepository {

    private final TipoEgresoJpaRepository jpaRepository;
    private final FinanzasEntityMapper    mapper;

    @Override
    public Optional<TipoEgreso> findById(String codigo) {
        return jpaRepository.findById(codigo).map(mapper::toDomain);
    }

    @Override
    public List<TipoEgreso> findAllActivos() {
        return jpaRepository.findByActivoTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TipoEgreso> findByCategoria(CategoriaEgreso categoria) {
        return jpaRepository.findByCategoriaAndActivoTrue(categoria).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public TipoEgreso save(TipoEgreso tipo) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(tipo)));
    }

    @Override
    @Transactional
    public void desactivar(String codigo) {
        jpaRepository.desactivar(codigo);
    }
}
