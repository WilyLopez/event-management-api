package com.playzone.pems.application.usuario.dto.query;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class ClienteQuery {

    private Long       id;
    private String     nombre;
    private String     correo;
    private String     telefono;
    private String     dni;
    private String     ruc;
    private String     razonSocial;
    private String     direccionFiscal;
    private String     fotoPerfil;
    private Instant    ultimoLogin;
    private LocalDate  fechaNacimiento;
    private String     tipoCliente;
    private boolean    esVip;
    private BigDecimal descuentoVip;
    private int        contadorVisitas;
    private boolean    correoVerificado;
    private boolean    activo;

    private String     origenRegistro;
    private boolean    tieneAccesoWeb;
    private boolean    aceptaComunicaciones;
    private String     observaciones;
    private Instant    fechaMigracionWeb;
    private Instant    ultimaVisita;
    private BigDecimal totalGastado;
    private String     segmentoCliente;

    private Instant    fechaCreacion;
}