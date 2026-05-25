package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "actividadlocal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadLocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idactividad")
    private Long id;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(name = "imagenurl", length = 500)
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "idzona")
    private ZonaJuegoEntity zona;

    @Column(name = "esespecial", nullable = false)
    private boolean esEspecial;

    @Column(name = "fechainicio")
    private LocalDate fechaInicio;

    @Column(name = "fechafin")
    private LocalDate fechaFin;

    @Column(nullable = false)
    private boolean activa;

    @Column(nullable = false)
    private boolean destacada;

    @Column(nullable = false)
    private int orden;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
