package com.playzone.pems.infrastructure.persistence.evento.entity;

import com.playzone.pems.infrastructure.persistence.usuario.entity.ClienteEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.SedeEntity;
import com.playzone.pems.infrastructure.persistence.usuario.entity.UsuarioAdminEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta_presencial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaPresencialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idventa")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsede", nullable = false)
    private SedeEntity sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente")
    private ClienteEntity cliente;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDate fechaVisita;

    @Column(name = "nombre_acompanante", nullable = false, length = 150)
    private String nombreAcompanante;

    @Column(name = "dni_acompanante", nullable = false, length = 15)
    private String dniAcompanante;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "idpromocion")
    private Long idPromocion;

    @Column(name = "descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "efectivo_recibido", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoRecibido = BigDecimal.ZERO;

    @Column(name = "vuelto", nullable = false, precision = 10, scale = 2)
    private BigDecimal vuelto = BigDecimal.ZERO;

    @Column(name = "acta_firmada", nullable = false)
    private boolean actaFirmada = false;

    @Column(name = "es_anticipada", nullable = false)
    private boolean esAnticipada = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario_registra", nullable = false)
    private UsuarioAdminEntity usuarioRegistra;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
