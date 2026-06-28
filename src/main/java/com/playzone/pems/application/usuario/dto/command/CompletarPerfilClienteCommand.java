package com.playzone.pems.application.usuario.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CompletarPerfilClienteCommand {

    private UUID    usuarioId;
    private String  correo;
    private String  nombres;
    private String  apellidoPaterno;
    private String  apellidoMaterno;
    private String  tipoDocumentoCodigo;
    private String  numeroDocumento;
    private String  telefono;
    private boolean aceptaComunicaciones;
}
