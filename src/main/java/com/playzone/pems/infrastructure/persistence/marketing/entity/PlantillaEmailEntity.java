package com.playzone.pems.infrastructure.persistence.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "plantilla_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantillaEmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tipo_email_codigo", nullable = false, length = 50)
    private String tipoEmailCodigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(name = "contenido_html", nullable = false, columnDefinition = "TEXT")
    private String contenidoHtml;

    @Column(name = "contenido_fallback", columnDefinition = "TEXT")
    private String contenidoFallback;

    @Column(name = "variables_permitidas", columnDefinition = "jsonb")
    private String variablesPermitidas;

    @Column(name = "es_activa", nullable = false)
    @Builder.Default
    private boolean esActiva = true;

    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
