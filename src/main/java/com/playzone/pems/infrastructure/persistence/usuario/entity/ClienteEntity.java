package com.playzone.pems.infrastructure.persistence.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "contresenahash", nullable = false)
    private String contrasenaHash;

    @Column(length = 20)
    private String telefono;

    @Column(length = 8)
    private String dni;

    @Column(name = "direccionfiscal", length = 300)
    private String direccionFiscal;

    @Column(length = 11)
    private String ruc;

    @Column(name = "razonsocial", length = 250)
    private String razonSocial;

    @Column(name = "fotoperfil", length = 500)
    private String fotoPerfil;

    @Column(name = "ultimologin")
    private Instant ultimoLogin;

    @Column(name = "fechanacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "tipocliente", length = 20)
    @Builder.Default
    private String tipoCliente = "PERSONA";

    @Column(name = "esvip", nullable = false)
    @Builder.Default
    private boolean esVip = false;

    @Column(name = "descuentovip", precision = 5, scale = 2)
    private BigDecimal descuentoVip;

    @Column(name = "contadorvisitas", nullable = false)
    @Builder.Default
    private int contadorVisitas = 0;

    @Column(name = "correoverificado", nullable = false)
    @Builder.Default
    private boolean correoVerificado = false;

    @Column(name = "tokenverificacion", length = 200)
    private String tokenVerificacion;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "fechacreacion", updatable = false)
    private Instant fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fechaactualizacion")
    private Instant fechaActualizacion;
}