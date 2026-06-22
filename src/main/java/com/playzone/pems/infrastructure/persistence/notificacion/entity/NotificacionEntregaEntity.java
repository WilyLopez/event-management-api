package com.playzone.pems.infrastructure.persistence.notificacion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notificacion_entrega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionEntregaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notificacion_id", nullable = false)
    private NotificacionEntity notificacion;

    @Column(name = "canal", nullable = false)
    private String canal;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "enviado_at")
    private OffsetDateTime fechaEnvio;

    @Column(name = "mensaje_error")
    private String mensajeError;

    @Column(name = "proveedor_id")
    private String proveedorId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", nullable = false)
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
