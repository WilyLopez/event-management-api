package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActualizarUsuarioAdminRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'\\-]+$",
        message = "El nombre solo puede contener letras y espacios"
    )
    private String nombre;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Pattern(
        regexp = "^(9\\d{8})?$",
        message = "El teléfono debe comenzar con 9 y tener exactamente 9 dígitos"
    )
    private String telefono;
}
