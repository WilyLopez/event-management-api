package com.playzone.pems.application.cms.dto.query;

import com.playzone.pems.domain.cms.model.Banner;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class BannerQuery {

    private Long          id;
    private Long          idSede;
    private String        titulo;
    private String        descripcion;
    private String        imagenUrl;
    private String        enlaceDestino;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private boolean       activo;
    private int           orden;
    private LocalDateTime fechaCreacion;

    public static BannerQuery from(Banner b) {
        return BannerQuery.builder()
                .id(b.getId())
                .idSede(b.getIdSede())
                .titulo(b.getTitulo())
                .descripcion(b.getDescripcion())
                .imagenUrl(b.getImagenUrl())
                .enlaceDestino(b.getEnlaceDestino())
                .fechaInicio(b.getFechaInicio())
                .fechaFin(b.getFechaFin())
                .activo(b.isActivo())
                .orden(b.getOrden())
                .fechaCreacion(b.getFechaCreacion())
                .build();
    }
}
