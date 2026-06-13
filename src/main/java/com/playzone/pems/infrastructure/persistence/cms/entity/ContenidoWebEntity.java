package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contenido_web",
        uniqueConstraints = @UniqueConstraint(columnNames = {"seccion_codigo", "clave"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoWebEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seccion_codigo", nullable = false, length = 100)
    private String seccionCodigo;

    @Column(name = "tipo_contenido_codigo", nullable = false, length = 100)
    private String tipoContenidoCodigo;

    @Column(nullable = false, length = 100, columnDefinition = "VARCHAR(100)")
    private String clave;

    @Column(name = "valor_es", nullable = false, columnDefinition = "TEXT")
    private String valorEs;

    @Column(name = "valor_en", columnDefinition = "TEXT")
    private String valorEn;

    @Column(name = "imagen_path", length = 500)
    private String imagenUrl;

    @Column(length = 300)
    private String descripcion;

    @Column(name = "orden", nullable = false)
    private int ordenVisualizacion = 0;

    @Column(name = "es_visible", nullable = false)
    private boolean visible = true;

    @Column(nullable = false)
    private int version = 1;

    @Column(name = "metadatos", columnDefinition = "jsonb")
    private String metadatos;

    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime fechaActualizacion;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
