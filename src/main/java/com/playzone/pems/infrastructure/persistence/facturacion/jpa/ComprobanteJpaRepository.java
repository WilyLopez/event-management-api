package com.playzone.pems.infrastructure.persistence.facturacion.jpa;

import com.playzone.pems.domain.facturacion.model.enums.EstadoComprobante;
import com.playzone.pems.infrastructure.persistence.facturacion.entity.ComprobanteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ComprobanteJpaRepository extends JpaRepository<ComprobanteEntity, Long> {

    Optional<ComprobanteEntity> findByNumeroCompleto(String numeroCompleto);

    Optional<ComprobanteEntity> findByPago_Id(Long idPago);

    @Query("SELECT c FROM ComprobanteEntity c WHERE c.serie.sede.id = :idSede AND c.fechaEmision BETWEEN :desde AND :hasta")
    Page<ComprobanteEntity> findBySedeAndFechasBetween(
            @Param("idSede") Long idSede,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);

    List<ComprobanteEntity> findByEstadoComprobanteIn(List<EstadoComprobante> estados);

    boolean existsByNumeroCompleto(String numeroCompleto);
}