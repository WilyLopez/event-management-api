package com.playzone.pems.infrastructure.persistence.finanzas.entity;

import com.playzone.pems.domain.finanzas.model.enums.EstadoPresupuesto;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "presupuestoevento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresupuestoEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpresupuesto")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ideventoprivado", nullable = false)
    private EventoPrivadoEntity eventoPrivado;

    @Column(name = "concepto", nullable = false, length = 200)
    private String concepto;

    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "montoestimado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoEstimado;

    @Column(name = "montoreal", precision = 10, scale = 2)
    private BigDecimal montoReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoPresupuesto estado = EstadoPresupuesto.PENDIENTE;

    @Column(name = "idusuarioregistra")
    private Long idUsuarioRegistra;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
