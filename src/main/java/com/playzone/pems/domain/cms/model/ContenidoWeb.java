package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoWeb {

    private Long          id;
    private Long          idSeccion;
    private Long          idTipoContenido;
    private String        clave;
    private String        valorEs;
    private String        valorEn;
    private boolean       activo;
    private Long          idUsuarioEditor;
    private LocalDateTime fechaActualizacion;

    public String valorParaIdioma(String codigoIdioma) {
        if ("en".equalsIgnoreCase(codigoIdioma) && valorEn != null && !valorEn.isBlank()) {
            return valorEn;
        }
        return valorEs;
    }

    public boolean tieneTraduccionIngles() {
        return valorEn != null && !valorEn.isBlank();
    }
}