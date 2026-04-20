package com.playzone.pems.infrastructure.persistence.evento.jpa;

import com.playzone.pems.domain.evento.model.enums.EstadoReservaPublica;
import com.playzone.pems.infrastructure.persistence.evento.entity.ReservaPublicaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaPublicaJpaRepository extends JpaRepository<ReservaPublicaEntity, Long> {

    Optional<ReservaPublicaEntity> findByNumeroTicket(String numeroTicket);

    Page<ReservaPublicaEntity> findByCliente_Id(Long idCliente, Pageable pageable);

    Page<ReservaPublicaEntity> findBySede_IdAndFechaEvento(Long idSede, LocalDate fecha, Pageable pageable);

    Page<ReservaPublicaEntity> findBySede_IdAndEstado(Long idSede, EstadoReservaPublica estado, Pageable pageable);

    List<ReservaPublicaEntity> findBySede_IdAndFechaEventoAndEstado(Long idSede, LocalDate fecha, EstadoReservaPublica estado);

    @Query("SELECT COUNT(r) FROM ReservaPublicaEntity r WHERE r.sede.id = :idSede AND r.fechaEvento = :fecha AND r.estado = 'CONFIRMADA'")
    int countConfirmadasBySedeAndFecha(@Param("idSede") Long idSede, @Param("fecha") LocalDate fecha);

    boolean existsByNumeroTicket(String numeroTicket);
}