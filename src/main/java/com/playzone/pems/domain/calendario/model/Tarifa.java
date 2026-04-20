package com.playzone.pems.domain.calendario.model;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {

    private Long          id;
    private Long          idSede;
    private TipoDia       tipoDia;
    private BigDecimal    precio;
    private LocalDate     vigenciaDesde;
    private LocalDate     vigenciaHasta;
    private boolean       activo;
    private Long          idUsuarioCreador;
    private LocalDateTime fechaCreacion;

    public boolean estaVigenteEn(LocalDate fecha) {
        if (!activo) return false;
        boolean despuesDeInicio = !fecha.isBefore(vigenciaDesde);
        boolean antesDeVencimiento = vigenciaHasta == null || !fecha.isAfter(vigenciaHasta);
        return despuesDeInicio && antesDeVencimiento;
    }

    public boolean aplicaParaTipoDia(TipoDia tipoDia) {
        return this.tipoDia == tipoDia;
    }
}