package com.playzone.pems.domain.facturacion.model;

import com.playzone.pems.domain.facturacion.model.enums.EstadoComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoComprobante;
import com.playzone.pems.domain.facturacion.model.enums.TipoDocReceptor;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Comprobante {

    private Long              id;
    private Long              idPago;
    private TipoComprobante   tipoComprobante;
    private EstadoComprobante estadoComprobante;
    private Long              idSerie;
    private String            serie;
    private String            correlativo;
    private String            numeroCompleto;
    private String            rucEmisor;
    private String            razonSocialEmisor;
    private TipoDocReceptor   tipoDocReceptor;
    private String            nroDocReceptor;
    private String            razonSocialReceptor;
    private String            direccionReceptor;
    private BigDecimal        montoBase;
    private BigDecimal        montoIgv;
    private BigDecimal        montoTotal;
    private String            xmlUrl;
    private String            pdfUrl;
    private String            hashSunat;
    private String            cdrEstado;
    private String            cdrDescripcion;
    private String            motivoAnulacion;
    private Long              idComprobanteNota;
    private LocalDateTime     fechaEmision;
    private LocalDateTime     fechaActualizacion;

    public boolean tieneValidezFiscal() {
        return estadoComprobante.tieneValidezFiscal();
    }

    public boolean esAnulable() {
        return estadoComprobante.esAnulable();
    }

    public boolean tieneCdr() {
        return cdrEstado != null && !cdrEstado.isBlank();
    }

    public boolean tienePdf() {
        return pdfUrl != null && !pdfUrl.isBlank();
    }

    public boolean montossonCoherentes() {
        BigDecimal esperado = montoBase.add(montoIgv);
        return montoTotal.compareTo(esperado) == 0;
    }

    public boolean receptorEsValido() {
        if (tipoComprobante.requiereRucReceptor()) {
            return TipoDocReceptor.RUC == tipoDocReceptor
                    && nroDocReceptor != null && !nroDocReceptor.isBlank();
        }
        return true;
    }
}