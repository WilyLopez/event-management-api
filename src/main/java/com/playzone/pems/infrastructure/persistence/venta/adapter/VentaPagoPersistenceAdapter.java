package com.playzone.pems.infrastructure.persistence.venta.adapter;

import com.playzone.pems.domain.venta.model.VentaPago;
import com.playzone.pems.domain.venta.repository.VentaPagoRepository;
import com.playzone.pems.infrastructure.persistence.venta.jpa.VentaPagoJpaRepository;
import com.playzone.pems.infrastructure.persistence.venta.mapper.VentaPagoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VentaPagoPersistenceAdapter implements VentaPagoRepository {

    private final VentaPagoJpaRepository jpaRepository;
    private final VentaPagoEntityMapper  mapper;

    @Override
    public VentaPago save(VentaPago pago) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(pago)));
    }

    @Override
    public List<VentaPago> findByVentaId(Long ventaId) {
        return jpaRepository.findByVentaId(ventaId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public BigDecimal sumValidadosBySedeAndPeriodoAndMedioPago(
            Long idSede, int anio, int mes, String medioPago) {
        BigDecimal result = jpaRepository.sumBySedeAndPeriodoAndMedioPago(idSede, anio, mes, medioPago);
        return result != null ? result : BigDecimal.ZERO;
    }
}
