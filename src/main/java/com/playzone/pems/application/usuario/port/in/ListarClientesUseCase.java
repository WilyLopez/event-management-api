package com.playzone.pems.application.usuario.port.in;

import com.playzone.pems.application.usuario.dto.query.ClientePageQuery;
import org.springframework.data.domain.Pageable;

public interface ListarClientesUseCase {

    ClientePageQuery ejecutar(
            String   search,
            Boolean  esVip,
            Boolean  activo,
            Boolean  verificado,
            Boolean  frecuente,
            Pageable pageable
    );
}