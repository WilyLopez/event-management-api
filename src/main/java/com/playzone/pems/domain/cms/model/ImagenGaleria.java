package com.playzone.pems.domain.cms.model;

import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ImagenGaleria {

    private Long             id;
    private Long             idSede;
    private String           urlImagen;
    private String           altTexto;
    private String           titulo;
    private String           descripcion;
    private CategoriaImagen  categoriaImagen;
    private String           tipoMime;
    private Long             tamanioBytes;
    private int              ordenVisualizacion;
    private boolean          activo;
    private boolean          destacada;
    private boolean          eliminada;
    private Long             idUsuarioSubio;
    private LocalDateTime    fechaSubida;

    public String altTextoEfectivo() {
        return (altTexto != null && !altTexto.isBlank())
                ? altTexto
                : categoriaImagen.getDescripcion();
    }

    public boolean esDeCumpleanos() {
        return CategoriaImagen.CUMPLEANOS == categoriaImagen;
    }
}