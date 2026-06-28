package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CambiarContrasenaAdminRequest {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String contrasenaActual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String contrasenaNueva;

    @AssertTrue(message = "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial (!@#$%&*?)")
    public boolean isContrasenaValida() {
        if (contrasenaNueva == null || contrasenaNueva.length() < 8) return false;
        boolean tieneUpper    = contrasenaNueva.chars().anyMatch(Character::isUpperCase);
        boolean tieneLower    = contrasenaNueva.chars().anyMatch(Character::isLowerCase);
        boolean tieneDigit    = contrasenaNueva.chars().anyMatch(Character::isDigit);
        boolean tieneEspecial = contrasenaNueva.chars().anyMatch(c -> "!@#$%&*?".indexOf(c) >= 0);
        return tieneUpper && tieneLower && tieneDigit && tieneEspecial;
    }
}
