package com.playzone.pems.domain.evento.model;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.evento.model.enums.CanalReserva;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReservaPublica {

    private Long                  id;
    private Long                  idCliente;
    private Long                  idSede;
    private EstadoReservaPublica estado;
    private CanalReserva canalReserva;
    private TipoDia               tipoDia;
    private Long                  idReservaOriginal;
    private boolean               esReprogramacion;
    private int                   vecesReprogramada;
    private LocalDate             fechaEvento;
    private String                numeroTicket;
    private BigDecimal            precioHistorico;
    private BigDecimal            descuentoAplicado;
    private BigDecimal            totalPagado;
    private String                nombreNino;
    private int                   edadNino;
    private String                nombreAcompanante;
    private String                dniAcompanante;
    private boolean               firmoConsentimiento;
    private String                motivoCancelacion;
    private LocalDateTime         fechaCreacion;
    private LocalDateTime         fechaActualizacion;
    
    public boolean puedeReprogramarse(int maxReprogramaciones) {
        return estado.esReprogramable() && vecesReprogramada < maxReprogramaciones;
    }
    
    public boolean puedeCancelarse() {
        return estado.esCancelable();
    }

    public boolean ocupaAforo() {
        return estado.ocupaAforo();
    }

    public boolean totalEsCoherente() {
        BigDecimal esperado = precioHistorico.subtract(descuentoAplicado);
        return totalPagado.compareTo(esperado) == 0;
    }
}