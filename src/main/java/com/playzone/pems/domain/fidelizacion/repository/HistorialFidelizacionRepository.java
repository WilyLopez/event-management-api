package com.playzone.pems.domain.fidelizacion.repository;

import com.playzone.pems.domain.fidelizacion.model.HistorialFidelizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HistorialFidelizacionRepository {

    Optional<HistorialFidelizacion> findById(Long id);

    Optional<HistorialFidelizacion> findByReservaPublica(Long idReservaPublica);

    Page<HistorialFidelizacion> findByCliente(Long idCliente, Pageable pageable);

    int countVisitasByCliente(Long idCliente);

    boolean existsBeneficioAplicadoByCliente(Long idCliente, int visitaNumero);

    HistorialFidelizacion save(HistorialFidelizacion historial);
}