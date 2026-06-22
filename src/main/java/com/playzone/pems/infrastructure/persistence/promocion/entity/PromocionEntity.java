package com.playzone.pems.infrastructure.persistence.promocion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "promocion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromocionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tipo_codigo", nullable = false, length = 40)
    private String tipoCodigo;

    @Column(name = "sede_id")
    private Long sedeId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 400)
    private String descripcion;

    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @Column(name = "tipo_dia_codigo", length = 30)
    private String tipoDiaCodigo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "es_automatica", nullable = false)
    @Builder.Default
    private boolean esAutomatica = false;

    @Column(name = "es_activo", nullable = false)
    @Builder.Default
    private boolean esActivo = true;

    @Column(name = "prioridad", nullable = false)
    @Builder.Default
    private int prioridad = 0;

    @Column(name = "minimo_personas")
    private Integer minimoPersonas;

    @Column(name = "monto_minimo", precision = 10, scale = 2)
    private BigDecimal montoMinimo;

    @Column(name = "limite_usos")
    private Integer limiteUsos;

    @Column(name = "limite_por_cliente")
    private Integer limitePorCliente;

    @Column(name = "usos_actuales", nullable = false)
    @Builder.Default
    private int usosActuales = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "created_by", columnDefinition = "uuid")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToOne(mappedBy = "promocion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PromocionMarketingEntity marketing;
}
