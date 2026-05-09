package com.playzone.pems.infrastructure.persistence.contrato.mapper;

import com.playzone.pems.domain.contrato.model.Contrato;
import com.playzone.pems.domain.contrato.model.ContratoProveedor;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoEntity;
import com.playzone.pems.infrastructure.persistence.contrato.entity.ContratoProveedorEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.proveedor.entity.ProveedorEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class ContratoEntityMapper {

    public Contrato toDomain(ContratoEntity e) {
        if (e == null) return null;
        var ev = e.getEventoPrivado();
        var cl = ev.getCliente();
        
        java.math.BigDecimal saldo = java.math.BigDecimal.ZERO;
        if (ev.getPrecioTotalContrato() != null) {
            saldo = ev.getPrecioTotalContrato().subtract(
                ev.getMontoAdelanto() != null ? ev.getMontoAdelanto() : java.math.BigDecimal.ZERO);
        }

        return Contrato.builder()
                .id(e.getId())
                .idEventoPrivado(ev.getId())
                .estado(e.getEstado())
                .contenidoTexto(e.getContenidoTexto())
                .archivoPdfUrl(e.getArchivoPdfUrl())
                .fechaFirma(e.getFechaFirma())
                .idUsuarioRedactor(e.getUsuarioRedactor().getId())
                .usuarioRedactor(e.getUsuarioRedactor().getNombre())
                .plantilla(e.getPlantilla())
                .observaciones(e.getObservaciones())
                .version(e.getVersion())
                .nombreCliente(cl.getNombre())
                .correoCliente(cl.getCorreo())
                .tipoEvento(ev.getTipoEvento())
                .fechaEvento(ev.getFechaEvento())
                .turno(ev.getTurno().getNombre())
                .aforoDeclarado(ev.getAforoDeclarado())
                .precioTotalContrato(ev.getPrecioTotalContrato())
                .montoAdelanto(ev.getMontoAdelanto())
                .saldoPendiente(saldo)
                .fechaCreacion(e.getFechaCreacion())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ContratoEntity toEntity(Contrato d,
                                   EventoPrivadoEntity evento,
                                   UsuarioAdminEntity redactor) {
        if (d == null) return null;
        return ContratoEntity.builder()
                .id(d.getId())
                .eventoPrivado(evento)
                .estado(d.getEstado())
                .contenidoTexto(d.getContenidoTexto())
                .archivoPdfUrl(d.getArchivoPdfUrl())
                .fechaFirma(d.getFechaFirma())
                .usuarioRedactor(redactor)
                .plantilla(d.getPlantilla())
                .observaciones(d.getObservaciones())
                .version(d.getVersion())
                .build();
    }

    public ContratoProveedor toDomain(ContratoProveedorEntity e) {
        if (e == null) return null;
        return ContratoProveedor.builder()
                .id(e.getId())
                .idContrato(e.getContrato().getId())
                .idProveedor(e.getProveedor().getId())
                .servicioDescripcion(e.getServicioDescripcion())
                .montoAcordado(e.getMontoAcordado())
                .contratadoPor(e.getContratadoPor())
                .build();
    }

    public ContratoProveedorEntity toEntity(ContratoProveedor d,
                                            ContratoEntity contrato,
                                            ProveedorEntity proveedor) {
        if (d == null) return null;
        return ContratoProveedorEntity.builder()
                .id(d.getId())
                .contrato(contrato)
                .proveedor(proveedor)
                .servicioDescripcion(d.getServicioDescripcion())
                .montoAcordado(d.getMontoAcordado())
                .contratadoPor(d.getContratadoPor())
                .build();
    }
}