package com.playzone.pems.domain.marketing.repository;

import com.playzone.pems.domain.marketing.model.PlantillaEmail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PlantillaEmailRepository {

    Optional<PlantillaEmail> findById(Long id);

    Page<PlantillaEmail> findAll(Pageable pageable);

    List<PlantillaEmail> findAllActivasByTipo(Long idTipoEmail);

    PlantillaEmail save(PlantillaEmail plantilla);

    void eliminar(Long id);

    boolean existsById(Long id);
}
