package com.playzone.pems.infrastructure.persistence.auditoria.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.playzone.pems.infrastructure.persistence.cms.entity.InetConverter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogAuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "usuario_id", columnDefinition = "uuid")
    private UUID usuarioId;

    @Column(nullable = false, length = 40)
    private String accion;

    @Column(nullable = false, length = 80)
    private String modulo;

    @Column(name = "entidad", nullable = false, length = 80)
    private String entidadAfectada;

    @Column(name = "entidad_id")
    private Long idEntidad;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valor_anterior", columnDefinition = "jsonb")
    private String valorAnterior;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valor_nuevo", columnDefinition = "jsonb")
    private String valorNuevo;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "user_agent", length = 300)
    private String userAgent;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String nivel = "INFO";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String resultado = "EXITOSO";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaLog;
}
