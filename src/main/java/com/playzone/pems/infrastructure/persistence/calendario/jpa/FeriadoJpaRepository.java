package com.playzone.pems.infrastructure.persistence.calendario.jpa;

import com.playzone.pems.infrastructure.persistence.calendario.entity.FeriadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FeriadoJpaRepository extends JpaRepository<FeriadoEntity, Long> {

    Optional<FeriadoEntity> findByFecha(LocalDate fecha);

    List<FeriadoEntity> findByAnio(int anio);

    List<FeriadoEntity> findByFechaBetween(LocalDate inicio, LocalDate fin);

    boolean existsByFecha(LocalDate fecha);
}