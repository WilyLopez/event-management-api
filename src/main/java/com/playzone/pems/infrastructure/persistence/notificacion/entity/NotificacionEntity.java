package com.playzone.pems.infrastructure.persistence.notificacion.entity;

import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.ClientePerfilEntity;
import com.playzone.pems.infrastructure.persistence.usuario_supabase.entity.PerfilUsuarioEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_codigo", nullable = false)
    private TipoNotificacionEntity tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_usuario_id")
    private PerfilUsuarioEntity destinatarioUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_cliente_id")
    private ClientePerfilEntity destinatarioCliente;

    @Column(name = "entidad_tipo")
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "url_accion")
    private String urlAccion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", nullable = false)
    private String metadata;

    @Column(name = "leida", nullable = false)
    private boolean leida;

    @Column(name = "leida_at")
    private OffsetDateTime fechaLectura;

    @Column(name = "prioridad", nullable = false)
    private String prioridad;

    @Column(name = "expira_at")
    private OffsetDateTime fechaExpiracion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
