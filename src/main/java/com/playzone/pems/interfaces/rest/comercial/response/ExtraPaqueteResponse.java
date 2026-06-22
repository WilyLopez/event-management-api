package com.playzone.pems.interfaces.rest.comercial.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExtraPaqueteResponse {
    private Long id;
    private Long idPaquete;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private int orden;
}
