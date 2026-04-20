package com.playzone.pems.infrastructure.persistence.cms.adapter;

import com.playzone.pems.domain.cms.model.Banner;
import com.playzone.pems.domain.cms.repository.BannerRepository;
import com.playzone.pems.infrastructure.persistence.cms.jpa.BannerJpaRepository;
import com.playzone.pems.infrastructure.persistence.cms.mapper.CmsEntityMapper;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.SedeJpaRepository;
import com.playzone.pems.infrastructure.persistence.usuario.jpa.UsuarioAdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BannerPersistenceAdapter implements BannerRepository {

    private final BannerJpaRepository       bannerJpa;
    private final SedeJpaRepository         sedeJpa;
    private final UsuarioAdminJpaRepository adminJpa;
    private final CmsEntityMapper           mapper;

    @Override
    public Optional<Banner> findById(Long id) {
        return bannerJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Banner> findVisiblesBySedeAndFecha(Long idSede, LocalDate fecha) {
        return bannerJpa.findVisiblesBySedeAndFecha(idSede, fecha)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Page<Banner> findAll(Pageable pageable) {
        return bannerJpa.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Banner save(Banner b) {
        var sede    = b.getIdSede() != null
                ? sedeJpa.findById(b.getIdSede()).orElse(null) : null;
        var creador = b.getIdUsuarioCreador() != null
                ? adminJpa.findById(b.getIdUsuarioCreador()).orElse(null) : null;
        return mapper.toDomain(bannerJpa.save(mapper.toEntity(b, sede, creador)));
    }

    @Override
    public void deleteById(Long id) {
        bannerJpa.deleteById(id);
    }
}