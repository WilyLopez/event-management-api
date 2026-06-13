package com.playzone.pems.application.comercial.dto.query;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActividadLocalQuery {
    private Long          id;
    private String        nombre;
    private String        descripcion;
    private String        imagenUrl;
    private Long          idZona;
    private String        nombreZona;
    private boolean       esEspecial;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private boolean       activa;
    private boolean       destacada;
    private int           orden;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
