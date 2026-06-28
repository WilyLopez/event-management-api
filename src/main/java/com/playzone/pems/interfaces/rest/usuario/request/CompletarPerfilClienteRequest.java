package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletarPerfilClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    private String apellidoPaterno;
    private String apellidoMaterno;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;

    private String  telefono;
    private boolean aceptaComunicaciones;
}
