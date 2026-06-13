package com.playzone.pems.infrastructure.persistence.fidelizacion.entity;

import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fidelizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialFidelizacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private ReservaPublicaEntity reservaPublica;

    @Column(name = "visita_numero", nullable = false)
    private int visitaNumero;

    @Column(name = "es_beneficio", nullable = false)
    private boolean esBeneficioAplicado = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaRegistro;
}
