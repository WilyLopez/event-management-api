package com.playzone.pems.domain.usuario.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientePerfil {

    private Long       id;
    private UUID       usuarioId;
    private String     tipoDocumentoCodigo;
    private String     numeroDocumento;
    private String     nombres;
    private String     apellidoPaterno;
    private String     apellidoMaterno;
    private String     correo;
    private String     telefono;
    private String     segmentoCodigo;
    private boolean    esVip;
    private BigDecimal descuentoVip;
    private boolean    aceptaComunicaciones;
    private int        contadorVisitas;
    private BigDecimal totalGastado;
    private String     origen;
    private OffsetDateTime ultimaVisitaAt;
    private OffsetDateTime creadoEn;

    public String nombreCompleto() {
        StringBuilder sb = new StringBuilder(nombres != null ? nombres : "");
        if (apellidoPaterno != null) sb.append(" ").append(apellidoPaterno);
        if (apellidoMaterno != null) sb.append(" ").append(apellidoMaterno);
        return sb.toString().trim();
    }
}
