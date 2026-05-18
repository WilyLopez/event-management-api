package com.playzone.pems.application.cms.service;

import com.playzone.pems.application.cms.port.in.GestionarGaleriaUseCase;
import com.playzone.pems.application.cms.port.out.SubirImagenStoragePort;
import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.domain.cms.repository.ImagenGaleriaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GaleriaService implements GestionarGaleriaUseCase {

    private final ImagenGaleriaRepository galeriaRepository;
    private final SubirImagenStoragePort  storagePort;

    @Override
    @Transactional(readOnly = true)
    public Page<ImagenGaleria> listar(Long idSede, Boolean destacada, Pageable pageable) {
        if (Boolean.TRUE.equals(destacada)) {
            return galeriaRepository.findBySedeAndDestacada(idSede, true, pageable);
        }
        return galeriaRepository.findBySede(idSede, pageable);
    }

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

    @Override
    @Transactional
    public void destacar(Long idImagen) {
        ImagenGaleria imagen = galeriaRepository.findById(idImagen)
                .orElseThrow(() -> new ResourceNotFoundException("ImagenGaleria", idImagen));
        galeriaRepository.save(imagen.toBuilder().destacada(true).build());
    }

    @Override
    @Transactional
    public void quitarDestacado(Long idImagen) {
        ImagenGaleria imagen = galeriaRepository.findById(idImagen)
                .orElseThrow(() -> new ResourceNotFoundException("ImagenGaleria", idImagen));
        galeriaRepository.save(imagen.toBuilder().destacada(false).build());
    }

    @Override
    @Transactional
    public ImagenGaleria actualizar(Long idImagen, String altTexto, CategoriaImagen categoria, Integer orden) {
        ImagenGaleria imagen = galeriaRepository.findById(idImagen)
                .orElseThrow(() -> new ResourceNotFoundException("ImagenGaleria", idImagen));
        
        ImagenGaleria.ImagenGaleriaBuilder builder = imagen.toBuilder();
        if (altTexto != null) builder.altTexto(altTexto);
        if (categoria != null) builder.categoriaImagen(categoria);
        if (orden != null) builder.ordenVisualizacion(orden);
        
        return galeriaRepository.save(builder.build());
    }
}
