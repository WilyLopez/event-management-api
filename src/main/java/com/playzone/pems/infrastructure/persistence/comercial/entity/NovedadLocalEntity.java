package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "novedad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovedadLocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "imagen_path", length = 500)
    private String imagenUrl;

    @Column(name = "es_activa", nullable = false)
    private boolean activa;

    @Column(name = "es_visible_home", nullable = false)
    private boolean visibleHome;

    @Column(name = "es_destacada", nullable = false)
    private boolean destacada;

    @Column(nullable = false)
    private int prioridad;

    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
