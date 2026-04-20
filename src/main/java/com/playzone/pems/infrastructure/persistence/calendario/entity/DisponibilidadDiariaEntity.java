package com.playzone.pems.infrastructure.persistence.calendario.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidaddiaria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idsede", "fecha"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadDiariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddisponibilidad")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "accesopublicoactivo", nullable = false)
    private boolean accesoPublicoActivo = true;

    @Column(name = "turnot1disponible", nullable = false)
    private boolean turnoT1Disponible = true;

    @Column(name = "turnot2disponible", nullable = false)
    private boolean turnoT2Disponible = true;

    @Column(name = "aforopublicoactual", nullable = false)
    private int aforoPublicoActual = 0;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}