package com.playzone.pems.infrastructure.persistence.facturacion.mapper;

import com.playzone.pems.domain.facturacion.model.Comprobante;
import com.playzone.pems.domain.facturacion.model.SerieComprobante;
import com.playzone.pems.infrastructure.persistence.facturacion.entity.ComprobanteEntity;
import com.playzone.pems.infrastructure.persistence.facturacion.entity.SerieComprobanteEntity;
import com.playzone.pems.infrastructure.persistence.pago.entity.PagoEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import org.springframework.stereotype.Component;

@Component
public class ComprobanteEntityMapper {

    public SerieComprobante toDomain(SerieComprobanteEntity e) {
        if (e == null) return null;
        return SerieComprobante.builder()
                .id(e.getId())
                .idSede(e.getSede().getId())
                .tipoComprobante(e.getTipoComprobante())
                .serie(e.getSerie())
                .correlativoActual(e.getCorrelativoActual())
                .activo(e.isActivo())
                .build();
    }

    public SerieComprobanteEntity toEntity(SerieComprobante d, SedeEntity sede) {
        if (d == null) return null;
        return SerieComprobanteEntity.builder()
                .id(d.getId())
                .sede(sede)
                .tipoComprobante(d.getTipoComprobante())
                .serie(d.getSerie())
                .correlativoActual(d.getCorrelativoActual())
                .activo(d.isActivo())
                .build();
    }

    public Comprobante toDomain(ComprobanteEntity e) {
        if (e == null) return null;
        return Comprobante.builder()
                .id(e.getId())
                .idPago(e.getPago().getId())
                .tipoComprobante(e.getTipoComprobante())
                .estadoComprobante(e.getEstadoComprobante())
                .idSerie(e.getSerie().getId())
                .serie(e.getSerieNum())
                .correlativo(e.getCorrelativo())
                .numeroCompleto(e.getNumeroCompleto())
                .rucEmisor(e.getRucEmisor())
                .razonSocialEmisor(e.getRazonSocialEmisor())
                .tipoDocReceptor(e.getTipoDocReceptor())
                .nroDocReceptor(e.getNroDocReceptor())
                .razonSocialReceptor(e.getRazonSocialReceptor())
                .direccionReceptor(e.getDireccionReceptor())
                .montoBase(e.getMontoBase())
                .montoIgv(e.getMontoIgv())
                .montoTotal(e.getMontoTotal())
                .xmlUrl(e.getXmlUrl())
                .pdfUrl(e.getPdfUrl())
                .hashSunat(e.getHashSunat())
                .cdrEstado(e.getCdrEstado())
                .cdrDescripcion(e.getCdrDescripcion())
                .motivoAnulacion(e.getMotivoAnulacion())
                .idComprobanteNota(e.getComprobanteNota() != null ? e.getComprobanteNota().getId() : null)
                .fechaEmision(e.getFechaEmision())
                .fechaActualizacion(e.getFechaActualizacion())
                .build();
    }

    public ComprobanteEntity toEntity(Comprobante d,
                                      PagoEntity pago,
                                      SerieComprobanteEntity serie,
                                      ComprobanteEntity nota) {
        if (d == null) return null;
        return ComprobanteEntity.builder()
                .id(d.getId())
                .pago(pago)
                .tipoComprobante(d.getTipoComprobante())
                .estadoComprobante(d.getEstadoComprobante())
                .serie(serie)
                .serieNum(d.getSerie())
                .correlativo(d.getCorrelativo())
                .numeroCompleto(d.getNumeroCompleto())
                .rucEmisor(d.getRucEmisor())
                .razonSocialEmisor(d.getRazonSocialEmisor())
                .tipoDocReceptor(d.getTipoDocReceptor())
                .nroDocReceptor(d.getNroDocReceptor())
                .razonSocialReceptor(d.getRazonSocialReceptor())
                .direccionReceptor(d.getDireccionReceptor())
                .montoBase(d.getMontoBase())
                .montoIgv(d.getMontoIgv())
                .montoTotal(d.getMontoTotal())
                .xmlUrl(d.getXmlUrl())
                .pdfUrl(d.getPdfUrl())
                .hashSunat(d.getHashSunat())
                .cdrEstado(d.getCdrEstado())
                .cdrDescripcion(d.getCdrDescripcion())
                .motivoAnulacion(d.getMotivoAnulacion())
                .comprobanteNota(nota)
                .build();
    }
}