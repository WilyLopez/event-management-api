package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "consentimiento_legal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentimientoLegalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String origen;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "version_v", nullable = false)
    private int version;

    @Column(length = 64)
    private String ip;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
