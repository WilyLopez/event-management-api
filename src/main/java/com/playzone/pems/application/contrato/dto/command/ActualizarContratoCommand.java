package com.playzone.pems.application.contrato.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ActualizarContratoCommand {
    private Long   id;
    private String contenidoTexto;
    private String plantilla;
    private String observaciones;
    private UUID   idUsuario;
}