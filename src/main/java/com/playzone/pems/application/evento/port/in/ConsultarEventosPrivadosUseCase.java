package com.playzone.pems.application.evento.port.in;

import com.playzone.pems.application.evento.dto.query.EventoPrivadoQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ConsultarEventosPrivadosUseCase {

    Page<EventoPrivadoQuery> consultarPorCliente(Long idCliente, Pageable pageable);

    Page<EventoPrivadoQuery> consultarPorSedeYEstado(Long idSede, String estado, Pageable pageable);

    Page<EventoPrivadoQuery> consultarPorSedeYRangoFechas(
            Long idSede, LocalDate inicio, LocalDate fin, Pageable pageable);

    EventoPrivadoQuery consultarPorId(Long idEvento);
}
