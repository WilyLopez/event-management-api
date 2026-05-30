package com.playzone.pems.application.cms.port.in;

import com.playzone.pems.application.cms.dto.query.ContenidoWebQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConsultarContenidoWebUseCase {

    Page<ContenidoWebQuery> listar(Long idSeccion, String clave, Pageable pageable);
}
