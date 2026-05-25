package com.playzone.pems.domain.comercial.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActividadLocal {
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
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
