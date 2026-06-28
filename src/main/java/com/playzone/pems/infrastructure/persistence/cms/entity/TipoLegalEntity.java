package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tipo_legal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoLegalEntity {

    @Id
    @Column(length = 50)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String etiqueta;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false)
    private int orden;

    @Column(name = "es_sistema", nullable = false)
    private boolean esSistema;

    @Column(nullable = false)
    private boolean requerido;

    @Column(name = "visible_footer", nullable = false)
    private boolean visibleFooter;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
