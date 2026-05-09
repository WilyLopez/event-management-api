package com.playzone.pems.application.contrato.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class ContratoQuery {

    private Long        id;
    private Long        idEventoPrivado;
    private String      estado;
    private String      contenidoTexto;
    private String      archivoPdfUrl;
    private LocalDate   fechaFirma;
    private String      usuarioRedactor;
    private String      plantilla;
    private String      observaciones;
    private int         version;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    private String      nombreCliente;
    private String      correoCliente;
    private String      tipoEvento;
    private LocalDate   fechaEvento;
    private String      turno;
    private Integer     aforoDeclarado;
    private BigDecimal  precioTotalContrato;
    private BigDecimal  montoAdelanto;
    private BigDecimal  saldoPendiente;

    private List<DocumentoContratoQuery> documentos;
    private List<ActividadContratoQuery> actividades;
}