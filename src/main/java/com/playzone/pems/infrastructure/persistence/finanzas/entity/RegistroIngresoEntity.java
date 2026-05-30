package com.playzone.pems.infrastructure.persistence.finanzas.entity;

import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registroingreso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroIngresoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idregistroingreso")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idtipoingreso", nullable = false)
    private TipoIngresoEntity tipoIngreso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idreservapublica")
    private ReservaPublicaEntity reservaPublica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ideventoprivado")
    private EventoPrivadoEntity eventoPrivado;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "mediopago", length = 30)
    private String medioPago;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "esautomatico", nullable = false)
    @Builder.Default
    private boolean esAutomatico = false;

    @Column(name = "idusuarioregistra")
    private Long idUsuarioRegistra;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
