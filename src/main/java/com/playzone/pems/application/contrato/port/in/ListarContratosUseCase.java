package com.playzone.pems.application.contrato.port.in;

import com.playzone.pems.application.contrato.dto.query.ContratoPageQuery;
import org.springframework.data.domain.Pageable;

public interface ListarContratosUseCase {
    ContratoPageQuery ejecutar(
        String  search,
        String  estado,
        Long    idSede,
        Pageable pageable
    );
}