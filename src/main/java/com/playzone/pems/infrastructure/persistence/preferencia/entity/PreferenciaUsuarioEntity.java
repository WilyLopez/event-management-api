package com.playzone.pems.infrastructure.persistence.preferencia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "preferencia_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaUsuarioEntity {

    @Id
    @Column(name = "usuario_id", columnDefinition = "uuid")
    private UUID usuarioId;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String tema = "SYSTEM";

    @Column(nullable = false, length = 5)
    @Builder.Default
    private String idioma = "es";

    @Column(name = "zona_horaria", nullable = false, length = 60)
    @Builder.Default
    private String zonaHoraria = "America/Lima";

    @Column(name = "formato_fecha", nullable = false, length = 20)
    @Builder.Default
    private String formatoFecha = "DD/MM/YYYY";

    @Column(name = "formato_hora", nullable = false, length = 5)
    @Builder.Default
    private String formatoHora = "24H";

    @Column(name = "sidebar_colapsado", nullable = false)
    @Builder.Default
    private boolean sidebarColapsado = false;

    @Column(name = "autorefresh_dashboard", nullable = false)
    @Builder.Default
    private boolean autorefreshDashboard = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferencias_extras", columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private Map<String, Object> preferenciasExtras = new java.util.HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "timestamptz DEFAULT NOW()")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
