package com.playzone.pems.infrastructure.persistence.finanzas.entity;

import com.playzone.pems.domain.finanzas.model.enums.EstadoCaja;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aperturacaja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AperturaCajaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idaperturacaja")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "saldoinicial", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(name = "saldofinal", precision = 10, scale = 2)
    private BigDecimal saldoFinal;

    @Column(name = "totalingresos", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalIngresos = BigDecimal.ZERO;

    @Column(name = "totalegresos", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalEgresos = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCaja estado = EstadoCaja.ABIERTA;

    @Column(name = "idusuarioapertura")
    private Long idUsuarioApertura;

    @Column(name = "idusuariocierre")
    private Long idUsuarioCierre;

    @Column(name = "fechaapertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fechacierre")
    private LocalDateTime fechaCierre;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
