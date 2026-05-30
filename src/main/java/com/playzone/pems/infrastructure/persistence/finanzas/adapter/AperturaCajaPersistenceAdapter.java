package com.playzone.pems.infrastructure.persistence.finanzas.adapter;

import com.playzone.pems.domain.finanzas.model.AperturaCaja;
import com.playzone.pems.domain.finanzas.repository.AperturaCajaRepository;
import com.playzone.pems.infrastructure.persistence.finanzas.entity.AperturaCajaEntity;
import com.playzone.pems.infrastructure.persistence.finanzas.jpa.AperturaCajaJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AperturaCajaPersistenceAdapter implements AperturaCajaRepository {

    private final AperturaCajaJpaRepository jpaRepository;
    private final SedeJpaRepository         sedeJpaRepository;

    @Override
    public Optional<AperturaCaja> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AperturaCaja> findBySedeAndFecha(Long idSede, LocalDate fecha) {
        return jpaRepository.findBySede_IdAndFecha(idSede, fecha).map(this::toDomain);
    }

    @Override
    public List<AperturaCaja> findBySedeAndRango(Long idSede, LocalDate inicio, LocalDate fin) {
        return jpaRepository.findBySede_IdAndFechaBetweenOrderByFechaAsc(idSede, inicio, fin)
                .stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public AperturaCaja save(AperturaCaja apertura) {
        SedeEntity sede = sedeJpaRepository.getReferenceById(apertura.getIdSede());
        AperturaCajaEntity entity = AperturaCajaEntity.builder()
                .id(apertura.getId())
                .sede(sede)
                .fecha(apertura.getFecha())
                .saldoInicial(apertura.getSaldoInicial())
                .saldoFinal(apertura.getSaldoFinal())
                .totalIngresos(apertura.getTotalIngresos())
                .totalEgresos(apertura.getTotalEgresos())
                .estado(apertura.getEstado())
                .idUsuarioApertura(apertura.getIdUsuarioApertura())
                .idUsuarioCierre(apertura.getIdUsuarioCierre())
                .fechaApertura(apertura.getFechaApertura())
                .fechaCierre(apertura.getFechaCierre())
                .observaciones(apertura.getObservaciones())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    private AperturaCaja toDomain(AperturaCajaEntity e) {
        return AperturaCaja.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .fecha(e.getFecha())
                .saldoInicial(e.getSaldoInicial())
                .saldoFinal(e.getSaldoFinal())
                .totalIngresos(e.getTotalIngresos())
                .totalEgresos(e.getTotalEgresos())
                .estado(e.getEstado())
                .idUsuarioApertura(e.getIdUsuarioApertura())
                .idUsuarioCierre(e.getIdUsuarioCierre())
                .fechaApertura(e.getFechaApertura())
                .fechaCierre(e.getFechaCierre())
                .observaciones(e.getObservaciones())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}
