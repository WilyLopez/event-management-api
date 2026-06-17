package com.playzone.pems.domain.calendario.model;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BloqueCalendario {

    private Long          id;
    private Long          idSede;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private String        tipoBloqueo;
    private String        motivo;
    private boolean       activo;
    private UUID          idUsuarioCreador;
    private OffsetDateTime createdAt;

    public boolean comprendeFecha(LocalDate fecha) {
        return activo
                && !fecha.isBefore(fechaInicio)
                && !fecha.isAfter(fechaFin);
    }

    public boolean sesolapaConRango(LocalDate inicio, LocalDate fin) {
        return activo
                && !fechaFin.isBefore(inicio)
                && !fechaInicio.isAfter(fin);
    }

    public long duracionEnDias() {
        return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }
}