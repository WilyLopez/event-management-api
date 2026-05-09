package com.playzone.pems.interfaces.rest.usuario.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteResponse {

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
    private Instant    fechaCreacion;
}