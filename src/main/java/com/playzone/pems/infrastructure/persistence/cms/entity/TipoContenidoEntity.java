package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tipo_contenido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoContenidoEntity {

    @Id
    @Column(name = "codigo", nullable = false, length = 40)
    private String codigo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "es_sistema", nullable = false)
    private boolean esSistema;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "orden", nullable = false)
    private int orden;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
