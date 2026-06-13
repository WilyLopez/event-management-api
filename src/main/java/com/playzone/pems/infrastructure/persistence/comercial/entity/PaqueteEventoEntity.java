package com.playzone.pems.infrastructure.persistence.comercial.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "paquete")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaqueteEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "descripcion_corta", nullable = false)
    private String descripcionCorta;

    @Column(name = "descripcion_larga")
    private String descripcionLarga;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "badge")
    private String badge;

    @Column(name = "color_hex")
    private String color;

    @Column(name = "imagen_path")
    private String imagenUrl;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "limite_personas")
    private Integer limitepersonas;

    @Column(name = "es_activo", nullable = false)
    private boolean activo;

    @Column(name = "es_destacado", nullable = false)
    private boolean destacado;

    @Column(name = "orden", nullable = false)
    private int orden;

    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    private List<BeneficioPaqueteEntity> beneficios;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime fechaActualizacion;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
