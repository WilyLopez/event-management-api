package com.playzone.pems.domain.contrato.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActividadContrato {

    private Long          id;
    private Long          idContrato;
    private String        accion;
    private String        descripcion;
    private Long          idUsuario;
    private String        nombreUsuario;
    private LocalDateTime fechaAccion;
}