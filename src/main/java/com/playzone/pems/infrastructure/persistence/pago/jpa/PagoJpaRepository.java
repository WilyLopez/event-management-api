package com.playzone.pems.infrastructure.persistence.pago.jpa;

import com.playzone.pems.infrastructure.persistence.pago.entity.PagoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoJpaRepository extends JpaRepository<PagoEntity, Long> {

    List<PagoEntity> findByReservaPublica_Id(Long idReservaPublica);

    List<PagoEntity> findByEventoPrivado_Id(Long idEventoPrivado);

    List<PagoEntity> findByIdVenta(Long idVenta);

    @Query("SELECT p FROM PagoEntity p WHERE p.reservaPublica.sede.id = :idSede AND p.fechaPago BETWEEN :desde AND :hasta")
    Page<PagoEntity> findBySedeAndFechasBetween(
            @Param("idSede") Long idSede,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);
}