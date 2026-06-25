package com.playzone.pems.infrastructure.persistence.calendario.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "configuracion_calendario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionCalendarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idconfig")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false, unique = true)
    private SedeEntity sede;

    @Column(name = "dias_min_reserva_publica", nullable = false)
    private int diasMinReservaPublica;

    @Column(name = "dias_max_reserva_publica", nullable = false)
    private int diasMaxReservaPublica;

    @Column(name = "dias_min_evento_privado", nullable = false)
    private int diasMinEventoPrivado;

    @Column(name = "dias_max_evento_privado", nullable = false)
    private int diasMaxEventoPrivado;

    @Column(name = "aforo_maximo", nullable = false)
    private int aforoMaximo;

    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    @Column(name = "turno_t1_inicio", nullable = false)
    private LocalTime turnoT1Inicio;

    @Column(name = "turno_t1_fin", nullable = false)
    private LocalTime turnoT1Fin;

    @Column(name = "turno_t2_inicio", nullable = false)
    private LocalTime turnoT2Inicio;

    @Column(name = "turno_t2_fin", nullable = false)
    private LocalTime turnoT2Fin;

    @Column(name = "dias_operacion", nullable = false, length = 20)
    private String diasOperacion;

    @Column(name = "rango_max_bloqueo_dias", nullable = false)
    private int rangoMaxBloqueo;

    @Column(name = "edad_min_cumple", nullable = false)
    private int edadMinCumple;

    @Column(name = "edad_max_cumple", nullable = false)
    private int edadMaxCumple;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;
}
