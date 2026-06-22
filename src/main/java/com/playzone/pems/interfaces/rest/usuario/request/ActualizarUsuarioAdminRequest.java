package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarUsuarioAdminRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String telefono;
}
