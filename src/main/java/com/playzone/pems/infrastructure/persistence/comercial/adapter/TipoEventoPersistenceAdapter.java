package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.TipoEvento;
import com.playzone.pems.domain.comercial.repository.TipoEventoRepository;
import com.playzone.pems.infrastructure.persistence.comercial.entity.TipoEventoEntity;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.TipoEventoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TipoEventoPersistenceAdapter implements TipoEventoRepository {

    private final TipoEventoJpaRepository jpaRepo;

    @Override
    public List<TipoEvento> listarTodos() {
        return jpaRepo.findAllByOrderByOrdenAscNombreAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<TipoEvento> listarActivos() {
        return jpaRepo.findByActivoTrueOrderByOrdenAscNombreAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<TipoEvento> buscarPorCodigo(String codigo) {
        return jpaRepo.findById(codigo).map(this::toDomain);
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return jpaRepo.existsById(codigo);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return jpaRepo.existsByNombre(nombre);
    }

    @Override
    public boolean existePorNombreExcluyendo(String nombre, String codigoExcluido) {
        return jpaRepo.existsByNombreAndCodigoNot(nombre, codigoExcluido);
    }

    @Override
    public boolean tienePaquetesAsociados(String codigo) {
        return jpaRepo.tienePaquetesActivos(codigo);
    }

    @Override
    public TipoEvento guardar(TipoEvento tipoEvento) {
        TipoEventoEntity entity = TipoEventoEntity.builder()
                .codigo(tipoEvento.getCodigo())
                .nombre(tipoEvento.getNombre())
                .descripcion(tipoEvento.getDescripcion())
                .icono(tipoEvento.getIcono())
                .esSistema(tipoEvento.isEsSistema())
                .activo(tipoEvento.isActivo())
                .orden(tipoEvento.getOrden())
                .build();
        return toDomain(jpaRepo.save(entity));
    }

    @Override
    public void eliminar(String codigo) {
        jpaRepo.deleteById(codigo);
    }

    private TipoEvento toDomain(TipoEventoEntity e) {
        return TipoEvento.builder()
                .codigo(e.getCodigo())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .icono(e.getIcono())
                .esSistema(e.isEsSistema())
                .activo(e.isActivo())
                .orden(e.getOrden())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
