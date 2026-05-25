package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "novedadlocal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NovedadLocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnovedad")
    private Long id;

    @Column(nullable = false, length = 50)
    private String titulo;

    @Column(nullable = false, length = 120)
    private String descripcion;

    @Column(name = "imagenurl", length = 500)
    private String imagenUrl;

    @Column(name = "textocta", length = 25)
    private String textoCta;

    @Column(name = "urlcta", length = 300)
    private String urlCta;

    @Column(nullable = false)
    private int prioridad;

    @Column(name = "fechainicio")
    private LocalDate fechaInicio;

    @Column(name = "fechafin")
    private LocalDate fechaFin;

    @Column(name = "visiblehome", nullable = false)
    private boolean visibleHome;

    @Column(nullable = false)
    private boolean destacada;

    @Column(nullable = false)
    private boolean activa;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
