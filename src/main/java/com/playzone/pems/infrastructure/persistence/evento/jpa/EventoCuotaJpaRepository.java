package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.infrastructure.persistence.evento.entity.EventoCuotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventoCuotaJpaRepository extends JpaRepository<EventoCuotaEntity, Long> {

    List<EventoCuotaEntity> findByEventoIdOrderByNumeroCuotaAsc(Long eventoId);

    List<EventoCuotaEntity> findByEstadoAndFechaVencimientoBefore(String estado, LocalDate fecha);
}
