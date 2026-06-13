package com.playzone.pems.infrastructure.persistence.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "campana_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampanaEmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(name = "plantilla_id", nullable = false)
    private Long plantillaId;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "BORRADOR";

    @Column(name = "fecha_programada")
    private OffsetDateTime fechaProgramada;

    @Column(name = "total_destinatarios", nullable = false)
    @Builder.Default
    private int totalDestinatarios = 0;

    @Column(name = "total_enviados", nullable = false)
    @Builder.Default
    private int totalEnviados = 0;

    @Column(name = "total_fallidos", nullable = false)
    @Builder.Default
    private int totalFallidos = 0;

    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;

    @Column(name = "enviada_por", columnDefinition = "uuid")
    private UUID enviadaPor;

    @Column(name = "filtros", columnDefinition = "jsonb")
    private String filtros;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "enviada_at")
    private OffsetDateTime enviadaAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
