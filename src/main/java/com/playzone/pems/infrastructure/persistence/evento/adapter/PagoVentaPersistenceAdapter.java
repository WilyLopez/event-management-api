package com.playzone.pems.infrastructure.persistence.evento.adapter;

import com.playzone.pems.domain.evento.model.PagoVenta;
import com.playzone.pems.domain.evento.repository.PagoVentaRepository;
import com.playzone.pems.infrastructure.persistence.evento.entity.PagoVentaEntity;
import com.playzone.pems.infrastructure.persistence.evento.jpa.PagoVentaJpaRepository;
import com.playzone.pems.infrastructure.persistence.evento.jpa.VentaPresencialJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PagoVentaPersistenceAdapter implements PagoVentaRepository {

    private final PagoVentaJpaRepository       pagoJpa;
    private final VentaPresencialJpaRepository ventaJpa;

    @Override
    public PagoVenta save(PagoVenta pago) {
        var venta = ventaJpa.findById(pago.getIdVenta())
                .orElseThrow(() -> new ResourceNotFoundException("VentaPresencial", pago.getIdVenta()));
        PagoVentaEntity entity = PagoVentaEntity.builder()
                .id(pago.getId())
                .venta(venta)
                .metodo(pago.getMetodo())
                .monto(pago.getMonto())
                .build();
        return toDomain(pagoJpa.save(entity));
    }

    @Override
    public List<PagoVenta> findByIdVenta(Long idVenta) {
        return pagoJpa.findByVenta_Id(idVenta).stream().map(this::toDomain).toList();
    }

    private PagoVenta toDomain(PagoVentaEntity e) {
        return PagoVenta.builder()
                .id(e.getId())
                .idVenta(e.getVenta().getId())
                .metodo(e.getMetodo())
                .monto(e.getMonto())
                .build();
    }
}
