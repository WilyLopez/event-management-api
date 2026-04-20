package com.playzone.pems.domain.facturacion.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum TipoDocReceptor {

    DNI(
            "DNI",
            "Documento Nacional de Identidad",
            "1",
            8
    ),
    RUC(
            "RUC",
            "Registro Único de Contribuyentes",
            "6",
            11
    ),
    CE(
            "CE",
            "Carné de Extranjería",
            "4",
            9
    ),
    PASAPORTE(
            "PASAPORTE",
            "Pasaporte",
            "7",
            null
    ),
    SIN_DOC(
            "SIN_DOC",
            "Sin documento (consumidor final)",
            "0",
            null
    );

    private final String  codigo;
    private final String  descripcion;
    private final String  codigoSunat;
    private final Integer longitudEsperada;

    public boolean esPersonaJuridica() {
        return this == RUC;
    }

    public boolean requiereNumeroDocumento() {
        return this != SIN_DOC;
    }

    public static TipoDocReceptor desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo de documento de receptor inválido: '" + codigo + "'"));
    }

    public static TipoDocReceptor desdeCodigoSunat(String codigoSunat) {
        return Arrays.stream(values())
                .filter(e -> codigoSunat.equals(e.codigoSunat))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Código SUNAT de documento inválido: '" + codigoSunat + "'"));
    }
}