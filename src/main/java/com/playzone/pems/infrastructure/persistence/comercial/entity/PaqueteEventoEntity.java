package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "paqueteevento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaqueteEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpaquete")
    private Long id;

    @Column(nullable = false, length = 30)
    private String nombre;

    @Column(nullable = false, unique = true, length = 40)
    private String slug;

    @Column(name = "descripcioncorta", nullable = false, length = 80)
    private String descripcionCorta;

    @Column(name = "descripcionlarga", length = 500)
    private String descripcionLarga;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 20)
    private String badge;

    @Column(length = 7)
    private String color;

    @Column(name = "imagenurl", length = 500)
    private String imagenUrl;

    @Column(name = "duracionminutos")
    private Integer duracionMinutos;

    @Column(name = "limitepersonas")
    private Integer limitepersonas;

    @Column(nullable = false)
    private boolean activo;

    @Column(nullable = false)
    private boolean destacado;

    @Column(nullable = false)
    private int orden;

    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    private List<BeneficioPaqueteEntity> beneficios;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
