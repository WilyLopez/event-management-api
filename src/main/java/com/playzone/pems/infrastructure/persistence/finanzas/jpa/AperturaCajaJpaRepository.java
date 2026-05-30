package com.playzone.pems.infrastructure.persistence.finanzas.jpa;

import com.playzone.pems.infrastructure.persistence.finanzas.entity.AperturaCajaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AperturaCajaJpaRepository extends JpaRepository<AperturaCajaEntity, Long> {

    Optional<AperturaCajaEntity> findBySede_IdAndFecha(Long idSede, LocalDate fecha);

    List<AperturaCajaEntity> findBySede_IdAndFechaBetweenOrderByFechaAsc(
            Long idSede, LocalDate inicio, LocalDate fin);
}
