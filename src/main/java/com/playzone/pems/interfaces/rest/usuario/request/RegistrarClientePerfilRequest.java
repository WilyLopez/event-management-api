package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegistrarClientePerfilRequest {

    @NotBlank
    @Size(max = 20)
    private String  tipoDocumentoCodigo;

    @NotBlank
    @Size(max = 20)
    private String  numeroDocumento;

    @NotBlank
    @Size(max = 100)
    private String  nombres;

    @NotBlank
    @Size(max = 100)
    private String  apellidoPaterno;

    @Size(max = 100)
    private String  apellidoMaterno;

    @Size(max = 150)
    private String  correo;

    @Size(max = 20)
    private String  telefono;

    private String  origen;

    private boolean aceptaComunicaciones;
}
