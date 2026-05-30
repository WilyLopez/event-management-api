package com.playzone.pems.interfaces.rest.evento.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    @Size(max = 120)
    private String nombreNino;

    @Min(0) @Max(18)
    private Integer edadCumple;

    private Long         idPaquete;
    private List<Long>   idsExtras;
    private List<String> extrasLibres;

    @Size(max = 2000)
    private String observaciones;
}
