package com.playzone.pems.infrastructure.persistence.evento.entity;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.evento.model.enums.CanalReserva;
import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservapublica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaPublicaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreservapublica")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcliente", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @Enumerated(EnumType.STRING)
    @Column(name = "idestado", nullable = false, length = 40)
    private EstadoReservaPublica estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "idcanalreserva", nullable = false, length = 30)
    private CanalReserva canalReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "idtipodiacod", nullable = false, length = 30)
    private TipoDia tipoDia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idreservaoriginal")
    private ReservaPublicaEntity reservaOriginal;

    @Column(name = "esreprogramacion", nullable = false)
    private boolean esReprogramacion = false;

    @Column(name = "vecesreprogramada", nullable = false)
    private int vecesReprogramada = 0;

    @Column(name = "fechaevento", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "numeroticket", nullable = false, unique = true, length = 50)
    private String numeroTicket;

    @Column(name = "preciohistorico", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioHistorico;

    @Column(name = "descuentoaplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO;

    @Column(name = "totalpagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPagado;

    @Column(name = "nombrenino", nullable = false, length = 120)
    private String nombreNino;

    @Column(name = "edadnino", nullable = false)
    private int edadNino;

    @Column(name = "nombreacompanante", nullable = false, length = 120)
    private String nombreAcompanante;

    @Column(name = "dniacompanante", nullable = false, length = 8)
    private String dniAcompanante;

    @Column(name = "firmoconsentimiento", nullable = false)
    private boolean firmoConsentimiento = false;

    @Column(name = "motivocancelacion", length = 300)
    private String motivoCancelacion;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}