package com.playzone.pems.domain.cms.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoWeb {

    private Long          id;
    private String        seccionCodigo;
    private String        tipoContenidoCodigo;
    private String        clave;
    private String        valorEs;
    private String        valorEn;
    private String        imagenUrl;
    private String        descripcion;
    private int           ordenVisualizacion;
    private boolean       visible;
    private int           version;
    private String        metadatos;
    private boolean       activo;
    private UUID          idUsuarioEditor;
    private OffsetDateTime fechaActualizacion;

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
