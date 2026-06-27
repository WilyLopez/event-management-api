package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.ArqueoCaja;
import com.playzone.pems.domain.finanzas.repository.ArqueoCajaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.ArqueoCajaEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.AperturaCajaJpaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.ArqueoCajaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArqueoCajaPersistenceAdapter implements ArqueoCajaRepository {

    private final ArqueoCajaJpaRepository  jpaRepository;
    private final AperturaCajaJpaRepository aperturaCajaJpaRepository;

    @Override
    @Transactional
    public ArqueoCaja save(ArqueoCaja arqueo) {
        ArqueoCajaEntity entity = ArqueoCajaEntity.builder()
                .id(arqueo.getId())
                .aperturaCaja(aperturaCajaJpaRepository.getReferenceById(arqueo.getIdAperturaCaja()))
                .saldoEsperado(arqueo.getSaldoEsperado())
                .saldoContado(arqueo.getSaldoContado())
                .diferencia(arqueo.getDiferencia())
                .observaciones(arqueo.getObservaciones())
                .realizadoPor(arqueo.getRealizadoPor())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<ArqueoCaja> findByApertura(Long idAperturaCaja) {
        return jpaRepository.findByAperturaCaja_IdOrderByFechaCreacionAsc(idAperturaCaja)
                .stream().map(this::toDomain).toList();
    }

    private ArqueoCaja toDomain(ArqueoCajaEntity e) {
        return ArqueoCaja.builder()
                .id(e.getId())
                .idAperturaCaja(e.getAperturaCaja().getId())
                .saldoEsperado(e.getSaldoEsperado())
                .saldoContado(e.getSaldoContado())
                .diferencia(e.getDiferencia())
                .observaciones(e.getObservaciones())
                .realizadoPor(e.getRealizadoPor())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
