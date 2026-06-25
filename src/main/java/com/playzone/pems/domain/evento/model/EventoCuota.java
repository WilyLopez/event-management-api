package com.playzone.pems.domain.evento.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EventoCuota {

    private Long          id;
    private Long          eventoId;
    private int           numeroCuota;
    private BigDecimal    monto;
    private LocalDate     fechaVencimiento;
    private String        estado;        // PENDIENTE | PAGADO | VENCIDO
    private Long          ventaId;
    private OffsetDateTime createdAt;

    public boolean esPagado()    { return "PAGADO".equals(estado); }
    public boolean esPendiente() { return "PENDIENTE".equals(estado); }
    public boolean esVencido()   { return "VENCIDO".equals(estado); }
}
