package com.playzone.pems.application.usuario.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarClientePublicoCommand {
    private String nombre;
    private String correo;
    private String password;
    private String telefono;
    private String tipoDocumentoCodigo;
    private String numeroDocumento;
}
