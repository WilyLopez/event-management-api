package com.playzone.pems.application.contrato.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CambiarEstadoContratoCommand {
    private Long   idContrato;
    private String nuevoEstado;
    private String motivo;
    private Long   idUsuarioAdmin;
}