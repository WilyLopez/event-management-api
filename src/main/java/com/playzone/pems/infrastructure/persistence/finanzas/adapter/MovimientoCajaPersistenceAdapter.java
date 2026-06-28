package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.MovimientoCaja;
import com.playzone.pems.domain.finanzas.repository.MovimientoCajaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.AperturaCajaEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.MovimientoCajaEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.AperturaCajaJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.MovimientoCajaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovimientoCajaPersistenceAdapter implements MovimientoCajaRepository {

    private final MovimientoCajaJpaRepository jpaRepository;
    private final AperturaCajaJpaRepository   aperturaCajaJpaRepository;

    @Override
    public List<MovimientoCaja> findByApertura(Long idAperturaCaja) {
        return jpaRepository.findByAperturaCaja_IdOrderByFechaCreacionAsc(idAperturaCaja)
                .stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public MovimientoCaja save(MovimientoCaja movimiento) {
        AperturaCajaEntity apertura = aperturaCajaJpaRepository
                .getReferenceById(movimiento.getIdAperturaCaja());
        MovimientoCajaEntity entity = MovimientoCajaEntity.builder()
                .id(movimiento.getId())
                .aperturaCaja(apertura)
                .tipo(movimiento.getTipo())
                .concepto(movimiento.getConcepto())
                .monto(movimiento.getMonto())
                .medioPago(movimiento.getMedioPago())
                .categoriaRetiro(movimiento.getCategoriaRetiro())
                .idRegistroIngreso(movimiento.getIdRegistroIngreso())
                .idRegistroEgreso(movimiento.getIdRegistroEgreso())
                .ventaId(movimiento.getIdVenta())
                .esManual(movimiento.isEsManual())
                .createdBy(movimiento.getIdUsuarioRegistra())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private MovimientoCaja toDomain(MovimientoCajaEntity e) {
        return MovimientoCaja.builder()
                .id(e.getId())
                .idAperturaCaja(e.getAperturaCaja().getId())
                .tipo(e.getTipo())
                .concepto(e.getConcepto())
                .monto(e.getMonto())
                .medioPago(e.getMedioPago())
                .categoriaRetiro(e.getCategoriaRetiro())
                .idRegistroIngreso(e.getIdRegistroIngreso())
                .idRegistroEgreso(e.getIdRegistroEgreso())
                .idVenta(e.getVentaId())
                .esManual(e.isEsManual())
                .idUsuarioRegistra(e.getCreatedBy())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
