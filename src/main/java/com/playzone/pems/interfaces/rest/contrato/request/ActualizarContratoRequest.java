package com.playzone.pems.interfaces.rest.contrato.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ActualizarContratoRequest {
    @NotBlank private String contenidoTexto;
    private String plantilla;
    private String observaciones;
}