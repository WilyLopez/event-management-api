package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

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
    private Long          idUsuarioCreador;
    private LocalDateTime fechaCreacion;

    public boolean esVisibleEn(LocalDate fecha) {
        if (!activo) return false;
        boolean despuesDeInicio = !fecha.isBefore(fechaInicio);
        boolean antesDeVencimiento = fechaFin == null || !fecha.isAfter(fechaFin);
        return despuesDeInicio && antesDeVencimiento;
    }

    public boolean tieneEnlace() {
        return enlaceDestino != null && !enlaceDestino.isBlank();
    }

    public boolean esGlobal() {
        return idSede == null;
    }
}