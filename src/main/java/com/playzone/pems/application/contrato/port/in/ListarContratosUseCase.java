package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.query.ContratoPageQuery;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ListarContratosUseCase {
    ContratoPageQuery ejecutar(
        String    search,
        String    estado,
        Long      idSede,
        LocalDate fechaEvento,
        Pageable  pageable
    );
}