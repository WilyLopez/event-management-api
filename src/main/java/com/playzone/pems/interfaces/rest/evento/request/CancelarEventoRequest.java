package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CancelarEventoRequest {

    @NotBlank(message = "El motivo de cancelacion es obligatorio.")
    @Size(min = 10, message = "El motivo debe tener al menos 10 caracteres.")
    private String motivo;
}
