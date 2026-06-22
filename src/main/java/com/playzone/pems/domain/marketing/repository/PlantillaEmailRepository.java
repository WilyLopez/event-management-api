package com.playzone.pems.domain.marketing.repository;

import com.playzone.pems.domain.marketing.model.PlantillaEmail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlantillaEmailRepository {

    Optional<PlantillaEmail> findById(Long id);

    List<PlantillaEmail> findAllById(List<Long> ids);

    Page<PlantillaEmail> findAll(Pageable pageable);

    Page<PlantillaEmail> findAllMarketing(Pageable pageable);

    List<PlantillaEmail> findAllActivasByTipo(String tipoEmailCodigo);

    PlantillaEmail save(PlantillaEmail plantilla);

    void softDelete(Long id);

    boolean existsById(Long id);
}
