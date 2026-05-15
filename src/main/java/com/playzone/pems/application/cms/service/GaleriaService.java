package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.port.in.GestionarGaleriaUseCase;
import com.playzone.pems.application.cms.port.out.SubirImagenStoragePort;
import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.domain.cms.repository.ImagenGaleriaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GaleriaService implements GestionarGaleriaUseCase {

    private final ImagenGaleriaRepository galeriaRepository;
    private final SubirImagenStoragePort  storagePort;

    @Override
    @Transactional
    public ImagenGaleria subir(Long idSede, byte[] contenido, String nombreArchivo,
                               String contentType, String altTexto, CategoriaImagen categoria,
                               int orden, Long idUsuario) {

        String url = storagePort.subir(contenido, nombreArchivo, contentType);

        ImagenGaleria imagen = ImagenGaleria.builder()
                .idSede(idSede)
                .urlImagen(url)
                .altTexto(altTexto)
                .categoriaImagen(categoria)
                .tipoMime(contentType)
                .tamanioBytes((long) contenido.length)
                .ordenVisualizacion(orden)
                .activo(true)
                .destacada(false)
                .eliminada(false)
                .idUsuarioSubio(idUsuario)
                .build();

        return galeriaRepository.save(imagen);
    }

    @Override
    @Transactional
    public void eliminar(Long idImagen) {
        ImagenGaleria imagen = galeriaRepository.findById(idImagen)
                .orElseThrow(() -> new ResourceNotFoundException("ImagenGaleria", idImagen));

        storagePort.eliminar(imagen.getUrlImagen());
        galeriaRepository.deleteById(idImagen);
    }

    @Override
    @Transactional
    public void reordenar(Long idImagen, int nuevoOrden) {
        ImagenGaleria imagen = galeriaRepository.findById(idImagen)
                .orElseThrow(() -> new ResourceNotFoundException("ImagenGaleria", idImagen));

        galeriaRepository.save(imagen.toBuilder().ordenVisualizacion(nuevoOrden).build());
    }
}