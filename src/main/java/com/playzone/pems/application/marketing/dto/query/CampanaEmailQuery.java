package com.playzone.pems.application.marketing.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CampanaEmailQuery {

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
    private UUID    createdBy;
    private Instant fechaCreacion;
}
