package com.playzone.pems.application.comercial.dto.command;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearActividadCommand {

    @NotBlank
    @Size(max = 40)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    private String descripcion;

    private String    imagenUrl;
    private Long      idZona;
    private boolean   esEspecial;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
