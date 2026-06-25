package com.playzone.pems.interfaces.rest.contrato.mapper;

import com.playzone.pems.application.contrato.dto.query.ContratoQuery;
import com.playzone.pems.interfaces.rest.contrato.response.ContratoResponse;
import org.springframework.stereotype.Component;

@Component
public class ContratoResponseMapper {

    public ContratoResponse toResponse(ContratoQuery q) {
        return ContratoResponse.builder()
                .id(q.getId())
                .idEventoPrivado(q.getIdEventoPrivado())
                .estado(q.getEstado())
                .esEditable(q.isEsEditable())
                .contenidoTexto(q.getContenidoTexto())
                .archivoPdfUrl(q.getArchivoPdfUrl())
                .fechaFirma(q.getFechaFirma())
                .usuarioRedactor(q.getUsuarioRedactor())
                .plantilla(q.getPlantilla())
                .observaciones(q.getObservaciones())
                .version(q.getVersion())
                .nombreCliente(q.getNombreCliente())
                .correoCliente(q.getCorreoCliente())
                .tipoEvento(q.getTipoEvento())
                .fechaEvento(q.getFechaEvento())
                .turno(q.getTurno())
                .aforoDeclarado(q.getAforoDeclarado())
                .precioTotalContrato(q.getPrecioTotalContrato())
                .montoAdelanto(q.getMontoAdelanto())
                .saldoPendiente(q.getSaldoPendiente())
                .documentos(q.getDocumentos() == null ? null : q.getDocumentos().stream()
                        .map(d -> ContratoResponse.DocumentoContratoResponse.builder()
                                .id(d.getId())
                                .nombre(d.getNombre())
                                .archivoUrl(d.getArchivoUrl())
                                .tipoArchivo(d.getTipoArchivo())
                                .tamanobytes(d.getTamanobytes())
                                .usuarioCarga(d.getUsuarioCarga())
                                .fechaCarga(d.getFechaCarga())
                                .build()).toList())
                .actividades(q.getActividades() == null ? null : q.getActividades().stream()
                        .map(a -> ContratoResponse.ActividadContratoResponse.builder()
                                .id(a.getId())
                                .accion(a.getAccion())
                                .descripcion(a.getDescripcion())
                                .usuario(a.getUsuario())
                                .fechaAccion(a.getFechaAccion())
                                .build()).toList())
                .fechaCreacion(q.getFechaCreacion())
                .fechaActualizacion(q.getFechaActualizacion())
                .build();
    }
}
