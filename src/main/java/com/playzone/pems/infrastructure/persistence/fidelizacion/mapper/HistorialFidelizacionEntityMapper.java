package com.playzone.pems.infrastructure.persistence.fidelizacion.mapper;

import com.playzone.pems.domain.fidelizacion.model.HistorialFidelizacion;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.fidelizacion.entity.HistorialFidelizacionEntity;
import org.springframework.stereotype.Component;

@Component
public class HistorialFidelizacionEntityMapper {

    public HistorialFidelizacion toDomain(HistorialFidelizacionEntity e) {
        if (e == null) return null;
        return HistorialFidelizacion.builder()
                .id(e.getId())
                .idCliente(e.getClienteId())
                .idReservaPublica(e.getReservaPublica().getId())
                .visitaNumero(e.getVisitaNumero())
                .esBeneficioAplicado(e.isEsBeneficioAplicado())
                .fechaRegistro(e.getFechaRegistro())
                .build();
    }

    public HistorialFidelizacionEntity toEntity(HistorialFidelizacion d, ReservaPublicaEntity reserva) {
        if (d == null) return null;
        return HistorialFidelizacionEntity.builder()
                .id(d.getId())
                .clienteId(d.getIdCliente())
                .reservaPublica(reserva)
                .visitaNumero(d.getVisitaNumero())
                .esBeneficioAplicado(d.isEsBeneficioAplicado())
                .build();
    }
}
