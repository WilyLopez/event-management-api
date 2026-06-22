package com.playzone.pems.infrastructure.persistence.evento.entity;

import com.playzone.pems.infrastructure.persistence.comercial.entity.ExtraPaqueteEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento_extra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoExtraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoPrivadoEntity evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paquete_extra_id")
    private ExtraPaqueteEntity extra;

    @Column(name = "nombre_libre", length = 300)
    private String nombreLibre;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
