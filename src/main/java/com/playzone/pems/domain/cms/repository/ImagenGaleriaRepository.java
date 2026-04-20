package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ImagenGaleriaRepository {

    Optional<ImagenGaleria> findById(Long id);

    List<ImagenGaleria> findActivasBySede(Long idSede);

    List<ImagenGaleria> findActivasBySedeAndCategoria(
            Long idSede, CategoriaImagen categoria);

    Page<ImagenGaleria> findBySede(Long idSede, Pageable pageable);

    ImagenGaleria save(ImagenGaleria imagen);

    void deleteById(Long id);
}