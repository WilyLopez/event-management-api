package com.playzone.pems.application.comercial.dto.command;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarNovedadCommand {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String titulo;

    @NotBlank
    @Size(max = 120)
    private String descripcion;

    private String imagenUrl;

    @Size(max = 25)
    private String textoCta;

    private String    urlCta;
    private int       prioridad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean   visibleHome;
    private boolean   destacada;
    private boolean   activa;
}
