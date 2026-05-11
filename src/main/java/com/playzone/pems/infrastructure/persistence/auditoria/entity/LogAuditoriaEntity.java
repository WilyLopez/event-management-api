package com.playzone.pems.infrastructure.persistence.auditoria.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "logauditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogAuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idlog")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuarioadmin")
    private UsuarioAdminEntity usuarioAdmin;

    @Column(nullable = false, length = 40)
    private String accion;

    @Column(nullable = false, length = 80)
    private String modulo;

    @Column(name = "entidadafectada", nullable = false, length = 80)
    private String entidadAfectada;

    @Column(name = "identidad")
    private Long idEntidad;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valoranterior", columnDefinition = "jsonb")
    private String valorAnterior;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valornuevo", columnDefinition = "jsonb")
    private String valorNuevo;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "iporigen", columnDefinition = "inet")
    private String ipOrigen;

    @Column(name = "useragent", length = 300)
    private String userAgent;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String nivel = "INFO";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String resultado = "EXITOSO";

    @CreationTimestamp
    @Column(name = "fechalog", nullable = false, updatable = false)
    private LocalDateTime fechaLog;
}
