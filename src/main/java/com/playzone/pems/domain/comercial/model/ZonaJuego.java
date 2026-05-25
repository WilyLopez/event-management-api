package com.playzone.pems.domain.comercial.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaJuego {
    private Long          id;
    private String        nombre;
    private String        slug;
    private String        descripcion;
    private Integer       edadMinima;
    private Integer       edadMaxima;
    private boolean       activa;
    private boolean       destacada;
    private int           orden;
    private List<String>  imagenes;
    private List<String>  videos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
