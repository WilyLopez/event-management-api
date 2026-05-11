package com.playzone.pems.domain.evento.model;

import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
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
public class EventoPrivado {

    private Long                id;
    private Long                idCliente;
    private Long                idSede;
    private EstadoEventoPrivado estado;
    private Long                idTurno;
    private LocalDate           fechaEvento;
    private String              tipoEvento;
    private String              contactoAdicional;
    private Integer             aforoDeclarado;
    private BigDecimal          precioTotalContrato;
    private BigDecimal          montoAdelanto;
    private String              motivoCancelacion;
    private String              notasInternas;
    private Long                idUsuarioGestor;
    private LocalDateTime       fechaCreacion;
    private LocalDateTime       fechaActualizacion;
    private String              estadoOperativo;
    private boolean             checklistCompleto;
    private LocalDateTime       horaInicioReal;
    private LocalDateTime       horaFinReal;

    public BigDecimal calcularMontoSaldo() {
        if (precioTotalContrato == null) return null;
        BigDecimal adelanto = montoAdelanto != null ? montoAdelanto : BigDecimal.ZERO;
        return precioTotalContrato.subtract(adelanto);
    }

    public boolean puedeCancelarse() {
        return estado.esCancelable();
    }

    public boolean bloqueaAccesoPublico() {
        return estado.bloqueaDisponibilidadPublica();
    }

    public boolean adelantoCubreMitad() {
        if (precioTotalContrato == null || montoAdelanto == null) return false;
        return montoAdelanto.multiply(BigDecimal.valueOf(2))
                .compareTo(precioTotalContrato) >= 0;
    }

    public boolean estaSaldado() {
        BigDecimal saldo = calcularMontoSaldo();
        return saldo != null && saldo.compareTo(BigDecimal.ZERO) == 0;
    }
}