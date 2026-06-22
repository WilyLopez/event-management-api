package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.ServicioCotizacion;
import com.playzone.pems.domain.comercial.repository.ServicioCotizacionRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.ServicioCotizacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import com.playzone.pems.infrastructure.persistence.comercial.entity.ServicioCotizacionEntity;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServicioCotizacionPersistenceAdapter implements ServicioCotizacionRepository {

    private final ServicioCotizacionJpaRepository jpaRepo;

    @Override
    public List<ServicioCotizacion> findAllActivos() {
        return jpaRepo.findByActivoTrueOrderByOrdenAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ServicioCotizacion> findAll() {
        return jpaRepo.findAllByOrderByOrdenAsc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<ServicioCotizacion> findById(Long id) {
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public ServicioCotizacion save(ServicioCotizacion s) {
        ServicioCotizacionEntity entity = ServicioCotizacionEntity.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .precioReferencial(s.getPrecioReferencial())
                .icono(s.getIcono())
                .activo(s.isActivo())
                .orden(s.getOrden())
                .build();
        return toDomain(jpaRepo.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }

    private ServicioCotizacion toDomain(ServicioCotizacionEntity e) {
        return ServicioCotizacion.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .precioReferencial(e.getPrecioReferencial())
                .icono(e.getIcono())
                .activo(e.isActivo())
                .orden(e.getOrden())
                .build();
    }
}
