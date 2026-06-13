package com.playzone.pems.infrastructure.persistence.contrato.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contrato_actividad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadContratoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoEntity contrato;

    @Column(name = "accion", nullable = false)
    private String accion;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "usuario_id", columnDefinition = "uuid")
    private UUID usuarioId;

    @CreationTimestamp
    @Column(name = "accion_at", nullable = false, updatable = false)
    private OffsetDateTime fechaAccion;
}
