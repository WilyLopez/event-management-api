package com.playzone.pems.interfaces.rest.proveedor.request;

import com.playzone.pems.shared.validation.RucValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GestionarProveedorRequest {

    @NotBlank @Size(max = 200)
    private String nombre;

    @RucValidator(requerido = false)
    private String ruc;

    @Size(max = 120)
    private String contactoNombre;

    @Size(max = 20)
    private String contactoTelefono;

    @Email @Size(max = 120)
    private String contactoCorreo;

    @NotBlank @Size(max = 200)
    private String tipoServicio;

    private String notas;
}