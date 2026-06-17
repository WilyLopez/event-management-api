package com.playzone.pems.domain.notificacion.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoNotificacion {
    private String codigo;
    private String modulo;
    private String nombre;
    private String descripcion;
    private String destinatarioDefault;
    private List<String> canalesDefault;
    private String plantillaTitulo;
    private String plantillaMensaje;
    private String prioridad;
    private boolean esSistema;
    private boolean activo;
    private int orden;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
