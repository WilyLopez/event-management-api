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
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", nullable = false)
    private ZonaJuegoEntity zona;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "archivo_path", nullable = false)
    private String url;

    @Column(name = "alt_texto")
    private String altTexto;

    @Column(name = "orden", nullable = false)
    private int orden;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
