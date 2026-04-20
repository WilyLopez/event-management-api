package com.playzone.pems.infrastructure.persistence.usuario.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcliente")
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(name = "contresenahash", nullable = false, length = 255)
    private String contrasenaHash;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 8)
    private String dni;

    @Column(length = 11)
    private String ruc;

    @Column(name = "razonsocial", length = 200)
    private String razonSocial;

    @Column(name = "direccionfiscal", length = 300)
    private String direccionFiscal;

    @Column(name = "esvip", nullable = false)
    private boolean esVip = false;

    @Column(name = "descuentovip", precision = 5, scale = 2)
    private BigDecimal descuentoVip;

    @Column(name = "contadorvisitas", nullable = false)
    private int contadorVisitas = 0;

    @Column(name = "correoverificado", nullable = false)
    private boolean correoVerificado = false;

    @Column(name = "tokenverificacion", length = 255)
    private String tokenVerificacion;

    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}