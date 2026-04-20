package com.playzone.pems.domain.facturacion.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum EstadoComprobante {

    PENDIENTE(
            "PENDIENTE",
            "Pendiente de envío a SUNAT"
    ),
    EMITIDO(
            "EMITIDO",
            "Validado y aceptado por SUNAT"
    ),
    RECHAZADO(
            "RECHAZADO",
            "Rechazado por SUNAT, requiere corrección"
    ),
    ANULADO(
            "ANULADO",
            "Anulado mediante nota de crédito"
    );

    private final String codigo;
    private final String descripcion;

    private static final Set<EstadoComprobante> PROCESADOS =
            Set.of(EMITIDO, RECHAZADO, ANULADO);

    public boolean tieneValidezFiscal() {
        return this == EMITIDO;
    }

    public boolean estaProcesado() {
        return PROCESADOS.contains(this);
    }

    public boolean esAnulable() {
        return this == EMITIDO;
    }

    public boolean requiereReintento() {
        return this == PENDIENTE || this == RECHAZADO;
    }

    public static EstadoComprobante desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Estado de comprobante inválido: '" + codigo + "'"));
    }
}