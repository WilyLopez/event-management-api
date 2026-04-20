package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.domain.cms.repository.ImagenGaleriaRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ImagenGaleriaJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import com.playzone.pems.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ImagenGaleriaPersistenceAdapter implements ImagenGaleriaRepository {

    private final ImagenGaleriaJpaRepository galeriaJpa;
    private final SedeJpaRepository          sedeJpa;
    private final UsuarioAdminJpaRepository  adminJpa;
    private final CmsEntityMapper            mapper;

    @Override
    public Optional<ImagenGaleria> findById(Long id) {
        return galeriaJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ImagenGaleria> findActivasBySede(Long idSede) {
        return galeriaJpa.findBySede_IdAndActivoTrueOrderByOrdenVisualizacionAsc(idSede)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ImagenGaleria> findActivasBySedeAndCategoria(Long idSede, CategoriaImagen categoria) {
        return galeriaJpa.findBySede_IdAndCategoriaImagenAndActivoTrueOrderByOrdenVisualizacionAsc(idSede, categoria)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Page<ImagenGaleria> findBySede(Long idSede, Pageable pageable) {
        return galeriaJpa.findBySede_Id(idSede, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public ImagenGaleria save(ImagenGaleria img) {
        var sede    = sedeJpa.findById(img.getIdSede())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", img.getIdSede()));
        var usuario = img.getIdUsuarioSubio() != null
                ? adminJpa.findById(img.getIdUsuarioSubio()).orElse(null) : null;
        return mapper.toDomain(galeriaJpa.save(mapper.toEntity(img, sede, usuario)));
    }

    @Override
    public void deleteById(Long id) {
        galeriaJpa.deleteById(id);
    }
}