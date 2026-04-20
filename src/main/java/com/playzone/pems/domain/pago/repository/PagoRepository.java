package com.playzone.pems.domain.pago.repository;

import com.playzone.pems.domain.pago.model.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoRepository {

    Optional<Pago> findById(Long id);

    List<Pago> findByReservaPublica(Long idReservaPublica);

    List<Pago> findByEventoPrivado(Long idEventoPrivado);

    List<Pago> findByVenta(Long idVenta);

    Page<Pago> findBySedeAndFechasBetween(
            Long idSede,
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable);

    Pago save(Pago pago);
}