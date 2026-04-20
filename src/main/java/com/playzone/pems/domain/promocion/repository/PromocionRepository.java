package com.playzone.pems.domain.promocion.repository;

import com.playzone.pems.domain.calendario.model.enums.TipoDia;
import com.playzone.pems.domain.promocion.model.Promocion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromocionRepository {

    Optional<Promocion> findById(Long id);

    Page<Promocion> findAll(Pageable pageable);

    List<Promocion> findAutomaticasVigentes(Long idSede, TipoDia tipoDia, LocalDate fecha);

    Promocion save(Promocion promocion);

    void desactivar(Long id);
}