package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    private Long          id;
    private Long          idCliente;
    private Long          idEventoPrivado;
    private String        nombreAutor;
    private String        contenido;
    private int           calificacion;
    private boolean       aprobada;
    private String        fotoUrl;
    private String        respuestaAdmin;
    private LocalDateTime fechaRespuesta;
    private boolean       destacada;
    private boolean       mostrarHome;
    private Long          idUsuarioAprueba;
    private LocalDateTime fechaCreacion;

    public boolean esCalificacionPositiva() {
        return calificacion >= 4;
    }

    public boolean esCalificacionNegativa() {
        return calificacion <= 2;
    }

    public boolean estaPendienteDeModeracion() {
        return !aprobada && idUsuarioAprueba == null;
    }

    public boolean esDeClienteRegistrado() {
        return idCliente != null;
    }

    public String extracto(int maxCaracteres) {
        if (contenido == null) return "";
        return contenido.length() <= maxCaracteres
                ? contenido
                : contenido.substring(0, maxCaracteres) + "…";
    }
}