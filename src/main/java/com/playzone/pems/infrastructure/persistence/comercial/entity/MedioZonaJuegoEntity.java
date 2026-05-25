package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mediozonasjuego")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedioZonaJuegoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmedio")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idzona", nullable = false)
    private ZonaJuegoEntity zona;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private int orden;
}
