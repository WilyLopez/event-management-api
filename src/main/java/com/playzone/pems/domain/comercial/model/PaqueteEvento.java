package com.playzone.pems.domain.comercial.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
    private String        colorHex;
    private String        imagenPath;
    private Integer       duracionMinutos;
    private Integer       limitePersonas;
    private boolean       esActivo;
    private boolean       esDestacado;
    private int           orden;
    private String        tipoEventoCodigo;
    private List<String>  beneficios;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
