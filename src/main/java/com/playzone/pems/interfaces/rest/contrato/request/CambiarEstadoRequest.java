package com.playzone.pems.interfaces.rest.contrato.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CambiarEstadoRequest {
    @NotBlank private String nuevoEstado;
    private String motivo;
}