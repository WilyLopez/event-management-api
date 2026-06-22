package com.playzone.pems.application.comercial.dto.query;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteEventoQuery {
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
    private String        tipoEventoCodigo;
    private List<String>  beneficios;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
