package com.playzone.pems.infrastructure.persistence.evento.mapper;

import com.playzone.pems.domain.evento.model.ReservaPublica;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservaPublicaEntityMapper {

    public ReservaPublica toDomain(ReservaPublicaEntity e) {
        if (e == null) return null;
        return ReservaPublica.builder()
                .id(e.getId())
                .idCliente(e.getCliente().getId())
                .idSede(e.getSede().getId())
                .estado(e.getEstado())
                .canalReserva(e.getCanalReserva())
                .tipoDia(e.getTipoDia())
                .idReservaOriginal(e.getReservaOriginal() != null ? e.getReservaOriginal().getId() : null)
                .esReprogramacion(e.isEsReprogramacion())
                .vecesReprogramada(e.getVecesReprogramada())
                .fechaEvento(e.getFechaEvento())
                .numeroTicket(e.getNumeroTicket())
                .precioHistorico(e.getPrecioHistorico())
                .descuentoAplicado(e.getDescuentoAplicado())
                .totalPagado(e.getTotalPagado())
                .nombreNino(e.getNombreNino())
                .edadNino(e.getEdadNino())
                .nombreAcompanante(e.getNombreAcompanante())
                .dniAcompanante(e.getDniAcompanante())
                .firmoConsentimiento(e.isFirmoConsentimiento())
                .motivoCancelacion(e.getMotivoCancelacion())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ReservaPublicaEntity toEntity(ReservaPublica d,
                                         ClienteEntity cliente,
                                         SedeEntity sede,
                                         ReservaPublicaEntity reservaOriginal) {
        if (d == null) return null;
        return ReservaPublicaEntity.builder()
                .id(d.getId())
                .cliente(cliente)
                .sede(sede)
                .estado(d.getEstado())
                .canalReserva(d.getCanalReserva())
                .tipoDia(d.getTipoDia())
                .reservaOriginal(reservaOriginal)
                .esReprogramacion(d.isEsReprogramacion())
                .vecesReprogramada(d.getVecesReprogramada())
                .fechaEvento(d.getFechaEvento())
                .numeroTicket(d.getNumeroTicket())
                .precioHistorico(d.getPrecioHistorico())
                .descuentoAplicado(d.getDescuentoAplicado())
                .totalPagado(d.getTotalPagado())
                .nombreNino(d.getNombreNino())
                .edadNino(d.getEdadNino())
                .nombreAcompanante(d.getNombreAcompanante())
                .dniAcompanante(d.getDniAcompanante())
                .firmoConsentimiento(d.isFirmoConsentimiento())
                .motivoCancelacion(d.getMotivoCancelacion())
                .build();
    }
}