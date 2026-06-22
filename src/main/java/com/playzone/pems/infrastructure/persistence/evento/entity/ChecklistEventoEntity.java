package com.playzone.pems.infrastructure.persistence.evento.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "checklist_evento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoPrivadoEntity eventoPrivado;

    @Column(name = "tarea", nullable = false, length = 200)
    private String tarea;

    @Column(name = "completada", nullable = false)
    @Builder.Default
    private boolean completada = false;

    @Column(name = "orden", nullable = false)
    @Builder.Default
    private int orden = 0;

    @Column(name = "completada_por", columnDefinition = "uuid")
    private UUID completadaPor;

    @Column(name = "completada_at")
    private OffsetDateTime fechaCompletado;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
