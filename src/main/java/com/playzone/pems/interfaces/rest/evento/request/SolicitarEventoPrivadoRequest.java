package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SolicitarEventoPrivadoRequest {

    @NotNull
    private Long idTurno;

    @NotNull @Future
    private LocalDate fechaEvento;

    @NotBlank @Size(max = 200)
    private String tipoEvento;

    @Size(max = 200)
    private String contactoAdicional;

    @Min(1) @Max(60)
    private Integer aforoDeclarado;
}