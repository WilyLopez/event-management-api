package com.playzone.pems.domain.contrato.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ActividadContrato {

    private Long          id;
    private Long          idContrato;
    private String        accion;
    private String        descripcion;
    private UUID          idUsuario;
    private String        nombreUsuario;
    private OffsetDateTime fechaAccion;
}