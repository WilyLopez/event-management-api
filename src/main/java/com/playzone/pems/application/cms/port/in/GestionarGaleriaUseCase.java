package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GestionarGaleriaUseCase {

    Page<ImagenGaleria> listar(Long idSede, Boolean destacada, Pageable pageable);

    ImagenGaleria subir(
            Long idSede,
            byte[] contenido,
            String nombreArchivo,
            String contentType,
            String altTexto,
            CategoriaImagen categoria,
            int orden,
            UUID idUsuario);

    void eliminar(Long idImagen);

    void reordenar(Long idImagen, int nuevoOrden);

    void destacar(Long idImagen);

    void quitarDestacado(Long idImagen);

    ImagenGaleria actualizar(Long idImagen, String altTexto, CategoriaImagen categoria, Integer orden);
}
