package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.MetricasReservaQuery;
import com.playzone.pems.application.evento.dto.query.ReservaPublicaQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BuscarReservasAdminUseCase {

    Page<ReservaPublicaQuery> buscar(
        Long      idSede,
        String    estado,
        LocalDate fecha,
        Boolean   ingresado,
        Boolean   esReprogramacion,
        String    search,
        Pageable  pageable
    );

    MetricasReservaQuery metricas(Long idSede, LocalDate fecha);
}