package com.playzone.pems.interfaces.rest.usuario.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarUsuarioAdminRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    private String correo;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    private String telefono;

    private boolean generarPassword;

    @jakarta.validation.constraints.Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @AssertTrue(message = "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial (!@#$%&*?)")
    public boolean isPasswordValida() {
        if (generarPassword) return true;
        if (password == null || password.length() < 8) return false;
        boolean tieneUpper   = password.chars().anyMatch(Character::isUpperCase);
        boolean tieneLower   = password.chars().anyMatch(Character::isLowerCase);
        boolean tieneDigit   = password.chars().anyMatch(Character::isDigit);
        boolean tieneEspecial = password.chars().anyMatch(c -> "!@#$%&*?".indexOf(c) >= 0);
        return tieneUpper && tieneLower && tieneDigit && tieneEspecial;
    }
}
