package com.playzone.pems.domain.cms.repository;

import com.playzone.pems.domain.cms.model.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BannerRepository {

    Optional<Banner> findById(Long id);

    List<Banner> findVisiblesBySedeAndFecha(Long idSede, LocalDate fecha);

    Page<Banner> findAll(Pageable pageable);

    Banner save(Banner banner);

    void deleteById(Long id);
}