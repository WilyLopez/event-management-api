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
    @Column(name = "ideventoextra")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ideventoprivado", nullable = false)
    private EventoPrivadoEntity evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idextra")
    private ExtraPaqueteEntity extra;

    @Column(name = "nombrelibre", length = 300)
    private String nombreLibre;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
