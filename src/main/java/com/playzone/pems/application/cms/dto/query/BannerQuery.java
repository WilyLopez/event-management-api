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
    private String        imagenMovilUrl;
    private String        enlaceDestino;
    private String        textoBoton;
    private String        colorOverlay;
    private String        tipoBanner;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private boolean       activo;
    private int           orden;
    private int           prioridad;
    private boolean       soloMovil;
    private boolean       soloDesktop;
    private LocalDateTime fechaCreacion;

    public static BannerQuery from(Banner b) {
        return BannerQuery.builder()
                .id(b.getId())
                .idSede(b.getIdSede())
                .titulo(b.getTitulo())
                .descripcion(b.getDescripcion())
                .imagenUrl(b.getImagenUrl())
                .imagenMovilUrl(b.getImagenMovilUrl())
                .enlaceDestino(b.getEnlaceDestino())
                .textoBoton(b.getTextoBoton())
                .colorOverlay(b.getColorOverlay())
                .tipoBanner(b.getTipoBanner())
                .fechaInicio(b.getFechaInicio())
                .fechaFin(b.getFechaFin())
                .activo(b.isActivo())
                .orden(b.getOrden())
                .prioridad(b.getPrioridad())
                .soloMovil(b.isSoloMovil())
                .soloDesktop(b.isSoloDesktop())
                .fechaCreacion(b.getFechaCreacion())
                .build();
    }
}
