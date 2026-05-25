package com.playzone.pems.application.comercial.dto.query;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovedadLocalQuery {
    private Long          id;
    private String        titulo;
    private String        descripcion;
    private String        imagenUrl;
    private String        textoCta;
    private String        urlCta;
    private int           prioridad;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private boolean       visibleHome;
    private boolean       destacada;
    private boolean       activa;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
