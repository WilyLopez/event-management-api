package com.playzone.pems.interfaces.rest.finanzas.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.playzone.pems.domain.finanzas.model.enums.CategoriaIngreso;

@Getter
@NoArgsConstructor
public class CrearTipoIngresoRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private CategoriaIngreso categoria;
}
