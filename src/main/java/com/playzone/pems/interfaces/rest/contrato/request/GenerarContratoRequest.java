package com.playzone.pems.interfaces.rest.contrato.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerarContratoRequest {

    private String contenidoTexto;

    private String plantilla;
}