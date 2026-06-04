package com.playzone.pems.infrastructure.persistence.evento.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pago_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoVentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpago")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idventa", nullable = false)
    private VentaPresencialEntity venta;

    @Column(name = "metodo", nullable = false, length = 20)
    private String metodo;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
}
