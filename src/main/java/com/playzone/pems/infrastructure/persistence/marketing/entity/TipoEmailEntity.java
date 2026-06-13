package com.playzone.pems.infrastructure.persistence.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tipo_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEmailEntity {

    @Id
    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(name = "es_sistema", nullable = false)
    @Builder.Default
    private boolean esSistema = false;

    @Column(name = "orden", nullable = false)
    private int orden;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
