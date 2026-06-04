package com.playzone.pems.infrastructure.persistence.evento.mapper;

import com.playzone.pems.domain.evento.model.EventoPrivado;
import com.playzone.pems.infrastructure.persistence.calendario.entity.TurnoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class EventoPrivadoEntityMapper {

    public EventoPrivado toDomain(EventoPrivadoEntity e) {
        if (e == null) return null;
        return EventoPrivado.builder()
                .id(e.getId())
                .idCliente(e.getCliente().getId())
                .idSede(e.getSede().getId())
                .estado(e.getEstado())
                .idTurno(e.getTurno().getId())
                .codigoTurno(e.getTurno().getCodigo())
                .fechaEvento(e.getFechaEvento())
                .tipoEvento(e.getTipoEvento())
                .contactoAdicional(e.getContactoAdicional())
                .aforoDeclarado(e.getAforoDeclarado())
                .precioTotalContrato(e.getPrecioTotalContrato())
                .montoAdelanto(e.getMontoAdelanto())
                .motivoCancelacion(e.getMotivoCancelacion())
                .notasInternas(e.getNotasInternas())
                .nombreNino(e.getNombreNino())
                .edadCumple(e.getEdadCumple())
                .medioPagoAdelanto(e.getMedioPagoAdelanto())
                .observaciones(e.getObservaciones())
                .idPaquete(e.getIdPaquete())
                .descripcionPersonalizada(e.getDescripcionPersonalizada())
                .presupuestoEstimado(e.getPresupuestoEstimado())
                .esCotizacionPersonalizada(e.isEsCotizacionPersonalizada())
                .idUsuarioGestor(e.getUsuarioGestor() != null ? e.getUsuarioGestor().getId() : null)
                .estadoOperativo(e.getEstadoOperativo())
                .checklistCompleto(e.isChecklistCompleto())
                .horaInicioReal(e.getHoraInicioReal())
                .horaFinReal(e.getHoraFinReal())
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public EventoPrivadoEntity toEntity(EventoPrivado d,
                                         ClienteEntity cliente,
                                         SedeEntity sede,
                                         TurnoEntity turno,
                                         UsuarioAdminEntity gestor) {
        if (d == null) return null;
        return EventoPrivadoEntity.builder()
                .id(d.getId())
                .cliente(cliente)
                .sede(sede)
                .estado(d.getEstado())
                .turno(turno)
                .fechaEvento(d.getFechaEvento())
                .tipoEvento(d.getTipoEvento())
                .contactoAdicional(d.getContactoAdicional())
                .aforoDeclarado(d.getAforoDeclarado())
                .precioTotalContrato(d.getPrecioTotalContrato())
                .montoAdelanto(d.getMontoAdelanto())
                .motivoCancelacion(d.getMotivoCancelacion())
                .notasInternas(d.getNotasInternas())
                .nombreNino(d.getNombreNino())
                .edadCumple(d.getEdadCumple())
                .medioPagoAdelanto(d.getMedioPagoAdelanto())
                .observaciones(d.getObservaciones())
                .idPaquete(d.getIdPaquete())
                .descripcionPersonalizada(d.getDescripcionPersonalizada())
                .presupuestoEstimado(d.getPresupuestoEstimado())
                .esCotizacionPersonalizada(d.isEsCotizacionPersonalizada())
                .usuarioGestor(gestor)
                .estadoOperativo(d.getEstadoOperativo())
                .checklistCompleto(d.isChecklistCompleto())
                .horaInicioReal(d.getHoraInicioReal())
                .horaFinReal(d.getHoraFinReal())
                .build();
    }
}