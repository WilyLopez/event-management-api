package com.playzone.pems.infrastructure.persistence.comercial.adapter;

import com.playzone.pems.domain.comercial.model.ServicioCotizacion;
import com.playzone.pems.domain.comercial.repository.ServicioCotizacionRepository;
import com.playzone.pems.infrastructure.persistence.comercial.jpa.ServicioCotizacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServicioCotizacionPersistenceAdapter implements ServicioCotizacionRepository {

    private final ServicioCotizacionJpaRepository jpaRepo;

    @Override
    public List<ServicioCotizacion> findAllActivos() {
        return jpaRepo.findByActivoTrueOrderByOrdenAsc()
                .stream()
                .map(e -> ServicioCotizacion.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .descripcion(e.getDescripcion())
                        .precioReferencial(e.getPrecioReferencial())
                        .icono(e.getIcono())
                        .build())
                .toList();
    }
}
