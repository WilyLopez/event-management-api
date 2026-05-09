package com.playzone.pems.infrastructure.persistence.calendario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "turno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idturno")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(nullable = false, length = 60)
    private String descripcion;

    @Column(name = "horainicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "horafin", nullable = false)
    private LocalTime horaFin;

    public String getNombre() {
        return descripcion;
    }
}