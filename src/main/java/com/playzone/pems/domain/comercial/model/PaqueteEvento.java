package com.playzone.pems.domain.comercial.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteEvento {
    private Long          id;
    private String        nombre;
    private String        slug;
    private String        descripcionCorta;
    private String        descripcionLarga;
    private BigDecimal    precio;
    private String        badge;
    private String        color;
    private String        imagenUrl;
    private Integer       duracionMinutos;
    private Integer       limitepersonas;
    private boolean       activo;
    private boolean       destacado;
    private int           orden;
    private List<String>  beneficios;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
