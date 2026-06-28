package com.playzone.pems.application.notificacion.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class NotificacionQuery {

    private final Long            id;
    private final String          tipoCodigo;
    private final String          titulo;
    private final String          mensaje;
    private final String          prioridad;
    private final String          urlAccion;
    private final boolean         leida;
    private final OffsetDateTime  leidaAt;
    private final OffsetDateTime  expiraAt;
    private final OffsetDateTime  createdAt;
    private final String          entidadTipo;
    private final Long            entidadId;
}
