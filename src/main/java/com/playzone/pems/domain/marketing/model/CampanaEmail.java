package com.playzone.pems.domain.marketing.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CampanaEmail {

    private Long    id;
    private String  nombre;
    private String  descripcion;
    private Long    idPlantillaEmail;
    private String  plantillaNombre;
    private String  estado;
    private Instant fechaProgramada;
    private int     totalDestinatarios;
    private int     totalEnviados;
    private int     totalFallidos;
    private Long    idUsuarioCreador;
    private Instant fechaCreacion;

    public boolean puedeEnviarse() {
        return "PROGRAMADA".equals(estado);
    }

    public boolean estaPendiente() {
        return "BORRADOR".equals(estado) || "PROGRAMADA".equals(estado);
    }
}
