package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "zona_juego_medio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedioZonaJuegoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaJuegoEntity zona;

    @Column(name = "archivo_path", nullable = false, length = 500)
    private String url;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // IMAGEN, VIDEO

    @Column(nullable = false)
    private int orden;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
