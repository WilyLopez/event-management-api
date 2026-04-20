package com.playzone.pems.domain.facturacion.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TipoComprobante {

    BOLETA(
            "BOLETA",
            "Boleta de Venta Electrónica",
            true,
            "03"
    ),
    FACTURA(
            "FACTURA",
            "Factura Electrónica",
            true,
            "01"
    ),
    NOTA_VENTA(
            "NOTA_VENTA",
            "Nota de Venta (uso interno)",
            false,
            null
    );

    private final String  codigo;
    private final String  descripcion;

    private final boolean validoSunat;

    private final String  codigoSunat;

    public boolean requiereEnvioSunat() {
        return validoSunat;
    }

    public boolean requiereRucReceptor() {
        return this == FACTURA;
    }

    public static TipoComprobante desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Tipo de comprobante inválido: '" + codigo + "'"));
    }

    public static TipoComprobante desdeCodigoSunat(String codigoSunat) {
        return Arrays.stream(values())
                .filter(e -> codigoSunat.equals(e.codigoSunat))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Código SUNAT de comprobante inválido: '" + codigoSunat + "'"));
    }
}