package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.ImagenGaleria;
import com.playzone.pems.domain.cms.model.enums.CategoriaImagen;
import com.playzone.pems.domain.cms.repository.ImagenGaleriaRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.ImagenGaleriaJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ImagenGaleriaPersistenceAdapter implements ImagenGaleriaRepository {

    private final ImagenGaleriaJpaRepository jpaRepository;
    private final SedeJpaRepository          sedeRepository;
    private final UsuarioAdminJpaRepository  usuarioRepository;
    private final CmsEntityMapper            mapper;

    @Override
    public Optional<ImagenGaleria> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ImagenGaleria> findActivasBySede(Long idSede) {
        return jpaRepository.findBySede_IdAndActivoTrueOrderByOrdenVisualizacionAsc(idSede)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ImagenGaleria> findActivasBySedeAndCategoria(Long idSede, CategoriaImagen categoria) {
        return jpaRepository.findBySede_IdAndCategoriaImagenAndActivoTrueOrderByOrdenVisualizacionAsc(idSede, categoria)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Page<ImagenGaleria> findBySede(Long idSede, Pageable pageable) {
        return jpaRepository.findBySede_Id(idSede, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<ImagenGaleria> findBySedeAndDestacada(Long idSede, boolean destacada, Pageable pageable) {
        return jpaRepository.findBySede_IdAndDestacada(idSede, destacada, pageable).map(mapper::toDomain);
    }

    @Override
    public ImagenGaleria save(ImagenGaleria imagen) {
        var sedeEntity = sedeRepository.getReferenceById(imagen.getIdSede());
        var usuarioEntity = imagen.getIdUsuarioSubio() != null 
                ? usuarioRepository.getReferenceById(imagen.getIdUsuarioSubio()) 
                : null;
        
        var entity = mapper.toEntity(imagen, sedeEntity, usuarioEntity);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
