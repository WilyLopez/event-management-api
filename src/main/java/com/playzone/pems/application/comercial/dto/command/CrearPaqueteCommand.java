package com.playzone.pems.application.comercial.dto.command;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearPaqueteCommand {

    @NotBlank
    @Size(max = 30)
    private String nombre;

    @NotBlank
    @Size(max = 80)
    private String descripcionCorta;

    @Size(max = 500)
    private String descripcionLarga;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;

    @Size(max = 20)
    private String badge;

    @Size(max = 7)
    private String color;

    private String imagenUrl;

    @Min(1)
    private Integer duracionMinutos;

    @Min(1)
    private Integer limitepersonas;

    @Size(max = 8)
    private List<@Size(max = 60) String> beneficios;
}
