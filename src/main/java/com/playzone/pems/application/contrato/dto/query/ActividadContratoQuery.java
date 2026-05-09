package com.playzone.pems.application.contrato.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ActividadContratoQuery {
    private Long          id;
    private String        accion;
    private String        descripcion;
    private String        usuario;
    private LocalDateTime fechaAccion;
}