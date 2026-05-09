package com.playzone.pems.domain.usuario.model;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private Long id;

    private String nombre;
    private String correo;
    private String contrasenaHash;

    private String telefono;
    private String dni;
    private String direccionFiscal;

    private LocalDate fechaNacimiento;

    private String tipoCliente;

    private String fotoPerfil;

    private String ruc;
    private String razonSocial;

    private boolean esVip;

    private BigDecimal descuentoVip;

    private int contadorVisitas;

    private boolean correoVerificado;

    private String tokenVerificacion;

    private Instant ultimoLogin;

    private boolean activo;

    private Instant fechaCreacion;

    private Instant fechaActualizacion;

    public boolean tieneDatosParaFactura() {
        return ruc != null
                && !ruc.isBlank()
                && razonSocial != null
                && !razonSocial.isBlank();
    }

    public BigDecimal aplicarDescuentoVip(BigDecimal precioBase) {

        if (!esVip
                || descuentoVip == null
                || descuentoVip.compareTo(BigDecimal.ZERO) == 0) {

            return precioBase;
        }

        BigDecimal factor = BigDecimal.ONE.subtract(
                descuentoVip.divide(
                        BigDecimal.valueOf(100),
                        4,
                        RoundingMode.HALF_UP
                )
        );

        return precioBase
                .multiply(factor)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean puedeAcceder() {
        return activo && correoVerificado;
    }

    public String nombreParaMostrar() {

        return (razonSocial != null && !razonSocial.isBlank())
                ? razonSocial
                : nombre;
    }

    public void registrarVisita() {
        this.contadorVisitas++;
    }

    public void actualizarUltimoLogin() {
        this.ultimoLogin = Instant.now();
    }
}