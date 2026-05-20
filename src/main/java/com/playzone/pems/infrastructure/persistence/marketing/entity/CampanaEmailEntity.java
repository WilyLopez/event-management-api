package com.playzone.pems.infrastructure.persistence.marketing.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "campanaemail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampanaEmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcampanaemail")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idplantillaemail", nullable = false)
    private PlantillaEmailEntity plantillaEmail;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "BORRADOR";

    @Column(name = "fechaprogramada")
    private Instant fechaProgramada;

    @Column(name = "totaldestinatarios", nullable = false)
    @Builder.Default
    private int totalDestinatarios = 0;

    @Column(name = "totalenviados", nullable = false)
    @Builder.Default
    private int totalEnviados = 0;

    @Column(name = "totalfallidos", nullable = false)
    @Builder.Default
    private int totalFallidos = 0;

    @Column(name = "idusuariocreador")
    private Long idUsuarioCreador;

    @CreationTimestamp
    @Column(name = "fechacreacion", updatable = false)
    private Instant fechaCreacion;
}
