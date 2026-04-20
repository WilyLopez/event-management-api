package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.domain.evento.model.enums.EstadoEventoPrivado;
import com.playzone.pems.infrastructure.persistence.evento.entity.EventoPrivadoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface EventoPrivadoJpaRepository extends JpaRepository<EventoPrivadoEntity, Long> {

    Page<EventoPrivadoEntity> findByCliente_Id(Long idCliente, Pageable pageable);

    Page<EventoPrivadoEntity> findBySede_IdAndEstado(Long idSede, EstadoEventoPrivado estado, Pageable pageable);

    Page<EventoPrivadoEntity> findBySede_IdAndFechaEventoBetween(Long idSede, LocalDate inicio, LocalDate fin, Pageable pageable);

    @Query("SELECT COUNT(e) > 0 FROM EventoPrivadoEntity e WHERE e.sede.id = :idSede AND e.fechaEvento = :fecha AND e.turno.id = :idTurno AND e.estado IN ('SOLICITADA','CONFIRMADA')")
    boolean existsActivoBySedeAndFechaAndTurno(
            @Param("idSede") Long idSede,
            @Param("fecha") LocalDate fecha,
            @Param("idTurno") Long idTurno);
}