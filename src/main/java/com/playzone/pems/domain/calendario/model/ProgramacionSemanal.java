package com.playzone.pems.domain.calendario.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ProgramacionSemanal {

    private Long            id;
    private Long            idSede;
    private LocalDate       semanaInicio;
    private LocalDate       semanaFin;
    private String          estado;
    private boolean         autoGenerada;
    private UUID            creadoPor;
    private OffsetDateTime  creadoEn;

    public boolean esActiva() {
        return "ACTIVA".equals(estado);
    }

    public boolean comprendeFecha(LocalDate fecha) {
        return !fecha.isBefore(semanaInicio) && !fecha.isAfter(semanaFin);
    }
}
