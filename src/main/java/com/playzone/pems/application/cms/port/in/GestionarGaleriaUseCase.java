package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;

public interface GestionarGaleriaUseCase {

    ImagenGaleria subir(
            Long idSede,
            byte[] contenido,
            String nombreArchivo,
            String contentType,
            String altTexto,
            CategoriaImagen categoria,
            int orden,
            Long idUsuario);

    void eliminar(Long idImagen);

    void reordenar(Long idImagen, int nuevoOrden);
}