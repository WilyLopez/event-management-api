package com.playzone.pems.infrastructure.persistence.pago.mapper;

import com.playzone.pems.domain.pago.model.Pago;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.pago.entity.PagoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoEntityMapper {

    public Pago toDomain(PagoEntity e) {
        if (e == null) return null;
        return Pago.builder()
                .id(e.getId())
                .medioPago(e.getMedioPago())
                .tipoPago(e.getTipoPago())
                .idReservaPublica(e.getReservaPublica() != null ? e.getReservaPublica().getId() : null)
                .idEventoPrivado(e.getEventoPrivado() != null ? e.getEventoPrivado().getId() : null)
                .idVenta(e.getIdVenta())
                .monto(e.getMonto())
                .referenciaPago(e.getReferenciaPago())
                .esParcial(e.isEsParcial())
                .idUsuarioRegistra(e.getUsuarioRegistra() != null ? e.getUsuarioRegistra().getId() : null)
                .fechaPago(e.getFechaPago())
                .build();
    }

    public PagoEntity toEntity(Pago d,
                               ReservaPublicaEntity reserva,
                               EventoPrivadoEntity evento,
                               UsuarioAdminEntity usuario) {
        if (d == null) return null;
        return PagoEntity.builder()
                .id(d.getId())
                .medioPago(d.getMedioPago())
                .tipoPago(d.getTipoPago())
                .reservaPublica(reserva)
                .eventoPrivado(evento)
                .idVenta(d.getIdVenta())
                .monto(d.getMonto())
                .referenciaPago(d.getReferenciaPago())
                .esParcial(d.isEsParcial())
                .usuarioRegistra(usuario)
                .build();
    }
}