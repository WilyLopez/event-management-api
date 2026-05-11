package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BuscarEventosAdminUseCase {

    Page<EventoPrivadoQuery> buscar(
        Long      idSede,
        String    estado,
        LocalDate fecha,
        String    search,
        Pageable  pageable
    );
}