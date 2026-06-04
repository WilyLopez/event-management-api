package com.playzone.pems.domain.evento.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class VentaPresencial {

    private Long          id;
    private Long          idSede;
    private Long          idCliente;
    private LocalDate     fechaVisita;
    private String        nombreAcompanante;
    private String        dniAcompanante;
    private BigDecimal    subtotal;
    private Long          idPromocion;
    private BigDecimal    descuento;
    private BigDecimal    total;
    private BigDecimal    efectivoRecibido;
    private BigDecimal    vuelto;
    private boolean       actaFirmada;
    private boolean       esAnticipada;
    private Long          idUsuarioRegistra;
    private LocalDateTime fechaCreacion;
}
