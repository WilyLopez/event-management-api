package com.playzone.pems.infrastructure.persistence.notificacion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "tipo_notificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoNotificacionEntity {

    @Id
    @Column(name = "codigo")
    private String codigo;

    @Column(name = "modulo", nullable = false)
    private String modulo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "destinatario_default", nullable = false)
    private String destinatarioDefault;

    @Column(name = "canales_default", nullable = false)
    private List<String> canalesDefault;

    @Column(name = "plantilla_titulo")
    private String plantillaTitulo;

    @Column(name = "plantilla_mensaje")
    private String plantillaMensaje;

    @Column(name = "prioridad", nullable = false)
    private String prioridad;

    @Column(name = "es_sistema", nullable = false)
    private boolean esSistema;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "orden", nullable = false)
    private int orden;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime fechaActualizacion;
}
