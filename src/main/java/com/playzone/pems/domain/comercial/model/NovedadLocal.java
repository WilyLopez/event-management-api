package com.playzone.pems.domain.comercial.model;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovedadLocal {
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
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
