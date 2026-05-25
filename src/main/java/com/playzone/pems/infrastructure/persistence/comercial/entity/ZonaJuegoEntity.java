package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "zonajuego")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZonaJuegoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idzona")
    private Long id;

    @Column(nullable = false, length = 25)
    private String nombre;

    @Column(nullable = false, unique = true, length = 35)
    private String slug;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(name = "edadminima")
    private Integer edadMinima;

    @Column(name = "edadmaxima")
    private Integer edadMaxima;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private boolean destacada;

    @Column(nullable = false)
    private int orden;

    @OneToMany(mappedBy = "zona", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    private List<MedioZonaJuegoEntity> medios;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
