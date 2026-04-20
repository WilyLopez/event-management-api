package com.playzone.pems.domain.cms.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CategoriaImagen {

    CUMPLEANOS(
            "CUMPLEANOS",
            "Fotos de celebraciones de cumpleaños"
    ),
    JUEGOS(
            "JUEGOS",
            "Fotos de las instalaciones y juegos"
    ),
    DECORACION(
            "DECORACION",
            "Fotos de decoración temática del local"
    ),
    GENERAL(
            "GENERAL",
            "Imágenes generales del local"
    ),
    EVENTO(
            "EVENTO",
            "Fotos de eventos privados realizados"
    );

    private final String codigo;
    private final String descripcion;

    public static CategoriaImagen desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoría de imagen inválida: '" + codigo + "'"));
    }
}