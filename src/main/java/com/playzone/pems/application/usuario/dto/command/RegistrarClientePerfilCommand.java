package com.playzone.pems.application.usuario.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RegistrarClientePerfilCommand {

    private String tipoDocumentoCodigo;
    private String numeroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String telefono;
    private String origen;
    private UUID   usuarioId;
    private boolean aceptaComunicaciones;
}
