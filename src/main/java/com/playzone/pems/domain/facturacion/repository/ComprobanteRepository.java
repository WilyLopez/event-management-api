package com.playzone.pems.domain.facturacion.repository;

import com.playzone.pems.domain.facturacion.model.Comprobante;
import com.playzone.pems.domain.facturacion.model.enums.EstadoComprobante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ComprobanteRepository {

    Optional<Comprobante> findById(Long id);

    Optional<Comprobante> findByNumeroCompleto(String numeroCompleto);

    Optional<Comprobante> findByPago(Long idPago);

    Page<Comprobante> findBySedeAndFechasBetween(
            Long idSede,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable);

    List<Comprobante> findPendientesDeEnvio();

    Comprobante save(Comprobante comprobante);

    boolean existsByNumeroCompleto(String numeroCompleto);
}