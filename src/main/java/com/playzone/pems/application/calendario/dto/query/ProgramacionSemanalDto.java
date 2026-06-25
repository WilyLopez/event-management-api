package com.playzone.pems.application.calendario.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
public class ProgramacionSemanalDto {

    private Long            id;
    private Long            idSede;
    private LocalDate       semanaInicio;
    private LocalDate       semanaFin;
    private String          estado;
    private boolean         autoGenerada;
    private OffsetDateTime  creadoEn;
}
